package com.smart.vbox;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;

/**
 * Application类
 * @author lhq
 * created at 2015/10/22 21:05
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;

    public static MyApplication getsInstance() {
        return sInstance;
    }

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();

        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //由android系统帮你实例化的
        sInstance = this;

        refWatcher = LeakCanary.install(sInstance);

        ButterKnife.setDebug(BuildConfig.DEBUG);

//        Fresco.initialize(sInstance, FrescoConfig.getImagePipelineConfig(sInstance));

    }

}
