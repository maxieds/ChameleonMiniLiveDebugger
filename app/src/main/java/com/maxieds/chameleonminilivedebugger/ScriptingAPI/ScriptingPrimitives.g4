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

@header {
     import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariable;
}

tokens {
     TYPE_INT,
     TYPE_BOOL,
     TYPE_BYTES,
     TYPE_STRING,
     TYPE_HEX_STRING,
     TYPE_RAW_STRING
}

type_literal returns [ScriptVariable svar]:
     dl=DecimalLiteral   { $svar=ScriptVariable.parseInt($dl.text); }
     |
     hs=HexString        { $svar=ScriptVariable.parseHexString($hs.text); }
     |
     hb=HexByte          { $svar=ScriptVariable.parseInt($hb.text); }
     |
     hl=HexLiteral       { if($hl.text.length() > 8) {
                                $svar=ScriptVariable.parseHexString($hl.text);
                           }
                           else if($hl.text.length() < 2 || !$hl.text.substring(0, 2).equals("0x")) {
                                $svar=ScriptVariable.parseInt("0x" + $hl.text);
                           }
                           else {
                                $svar=ScriptVariable.parseInt($hl.text);
                           }
                         }
     |
     bl=BooleanLiteral   { $svar=ScriptVariable.parseBoolean($bl.text); }
     |
     qsl=quoted_string_literal { $svar=$qsl.svar; }
     |
     OpenBrace bll=byte_literal_list CloseBrace { $svar=$bll.svar; }
     ;

quoted_string_literal returns [ScriptVariable svar]:
     qsl=QuotedStringLiteral {
          $svar=ScriptVariable.newInstance().set($qsl.text.substring(1, $qsl.text.length() - 1));
     }
     |
     qhsl=QuotedHexStringLiteral {
          $svar=ScriptVariable.newInstance().set($qhsl.text.substring(2, $qhsl.text.length() - 2));
     }
     |
     qrsl=QuotedHexStringLiteral {
          $svar=ScriptVariable.newInstance().set($qrsl.text.substring(2, $qrsl.text.length() - 2));
     }
     ;

byte_literal_list returns [ScriptVariable svar]:
     hb=HexByte {
          $svar=ScriptVariable.newInstance().set(new byte[] { (byte) Integer.parseInt($hb.text, 16) });
     }
     |
     hb=HexByte CommaSeparator bll=byte_literal_list {
          int bllLength = $bll.svar.getValueAsBytes().length;
          byte[] bytesArr = new byte[bllLength + 1];
          System.arraycopy($bll.svar.getValueAsBytes(), 0, bytesArr, 0, bllLength);
          bytesArr[bllLength] = (byte) Integer.parseInt($hb.text, 16);
          $svar=ScriptVariable.newInstance().set(bytesArr);
     }
     ;

variable_reference returns [ScriptVariable svar]:
     VariableStartSymbol vname=VariableName {
           $svar=ChameleonScripting.getRunningInstance().lookupVariableByName($vname.text);
     }
     ;

operand_expression returns [ScriptVariable svar]:
     vr=variable_reference {
          $svar=$vr.svar;
     }
     |
     tl=type_literal {
          $svar=$tl.svar;
     }
     ;

expression_eval_term returns [ScriptVariable svar]:
     vr=variable_reference {
          $svar=$vr.svar;
     }
     |
     tl=type_literal {
          $svar=$tl.svar;
     }
     |
     bvOp=boolean_valued_operation {
          $svar=$bvOp.opResult;
     }
     |
     otherOp=other_operation_result {
          $svar=$otherOp.svar;
     }
     |
     aOp=assignment_operation {
          $svar=$aOp.svar;
     }
     |
     tc=typecast_expression {
          $svar=$tc.svar;
     }
     |
     vgp=variable_get_property {
          $svar=$vgp.svar;
     }
     |
     arrIdxProp=extract_expression_from_array_index {
          $svar=$arrIdxProp.svar;
     }
     |
     arrSliceProp=extract_expression_from_array_slice {
          $svar=$arrSliceProp.svar;
     }
     ;

boolean_valued_operation returns [ScriptVariable  opResult]:
     lhs=operand_expression EqualsComparisonOperator rhs=operand_expression {
          $opResult=ScriptVariable.newInstance().set($lhs.svar.getValueAsBoolean() == $rhs.svar.getValueAsBoolean());
     }
     |
     lhs=operand_expression NotEqualsComparisonOperator rhs=operand_expression {
          $opResult=ScriptVariable.newInstance().set($lhs.svar.getValueAsBoolean() != $rhs.svar.getValueAsBoolean());
     }
     |
     lhs=operand_expression LogicalAndOperator rhs=operand_expression {
          $opResult=ScriptVariable.newInstance().set($lhs.svar.getValueAsBoolean() && $rhs.svar.getValueAsBoolean());
     }
     |
     lhs=operand_expression LogicalOrOperator rhs=operand_expression {
          $opResult=ScriptVariable.newInstance().set($lhs.svar.getValueAsBoolean() || $rhs.svar.getValueAsBoolean());
     }
     |
     LogicalNotOperator rhs=operand_expression {
          $opResult=ScriptVariable.newInstance().set(!$rhs.svar.getValueAsBoolean());
     }
     ;

