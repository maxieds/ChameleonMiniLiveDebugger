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

grammar FullScriptParser;
import LexerMembers, ScriptingPrimitives, ScriptingAPI, ChameleonTerminalScripting;

FileContents: (ScriptLine)+ EOF;

ScriptLine: WhiteSpace (ExecuteCommand | VariableDeclaration | AssignmentOperator | BuiltInFunction)
            WhiteSpace NewLineBreak | NewLineBreak;

ExecuteCommand: ChameleonExecuteCommand | 'pass' ;
BuiltInFunction: ScriptingAPIFunction ;

/* Maybe for later (future revisions):

FunctionName: VariableName ;
FunctionParametersList: '...' ;
FunctionVoidDeclaration: 'function void' FunctionName '(' FunctionParametersList ')' WhiteSpace
                         '{' NewLineBreak (ScriptLine)* '}' ;
FunctionValueDeclaration: 'function var' FunctionName '(' FunctionParametersList ')' WhiteSpace
                         '{' NewLineBreak (ScriptLine)*
                         'return' (VariableReference | VariableProperty) NewLineBreak '}' ;

ConditionalLabel: 'if' | 'elif' | 'else' ;
ConditionalStatement: ConditionalLabel WhiteSpace ExpressionEvalTerm WhiteSpace ':' ;
ConditionalBlock: ConditionalStatement WhiteSpace '{' WhiteSpace NewLineBreak
                  (ScriptLine)+ WhiteSpace '}' WhiteSpace NewLineBreak ;

LoopLabel: 'while' ;
LoopStatement: LoopLabel WhiteSpace '[' WhiteSpace ExpressionEvalTerm WhiteSpace ']:' ;
LoopBlock: LoopStatement WhiteSpace '{' WhiteSpace NewLineBreak
           (ScriptLine)+ WhiteSpace '}' WhiteSpace NewLineBreak  ;
*/
