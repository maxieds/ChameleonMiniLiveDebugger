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

parser grammar ChameleonScriptParser;

@header {
     import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariable;
}
@rulecatch {
    catch(ScriptingExecptions.ChameleonScriptingException rtEx) {
        throw rtEx;
    }
}

/* Start rule for the main grammar: */
file_contents: (script_line)+ EOF | EOF ;

script_line: label_statement |
             assignment_operation | assignment_by_array_slice |
             scripting_api_function_result |
             exec_chameleon_command |
             conditional_block | while_loop ;

script_line_block: (script_line)* ;

while_loop: While OpenParens operand_expression ClosedParens
            OpenBrace script_line_block ClosedBrace ;

if_block:   IfCond OpenParens operand_expression ClosedParens
            OpenBrace script_line_block ClosedBrace ;
elif_block: ElifCond OpenParens operand_expression ClosedParens
            OpenBrace script_line_block ClosedBrace ;
else_block: ElseCond OpenParens operand_expression ClosedParens
            OpenBrace script_line_block ClosedBrace ;

conditional_block: if_block (elif_block)+ else_block |
                   if_block (elif_block)* ;

variable_reference_v1 returns [ScriptVariable svar]:
     VariableStartSymbol vname=VariableName {
           $svar=ChameleonScripting.getRunningInstance().lookupVariableByName($vname.text);
     }
     ;

variable_reference_v2 returns [ScriptVariable svar]:
     var=variable_reference_v1 HashedIndexAccessor propName=VariableName {
          $svar=$var.svar.getValueAt($propName.text);
     }
     ;

variable_reference returns [ScriptVariable svar]:
     vrtype2=variable_reference_v2 {
          $svar=$vrtype2.svar;
     }
     |
     vrtype1=variable_reference_v1 {
          $svar=$vrtype1.svar;
     }
     ;

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

operand_expression_v1 returns [ScriptVariable svar]:
     vr=variable_reference {
          $svar=$vr.svar;
     }
     |
     tl=type_literal {
          $svar=$tl.svar;
     }
     ;

typecast_expression returns [ScriptVariable svar]:
     TypeCastByte initVar=operand_expression_v1 {
          $svar=ScriptVariable.newInstance().set(new byte[] { $initVar.svar.getValueAsByte() });
     }
     |
     TypeCastShort initVar=operand_expression_v1 {
          $svar=ScriptVariable.newInstance().set((int) $initVar.svar.getValueAsShort());
     }
     |
     TypeCastInt32 initVar=operand_expression_v1 {
          $svar=ScriptVariable.newInstance().set((int) $initVar.svar.getValueAsInt());
     }
     |
     TypeCastBoolean initVar=operand_expression_v1 {
          $svar=ScriptVariable.newInstance().set((boolean) $initVar.svar.getValueAsBoolean());
     }
     |
     TypeCastString initVar=operand_expression_v1 {
          $svar=ScriptVariable.newInstance().set($initVar.svar.getValueAsString());
     }
     |
     TypeCastBytes initVar=operand_expression_v1 {
          $svar=ScriptVariable.newInstance().set($initVar.svar.getValueAsBytes());
     }
     ;

operand_expression_v2 returns [ScriptVariable svar]:
     oe=operand_expression_v1 {
          $svar=$oe.svar;
     }
     |
     tce=typecast_expression {
          $svar=$tce.svar;
     }
     ;

other_operation_result returns [ScriptVariable svar]:
     lhs=operand_expression_v2 LeftShiftOperator rhs=operand_expression_v2 {
          $svar=$lhs.svar.binaryOperation(ScriptVariable.Operation.BINOP_SHIFT_LEFT, $rhs.svar);
     }
     |
     lhs=operand_expression_v2 RightShiftOperator rhs=operand_expression_v2 {
          $svar=$lhs.svar.binaryOperation(ScriptVariable.Operation.BINOP_SHIFT_RIGHT, $rhs.svar);
     }
     |
     lhs=operand_expression_v2 BitwiseAndOperator rhs=operand_expression_v2 {
          $svar=$lhs.svar.binaryOperation(ScriptVariable.Operation.BINOP_BITWISE_AND, $rhs.svar);
     }
     |
     lhs=operand_expression_v2 BitwiseOrOperator rhs=operand_expression_v2 {
          $svar=$lhs.svar.binaryOperation(ScriptVariable.Operation.BINOP_BITWISE_OR, $rhs.svar);
     }
     |
     lhs=operand_expression_v2 BitwiseXorOperator rhs=operand_expression_v2 {
          $svar=$lhs.svar.binaryOperation(ScriptVariable.Operation.BINOP_BITWISE_XOR, $rhs.svar);
     }
     |
     lhs=operand_expression_v2 ArithmeticPlusOperator rhs=operand_expression_v2 {
          $svar=$lhs.svar.binaryOperation(ScriptVariable.Operation.BINOP_PLUS, $rhs.svar);
     }
     |
     BitwiseNotOperator rhs=operand_expression_v2 {
          $svar=$rhs.svar.unaryOperation(ScriptVariable.Operation.UOP_BITWISE_NOT);
     }
     ;

