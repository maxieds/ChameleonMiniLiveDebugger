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

grammar ScriptingAPI;
import ScriptingPrimitives;

function_args_list returns [List<ScriptVariable> varsList]:
     var=expression_eval_term FunctionArgInnerSeparator argsList=function_args_list {
          $argsList.varsList.add($var.svar);
          $varsList=$argsList.varsList;
     }
     |
     var=expression_eval_term {
          $varsList=new ArrayList<ScriptVariable>();
          $varsList.add($var.svar);
     }
     |
     {
          $varsList=new ArrayList<ScriptVariable>();
     }
     ;

scripting_api_function returns [ScriptVariable funcResult]:
     funcName=ScriptingAPIFunctionName FunctionStartArgsDelimiter
     funcArgs=function_args_list FunctionEndArgsDelimiter {
          $funcResult=ScriptingFunctions.callFunction($funcName.text, $funcArgs.varsList);
     }
     ;

FunctionArgInnerSeparator:  CommaSeparator ;
FunctionStartArgsDelimiter: OpenParens ;
FunctionEndArgsDelimiter:   ClosedParens ;

ScriptingAPIFunctionName:  ScriptControlFlowFunctions | PrintingAndLoggingFunctions |
                           ChameleonConnectionTypeFunctions | VariableTypeFunctions |
                           ChameleonCommandAndLogFunctions |
                           StringFunctions | APDUHandlingFunctions |
                           CryptoAndHashFunctions | UtilityFunctions ;

/* Script control flow functions: */
/* Debugging and assertion commands */
ExitFuncName:               'Exit' ;
AssertFuncName:             'Assert' ;

ScriptControlFlowFunctions: ExitFuncName | AssertFuncName;

/* Console printing and logging functions: */
PrintFuncName:              'Print' ;
PrintfFuncName:             'Printf' ;
SprintfFuncName:            'Sprintf' ;

PrintingAndLoggingFunctions: PrintFuncName | PrintfFuncName | SprintfFuncName;

/* Type conversion and checking functions: */
/* Environmental variables: */
AsHexStringFuncName:      'AsHexString' ;
AsBinaryStringFuncName:   'AsBinaryString' ;
AsByteArrayFuncName:      'AsByteArray' ;
GetLengthFuncName:        'GetLength' ;
GetEnvFuncName:           'GetEnv' ;

VariableTypeFunctions:     AsHexStringFuncName | AsBinaryStringFuncName | AsByteArrayFuncName |
                           GetLengthFuncName | GetEnvFuncName;

/* Chameleon connection types: */
IsChameleonConnectedFuncName:     'IsChameleonConnected' ;
IsChameleonRevGFuncName:          'IsChameleonRevG' ;
IsChameleonRevEFuncName:          'IsChameleonRevE' ;
GetChameleonDescFuncName:         'GetChameleonDesc' ;

ChameleonConnectionTypeFunctions: IsChameleonConnectedFuncName |
                                  IsChameleonRevGFuncName | IsChameleonRevEFuncName ;

/* Chameleon command and command output post processing functions: */
CmdDownloadTagFuncName:              'DownloadTagDump' ;
CmdUploadTagFuncName:                'UploadTagDump' ;
CmdDownloadLogsFuncName:             'DownloadLogs' ;

ChameleonCommandAndLogFunctions:     CmdDownloadTagFuncName | CmdUploadTagFuncName |
                                     CmdDownloadLogsFuncName ;

/* String handling functions: */
StringSearchFuncName:              'Find' ;
StringContainsFuncName:            'Contains' ;
StringReplaceFuncName:             'Replace' ;
StringSplitFuncName:               'Split' ;
StringStripFuncName:               'Strip' ;
SubstrFuncName:                    'Substring' ;

StringFunctions:                   StringSearchFuncName | StringContainsFuncName | StringReplaceFuncName |
                                   StringStripFuncName | StringSplitFuncName | SubstrFuncName ;

/* APDU handling functions: */
AsWrappedAPDUFuncName:                 'AsWrappedAPDU' ; // ($v -- assumes have prepended CLA,INS); -> ByteArray | ($v, CLA,INS,P1,P2)
ExtractDataFromWrappedAPDUFuncName:    'ExtractDataFromWrappedAPDU' ;
ExtractDataFromNativeAPDUFuncName:     'ExtractDataFromNativeAPDU' ;
SplitWrappedAPDUFuncName:              'SplitAPDUResponse' ;
SearchAPDUCStatusCodesFuncName:        'SearchAPDUStatusCodes' ;
SearchAPDUInsCodesFuncName:            'SearchAPDUInsCodes' ;
SearchAPDUClaCodesFuncName:            'SearchAPDUClaCodes' ;

APDUHandlingFunctions:                 AsWrappedAPDUFuncName | ExtractDataFromWrappedAPDUFuncName |
                                       ExtractDataFromNativeAPDUFuncName | SplitWrappedAPDUFuncName |
                                       SearchAPDUCStatusCodesFuncName | SearchAPDUInsCodesFuncName |
                                       SearchAPDUClaCodesFuncName ;

/* Crypto and hash related functionality: */
GetRandomBytesFuncName:       'RandomBytes' ;
GetRandomIntFuncName:         'RandomInt32' ;
GetCRC16FuncName:             'GetCRC16' ;
AppendCRC16FuncName:          'AppendCRC16' ;
CheckCRC16FuncName:           'CheckCRC16' ;
GetCommonKeysFuncName:        'GetCommonKeys' ;
GetUserKeysFuncName:          'GetUserKeys' ;

CryptoAndHashFunctions:       GetRandomBytesFuncName | GetRandomIntFuncName |
                              GetCRC16FuncName | AppendCRC16FuncName | CheckCRC16FuncName |
                              GetCommonKeysFuncName | GetUserKeysFuncName ;

/* Misc utility functions: */
GetTimestampFuncName:          'GetTimestamp' ;
MemoryXORFuncName:             'MemoryXOR' ;
MaxFuncName:                   'Max' ;
MinFuncName:                   'Min' ;
ArrayReverseFuncName:          'Reverse' ;
ArrayPadLeftFuncName:          'PadLeft' ;
ArrayPadRightFuncName:         'PadRight' ;
GetSubarrayFuncName:           'GetSubarray' ;
GetConstantStringFuncName:     'GetConstantString' ;
GetConstantByteArrayFuncName:  'GetConstantArray' ;
GetIntegersFromRangeFuncName:  'IntegerRange' ;

UtilityFunctions:              GetTimestampFuncName |
                               MemoryXORFuncName | MaxFuncName | MinFuncName |
                               ArrayReverseFuncName | ArrayPadLeftFuncName | ArrayPadRightFuncName |
                               GetSubarrayFuncName | GetConstantStringFuncName | GetConstantByteArrayFuncName |
                               GetIntegersFromRangeFuncName ;