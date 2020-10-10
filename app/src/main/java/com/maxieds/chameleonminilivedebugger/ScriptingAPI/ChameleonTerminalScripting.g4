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

grammar ChameleonTerminalScripting;
import ScriptingPrimitives;

ChameleonCommandResponse:
     '100:OK' |
     '101:OK WITH TEXT' (NewLineBreak StringLiteral | StringLiteral NewLineBreak | NewLineBreak StringLiteral NewLineBreak) |
     '110:WAITING FOR XMODEM' | '120:FALSE' | '121:TRUE' |
     '200:UNKNOWN COMMAND' | '201:INVALID COMMAND USAGE' |
     '202:INVALID PARAMETER' | '203:TIMEOUT' ;

ChameleonCommandArgValue: StringLiteral | HexLiteral ;
ChameleonCommandArgList:  ((WhiteSpace)* ChameleonCommandArgValue)* ;

ChameleonCommandTypeQuery:    ChameleonCommandName'?' ;
ChameleonCommandTypeQueryAll: ChameleonCommandName'=''?' ;
ChameleonCommandTypeSet:      ChameleonCommandName'='ChameleonCommandArgValue ;
ChameleonCommandTypeWithArgs: ChameleonCommandName' 'ChameleonCommandArgList ;
ChameleonExecuteCommand: ChameleonCommandTypeQuery | ChameleonCommandTypeQueryAll |
                         ChameleonCommandTypeSet | ChameleonCommandTypeWithArgs ;

ChameleonCommandXModem:      'UPLOAD' | 'DOWNLOAD' | 'LOGDOWNLOAD' ;
ChameleonCommandGenericName: 'CHARGING' | 'HELP' | 'RESET' | 'RSSI' | 'SYSTICK' | 'UPGRADE' | 'VERSION' |
                             'RBUTTON' | 'LBUTTON' | 'RBUTTON_LONG' | 'LBUTTON_LONG' | 'LEDGREEN' | 'LEDRED' |
                             'LOGMODE' | 'LOGMEM' | 'LOGCLEAR' | 'LOGSTORE' | 'SETTING' ;
ChameleonCommandSlotName:    'CONFIG' | 'UIDSIZE' | 'UID' | 'READONLY' | 'MEMSIZE' | 'UPLOAD' |
                             'STORE' | 'RECALL' | 'TIMEOUT' | 'SEND' | 'SEND_RAW' | 'GETUID' |
                             'DUMP_MFU' | 'IDENTIFY' | 'THRESHOLD' | 'AUTOCALIBRATE' | 'FIELD' ;
ChameleonCommandName:        ChameleonCommandGenericName | ChameleonCommandSlotName | ChameleonCommandXModem ;
