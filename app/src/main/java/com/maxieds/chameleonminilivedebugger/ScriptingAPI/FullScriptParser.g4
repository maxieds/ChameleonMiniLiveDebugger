
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
import LexerMembers, ScriptingPrimitives, ScriptingAPI;

FileContents: (ScriptLine)+ EOF;

ScriptLine:              (WhiteSpace)* (VariableDeclaration | AssignmentOperator | Command | MiscSyntax)
                         (WhiteSpace)* NewLineBreak |
                         NewLineBreak -> channel(HIDDEN) ;

LabelSyntax:             StringLiteral ':' ;
SetBreakpointSyntax:    'set' (WhiteSpace)* 'breakpoint' ;
MiscSyntax:              LabelSyntax | SetBreakpointSyntax ;

ChameleonCommand:       '$$(' (WhiteSpace)* ExpressionEvalTerm (WhiteSpace)* ')' ;
GotoLabelCommand:       'goto' (WhiteSpace)* (StringLiteral | VariableReference) ;
PassStatement:          'pass' -> channel(HIDDEN);
Command:                ScriptingAPIFunction | ChameleonCommand | GotoLabelCommand |
                        PassStatement ;