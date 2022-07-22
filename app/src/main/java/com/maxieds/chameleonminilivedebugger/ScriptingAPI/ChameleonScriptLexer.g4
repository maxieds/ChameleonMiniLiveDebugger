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

lexer grammar ChameleonScriptLexer;

/**** The built-in functions API list (unique function name identifiers matched first): ****/

ScriptingAPIFunctionName:  ScriptControlFlowFunctions | PrintingAndLoggingFunctions |
                           ChameleonConnectionTypeFunctions | VariableTypeFunctions |
                           ChameleonCommandAndLogFunctions |
                           StringFunctions | APDUHandlingFunctions |
                           CryptoAndHashFunctions | UtilityFunctions ;

/**** Script control flow functions:    ****/
/**** Debugging and assertion commands: ****/
ExitFuncName:               'Exit(' ;
AssertFuncName:             'Assert(' ;

ScriptControlFlowFunctions: ExitFuncName | AssertFuncName;

/**** Console printing and logging functions: ****/
PrintFuncName:              'Print(' ;
PrintfFuncName:             'Printf(' ;
SprintfFuncName:            'Sprintf(' ;

PrintingAndLoggingFunctions: PrintFuncName | PrintfFuncName | SprintfFuncName;

/**** Type conversion and checking functions: ****/
/**** Environmental variables: ****/
AsHexStringFuncName:      'AsHexString(' ;
AsBinaryStringFuncName:   'AsBinaryString(' ;
AsByteArrayFuncName:      'AsByteArray(' ;
GetLengthFuncName:        'GetLength(' ;
GetEnvFuncName:           'GetEnv(' ;

VariableTypeFunctions:     AsHexStringFuncName | AsBinaryStringFuncName | AsByteArrayFuncName |
                           GetLengthFuncName | GetEnvFuncName;

/* Chameleon connection types: */
IsChameleonConnectedFuncName:     'IsChameleonConnected(' ;
IsChameleonRevGFuncName:          'IsChameleonRevG(' ;
IsChameleonRevEFuncName:          'IsChameleonRevE(' ;
GetChameleonDescFuncName:         'GetChameleonDesc(' ;

ChameleonConnectionTypeFunctions: IsChameleonConnectedFuncName |
                                  IsChameleonRevGFuncName | IsChameleonRevEFuncName ;

/**** Chameleon command and command output post processing functions: ****/
CmdDownloadTagFuncName:              'DownloadTagDump(' ;
CmdUploadTagFuncName:                'UploadTagDump(' ;
CmdDownloadLogsFuncName:             'DownloadLogs(' ;

ChameleonCommandAndLogFunctions:     CmdDownloadTagFuncName | CmdUploadTagFuncName |
                                     CmdDownloadLogsFuncName ;

/**** String handling functions: ****/
StringSearchFuncName:              'StringFind(' ;
StringContainsFuncName:            'StringContains(' ;
StringReplaceFuncName:             'StringReplace(' ;
StringCatFuncName:                 'Strcat(';
StringSplitFuncName:               'StringSplit(' ;
StringStripFuncName:               'StringStrip(' ;
SubstrFuncName:                    'Substr(' ;

StringFunctions:                   StringSearchFuncName | StringContainsFuncName | StringReplaceFuncName |
                                   StringStripFuncName | StringSplitFuncName | SubstrFuncName ;

/**** APDU handling functions: ****/
ExtractDataFromWrappedAPDUFuncName:    'ExtractDataFromWrappedAPDU(' ;
ExtractDataFromNativeAPDUFuncName:     'ExtractDataFromNativeAPDU(' ;
SplitWrappedAPDUFuncName:              'SplitAPDUResponse(' ;
SearchAPDUCStatusCodesFuncName:        'SearchAPDUStatusCodes(' ;
SearchAPDUInsCodesFuncName:            'SearchAPDUInsCodes(' ;
SearchAPDUClaCodesFuncName:            'SearchAPDUClaCodes(' ;

APDUHandlingFunctions:                 AsWrappedAPDUFuncName | ExtractDataFromWrappedAPDUFuncName |
                                       ExtractDataFromNativeAPDUFuncName | SplitWrappedAPDUFuncName |
                                       SearchAPDUCStatusCodesFuncName | SearchAPDUInsCodesFuncName |
                                       SearchAPDUClaCodesFuncName ;

/**** Crypto and hash related functionality: ****/
GetRandomBytesFuncName:       'RandomBytes(' ;
GetRandomIntFuncName:         'RandomInt32(' ;
GetCRC16FuncName:             'GetCRC16(' ;
AppendCRC16FuncName:          'AppendCRC16(' ;
CheckCRC16FuncName:           'CheckCRC16(' ;
GetCommonKeysFuncName:        'GetCommonKeys(' ;
GetUserKeysFuncName:          'GetUserKeys(' ;

