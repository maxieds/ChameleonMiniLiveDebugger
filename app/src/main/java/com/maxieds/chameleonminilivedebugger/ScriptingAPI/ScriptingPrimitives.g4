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

tokens {
     TYPE_INT,
     TYPE_BOOL,
     TYPE_BYTES,
     TYPE_STRING,
     TYPE_HEX_STRING,
     TYPE_RAW_STRING
}

type_literal returns [ScriptVariable var]:
     hs=HexString        { $var=ScriptVariable.parseHexString($hs.text); }
     |
     hb=HexByte          { $var=ScriptVariable.parseInt($hb.text); }
     |
     hl=HexLiteral       { if($hs.text.length() > 8) {
                                $var=ScriptVariable.parseHexString($hs.text);
                           }
                           else if($hs.text.length() < 2 || !$hs.text.substring(0, 2).equals("0x")) {
                                $var=ScriptVariable.parseInt("0x" + $hs.text);
                           }
                           else {
                                $var=ScriptVariable.parseInt($hs.text);
                           }
                         }
     |
     bl=BooleanLiteral   { $var=ScriptVariable.parseBoolean($bl.text); }
     |
     sl=StringLiteral    { $var=ScriptVariable.newInstance().set($sl.text); }
     |
     hs=HexStringLiteral { $var=ScriptVariable.parseHexString($hs.text); }
     |
     rs=RawStringLiteral { $var=ScriptVariable.parseRawString($rs.text); }
     ;

variable_reference returns [ScriptVariable var]:
     VariableStartSymbol vname=VariableName {
           $var=ChameleonScripting.getRunningInstance().lookupVariableByName($vname.text);
     }
     ;

operand_expression: variable_reference | type_literal ;

expression_eval_term: variable_reference | type_literal |
                      boolean_valued_operation | other_operation_result |
                      assignment_operation | typecast_expression ;

boolean_valued_operation returns [ScriptVariable  opResult]:
     lhs=operand_expression EqualsComparisonOperator rhs=operand_expression {
          $opResult=ScriptVariable.newInstance().set($lhs.getValueAsBoolean() == $lhs.getValueAsBoolean());
     }
     |
     lhs=operand_expression NotEqualsComparisonOperator rhs=operand_expression {
          $opResult=ScriptVariable.newInstance().set($lhs.getValueAsBoolean() != $lhs.getValueAsBoolean());
     }
     |
     lhs=operand_expression LogicalAndOperator rhs=operand_expression {
          $opResult=ScriptVariable.newInstance().set($lhs.getValueAsBoolean() && $lhs.getValueAsBoolean());
     }
     |
     lhs=operand_expression LogicalOrOperator rhs=operand_expression {
          $opResult=ScriptVariable.newInstance().set($lhs.getValueAsBoolean() || $lhs.getValueAsBoolean());
     }
     |
     LogicalNotOperator rhs=operand_expression {
          $opResult=ScriptVariable.newInstance().set(!$rhs.getValueAsBoolean());
     }
     ;

other_operation_result returns [ScriptVariable var]:
     lhs=operand_expression LeftShiftOperator rhs=operand_expression {
          $var=$lhs.binaryOperation(ScriptVariable.BinaryOperation.BINOP_SHIFT_LEFT, $rhs);
     }
     |
     lhs=operand_expression RightShiftOperator rhs=operand_expression {
          $var=$lhs.binaryOperation(ScriptVariable.BinaryOperation.BINOP_SHIFT_RIGHT, $rhs);
     }
     |
     lhs=operand_expression BitwiseAndOperator rhs=operand_expression {
          $var=$lhs.binaryOperation(ScriptVariable.BinaryOperation.BINOP_BITWISE_AND, $rhs);
     }
     |
     lhs=operand_expression BitwiseOrOperator rhs=operand_expression {
          $var=$lhs.binaryOperation(ScriptVariable.BinaryOperation.BINOP_BITWISE_OR, $rhs);
     }
     |
     lhs=operand_expression BitwiseXorOperator rhs=operand_expression {
          $var=$lhs.binaryOperation(ScriptVariable.BinaryOperation.BINOP_BITWISE_XOR, $rhs);
     }
     |
     BitwiseNotOperator rhs=operand_expression {
          $var=$rhs.unaryOperation(ScriptVariable.UnaryOperation.UOP_BITWISE_NOT);
     }
     |
     cond=operand_expression TernaryOperatorFirstSymbol vtrue=operand_expression
     TernaryOperatorSecondSymbol vfalse=operand_expression {
          boolean predicate = $cond.getAsBoolean();
          if(predicate) {
               $var=$vtrue;
          }
          else {
               $var=$vfalse;
          }
     }
     ;

