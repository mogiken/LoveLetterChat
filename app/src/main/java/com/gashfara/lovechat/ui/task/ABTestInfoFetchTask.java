package com.gashfara.lovechat.ui.task;

import com.kii.cloud.abtesting.KiiExperiment;
import com.gashfara.lovechat.ApplicationConst;
import com.gashfara.lovechat.util.Logger;

import android.os.AsyncTask;

/**
 * バックグラウンドでA/Bテストの情報を取得します。
 * @author tatsuro.fujii@kii.com
 * 
 */
public abstract class ABTestInfoFetchTask extends AsyncTask<Void, Void, KiiExperiment> {

    @Override
    protected KiiExperiment doInBackground(Void... params) {
        KiiExperiment experiment = null;
        try {
            //もぎ未使用
            //experiment = KiiExperiment.getByID(ApplicationConst.ABTEST_ID);
        } catch (Exception e) {
            Logger.e("Fetching A/B test info is failed.", e);
        }
        return experiment;
    }

}
