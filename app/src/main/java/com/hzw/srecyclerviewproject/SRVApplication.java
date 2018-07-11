package com.hzw.srecyclerviewproject;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;

/**
 * author: hzw
 * time: 2018/7/11 上午11:08
 * description:
 */
public class SRVApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
