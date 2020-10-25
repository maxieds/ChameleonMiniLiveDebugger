// Generated from /Users/mschmidt34/ChameleonMiniLiveDebugger/app/src/main/java/com/maxieds/chameleonminilivedebugger/ScriptingAPI/ScriptingAPI.g4 by ANTLR 4.8

     package com.maxieds.chameleonminilivedebugger.ScriptingAPI;


     package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ScriptingAPIParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ScriptingAPIVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ScriptingAPIParser#function_args_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_args_list(ScriptingAPIParser.Function_args_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingAPIParser#scripting_api_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScripting_api_function(ScriptingAPIParser.Scripting_api_functionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingAPIParser#type_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_literal(ScriptingAPIParser.Type_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingAPIParser#variable_reference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_reference(ScriptingAPIParser.Variable_referenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingAPIParser#operand_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand_expression(ScriptingAPIParser.Operand_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingAPIParser#expression_eval_term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_eval_term(ScriptingAPIParser.Expression_eval_termContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingAPIParser#boolean_valued_operation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolean_valued_operation(ScriptingAPIParser.Boolean_valued_operationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingAPIParser#other_operation_result}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOther_operation_result(ScriptingAPIParser.Other_operation_resultContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingAPIParser#assignment_operation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment_operation(ScriptingAPIParser.Assignment_operationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingAPIParser#typecast_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypecast_expression(ScriptingAPIParser.Typecast_expressionContext ctx);
}