CryptoAndHashFunctions:       GetRandomBytesFuncName | GetRandomIntFuncName |
                              GetCRC16FuncName | AppendCRC16FuncName | CheckCRC16FuncName |
                              GetCommonKeysFuncName | GetUserKeysFuncName ;

/**** Misc utility functions: ****/
GetTimestampFuncName:          'GetTimestamp(' ;
MemoryXORFuncName:             'MemoryXOR(' ;
MaxFuncName:                   'Max(' ;
MinFuncName:                   'Min(' ;
ArrayReverseFuncName:          'Reverse(' ;
ArrayPadLeftFuncName:          'PadLeft(' ;
ArrayPadRightFuncName:         'PadRight(' ;
GetSubarrayFuncName:           'GetSubarray(' ;
ArrayToStringFuncName:         'ArrayToString(' ;
GetConstantStringFuncName:     'GetConstantString(' ;
GetConstantByteArrayFuncName:  'GetConstantArray(' ;
GetIntegersFromRangeFuncName:  'IntegerRange(' ;

UtilityFunctions:              GetTimestampFuncName |
                               MemoryXORFuncName | MaxFuncName | MinFuncName |
                               ArrayReverseFuncName | ArrayPadLeftFuncName | ArrayPadRightFuncName |
                               GetSubarrayFuncName | ArrayToStringFuncName |
                               GetConstantStringFuncName | GetConstantByteArrayFuncName |
                               GetIntegersFromRangeFuncName ;

/**** Loops and conditional blocks: ****/

While: 'while' ;
IfCond: 'if' ;
ElseCond: 'else' ;

/**** Other syntax constants and regex matchers to define non-constant ids: ****/

QuotedStringLiteral: '"' (~["] | '\\' [a-z0-9] | [\n\r\t]+ )* '"' ;
QuotedHexStringLiteral: 'h\'' (~[\\"] | '\\' [a-z0-9])* '\'' ;
QuotedRawStringLiteral: 'r\'' (~[\\"] | '\\' [a-z0-9])* '\'' ;

WhiteSpace:            [ \t\r\u000C]+ -> channel(HIDDEN) ;
NewLine:               ('\n' ('\n\n')*)+ -> channel(HIDDEN) ;
CStyleBlockComment:    '/*'.*?'*/' -> channel(HIDDEN) ;
//CStyleLineComment:     '//'~[\n]* ('\n'|EOF) -> channel(HIDDEN) ;
CStyleLineComment:     '//' ~[\r\n]* '\r'? '\n' -> channel(HIDDEN) ;
HashStyleLineComment:  '#'~[\n]*  ('\n'|EOF) -> channel(HIDDEN) ;

EqualsComparisonOperator: '==' ;
NotEqualsComparisonOperator: '!=' ;
PlusEqualsOperator: '+=' ;
DefEqualsOperator: '=' | ':=' ;
ExecCommandStartSymbol: '$$(' ;
TernaryOperatorFirstSymbol: '?' ;

HashedIndexAccessor: '->' ;
MinusSign: '-' ;
CommaSeparator: ',' ;
OpenParens: '(' ;
ClosedParens: ')' ;
ColonSeparator: ':' ;
DoubleOpenCurlyBrace: '{{' ;
OpenBrace: '{' ;
DoubleClosedCurlyBrace: '}}' ;
ClosedBrace: '}' ;

LogicalAndOperator: '&&' | 'and' ;
LogicalOrOperator: '||' | 'or' ;
LogicalNotOperator: '!' | 'not' ;
RightShiftOperator: '>>' ;
LeftShiftOperator: '<<' ;
BitwiseAndOperator: '&' ;
BitwiseOrOperator: '|' ;
BitwiseXorOperator: '^' ;
BitwiseNotOperator: '~' ;
ArithmeticPlusOperator: '+' ;

//UnaryIncrementOperator: '++' ;
//UnaryDecrementOperator: '--' ;

TypeCastByte: '(Byte)' ;
TypeCastBytes: '(Bytes)' ;
TypeCastShort: '(Short)' ;
TypeCastInt32: '(Int32)' ;
TypeCastBoolean: '(Boolean)' ;
TypeCastString: '(String)' ;

ArrayIndexOpenBracket: '[' ;
ArrayIndexCloseBracket: ']' ;

VariableStartSymbol: '$' ;
VariableNameStartChar: '_' | [a-zA-Z] ;
VariableName: VariableNameStartChar ('_' | [a-zA-Z] | [0-9])* ;

DecimalLiteral: MinusSign [1-9] ([0-9])* | ([0-9])+ ;
HexString: ([0-9a-fA-F])+ ;
HexByte: '0x' [0-9a-fA-F] [0-9a-fA-F] | '0x' [0-9a-fA-F] ;
HexLiteral: HexByte | '0x'HexString ;
BooleanLiteral: 'true' | 'True' | 'TRUE' | 'false' | 'False' | 'FALSE' ;
LabelText: VariableName ColonSeparator ;