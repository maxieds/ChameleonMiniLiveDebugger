// Generated from /Users/mschmidt34/ChameleonMiniLiveDebugger/app/src/main/java/com/maxieds/chameleonminilivedebugger/ScriptingAPI/ScriptingPrimitives.g4 by ANTLR 4.8
package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

     package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link ScriptingPrimitivesVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class ScriptingPrimitivesBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements ScriptingPrimitivesVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitType_literal(ScriptingPrimitivesParser.Type_literalContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitVariable_reference(ScriptingPrimitivesParser.Variable_referenceContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitOperand_expression(ScriptingPrimitivesParser.Operand_expressionContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitExpression_eval_term(ScriptingPrimitivesParser.Expression_eval_termContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitBoolean_valued_operation(ScriptingPrimitivesParser.Boolean_valued_operationContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitOther_operation_result(ScriptingPrimitivesParser.Other_operation_resultContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitAssignment_operation(ScriptingPrimitivesParser.Assignment_operationContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitTypecast_expression(ScriptingPrimitivesParser.Typecast_expressionContext ctx) { return visitChildren(ctx); }
}