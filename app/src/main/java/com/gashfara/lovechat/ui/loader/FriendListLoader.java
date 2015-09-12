package com.gashfara.lovechat.ui.loader;

import java.util.List;

import android.content.Context;

import com.gashfara.lovechat.model.ChatFriend;

/**
 * {@link ChatFriend}をchat_friendsバケットから取得するローダーです。
 * 
 * @author noriyoshi.fukuzaki@kii.com
 */
public class FriendListLoader extends AbstractAsyncTaskLoader<List<ChatFriend>> {
	
	public FriendListLoader(Context context) {
		super(context);
	}
	@Override
	public List<ChatFriend> loadInBackground() {
		return ChatFriend.list();
	}
}
