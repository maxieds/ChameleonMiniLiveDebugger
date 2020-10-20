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

grammar ScriptingPrimitives;
import LexerMembers;

tokens {
     TYPE_INT,
     TYPE_BOOL,
     TYPE_BYTES,
     TYPE_STRING,
     CONTEXT_EXPR_EVAL,
     CONTEXT_FUNCTION_DEF,
     CONTEXT_VARIABLE_DEF,
     CONTEXT_ASSIGNMENT_LINE,
     CONTEXT_RUNCMD_LINE,
     CONTEXT_CONDITIONAL_STMT,
     CONTEXT_CONDITIONAL_BODY,
     CONTEXT_LOOP_CONDITION,
     CONTEXT_LOOP_BODY,
     HAVE_FATAL_ERROR,
     HAVE_WARNING
}

WhiteSpaceText:        ( WhiteSpace | NewLineBreak )+ -> channel(HIDDEN) ;
WhiteSpace:            [ \t]* -> channel(HIDDEN) ;
NewLineBreak:          ( '\r''\n'? | '\n' ) -> channel(HIDDEN) ;
Commentary:            CStyleBlockComment | CStyleLineComment ;
CStyleBlockComment:    '/*'.*?'*/' -> channel(HIDDEN) ;
CStyleLineComment:     '//'~[\r\n]* -> channel(HIDDEN) ;

/* GCC-like constants as macros for printing status: */
ScriptLineMacro:     '__LINE__'     { $exprEval = _scriptLineNumber = ctx.start.getLine(); _scriptLineNumber; } ;
ScriptLineCharMacro: '__LINECHAR__' { _scriptLineCharPos = _input.index(); $exprEval = _scriptLineCharPos; } ;
ScriptFileMacro:     '__FILE__'     { $exprEval = _scriptFileName; } ;
ScriptFunctionMacro: '__FUNC__'     { $exprEval = _scriptFunc; } ;
ScriptErrorMacro:    '__ERROR__'    { $exprEval = _scriptErrorCode; } ;
ScriptErrorMsgMacro: '__ERRORMSG__' { $exprEval = _scriptErrorMsg; } ;
MacroConstant: ScriptLineMacro | ScriptLineCharMacro | ScriptFileMacro |
               ScriptErrorMacro | ScriptErrorMsgMacro ;

HexDigit: [0-9a-fA-F] -> type(TYPE_INT);
HexString: (HexDigit)+ -> type(TYPE_BYTES);
HexByte: '0x' HexDigit HexDigit | '0x' HexDigit | HexDigit HexDigit -> type(TYPE_INT);
HexLiteral: HexByte | '0x'HexString | HexString ;
BooleanLiteral: 'True' | 'False' -> type(TYPE_BOOL) ;
AsciiChar: [\u0040-\u0046\u0050-\u0133\u0135-\u0176] ;
StringLiteral: '"'(AsciiChar)*'"' -> type(TYPE_STRING);
TypeLiteral: HexString | HexByte | HexLiteral | BooleanLiteral | StringLiteral ;

VariableNameStartChar: '_' | [a-zA-Z] ;
VariableNameMiddleChar: VariableNameStartChar | [0-9] ;
VariableStartSymbol: '$' ;
VariableName: VariableNameStartChar (VariableNameMiddleChar)* ;
VariableDeclaration: 'define var' VariableStartSymbol VariableName NewLineBreak;
VariableReference: VariableStartSymbol VariableName {}; // TODO
VariablePropertyAccessor: '->';
VariablePropertyGetName: VariableReference VariablePropertyAccessor 'getHashName()'
                         { _input._variableReference.getName(); };
VariablePropertyGetType: VariableReference VariablePropertyAccessor 'getType()'
                         { _input._variableReference.getTypeName(); };
VariablePropertyToString: VariableReference VariablePropertyAccessor 'asString()'
                          { _input._variableReference.getValueAsString(); };
VariablePropertyToInteger: VariableReference VariablePropertyAccessor 'asInteger()'
                           { _input._variableReference.getValueAsInteger(); };
VariablePropertyToBoolean: VariableReference VariablePropertyAccessor 'asBoolean()'
                           { _input._variableReference.getValueAsBoolean(); };
