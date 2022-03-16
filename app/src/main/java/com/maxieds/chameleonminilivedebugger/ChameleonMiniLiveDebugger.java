package com.maxieds.chameleonminilivedebugger;

import android.app.Application;
import android.content.Context;

public class ChameleonMiniLiveDebugger extends Application {

    public static ChameleonMiniLiveDebugger cmldAppInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        cmldAppInstance = this;
    }
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static ChameleonMiniLiveDebugger getInstance() {
        return cmldAppInstance;
    }

}
