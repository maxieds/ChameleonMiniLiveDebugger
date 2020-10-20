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

package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

import android.content.ContentResolver;
import android.net.Uri;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;

import com.maxieds.chameleonminilivedebugger.LiveLoggerActivity;
import com.maxieds.chameleonminilivedebugger.R;
import com.maxieds.chameleonminilivedebugger.Utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ChameleonScripting {

    private static final String TAG = ChameleonScripting.class.getSimpleName();

    // need to return script context ...
    public static boolean LoadNewScript(String scriptFilePath) {
        return false;
    }

    // need to return script context ...
    public static boolean LoadNewScript(@AttrRes int scriptRawResPath) {
        return false;
    }

    // saveChameleonState() ; State = Cfg,Logmode,setting $,Readonly,Field,Threshold,Timeout,UID;

}
