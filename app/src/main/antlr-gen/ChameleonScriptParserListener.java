// Generated from app/src/main/java/com/maxieds/chameleonminilivedebugger/ScriptingAPI/ChameleonScriptParser.g4 by ANTLR 4.13.2
package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

     import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariable;
     import com.maxieds.chameleonminilivedebugger.AndroidLogger;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ChameleonScriptParser}.
 */
public interface ChameleonScriptParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#file_contents}.
	 * @param ctx the parse tree
	 */
	void enterFile_contents(ChameleonScriptParser.File_contentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#file_contents}.
	 * @param ctx the parse tree
	 */
	void exitFile_contents(ChameleonScriptParser.File_contentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#script_line}.
	 * @param ctx the parse tree
	 */
	void enterScript_line(ChameleonScriptParser.Script_lineContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#script_line}.
	 * @param ctx the parse tree
	 */
	void exitScript_line(ChameleonScriptParser.Script_lineContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#script_line_block}.
	 * @param ctx the parse tree
	 */
	void enterScript_line_block(ChameleonScriptParser.Script_line_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#script_line_block}.
	 * @param ctx the parse tree
	 */
	void exitScript_line_block(ChameleonScriptParser.Script_line_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#while_loop}.
	 * @param ctx the parse tree
	 */
	void enterWhile_loop(ChameleonScriptParser.While_loopContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#while_loop}.
	 * @param ctx the parse tree
	 */
	void exitWhile_loop(ChameleonScriptParser.While_loopContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#if_block}.
	 * @param ctx the parse tree
	 */
	void enterIf_block(ChameleonScriptParser.If_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#if_block}.
	 * @param ctx the parse tree
	 */
	void exitIf_block(ChameleonScriptParser.If_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#ifelse_block}.
	 * @param ctx the parse tree
	 */
	void enterIfelse_block(ChameleonScriptParser.Ifelse_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#ifelse_block}.
	 * @param ctx the parse tree
	 */
	void exitIfelse_block(ChameleonScriptParser.Ifelse_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#variable_reference_v1}.
	 * @param ctx the parse tree
	 */
	void enterVariable_reference_v1(ChameleonScriptParser.Variable_reference_v1Context ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#variable_reference_v1}.
	 * @param ctx the parse tree
	 */
	void exitVariable_reference_v1(ChameleonScriptParser.Variable_reference_v1Context ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#variable_reference_v2}.
	 * @param ctx the parse tree
	 */
	void enterVariable_reference_v2(ChameleonScriptParser.Variable_reference_v2Context ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#variable_reference_v2}.
	 * @param ctx the parse tree
	 */
	void exitVariable_reference_v2(ChameleonScriptParser.Variable_reference_v2Context ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#variable_reference}.
	 * @param ctx the parse tree
	 */
	void enterVariable_reference(ChameleonScriptParser.Variable_referenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#variable_reference}.
	 * @param ctx the parse tree
	 */
	void exitVariable_reference(ChameleonScriptParser.Variable_referenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#type_literal}.
	 * @param ctx the parse tree
	 */
	void enterType_literal(ChameleonScriptParser.Type_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#type_literal}.
	 * @param ctx the parse tree
	 */
	void exitType_literal(ChameleonScriptParser.Type_literalContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#quoted_string_literal}.
	 * @param ctx the parse tree
	 */
	void enterQuoted_string_literal(ChameleonScriptParser.Quoted_string_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#quoted_string_literal}.
	 * @param ctx the parse tree
	 */
	void exitQuoted_string_literal(ChameleonScriptParser.Quoted_string_literalContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#byte_literal_list}.
	 * @param ctx the parse tree
	 */
	void enterByte_literal_list(ChameleonScriptParser.Byte_literal_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#byte_literal_list}.
	 * @param ctx the parse tree
	 */
	void exitByte_literal_list(ChameleonScriptParser.Byte_literal_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#operand_expression_v1}.
	 * @param ctx the parse tree
	 */
	void enterOperand_expression_v1(ChameleonScriptParser.Operand_expression_v1Context ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v1}.
	 * @param ctx the parse tree
	 */
	void exitOperand_expression_v1(ChameleonScriptParser.Operand_expression_v1Context ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#typecast_expression}.
	 * @param ctx the parse tree
	 */
	void enterTypecast_expression(ChameleonScriptParser.Typecast_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#typecast_expression}.
	 * @param ctx the parse tree
	 */
	void exitTypecast_expression(ChameleonScriptParser.Typecast_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#operand_expression_v2}.
	 * @param ctx the parse tree
	 */
	void enterOperand_expression_v2(ChameleonScriptParser.Operand_expression_v2Context ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v2}.
	 * @param ctx the parse tree
	 */
	void exitOperand_expression_v2(ChameleonScriptParser.Operand_expression_v2Context ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#other_operation_result}.
	 * @param ctx the parse tree
	 */
	void enterOther_operation_result(ChameleonScriptParser.Other_operation_resultContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#other_operation_result}.
	 * @param ctx the parse tree
	 */
	void exitOther_operation_result(ChameleonScriptParser.Other_operation_resultContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#operand_expression_v3}.
	 * @param ctx the parse tree
	 */
	void enterOperand_expression_v3(ChameleonScriptParser.Operand_expression_v3Context ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v3}.
	 * @param ctx the parse tree
	 */
	void exitOperand_expression_v3(ChameleonScriptParser.Operand_expression_v3Context ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#boolean_valued_operation}.
	 * @param ctx the parse tree
	 */
	void enterBoolean_valued_operation(ChameleonScriptParser.Boolean_valued_operationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#boolean_valued_operation}.
	 * @param ctx the parse tree
	 */
	void exitBoolean_valued_operation(ChameleonScriptParser.Boolean_valued_operationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#operand_expression_v4}.
	 * @param ctx the parse tree
	 */
	void enterOperand_expression_v4(ChameleonScriptParser.Operand_expression_v4Context ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v4}.
	 * @param ctx the parse tree
	 */
	void exitOperand_expression_v4(ChameleonScriptParser.Operand_expression_v4Context ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#extract_expression_from_array_index}.
	 * @param ctx the parse tree
	 */
	void enterExtract_expression_from_array_index(ChameleonScriptParser.Extract_expression_from_array_indexContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#extract_expression_from_array_index}.
	 * @param ctx the parse tree
	 */
	void exitExtract_expression_from_array_index(ChameleonScriptParser.Extract_expression_from_array_indexContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#operand_expression_v5}.
	 * @param ctx the parse tree
	 */
	void enterOperand_expression_v5(ChameleonScriptParser.Operand_expression_v5Context ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v5}.
	 * @param ctx the parse tree
	 */
	void exitOperand_expression_v5(ChameleonScriptParser.Operand_expression_v5Context ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#extract_expression_from_array_slice}.
	 * @param ctx the parse tree
	 */
	void enterExtract_expression_from_array_slice(ChameleonScriptParser.Extract_expression_from_array_sliceContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#extract_expression_from_array_slice}.
	 * @param ctx the parse tree
	 */
	void exitExtract_expression_from_array_slice(ChameleonScriptParser.Extract_expression_from_array_sliceContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#operand_expression_v6}.
	 * @param ctx the parse tree
	 */
	void enterOperand_expression_v6(ChameleonScriptParser.Operand_expression_v6Context ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v6}.
	 * @param ctx the parse tree
	 */
	void exitOperand_expression_v6(ChameleonScriptParser.Operand_expression_v6Context ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#exec_chameleon_command}.
	 * @param ctx the parse tree
	 */
	void enterExec_chameleon_command(ChameleonScriptParser.Exec_chameleon_commandContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#exec_chameleon_command}.
	 * @param ctx the parse tree
	 */
	void exitExec_chameleon_command(ChameleonScriptParser.Exec_chameleon_commandContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#scripting_api_function_result}.
	 * @param ctx the parse tree
	 */
	void enterScripting_api_function_result(ChameleonScriptParser.Scripting_api_function_resultContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#scripting_api_function_result}.
	 * @param ctx the parse tree
	 */
	void exitScripting_api_function_result(ChameleonScriptParser.Scripting_api_function_resultContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#operand_expression_v7}.
	 * @param ctx the parse tree
	 */
	void enterOperand_expression_v7(ChameleonScriptParser.Operand_expression_v7Context ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v7}.
	 * @param ctx the parse tree
	 */
	void exitOperand_expression_v7(ChameleonScriptParser.Operand_expression_v7Context ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#array_literal_list}.
	 * @param ctx the parse tree
	 */
	void enterArray_literal_list(ChameleonScriptParser.Array_literal_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#array_literal_list}.
	 * @param ctx the parse tree
	 */
	void exitArray_literal_list(ChameleonScriptParser.Array_literal_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#operand_expression_v72}.
	 * @param ctx the parse tree
	 */
	void enterOperand_expression_v72(ChameleonScriptParser.Operand_expression_v72Context ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v72}.
	 * @param ctx the parse tree
	 */
	void exitOperand_expression_v72(ChameleonScriptParser.Operand_expression_v72Context ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#assignment_operation}.
	 * @param ctx the parse tree
	 */
	void enterAssignment_operation(ChameleonScriptParser.Assignment_operationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#assignment_operation}.
	 * @param ctx the parse tree
	 */
	void exitAssignment_operation(ChameleonScriptParser.Assignment_operationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#operand_expression_v8}.
	 * @param ctx the parse tree
	 */
	void enterOperand_expression_v8(ChameleonScriptParser.Operand_expression_v8Context ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v8}.
	 * @param ctx the parse tree
	 */
	void exitOperand_expression_v8(ChameleonScriptParser.Operand_expression_v8Context ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#ternary_operator_expression}.
	 * @param ctx the parse tree
	 */
	void enterTernary_operator_expression(ChameleonScriptParser.Ternary_operator_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#ternary_operator_expression}.
	 * @param ctx the parse tree
	 */
	void exitTernary_operator_expression(ChameleonScriptParser.Ternary_operator_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#operand_expression_v9}.
	 * @param ctx the parse tree
	 */
	void enterOperand_expression_v9(ChameleonScriptParser.Operand_expression_v9Context ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v9}.
	 * @param ctx the parse tree
	 */
	void exitOperand_expression_v9(ChameleonScriptParser.Operand_expression_v9Context ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#operand_expression}.
	 * @param ctx the parse tree
	 */
	void enterOperand_expression(ChameleonScriptParser.Operand_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#operand_expression}.
	 * @param ctx the parse tree
	 */
	void exitOperand_expression(ChameleonScriptParser.Operand_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#assignment_by_array_slice}.
	 * @param ctx the parse tree
	 */
	void enterAssignment_by_array_slice(ChameleonScriptParser.Assignment_by_array_sliceContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#assignment_by_array_slice}.
	 * @param ctx the parse tree
	 */
	void exitAssignment_by_array_slice(ChameleonScriptParser.Assignment_by_array_sliceContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#function_args_list}.
	 * @param ctx the parse tree
	 */
	void enterFunction_args_list(ChameleonScriptParser.Function_args_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#function_args_list}.
	 * @param ctx the parse tree
	 */
	void exitFunction_args_list(ChameleonScriptParser.Function_args_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link ChameleonScriptParser#label_statement}.
	 * @param ctx the parse tree
	 */
	void enterLabel_statement(ChameleonScriptParser.Label_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ChameleonScriptParser#label_statement}.
	 * @param ctx the parse tree
	 */
	void exitLabel_statement(ChameleonScriptParser.Label_statementContext ctx);
}