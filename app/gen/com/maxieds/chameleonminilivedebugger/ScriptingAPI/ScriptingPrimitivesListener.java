// Generated from /Users/mschmidt34/ChameleonMiniLiveDebugger/app/src/main/java/com/maxieds/chameleonminilivedebugger/ScriptingAPI/ScriptingPrimitives.g4 by ANTLR 4.8
package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

     package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ScriptingPrimitivesParser}.
 */
public interface ScriptingPrimitivesListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ScriptingPrimitivesParser#type_literal}.
	 * @param ctx the parse tree
	 */
	void enterType_literal(ScriptingPrimitivesParser.Type_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingPrimitivesParser#type_literal}.
	 * @param ctx the parse tree
	 */
	void exitType_literal(ScriptingPrimitivesParser.Type_literalContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingPrimitivesParser#variable_reference}.
	 * @param ctx the parse tree
	 */
	void enterVariable_reference(ScriptingPrimitivesParser.Variable_referenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingPrimitivesParser#variable_reference}.
	 * @param ctx the parse tree
	 */
	void exitVariable_reference(ScriptingPrimitivesParser.Variable_referenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingPrimitivesParser#operand_expression}.
	 * @param ctx the parse tree
	 */
	void enterOperand_expression(ScriptingPrimitivesParser.Operand_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingPrimitivesParser#operand_expression}.
	 * @param ctx the parse tree
	 */
	void exitOperand_expression(ScriptingPrimitivesParser.Operand_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingPrimitivesParser#expression_eval_term}.
	 * @param ctx the parse tree
	 */
	void enterExpression_eval_term(ScriptingPrimitivesParser.Expression_eval_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingPrimitivesParser#expression_eval_term}.
	 * @param ctx the parse tree
	 */
	void exitExpression_eval_term(ScriptingPrimitivesParser.Expression_eval_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingPrimitivesParser#boolean_valued_operation}.
	 * @param ctx the parse tree
	 */
	void enterBoolean_valued_operation(ScriptingPrimitivesParser.Boolean_valued_operationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingPrimitivesParser#boolean_valued_operation}.
	 * @param ctx the parse tree
	 */
	void exitBoolean_valued_operation(ScriptingPrimitivesParser.Boolean_valued_operationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingPrimitivesParser#other_operation_result}.
	 * @param ctx the parse tree
	 */
	void enterOther_operation_result(ScriptingPrimitivesParser.Other_operation_resultContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingPrimitivesParser#other_operation_result}.
	 * @param ctx the parse tree
	 */
	void exitOther_operation_result(ScriptingPrimitivesParser.Other_operation_resultContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingPrimitivesParser#assignment_operation}.
	 * @param ctx the parse tree
	 */
	void enterAssignment_operation(ScriptingPrimitivesParser.Assignment_operationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingPrimitivesParser#assignment_operation}.
	 * @param ctx the parse tree
	 */
	void exitAssignment_operation(ScriptingPrimitivesParser.Assignment_operationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptingPrimitivesParser#typecast_expression}.
	 * @param ctx the parse tree
	 */
	void enterTypecast_expression(ScriptingPrimitivesParser.Typecast_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptingPrimitivesParser#typecast_expression}.
	 * @param ctx the parse tree
	 */
	void exitTypecast_expression(ScriptingPrimitivesParser.Typecast_expressionContext ctx);
}