assignment_operation returns [ScriptVariable var]:
     lhs=variable_reference DefEqualsOperator rhs=operand_expression {
          $lhs=$rhs;
          ChameleonScripting.getRunningInstance().setVariableByName($lhs.text);
          $var=$lhs;
     }
     |
     lhs=variable_reference PlusEqualsOperator rhs=operand_expression {
          $lhs=$lhs+$rhs;
          ChameleonScripting.getRunningInstance().setVariableByName($lhs.text);
          $var=$lhs;
     }
     ;

typecast_expression returns [ScriptVariable var]:
     TypeCastByte initVar=operand_expression {
          $var=$initVar.getAsByte();
     }
     |
     TypeCastShort initVar=operand_expression {
          $var=$initVar.getAsShort();
     }
     |
     TypeCastInt32 initVar=operand_expression {
          $var=$initVar.getAsInteger();
     }
     |
     TypeCastBoolean initVar=operand_expression {
          $var=$initVar.getAsBoolean();
     }
     |
     TypeCastString initVar=operand_expression {
          $var=$initVar.getAsString();
     }
     ;

WhiteSpaceText:        ( WhiteSpace | NewLineBreak )+ -> channel(HIDDEN) ;
WhiteSpace:            [ \t\r\t]* -> channel(HIDDEN) ;
NewLineBreak:          ( '\r''\n'? | '\n' ) -> channel(HIDDEN) ;
CStyleBlockComment:    '/*'.*?'*/' -> channel(HIDDEN) ;
CStyleLineComment:     '//'~[\n]* NewLineBreak -> channel(HIDDEN) ;
HashStyleLineComment:  '#'~[\n]* NewLineBreak -> channel(HIDDEN) ;
Commentary:            CStyleBlockComment | CStyleLineComment | HashStyleLineComment ;

HexDigit: [0-9a-fA-F] -> type(TYPE_INT) ;
HexString: (HexDigit)+ -> type(TYPE_BYTES) ;
HexByte: '0x' HexDigit HexDigit | '0x' HexDigit | HexDigit HexDigit -> type(TYPE_INT) ;
HexLiteral: HexByte | '0x'HexString | HexString ;
BooleanLiteral: 'true' | 'True' | 'TRUE' | 'false' | 'False' | 'FALSE' -> type(TYPE_BOOL) ;
AsciiChar: [\u0040-\u0046\u0050-\u0133\u0135-\u0176] ;
StringLiteral: '"'(AsciiChar)*'"' -> type(TYPE_STRING);
HexStringLiteral: 'h\'' HexString '\'' -> type(TYPE_HEX_STRING) ;
RawStringLiteral: 'r\'' (AsciiChar)* '\'' -> type(TYPE_RAW_STRING) ;

VariableNameStartChar: '_' | [a-zA-Z] ;
VariableNameMiddleChar: VariableNameStartChar | [0-9] ;
VariableStartSymbol: '$' ;
VariableName: VariableNameStartChar (VariableNameMiddleChar)* ;

EqualsComparisonOperator: '==' ;
NotEqualsComparisonOperator: '!=' ;
LogicalAndOperator: ('&&' | 'and') ;
LogicalOrOperator: ('||' | 'or') ;
LogicalNotOperator: ('!' | 'not') ;
RightShiftOperator: '>>' ;
LeftShiftOperator: '<<' ;
BitwiseAndOperator: '&' ;
BitwiseOrOperator: '|' ;
BitwiseXorOperator: '^' ;
BitwiseNotOperator: '~' ;
TernaryOperatorFirstSymbol: '?' ;
TernaryOperatorSecondSymbol: ':' ;
DefEqualsOperator: '=' | ':=' ;
PlusEqualsOperator: '+=' ;

TypeCastByte: '(Byte)' ;
TypeCastShort: '(Short)' ;
TypeCastInt32: '(Int32)' ;
TypeCastBoolean: '(Boolean)' ;
TypeCastString: '(String)' ;