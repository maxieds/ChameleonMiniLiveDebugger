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

lexer grammar LexerMembers;

@lexer::members {

     /* Error handling and logging: */
     private int     _scriptLineNumber;
     private int     _scriptLineCharPos;
     private String  _scriptFileName;
     private String  _scriptFunc;
     private int     _scriptErrorCode;
     private String  _scriptErrorMsg;

     /* Scripting primitives: Current value storage: */
     private ScriptVariable _lastLiteralValue;

     /* Scripting primitives: Variable storage and recognition: */
     private Map<ScriptVariable, String> _scriptVariablesMap;

     /* Scripting primitives: Expression evaluation handling: */
     private ScriptVariable _lastExprEval;

     /* Scripting primitives: Variable property accessors: */
     private ScriptVariable _variableReference;
     private ScriptVariable _variablePropertyResult;


}