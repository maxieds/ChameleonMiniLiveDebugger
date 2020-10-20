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
import LexerMembers, ScriptingPrimitives;

FunctionParameterArgType: ExpressionEvalTerm ;
FunctionParameterArgsList: FunctionParameterArgType | (FunctionParameterArgType ', ' FunctionParameterArgType) ;
ScriptingAPIFunctionName:  ScriptControlFlowFunctions | PrintingAndLoggingFunctions | VariableTypeFunctions |
                           DebuggingFunctions | EnvironmentFunctions | ChameleonCommandAndLogFunctions |
                           StringFunctions | FileIOFunctions | APDUHandlingFunctions |
                           CryptoAndHashFunctions | UtilityFunctions ;
ScriptingAPIFunction:      ScriptingAPIFunctionName '(' FunctionParameterArgsList ')' |
                           ScriptingAPIFunctionName '()' ;

/* Script control flow functions: */
ExitFuncName:               'Exit' ;

ScriptControlFlowFunctions: ExitFuncName ;

/* Console printing and logging functions: */
PrintFuncName:              'Print' ;
PrintfFuncName:             'Printf' ;
PrintLogFuncName:           'PrintLog' ;
PrintfLogFuncName:          'PrintfLog' ;
DisplayToastFuncName:       'DisplayToast' ;

PrintingAndLoggingFunctions: PrintFuncName | PrintfFuncName | PrintLogFuncName |
                             PrintfLogFuncName | DisplayToastFuncName ;

/* Type conversion and checking functions: */
AsInteger32FuncName:      'AsInt32' ;
EvalAsBooleanFuncName:    'EvaluateAsBoolean' ;
AsStringFuncName:         'AsString' ;
AsHexStringFuncName:      'AsHexString' ;
AsBinaryStringFuncName:   'AsBinaryString' ;
AsByteArrayFuncName:      'AsByteArray' ;
GetLengthFuncName:        'GetLength' ;
IsConstantFuncName:       'IsConstant' ;
IsFloatFuncName:          'IsFloat' ;
IsHexFuncName:            'IsHex' ;
IsDecimalFuncName:        'IsDecimal' ;
IsNumericFuncName:        'IsNumeric' ;
IsStringFuncName:         'IsString' ;
IsArrayFuncName:          'IsArray' ;
GetArrayDataTypeFuncName: 'GetArrayDataType' ;
GetTypeFuncName:          'GetType' ;
GetTypeSizeofFuncName:    'Sizeof' ;
ToStringFuncName:         'ToString' ; // (int, base);

VariableTypeFunctions:     AsInteger32FuncName | EvalAsBooleanFuncName | AsStringFuncName |
                           AsHexStringFuncName | AsBinaryStringFuncName | AsByteArrayFuncName |
                           GetLengthFuncName | IsConstantFuncName | IsFloatFuncName |
                           IsHexFuncName | IsDecimalFuncName | IsNumericFuncName |
                           IsStringFuncName | IsArrayFuncName | GetArrayDataTypeFuncName |
                           GetTypeFuncName | GetTypeSizeofFuncName | ToStringFuncName ;

/* Debugging and assertion commands */
AssertFuncName:            'Assert' ;
AssertEqualFuncName:       'AssertEqual' ;
AssertNotEqualFuncName:    'AssertNotEqual' ;

DebuggingFunctions:         AssertFuncName | AssertEqualFuncName | AssertNotEqualFuncName ;

/* Environmental variables: */
GetEnvFuncName:             'GetEnv' ;

EnvironmentFunctions:        GetEnvFuncName ;

/* Chameleon command and command output post processing functions: */
CmdGetResponseFuncName:              'GetCommandResponse' ;
CmdGetResponseCodeFuncName:          'GetCommandResponseCode' ;
CmdGetResponseDescFuncName:          'GetCommandResponseDesc' ;
GetResponseDataFuncName:             'GetCommandResponseData' ;
CmdIsSuccessFuncName:                'CommandIsSuccess' ;
CmdIsErrorFuncName:                  'CommandIsError' ;
CmdContainsDataFuncName:             'CommandContainsData' ;
CmdGetIntermedLogsFuncName:          'GetIntermediateLogs' ;
CmdSaveDeviceStateFuncName:          'SaveDeviceState' ;
CmdRestoreDeviceStateFuncName:       'RestoreDeviceState' ;
CmdDownloadTagFuncName:              'DownloadTagDump' ;
CmdUploadTagFuncName:                'UploadTagDump' ;
CmdDownloadLogsFuncName:             'DownloadLogs' ;