other_operation_result returns [ScriptVariable svar]:
     lhs=operand_expression LeftShiftOperator rhs=operand_expression {
          $svar=$lhs.svar.binaryOperation(ScriptVariable.Operation.BINOP_SHIFT_LEFT, $rhs.svar);
     }
     |
     lhs=operand_expression RightShiftOperator rhs=operand_expression {
          $svar=$lhs.svar.binaryOperation(ScriptVariable.Operation.BINOP_SHIFT_RIGHT, $rhs.svar);
     }
     |
     lhs=operand_expression BitwiseAndOperator rhs=operand_expression {
          $svar=$lhs.svar.binaryOperation(ScriptVariable.Operation.BINOP_BITWISE_AND, $rhs.svar);
     }
     |
     lhs=operand_expression BitwiseOrOperator rhs=operand_expression {
          $svar=$lhs.svar.binaryOperation(ScriptVariable.Operation.BINOP_BITWISE_OR, $rhs.svar);
     }
     |
     lhs=operand_expression BitwiseXorOperator rhs=operand_expression {
          $svar=$lhs.svar.binaryOperation(ScriptVariable.Operation.BINOP_BITWISE_XOR, $rhs.svar);
     }
     |
     BitwiseNotOperator rhs=operand_expression {
          $svar=$rhs.svar.unaryOperation(ScriptVariable.Operation.UOP_BITWISE_NOT);
     }
     |
     cond=operand_expression TernaryOperatorFirstSymbol vtrue=operand_expression
     TernaryOperatorSecondSymbol vfalse=operand_expression {
          boolean predicate = $cond.svar.getValueAsBoolean();
          if(predicate) {
               $svar=$vtrue.svar;
          }
          else {
               $svar=$vfalse.svar;
          }
     }
     ;

assignment_operation returns [ScriptVariable svar]:
     lhs=variable_reference DefEqualsOperator rhs=operand_expression {
          $svar=$rhs.svar;
          ChameleonScripting.getRunningInstance().setVariableByName($lhs.svar);
     }
     |
     lhs=variable_reference PlusEqualsOperator rhs=operand_expression {
          $svar=$lhs.svar.binaryOperation(ScriptVariable.Operation.BINOP_PLUS, $rhs.svar);
          ChameleonScripting.getRunningInstance().setVariableByName($lhs.svar);
     }
     ;

typecast_expression returns [ScriptVariable svar]:
     TypeCastByte initVar=operand_expression {
          $svar=ScriptVariable.newInstance().set(new byte[] { $initVar.svar.getValueAsByte() });
     }
     |
     TypeCastShort initVar=operand_expression {
          $svar=ScriptVariable.newInstance().set((int) $initVar.svar.getValueAsShort());
     }
     |
     TypeCastInt32 initVar=operand_expression {
          $svar=ScriptVariable.newInstance().set((int) $initVar.svar.getValueAsInt());
     }
     |
     TypeCastBoolean initVar=operand_expression {
          $svar=ScriptVariable.newInstance().set((boolean) $initVar.svar.getValueAsBoolean());
     }
     |
     TypeCastString initVar=operand_expression {
          $svar=ScriptVariable.newInstance().set($initVar.svar.getValueAsString());
     }
     |
     TypeCastBytes initVar=operand_expression {
          $svar=ScriptVariable.newInstance().set($initVar.svar.getValueAsBytes());
     }
     ;

variable_get_property returns [ScriptVariable svar]:
     var=variable_reference HashedIndexAccessor propName=PropertyName {
          $svar=$var.svar.getValueAt($propName.text);
     }
     ;

extract_expression_from_array_index returns [ScriptVariable svar]:
     varRef=variable_reference ArrayIndexOpenBracket oexpr=operand_expression ArrayIndexCloseBracket {
          $svar=$varRef.svar.getValueAt($oexpr.svar.getValueAsInt());
     }
     ;

