package com.gashfara.lovechat.ui;

import com.kii.cloud.abtesting.KiiExperiment;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.social.KiiSocialConnect;
import com.kii.cloud.storage.social.connector.KiiSocialNetworkConnector;
import com.gashfara.lovechat.R;
import com.gashfara.lovechat.model.ChatRoom;
import com.gashfara.lovechat.ui.task.ChatUserInitializeTask.OnInitializeListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.callback.KiiSocialCallBack;
import com.kii.cloud.storage.social.KiiSocialConnect.SocialNetwork;
import com.gashfara.lovechat.PreferencesManager;
import com.gashfara.lovechat.ui.task.ABTestInfoFetchTask;
import com.gashfara.lovechat.ui.task.ChatUserInitializeTask;
import com.gashfara.lovechat.ui.util.SimpleProgressDialogFragment;
import com.gashfara.lovechat.ui.util.ToastUtils;
import com.gashfara.lovechat.util.Logger;

/**
 * サインイン画面のアクティビティです。
 * 
 * @author noriyoshi.fukuzaki@kii.com
 */
public class SigninActivity extends ActionBarActivity implements OnInitializeListener{
	
	private TextView textNewAccount;
	private Button btnFbSignin;
	private Button btnSignin;
	private CheckBox checkRemember;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		
		this.textNewAccount = (TextView)findViewById(R.id.text_new_account);
		this.checkRemember = (CheckBox)findViewById(R.id.check_remember);
		this.btnFbSignin = (Button)findViewById(R.id.button_facebook);
		this.btnSignin = (Button)findViewById(R.id.button_signin);
		
		this.btnFbSignin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				// Facebookの認証画面を表示する
				KiiFacebookConnect connect = (KiiFacebookConnect) Kii.socialConnect(SocialNetwork.FACEBOOK);
				connect.initialize(ApplicationConst.FACEBOOK_APP_ID, null, null);
				Bundle options = new Bundle();
				String[] permission = new String[] { "email" };
				options.putStringArray(KiiFacebookConnect.FACEBOOK_PERMISSIONS, permission);
				connect.logIn(SigninActivity.this, options, new KiiSocialCallBack() {
					public void onLoginCompleted(SocialNetwork network, KiiUser user, Exception exception) {
						if (exception == null) {
							if (checkRemember.isChecked()) {
								// ログイン状態を保持する場合は、SharedPreferencesにAccessTokenを保存する
								Logger.i(user.getAccessToken());
								PreferencesManager.setStoredAccessToken(user.getAccessToken());
							}
							// ログイン後処理を行う
							new PostSigninTask(user.getDisplayname(), user.getEmail()).execute();
						} else {
							Logger.e("failed to sign up", exception);
							ToastUtils.showShort(SigninActivity.this, "Unable to sign up");
						}
					}
				});
				*/
				//あたらしいFacebook ログイン
				//Activity activity = this.getActivity();
				Activity activity = SigninActivity.this;
				KiiSocialConnect connect = Kii.socialConnect(SocialNetwork.SOCIALNETWORK_CONNECTOR);

				Bundle options = new Bundle();
				options.putParcelable(KiiSocialNetworkConnector.PROVIDER, KiiSocialNetworkConnector.Provider.FACEBOOK);

				// Login.
				connect.logIn(activity, options, new KiiSocialCallBack() {
					@Override
					public void onLoginCompleted(SocialNetwork network, KiiUser user, Exception exception) {
						if (exception == null) {
							if (checkRemember.isChecked()) {
								// ログイン状態を保持する場合は、SharedPreferencesにAccessTokenを保存する
								Logger.i(user.getAccessToken());
								PreferencesManager.setStoredAccessToken(user.getAccessToken());
							}
							// ログイン後処理を行う
							new PostSigninTask(user.getDisplayname(), user.getEmail()).execute();
						} else {
							Logger.e("failed to sign up", exception);
							ToastUtils.showShort(SigninActivity.this, "Unable to sign up");
						}
					}
				});
			}
		});
		this.btnSignin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// サインイン画面を表示する
				SigninDialogFragment signinFragment = SigninDialogFragment.newInstance(SigninActivity.this, checkRemember.isChecked());
				signinFragment.show(getSupportFragmentManager(), SigninDialogFragment.TAG);
			}
		});
		this.textNewAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// サインアップ画面を表示する
				SignupDialogFragment signupFragment = SignupDialogFragment.newInstance(SigninActivity.this);
				signupFragment.show(getSupportFragmentManager(), "signup");
			}
		});
	}
	
	private class PostSigninTask extends ChatUserInitializeTask {
		
		private PostSigninTask(String username, String email) {
			super(username, email);
		}
		
		@Override
		protected void onPreExecute() {
			SimpleProgressDialogFragment.show(getSupportFragmentManager(), "Signin", "Processing...");
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			SimpleProgressDialogFragment.hide(getSupportFragmentManager());
			if (result) {
				// サインアップ処理が正常に行われた場合はメイン画面に遷移する
			    new PreMoveToChatMainTask().execute();
			} else {
				ToastUtils.showShort(SigninActivity.this, "Unable to sign in");
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Kii.socialConnect(SocialNetwork.FACEBOOK).respondAuthOnActivityResult(requestCode, resultCode, data);
		//mogi 新しいFacebook認証に書き換え
		if (requestCode == KiiSocialNetworkConnector.REQUEST_CODE) {
			Kii.socialConnect(SocialNetwork.SOCIALNETWORK_CONNECTOR).respondAuthOnActivityResult(
					requestCode,
					resultCode,
					data);
		}
	}
	
	/**
	 * Chatのメイン画面に遷移する前にA/Bテストの情報取得を行います。
	 * @author tatsuro.fujii@kii.com
	 *
	 */
	private class PreMoveToChatMainTask extends ABTestInfoFetchTask {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(KiiExperiment result) {
            moveToChatMain(result);
        }
    }
	
	@Override
	public void onInitializeCompleted() {
	    new PreMoveToChatMainTask().execute();
	}
	
    private void moveToChatMain(final KiiExperiment experiment) {
        ChatRoom.ensureSubscribedBucket(KiiUser.getCurrentUser());
        Intent intent = new Intent(SigninActivity.this, ChatMainActivity.class);
        intent.putExtra(ChatActivity.INTENT_EXPERIMENT, experiment);
        startActivity(intent);
    }	
}