GetLogTypeFuncName:                  'GetLogType' ;
GetLogDataLengthFuncName:            'GetLogDataLength' ;
GetLogSystickTimeFuncName:           'GetLogSystickTime' ;
GetLogDataFuncName:                  'GetLogData' ;
GetLogDirectionFuncName:             'GetLogDirection' ;
GetLogByteCodeFuncName:              'GetLogByteCode' ;
GetLogDescFuncName:                  'GetLogDesc' ;

ChameleonCommandAndLogFunctions:     CmdGetResponseFuncName | CmdGetResponseCodeFuncName | CmdGetResponseDescFuncName |
                                     GetResponseDataFuncName | CmdIsSuccessFuncName | CmdIsErrorFuncName |
                                     CmdContainsDataFuncName | CmdGetIntermedLogsFuncName | CmdSaveDeviceStateFuncName |
                                     CmdRestoreDeviceStateFuncName | CmdDownloadTagFuncName | CmdUploadTagFuncName |
                                     CmdDownloadLogsFuncName | GetLogTypeFuncName | GetLogDataLengthFuncName |
                                     GetLogSystickTimeFuncName | GetLogDataFuncName | GetLogByteCodeFuncName |
                                     GetLogDescFuncName ;

/* File I/O functions: */
FscanfFuncName:           'Fscanf' ;
ReadXMLFileFuncName:      'ReadXMLFile' ;
ReadCSVFileFuncName:      'ReadCSVFile' ;
ReadTextFileName:         'ReadTextFile' ;
ReadBinaryFileFuncName:   'ReadBinaryFile' ;
WriteXMLFileFuncName:     'WriteXMLFile' ;
WriteCSVFileFuncName:     'WriteCSVFile' ;
WriteTextFileName:        'WriteTextFile' ;
WriteBinaryFileFuncName:  'WriteBinaryFile' ;

FileIOFunctions:          FscanfFuncName | ReadXMLFileFuncName | ReadXMLFileFuncName |
                          ReadTextFileName | ReadBinaryFileFuncName |
                          WriteXMLFileFuncName | WriteCSVFileFuncName |
                          WriteTextFileName | WriteBinaryFileFuncName ;

/* String handling functions: */
StringSearchFuncName:              'Find' ;
StringContainsFuncName:            'Contains' ;
StringReplaceFuncName:             'Replace' ;
StringStripFuncName:               'Strip' ;
StringCompressWhitepaceFuncName:   'CompressWhitespace' ;
SubstrFuncName:                    'Substr' ;

StringFunctions:                   StringSearchFuncName | StringContainsFuncName | StringReplaceFuncName |
                                   StringStripFuncName | StringCompressWhitepaceFuncName |
                                   SubstrFuncName ;

/* APDU handling functions: */
AsWrappedAPDUFuncName:                 'AsWrappedAPDU' ; // ($v -- assumes have prepended CLA,INS); -> ByteArray | ($v, CLA,INS,P1,P2)
ExtractDataFromWrappedAPDUFuncName:    'ExtractDataFromWrappedAPDU' ;
ExtractDataFromNativeAPDUFuncName:     'ExtractDataFromNativeAPDU' ;
SearchAPDUCStatusCodesFuncName:        'SearchAPDUStatusCodes' ;
SearchAPDUInsCodesFuncName:            'SearchAPDUInsCodes' ;
SearchAPDUClaCodesFuncName:            'SearchAPDUClaCodes' ;
NFCAntiColFuncName:                    'NFCAntiCol' ; // returns arrays: (UID,ATQA,SAK,ATS)

