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

grammar ChameleonScript;
import ScriptingPrimitives, ScriptingAPI;

@rulecatch {
    catch(ScriptingExecptions.ChameleonScriptingException rtEx) {
        throw rtEx;
    }
}

/* Start rule for the main grammar: */
file_contents: (script_line)+ EOF;

label_statement: lblNameWithSep=LabelText {
          String labelName = $lblNameWithSep.text.substring(0, $lblNameWithSep.text.length() - 1);
          // TODO: no goto for now, so see if have encountered a breakpoint ...
     }
     ;

exec_chameleon_command returns [ScriptVariable cmdResult]:
     ExecChameleonCommandInit eet=expression_eval_term ExecChameleonCommandEnd {
          $cmdResult=ChameleonIOHandler.executeChameleonCommandForResult($eet.svar.getValueAsString());
     }
     ;

script_line: label_statement | exec_chameleon_command | scripting_api_function_result |
             assignment_operation | assignment_by_array_slice ;

LabelText: (AsciiChar)+ ColonSeparator ;

ExecCommandStartSymbol: VariableStartSymbol VariableStartSymbol ;
ExecChameleonCommandInit: ExecCommandStartSymbol OpenParens;
ExecChameleonCommandEnd: ClosedParens ;