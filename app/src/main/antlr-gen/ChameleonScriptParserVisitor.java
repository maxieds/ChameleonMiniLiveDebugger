// Generated from app/src/main/java/com/maxieds/chameleonminilivedebugger/ScriptingAPI/ChameleonScriptParser.g4 by ANTLR 4.13.2
package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

     import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariable;
     import com.maxieds.chameleonminilivedebugger.AndroidLogger;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ChameleonScriptParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ChameleonScriptParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#file_contents}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile_contents(ChameleonScriptParser.File_contentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#script_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScript_line(ChameleonScriptParser.Script_lineContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#script_line_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScript_line_block(ChameleonScriptParser.Script_line_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#while_loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile_loop(ChameleonScriptParser.While_loopContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#if_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_block(ChameleonScriptParser.If_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#ifelse_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfelse_block(ChameleonScriptParser.Ifelse_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#variable_reference_v1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_reference_v1(ChameleonScriptParser.Variable_reference_v1Context ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#variable_reference_v2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_reference_v2(ChameleonScriptParser.Variable_reference_v2Context ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#variable_reference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_reference(ChameleonScriptParser.Variable_referenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#type_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_literal(ChameleonScriptParser.Type_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#quoted_string_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuoted_string_literal(ChameleonScriptParser.Quoted_string_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#byte_literal_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitByte_literal_list(ChameleonScriptParser.Byte_literal_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand_expression_v1(ChameleonScriptParser.Operand_expression_v1Context ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#typecast_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypecast_expression(ChameleonScriptParser.Typecast_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand_expression_v2(ChameleonScriptParser.Operand_expression_v2Context ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#other_operation_result}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOther_operation_result(ChameleonScriptParser.Other_operation_resultContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v3}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand_expression_v3(ChameleonScriptParser.Operand_expression_v3Context ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#boolean_valued_operation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolean_valued_operation(ChameleonScriptParser.Boolean_valued_operationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v4}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand_expression_v4(ChameleonScriptParser.Operand_expression_v4Context ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#extract_expression_from_array_index}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtract_expression_from_array_index(ChameleonScriptParser.Extract_expression_from_array_indexContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v5}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand_expression_v5(ChameleonScriptParser.Operand_expression_v5Context ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#extract_expression_from_array_slice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtract_expression_from_array_slice(ChameleonScriptParser.Extract_expression_from_array_sliceContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v6}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand_expression_v6(ChameleonScriptParser.Operand_expression_v6Context ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#exec_chameleon_command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExec_chameleon_command(ChameleonScriptParser.Exec_chameleon_commandContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#scripting_api_function_result}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScripting_api_function_result(ChameleonScriptParser.Scripting_api_function_resultContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v7}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand_expression_v7(ChameleonScriptParser.Operand_expression_v7Context ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#array_literal_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_literal_list(ChameleonScriptParser.Array_literal_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v72}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand_expression_v72(ChameleonScriptParser.Operand_expression_v72Context ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#assignment_operation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment_operation(ChameleonScriptParser.Assignment_operationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v8}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand_expression_v8(ChameleonScriptParser.Operand_expression_v8Context ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#ternary_operator_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTernary_operator_expression(ChameleonScriptParser.Ternary_operator_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#operand_expression_v9}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand_expression_v9(ChameleonScriptParser.Operand_expression_v9Context ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#operand_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand_expression(ChameleonScriptParser.Operand_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#assignment_by_array_slice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment_by_array_slice(ChameleonScriptParser.Assignment_by_array_sliceContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#function_args_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_args_list(ChameleonScriptParser.Function_args_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link ChameleonScriptParser#label_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel_statement(ChameleonScriptParser.Label_statementContext ctx);
}