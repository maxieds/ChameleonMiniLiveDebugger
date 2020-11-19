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

import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariable;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScripting.ChameleonScriptInstance;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScriptParser;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScriptParserBaseVisitor;

public class ChameleonScriptVisitorExtended extends ChameleonScriptParserBaseVisitor<ScriptVariable> {

    private static final String TAG = ChameleonScriptVisitorExtended.class.getSimpleName();

    private ChameleonScriptInstance scriptContext;

    public ChameleonScriptVisitorExtended(ChameleonScriptInstance ctxInstance) {
        scriptContext = ctxInstance;
    }

    public ScriptVariable visitWhile_loop(ChameleonScriptParser.While_loopContext ctx) {
        ScriptVariable boolPreCond = this.visit(ctx.oe);
        while(boolPreCond.getValueAsBoolean()) {
            this.visit(ctx.scrLineBlk);
            boolPreCond = this.visit(ctx.oe);
        }
        return ScriptVariable.newInstance();
    }

    public ScriptVariable visitIf_block(ChameleonScriptParser.If_blockContext ctx) {
        ScriptVariable boolPreCond = this.visit(ctx.oe);
        if(boolPreCond.getValueAsBoolean()) {
            this.visit(ctx.scrLineBlk);
        }
        return ScriptVariable.newInstance();
    }

    public ScriptVariable visitIfelse_block(ChameleonScriptParser.Ifelse_blockContext ctx) {
        ScriptVariable boolPreCond = this.visit(ctx.ifoe);
        if(boolPreCond.getValueAsBoolean()) {
            this.visit(ctx.scrLineBlkIf);
        }
        else {
            this.visit(ctx.scrLineBlkElse);
        }
        return ScriptVariable.newInstance();
    }

    public ScriptVariable visitLabel_statement(ChameleonScriptParser.Label_statementContext ctx) {
        String labelName = ctx.lblNameWithSep.getText();
        scriptContext.postBreakpointLabel(labelName, ctx);
        return ScriptVariable.newInstance();
    }

}
