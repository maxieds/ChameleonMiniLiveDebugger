package com.maxieds.chameleonminilivedebugger;

import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.view.View;

/**
 * Created by mschmidt34 on 12/31/2017.
 */

public abstract class LogEntryBase {
    public abstract String writeXMLFragment(int indentLevel);
    public abstract String toString();
    public abstract View getLayoutContainer();
}
