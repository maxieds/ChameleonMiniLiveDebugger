// Generated from /Users/mschmidt34/ChameleonMiniLiveDebugger/app/src/main/java/com/maxieds/chameleonminilivedebugger/ScriptingAPI/ScriptingAPI.g4 by ANTLR 4.8

     package com.maxieds.chameleonminilivedebugger.ScriptingAPI;


     package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ScriptingAPIParser}.
 */
public interface ScriptingAPIListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ScriptingAPIParser#function_args_list}.
	 * @param ctx the parse tree
	 */
	void enterFunction_args_list(ScriptingAPIParser.Function_args_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingAPIParser#function_args_list}.
	 * @param ctx the parse tree
	 */
	void exitFunction_args_list(ScriptingAPIParser.Function_args_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingAPIParser#scripting_api_function}.
	 * @param ctx the parse tree
	 */
	void enterScripting_api_function(ScriptingAPIParser.Scripting_api_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingAPIParser#scripting_api_function}.
	 * @param ctx the parse tree
	 */
	void exitScripting_api_function(ScriptingAPIParser.Scripting_api_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingAPIParser#type_literal}.
	 * @param ctx the parse tree
	 */
	void enterType_literal(ScriptingAPIParser.Type_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingAPIParser#type_literal}.
	 * @param ctx the parse tree
	 */
	void exitType_literal(ScriptingAPIParser.Type_literalContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingAPIParser#variable_reference}.
	 * @param ctx the parse tree
	 */
	void enterVariable_reference(ScriptingAPIParser.Variable_referenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingAPIParser#variable_reference}.
	 * @param ctx the parse tree
	 */
	void exitVariable_reference(ScriptingAPIParser.Variable_referenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingAPIParser#operand_expression}.
	 * @param ctx the parse tree
	 */
	void enterOperand_expression(ScriptingAPIParser.Operand_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingAPIParser#operand_expression}.
	 * @param ctx the parse tree
	 */
	void exitOperand_expression(ScriptingAPIParser.Operand_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingAPIParser#expression_eval_term}.
	 * @param ctx the parse tree
	 */
	void enterExpression_eval_term(ScriptingAPIParser.Expression_eval_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingAPIParser#expression_eval_term}.
	 * @param ctx the parse tree
	 */
	void exitExpression_eval_term(ScriptingAPIParser.Expression_eval_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingAPIParser#boolean_valued_operation}.
	 * @param ctx the parse tree
	 */
	void enterBoolean_valued_operation(ScriptingAPIParser.Boolean_valued_operationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingAPIParser#boolean_valued_operation}.
	 * @param ctx the parse tree
	 */
	void exitBoolean_valued_operation(ScriptingAPIParser.Boolean_valued_operationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingAPIParser#other_operation_result}.
	 * @param ctx the parse tree
	 */
	void enterOther_operation_result(ScriptingAPIParser.Other_operation_resultContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingAPIParser#other_operation_result}.
	 * @param ctx the parse tree
	 */
	void exitOther_operation_result(ScriptingAPIParser.Other_operation_resultContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingAPIParser#assignment_operation}.
	 * @param ctx the parse tree
	 */
	void enterAssignment_operation(ScriptingAPIParser.Assignment_operationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingAPIParser#assignment_operation}.
	 * @param ctx the parse tree
	 */
	void exitAssignment_operation(ScriptingAPIParser.Assignment_operationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingAPIParser#typecast_expression}.
	 * @param ctx the parse tree
	 */
	void enterTypecast_expression(ScriptingAPIParser.Typecast_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingAPIParser#typecast_expression}.
	 * @param ctx the parse tree
	 */
	void exitTypecast_expression(ScriptingAPIParser.Typecast_expressionContext ctx);
}