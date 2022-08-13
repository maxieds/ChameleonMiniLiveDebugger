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

import com.maxieds.chameleonminilivedebugger.AndroidLogger;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariable;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScripting.ChameleonScriptInstance;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.RuleNode;

public class ChameleonScriptVisitorExtended extends ChameleonScriptParserBaseVisitor<ScriptVariable> {

    private static final String TAG = ChameleonScriptVisitorExtended.class.getSimpleName();

    private ChameleonScriptInstance scriptContext;

    public ChameleonScriptVisitorExtended(ChameleonScriptInstance ctxInstance) {
        scriptContext = ctxInstance;
    }

    @Override
    public ScriptVariable visitWhile_loop(ChameleonScriptParser.While_loopContext ctx) {
        setActiveLineOfCode(ctx);
        AndroidLogger.i(TAG, "Before WHILE BLOCK");
        while(this.visit(ctx.oe).getValueAsBoolean()) {
            this.visit(ctx.scrLineBlk);
            AndroidLogger.i(TAG, "Visiting WHILE BLOCK");
        }
        return this.visitChildren(ctx);
    }

    @Override
    public ScriptVariable visitIf_block(ChameleonScriptParser.If_blockContext ctx) {
        setActiveLineOfCode(ctx);
        ScriptVariable boolPreCond = this.visit(ctx.oe);
        if(boolPreCond.getValueAsBoolean()) {
            this.visit(ctx.scrLineBlk);
            AndroidLogger.i(TAG, "Visiting IF (SG) BLOCK");
        }
        return this.visitChildren(ctx);
    }

    @Override
    public ScriptVariable visitIfelse_block(ChameleonScriptParser.Ifelse_blockContext ctx) {
        setActiveLineOfCode(ctx);
        if(this.visit(ctx.ifoe).getValueAsBoolean()) {
            this.visit(ctx.scrLineBlkIf);
            AndroidLogger.i(TAG, "Visiting IF BLOCK");
        }
        else {
            this.visit(ctx.scrLineBlkElse);
            AndroidLogger.i(TAG, "Visiting ELSE BLOCK");
        }
        return this.visitChildren(ctx);
    }

    @Override
    public ScriptVariable visitLabel_statement(ChameleonScriptParser.Label_statementContext ctx) {
        setActiveLineOfCode(ctx);
        String labelName = ctx.lblNameWithSep.getText().replaceAll(":", "");
        if(ScriptingBreakPoint.searchBreakpointByLineLabel(labelName)) {
            scriptContext.postBreakpointLabel(labelName, ctx);
        }
        return this.visitChildren(ctx);
    }

    @Override
    public ScriptVariable visitChildren(RuleNode parentCtx) {
        setActiveLineOfCode(parentCtx);
        return super.visitChildren(parentCtx);
    }

    public static void setActiveLineOfCode(RuleNode ctx) {
        try {
            int activeLOC = ((ParserRuleContext) ctx).getStart().getLine();
            ChameleonScripting.getRunningInstance().setActiveLineOfCode(activeLOC);
        } catch(Exception ex) {
            AndroidLogger.printStackTrace(ex);
            ChameleonScripting.getRunningInstance().setActiveLineOfCode(-1);
        }
    }

}