extract_expression_from_array_slice returns [ScriptVariable svar]:
     varRef=variable_reference ArrayIndexOpenBracket oexprStartIdx=operand_expression
     ArraySliceSeparator oexprLengthIdx=operand_expression ArrayIndexCloseBracket {
          $svar=$varRef.svar.getSubArray($oexprStartIdx.svar.getValueAsInt(), $oexprLengthIdx.svar.getValueAsInt());
     }
     |
     varRef=variable_reference ArrayIndexOpenBracket oexprStartIdx=operand_expression
     ArraySliceSeparator ArrayIndexCloseBracket {
          $svar=$varRef.svar.getSubArray($oexprStartIdx.svar.getValueAsInt());
     }
     |
     varRef=variable_reference ArrayIndexOpenBracket
     ArraySliceSeparator oexprLengthIdx=operand_expression ArrayIndexCloseBracket {
          $svar=$varRef.svar.getSubArray(0, $oexprLengthIdx.svar.getValueAsInt());
     }
     ;

assignment_by_array_slice returns [ScriptVariable svar]:
     varRef=variable_reference ArrayIndexOpenBracket oexprStartIdx=operand_expression
     ArraySliceSeparator oexprLengthIdx=operand_expression ArrayIndexCloseBracket
     DefEqualsOperator rhsExpr=operand_expression {
          $varRef.svar.insertSubArray($oexprStartIdx.svar.getValueAsInt(), $oexprLengthIdx.svar.getValueAsInt(), $rhsExpr.svar);
          $svar=$varRef.svar;
          ChameleonScripting.getRunningInstance().setVariableByName($varRef.svar);
     }
     |
     varRef=variable_reference ArrayIndexOpenBracket oexprStartIdx=operand_expression
     ArraySliceSeparator ArrayIndexCloseBracket
     DefEqualsOperator rhsExpr=operand_expression {
          $varRef.svar.insertSubArray($oexprStartIdx.svar.getValueAsInt(), $rhsExpr.svar);
          $svar=$varRef.svar;
          ChameleonScripting.getRunningInstance().setVariableByName($varRef.svar);
     }
     |
     varRef=variable_reference ArrayIndexOpenBracket
     ArraySliceSeparator oexprLengthIdx=operand_expression ArrayIndexCloseBracket
     DefEqualsOperator rhsExpr=operand_expression {
          $varRef.svar.insertSubArray(0, $oexprLengthIdx.svar.getValueAsInt(), $rhsExpr.svar);
          $svar=$varRef.svar;
          ChameleonScripting.getRunningInstance().setVariableByName($varRef.svar);
     }
     ;

WhiteSpace:            [ \t\r\u000C][ \t\r\n\u000C]* -> channel(HIDDEN) ;
NewLineBreak:          '\n' -> channel(HIDDEN) ;
CStyleBlockComment:    '/*'.*?'*/' -> channel(HIDDEN) ;
CStyleLineComment:     '//'~[\n]* NewLineBreak -> channel(HIDDEN) ;
HashStyleLineComment:  '#'~[\n]* NewLineBreak -> channel(HIDDEN) ;
Commentary:            CStyleBlockComment | CStyleLineComment | HashStyleLineComment ;

DecimalDigit: [0-9] ;
DecimalLiteral: MinusSign (DecimalDigit)+ | (DecimalDigit)+ ;
HexDigit: [0-9a-fA-F] ;
HexString: (HexDigit)+ ;
HexByte: '0x' HexDigit HexDigit | '0x' HexDigit | HexDigit HexDigit ;
HexLiteral: HexByte | '0x'HexString | HexString ;
BooleanLiteral: 'true' | 'True' | 'TRUE' | 'false' | 'False' | 'FALSE' ;
AsciiChar: [\u0040-\u0046\u0050-\u0133\u0135-\u0176] ;
QuotedStringLiteral: '"' (~["\\] | '\\' .)* '"' ;
QuotedHexStringLiteral: 'h\'' (~["\\] | '\\' .)* '\'' ;
QuotedRawStringLiteral: 'r\'' (~["\\] | '\\' .)* '\'' ;

MinusSign: '-' ;
CommaSeparator: ',' ;
OpenParens: '(' ;
ClosedParens: ')' ;
ColonSeparator: ':' ;
OpenBrace: '{' ;
CloseBrace: '}' ;

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
TernaryOperatorSecondSymbol: ColonSeparator ;
DefEqualsOperator: '=' | ':=' ;
PlusEqualsOperator: '+=' ;
UnaryIncrementOperator: '++' ; // TODO
UnaryDecrementOperator: '--' ; // TODO

TypeCastByte: '(Byte)' ;
TypeCastBytes: '(Bytes)' ;
TypeCastShort: '(Short)' ;
TypeCastInt32: '(Int32)' ;
TypeCastBoolean: '(Boolean)' ;
TypeCastString: '(String)' ;

/* TODO: No current support for hash indexed assignments:
 *       $v->propName = $q // does NOT work!
 */
HashedIndexAccessor: '->' ;
PropertyName: VariableName ;

ArrayIndexOpenBracket: '[' ;
ArrayIndexCloseBracket: ']' ;
ArraySliceSeparator: ColonSeparator ;