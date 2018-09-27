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

    public static final float LOGENTRY_GUI_ALPHA = 0.95f;

    public abstract String writeXMLFragment(int indentLevel);
    public abstract String toString();
    public abstract View getLayoutContainer();
    public abstract View cloneLayoutContainer();
}
