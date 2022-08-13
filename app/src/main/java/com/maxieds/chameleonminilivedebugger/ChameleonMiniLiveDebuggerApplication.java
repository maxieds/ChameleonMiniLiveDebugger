package com.maxieds.chameleonminilivedebugger;

import android.app.Application;
import android.content.Context;

public class ChameleonMiniLiveDebuggerApplication extends Application {

    public static ChameleonMiniLiveDebuggerApplication cmldAppInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        cmldAppInstance = this;
    }
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static ChameleonMiniLiveDebuggerApplication getInstance() {
        return cmldAppInstance;
    }

}