VariablePropertyToBytes: VariableReference VariablePropertyAccessor 'asBytes()'
                         { _input._variableReference.getValueAsBytes(); };
VariablePropertyExists: VariableReference VariablePropertyAccessor 'exists()'
                        { TokenExists(_input._variableReference.getName()); };
VariablePropertyEvaluate: VariableReference VariablePropertyAccessor 'eval()' ;
VariableProperty: VariablePropertyGetName | VariablePropertyGetType | VariablePropertyToString |
                      VariablePropertyToInteger | VariablePropertyToBoolean |
                      VariablePropertyToBytes | VariablePropertyExists | VariablePropertyEvaluate ;

ExpressionEvalTerm: VariableProperty | Operator |
                    TypeLiteral | MacroConstant -> mode(CONTEXT_EXPR_EVAL);

EqualsComparisonOperator: ExpressionEvalTerm WhiteSpace '==' WhiteSpace ExpressionEvalTerm ;
NotEqualsComparisonOperator: ExpressionEvalTerm WhiteSpace '!=' WhiteSpace ExpressionEvalTerm ;
IdenticallyEqualComparisonOperator: ExpressionEvalTerm WhiteSpace '===' WhiteSpace ExpressionEvalTerm ;
LogicalAndOperator: ExpressionEvalTerm WhiteSpace ('&&' | 'and') WhiteSpace ExpressionEvalTerm ;
LogicalOrOperator: ExpressionEvalTerm WhiteSpace  ('||' | 'or') WhiteSpace ExpressionEvalTerm ;
LogicalNotOperator: ('!' | 'not') WhiteSpace ExpressionEvalTerm WhiteSpace ;
BitwiseAndOperator: ExpressionEvalTerm WhiteSpace '&' WhiteSpace ExpressionEvalTerm ;
BitwiseOrOperator: ExpressionEvalTerm WhiteSpace '|' WhiteSpace ExpressionEvalTerm ;
BitwiseXorOperator: ExpressionEvalTerm WhiteSpace '^' WhiteSpace ExpressionEvalTerm ;
BitwiseNotOperator: ExpressionEvalTerm WhiteSpace '~' WhiteSpace ExpressionEvalTerm ;
BitwiseRightShiftOperator: ExpressionEvalTerm WhiteSpace '>>' WhiteSpace ExpressionEvalTerm ;
BitwiseLeftShiftOperator: ExpressionEvalTerm WhiteSpace '<<' WhiteSpace ExpressionEvalTerm ;
DefEqualsOperator: ExpressionEvalTerm WhiteSpace ':=' WhiteSpace ExpressionEvalTerm ;
ArithmeticPlusEqualsOperator: ExpressionEvalTerm WhiteSpace '+=' WhiteSpace ExpressionEvalTerm ;
ArithmeticMinusEqualsOperator: ExpressionEvalTerm WhiteSpace '-=' WhiteSpace ExpressionEvalTerm ;
ArithmeticAstEqualsOperator: ExpressionEvalTerm WhiteSpace '*=' WhiteSpace ExpressionEvalTerm ;
AssignmentOperator: DefEqualsOperator | ArithmeticPlusEqualsOperator |
                    ArithmeticMinusEqualsOperator | ArithmeticAstEqualsOperator ;
TernaryOperator:    ExpressionEvalTerm (WhiteSpace)* '?' (WhiteSpace)* ExpressionEvalTerm
                    (WhiteSpace)* ':' (WhiteSpace)* ExpressionEvalTerm (WhiteSpace)* ;

UnaryOperation:  LogicalNotOperator | BitwiseNotOperator;
BinaryOperation: AssignmentOperator | EqualsComparisonOperator | NotEqualsComparisonOperator |
                 IdenticallyEqualComparisonOperator | LogicalAndOperator | LogicalOrOperator |
                 BitwiseAndOperator | BitwiseOrOperator | BitwiseXorOperator |
                 ArithmeticPlusEqualsOperator | ArithmeticMinusEqualsOperator |
                 ArithmeticAstEqualsOperator ;
Operator:        UnaryOperation | BinaryOperation | TernaryOperator ;