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
import LexerMembers;

ErrorHandlingFunction: 'TODO' ;
LoggingFunction:       'TODO' ;
ScriptingAPIFunction:  ErrorHandlingFunction | LoggingFunction | 'TODO' ;

/* Script control flow functions: */
//exit() | exit(code);

/* Console printing and logging functions: */
PrintFuncName:         'Print' ;
PrintfFuncName:        'Printf' ;
PrintLog:              'PrintLog' ;
PrintfLog:             'PrintfLog' ;
//ToastMsg;

/* Type conversion and checking functions: */
AsInteger32FuncName:   'AsInt32' ;
EvalBooleanFuncName:   'EvaluateBoolean' ;
AsStringFuncName:
AsHexStringFuncName:
AsBinaryStringFuncName:
AsByteArrayFuncName:
GetLengthFuncName:
IsConstantFuncName:
IsFloatFuncName:
IsHexFuncName:
IsDecimalFuncName:
IsNumericFuncName:
IsStringFuncName:
IsArrayFuncName:
GetTypeFuncName:
GetArrayDataTypeFuncName:
// Sizeof (in bytes);
// Integer.parseInt(str, base);
// Integer.ToString(int, base);




/* Debugging and assertion commands */
// Assert, AssertEquals, AssertNotEquals;

/* Environmental variables: */
// GetEnv(name);

/* Chameleon command and command output post processing functions: */
CmdGetResponseFuncName:              '' ;
CmdGetResponseCodeFuncName:          '' ;
GetResponseDataFuncName:             '' ;
CmdIsSuccessFuncName:                '' ;
CmdIsErrorFuncName:                  '' ;
CmdContainsDataFuncName:             '' ;
CmdGetIntermedLogsFuncName:          '' ;
CmdSaveDeviceStateFuncName:          '' ;
CmdRestoreDeviceStateFuncName:       '' ;
CmdDonloadTagFuncName:               '' ;
CmdUploadTagFuncName:                '' ;
CmdDownloadLogsFuncName:             '' ;
// State = Cfg,Logmode,setting $,Readonly,Field,Threshold,Timeout,UID;

// TODO: Get functions for log types ...
// LogType; LogDataLength; LogTime; LogData; LogDirection;
// LogByteCode; LogDesc;

/* File I/O functions: */
// fscanf
// rwXML (useful for associative arrays, store into hashed array);
// rwCSV(delim); rwBinaryData; rwTextFile(from, to); rwJSON;
// Chdir/SetCWD
// FileExists; TouchFile; Mkdir? SetPerms;

/* String handling functions: */
// string search / find first or last of (with pattern matching / Java regex support)
// replace pattern
// contains
// strip; CompressWhitespaces;
// remove carriage returns: "\r\n -> \n" ;
// Substr(from); Substr(from,length);

/* APDU handling functions: */
// AsWrappedAPDU($v -- assumes have prepended CLA,INS); -> ByteArray
// AsWrappedAPDU($v, CLA,INS,P1,P2);
// ExtractDataFromWrapped
// ExtractDataFromNative
// SearchStatusCodes;
// SearchInsCodes;
// SearchCLACodes;

// Possibly: PrintNFCAnticol(UID,ATQA,SAK,ATS);

/* Crypto and hash related functionality: */
// get random bytes: ByteArray | Integer32
// different hashes: hashCode|base64enc/dec|MD5|SHA1|SHA256|SHA512 (java.security.MessageDigest);
// GetCRC16Bytes; AppendCRC16Bytes; CheckCRC16Bytes;
// Enc/Dec schemes: crypto1|DES|3DES|2KDEA|AES(128,192,256);
// AES mode options: CTR|ECB|CBC|OTF;
// GetCommonMFCKeys

/* Misc utility functions: */
GetTimestampFuncName:  'GetTimestamp' ;
GetLocationFuncName:   'GetLocation'  ;
// SeedRandom(blank for time | $v);
// ReverseEndian(Int32|ByteArray, optional packing size);
// GetRandomUUID
// MemoryXOR(ByteArray|Int32);
// Math.max/min;
// ArrayReverse; ArrayShiftL/R; ArrayPadR/L; ArrayConcat; GetSubarray;
// GetConstantString; GetConstantByteArray;
// GetLocation(Short|Long|VVerbose|specially %f formatted string);
// Byte.ReverseBits (|Int32|ByteArray);
// computeEntropy;
// BytesFromRange; IntsFromRange;
// Int32ToShort; Int32ToByte;


