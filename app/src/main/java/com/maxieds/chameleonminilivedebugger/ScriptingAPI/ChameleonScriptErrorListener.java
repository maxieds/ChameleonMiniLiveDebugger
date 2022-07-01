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

import android.util.Log;

import com.maxieds.chameleonminilivedebugger.AndroidLog;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.Utils;

import java.util.ArrayList;
import java.util.List;

public class ChameleonScriptErrorListener extends BaseErrorListener implements ANTLRErrorListener {

    private static final String TAG = ChameleonScriptErrorListener.class.getSimpleName();

    public static class SyntaxError {

        private final Recognizer<?, ?> recognizer;
        private final Object offendingSymbol;
        private final int line;
        private final int charPositionInLine;
        private final String message;
        private final RecognitionException e;

        SyntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            this.recognizer = recognizer;
            this.offendingSymbol = offendingSymbol;
            this.line = line;
            this.charPositionInLine = charPositionInLine;
            this.message = msg;
            this.e = e;
        }

        public Recognizer<?, ?> getRecognizer() {
            return recognizer;
        }

        public Object getOffendingSymbol() {
            return offendingSymbol;
        }

        public int getLine() {
            return line;
        }

        public int getCharPositionInLine() {
            return charPositionInLine;
        }

        public String getMessage() {
            return message;
        }

        public RecognitionException getException() {
            return e;
        }

    }

    private final List<SyntaxError> syntaxErrors = new ArrayList();

    public ChameleonScriptErrorListener() {}

    List<SyntaxError> getSyntaxErrors() {
        return syntaxErrors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e) {
        syntaxErrors.add(new SyntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e));
        String sourceFileName = recognizer.getInputStream().getSourceName();
        sourceFileName = !sourceFileName.isEmpty() ? sourceFileName + ": " : "";
        AndroidLog.i(TAG, sourceFileName + "line #" + line + " @ " + charPositionInLine + ": " + msg);
    }

    @Override
    public String toString() {
        return Utils.join(syntaxErrors.iterator(), "\n");
    }

}
