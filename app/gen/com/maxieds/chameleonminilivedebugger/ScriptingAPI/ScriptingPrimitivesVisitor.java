// Generated from /Users/mschmidt34/ChameleonMiniLiveDebugger/app/src/main/java/com/maxieds/chameleonminilivedebugger/ScriptingAPI/ScriptingPrimitives.g4 by ANTLR 4.8
package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

     package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ScriptingPrimitivesParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ScriptingPrimitivesVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ScriptingPrimitivesParser#type_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_literal(ScriptingPrimitivesParser.Type_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingPrimitivesParser#variable_reference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_reference(ScriptingPrimitivesParser.Variable_referenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingPrimitivesParser#operand_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand_expression(ScriptingPrimitivesParser.Operand_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingPrimitivesParser#expression_eval_term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_eval_term(ScriptingPrimitivesParser.Expression_eval_termContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingPrimitivesParser#boolean_valued_operation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolean_valued_operation(ScriptingPrimitivesParser.Boolean_valued_operationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingPrimitivesParser#other_operation_result}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOther_operation_result(ScriptingPrimitivesParser.Other_operation_resultContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingPrimitivesParser#assignment_operation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment_operation(ScriptingPrimitivesParser.Assignment_operationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptingPrimitivesParser#typecast_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypecast_expression(ScriptingPrimitivesParser.Typecast_expressionContext ctx);
}