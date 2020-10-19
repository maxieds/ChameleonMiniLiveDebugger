/*
This program (The Chameleon Mini Live Debugger) is free software written by
Maxie Dion Schmidt: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

The complete license provided with source distributions of this library is
available at the following link:
https://github.com/maxieds/ChameleonMiniLiveDebugger
*/

package com.maxieds.chameleonminilivedebugger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;

public class ChameleonMiniLiveDebuggerActivity extends AppCompatActivity {

    /**
     * We assume there is only one instance of the singleton activity running at a time.
     */
    protected static ChameleonMiniLiveDebuggerActivity runningActivity = null;
    protected static Bundle localSavedInstanceState;
    protected static LayoutInflater defaultInflater;
    protected static Context defaultContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runningActivity = this;
        localSavedInstanceState = savedInstanceState;
        defaultInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        defaultContext = getApplicationContext();
    }

    public static ChameleonMiniLiveDebuggerActivity getInstance() {
        return runningActivity;
    }

    public static LayoutInflater getDefaultInflater() { return defaultInflater; }

    public static Context getContext() {
        return defaultContext;
    }

}