APDUHandlingFunctions:                 AsWrappedAPDUFuncName | ExtractDataFromWrappedAPDUFuncName |
                                       ExtractDataFromNativeAPDUFuncName |
                                       SearchAPDUCStatusCodesFuncName | SearchAPDUInsCodesFuncName |
                                       SearchAPDUClaCodesFuncName | NFCAntiColFuncName;

/* Crypto and hash related functionality: */
GetRandomBytesFuncName:       'RandomBytes' ;
GetRandomIntFuncName:         'RandomInt32' ;
EncodeBase64HashFuncName:     'EncodeBase64' ;
DecodeBase64FuncName:         'DecodeBase64' ;
GetMD5HashFuncName:           'GetMD5Hash' ;     // (java.security.MessageDigest)
GetSHA1FuncName:              'GetSHA1Hash' ;
GetSHA256FuncName:            'GetSHA256Hash' ;
GetSHA512FuncName:            'GetSHA512Hash' ;
GetCRC16FuncName:             'GetCRC16' ;
AppendCRC16FuncName:          'AppendCRC16' ;
CheckCRC16FuncName:           'CheckCRC16' ;
GetCommonKeysFuncName:        'GetCommonKeys' ;
GetUserKeysFuncName:          'GetUserKeys' ;

EncryptionSchemeModes:         'AES128-CBC' | 'AES128-ECB' |
                               'DES-CBC' | 'DES-ECB' | '3DES-CBC' | '3DES-ECB' ;
CryptoEncryptFuncName:         'Encrypt' ;
CryptoDescryptFuncName:        'Decrypt' ;

CryptoAndHashFunctions:       GetRandomBytesFuncName | GetRandomIntFuncName | EncodeBase64HashFuncName |
                              DecodeBase64FuncName | GetMD5HashFuncName | GetSHA1FuncName |
                              GetSHA256FuncName | GetSHA512FuncName | GetCRC16FuncName |
                              AppendCRC16FuncName | CheckCRC16FuncName |
                              GetCommonKeysFuncName | GetUserKeysFuncName |
                              CryptoEncryptFuncName | CryptoDescryptFuncName ;

/* Misc utility functions: */
GetTimestampFuncName:          'GetTimestamp' ;
GetLocationFuncName:           'GetLocation'  ;  // (Short|Long|VVerbose|specially %f formatted string)
SeedRandomFuncName:            'SeedRandom' ;    // (blank for time | $v)
MemoryXORFuncName:             'MemoryXOR' ;     // ??? Add to default Utils ???
MaxFuncName:                   'Max' ;
MinFuncName:                   'Min' ;
ArrayAppendFuncName:           'Append' ;
ArrayReverseFuncName:          'Reverse' ;
ArrayShiftLeftFuncName:        'ShiftLeft' ;
ArrayShiftRightFuncName:       'ShiftRight' ;
ArrayPadLeftFuncName:          'PadLeft' ;
ArrayPadRightFuncName:         'PadRight' ;
GetSubarrayFuncName:           'GetSubarray' ;
GetConstantStringFuncName:     'GetConstantString' ;
GetConstantByteArrayFuncName:  'GetConstantArray' ;
ReverseBitsFuncName:           'ReverseBits' ;
ComputeEntropyFuncName:        'ComputeEntropy' ;
GetBytesFromRangeFuncName:     'ByteRange' ;
GetIntegersFromRangeFuncName:  'IntegerRange' ;

UtilityFunctions:              GetTimestampFuncName | GetLocationFuncName | SeedRandomFuncName |
                               MemoryXORFuncName | MaxFuncName | MinFuncName |
                               ArrayAppendFuncName | ArrayReverseFuncName | ArrayShiftLeftFuncName |
                               ArrayShiftRightFuncName | ArrayPadLeftFuncName | ArrayPadRightFuncName |
                               GetSubarrayFuncName | GetConstantStringFuncName | GetConstantByteArrayFuncName |
                               ReverseBitsFuncName | ComputeEntropyFuncName | GetBytesFromRangeFuncName |
                               GetIntegersFromRangeFuncName ;