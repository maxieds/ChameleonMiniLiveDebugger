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

import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScriptVisitor;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariable;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScripting.ChameleonScriptInstance;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScriptParser;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScriptBaseVisitor;

public class ChameleonScriptVisitorExtended extends ChameleonScriptBaseVisitor<ScriptVariable> {

    private static final String TAG = ChameleonScriptVisitorExtended.class.getSimpleName();

    private ChameleonScriptInstance scriptContext;

    public ChameleonScriptVisitorExtended(ChameleonScriptInstance ctxInstance) {
        scriptContext = ctxInstance;
    }

    /*public ScriptVariable visitWhile_loop(ChameleonScriptParser.While_loopContext ctx) {
        ScriptVariable boolCond = this.visit(ctx.operand_expression());
        while(boolCond.getValueAsBoolean()) {
            this.visit(ctx.script_line_block());
            boolCond = this.visit(ctx.operand_expression());
        }
        return ScriptVariable.newInstance();
    }*/

}
