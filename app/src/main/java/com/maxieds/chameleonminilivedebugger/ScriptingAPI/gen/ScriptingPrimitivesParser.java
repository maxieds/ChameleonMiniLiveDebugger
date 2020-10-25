// Generated from /Users/mschmidt34/ChameleonMiniLiveDebugger/app/src/main/java/com/maxieds/chameleonminilivedebugger/ScriptingAPI/ScriptingPrimitives.g4 by ANTLR 4.8

     package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ScriptingPrimitivesParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WhiteSpaceText=1, WhiteSpace=2, NewLineBreak=3, CStyleBlockComment=4, 
		CStyleLineComment=5, HashStyleLineComment=6, Commentary=7, HexDigit=8, 
		HexString=9, HexByte=10, HexLiteral=11, BooleanLiteral=12, AsciiChar=13, 
		StringLiteral=14, HexStringLiteral=15, RawStringLiteral=16, CommaSeparator=17, 
		OpenParens=18, ClosedParens=19, ColonSeparator=20, VariableNameStartChar=21, 
		VariableNameMiddleChar=22, VariableStartSymbol=23, VariableName=24, EqualsComparisonOperator=25, 
		NotEqualsComparisonOperator=26, LogicalAndOperator=27, LogicalOrOperator=28, 
		LogicalNotOperator=29, RightShiftOperator=30, LeftShiftOperator=31, BitwiseAndOperator=32, 
		BitwiseOrOperator=33, BitwiseXorOperator=34, BitwiseNotOperator=35, TernaryOperatorFirstSymbol=36, 
		TernaryOperatorSecondSymbol=37, DefEqualsOperator=38, PlusEqualsOperator=39, 
		TypeCastByte=40, TypeCastShort=41, TypeCastInt32=42, TypeCastBoolean=43, 
		TypeCastString=44, TYPE_INT=45, TYPE_BOOL=46, TYPE_BYTES=47, TYPE_STRING=48, 
		TYPE_HEX_STRING=49, TYPE_RAW_STRING=50;
	public static final int
		RULE_type_literal = 0, RULE_variable_reference = 1, RULE_operand_expression = 2, 
		RULE_expression_eval_term = 3, RULE_boolean_valued_operation = 4, RULE_other_operation_result = 5, 
		RULE_assignment_operation = 6, RULE_typecast_expression = 7;
	private static String[] makeRuleNames() {
		return new String[] {
			"type_literal", "variable_reference", "operand_expression", "expression_eval_term", 
			"boolean_valued_operation", "other_operation_result", "assignment_operation", 
			"typecast_expression"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, "','", "'('", "')'", "':'", null, null, 
			"'$'", null, "'=='", "'!='", null, null, null, "'>>'", "'<<'", "'&'", 
			"'|'", "'^'", "'~'", "'?'", null, null, "'+='", "'(Byte)'", "'(Short)'", 
			"'(Int32)'", "'(Boolean)'", "'(String)'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "WhiteSpaceText", "WhiteSpace", "NewLineBreak", "CStyleBlockComment", 
			"CStyleLineComment", "HashStyleLineComment", "Commentary", "HexDigit", 
			"HexString", "HexByte", "HexLiteral", "BooleanLiteral", "AsciiChar", 
			"StringLiteral", "HexStringLiteral", "RawStringLiteral", "CommaSeparator", 
			"OpenParens", "ClosedParens", "ColonSeparator", "VariableNameStartChar", 
			"VariableNameMiddleChar", "VariableStartSymbol", "VariableName", "EqualsComparisonOperator", 
			"NotEqualsComparisonOperator", "LogicalAndOperator", "LogicalOrOperator", 
			"LogicalNotOperator", "RightShiftOperator", "LeftShiftOperator", "BitwiseAndOperator", 
			"BitwiseOrOperator", "BitwiseXorOperator", "BitwiseNotOperator", "TernaryOperatorFirstSymbol", 
			"TernaryOperatorSecondSymbol", "DefEqualsOperator", "PlusEqualsOperator", 
			"TypeCastByte", "TypeCastShort", "TypeCastInt32", "TypeCastBoolean", 
			"TypeCastString", "TYPE_INT", "TYPE_BOOL", "TYPE_BYTES", "TYPE_STRING", 
			"TYPE_HEX_STRING", "TYPE_RAW_STRING"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "ScriptingPrimitives.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ScriptingPrimitivesParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class Type_literalContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Token hs;
		public Token hb;
		public Token hl;
		public Token bl;
		public Token sl;
		public Token rs;
		public TerminalNode HexString() { return getToken(ScriptingPrimitivesParser.HexString, 0); }
		public TerminalNode HexByte() { return getToken(ScriptingPrimitivesParser.HexByte, 0); }
		public TerminalNode HexLiteral() { return getToken(ScriptingPrimitivesParser.HexLiteral, 0); }
		public TerminalNode BooleanLiteral() { return getToken(ScriptingPrimitivesParser.BooleanLiteral, 0); }
		public TerminalNode StringLiteral() { return getToken(ScriptingPrimitivesParser.StringLiteral, 0); }
		public TerminalNode HexStringLiteral() { return getToken(ScriptingPrimitivesParser.HexStringLiteral, 0); }
		public TerminalNode RawStringLiteral() { return getToken(ScriptingPrimitivesParser.RawStringLiteral, 0); }
		public Type_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).enterType_literal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).exitType_literal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingPrimitivesVisitor ) return ((ScriptingPrimitivesVisitor<? extends T>)visitor).visitType_literal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_literalContext type_literal() throws RecognitionException {
		Type_literalContext _localctx = new Type_literalContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_type_literal);
		try {
			setState(30);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case HexString:
				enterOuterAlt(_localctx, 1);
				{
				setState(16);
				((Type_literalContext)_localctx).hs = match(HexString);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.parseHexString((((Type_literalContext)_localctx).hs!=null?((Type_literalContext)_localctx).hs.getText():null)); 
				}
				break;
			case HexByte:
				enterOuterAlt(_localctx, 2);
				{
				setState(18);
				((Type_literalContext)_localctx).hb = match(HexByte);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.parseInt((((Type_literalContext)_localctx).hb!=null?((Type_literalContext)_localctx).hb.getText():null)); 
				}
				break;
			case HexLiteral:
				enterOuterAlt(_localctx, 3);
				{
				setState(20);
				((Type_literalContext)_localctx).hl = match(HexLiteral);
				 if((((Type_literalContext)_localctx).hl!=null?((Type_literalContext)_localctx).hl.getText():null).length() > 8) {
				                                ((Type_literalContext)_localctx).svar = ScriptVariable.parseHexString((((Type_literalContext)_localctx).hl!=null?((Type_literalContext)_localctx).hl.getText():null));
				                           }
				                           else if((((Type_literalContext)_localctx).hl!=null?((Type_literalContext)_localctx).hl.getText():null).length() < 2 || !(((Type_literalContext)_localctx).hl!=null?((Type_literalContext)_localctx).hl.getText():null).substring(0, 2).equals("0x")) {
				                                ((Type_literalContext)_localctx).svar = ScriptVariable.parseInt("0x" + (((Type_literalContext)_localctx).hl!=null?((Type_literalContext)_localctx).hl.getText():null));
				                           }
				                           else {
				                                ((Type_literalContext)_localctx).svar = ScriptVariable.parseInt((((Type_literalContext)_localctx).hl!=null?((Type_literalContext)_localctx).hl.getText():null));
				                           }
				                         
				}
				break;
			case BooleanLiteral:
				enterOuterAlt(_localctx, 4);
				{
				setState(22);
				((Type_literalContext)_localctx).bl = match(BooleanLiteral);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.parseBoolean((((Type_literalContext)_localctx).bl!=null?((Type_literalContext)_localctx).bl.getText():null)); 
				}
				break;
			case StringLiteral:
				enterOuterAlt(_localctx, 5);
				{
				setState(24);
				((Type_literalContext)_localctx).sl = match(StringLiteral);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.newInstance().set((((Type_literalContext)_localctx).sl!=null?((Type_literalContext)_localctx).sl.getText():null)); 
				}
				break;
			case HexStringLiteral:
				enterOuterAlt(_localctx, 6);
				{
				setState(26);
				((Type_literalContext)_localctx).hs = match(HexStringLiteral);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.parseHexString((((Type_literalContext)_localctx).hs!=null?((Type_literalContext)_localctx).hs.getText():null)); 
				}
				break;
			case RawStringLiteral:
				enterOuterAlt(_localctx, 7);
				{
				setState(28);
				((Type_literalContext)_localctx).rs = match(RawStringLiteral);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.parseRawString((((Type_literalContext)_localctx).rs!=null?((Type_literalContext)_localctx).rs.getText():null)); 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Variable_referenceContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Token vname;
		public TerminalNode VariableStartSymbol() { return getToken(ScriptingPrimitivesParser.VariableStartSymbol, 0); }
		public TerminalNode VariableName() { return getToken(ScriptingPrimitivesParser.VariableName, 0); }
		public Variable_referenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable_reference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).enterVariable_reference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).exitVariable_reference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingPrimitivesVisitor ) return ((ScriptingPrimitivesVisitor<? extends T>)visitor).visitVariable_reference(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Variable_referenceContext variable_reference() throws RecognitionException {
		Variable_referenceContext _localctx = new Variable_referenceContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_variable_reference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			match(VariableStartSymbol);
			setState(33);
			((Variable_referenceContext)_localctx).vname = match(VariableName);

			           ((Variable_referenceContext)_localctx).svar = ChameleonScripting.getRunningInstance().lookupVariableByName((((Variable_referenceContext)_localctx).vname!=null?((Variable_referenceContext)_localctx).vname.getText():null));
			     
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Operand_expressionContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Variable_referenceContext vr;
		public Type_literalContext tl;
		public Variable_referenceContext variable_reference() {
			return getRuleContext(Variable_referenceContext.class,0);
		}
		public Type_literalContext type_literal() {
			return getRuleContext(Type_literalContext.class,0);
		}
		public Operand_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).enterOperand_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).exitOperand_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingPrimitivesVisitor ) return ((ScriptingPrimitivesVisitor<? extends T>)visitor).visitOperand_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operand_expressionContext operand_expression() throws RecognitionException {
		Operand_expressionContext _localctx = new Operand_expressionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_operand_expression);
		try {
			setState(42);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VariableStartSymbol:
				enterOuterAlt(_localctx, 1);
				{
				setState(36);
				((Operand_expressionContext)_localctx).vr = variable_reference();

				          ((Operand_expressionContext)_localctx).svar = ((Operand_expressionContext)_localctx).vr.svar;
				     
				}
				break;
			case HexString:
			case HexByte:
			case HexLiteral:
			case BooleanLiteral:
			case StringLiteral:
			case HexStringLiteral:
			case RawStringLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(39);
				((Operand_expressionContext)_localctx).tl = type_literal();

				          ((Operand_expressionContext)_localctx).svar = ((Operand_expressionContext)_localctx).tl.svar;
				     
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Expression_eval_termContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Variable_referenceContext vr;
		public Type_literalContext tl;
		public Boolean_valued_operationContext bvOp;
		public Other_operation_resultContext otherOp;
		public Assignment_operationContext aOp;
		public Typecast_expressionContext tc;
		public Variable_referenceContext variable_reference() {
			return getRuleContext(Variable_referenceContext.class,0);
		}
		public Type_literalContext type_literal() {
			return getRuleContext(Type_literalContext.class,0);
		}
		public Boolean_valued_operationContext boolean_valued_operation() {
			return getRuleContext(Boolean_valued_operationContext.class,0);
		}
		public Other_operation_resultContext other_operation_result() {
			return getRuleContext(Other_operation_resultContext.class,0);
		}
		public Assignment_operationContext assignment_operation() {
			return getRuleContext(Assignment_operationContext.class,0);
		}
		public Typecast_expressionContext typecast_expression() {
			return getRuleContext(Typecast_expressionContext.class,0);
		}
		public Expression_eval_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression_eval_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).enterExpression_eval_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).exitExpression_eval_term(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingPrimitivesVisitor ) return ((ScriptingPrimitivesVisitor<? extends T>)visitor).visitExpression_eval_term(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Expression_eval_termContext expression_eval_term() throws RecognitionException {
		Expression_eval_termContext _localctx = new Expression_eval_termContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_expression_eval_term);
		try {
			setState(62);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(44);
				((Expression_eval_termContext)_localctx).vr = variable_reference();

				          ((Expression_eval_termContext)_localctx).svar = ((Expression_eval_termContext)_localctx).vr.svar;
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(47);
				((Expression_eval_termContext)_localctx).tl = type_literal();

				          ((Expression_eval_termContext)_localctx).svar = ((Expression_eval_termContext)_localctx).tl.svar;
				     
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(50);
				((Expression_eval_termContext)_localctx).bvOp = boolean_valued_operation();

				          ((Expression_eval_termContext)_localctx).svar = ScriptVariable.newInstance().set(((Expression_eval_termContext)_localctx).bvOp.opResult);
				     
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(53);
				((Expression_eval_termContext)_localctx).otherOp = other_operation_result();

				          ((Expression_eval_termContext)_localctx).svar = ((Expression_eval_termContext)_localctx).otherOp.svar;
				     
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(56);
				((Expression_eval_termContext)_localctx).aOp = assignment_operation();

				          ((Expression_eval_termContext)_localctx).svar = ((Expression_eval_termContext)_localctx).aOp.svar;
				     
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(59);
				((Expression_eval_termContext)_localctx).tc = typecast_expression();

				          ((Expression_eval_termContext)_localctx).svar = ((Expression_eval_termContext)_localctx).tc.svar;
				     
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Boolean_valued_operationContext extends ParserRuleContext {
		public ScriptVariable opResult;
		public Operand_expressionContext lhs;
		public Operand_expressionContext rhs;
		public TerminalNode EqualsComparisonOperator() { return getToken(ScriptingPrimitivesParser.EqualsComparisonOperator, 0); }
		public List<Operand_expressionContext> operand_expression() {
			return getRuleContexts(Operand_expressionContext.class);
		}
		public Operand_expressionContext operand_expression(int i) {
			return getRuleContext(Operand_expressionContext.class,i);
		}
		public TerminalNode NotEqualsComparisonOperator() { return getToken(ScriptingPrimitivesParser.NotEqualsComparisonOperator, 0); }
		public TerminalNode LogicalAndOperator() { return getToken(ScriptingPrimitivesParser.LogicalAndOperator, 0); }
		public TerminalNode LogicalOrOperator() { return getToken(ScriptingPrimitivesParser.LogicalOrOperator, 0); }
		public TerminalNode LogicalNotOperator() { return getToken(ScriptingPrimitivesParser.LogicalNotOperator, 0); }
		public Boolean_valued_operationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolean_valued_operation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).enterBoolean_valued_operation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).exitBoolean_valued_operation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingPrimitivesVisitor ) return ((ScriptingPrimitivesVisitor<? extends T>)visitor).visitBoolean_valued_operation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Boolean_valued_operationContext boolean_valued_operation() throws RecognitionException {
		Boolean_valued_operationContext _localctx = new Boolean_valued_operationContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_boolean_valued_operation);
		try {
			setState(88);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(64);
				((Boolean_valued_operationContext)_localctx).lhs = operand_expression();
				setState(65);
				match(EqualsComparisonOperator);
				setState(66);
				((Boolean_valued_operationContext)_localctx).rhs = operand_expression();

				          ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsBoolean() == ((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsBoolean());
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(69);
				((Boolean_valued_operationContext)_localctx).lhs = operand_expression();
				setState(70);
				match(NotEqualsComparisonOperator);
				setState(71);
				((Boolean_valued_operationContext)_localctx).rhs = operand_expression();

				          ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsBoolean() != ((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsBoolean());
				     
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(74);
				((Boolean_valued_operationContext)_localctx).lhs = operand_expression();
				setState(75);
				match(LogicalAndOperator);
				setState(76);
				((Boolean_valued_operationContext)_localctx).rhs = operand_expression();

				          ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsBoolean() && ((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsBoolean());
				     
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(79);
				((Boolean_valued_operationContext)_localctx).lhs = operand_expression();
				setState(80);
				match(LogicalOrOperator);
				setState(81);
				((Boolean_valued_operationContext)_localctx).rhs = operand_expression();

				          ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsBoolean() || ((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsBoolean());
				     
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(84);
				match(LogicalNotOperator);
				setState(85);
				((Boolean_valued_operationContext)_localctx).rhs = operand_expression();

				          ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(!((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsBoolean());
				     
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Other_operation_resultContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expressionContext lhs;
		public Operand_expressionContext rhs;
		public Operand_expressionContext cond;
		public Operand_expressionContext vtrue;
		public Operand_expressionContext vfalse;
		public TerminalNode LeftShiftOperator() { return getToken(ScriptingPrimitivesParser.LeftShiftOperator, 0); }
		public List<Operand_expressionContext> operand_expression() {
			return getRuleContexts(Operand_expressionContext.class);
		}
		public Operand_expressionContext operand_expression(int i) {
			return getRuleContext(Operand_expressionContext.class,i);
		}
		public TerminalNode RightShiftOperator() { return getToken(ScriptingPrimitivesParser.RightShiftOperator, 0); }
		public TerminalNode BitwiseAndOperator() { return getToken(ScriptingPrimitivesParser.BitwiseAndOperator, 0); }
		public TerminalNode BitwiseOrOperator() { return getToken(ScriptingPrimitivesParser.BitwiseOrOperator, 0); }
		public TerminalNode BitwiseXorOperator() { return getToken(ScriptingPrimitivesParser.BitwiseXorOperator, 0); }
		public TerminalNode BitwiseNotOperator() { return getToken(ScriptingPrimitivesParser.BitwiseNotOperator, 0); }
		public TerminalNode TernaryOperatorFirstSymbol() { return getToken(ScriptingPrimitivesParser.TernaryOperatorFirstSymbol, 0); }
		public TerminalNode TernaryOperatorSecondSymbol() { return getToken(ScriptingPrimitivesParser.TernaryOperatorSecondSymbol, 0); }
		public Other_operation_resultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_other_operation_result; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).enterOther_operation_result(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).exitOther_operation_result(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingPrimitivesVisitor ) return ((ScriptingPrimitivesVisitor<? extends T>)visitor).visitOther_operation_result(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Other_operation_resultContext other_operation_result() throws RecognitionException {
		Other_operation_resultContext _localctx = new Other_operation_resultContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_other_operation_result);
		try {
			setState(126);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(90);
				((Other_operation_resultContext)_localctx).lhs = operand_expression();
				setState(91);
				match(LeftShiftOperator);
				setState(92);
				((Other_operation_resultContext)_localctx).rhs = operand_expression();

				          ((Other_operation_resultContext)_localctx).svar = ((Other_operation_resultContext)_localctx).lhs.svar.binaryOperation(ScriptVariable.BinaryOperation.BINOP_SHIFT_LEFT, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(95);
				((Other_operation_resultContext)_localctx).lhs = operand_expression();
				setState(96);
				match(RightShiftOperator);
				setState(97);
				((Other_operation_resultContext)_localctx).rhs = operand_expression();

				          ((Other_operation_resultContext)_localctx).svar = ((Other_operation_resultContext)_localctx).lhs.svar.binaryOperation(ScriptVariable.BinaryOperation.BINOP_SHIFT_RIGHT, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(100);
				((Other_operation_resultContext)_localctx).lhs = operand_expression();
				setState(101);
				match(BitwiseAndOperator);
				setState(102);
				((Other_operation_resultContext)_localctx).rhs = operand_expression();

				          ((Other_operation_resultContext)_localctx).svar = ((Other_operation_resultContext)_localctx).lhs.svar.binaryOperation(ScriptVariable.BinaryOperation.BINOP_BITWISE_AND, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(105);
				((Other_operation_resultContext)_localctx).lhs = operand_expression();
				setState(106);
				match(BitwiseOrOperator);
				setState(107);
				((Other_operation_resultContext)_localctx).rhs = operand_expression();

				          ((Other_operation_resultContext)_localctx).svar = ((Other_operation_resultContext)_localctx).lhs.svar.binaryOperation(ScriptVariable.BinaryOperation.BINOP_BITWISE_OR, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(110);
				((Other_operation_resultContext)_localctx).lhs = operand_expression();
				setState(111);
				match(BitwiseXorOperator);
				setState(112);
				((Other_operation_resultContext)_localctx).rhs = operand_expression();

				          ((Other_operation_resultContext)_localctx).svar = ((Other_operation_resultContext)_localctx).lhs.svar.binaryOperation(ScriptVariable.BinaryOperation.BINOP_BITWISE_XOR, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(115);
				match(BitwiseNotOperator);
				setState(116);
				((Other_operation_resultContext)_localctx).rhs = operand_expression();

				          ((Other_operation_resultContext)_localctx).svar = ((Other_operation_resultContext)_localctx).rhs.svar.unaryOperation(ScriptVariable.UnaryOperation.UOP_BITWISE_NOT);
				     
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(119);
				((Other_operation_resultContext)_localctx).cond = operand_expression();
				setState(120);
				match(TernaryOperatorFirstSymbol);
				setState(121);
				((Other_operation_resultContext)_localctx).vtrue = operand_expression();
				setState(122);
				match(TernaryOperatorSecondSymbol);
				setState(123);
				((Other_operation_resultContext)_localctx).vfalse = operand_expression();

				          boolean predicate = ((Other_operation_resultContext)_localctx).cond.svar.getAsBoolean();
				          if(predicate) {
				               ((Other_operation_resultContext)_localctx).svar = ((Other_operation_resultContext)_localctx).vtrue.svar;
				          }
				          else {
				               ((Other_operation_resultContext)_localctx).svar = ((Other_operation_resultContext)_localctx).vfalse.svar;
				          }
				     
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Assignment_operationContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Variable_referenceContext lhs;
		public Operand_expressionContext rhs;
		public TerminalNode DefEqualsOperator() { return getToken(ScriptingPrimitivesParser.DefEqualsOperator, 0); }
		public Variable_referenceContext variable_reference() {
			return getRuleContext(Variable_referenceContext.class,0);
		}
		public Operand_expressionContext operand_expression() {
			return getRuleContext(Operand_expressionContext.class,0);
		}
		public TerminalNode PlusEqualsOperator() { return getToken(ScriptingPrimitivesParser.PlusEqualsOperator, 0); }
		public Assignment_operationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment_operation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).enterAssignment_operation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).exitAssignment_operation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingPrimitivesVisitor ) return ((ScriptingPrimitivesVisitor<? extends T>)visitor).visitAssignment_operation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Assignment_operationContext assignment_operation() throws RecognitionException {
		Assignment_operationContext _localctx = new Assignment_operationContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_assignment_operation);
		try {
			setState(138);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(128);
				((Assignment_operationContext)_localctx).lhs = variable_reference();
				setState(129);
				match(DefEqualsOperator);
				setState(130);
				((Assignment_operationContext)_localctx).rhs = operand_expression();

				          ((Assignment_operationContext)_localctx).svar = ((Assignment_operationContext)_localctx).rhs.svar;
				          ChameleonScripting.getRunningInstance().setVariableByName(((Assignment_operationContext)_localctx).lhs.svar.getName());
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(133);
				((Assignment_operationContext)_localctx).lhs = variable_reference();
				setState(134);
				match(PlusEqualsOperator);
				setState(135);
				((Assignment_operationContext)_localctx).rhs = operand_expression();

				          ((Assignment_operationContext)_localctx).svar = ((Assignment_operationContext)_localctx).lhs.svar.binaryOperation(ScriptVariable.BinaryOperation.BINOP_PLUS, ((Assignment_operationContext)_localctx).rhs.svar);
				          ChameleonScripting.getRunningInstance().setVariableByName(((Assignment_operationContext)_localctx).lhs.svar.getName());
				     
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Typecast_expressionContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expressionContext initVar;
		public TerminalNode TypeCastByte() { return getToken(ScriptingPrimitivesParser.TypeCastByte, 0); }
		public Operand_expressionContext operand_expression() {
			return getRuleContext(Operand_expressionContext.class,0);
		}
		public TerminalNode TypeCastShort() { return getToken(ScriptingPrimitivesParser.TypeCastShort, 0); }
		public TerminalNode TypeCastInt32() { return getToken(ScriptingPrimitivesParser.TypeCastInt32, 0); }
		public TerminalNode TypeCastBoolean() { return getToken(ScriptingPrimitivesParser.TypeCastBoolean, 0); }
		public TerminalNode TypeCastString() { return getToken(ScriptingPrimitivesParser.TypeCastString, 0); }
		public Typecast_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typecast_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).enterTypecast_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingPrimitivesListener ) ((ScriptingPrimitivesListener)listener).exitTypecast_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingPrimitivesVisitor ) return ((ScriptingPrimitivesVisitor<? extends T>)visitor).visitTypecast_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Typecast_expressionContext typecast_expression() throws RecognitionException {
		Typecast_expressionContext _localctx = new Typecast_expressionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_typecast_expression);
		try {
			setState(160);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TypeCastByte:
				enterOuterAlt(_localctx, 1);
				{
				setState(140);
				match(TypeCastByte);
				setState(141);
				((Typecast_expressionContext)_localctx).initVar = operand_expression();

				          ((Typecast_expressionContext)_localctx).svar = ((Typecast_expressionContext)_localctx).initVar.svar.getAsByte();
				     
				}
				break;
			case TypeCastShort:
				enterOuterAlt(_localctx, 2);
				{
				setState(144);
				match(TypeCastShort);
				setState(145);
				((Typecast_expressionContext)_localctx).initVar = operand_expression();

				          ((Typecast_expressionContext)_localctx).svar = ((Typecast_expressionContext)_localctx).initVar.svar.getAsShort();
				     
				}
				break;
			case TypeCastInt32:
				enterOuterAlt(_localctx, 3);
				{
				setState(148);
				match(TypeCastInt32);
				setState(149);
				((Typecast_expressionContext)_localctx).initVar = operand_expression();

				          ((Typecast_expressionContext)_localctx).svar = ((Typecast_expressionContext)_localctx).initVar.svar.getAsInteger();
				     
				}
				break;
			case TypeCastBoolean:
				enterOuterAlt(_localctx, 4);
				{
				setState(152);
				match(TypeCastBoolean);
				setState(153);
				((Typecast_expressionContext)_localctx).initVar = operand_expression();

				          ((Typecast_expressionContext)_localctx).svar = ((Typecast_expressionContext)_localctx).initVar.svar.getAsBoolean();
				     
				}
				break;
			case TypeCastString:
				enterOuterAlt(_localctx, 5);
				{
				setState(156);
				match(TypeCastString);
				setState(157);
				((Typecast_expressionContext)_localctx).initVar = operand_expression();

				          ((Typecast_expressionContext)_localctx).svar = ((Typecast_expressionContext)_localctx).initVar.svar.getAsString();
				     
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\64\u00a5\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2!\n\2\3\3\3\3\3\3\3\3\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\5\4-\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5A\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\5\6[\n\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\5\7\u0081\n\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5"+
		"\b\u008d\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\5\t\u00a3\n\t\3\t\2\2\n\2\4\6\b\n\f\16\20\2\2\2"+
		"\u00b7\2 \3\2\2\2\4\"\3\2\2\2\6,\3\2\2\2\b@\3\2\2\2\nZ\3\2\2\2\f\u0080"+
		"\3\2\2\2\16\u008c\3\2\2\2\20\u00a2\3\2\2\2\22\23\7\13\2\2\23!\b\2\1\2"+
		"\24\25\7\f\2\2\25!\b\2\1\2\26\27\7\r\2\2\27!\b\2\1\2\30\31\7\16\2\2\31"+
		"!\b\2\1\2\32\33\7\20\2\2\33!\b\2\1\2\34\35\7\21\2\2\35!\b\2\1\2\36\37"+
		"\7\22\2\2\37!\b\2\1\2 \22\3\2\2\2 \24\3\2\2\2 \26\3\2\2\2 \30\3\2\2\2"+
		" \32\3\2\2\2 \34\3\2\2\2 \36\3\2\2\2!\3\3\2\2\2\"#\7\31\2\2#$\7\32\2\2"+
		"$%\b\3\1\2%\5\3\2\2\2&\'\5\4\3\2\'(\b\4\1\2(-\3\2\2\2)*\5\2\2\2*+\b\4"+
		"\1\2+-\3\2\2\2,&\3\2\2\2,)\3\2\2\2-\7\3\2\2\2./\5\4\3\2/\60\b\5\1\2\60"+
		"A\3\2\2\2\61\62\5\2\2\2\62\63\b\5\1\2\63A\3\2\2\2\64\65\5\n\6\2\65\66"+
		"\b\5\1\2\66A\3\2\2\2\678\5\f\7\289\b\5\1\29A\3\2\2\2:;\5\16\b\2;<\b\5"+
		"\1\2<A\3\2\2\2=>\5\20\t\2>?\b\5\1\2?A\3\2\2\2@.\3\2\2\2@\61\3\2\2\2@\64"+
		"\3\2\2\2@\67\3\2\2\2@:\3\2\2\2@=\3\2\2\2A\t\3\2\2\2BC\5\6\4\2CD\7\33\2"+
		"\2DE\5\6\4\2EF\b\6\1\2F[\3\2\2\2GH\5\6\4\2HI\7\34\2\2IJ\5\6\4\2JK\b\6"+
		"\1\2K[\3\2\2\2LM\5\6\4\2MN\7\35\2\2NO\5\6\4\2OP\b\6\1\2P[\3\2\2\2QR\5"+
		"\6\4\2RS\7\36\2\2ST\5\6\4\2TU\b\6\1\2U[\3\2\2\2VW\7\37\2\2WX\5\6\4\2X"+
		"Y\b\6\1\2Y[\3\2\2\2ZB\3\2\2\2ZG\3\2\2\2ZL\3\2\2\2ZQ\3\2\2\2ZV\3\2\2\2"+
		"[\13\3\2\2\2\\]\5\6\4\2]^\7!\2\2^_\5\6\4\2_`\b\7\1\2`\u0081\3\2\2\2ab"+
		"\5\6\4\2bc\7 \2\2cd\5\6\4\2de\b\7\1\2e\u0081\3\2\2\2fg\5\6\4\2gh\7\"\2"+
		"\2hi\5\6\4\2ij\b\7\1\2j\u0081\3\2\2\2kl\5\6\4\2lm\7#\2\2mn\5\6\4\2no\b"+
		"\7\1\2o\u0081\3\2\2\2pq\5\6\4\2qr\7$\2\2rs\5\6\4\2st\b\7\1\2t\u0081\3"+
		"\2\2\2uv\7%\2\2vw\5\6\4\2wx\b\7\1\2x\u0081\3\2\2\2yz\5\6\4\2z{\7&\2\2"+
		"{|\5\6\4\2|}\7\'\2\2}~\5\6\4\2~\177\b\7\1\2\177\u0081\3\2\2\2\u0080\\"+
		"\3\2\2\2\u0080a\3\2\2\2\u0080f\3\2\2\2\u0080k\3\2\2\2\u0080p\3\2\2\2\u0080"+
		"u\3\2\2\2\u0080y\3\2\2\2\u0081\r\3\2\2\2\u0082\u0083\5\4\3\2\u0083\u0084"+
		"\7(\2\2\u0084\u0085\5\6\4\2\u0085\u0086\b\b\1\2\u0086\u008d\3\2\2\2\u0087"+
		"\u0088\5\4\3\2\u0088\u0089\7)\2\2\u0089\u008a\5\6\4\2\u008a\u008b\b\b"+
		"\1\2\u008b\u008d\3\2\2\2\u008c\u0082\3\2\2\2\u008c\u0087\3\2\2\2\u008d"+
		"\17\3\2\2\2\u008e\u008f\7*\2\2\u008f\u0090\5\6\4\2\u0090\u0091\b\t\1\2"+
		"\u0091\u00a3\3\2\2\2\u0092\u0093\7+\2\2\u0093\u0094\5\6\4\2\u0094\u0095"+
		"\b\t\1\2\u0095\u00a3\3\2\2\2\u0096\u0097\7,\2\2\u0097\u0098\5\6\4\2\u0098"+
		"\u0099\b\t\1\2\u0099\u00a3\3\2\2\2\u009a\u009b\7-\2\2\u009b\u009c\5\6"+
		"\4\2\u009c\u009d\b\t\1\2\u009d\u00a3\3\2\2\2\u009e\u009f\7.\2\2\u009f"+
		"\u00a0\5\6\4\2\u00a0\u00a1\b\t\1\2\u00a1\u00a3\3\2\2\2\u00a2\u008e\3\2"+
		"\2\2\u00a2\u0092\3\2\2\2\u00a2\u0096\3\2\2\2\u00a2\u009a\3\2\2\2\u00a2"+
		"\u009e\3\2\2\2\u00a3\21\3\2\2\2\t ,@Z\u0080\u008c\u00a2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}