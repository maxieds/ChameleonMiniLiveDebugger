// Generated from /Users/mschmidt34/ChameleonMiniLiveDebugger/app/src/main/java/com/maxieds/chameleonminilivedebugger/ScriptingAPI/ScriptingAPI.g4 by ANTLR 4.8

     package com.maxieds.chameleonminilivedebugger.ScriptingAPI;


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
public class ScriptingAPIParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		FunctionArgInnerSeparator=1, FunctionStartArgsDelimiter=2, FunctionEndArgsDelimiter=3, 
		ScriptingAPIFunctionName=4, ExitFuncName=5, ScriptControlFlowFunctions=6, 
		PrintFuncName=7, PrintfFuncName=8, PrintingAndLoggingFunctions=9, IsChameleonConnectedFuncName=10, 
		IsChameleonRevGFuncName=11, IsChameleonRevEFuncName=12, ChameleonConnectionTypeFunctions=13, 
		AsHexStringFuncName=14, AsBinaryStringFuncName=15, AsByteArrayFuncName=16, 
		GetLengthFuncName=17, GetTypeFuncName=18, ToStringFuncName=19, VariableTypeFunctions=20, 
		AssertFuncName=21, DebuggingFunctions=22, GetEnvFuncName=23, EnvironmentFunctions=24, 
		CmdGetResponseFuncName=25, CmdGetResponseCodeFuncName=26, CmdGetResponseDescFuncName=27, 
		GetResponseDataFuncName=28, CmdIsSuccessFuncName=29, CmdIsErrorFuncName=30, 
		CmdContainsDataFuncName=31, CmdSaveDeviceStateFuncName=32, CmdRestoreDeviceStateFuncName=33, 
		CmdDownloadTagFuncName=34, CmdUploadTagFuncName=35, CmdDownloadLogsFuncName=36, 
		ChameleonCommandAndLogFunctions=37, StringSearchFuncName=38, StringContainsFuncName=39, 
		StringReplaceFuncName=40, StringSplitFuncName=41, StringStripFuncName=42, 
		SubstrFuncName=43, StringFunctions=44, AsWrappedAPDUFuncName=45, ExtractDataFromWrappedAPDUFuncName=46, 
		ExtractDataFromNativeAPDUFuncName=47, SearchAPDUCStatusCodesFuncName=48, 
		SearchAPDUInsCodesFuncName=49, SearchAPDUClaCodesFuncName=50, APDUHandlingFunctions=51, 
		GetRandomBytesFuncName=52, GetRandomIntFuncName=53, GetCRC16FuncName=54, 
		AppendCRC16FuncName=55, CheckCRC16FuncName=56, GetCommonKeysFuncName=57, 
		GetUserKeysFuncName=58, CryptoAndHashFunctions=59, GetTimestampFuncName=60, 
		MemoryXORFuncName=61, MaxFuncName=62, MinFuncName=63, ArrayReverseFuncName=64, 
		ArrayPadLeftFuncName=65, ArrayPadRightFuncName=66, GetSubarrayFuncName=67, 
		GetConstantStringFuncName=68, GetConstantByteArrayFuncName=69, GetIntegersFromRangeFuncName=70, 
		UtilityFunctions=71, WhiteSpaceText=72, WhiteSpace=73, NewLineBreak=74, 
		CStyleBlockComment=75, CStyleLineComment=76, HashStyleLineComment=77, 
		Commentary=78, HexDigit=79, HexString=80, HexByte=81, HexLiteral=82, BooleanLiteral=83, 
		AsciiChar=84, StringLiteral=85, HexStringLiteral=86, RawStringLiteral=87, 
		CommaSeparator=88, OpenParens=89, ClosedParens=90, ColonSeparator=91, 
		VariableNameStartChar=92, VariableNameMiddleChar=93, VariableStartSymbol=94, 
		VariableName=95, EqualsComparisonOperator=96, NotEqualsComparisonOperator=97, 
		LogicalAndOperator=98, LogicalOrOperator=99, LogicalNotOperator=100, RightShiftOperator=101, 
		LeftShiftOperator=102, BitwiseAndOperator=103, BitwiseOrOperator=104, 
		BitwiseXorOperator=105, BitwiseNotOperator=106, TernaryOperatorFirstSymbol=107, 
		TernaryOperatorSecondSymbol=108, DefEqualsOperator=109, PlusEqualsOperator=110, 
		TypeCastByte=111, TypeCastShort=112, TypeCastInt32=113, TypeCastBoolean=114, 
		TypeCastString=115, TYPE_INT=116, TYPE_BOOL=117, TYPE_BYTES=118, TYPE_STRING=119, 
		TYPE_HEX_STRING=120, TYPE_RAW_STRING=121;
	public static final int
		RULE_function_args_list = 0, RULE_scripting_api_function = 1, RULE_type_literal = 2, 
		RULE_variable_reference = 3, RULE_operand_expression = 4, RULE_expression_eval_term = 5, 
		RULE_boolean_valued_operation = 6, RULE_other_operation_result = 7, RULE_assignment_operation = 8, 
		RULE_typecast_expression = 9;
	private static String[] makeRuleNames() {
		return new String[] {
			"function_args_list", "scripting_api_function", "type_literal", "variable_reference", 
			"operand_expression", "expression_eval_term", "boolean_valued_operation", 
			"other_operation_result", "assignment_operation", "typecast_expression"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, "'Exit'", null, "'Print'", "'Printf'", 
			null, "'IsChameleonConnected'", "'IsChameleonRevG'", "'IsChameleonRevE'", 
			null, "'AsHexString'", "'AsBinaryString'", "'AsByteArray'", "'GetLength'", 
			"'GetType'", "'ToString'", null, "'Assert'", null, "'GetEnv'", null, 
			"'GetCommandResponse'", "'GetCommandResponseCode'", "'GetCommandResponseDesc'", 
			"'GetCommandResponseData'", "'CommandIsSuccess'", "'CommandIsError'", 
			"'CommandContainsData'", "'SaveDeviceState'", "'RestoreDeviceState'", 
			"'DownloadTagDump'", "'UploadTagDump'", "'DownloadLogs'", null, "'Find'", 
			"'Contains'", "'Replace'", "'Split'", "'Strip'", "'Substring'", null, 
			"'AsWrappedAPDU'", "'ExtractDataFromWrappedAPDU'", "'ExtractDataFromNativeAPDU'", 
			"'SearchAPDUStatusCodes'", "'SearchAPDUInsCodes'", "'SearchAPDUClaCodes'", 
			null, "'RandomBytes'", "'RandomInt32'", "'GetCRC16'", "'AppendCRC16'", 
			"'CheckCRC16'", "'GetCommonKeys'", "'GetUserKeys'", null, "'GetTimestamp'", 
			"'MemoryXOR'", "'Max'", "'Min'", "'Reverse'", "'PadLeft'", "'PadRight'", 
			"'GetSubarray'", "'GetConstantString'", "'GetConstantArray'", "'IntegerRange'", 
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
			null, "FunctionArgInnerSeparator", "FunctionStartArgsDelimiter", "FunctionEndArgsDelimiter", 
			"ScriptingAPIFunctionName", "ExitFuncName", "ScriptControlFlowFunctions", 
			"PrintFuncName", "PrintfFuncName", "PrintingAndLoggingFunctions", "IsChameleonConnectedFuncName", 
			"IsChameleonRevGFuncName", "IsChameleonRevEFuncName", "ChameleonConnectionTypeFunctions", 
			"AsHexStringFuncName", "AsBinaryStringFuncName", "AsByteArrayFuncName", 
			"GetLengthFuncName", "GetTypeFuncName", "ToStringFuncName", "VariableTypeFunctions", 
			"AssertFuncName", "DebuggingFunctions", "GetEnvFuncName", "EnvironmentFunctions", 
			"CmdGetResponseFuncName", "CmdGetResponseCodeFuncName", "CmdGetResponseDescFuncName", 
			"GetResponseDataFuncName", "CmdIsSuccessFuncName", "CmdIsErrorFuncName", 
			"CmdContainsDataFuncName", "CmdSaveDeviceStateFuncName", "CmdRestoreDeviceStateFuncName", 
			"CmdDownloadTagFuncName", "CmdUploadTagFuncName", "CmdDownloadLogsFuncName", 
			"ChameleonCommandAndLogFunctions", "StringSearchFuncName", "StringContainsFuncName", 
			"StringReplaceFuncName", "StringSplitFuncName", "StringStripFuncName", 
			"SubstrFuncName", "StringFunctions", "AsWrappedAPDUFuncName", "ExtractDataFromWrappedAPDUFuncName", 
			"ExtractDataFromNativeAPDUFuncName", "SearchAPDUCStatusCodesFuncName", 
			"SearchAPDUInsCodesFuncName", "SearchAPDUClaCodesFuncName", "APDUHandlingFunctions", 
			"GetRandomBytesFuncName", "GetRandomIntFuncName", "GetCRC16FuncName", 
			"AppendCRC16FuncName", "CheckCRC16FuncName", "GetCommonKeysFuncName", 
			"GetUserKeysFuncName", "CryptoAndHashFunctions", "GetTimestampFuncName", 
			"MemoryXORFuncName", "MaxFuncName", "MinFuncName", "ArrayReverseFuncName", 
			"ArrayPadLeftFuncName", "ArrayPadRightFuncName", "GetSubarrayFuncName", 
			"GetConstantStringFuncName", "GetConstantByteArrayFuncName", "GetIntegersFromRangeFuncName", 
			"UtilityFunctions", "WhiteSpaceText", "WhiteSpace", "NewLineBreak", "CStyleBlockComment", 
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
	public String getGrammarFileName() { return "ScriptingAPI.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ScriptingAPIParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class Function_args_listContext extends ParserRuleContext {
		public List<ScriptVariable> varsList;
		public Expression_eval_termContext var;
		public Function_args_listContext argsList;
		public TerminalNode FunctionArgInnerSeparator() { return getToken(ScriptingAPIParser.FunctionArgInnerSeparator, 0); }
		public Expression_eval_termContext expression_eval_term() {
			return getRuleContext(Expression_eval_termContext.class,0);
		}
		public Function_args_listContext function_args_list() {
			return getRuleContext(Function_args_listContext.class,0);
		}
		public Function_args_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_args_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).enterFunction_args_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).exitFunction_args_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingAPIVisitor ) return ((ScriptingAPIVisitor<? extends T>)visitor).visitFunction_args_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Function_args_listContext function_args_list() throws RecognitionException {
		Function_args_listContext _localctx = new Function_args_listContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_function_args_list);
		try {
			setState(29);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(20);
				((Function_args_listContext)_localctx).var = expression_eval_term();
				setState(21);
				match(FunctionArgInnerSeparator);
				setState(22);
				((Function_args_listContext)_localctx).argsList = function_args_list();

				          ((Function_args_listContext)_localctx).argsList.varsList.add(((Function_args_listContext)_localctx).var.svar);
				          ((Function_args_listContext)_localctx).varsList = ((Function_args_listContext)_localctx).argsList.varsList;
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(25);
				((Function_args_listContext)_localctx).var = expression_eval_term();

				          ((Function_args_listContext)_localctx).varsList = new ArrayList<ScriptVariable>();
				          _localctx.varsList.add(((Function_args_listContext)_localctx).var.svar);
				     
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{

				          ((Function_args_listContext)_localctx).varsList = new ArrayList<ScriptVariable>();
				     
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

	public static class Scripting_api_functionContext extends ParserRuleContext {
		public ScriptVariable funcResult;
		public Token funcName;
		public Function_args_listContext funcArgs;
		public TerminalNode FunctionStartArgsDelimiter() { return getToken(ScriptingAPIParser.FunctionStartArgsDelimiter, 0); }
		public TerminalNode FunctionEndArgsDelimiter() { return getToken(ScriptingAPIParser.FunctionEndArgsDelimiter, 0); }
		public TerminalNode ScriptingAPIFunctionName() { return getToken(ScriptingAPIParser.ScriptingAPIFunctionName, 0); }
		public Function_args_listContext function_args_list() {
			return getRuleContext(Function_args_listContext.class,0);
		}
		public Scripting_api_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scripting_api_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).enterScripting_api_function(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).exitScripting_api_function(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingAPIVisitor ) return ((ScriptingAPIVisitor<? extends T>)visitor).visitScripting_api_function(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Scripting_api_functionContext scripting_api_function() throws RecognitionException {
		Scripting_api_functionContext _localctx = new Scripting_api_functionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_scripting_api_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(31);
			((Scripting_api_functionContext)_localctx).funcName = match(ScriptingAPIFunctionName);
			setState(32);
			match(FunctionStartArgsDelimiter);
			setState(33);
			((Scripting_api_functionContext)_localctx).funcArgs = function_args_list();
			setState(34);
			match(FunctionEndArgsDelimiter);

			          ((Scripting_api_functionContext)_localctx).funcResult = ScriptingFunctions.callFunction((((Scripting_api_functionContext)_localctx).funcName!=null?((Scripting_api_functionContext)_localctx).funcName.getText():null), ((Scripting_api_functionContext)_localctx).funcArgs.varsList);
			     
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

	public static class Type_literalContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Token hs;
		public Token hb;
		public Token hl;
		public Token bl;
		public Token sl;
		public Token rs;
		public TerminalNode HexString() { return getToken(ScriptingAPIParser.HexString, 0); }
		public TerminalNode HexByte() { return getToken(ScriptingAPIParser.HexByte, 0); }
		public TerminalNode HexLiteral() { return getToken(ScriptingAPIParser.HexLiteral, 0); }
		public TerminalNode BooleanLiteral() { return getToken(ScriptingAPIParser.BooleanLiteral, 0); }
		public TerminalNode StringLiteral() { return getToken(ScriptingAPIParser.StringLiteral, 0); }
		public TerminalNode HexStringLiteral() { return getToken(ScriptingAPIParser.HexStringLiteral, 0); }
		public TerminalNode RawStringLiteral() { return getToken(ScriptingAPIParser.RawStringLiteral, 0); }
		public Type_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).enterType_literal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).exitType_literal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingAPIVisitor ) return ((ScriptingAPIVisitor<? extends T>)visitor).visitType_literal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_literalContext type_literal() throws RecognitionException {
		Type_literalContext _localctx = new Type_literalContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_type_literal);
		try {
			setState(51);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case HexString:
				enterOuterAlt(_localctx, 1);
				{
				setState(37);
				((Type_literalContext)_localctx).hs = match(HexString);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.parseHexString((((Type_literalContext)_localctx).hs!=null?((Type_literalContext)_localctx).hs.getText():null)); 
				}
				break;
			case HexByte:
				enterOuterAlt(_localctx, 2);
				{
				setState(39);
				((Type_literalContext)_localctx).hb = match(HexByte);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.parseInt((((Type_literalContext)_localctx).hb!=null?((Type_literalContext)_localctx).hb.getText():null)); 
				}
				break;
			case HexLiteral:
				enterOuterAlt(_localctx, 3);
				{
				setState(41);
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
				setState(43);
				((Type_literalContext)_localctx).bl = match(BooleanLiteral);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.parseBoolean((((Type_literalContext)_localctx).bl!=null?((Type_literalContext)_localctx).bl.getText():null)); 
				}
				break;
			case StringLiteral:
				enterOuterAlt(_localctx, 5);
				{
				setState(45);
				((Type_literalContext)_localctx).sl = match(StringLiteral);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.newInstance().set((((Type_literalContext)_localctx).sl!=null?((Type_literalContext)_localctx).sl.getText():null)); 
				}
				break;
			case HexStringLiteral:
				enterOuterAlt(_localctx, 6);
				{
				setState(47);
				((Type_literalContext)_localctx).hs = match(HexStringLiteral);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.parseHexString((((Type_literalContext)_localctx).hs!=null?((Type_literalContext)_localctx).hs.getText():null)); 
				}
				break;
			case RawStringLiteral:
				enterOuterAlt(_localctx, 7);
				{
				setState(49);
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
		public TerminalNode VariableStartSymbol() { return getToken(ScriptingAPIParser.VariableStartSymbol, 0); }
		public TerminalNode VariableName() { return getToken(ScriptingAPIParser.VariableName, 0); }
		public Variable_referenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable_reference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).enterVariable_reference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).exitVariable_reference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingAPIVisitor ) return ((ScriptingAPIVisitor<? extends T>)visitor).visitVariable_reference(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Variable_referenceContext variable_reference() throws RecognitionException {
		Variable_referenceContext _localctx = new Variable_referenceContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_variable_reference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			match(VariableStartSymbol);
			setState(54);
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
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).enterOperand_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).exitOperand_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingAPIVisitor ) return ((ScriptingAPIVisitor<? extends T>)visitor).visitOperand_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operand_expressionContext operand_expression() throws RecognitionException {
		Operand_expressionContext _localctx = new Operand_expressionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_operand_expression);
		try {
			setState(63);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VariableStartSymbol:
				enterOuterAlt(_localctx, 1);
				{
				setState(57);
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
				setState(60);
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
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).enterExpression_eval_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).exitExpression_eval_term(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingAPIVisitor ) return ((ScriptingAPIVisitor<? extends T>)visitor).visitExpression_eval_term(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Expression_eval_termContext expression_eval_term() throws RecognitionException {
		Expression_eval_termContext _localctx = new Expression_eval_termContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_expression_eval_term);
		try {
			setState(83);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(65);
				((Expression_eval_termContext)_localctx).vr = variable_reference();

				          ((Expression_eval_termContext)_localctx).svar = ((Expression_eval_termContext)_localctx).vr.svar;
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(68);
				((Expression_eval_termContext)_localctx).tl = type_literal();

				          ((Expression_eval_termContext)_localctx).svar = ((Expression_eval_termContext)_localctx).tl.svar;
				     
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(71);
				((Expression_eval_termContext)_localctx).bvOp = boolean_valued_operation();

				          ((Expression_eval_termContext)_localctx).svar = ScriptVariable.newInstance().set(((Expression_eval_termContext)_localctx).bvOp.opResult);
				     
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(74);
				((Expression_eval_termContext)_localctx).otherOp = other_operation_result();

				          ((Expression_eval_termContext)_localctx).svar = ((Expression_eval_termContext)_localctx).otherOp.svar;
				     
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(77);
				((Expression_eval_termContext)_localctx).aOp = assignment_operation();

				          ((Expression_eval_termContext)_localctx).svar = ((Expression_eval_termContext)_localctx).aOp.svar;
				     
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(80);
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
		public TerminalNode EqualsComparisonOperator() { return getToken(ScriptingAPIParser.EqualsComparisonOperator, 0); }
		public List<Operand_expressionContext> operand_expression() {
			return getRuleContexts(Operand_expressionContext.class);
		}
		public Operand_expressionContext operand_expression(int i) {
			return getRuleContext(Operand_expressionContext.class,i);
		}
		public TerminalNode NotEqualsComparisonOperator() { return getToken(ScriptingAPIParser.NotEqualsComparisonOperator, 0); }
		public TerminalNode LogicalAndOperator() { return getToken(ScriptingAPIParser.LogicalAndOperator, 0); }
		public TerminalNode LogicalOrOperator() { return getToken(ScriptingAPIParser.LogicalOrOperator, 0); }
		public TerminalNode LogicalNotOperator() { return getToken(ScriptingAPIParser.LogicalNotOperator, 0); }
		public Boolean_valued_operationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolean_valued_operation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).enterBoolean_valued_operation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).exitBoolean_valued_operation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingAPIVisitor ) return ((ScriptingAPIVisitor<? extends T>)visitor).visitBoolean_valued_operation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Boolean_valued_operationContext boolean_valued_operation() throws RecognitionException {
		Boolean_valued_operationContext _localctx = new Boolean_valued_operationContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_boolean_valued_operation);
		try {
			setState(109);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(85);
				((Boolean_valued_operationContext)_localctx).lhs = operand_expression();
				setState(86);
				match(EqualsComparisonOperator);
				setState(87);
				((Boolean_valued_operationContext)_localctx).rhs = operand_expression();

				          ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsBoolean() == ((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsBoolean());
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(90);
				((Boolean_valued_operationContext)_localctx).lhs = operand_expression();
				setState(91);
				match(NotEqualsComparisonOperator);
				setState(92);
				((Boolean_valued_operationContext)_localctx).rhs = operand_expression();

				          ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsBoolean() != ((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsBoolean());
				     
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(95);
				((Boolean_valued_operationContext)_localctx).lhs = operand_expression();
				setState(96);
				match(LogicalAndOperator);
				setState(97);
				((Boolean_valued_operationContext)_localctx).rhs = operand_expression();

				          ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsBoolean() && ((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsBoolean());
				     
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(100);
				((Boolean_valued_operationContext)_localctx).lhs = operand_expression();
				setState(101);
				match(LogicalOrOperator);
				setState(102);
				((Boolean_valued_operationContext)_localctx).rhs = operand_expression();

				          ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsBoolean() || ((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsBoolean());
				     
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(105);
				match(LogicalNotOperator);
				setState(106);
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
		public TerminalNode LeftShiftOperator() { return getToken(ScriptingAPIParser.LeftShiftOperator, 0); }
		public List<Operand_expressionContext> operand_expression() {
			return getRuleContexts(Operand_expressionContext.class);
		}
		public Operand_expressionContext operand_expression(int i) {
			return getRuleContext(Operand_expressionContext.class,i);
		}
		public TerminalNode RightShiftOperator() { return getToken(ScriptingAPIParser.RightShiftOperator, 0); }
		public TerminalNode BitwiseAndOperator() { return getToken(ScriptingAPIParser.BitwiseAndOperator, 0); }
		public TerminalNode BitwiseOrOperator() { return getToken(ScriptingAPIParser.BitwiseOrOperator, 0); }
		public TerminalNode BitwiseXorOperator() { return getToken(ScriptingAPIParser.BitwiseXorOperator, 0); }
		public TerminalNode BitwiseNotOperator() { return getToken(ScriptingAPIParser.BitwiseNotOperator, 0); }
		public TerminalNode TernaryOperatorFirstSymbol() { return getToken(ScriptingAPIParser.TernaryOperatorFirstSymbol, 0); }
		public TerminalNode TernaryOperatorSecondSymbol() { return getToken(ScriptingAPIParser.TernaryOperatorSecondSymbol, 0); }
		public Other_operation_resultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_other_operation_result; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).enterOther_operation_result(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).exitOther_operation_result(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingAPIVisitor ) return ((ScriptingAPIVisitor<? extends T>)visitor).visitOther_operation_result(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Other_operation_resultContext other_operation_result() throws RecognitionException {
		Other_operation_resultContext _localctx = new Other_operation_resultContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_other_operation_result);
		try {
			setState(147);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(111);
				((Other_operation_resultContext)_localctx).lhs = operand_expression();
				setState(112);
				match(LeftShiftOperator);
				setState(113);
				((Other_operation_resultContext)_localctx).rhs = operand_expression();

				          ((Other_operation_resultContext)_localctx).svar = ((Other_operation_resultContext)_localctx).lhs.svar.binaryOperation(ScriptVariable.BinaryOperation.BINOP_SHIFT_LEFT, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(116);
				((Other_operation_resultContext)_localctx).lhs = operand_expression();
				setState(117);
				match(RightShiftOperator);
				setState(118);
				((Other_operation_resultContext)_localctx).rhs = operand_expression();

				          ((Other_operation_resultContext)_localctx).svar = ((Other_operation_resultContext)_localctx).lhs.svar.binaryOperation(ScriptVariable.BinaryOperation.BINOP_SHIFT_RIGHT, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(121);
				((Other_operation_resultContext)_localctx).lhs = operand_expression();
				setState(122);
				match(BitwiseAndOperator);
				setState(123);
				((Other_operation_resultContext)_localctx).rhs = operand_expression();

				          ((Other_operation_resultContext)_localctx).svar = ((Other_operation_resultContext)_localctx).lhs.svar.binaryOperation(ScriptVariable.BinaryOperation.BINOP_BITWISE_AND, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(126);
				((Other_operation_resultContext)_localctx).lhs = operand_expression();
				setState(127);
				match(BitwiseOrOperator);
				setState(128);
				((Other_operation_resultContext)_localctx).rhs = operand_expression();

				          ((Other_operation_resultContext)_localctx).svar = ((Other_operation_resultContext)_localctx).lhs.svar.binaryOperation(ScriptVariable.BinaryOperation.BINOP_BITWISE_OR, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(131);
				((Other_operation_resultContext)_localctx).lhs = operand_expression();
				setState(132);
				match(BitwiseXorOperator);
				setState(133);
				((Other_operation_resultContext)_localctx).rhs = operand_expression();

				          ((Other_operation_resultContext)_localctx).svar = ((Other_operation_resultContext)_localctx).lhs.svar.binaryOperation(ScriptVariable.BinaryOperation.BINOP_BITWISE_XOR, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(136);
				match(BitwiseNotOperator);
				setState(137);
				((Other_operation_resultContext)_localctx).rhs = operand_expression();

				          ((Other_operation_resultContext)_localctx).svar = ((Other_operation_resultContext)_localctx).rhs.svar.unaryOperation(ScriptVariable.UnaryOperation.UOP_BITWISE_NOT);
				     
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(140);
				((Other_operation_resultContext)_localctx).cond = operand_expression();
				setState(141);
				match(TernaryOperatorFirstSymbol);
				setState(142);
				((Other_operation_resultContext)_localctx).vtrue = operand_expression();
				setState(143);
				match(TernaryOperatorSecondSymbol);
				setState(144);
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
		public TerminalNode DefEqualsOperator() { return getToken(ScriptingAPIParser.DefEqualsOperator, 0); }
		public Variable_referenceContext variable_reference() {
			return getRuleContext(Variable_referenceContext.class,0);
		}
		public Operand_expressionContext operand_expression() {
			return getRuleContext(Operand_expressionContext.class,0);
		}
		public TerminalNode PlusEqualsOperator() { return getToken(ScriptingAPIParser.PlusEqualsOperator, 0); }
		public Assignment_operationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment_operation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).enterAssignment_operation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).exitAssignment_operation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingAPIVisitor ) return ((ScriptingAPIVisitor<? extends T>)visitor).visitAssignment_operation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Assignment_operationContext assignment_operation() throws RecognitionException {
		Assignment_operationContext _localctx = new Assignment_operationContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_assignment_operation);
		try {
			setState(159);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(149);
				((Assignment_operationContext)_localctx).lhs = variable_reference();
				setState(150);
				match(DefEqualsOperator);
				setState(151);
				((Assignment_operationContext)_localctx).rhs = operand_expression();

				          ((Assignment_operationContext)_localctx).svar = ((Assignment_operationContext)_localctx).rhs.svar;
				          ChameleonScripting.getRunningInstance().setVariableByName(((Assignment_operationContext)_localctx).lhs.svar.getName());
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(154);
				((Assignment_operationContext)_localctx).lhs = variable_reference();
				setState(155);
				match(PlusEqualsOperator);
				setState(156);
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
		public TerminalNode TypeCastByte() { return getToken(ScriptingAPIParser.TypeCastByte, 0); }
		public Operand_expressionContext operand_expression() {
			return getRuleContext(Operand_expressionContext.class,0);
		}
		public TerminalNode TypeCastShort() { return getToken(ScriptingAPIParser.TypeCastShort, 0); }
		public TerminalNode TypeCastInt32() { return getToken(ScriptingAPIParser.TypeCastInt32, 0); }
		public TerminalNode TypeCastBoolean() { return getToken(ScriptingAPIParser.TypeCastBoolean, 0); }
		public TerminalNode TypeCastString() { return getToken(ScriptingAPIParser.TypeCastString, 0); }
		public Typecast_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typecast_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).enterTypecast_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ScriptingAPIListener ) ((ScriptingAPIListener)listener).exitTypecast_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ScriptingAPIVisitor ) return ((ScriptingAPIVisitor<? extends T>)visitor).visitTypecast_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Typecast_expressionContext typecast_expression() throws RecognitionException {
		Typecast_expressionContext _localctx = new Typecast_expressionContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_typecast_expression);
		try {
			setState(181);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TypeCastByte:
				enterOuterAlt(_localctx, 1);
				{
				setState(161);
				match(TypeCastByte);
				setState(162);
				((Typecast_expressionContext)_localctx).initVar = operand_expression();

				          ((Typecast_expressionContext)_localctx).svar = ((Typecast_expressionContext)_localctx).initVar.svar.getAsByte();
				     
				}
				break;
			case TypeCastShort:
				enterOuterAlt(_localctx, 2);
				{
				setState(165);
				match(TypeCastShort);
				setState(166);
				((Typecast_expressionContext)_localctx).initVar = operand_expression();

				          ((Typecast_expressionContext)_localctx).svar = ((Typecast_expressionContext)_localctx).initVar.svar.getAsShort();
				     
				}
				break;
			case TypeCastInt32:
				enterOuterAlt(_localctx, 3);
				{
				setState(169);
				match(TypeCastInt32);
				setState(170);
				((Typecast_expressionContext)_localctx).initVar = operand_expression();

				          ((Typecast_expressionContext)_localctx).svar = ((Typecast_expressionContext)_localctx).initVar.svar.getAsInteger();
				     
				}
				break;
			case TypeCastBoolean:
				enterOuterAlt(_localctx, 4);
				{
				setState(173);
				match(TypeCastBoolean);
				setState(174);
				((Typecast_expressionContext)_localctx).initVar = operand_expression();

				          ((Typecast_expressionContext)_localctx).svar = ((Typecast_expressionContext)_localctx).initVar.svar.getAsBoolean();
				     
				}
				break;
			case TypeCastString:
				enterOuterAlt(_localctx, 5);
				{
				setState(177);
				match(TypeCastString);
				setState(178);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3{\u00ba\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2 \n\2\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\66\n\4"+
		"\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\5\6B\n\6\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7V\n\7\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\5\bp\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t\u0096\n\t\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\5\n\u00a2\n\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13\u00b8"+
		"\n\13\3\13\2\2\f\2\4\6\b\n\f\16\20\22\24\2\2\2\u00cc\2\37\3\2\2\2\4!\3"+
		"\2\2\2\6\65\3\2\2\2\b\67\3\2\2\2\nA\3\2\2\2\fU\3\2\2\2\16o\3\2\2\2\20"+
		"\u0095\3\2\2\2\22\u00a1\3\2\2\2\24\u00b7\3\2\2\2\26\27\5\f\7\2\27\30\7"+
		"\3\2\2\30\31\5\2\2\2\31\32\b\2\1\2\32 \3\2\2\2\33\34\5\f\7\2\34\35\b\2"+
		"\1\2\35 \3\2\2\2\36 \b\2\1\2\37\26\3\2\2\2\37\33\3\2\2\2\37\36\3\2\2\2"+
		" \3\3\2\2\2!\"\7\6\2\2\"#\7\4\2\2#$\5\2\2\2$%\7\5\2\2%&\b\3\1\2&\5\3\2"+
		"\2\2\'(\7R\2\2(\66\b\4\1\2)*\7S\2\2*\66\b\4\1\2+,\7T\2\2,\66\b\4\1\2-"+
		".\7U\2\2.\66\b\4\1\2/\60\7W\2\2\60\66\b\4\1\2\61\62\7X\2\2\62\66\b\4\1"+
		"\2\63\64\7Y\2\2\64\66\b\4\1\2\65\'\3\2\2\2\65)\3\2\2\2\65+\3\2\2\2\65"+
		"-\3\2\2\2\65/\3\2\2\2\65\61\3\2\2\2\65\63\3\2\2\2\66\7\3\2\2\2\678\7`"+
		"\2\289\7a\2\29:\b\5\1\2:\t\3\2\2\2;<\5\b\5\2<=\b\6\1\2=B\3\2\2\2>?\5\6"+
		"\4\2?@\b\6\1\2@B\3\2\2\2A;\3\2\2\2A>\3\2\2\2B\13\3\2\2\2CD\5\b\5\2DE\b"+
		"\7\1\2EV\3\2\2\2FG\5\6\4\2GH\b\7\1\2HV\3\2\2\2IJ\5\16\b\2JK\b\7\1\2KV"+
		"\3\2\2\2LM\5\20\t\2MN\b\7\1\2NV\3\2\2\2OP\5\22\n\2PQ\b\7\1\2QV\3\2\2\2"+
		"RS\5\24\13\2ST\b\7\1\2TV\3\2\2\2UC\3\2\2\2UF\3\2\2\2UI\3\2\2\2UL\3\2\2"+
		"\2UO\3\2\2\2UR\3\2\2\2V\r\3\2\2\2WX\5\n\6\2XY\7b\2\2YZ\5\n\6\2Z[\b\b\1"+
		"\2[p\3\2\2\2\\]\5\n\6\2]^\7c\2\2^_\5\n\6\2_`\b\b\1\2`p\3\2\2\2ab\5\n\6"+
		"\2bc\7d\2\2cd\5\n\6\2de\b\b\1\2ep\3\2\2\2fg\5\n\6\2gh\7e\2\2hi\5\n\6\2"+
		"ij\b\b\1\2jp\3\2\2\2kl\7f\2\2lm\5\n\6\2mn\b\b\1\2np\3\2\2\2oW\3\2\2\2"+
		"o\\\3\2\2\2oa\3\2\2\2of\3\2\2\2ok\3\2\2\2p\17\3\2\2\2qr\5\n\6\2rs\7h\2"+
		"\2st\5\n\6\2tu\b\t\1\2u\u0096\3\2\2\2vw\5\n\6\2wx\7g\2\2xy\5\n\6\2yz\b"+
		"\t\1\2z\u0096\3\2\2\2{|\5\n\6\2|}\7i\2\2}~\5\n\6\2~\177\b\t\1\2\177\u0096"+
		"\3\2\2\2\u0080\u0081\5\n\6\2\u0081\u0082\7j\2\2\u0082\u0083\5\n\6\2\u0083"+
		"\u0084\b\t\1\2\u0084\u0096\3\2\2\2\u0085\u0086\5\n\6\2\u0086\u0087\7k"+
		"\2\2\u0087\u0088\5\n\6\2\u0088\u0089\b\t\1\2\u0089\u0096\3\2\2\2\u008a"+
		"\u008b\7l\2\2\u008b\u008c\5\n\6\2\u008c\u008d\b\t\1\2\u008d\u0096\3\2"+
		"\2\2\u008e\u008f\5\n\6\2\u008f\u0090\7m\2\2\u0090\u0091\5\n\6\2\u0091"+
		"\u0092\7n\2\2\u0092\u0093\5\n\6\2\u0093\u0094\b\t\1\2\u0094\u0096\3\2"+
		"\2\2\u0095q\3\2\2\2\u0095v\3\2\2\2\u0095{\3\2\2\2\u0095\u0080\3\2\2\2"+
		"\u0095\u0085\3\2\2\2\u0095\u008a\3\2\2\2\u0095\u008e\3\2\2\2\u0096\21"+
		"\3\2\2\2\u0097\u0098\5\b\5\2\u0098\u0099\7o\2\2\u0099\u009a\5\n\6\2\u009a"+
		"\u009b\b\n\1\2\u009b\u00a2\3\2\2\2\u009c\u009d\5\b\5\2\u009d\u009e\7p"+
		"\2\2\u009e\u009f\5\n\6\2\u009f\u00a0\b\n\1\2\u00a0\u00a2\3\2\2\2\u00a1"+
		"\u0097\3\2\2\2\u00a1\u009c\3\2\2\2\u00a2\23\3\2\2\2\u00a3\u00a4\7q\2\2"+
		"\u00a4\u00a5\5\n\6\2\u00a5\u00a6\b\13\1\2\u00a6\u00b8\3\2\2\2\u00a7\u00a8"+
		"\7r\2\2\u00a8\u00a9\5\n\6\2\u00a9\u00aa\b\13\1\2\u00aa\u00b8\3\2\2\2\u00ab"+
		"\u00ac\7s\2\2\u00ac\u00ad\5\n\6\2\u00ad\u00ae\b\13\1\2\u00ae\u00b8\3\2"+
		"\2\2\u00af\u00b0\7t\2\2\u00b0\u00b1\5\n\6\2\u00b1\u00b2\b\13\1\2\u00b2"+
		"\u00b8\3\2\2\2\u00b3\u00b4\7u\2\2\u00b4\u00b5\5\n\6\2\u00b5\u00b6\b\13"+
		"\1\2\u00b6\u00b8\3\2\2\2\u00b7\u00a3\3\2\2\2\u00b7\u00a7\3\2\2\2\u00b7"+
		"\u00ab\3\2\2\2\u00b7\u00af\3\2\2\2\u00b7\u00b3\3\2\2\2\u00b8\25\3\2\2"+
		"\2\n\37\65AUo\u0095\u00a1\u00b7";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}