operand_expression_v3 returns [ScriptVariable svar]:
     oe=operand_expression_v2 {
          $svar=$oe.svar;
     }
     |
     oor=other_operation_result {
          $svar=$oor.svar;
     }
     ;

boolean_valued_operation returns [ScriptVariable  opResult]:
     lhs=operand_expression_v3 EqualsComparisonOperator rhs=operand_expression_v3 {
          $opResult=ScriptVariable.newInstance().set($lhs.svar.getValueAsBoolean() == $rhs.svar.getValueAsBoolean());
     }
     |
     lhs=operand_expression_v3 NotEqualsComparisonOperator rhs=operand_expression_v3 {
          $opResult=ScriptVariable.newInstance().set($lhs.svar.getValueAsBoolean() != $rhs.svar.getValueAsBoolean());
     }
     |
     lhs=operand_expression_v3 LogicalAndOperator rhs=operand_expression_v3 {
          $opResult=ScriptVariable.newInstance().set($lhs.svar.getValueAsBoolean() && $rhs.svar.getValueAsBoolean());
     }
     |
     lhs=operand_expression_v3 LogicalOrOperator rhs=operand_expression_v3 {
          $opResult=ScriptVariable.newInstance().set($lhs.svar.getValueAsBoolean() || $rhs.svar.getValueAsBoolean());
     }
     |
     LogicalNotOperator rhs=operand_expression_v3 {
          $opResult=ScriptVariable.newInstance().set(!$rhs.svar.getValueAsBoolean());
     }
     ;

operand_expression_v4 returns [ScriptVariable svar]:
     oe=operand_expression_v3 {
          $svar=$oe.svar;
     }
     |
     bvo=boolean_valued_operation {
          $svar=$bvo.opResult;
     }
     ;

extract_expression_from_array_index returns [ScriptVariable svar]:
     varRef=variable_reference ArrayIndexOpenBracket oexpr=operand_expression_v4 ArrayIndexCloseBracket {
          $svar=$varRef.svar.getValueAt($oexpr.svar.getValueAsInt());
     }
     ;

operand_expression_v5 returns [ScriptVariable svar]:
     oe=operand_expression_v4 {
          $svar=$oe.svar;
     }
     |
     aiExpr=extract_expression_from_array_index {
          $svar=$aiExpr.svar;
     }
     ;

extract_expression_from_array_slice returns [ScriptVariable svar]:
     varRef=variable_reference ArrayIndexOpenBracket oexprStartIdx=operand_expression_v5
     ColonSeparator oexprLengthIdx=operand_expression_v5 ArrayIndexCloseBracket {
          $svar=$varRef.svar.getSubArray($oexprStartIdx.svar.getValueAsInt(), $oexprLengthIdx.svar.getValueAsInt());
     }
     |
     varRef=variable_reference ArrayIndexOpenBracket oexprStartIdx=operand_expression_v5
     ColonSeparator ArrayIndexCloseBracket {
          $svar=$varRef.svar.getSubArray($oexprStartIdx.svar.getValueAsInt());
     }
     |
     varRef=variable_reference ArrayIndexOpenBracket
     ColonSeparator oexprLengthIdx=operand_expression_v5 ArrayIndexCloseBracket {
          $svar=$varRef.svar.getSubArray(0, $oexprLengthIdx.svar.getValueAsInt());
     }
     ;

operand_expression_v6 returns [ScriptVariable svar]:
     oe=operand_expression_v5 {
          $svar=$oe.svar;
     }
     |
     asExpr=extract_expression_from_array_slice {
          $svar=$asExpr.svar;
     }
     ;

exec_chameleon_command returns [ScriptVariable svar]:
     ExecCommandStartSymbol oe=operand_expression_v6 ClosedParens {
          $svar=ChameleonIOHandler.executeChameleonCommandForResult($oe.svar.getValueAsString());
     }
     ;


scripting_api_function_result returns [ScriptVariable svar]:
     funcName=ScriptingAPIFunctionName
     funcArgs=function_args_list ClosedParens {
          $svar=ScriptingFunctions.callFunction($funcName.text, $funcArgs.varsList);
     }
     |
     funcName=ScriptingAPIFunctionName ClosedParens {
          $svar=ScriptingFunctions.callFunction($funcName.text, new ArrayList<ScriptVariable>());
     }
     ;

