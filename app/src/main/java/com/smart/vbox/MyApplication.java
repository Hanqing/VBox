package com.smart.vbox;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.smart.vbox.model.bean.UserEntity;
import com.smart.vbox.support.config.FrescoConfig;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;

/**
 * Application类
 *
 * @author lhq
 *         created at 2015/10/22 21:05
 */
public class MyApplication extends MultiDexApplication {

    private static MyApplication sInstance;

    private UserEntity entity;

    public static MyApplication getsInstance() {
        return sInstance;
    }

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();

        return application.refWatcher;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //由android系统帮你实例化的
        sInstance = this;

        refWatcher = LeakCanary.install(sInstance);

        ButterKnife.setDebug(BuildConfig.DEBUG);

        Fresco.initialize(sInstance, FrescoConfig.getImagePipelineConfig(sInstance));
    }


    public void setUserEntity(UserEntity entity) {
        this.entity = entity;
    }

    public UserEntity getUserEntity() {
        return entity;
    }

}
