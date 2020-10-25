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
import ScriptingPrimitives, ScriptingAPI;

file_contents: (script_line)+ EOF;

label_statement: lblName=LabelText LabelEndDelimiter {
          // TODO: no goto for now, so see if have encountered a breakpoint ...
     }
     ;

exec_chameleon_command returns [String cmdResult]:
     ExecChameleonCommandInit expression_eval_term ExecChameleonCommandEnd {
          // TODO: Call chameleon command and assemble result string ...
     }
     ;

script_line: label_statement | exec_chameleon_command ;

LabelText: (AsciiChar)+ ;
LabelEndDelimiter: ':' ;

ExecChameleonCommandInit: '$$(' ;
ExecChameleonCommandEnd: ')' ;

/*

conditional_if_block returns [boolean runBlock]:
     IfBranchKeyword ConditionalOpen boolCond=expression_eval_term ConditionalClose
     OpenBlockStart (script_line)* CloseBlockEnd {
          $runBlock=$boolCond.getAsBoolean();
     }
     ;
conditional_elif_block returns [boolean runBlock]:
     ElifBranchKeyword ConditionalOpen boolCond=expression_eval_term ConditionalClose
     OpenBlockStart (script_line)* CloseBlockEnd {
          $runBlock=$boolCond.getAsBoolean();
     }
     ;
conditional_else_block returns [boolean runBlock]:
     ElseBranchKeyword ConditionalOpen boolCond=expression_eval_term ConditionalClose
     OpenBlockStart (script_line)* CloseBlockEnd {
          $runBlock=$boolCond.getAsBoolean();
     }
     ;

conditional_block: conditional_if_block (conditional_elif_block)* |
                   conditional_if_block (conditional_elif_block)* conditional_else_block ;

loop_block returns [boolean runBlock]:
     WhileLoopKeyword ConditionalOpen boolCond=expression_eval_term ConditionalClose
     OpenBlockStart (script_line)* CloseBlockEnd {
          $runBlock=$boolCond.getAsBoolean();
     }
     ;

ConditionalOpen: '(' ;
ConditionalClose: ')' ;
OpenBlockStart: '{' ;
CloseBlockEnd: '}' ;
WhileLoopKeyword: 'while' ;
IfBranchKeyword: 'if' ;
ElifBranchKeyword: 'else if' ;
ElseBranchKeyword: 'else' ;

*/