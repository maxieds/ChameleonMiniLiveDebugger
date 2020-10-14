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

import android.view.View;

/**
 * Created by mschmidt34 on 12/31/2017.
 */

/**
 * <h1>Log Entry Base Abstract Interface</h1>
 * Abstract base class for storing the log entry data.
 *
 * @author  Maxie D. Schmidt
 * @since   12/31/17
 */
public abstract class LogEntryBase {

    public static final float LOGENTRY_GUI_ALPHA = 0.91f;

    public abstract String writeXMLFragment(int indentLevel);
    public abstract String toString();
    public abstract View getLayoutContainer();
    public abstract View cloneLayoutContainer();
}
