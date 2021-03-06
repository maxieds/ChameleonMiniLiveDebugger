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

import com.maxieds.chameleonminilivedebugger.AndroidSettingsStorage;
import com.maxieds.chameleonminilivedebugger.BuildConfig;
import com.maxieds.chameleonminilivedebugger.R;
import com.maxieds.chameleonminilivedebugger.Utils;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScriptingBreakPoint {

    private static final String TAG = ScriptingBreakPoint.class.getSimpleName();

    public static LinearLayout breakpointsGUIDisplayContainer = null;
    public static List<ScriptingBreakPoint> breakpointsObjList = new ArrayList<ScriptingBreakPoint>();
    public static List<View> breakpointsGUIViewsList = new ArrayList<View>();
    private static int bpActiveIndex = -1;
    public static boolean bpDisabled = false;

    private static final int DISABLED_IMAGE_ALPHA = 96;
    private static final int ENABLED_IMAGE_ALPHA = 255;

    private int lineNumber;
    private String lineLabel;
    private boolean isLabelType;
    private boolean isPreserved;
    private boolean isEnabled;
    private LinearLayout mainGUILayoutView;

    private void initializeToDefaults() {
        lineNumber = -1;
        lineLabel = "";
        isLabelType = false;
        isPreserved = false;
        isEnabled = true;
        mainGUILayoutView = (LinearLayout) ScriptingConfig.SCRIPTING_CONFIG_ACTIVITY_CONTEXT.getLayoutInflater().inflate(R.layout.scripting_gui_breakpoint_entry, null);
    }

    public ScriptingBreakPoint(int line) {
        initializeToDefaults();
        lineNumber = line;
        isLabelType = false;
        initMainLayoutView();
    }

    public ScriptingBreakPoint(String label) {
        initializeToDefaults();
        lineLabel = label;
        isLabelType = true;
        initMainLayoutView();
    }

    public void initMainLayoutView() {
        ImageButton removeBtn = mainGUILayoutView.findViewById(R.id.scriptingGUIBreakpointRemoveBtn);
        if(removeBtn == null) {
            return;
        }
        removeBtn.setTag((Object) this);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btnView) {
                ScriptingBreakPoint sbpObj = (ScriptingBreakPoint) btnView.getTag();
                ScriptingBreakPoint.removeBreakpoint(sbpObj.getGUIContainer());
            }
        });
        ImageView activeImageView = mainGUILayoutView.findViewById(R.id.scriptingGUIBreakpointActiveIcon);
        activeImageView.setImageAlpha(DISABLED_IMAGE_ALPHA);
        ImageButton enabledImageButton = mainGUILayoutView.findViewById(R.id.scriptingGUIBreakpointEnableIcon);
        if(enabledImageButton == null) {
            return;
        }
        enabledImageButton.setImageAlpha(ENABLED_IMAGE_ALPHA);
        enabledImageButton.setTag((Object) this);
        enabledImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btnView) {
                ScriptingBreakPoint sbpObj = (ScriptingBreakPoint) btnView.getTag();
                sbpObj.toggleEnabled();
            }
        });
        ImageButton preserveImageButton = mainGUILayoutView.findViewById(R.id.scriptingGUIBreakpointPreserveIcon);
        if(preserveImageButton == null) {
            return;
        }
        preserveImageButton.setImageAlpha(ENABLED_IMAGE_ALPHA);
        preserveImageButton.setTag((Object) this);
        preserveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btnView) {
                ScriptingBreakPoint sbpObj = (ScriptingBreakPoint) btnView.getTag();
                sbpObj.togglePreserved();
            }
        });
        TextView tvBpTypeDesc = mainGUILayoutView.findViewById(R.id.scriptingGUIBreakpointTypeDesc);
        if(tvBpTypeDesc == null) {
            return;
        }
        tvBpTypeDesc.setText(isLabelType() ? "<BP_LABEL>" : "<BP_LINE>");
        TextView tvBpValue = mainGUILayoutView.findViewById(R.id.scriptingGUIBreakpointLabelValue);
        if(tvBpValue == null) {
            return;
        }
        if(isLabelType) {
            tvBpValue.setText(String.format(Locale.getDefault(), "@%s", getLabel()));
        }
        else {
            tvBpValue.setText(String.format(Locale.getDefault(), "L%d (0x%02X)", getLineNumber(), getLineNumber()));
        }
    }

    public boolean isLabelType() {
        return isLabelType;
    }

    public String getLabel() {
        return lineLabel;
    }

    public boolean isLineType() {
        return !isLabelType;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public View getGUIContainer() {
        return mainGUILayoutView;
    }

    public boolean toggleEnabled() {
        isEnabled = !isEnabled;
        ImageButton enabledImageButton = mainGUILayoutView.findViewById(R.id.scriptingGUIBreakpointEnableIcon);
        if(isEnabled) {
            enabledImageButton.setImageDrawable(ScriptingConfig.SCRIPTING_CONFIG_ACTIVITY_CONTEXT.getResources().getDrawable(R.drawable.breakpoint_enabled_icon24));
            enabledImageButton.setImageAlpha(ENABLED_IMAGE_ALPHA);
        }
        else {
            enabledImageButton.setImageDrawable(ScriptingConfig.SCRIPTING_CONFIG_ACTIVITY_CONTEXT.getResources().getDrawable(R.drawable.breakpoint_disabled_icon24));
            enabledImageButton.setImageAlpha(DISABLED_IMAGE_ALPHA);
        }
        return isEnabled;
    }

    public boolean togglePreserved() {
        isPreserved = !isPreserved;
        ImageButton preserveImageButton = mainGUILayoutView.findViewById(R.id.scriptingGUIBreakpointPreserveIcon);
        if(isPreserved) {
            preserveImageButton.setImageAlpha(ENABLED_IMAGE_ALPHA);
        }
        else {
            preserveImageButton.setImageAlpha(DISABLED_IMAGE_ALPHA);
        }
        return isPreserved;
    }

    public void setActive(boolean isActive) {
        if(bpActiveIndex >= 0) {
            bpActiveIndex = -1;
            breakpointsObjList.get(bpActiveIndex).setActive(false);
        }
        bpActiveIndex = breakpointsGUIViewsList.indexOf(mainGUILayoutView);
        View bpLayoutView = breakpointsObjList.get(bpActiveIndex).getGUIContainer();
        ImageView activeImageView = bpLayoutView.findViewById(R.id.scriptingGUIBreakpointActiveIcon);
        if(isActive) {
            activeImageView.setImageAlpha(ENABLED_IMAGE_ALPHA);
            activeImageView.setVisibility(ImageView.VISIBLE);
        }
        else {
            activeImageView.setImageAlpha(DISABLED_IMAGE_ALPHA);
            activeImageView.setVisibility(ImageView.INVISIBLE);
        }
    }

    public static boolean removeBreakpoint(View bpGUIView) {
        int bpIndex = breakpointsGUIViewsList.indexOf(bpGUIView);
        return removeBreakpoint(bpIndex);
    }

    public static boolean removeBreakpoint(int bpIndex) {
        if(bpIndex < 0 || bpIndex >= breakpointsObjList.size()) {
            return false;
        }
        if(bpActiveIndex == bpIndex) {
            bpActiveIndex = -1;
        }
        breakpointsObjList.remove(bpIndex);
        breakpointsGUIViewsList.remove(bpIndex);
        breakpointsGUIDisplayContainer.removeViewAt(bpIndex);
        return true;
    }

    private static boolean searchBreakpointByLineNumber(int lineNumber) {
        for(int bpIdx = 0; bpIdx < breakpointsObjList.size(); bpIdx++) {
            if(breakpointsObjList.get(bpIdx).isLineType() && breakpointsObjList.get(bpIdx).getLineNumber() == lineNumber) {
                return true;
            }
        }
        return false;
    }

    public static boolean searchBreakpointByLineLabel(String lineLabel) {
        for(int bpIdx = 0; bpIdx < breakpointsObjList.size(); bpIdx++) {
            if(breakpointsObjList.get(bpIdx).isLabelType() && breakpointsObjList.get(bpIdx).getLabel().equals(lineLabel)) {
                return true;
            }
        }
        return false;
    }

    public static boolean addBreakpoint(int lineNumber) {
        if(bpDisabled) {
            Utils.displayToastMessageShort("Setting breakpoints is disabled.");
            return false;
        }
        else if(lineNumber <= 0) {
            return false;
        }
        else if(searchBreakpointByLineNumber(lineNumber)) {
            return false;
        }
        ScriptingBreakPoint bp = new ScriptingBreakPoint(lineNumber);
        breakpointsObjList.add(bp);
        breakpointsGUIViewsList.add(bp.getGUIContainer());
        breakpointsGUIDisplayContainer.addView(bp.getGUIContainer());
        return true;
    }

    public static boolean addBreakpoint(String lineLabel) {
        if(bpDisabled) {
            Utils.displayToastMessageShort("Setting breakpoints is disabled.");
            return false;
        }
        else if(lineLabel.equals("")) {
            return false;
        }
        else if(searchBreakpointByLineLabel(lineLabel)) {
            return false;
        }
        ScriptingBreakPoint bp = new ScriptingBreakPoint(lineLabel);
        breakpointsObjList.add(bp);
        breakpointsGUIViewsList.add(bp.getGUIContainer());
        breakpointsGUIDisplayContainer.addView(bp.getGUIContainer());
        return true;
    }

}