operand_expression_v7 returns [ScriptVariable svar]:
     oe=operand_expression_v6 {
          $svar=$oe.svar;
     }
     |
     ecc=exec_chameleon_command {
          $svar=$ecc.svar;
     }
     |
     funcResult=scripting_api_function_result {
          $svar=$funcResult.svar;
     }
     ;

assignment_operation returns [ScriptVariable svar]:
     lhs=variable_reference DefEqualsOperator rhs=operand_expression_v7 {
          $svar=$rhs.svar;
          ChameleonScripting.getRunningInstance().setVariableByName($lhs.svar);
     }
     |
     lhs=variable_reference PlusEqualsOperator rhs=operand_expression_v7 {
          $svar=$lhs.svar.binaryOperation(ScriptVariable.Operation.BINOP_PLUS, $rhs.svar);
          ChameleonScripting.getRunningInstance().setVariableByName($lhs.svar);
     }
     ;

operand_expression_v8 returns [ScriptVariable svar]:
     oe=operand_expression_v7 {
          $svar=$oe.svar;
     }
     |
     aop=assignment_operation {
          $svar=$aop.svar;
     }
     ;

ternary_operator_expression returns [ScriptVariable svar]:
     cond=operand_expression_v8 TernaryOperatorFirstSymbol vtrue=operand_expression_v8
     ColonSeparator vfalse=operand_expression_v8 {
          boolean predicate = $cond.svar.getValueAsBoolean();
          if(predicate) {
               $svar=$vtrue.svar;
          }
          else {
               $svar=$vfalse.svar;
          }
     }
     ;

operand_expression_v9 returns [ScriptVariable svar]:
     oe=operand_expression_v8 {
          $svar=$oe.svar;
     }
     |
     toe=ternary_operator_expression {
          $svar=$toe.svar;
     }
     ;

operand_expression returns [ScriptVariable svar]:
     oe=operand_expression_v9 {
          $svar=$oe.svar;
     }
     ;

assignment_by_array_slice returns [ScriptVariable svar]:
     varRef=variable_reference ArrayIndexOpenBracket oexprStartIdx=operand_expression
     ColonSeparator oexprLengthIdx=operand_expression ArrayIndexCloseBracket
     DefEqualsOperator rhsExpr=operand_expression {
          $varRef.svar.insertSubArray($oexprStartIdx.svar.getValueAsInt(), $oexprLengthIdx.svar.getValueAsInt(), $rhsExpr.svar);
          $svar=$varRef.svar;
          ChameleonScripting.getRunningInstance().setVariableByName($varRef.svar);
     }
     |
     varRef=variable_reference ArrayIndexOpenBracket oexprStartIdx=operand_expression
     ColonSeparator ArrayIndexCloseBracket
     DefEqualsOperator rhsExpr=operand_expression {
          $varRef.svar.insertSubArray($oexprStartIdx.svar.getValueAsInt(), $rhsExpr.svar);
          $svar=$varRef.svar;
          ChameleonScripting.getRunningInstance().setVariableByName($varRef.svar);
     }
     |
     varRef=variable_reference ArrayIndexOpenBracket
     ColonSeparator oexprLengthIdx=operand_expression ArrayIndexCloseBracket
     DefEqualsOperator rhsExpr=operand_expression {
          $varRef.svar.insertSubArray(0, $oexprLengthIdx.svar.getValueAsInt(), $rhsExpr.svar);
          $svar=$varRef.svar;
          ChameleonScripting.getRunningInstance().setVariableByName($varRef.svar);
     }
     ;

function_args_list returns [List<ScriptVariable> varsList]:
     var=operand_expression CommaSeparator argsList=function_args_list {
          $argsList.varsList.add($var.svar);
          $varsList=$argsList.varsList;
     }
     |
     var=operand_expression {
          $varsList=new ArrayList<ScriptVariable>();
          $varsList.add($var.svar);
     }
     ;

label_statement: lblNameWithSep=LabelText {
          String labelName = $lblNameWithSep.text.substring(0, $lblNameWithSep.text.length() - 1);
          // TODO: no goto for now, so see if have encountered a breakpoint ...
     }
     ;

/*
 * TODO: Add the ability to create lists of arbitrary var/literal/exprs
 *       using the syntax: [ $v0, 0x00, $v2, ..., 0x04 ]
 */

 /* TODO: No current support for hash indexed assignments:
  *       $v->propName = $q // does NOT work!
  */
