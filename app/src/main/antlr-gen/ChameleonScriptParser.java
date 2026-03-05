// Generated from app/src/main/java/com/maxieds/chameleonminilivedebugger/ScriptingAPI/ChameleonScriptParser.g4 by ANTLR 4.13.2
package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

     import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariable;
     import com.maxieds.chameleonminilivedebugger.AndroidLogger;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ChameleonScriptParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ScriptingAPIFunctionName=1, ExitFuncName=2, AssertFuncName=3, ScriptControlFlowFunctions=4, 
		PrintFuncName=5, PrintfFuncName=6, SprintfFuncName=7, PrintingAndLoggingFunctions=8, 
		AsHexStringFuncName=9, AsBinaryStringFuncName=10, AsByteArrayFuncName=11, 
		AsWrappedAPDUFuncName=12, GetLengthFuncName=13, GetEnvFuncName=14, VariableTypeFunctions=15, 
		IsChameleonConnectedFuncName=16, IsChameleonRevGFuncName=17, IsChameleonRevEFuncName=18, 
		GetChameleonDescFuncName=19, ChameleonConnectionTypeFunctions=20, CmdDownloadTagFuncName=21, 
		CmdUploadTagFuncName=22, CmdDownloadLogsFuncName=23, ChameleonCommandAndLogFunctions=24, 
		StringSearchFuncName=25, StringContainsFuncName=26, StringReplaceFuncName=27, 
		StringCatFuncName=28, StringSplitFuncName=29, StringStripFuncName=30, 
		SubstrFuncName=31, StringFunctions=32, ExtractDataFromWrappedAPDUFuncName=33, 
		ExtractDataFromNativeAPDUFuncName=34, SplitWrappedAPDUFuncName=35, SearchAPDUCStatusCodesFuncName=36, 
		SearchAPDUInsCodesFuncName=37, SearchAPDUClaCodesFuncName=38, APDUHandlingFunctions=39, 
		GetRandomBytesFuncName=40, GetRandomIntFuncName=41, GetCRC16FuncName=42, 
		AppendCRC16FuncName=43, CheckCRC16FuncName=44, GetCommonKeysFuncName=45, 
		GetUserKeysFuncName=46, CryptoAndHashFunctions=47, GetTimestampFuncName=48, 
		MemoryXORFuncName=49, MaxFuncName=50, MinFuncName=51, ArrayReverseFuncName=52, 
		ArrayPadLeftFuncName=53, ArrayPadRightFuncName=54, GetSubarrayFuncName=55, 
		ArrayToStringFuncName=56, GetConstantStringFuncName=57, GetConstantByteArrayFuncName=58, 
		GetIntegersFromRangeFuncName=59, UtilityFunctions=60, While=61, IfCond=62, 
		ElseCond=63, QuotedStringLiteral=64, QuotedHexStringLiteral=65, QuotedRawStringLiteral=66, 
		WhiteSpace=67, NewLine=68, CStyleBlockComment=69, CStyleLineComment=70, 
		HashStyleLineComment=71, EqualsComparisonOperator=72, NotEqualsComparisonOperator=73, 
		PlusEqualsOperator=74, DefEqualsOperator=75, ExecCommandStartSymbol=76, 
		TernaryOperatorFirstSymbol=77, HashedIndexAccessor=78, MinusSign=79, CommaSeparator=80, 
		OpenParens=81, ClosedParens=82, ColonSeparator=83, DoubleOpenCurlyBrace=84, 
		OpenBrace=85, DoubleClosedCurlyBrace=86, ClosedBrace=87, LogicalAndOperator=88, 
		LogicalOrOperator=89, LogicalNotOperator=90, RightShiftOperator=91, LeftShiftOperator=92, 
		BitwiseAndOperator=93, BitwiseOrOperator=94, BitwiseXorOperator=95, BitwiseNotOperator=96, 
		ArithmeticPlusOperator=97, TypeCastByte=98, TypeCastBytes=99, TypeCastShort=100, 
		TypeCastInt32=101, TypeCastBoolean=102, TypeCastString=103, ArrayIndexOpenBracket=104, 
		ArrayIndexCloseBracket=105, VariableStartSymbol=106, VariableNameStartChar=107, 
		VariableName=108, DecimalLiteral=109, HexString=110, HexByte=111, HexLiteral=112, 
		BooleanLiteral=113, LabelText=114;
	public static final int
		RULE_file_contents = 0, RULE_script_line = 1, RULE_script_line_block = 2, 
		RULE_while_loop = 3, RULE_if_block = 4, RULE_ifelse_block = 5, RULE_variable_reference_v1 = 6, 
		RULE_variable_reference_v2 = 7, RULE_variable_reference = 8, RULE_type_literal = 9, 
		RULE_quoted_string_literal = 10, RULE_byte_literal_list = 11, RULE_operand_expression_v1 = 12, 
		RULE_typecast_expression = 13, RULE_operand_expression_v2 = 14, RULE_other_operation_result = 15, 
		RULE_operand_expression_v3 = 16, RULE_boolean_valued_operation = 17, RULE_operand_expression_v4 = 18, 
		RULE_extract_expression_from_array_index = 19, RULE_operand_expression_v5 = 20, 
		RULE_extract_expression_from_array_slice = 21, RULE_operand_expression_v6 = 22, 
		RULE_exec_chameleon_command = 23, RULE_scripting_api_function_result = 24, 
		RULE_operand_expression_v7 = 25, RULE_array_literal_list = 26, RULE_operand_expression_v72 = 27, 
		RULE_assignment_operation = 28, RULE_operand_expression_v8 = 29, RULE_ternary_operator_expression = 30, 
		RULE_operand_expression_v9 = 31, RULE_operand_expression = 32, RULE_assignment_by_array_slice = 33, 
		RULE_function_args_list = 34, RULE_label_statement = 35;
	private static String[] makeRuleNames() {
		return new String[] {
			"file_contents", "script_line", "script_line_block", "while_loop", "if_block", 
			"ifelse_block", "variable_reference_v1", "variable_reference_v2", "variable_reference", 
			"type_literal", "quoted_string_literal", "byte_literal_list", "operand_expression_v1", 
			"typecast_expression", "operand_expression_v2", "other_operation_result", 
			"operand_expression_v3", "boolean_valued_operation", "operand_expression_v4", 
			"extract_expression_from_array_index", "operand_expression_v5", "extract_expression_from_array_slice", 
			"operand_expression_v6", "exec_chameleon_command", "scripting_api_function_result", 
			"operand_expression_v7", "array_literal_list", "operand_expression_v72", 
			"assignment_operation", "operand_expression_v8", "ternary_operator_expression", 
			"operand_expression_v9", "operand_expression", "assignment_by_array_slice", 
			"function_args_list", "label_statement"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'Exit('", "'Assert('", null, "'Print('", "'Printf('", "'Sprintf('", 
			null, "'AsHexString('", "'AsBinaryString('", "'AsByteArray('", "'AsWrappedAPDU('", 
			"'GetLength('", "'GetEnv('", null, "'IsChameleonConnected('", "'IsChameleonRevG('", 
			"'IsChameleonRevE('", "'GetChameleonDesc('", null, "'DownloadTagDump('", 
			"'UploadTagDump('", "'DownloadLogs('", null, "'StringFind('", "'StringContains('", 
			"'StringReplace('", "'Strcat('", "'StringSplit('", "'StringStrip('", 
			"'Substr('", null, "'ExtractDataFromWrappedAPDU('", "'ExtractDataFromNativeAPDU('", 
			"'SplitAPDUResponse('", "'SearchAPDUStatusCodes('", "'SearchAPDUInsCodes('", 
			"'SearchAPDUClaCodes('", null, "'RandomBytes('", "'RandomInt32('", "'GetCRC16('", 
			"'AppendCRC16('", "'CheckCRC16('", "'GetCommonKeys('", "'GetUserKeys('", 
			null, "'GetTimestamp('", "'MemoryXOR('", "'Max('", "'Min('", "'Reverse('", 
			"'PadLeft('", "'PadRight('", "'GetSubarray('", "'ArrayToString('", "'GetConstantString('", 
			"'GetConstantArray('", "'IntegerRange('", null, "'while'", "'if'", "'else'", 
			null, null, null, null, null, null, null, null, "'=='", "'!='", "'+='", 
			null, "'$$('", "'?'", "'->'", "'-'", "','", "'('", "')'", "':'", "'{{'", 
			"'{'", "'}}'", "'}'", null, null, null, "'>>'", "'<<'", "'&'", "'|'", 
			"'^'", "'~'", "'+'", "'(Byte)'", "'(Bytes)'", "'(Short)'", "'(Int32)'", 
			"'(Boolean)'", "'(String)'", "'['", "']'", "'$'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ScriptingAPIFunctionName", "ExitFuncName", "AssertFuncName", "ScriptControlFlowFunctions", 
			"PrintFuncName", "PrintfFuncName", "SprintfFuncName", "PrintingAndLoggingFunctions", 
			"AsHexStringFuncName", "AsBinaryStringFuncName", "AsByteArrayFuncName", 
			"AsWrappedAPDUFuncName", "GetLengthFuncName", "GetEnvFuncName", "VariableTypeFunctions", 
			"IsChameleonConnectedFuncName", "IsChameleonRevGFuncName", "IsChameleonRevEFuncName", 
			"GetChameleonDescFuncName", "ChameleonConnectionTypeFunctions", "CmdDownloadTagFuncName", 
			"CmdUploadTagFuncName", "CmdDownloadLogsFuncName", "ChameleonCommandAndLogFunctions", 
			"StringSearchFuncName", "StringContainsFuncName", "StringReplaceFuncName", 
			"StringCatFuncName", "StringSplitFuncName", "StringStripFuncName", "SubstrFuncName", 
			"StringFunctions", "ExtractDataFromWrappedAPDUFuncName", "ExtractDataFromNativeAPDUFuncName", 
			"SplitWrappedAPDUFuncName", "SearchAPDUCStatusCodesFuncName", "SearchAPDUInsCodesFuncName", 
			"SearchAPDUClaCodesFuncName", "APDUHandlingFunctions", "GetRandomBytesFuncName", 
			"GetRandomIntFuncName", "GetCRC16FuncName", "AppendCRC16FuncName", "CheckCRC16FuncName", 
			"GetCommonKeysFuncName", "GetUserKeysFuncName", "CryptoAndHashFunctions", 
			"GetTimestampFuncName", "MemoryXORFuncName", "MaxFuncName", "MinFuncName", 
			"ArrayReverseFuncName", "ArrayPadLeftFuncName", "ArrayPadRightFuncName", 
			"GetSubarrayFuncName", "ArrayToStringFuncName", "GetConstantStringFuncName", 
			"GetConstantByteArrayFuncName", "GetIntegersFromRangeFuncName", "UtilityFunctions", 
			"While", "IfCond", "ElseCond", "QuotedStringLiteral", "QuotedHexStringLiteral", 
			"QuotedRawStringLiteral", "WhiteSpace", "NewLine", "CStyleBlockComment", 
			"CStyleLineComment", "HashStyleLineComment", "EqualsComparisonOperator", 
			"NotEqualsComparisonOperator", "PlusEqualsOperator", "DefEqualsOperator", 
			"ExecCommandStartSymbol", "TernaryOperatorFirstSymbol", "HashedIndexAccessor", 
			"MinusSign", "CommaSeparator", "OpenParens", "ClosedParens", "ColonSeparator", 
			"DoubleOpenCurlyBrace", "OpenBrace", "DoubleClosedCurlyBrace", "ClosedBrace", 
			"LogicalAndOperator", "LogicalOrOperator", "LogicalNotOperator", "RightShiftOperator", 
			"LeftShiftOperator", "BitwiseAndOperator", "BitwiseOrOperator", "BitwiseXorOperator", 
			"BitwiseNotOperator", "ArithmeticPlusOperator", "TypeCastByte", "TypeCastBytes", 
			"TypeCastShort", "TypeCastInt32", "TypeCastBoolean", "TypeCastString", 
			"ArrayIndexOpenBracket", "ArrayIndexCloseBracket", "VariableStartSymbol", 
			"VariableNameStartChar", "VariableName", "DecimalLiteral", "HexString", 
			"HexByte", "HexLiteral", "BooleanLiteral", "LabelText"
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
	public String getGrammarFileName() { return "ChameleonScriptParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ChameleonScriptParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class File_contentsContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(ChameleonScriptParser.EOF, 0); }
		public List<Script_lineContext> script_line() {
			return getRuleContexts(Script_lineContext.class);
		}
		public Script_lineContext script_line(int i) {
			return getRuleContext(Script_lineContext.class,i);
		}
		public File_contentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file_contents; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterFile_contents(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitFile_contents(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitFile_contents(this);
			else return visitor.visitChildren(this);
		}
	}

	public final File_contentsContext file_contents() throws RecognitionException {
		File_contentsContext _localctx = new File_contentsContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_file_contents);
		int _la;
		try {
			setState(80);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ScriptingAPIFunctionName:
			case While:
			case IfCond:
			case ExecCommandStartSymbol:
			case VariableStartSymbol:
			case LabelText:
				enterOuterAlt(_localctx, 1);
				{
				setState(73); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(72);
					script_line();
					}
					}
					setState(75); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 6917529027641081858L) != 0) || ((((_la - 76)) & ~0x3f) == 0 && ((1L << (_la - 76)) & 275951648769L) != 0) );
				setState(77);
				match(EOF);
				}
				break;
			case EOF:
				enterOuterAlt(_localctx, 2);
				{
				setState(79);
				match(EOF);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Script_lineContext extends ParserRuleContext {
		public Label_statementContext label_statement() {
			return getRuleContext(Label_statementContext.class,0);
		}
		public Assignment_operationContext assignment_operation() {
			return getRuleContext(Assignment_operationContext.class,0);
		}
		public Assignment_by_array_sliceContext assignment_by_array_slice() {
			return getRuleContext(Assignment_by_array_sliceContext.class,0);
		}
		public Scripting_api_function_resultContext scripting_api_function_result() {
			return getRuleContext(Scripting_api_function_resultContext.class,0);
		}
		public Exec_chameleon_commandContext exec_chameleon_command() {
			return getRuleContext(Exec_chameleon_commandContext.class,0);
		}
		public While_loopContext while_loop() {
			return getRuleContext(While_loopContext.class,0);
		}
		public If_blockContext if_block() {
			return getRuleContext(If_blockContext.class,0);
		}
		public Ifelse_blockContext ifelse_block() {
			return getRuleContext(Ifelse_blockContext.class,0);
		}
		public Script_lineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_script_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterScript_line(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitScript_line(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitScript_line(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Script_lineContext script_line() throws RecognitionException {
		Script_lineContext _localctx = new Script_lineContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_script_line);
		try {
			setState(90);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(82);
				label_statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(83);
				assignment_operation();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(84);
				assignment_by_array_slice();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(85);
				scripting_api_function_result();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(86);
				exec_chameleon_command();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(87);
				while_loop();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(88);
				if_block();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(89);
				ifelse_block();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Script_line_blockContext extends ParserRuleContext {
		public List<Script_lineContext> script_line() {
			return getRuleContexts(Script_lineContext.class);
		}
		public Script_lineContext script_line(int i) {
			return getRuleContext(Script_lineContext.class,i);
		}
		public Script_line_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_script_line_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterScript_line_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitScript_line_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitScript_line_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Script_line_blockContext script_line_block() throws RecognitionException {
		Script_line_blockContext _localctx = new Script_line_blockContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_script_line_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 6917529027641081858L) != 0) || ((((_la - 76)) & ~0x3f) == 0 && ((1L << (_la - 76)) & 275951648769L) != 0)) {
				{
				{
				setState(92);
				script_line();
				}
				}
				setState(97);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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

	@SuppressWarnings("CheckReturnValue")
	public static class While_loopContext extends ParserRuleContext {
		public Token whl;
		public Token op;
		public Operand_expressionContext oe;
		public Token cp;
		public Token ob;
		public Script_line_blockContext scrLineBlk;
		public Token cb;
		public TerminalNode While() { return getToken(ChameleonScriptParser.While, 0); }
		public TerminalNode OpenParens() { return getToken(ChameleonScriptParser.OpenParens, 0); }
		public Operand_expressionContext operand_expression() {
			return getRuleContext(Operand_expressionContext.class,0);
		}
		public TerminalNode ClosedParens() { return getToken(ChameleonScriptParser.ClosedParens, 0); }
		public TerminalNode OpenBrace() { return getToken(ChameleonScriptParser.OpenBrace, 0); }
		public Script_line_blockContext script_line_block() {
			return getRuleContext(Script_line_blockContext.class,0);
		}
		public TerminalNode ClosedBrace() { return getToken(ChameleonScriptParser.ClosedBrace, 0); }
		public While_loopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_while_loop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterWhile_loop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitWhile_loop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitWhile_loop(this);
			else return visitor.visitChildren(this);
		}
	}

	public final While_loopContext while_loop() throws RecognitionException {
		While_loopContext _localctx = new While_loopContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_while_loop);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			((While_loopContext)_localctx).whl = match(While);
			setState(99);
			((While_loopContext)_localctx).op = match(OpenParens);
			setState(100);
			((While_loopContext)_localctx).oe = operand_expression();
			setState(101);
			((While_loopContext)_localctx).cp = match(ClosedParens);
			setState(102);
			((While_loopContext)_localctx).ob = match(OpenBrace);
			setState(103);
			((While_loopContext)_localctx).scrLineBlk = script_line_block();
			setState(104);
			((While_loopContext)_localctx).cb = match(ClosedBrace);

			            
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

	@SuppressWarnings("CheckReturnValue")
	public static class If_blockContext extends ParserRuleContext {
		public Token ic;
		public Token op;
		public Operand_expressionContext oe;
		public Token cp;
		public Token ob;
		public Script_line_blockContext scrLineBlk;
		public Token cb;
		public TerminalNode IfCond() { return getToken(ChameleonScriptParser.IfCond, 0); }
		public TerminalNode OpenParens() { return getToken(ChameleonScriptParser.OpenParens, 0); }
		public Operand_expressionContext operand_expression() {
			return getRuleContext(Operand_expressionContext.class,0);
		}
		public TerminalNode ClosedParens() { return getToken(ChameleonScriptParser.ClosedParens, 0); }
		public TerminalNode OpenBrace() { return getToken(ChameleonScriptParser.OpenBrace, 0); }
		public Script_line_blockContext script_line_block() {
			return getRuleContext(Script_line_blockContext.class,0);
		}
		public TerminalNode ClosedBrace() { return getToken(ChameleonScriptParser.ClosedBrace, 0); }
		public If_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterIf_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitIf_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitIf_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final If_blockContext if_block() throws RecognitionException {
		If_blockContext _localctx = new If_blockContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_if_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			((If_blockContext)_localctx).ic = match(IfCond);
			setState(108);
			((If_blockContext)_localctx).op = match(OpenParens);
			setState(109);
			((If_blockContext)_localctx).oe = operand_expression();
			setState(110);
			((If_blockContext)_localctx).cp = match(ClosedParens);
			setState(111);
			((If_blockContext)_localctx).ob = match(OpenBrace);
			setState(112);
			((If_blockContext)_localctx).scrLineBlk = script_line_block();
			setState(113);
			((If_blockContext)_localctx).cb = match(ClosedBrace);

			            
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

	@SuppressWarnings("CheckReturnValue")
	public static class Ifelse_blockContext extends ParserRuleContext {
		public Operand_expressionContext ifoe;
		public Token cp;
		public Script_line_blockContext scrLineBlkIf;
		public Script_line_blockContext scrLineBlkElse;
		public TerminalNode IfCond() { return getToken(ChameleonScriptParser.IfCond, 0); }
		public TerminalNode OpenParens() { return getToken(ChameleonScriptParser.OpenParens, 0); }
		public List<TerminalNode> OpenBrace() { return getTokens(ChameleonScriptParser.OpenBrace); }
		public TerminalNode OpenBrace(int i) {
			return getToken(ChameleonScriptParser.OpenBrace, i);
		}
		public List<TerminalNode> ClosedBrace() { return getTokens(ChameleonScriptParser.ClosedBrace); }
		public TerminalNode ClosedBrace(int i) {
			return getToken(ChameleonScriptParser.ClosedBrace, i);
		}
		public TerminalNode ElseCond() { return getToken(ChameleonScriptParser.ElseCond, 0); }
		public Operand_expressionContext operand_expression() {
			return getRuleContext(Operand_expressionContext.class,0);
		}
		public TerminalNode ClosedParens() { return getToken(ChameleonScriptParser.ClosedParens, 0); }
		public List<Script_line_blockContext> script_line_block() {
			return getRuleContexts(Script_line_blockContext.class);
		}
		public Script_line_blockContext script_line_block(int i) {
			return getRuleContext(Script_line_blockContext.class,i);
		}
		public Ifelse_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifelse_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterIfelse_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitIfelse_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitIfelse_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ifelse_blockContext ifelse_block() throws RecognitionException {
		Ifelse_blockContext _localctx = new Ifelse_blockContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_ifelse_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			match(IfCond);
			setState(117);
			match(OpenParens);
			setState(118);
			((Ifelse_blockContext)_localctx).ifoe = operand_expression();
			setState(119);
			((Ifelse_blockContext)_localctx).cp = match(ClosedParens);
			setState(120);
			match(OpenBrace);
			setState(121);
			((Ifelse_blockContext)_localctx).scrLineBlkIf = script_line_block();
			setState(122);
			match(ClosedBrace);
			setState(123);
			match(ElseCond);
			setState(124);
			match(OpenBrace);
			setState(125);
			((Ifelse_blockContext)_localctx).scrLineBlkElse = script_line_block();
			setState(126);
			match(ClosedBrace);

			              
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

	@SuppressWarnings("CheckReturnValue")
	public static class Variable_reference_v1Context extends ParserRuleContext {
		public ScriptVariable svar;
		public Token vss;
		public Token vname;
		public TerminalNode VariableStartSymbol() { return getToken(ChameleonScriptParser.VariableStartSymbol, 0); }
		public TerminalNode VariableName() { return getToken(ChameleonScriptParser.VariableName, 0); }
		public Variable_reference_v1Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable_reference_v1; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterVariable_reference_v1(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitVariable_reference_v1(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitVariable_reference_v1(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Variable_reference_v1Context variable_reference_v1() throws RecognitionException {
		Variable_reference_v1Context _localctx = new Variable_reference_v1Context(_ctx, getState());
		enterRule(_localctx, 12, RULE_variable_reference_v1);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			((Variable_reference_v1Context)_localctx).vss = match(VariableStartSymbol);
			setState(130);
			((Variable_reference_v1Context)_localctx).vname = match(VariableName);

			           String varName = (((Variable_reference_v1Context)_localctx).vname!=null?((Variable_reference_v1Context)_localctx).vname.getText():null);
			           if(!ChameleonScripting.getRunningInstance().variableNameExists(varName)) {
			                ((Variable_reference_v1Context)_localctx).svar = ScriptVariable.newInstance().set("<uninitialized>").setName(varName);
			                ChameleonScripting.getRunningInstance().setVariableByName(varName, _localctx.svar);
			           }
			           else {
			                ((Variable_reference_v1Context)_localctx).svar = ChameleonScripting.getRunningInstance().lookupVariableByName(varName);
			           }
			     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Variable_reference_v2Context extends ParserRuleContext {
		public ScriptVariable svar;
		public Variable_reference_v1Context var;
		public Token hia;
		public Token propName;
		public Variable_reference_v1Context variable_reference_v1() {
			return getRuleContext(Variable_reference_v1Context.class,0);
		}
		public TerminalNode HashedIndexAccessor() { return getToken(ChameleonScriptParser.HashedIndexAccessor, 0); }
		public TerminalNode VariableName() { return getToken(ChameleonScriptParser.VariableName, 0); }
		public Variable_reference_v2Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable_reference_v2; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterVariable_reference_v2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitVariable_reference_v2(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitVariable_reference_v2(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Variable_reference_v2Context variable_reference_v2() throws RecognitionException {
		Variable_reference_v2Context _localctx = new Variable_reference_v2Context(_ctx, getState());
		enterRule(_localctx, 14, RULE_variable_reference_v2);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133);
			((Variable_reference_v2Context)_localctx).var = variable_reference_v1();
			setState(134);
			((Variable_reference_v2Context)_localctx).hia = match(HashedIndexAccessor);
			setState(135);
			((Variable_reference_v2Context)_localctx).propName = match(VariableName);

			          ((Variable_reference_v2Context)_localctx).svar = ((Variable_reference_v2Context)_localctx).var.svar.getValueAt((((Variable_reference_v2Context)_localctx).propName!=null?((Variable_reference_v2Context)_localctx).propName.getText():null));
			     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Variable_referenceContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Variable_reference_v2Context vrtype2;
		public Variable_reference_v1Context vrtype1;
		public Variable_reference_v2Context variable_reference_v2() {
			return getRuleContext(Variable_reference_v2Context.class,0);
		}
		public Variable_reference_v1Context variable_reference_v1() {
			return getRuleContext(Variable_reference_v1Context.class,0);
		}
		public Variable_referenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable_reference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterVariable_reference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitVariable_reference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitVariable_reference(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Variable_referenceContext variable_reference() throws RecognitionException {
		Variable_referenceContext _localctx = new Variable_referenceContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_variable_reference);
		try {
			setState(144);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(138);
				((Variable_referenceContext)_localctx).vrtype2 = variable_reference_v2();

				          ((Variable_referenceContext)_localctx).svar = ((Variable_referenceContext)_localctx).vrtype2.svar;
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(141);
				((Variable_referenceContext)_localctx).vrtype1 = variable_reference_v1();

				          ((Variable_referenceContext)_localctx).svar = ((Variable_referenceContext)_localctx).vrtype1.svar;
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Type_literalContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Token dl;
		public Token hs;
		public Token hb;
		public Token hl;
		public Token bl;
		public Quoted_string_literalContext qsl;
		public Token ob;
		public Byte_literal_listContext bll;
		public Token cb;
		public TerminalNode DecimalLiteral() { return getToken(ChameleonScriptParser.DecimalLiteral, 0); }
		public TerminalNode HexString() { return getToken(ChameleonScriptParser.HexString, 0); }
		public TerminalNode HexByte() { return getToken(ChameleonScriptParser.HexByte, 0); }
		public TerminalNode HexLiteral() { return getToken(ChameleonScriptParser.HexLiteral, 0); }
		public TerminalNode BooleanLiteral() { return getToken(ChameleonScriptParser.BooleanLiteral, 0); }
		public Quoted_string_literalContext quoted_string_literal() {
			return getRuleContext(Quoted_string_literalContext.class,0);
		}
		public TerminalNode OpenBrace() { return getToken(ChameleonScriptParser.OpenBrace, 0); }
		public Byte_literal_listContext byte_literal_list() {
			return getRuleContext(Byte_literal_listContext.class,0);
		}
		public TerminalNode ClosedBrace() { return getToken(ChameleonScriptParser.ClosedBrace, 0); }
		public Type_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterType_literal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitType_literal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitType_literal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_literalContext type_literal() throws RecognitionException {
		Type_literalContext _localctx = new Type_literalContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_type_literal);
		try {
			setState(164);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DecimalLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(146);
				((Type_literalContext)_localctx).dl = match(DecimalLiteral);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.parseInt((((Type_literalContext)_localctx).dl!=null?((Type_literalContext)_localctx).dl.getText():null)); 
				}
				break;
			case HexString:
				enterOuterAlt(_localctx, 2);
				{
				setState(148);
				((Type_literalContext)_localctx).hs = match(HexString);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.parseHexString((((Type_literalContext)_localctx).hs!=null?((Type_literalContext)_localctx).hs.getText():null)); 
				}
				break;
			case HexByte:
				enterOuterAlt(_localctx, 3);
				{
				setState(150);
				((Type_literalContext)_localctx).hb = match(HexByte);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.parseInt((((Type_literalContext)_localctx).hb!=null?((Type_literalContext)_localctx).hb.getText():null)); 
				}
				break;
			case HexLiteral:
				enterOuterAlt(_localctx, 4);
				{
				setState(152);
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
				enterOuterAlt(_localctx, 5);
				{
				setState(154);
				((Type_literalContext)_localctx).bl = match(BooleanLiteral);
				 ((Type_literalContext)_localctx).svar = ScriptVariable.parseBoolean((((Type_literalContext)_localctx).bl!=null?((Type_literalContext)_localctx).bl.getText():null)); 
				}
				break;
			case QuotedStringLiteral:
			case QuotedHexStringLiteral:
				enterOuterAlt(_localctx, 6);
				{
				setState(156);
				((Type_literalContext)_localctx).qsl = quoted_string_literal();
				 ((Type_literalContext)_localctx).svar = ((Type_literalContext)_localctx).qsl.svar; 
				}
				break;
			case OpenBrace:
				enterOuterAlt(_localctx, 7);
				{
				setState(159);
				((Type_literalContext)_localctx).ob = match(OpenBrace);
				setState(160);
				((Type_literalContext)_localctx).bll = byte_literal_list();
				setState(161);
				((Type_literalContext)_localctx).cb = match(ClosedBrace);
				 ((Type_literalContext)_localctx).svar = ((Type_literalContext)_localctx).bll.svar; 
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

	@SuppressWarnings("CheckReturnValue")
	public static class Quoted_string_literalContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Token qsl;
		public Token qhsl;
		public Token qrsl;
		public TerminalNode QuotedStringLiteral() { return getToken(ChameleonScriptParser.QuotedStringLiteral, 0); }
		public TerminalNode QuotedHexStringLiteral() { return getToken(ChameleonScriptParser.QuotedHexStringLiteral, 0); }
		public Quoted_string_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quoted_string_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterQuoted_string_literal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitQuoted_string_literal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitQuoted_string_literal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Quoted_string_literalContext quoted_string_literal() throws RecognitionException {
		Quoted_string_literalContext _localctx = new Quoted_string_literalContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_quoted_string_literal);
		try {
			setState(172);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(166);
				((Quoted_string_literalContext)_localctx).qsl = match(QuotedStringLiteral);

				          ((Quoted_string_literalContext)_localctx).svar = ScriptVariable.newInstance().set((((Quoted_string_literalContext)_localctx).qsl!=null?((Quoted_string_literalContext)_localctx).qsl.getText():null).substring(1, (((Quoted_string_literalContext)_localctx).qsl!=null?((Quoted_string_literalContext)_localctx).qsl.getText():null).length() - 1));
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(168);
				((Quoted_string_literalContext)_localctx).qhsl = match(QuotedHexStringLiteral);

				          ((Quoted_string_literalContext)_localctx).svar = ScriptVariable.newInstance().set((((Quoted_string_literalContext)_localctx).qhsl!=null?((Quoted_string_literalContext)_localctx).qhsl.getText():null).substring(1, (((Quoted_string_literalContext)_localctx).qhsl!=null?((Quoted_string_literalContext)_localctx).qhsl.getText():null).length() - 1));
				     
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(170);
				((Quoted_string_literalContext)_localctx).qrsl = match(QuotedHexStringLiteral);

				          ((Quoted_string_literalContext)_localctx).svar = ScriptVariable.newInstance().set((((Quoted_string_literalContext)_localctx).qrsl!=null?((Quoted_string_literalContext)_localctx).qrsl.getText():null).substring(1, (((Quoted_string_literalContext)_localctx).qrsl!=null?((Quoted_string_literalContext)_localctx).qrsl.getText():null).length() - 1));
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Byte_literal_listContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Token hb;
		public Token cs;
		public Byte_literal_listContext bll;
		public TerminalNode HexByte() { return getToken(ChameleonScriptParser.HexByte, 0); }
		public TerminalNode CommaSeparator() { return getToken(ChameleonScriptParser.CommaSeparator, 0); }
		public Byte_literal_listContext byte_literal_list() {
			return getRuleContext(Byte_literal_listContext.class,0);
		}
		public Byte_literal_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_byte_literal_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterByte_literal_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitByte_literal_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitByte_literal_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Byte_literal_listContext byte_literal_list() throws RecognitionException {
		Byte_literal_listContext _localctx = new Byte_literal_listContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_byte_literal_list);
		try {
			setState(181);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(174);
				((Byte_literal_listContext)_localctx).hb = match(HexByte);

				          ((Byte_literal_listContext)_localctx).svar = ScriptVariable.newInstance().set(new byte[] { (byte) Integer.parseInt((((Byte_literal_listContext)_localctx).hb!=null?((Byte_literal_listContext)_localctx).hb.getText():null), 16) });
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(176);
				((Byte_literal_listContext)_localctx).hb = match(HexByte);
				setState(177);
				((Byte_literal_listContext)_localctx).cs = match(CommaSeparator);
				setState(178);
				((Byte_literal_listContext)_localctx).bll = byte_literal_list();

				          int bllLength = ((Byte_literal_listContext)_localctx).bll.svar.getValueAsBytes().length;
				          byte[] bytesArr = new byte[bllLength + 1];
				          System.arraycopy(((Byte_literal_listContext)_localctx).bll.svar.getValueAsBytes(), 0, bytesArr, 0, bllLength);
				          bytesArr[bllLength] = (byte) Integer.parseInt((((Byte_literal_listContext)_localctx).hb!=null?((Byte_literal_listContext)_localctx).hb.getText():null), 16);
				          ((Byte_literal_listContext)_localctx).svar = ScriptVariable.newInstance().set(bytesArr);
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Operand_expression_v1Context extends ParserRuleContext {
		public ScriptVariable svar;
		public Variable_referenceContext vr;
		public Type_literalContext tl;
		public Variable_referenceContext variable_reference() {
			return getRuleContext(Variable_referenceContext.class,0);
		}
		public Type_literalContext type_literal() {
			return getRuleContext(Type_literalContext.class,0);
		}
		public Operand_expression_v1Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand_expression_v1; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterOperand_expression_v1(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitOperand_expression_v1(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitOperand_expression_v1(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operand_expression_v1Context operand_expression_v1() throws RecognitionException {
		Operand_expression_v1Context _localctx = new Operand_expression_v1Context(_ctx, getState());
		enterRule(_localctx, 24, RULE_operand_expression_v1);
		try {
			setState(189);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VariableStartSymbol:
				enterOuterAlt(_localctx, 1);
				{
				setState(183);
				((Operand_expression_v1Context)_localctx).vr = variable_reference();

				          ((Operand_expression_v1Context)_localctx).svar = ((Operand_expression_v1Context)_localctx).vr.svar;
				     
				}
				break;
			case QuotedStringLiteral:
			case QuotedHexStringLiteral:
			case OpenBrace:
			case DecimalLiteral:
			case HexString:
			case HexByte:
			case HexLiteral:
			case BooleanLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(186);
				((Operand_expression_v1Context)_localctx).tl = type_literal();

				          ((Operand_expression_v1Context)_localctx).svar = ((Operand_expression_v1Context)_localctx).tl.svar;
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Typecast_expressionContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Token tctype;
		public Operand_expression_v1Context initVar;
		public TerminalNode TypeCastByte() { return getToken(ChameleonScriptParser.TypeCastByte, 0); }
		public Operand_expression_v1Context operand_expression_v1() {
			return getRuleContext(Operand_expression_v1Context.class,0);
		}
		public TerminalNode TypeCastShort() { return getToken(ChameleonScriptParser.TypeCastShort, 0); }
		public TerminalNode TypeCastInt32() { return getToken(ChameleonScriptParser.TypeCastInt32, 0); }
		public TerminalNode TypeCastBoolean() { return getToken(ChameleonScriptParser.TypeCastBoolean, 0); }
		public TerminalNode TypeCastString() { return getToken(ChameleonScriptParser.TypeCastString, 0); }
		public TerminalNode TypeCastBytes() { return getToken(ChameleonScriptParser.TypeCastBytes, 0); }
		public Typecast_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typecast_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterTypecast_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitTypecast_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitTypecast_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Typecast_expressionContext typecast_expression() throws RecognitionException {
		Typecast_expressionContext _localctx = new Typecast_expressionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_typecast_expression);
		try {
			setState(215);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TypeCastByte:
				enterOuterAlt(_localctx, 1);
				{
				setState(191);
				((Typecast_expressionContext)_localctx).tctype = match(TypeCastByte);
				setState(192);
				((Typecast_expressionContext)_localctx).initVar = operand_expression_v1();

				          ((Typecast_expressionContext)_localctx).svar = ScriptVariable.newInstance().set(new byte[] { ((Typecast_expressionContext)_localctx).initVar.svar.getValueAsByte() });
				     
				}
				break;
			case TypeCastShort:
				enterOuterAlt(_localctx, 2);
				{
				setState(195);
				((Typecast_expressionContext)_localctx).tctype = match(TypeCastShort);
				setState(196);
				((Typecast_expressionContext)_localctx).initVar = operand_expression_v1();

				          ((Typecast_expressionContext)_localctx).svar = ScriptVariable.newInstance().set((int) ((Typecast_expressionContext)_localctx).initVar.svar.getValueAsShort());
				     
				}
				break;
			case TypeCastInt32:
				enterOuterAlt(_localctx, 3);
				{
				setState(199);
				((Typecast_expressionContext)_localctx).tctype = match(TypeCastInt32);
				setState(200);
				((Typecast_expressionContext)_localctx).initVar = operand_expression_v1();

				          ((Typecast_expressionContext)_localctx).svar = ScriptVariable.newInstance().set((int) ((Typecast_expressionContext)_localctx).initVar.svar.getValueAsInt());
				     
				}
				break;
			case TypeCastBoolean:
				enterOuterAlt(_localctx, 4);
				{
				setState(203);
				((Typecast_expressionContext)_localctx).tctype = match(TypeCastBoolean);
				setState(204);
				((Typecast_expressionContext)_localctx).initVar = operand_expression_v1();

				          ((Typecast_expressionContext)_localctx).svar = ScriptVariable.newInstance().set((boolean) ((Typecast_expressionContext)_localctx).initVar.svar.getValueAsBoolean());
				     
				}
				break;
			case TypeCastString:
				enterOuterAlt(_localctx, 5);
				{
				setState(207);
				((Typecast_expressionContext)_localctx).tctype = match(TypeCastString);
				setState(208);
				((Typecast_expressionContext)_localctx).initVar = operand_expression_v1();

				          ((Typecast_expressionContext)_localctx).svar = ScriptVariable.newInstance().set(((Typecast_expressionContext)_localctx).initVar.svar.getValueAsString());
				     
				}
				break;
			case TypeCastBytes:
				enterOuterAlt(_localctx, 6);
				{
				setState(211);
				((Typecast_expressionContext)_localctx).tctype = match(TypeCastBytes);
				setState(212);
				((Typecast_expressionContext)_localctx).initVar = operand_expression_v1();

				          ((Typecast_expressionContext)_localctx).svar = ScriptVariable.newInstance().set(((Typecast_expressionContext)_localctx).initVar.svar.getValueAsBytes());
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Operand_expression_v2Context extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expression_v1Context oe;
		public Typecast_expressionContext tce;
		public Operand_expression_v1Context operand_expression_v1() {
			return getRuleContext(Operand_expression_v1Context.class,0);
		}
		public Typecast_expressionContext typecast_expression() {
			return getRuleContext(Typecast_expressionContext.class,0);
		}
		public Operand_expression_v2Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand_expression_v2; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterOperand_expression_v2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitOperand_expression_v2(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitOperand_expression_v2(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operand_expression_v2Context operand_expression_v2() throws RecognitionException {
		Operand_expression_v2Context _localctx = new Operand_expression_v2Context(_ctx, getState());
		enterRule(_localctx, 28, RULE_operand_expression_v2);
		try {
			setState(223);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case QuotedStringLiteral:
			case QuotedHexStringLiteral:
			case OpenBrace:
			case VariableStartSymbol:
			case DecimalLiteral:
			case HexString:
			case HexByte:
			case HexLiteral:
			case BooleanLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(217);
				((Operand_expression_v2Context)_localctx).oe = operand_expression_v1();

				          ((Operand_expression_v2Context)_localctx).svar = ((Operand_expression_v2Context)_localctx).oe.svar;
				     
				}
				break;
			case TypeCastByte:
			case TypeCastBytes:
			case TypeCastShort:
			case TypeCastInt32:
			case TypeCastBoolean:
			case TypeCastString:
				enterOuterAlt(_localctx, 2);
				{
				setState(220);
				((Operand_expression_v2Context)_localctx).tce = typecast_expression();

				          ((Operand_expression_v2Context)_localctx).svar = ((Operand_expression_v2Context)_localctx).tce.svar;
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Other_operation_resultContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expression_v2Context lhs;
		public Operand_expression_v2Context rhs;
		public TerminalNode LeftShiftOperator() { return getToken(ChameleonScriptParser.LeftShiftOperator, 0); }
		public List<Operand_expression_v2Context> operand_expression_v2() {
			return getRuleContexts(Operand_expression_v2Context.class);
		}
		public Operand_expression_v2Context operand_expression_v2(int i) {
			return getRuleContext(Operand_expression_v2Context.class,i);
		}
		public TerminalNode RightShiftOperator() { return getToken(ChameleonScriptParser.RightShiftOperator, 0); }
		public TerminalNode BitwiseAndOperator() { return getToken(ChameleonScriptParser.BitwiseAndOperator, 0); }
		public TerminalNode BitwiseOrOperator() { return getToken(ChameleonScriptParser.BitwiseOrOperator, 0); }
		public TerminalNode BitwiseXorOperator() { return getToken(ChameleonScriptParser.BitwiseXorOperator, 0); }
		public TerminalNode ArithmeticPlusOperator() { return getToken(ChameleonScriptParser.ArithmeticPlusOperator, 0); }
		public TerminalNode BitwiseNotOperator() { return getToken(ChameleonScriptParser.BitwiseNotOperator, 0); }
		public Other_operation_resultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_other_operation_result; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterOther_operation_result(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitOther_operation_result(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitOther_operation_result(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Other_operation_resultContext other_operation_result() throws RecognitionException {
		Other_operation_resultContext _localctx = new Other_operation_resultContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_other_operation_result);
		try {
			setState(259);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(225);
				((Other_operation_resultContext)_localctx).lhs = operand_expression_v2();
				setState(226);
				match(LeftShiftOperator);
				setState(227);
				((Other_operation_resultContext)_localctx).rhs = operand_expression_v2();

				          ScriptVariable lhsVar = ((Other_operation_resultContext)_localctx).lhs.svar;
				          ((Other_operation_resultContext)_localctx).svar = lhsVar.binaryOperation(ScriptVariable.Operation.BINOP_SHIFT_LEFT, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(230);
				((Other_operation_resultContext)_localctx).lhs = operand_expression_v2();
				setState(231);
				match(RightShiftOperator);
				setState(232);
				((Other_operation_resultContext)_localctx).rhs = operand_expression_v2();

				          ScriptVariable lhsVar = ((Other_operation_resultContext)_localctx).lhs.svar;
				          ((Other_operation_resultContext)_localctx).svar = lhsVar.binaryOperation(ScriptVariable.Operation.BINOP_SHIFT_RIGHT, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(235);
				((Other_operation_resultContext)_localctx).lhs = operand_expression_v2();
				setState(236);
				match(BitwiseAndOperator);
				setState(237);
				((Other_operation_resultContext)_localctx).rhs = operand_expression_v2();

				          ScriptVariable lhsVar = ((Other_operation_resultContext)_localctx).lhs.svar;
				          ((Other_operation_resultContext)_localctx).svar = lhsVar.binaryOperation(ScriptVariable.Operation.BINOP_BITWISE_AND, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(240);
				((Other_operation_resultContext)_localctx).lhs = operand_expression_v2();
				setState(241);
				match(BitwiseOrOperator);
				setState(242);
				((Other_operation_resultContext)_localctx).rhs = operand_expression_v2();

				          ScriptVariable lhsVar = ((Other_operation_resultContext)_localctx).lhs.svar;
				          ((Other_operation_resultContext)_localctx).svar = lhsVar.binaryOperation(ScriptVariable.Operation.BINOP_BITWISE_OR, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(245);
				((Other_operation_resultContext)_localctx).lhs = operand_expression_v2();
				setState(246);
				match(BitwiseXorOperator);
				setState(247);
				((Other_operation_resultContext)_localctx).rhs = operand_expression_v2();

				          ScriptVariable lhsVar = ((Other_operation_resultContext)_localctx).lhs.svar;
				          ((Other_operation_resultContext)_localctx).svar = lhsVar.binaryOperation(ScriptVariable.Operation.BINOP_BITWISE_XOR, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(250);
				((Other_operation_resultContext)_localctx).lhs = operand_expression_v2();
				setState(251);
				match(ArithmeticPlusOperator);
				setState(252);
				((Other_operation_resultContext)_localctx).rhs = operand_expression_v2();

				          ScriptVariable lhsVar = ((Other_operation_resultContext)_localctx).lhs.svar;
				          ((Other_operation_resultContext)_localctx).svar = lhsVar.binaryOperation(ScriptVariable.Operation.BINOP_PLUS, ((Other_operation_resultContext)_localctx).rhs.svar);
				     
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(255);
				match(BitwiseNotOperator);
				setState(256);
				((Other_operation_resultContext)_localctx).rhs = operand_expression_v2();

				          ScriptVariable rhsVar = ((Other_operation_resultContext)_localctx).rhs.svar;
				          ((Other_operation_resultContext)_localctx).svar = rhsVar.unaryOperation(ScriptVariable.Operation.UOP_BITWISE_NOT);
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Operand_expression_v3Context extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expression_v2Context oe;
		public Other_operation_resultContext oor;
		public Operand_expression_v2Context operand_expression_v2() {
			return getRuleContext(Operand_expression_v2Context.class,0);
		}
		public Other_operation_resultContext other_operation_result() {
			return getRuleContext(Other_operation_resultContext.class,0);
		}
		public Operand_expression_v3Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand_expression_v3; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterOperand_expression_v3(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitOperand_expression_v3(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitOperand_expression_v3(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operand_expression_v3Context operand_expression_v3() throws RecognitionException {
		Operand_expression_v3Context _localctx = new Operand_expression_v3Context(_ctx, getState());
		enterRule(_localctx, 32, RULE_operand_expression_v3);
		try {
			setState(267);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(261);
				((Operand_expression_v3Context)_localctx).oe = operand_expression_v2();

				          ((Operand_expression_v3Context)_localctx).svar = ((Operand_expression_v3Context)_localctx).oe.svar;
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(264);
				((Operand_expression_v3Context)_localctx).oor = other_operation_result();

				          ((Operand_expression_v3Context)_localctx).svar = ((Operand_expression_v3Context)_localctx).oor.svar;
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Boolean_valued_operationContext extends ParserRuleContext {
		public ScriptVariable opResult;
		public Operand_expression_v3Context lhs;
		public Operand_expression_v3Context rhs;
		public TerminalNode EqualsComparisonOperator() { return getToken(ChameleonScriptParser.EqualsComparisonOperator, 0); }
		public List<Operand_expression_v3Context> operand_expression_v3() {
			return getRuleContexts(Operand_expression_v3Context.class);
		}
		public Operand_expression_v3Context operand_expression_v3(int i) {
			return getRuleContext(Operand_expression_v3Context.class,i);
		}
		public TerminalNode NotEqualsComparisonOperator() { return getToken(ChameleonScriptParser.NotEqualsComparisonOperator, 0); }
		public TerminalNode LogicalAndOperator() { return getToken(ChameleonScriptParser.LogicalAndOperator, 0); }
		public TerminalNode LogicalOrOperator() { return getToken(ChameleonScriptParser.LogicalOrOperator, 0); }
		public TerminalNode LogicalNotOperator() { return getToken(ChameleonScriptParser.LogicalNotOperator, 0); }
		public Boolean_valued_operationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolean_valued_operation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterBoolean_valued_operation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitBoolean_valued_operation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitBoolean_valued_operation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Boolean_valued_operationContext boolean_valued_operation() throws RecognitionException {
		Boolean_valued_operationContext _localctx = new Boolean_valued_operationContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_boolean_valued_operation);
		try {
			setState(293);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(269);
				((Boolean_valued_operationContext)_localctx).lhs = operand_expression_v3();
				setState(270);
				match(EqualsComparisonOperator);
				setState(271);
				((Boolean_valued_operationContext)_localctx).rhs = operand_expression_v3();

				          if(((Boolean_valued_operationContext)_localctx).rhs.svar.isStringType()) {
				               ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsString().equals(((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsString()));
				          }
				          else if(((Boolean_valued_operationContext)_localctx).rhs.svar.isIntegerType()) {
				               ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsInt() == ((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsInt());
				               AndroidLogger.i("TAG", "IS INTEGER TYPE!");
				          }
				          else {
				               ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsBoolean() == ((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsBoolean());
				          }
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(274);
				((Boolean_valued_operationContext)_localctx).lhs = operand_expression_v3();
				setState(275);
				match(NotEqualsComparisonOperator);
				setState(276);
				((Boolean_valued_operationContext)_localctx).rhs = operand_expression_v3();

				          if(((Boolean_valued_operationContext)_localctx).rhs.svar.isStringType()) {
				                         ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(!((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsString().equals(((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsString()));
				                    }
				                    else if(((Boolean_valued_operationContext)_localctx).rhs.svar.isIntegerType()) {
				                         ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsInt() != ((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsInt());
				                    }
				                    else {
				                         ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsBoolean() != ((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsBoolean());
				                    }
				     
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(279);
				((Boolean_valued_operationContext)_localctx).lhs = operand_expression_v3();
				setState(280);
				match(LogicalAndOperator);
				setState(281);
				((Boolean_valued_operationContext)_localctx).rhs = operand_expression_v3();

				          ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsBoolean() && ((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsBoolean());
				     
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(284);
				((Boolean_valued_operationContext)_localctx).lhs = operand_expression_v3();
				setState(285);
				match(LogicalOrOperator);
				setState(286);
				((Boolean_valued_operationContext)_localctx).rhs = operand_expression_v3();

				          ((Boolean_valued_operationContext)_localctx).opResult = ScriptVariable.newInstance().set(((Boolean_valued_operationContext)_localctx).lhs.svar.getValueAsBoolean() || ((Boolean_valued_operationContext)_localctx).rhs.svar.getValueAsBoolean());
				     
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(289);
				match(LogicalNotOperator);
				setState(290);
				((Boolean_valued_operationContext)_localctx).rhs = operand_expression_v3();

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

	@SuppressWarnings("CheckReturnValue")
	public static class Operand_expression_v4Context extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expression_v3Context oe;
		public Boolean_valued_operationContext bvo;
		public Operand_expression_v3Context operand_expression_v3() {
			return getRuleContext(Operand_expression_v3Context.class,0);
		}
		public Boolean_valued_operationContext boolean_valued_operation() {
			return getRuleContext(Boolean_valued_operationContext.class,0);
		}
		public Operand_expression_v4Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand_expression_v4; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterOperand_expression_v4(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitOperand_expression_v4(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitOperand_expression_v4(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operand_expression_v4Context operand_expression_v4() throws RecognitionException {
		Operand_expression_v4Context _localctx = new Operand_expression_v4Context(_ctx, getState());
		enterRule(_localctx, 36, RULE_operand_expression_v4);
		try {
			setState(301);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(295);
				((Operand_expression_v4Context)_localctx).oe = operand_expression_v3();

				          ((Operand_expression_v4Context)_localctx).svar = ((Operand_expression_v4Context)_localctx).oe.svar;
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(298);
				((Operand_expression_v4Context)_localctx).bvo = boolean_valued_operation();

				          ((Operand_expression_v4Context)_localctx).svar = ((Operand_expression_v4Context)_localctx).bvo.opResult;
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Extract_expression_from_array_indexContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Variable_referenceContext varRef;
		public Operand_expression_v4Context oexpr;
		public TerminalNode ArrayIndexOpenBracket() { return getToken(ChameleonScriptParser.ArrayIndexOpenBracket, 0); }
		public TerminalNode ArrayIndexCloseBracket() { return getToken(ChameleonScriptParser.ArrayIndexCloseBracket, 0); }
		public Variable_referenceContext variable_reference() {
			return getRuleContext(Variable_referenceContext.class,0);
		}
		public Operand_expression_v4Context operand_expression_v4() {
			return getRuleContext(Operand_expression_v4Context.class,0);
		}
		public Extract_expression_from_array_indexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extract_expression_from_array_index; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterExtract_expression_from_array_index(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitExtract_expression_from_array_index(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitExtract_expression_from_array_index(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Extract_expression_from_array_indexContext extract_expression_from_array_index() throws RecognitionException {
		Extract_expression_from_array_indexContext _localctx = new Extract_expression_from_array_indexContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_extract_expression_from_array_index);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(303);
			((Extract_expression_from_array_indexContext)_localctx).varRef = variable_reference();
			setState(304);
			match(ArrayIndexOpenBracket);
			setState(305);
			((Extract_expression_from_array_indexContext)_localctx).oexpr = operand_expression_v4();
			setState(306);
			match(ArrayIndexCloseBracket);

			          ((Extract_expression_from_array_indexContext)_localctx).svar = ((Extract_expression_from_array_indexContext)_localctx).varRef.svar.getValueAt(((Extract_expression_from_array_indexContext)_localctx).oexpr.svar.getValueAsInt());
			     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Operand_expression_v5Context extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expression_v4Context oe;
		public Extract_expression_from_array_indexContext aiExpr;
		public Operand_expression_v4Context operand_expression_v4() {
			return getRuleContext(Operand_expression_v4Context.class,0);
		}
		public Extract_expression_from_array_indexContext extract_expression_from_array_index() {
			return getRuleContext(Extract_expression_from_array_indexContext.class,0);
		}
		public Operand_expression_v5Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand_expression_v5; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterOperand_expression_v5(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitOperand_expression_v5(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitOperand_expression_v5(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operand_expression_v5Context operand_expression_v5() throws RecognitionException {
		Operand_expression_v5Context _localctx = new Operand_expression_v5Context(_ctx, getState());
		enterRule(_localctx, 40, RULE_operand_expression_v5);
		try {
			setState(315);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(309);
				((Operand_expression_v5Context)_localctx).oe = operand_expression_v4();

				          ((Operand_expression_v5Context)_localctx).svar = ((Operand_expression_v5Context)_localctx).oe.svar;
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(312);
				((Operand_expression_v5Context)_localctx).aiExpr = extract_expression_from_array_index();

				          ((Operand_expression_v5Context)_localctx).svar = ((Operand_expression_v5Context)_localctx).aiExpr.svar;
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Extract_expression_from_array_sliceContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Variable_referenceContext varRef;
		public Operand_expression_v5Context oexprStartIdx;
		public Operand_expression_v5Context oexprLengthIdx;
		public TerminalNode ArrayIndexOpenBracket() { return getToken(ChameleonScriptParser.ArrayIndexOpenBracket, 0); }
		public TerminalNode ColonSeparator() { return getToken(ChameleonScriptParser.ColonSeparator, 0); }
		public TerminalNode ArrayIndexCloseBracket() { return getToken(ChameleonScriptParser.ArrayIndexCloseBracket, 0); }
		public Variable_referenceContext variable_reference() {
			return getRuleContext(Variable_referenceContext.class,0);
		}
		public List<Operand_expression_v5Context> operand_expression_v5() {
			return getRuleContexts(Operand_expression_v5Context.class);
		}
		public Operand_expression_v5Context operand_expression_v5(int i) {
			return getRuleContext(Operand_expression_v5Context.class,i);
		}
		public Extract_expression_from_array_sliceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extract_expression_from_array_slice; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterExtract_expression_from_array_slice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitExtract_expression_from_array_slice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitExtract_expression_from_array_slice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Extract_expression_from_array_sliceContext extract_expression_from_array_slice() throws RecognitionException {
		Extract_expression_from_array_sliceContext _localctx = new Extract_expression_from_array_sliceContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_extract_expression_from_array_slice);
		try {
			setState(339);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(317);
				((Extract_expression_from_array_sliceContext)_localctx).varRef = variable_reference();
				setState(318);
				match(ArrayIndexOpenBracket);
				setState(319);
				((Extract_expression_from_array_sliceContext)_localctx).oexprStartIdx = operand_expression_v5();
				setState(320);
				match(ColonSeparator);
				setState(321);
				((Extract_expression_from_array_sliceContext)_localctx).oexprLengthIdx = operand_expression_v5();
				setState(322);
				match(ArrayIndexCloseBracket);

				          ((Extract_expression_from_array_sliceContext)_localctx).svar = ((Extract_expression_from_array_sliceContext)_localctx).varRef.svar.getSubArray(((Extract_expression_from_array_sliceContext)_localctx).oexprStartIdx.svar.getValueAsInt(), ((Extract_expression_from_array_sliceContext)_localctx).oexprLengthIdx.svar.getValueAsInt());
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(325);
				((Extract_expression_from_array_sliceContext)_localctx).varRef = variable_reference();
				setState(326);
				match(ArrayIndexOpenBracket);
				setState(327);
				((Extract_expression_from_array_sliceContext)_localctx).oexprStartIdx = operand_expression_v5();
				setState(328);
				match(ColonSeparator);
				setState(329);
				match(ArrayIndexCloseBracket);

				          ((Extract_expression_from_array_sliceContext)_localctx).svar = ((Extract_expression_from_array_sliceContext)_localctx).varRef.svar.getSubArray(((Extract_expression_from_array_sliceContext)_localctx).oexprStartIdx.svar.getValueAsInt());
				     
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(332);
				((Extract_expression_from_array_sliceContext)_localctx).varRef = variable_reference();
				setState(333);
				match(ArrayIndexOpenBracket);
				setState(334);
				match(ColonSeparator);
				setState(335);
				((Extract_expression_from_array_sliceContext)_localctx).oexprLengthIdx = operand_expression_v5();
				setState(336);
				match(ArrayIndexCloseBracket);

				          ((Extract_expression_from_array_sliceContext)_localctx).svar = ((Extract_expression_from_array_sliceContext)_localctx).varRef.svar.getSubArray(0, ((Extract_expression_from_array_sliceContext)_localctx).oexprLengthIdx.svar.getValueAsInt());
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Operand_expression_v6Context extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expression_v5Context oe;
		public Extract_expression_from_array_sliceContext asExpr;
		public Operand_expression_v5Context operand_expression_v5() {
			return getRuleContext(Operand_expression_v5Context.class,0);
		}
		public Extract_expression_from_array_sliceContext extract_expression_from_array_slice() {
			return getRuleContext(Extract_expression_from_array_sliceContext.class,0);
		}
		public Operand_expression_v6Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand_expression_v6; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterOperand_expression_v6(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitOperand_expression_v6(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitOperand_expression_v6(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operand_expression_v6Context operand_expression_v6() throws RecognitionException {
		Operand_expression_v6Context _localctx = new Operand_expression_v6Context(_ctx, getState());
		enterRule(_localctx, 44, RULE_operand_expression_v6);
		try {
			setState(347);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(341);
				((Operand_expression_v6Context)_localctx).oe = operand_expression_v5();

				          ((Operand_expression_v6Context)_localctx).svar = ((Operand_expression_v6Context)_localctx).oe.svar;
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(344);
				((Operand_expression_v6Context)_localctx).asExpr = extract_expression_from_array_slice();

				          ((Operand_expression_v6Context)_localctx).svar = ((Operand_expression_v6Context)_localctx).asExpr.svar;
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Exec_chameleon_commandContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expression_v6Context oe;
		public TerminalNode ExecCommandStartSymbol() { return getToken(ChameleonScriptParser.ExecCommandStartSymbol, 0); }
		public TerminalNode ClosedParens() { return getToken(ChameleonScriptParser.ClosedParens, 0); }
		public Operand_expression_v6Context operand_expression_v6() {
			return getRuleContext(Operand_expression_v6Context.class,0);
		}
		public Exec_chameleon_commandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exec_chameleon_command; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterExec_chameleon_command(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitExec_chameleon_command(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitExec_chameleon_command(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Exec_chameleon_commandContext exec_chameleon_command() throws RecognitionException {
		Exec_chameleon_commandContext _localctx = new Exec_chameleon_commandContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_exec_chameleon_command);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(349);
			match(ExecCommandStartSymbol);
			setState(350);
			((Exec_chameleon_commandContext)_localctx).oe = operand_expression_v6();
			setState(351);
			match(ClosedParens);

			          ((Exec_chameleon_commandContext)_localctx).svar = ChameleonIOHandler.executeChameleonCommandForResult(((Exec_chameleon_commandContext)_localctx).oe.svar.getValueAsString());
			     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Scripting_api_function_resultContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Token funcName;
		public Function_args_listContext funcArgs;
		public TerminalNode ClosedParens() { return getToken(ChameleonScriptParser.ClosedParens, 0); }
		public TerminalNode ScriptingAPIFunctionName() { return getToken(ChameleonScriptParser.ScriptingAPIFunctionName, 0); }
		public Function_args_listContext function_args_list() {
			return getRuleContext(Function_args_listContext.class,0);
		}
		public Scripting_api_function_resultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scripting_api_function_result; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterScripting_api_function_result(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitScripting_api_function_result(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitScripting_api_function_result(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Scripting_api_function_resultContext scripting_api_function_result() throws RecognitionException {
		Scripting_api_function_resultContext _localctx = new Scripting_api_function_resultContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_scripting_api_function_result);
		try {
			setState(362);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(354);
				((Scripting_api_function_resultContext)_localctx).funcName = match(ScriptingAPIFunctionName);
				setState(355);
				match(ClosedParens);

				          ((Scripting_api_function_resultContext)_localctx).svar = ScriptingFunctions.callFunction((((Scripting_api_function_resultContext)_localctx).funcName!=null?((Scripting_api_function_resultContext)_localctx).funcName.getText():null).replaceAll("\\(", ""), new ArrayList<ScriptVariable>());
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(357);
				((Scripting_api_function_resultContext)_localctx).funcName = match(ScriptingAPIFunctionName);
				setState(358);
				((Scripting_api_function_resultContext)_localctx).funcArgs = function_args_list();
				setState(359);
				match(ClosedParens);

				          ((Scripting_api_function_resultContext)_localctx).svar = ScriptingFunctions.callFunction((((Scripting_api_function_resultContext)_localctx).funcName!=null?((Scripting_api_function_resultContext)_localctx).funcName.getText():null).replaceAll("\\(", ""), ((Scripting_api_function_resultContext)_localctx).funcArgs.varsList);
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Operand_expression_v7Context extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expression_v6Context oe;
		public Exec_chameleon_commandContext ecc;
		public Scripting_api_function_resultContext funcResult;
		public Operand_expression_v6Context operand_expression_v6() {
			return getRuleContext(Operand_expression_v6Context.class,0);
		}
		public Exec_chameleon_commandContext exec_chameleon_command() {
			return getRuleContext(Exec_chameleon_commandContext.class,0);
		}
		public Scripting_api_function_resultContext scripting_api_function_result() {
			return getRuleContext(Scripting_api_function_resultContext.class,0);
		}
		public Operand_expression_v7Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand_expression_v7; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterOperand_expression_v7(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitOperand_expression_v7(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitOperand_expression_v7(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operand_expression_v7Context operand_expression_v7() throws RecognitionException {
		Operand_expression_v7Context _localctx = new Operand_expression_v7Context(_ctx, getState());
		enterRule(_localctx, 50, RULE_operand_expression_v7);
		try {
			setState(373);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case QuotedStringLiteral:
			case QuotedHexStringLiteral:
			case OpenBrace:
			case LogicalNotOperator:
			case BitwiseNotOperator:
			case TypeCastByte:
			case TypeCastBytes:
			case TypeCastShort:
			case TypeCastInt32:
			case TypeCastBoolean:
			case TypeCastString:
			case VariableStartSymbol:
			case DecimalLiteral:
			case HexString:
			case HexByte:
			case HexLiteral:
			case BooleanLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(364);
				((Operand_expression_v7Context)_localctx).oe = operand_expression_v6();

				          ((Operand_expression_v7Context)_localctx).svar = ((Operand_expression_v7Context)_localctx).oe.svar;
				     
				}
				break;
			case ExecCommandStartSymbol:
				enterOuterAlt(_localctx, 2);
				{
				setState(367);
				((Operand_expression_v7Context)_localctx).ecc = exec_chameleon_command();

				          ((Operand_expression_v7Context)_localctx).svar = ((Operand_expression_v7Context)_localctx).ecc.svar;
				     
				}
				break;
			case ScriptingAPIFunctionName:
				enterOuterAlt(_localctx, 3);
				{
				setState(370);
				((Operand_expression_v7Context)_localctx).funcResult = scripting_api_function_result();

				          ((Operand_expression_v7Context)_localctx).svar = ((Operand_expression_v7Context)_localctx).funcResult.svar;
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Array_literal_listContext extends ParserRuleContext {
		public List<ScriptVariable> arrElts;
		public Operand_expression_v7Context curArrElt;
		public Array_literal_listContext prevArrElts;
		public Operand_expression_v7Context operand_expression_v7() {
			return getRuleContext(Operand_expression_v7Context.class,0);
		}
		public TerminalNode CommaSeparator() { return getToken(ChameleonScriptParser.CommaSeparator, 0); }
		public Array_literal_listContext array_literal_list() {
			return getRuleContext(Array_literal_listContext.class,0);
		}
		public Array_literal_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array_literal_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterArray_literal_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitArray_literal_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitArray_literal_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Array_literal_listContext array_literal_list() throws RecognitionException {
		Array_literal_listContext _localctx = new Array_literal_listContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_array_literal_list);
		try {
			setState(383);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(375);
				((Array_literal_listContext)_localctx).curArrElt = operand_expression_v7();

				          ((Array_literal_listContext)_localctx).arrElts = new ArrayList<ScriptVariable>();
				          _localctx.arrElts.add(((Array_literal_listContext)_localctx).curArrElt.svar);
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(378);
				((Array_literal_listContext)_localctx).curArrElt = operand_expression_v7();
				setState(379);
				match(CommaSeparator);
				setState(380);
				((Array_literal_listContext)_localctx).prevArrElts = array_literal_list();

				          ((Array_literal_listContext)_localctx).prevArrElts.arrElts.add(((Array_literal_listContext)_localctx).curArrElt.svar);
				          ((Array_literal_listContext)_localctx).arrElts = ((Array_literal_listContext)_localctx).prevArrElts.arrElts;
				          AndroidLogger.i("PARSER-G4", ((Array_literal_listContext)_localctx).curArrElt.svar.getValueAsString());
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Operand_expression_v72Context extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expression_v7Context oe;
		public Array_literal_listContext arr;
		public Operand_expression_v7Context operand_expression_v7() {
			return getRuleContext(Operand_expression_v7Context.class,0);
		}
		public TerminalNode DoubleOpenCurlyBrace() { return getToken(ChameleonScriptParser.DoubleOpenCurlyBrace, 0); }
		public TerminalNode DoubleClosedCurlyBrace() { return getToken(ChameleonScriptParser.DoubleClosedCurlyBrace, 0); }
		public Array_literal_listContext array_literal_list() {
			return getRuleContext(Array_literal_listContext.class,0);
		}
		public Operand_expression_v72Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand_expression_v72; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterOperand_expression_v72(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitOperand_expression_v72(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitOperand_expression_v72(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operand_expression_v72Context operand_expression_v72() throws RecognitionException {
		Operand_expression_v72Context _localctx = new Operand_expression_v72Context(_ctx, getState());
		enterRule(_localctx, 54, RULE_operand_expression_v72);
		try {
			setState(393);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ScriptingAPIFunctionName:
			case QuotedStringLiteral:
			case QuotedHexStringLiteral:
			case ExecCommandStartSymbol:
			case OpenBrace:
			case LogicalNotOperator:
			case BitwiseNotOperator:
			case TypeCastByte:
			case TypeCastBytes:
			case TypeCastShort:
			case TypeCastInt32:
			case TypeCastBoolean:
			case TypeCastString:
			case VariableStartSymbol:
			case DecimalLiteral:
			case HexString:
			case HexByte:
			case HexLiteral:
			case BooleanLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(385);
				((Operand_expression_v72Context)_localctx).oe = operand_expression_v7();

				          ((Operand_expression_v72Context)_localctx).svar = ((Operand_expression_v72Context)_localctx).oe.svar;
				     
				}
				break;
			case DoubleOpenCurlyBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(388);
				match(DoubleOpenCurlyBrace);
				setState(389);
				((Operand_expression_v72Context)_localctx).arr = array_literal_list();
				setState(390);
				match(DoubleClosedCurlyBrace);

				          ((Operand_expression_v72Context)_localctx).svar = new ScriptVariable(((Operand_expression_v72Context)_localctx).arr.arrElts);
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Assignment_operationContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Variable_referenceContext lhs;
		public Operand_expression_v72Context rhs;
		public TerminalNode DefEqualsOperator() { return getToken(ChameleonScriptParser.DefEqualsOperator, 0); }
		public Variable_referenceContext variable_reference() {
			return getRuleContext(Variable_referenceContext.class,0);
		}
		public Operand_expression_v72Context operand_expression_v72() {
			return getRuleContext(Operand_expression_v72Context.class,0);
		}
		public TerminalNode PlusEqualsOperator() { return getToken(ChameleonScriptParser.PlusEqualsOperator, 0); }
		public Assignment_operationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment_operation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterAssignment_operation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitAssignment_operation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitAssignment_operation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Assignment_operationContext assignment_operation() throws RecognitionException {
		Assignment_operationContext _localctx = new Assignment_operationContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_assignment_operation);
		try {
			setState(405);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(395);
				((Assignment_operationContext)_localctx).lhs = variable_reference();
				setState(396);
				match(DefEqualsOperator);
				setState(397);
				((Assignment_operationContext)_localctx).rhs = operand_expression_v72();

				          ((Assignment_operationContext)_localctx).svar = ((Assignment_operationContext)_localctx).rhs.svar;
				          ChameleonScripting.getRunningInstance().setVariableByName(((Assignment_operationContext)_localctx).lhs.svar.getName(), _localctx.svar);
				          AndroidLogger.i("PARSER-G4", ":= LHS VAR NAME = " + ((Assignment_operationContext)_localctx).lhs.svar.getName() + ", NEW VALUE = " + _localctx.svar.getValueAsString());
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(400);
				((Assignment_operationContext)_localctx).lhs = variable_reference();
				setState(401);
				match(PlusEqualsOperator);
				setState(402);
				((Assignment_operationContext)_localctx).rhs = operand_expression_v72();

				          ((Assignment_operationContext)_localctx).svar = ((Assignment_operationContext)_localctx).lhs.svar.binaryOperation(ScriptVariable.Operation.BINOP_PLUS, ((Assignment_operationContext)_localctx).rhs.svar);
				          ChameleonScripting.getRunningInstance().setVariableByName(((Assignment_operationContext)_localctx).lhs.svar.getName(), _localctx.svar);
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Operand_expression_v8Context extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expression_v72Context oe;
		public Assignment_operationContext aop;
		public Operand_expression_v72Context operand_expression_v72() {
			return getRuleContext(Operand_expression_v72Context.class,0);
		}
		public Assignment_operationContext assignment_operation() {
			return getRuleContext(Assignment_operationContext.class,0);
		}
		public Operand_expression_v8Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand_expression_v8; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterOperand_expression_v8(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitOperand_expression_v8(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitOperand_expression_v8(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operand_expression_v8Context operand_expression_v8() throws RecognitionException {
		Operand_expression_v8Context _localctx = new Operand_expression_v8Context(_ctx, getState());
		enterRule(_localctx, 58, RULE_operand_expression_v8);
		try {
			setState(413);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(407);
				((Operand_expression_v8Context)_localctx).oe = operand_expression_v72();

				          ((Operand_expression_v8Context)_localctx).svar = ((Operand_expression_v8Context)_localctx).oe.svar;
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(410);
				((Operand_expression_v8Context)_localctx).aop = assignment_operation();

				          ((Operand_expression_v8Context)_localctx).svar = ((Operand_expression_v8Context)_localctx).aop.svar;
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Ternary_operator_expressionContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expression_v8Context cond;
		public Operand_expression_v8Context vtrue;
		public Operand_expression_v8Context vfalse;
		public TerminalNode TernaryOperatorFirstSymbol() { return getToken(ChameleonScriptParser.TernaryOperatorFirstSymbol, 0); }
		public TerminalNode ColonSeparator() { return getToken(ChameleonScriptParser.ColonSeparator, 0); }
		public List<Operand_expression_v8Context> operand_expression_v8() {
			return getRuleContexts(Operand_expression_v8Context.class);
		}
		public Operand_expression_v8Context operand_expression_v8(int i) {
			return getRuleContext(Operand_expression_v8Context.class,i);
		}
		public Ternary_operator_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ternary_operator_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterTernary_operator_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitTernary_operator_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitTernary_operator_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ternary_operator_expressionContext ternary_operator_expression() throws RecognitionException {
		Ternary_operator_expressionContext _localctx = new Ternary_operator_expressionContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_ternary_operator_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(415);
			((Ternary_operator_expressionContext)_localctx).cond = operand_expression_v8();
			setState(416);
			match(TernaryOperatorFirstSymbol);
			setState(417);
			((Ternary_operator_expressionContext)_localctx).vtrue = operand_expression_v8();
			setState(418);
			match(ColonSeparator);
			setState(419);
			((Ternary_operator_expressionContext)_localctx).vfalse = operand_expression_v8();

			          boolean predicate = ((Ternary_operator_expressionContext)_localctx).cond.svar.getValueAsBoolean();
			          if(predicate) {
			               ((Ternary_operator_expressionContext)_localctx).svar = ((Ternary_operator_expressionContext)_localctx).vtrue.svar;
			          }
			          else {
			               ((Ternary_operator_expressionContext)_localctx).svar = ((Ternary_operator_expressionContext)_localctx).vfalse.svar;
			          }
			     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Operand_expression_v9Context extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expression_v8Context oe;
		public Ternary_operator_expressionContext toe;
		public Operand_expression_v8Context operand_expression_v8() {
			return getRuleContext(Operand_expression_v8Context.class,0);
		}
		public Ternary_operator_expressionContext ternary_operator_expression() {
			return getRuleContext(Ternary_operator_expressionContext.class,0);
		}
		public Operand_expression_v9Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand_expression_v9; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterOperand_expression_v9(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitOperand_expression_v9(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitOperand_expression_v9(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operand_expression_v9Context operand_expression_v9() throws RecognitionException {
		Operand_expression_v9Context _localctx = new Operand_expression_v9Context(_ctx, getState());
		enterRule(_localctx, 62, RULE_operand_expression_v9);
		try {
			setState(428);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(422);
				((Operand_expression_v9Context)_localctx).oe = operand_expression_v8();

				          ((Operand_expression_v9Context)_localctx).svar = ((Operand_expression_v9Context)_localctx).oe.svar;
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(425);
				((Operand_expression_v9Context)_localctx).toe = ternary_operator_expression();

				          ((Operand_expression_v9Context)_localctx).svar = ((Operand_expression_v9Context)_localctx).toe.svar;
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Operand_expressionContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Operand_expression_v9Context oe;
		public Operand_expression_v9Context operand_expression_v9() {
			return getRuleContext(Operand_expression_v9Context.class,0);
		}
		public Operand_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterOperand_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitOperand_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitOperand_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operand_expressionContext operand_expression() throws RecognitionException {
		Operand_expressionContext _localctx = new Operand_expressionContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_operand_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(430);
			((Operand_expressionContext)_localctx).oe = operand_expression_v9();

			          ((Operand_expressionContext)_localctx).svar = ((Operand_expressionContext)_localctx).oe.svar;
			     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Assignment_by_array_sliceContext extends ParserRuleContext {
		public ScriptVariable svar;
		public Variable_referenceContext varRef;
		public Operand_expressionContext oexprStartIdx;
		public Operand_expressionContext oexprLengthIdx;
		public Operand_expressionContext rhsExpr;
		public TerminalNode ArrayIndexOpenBracket() { return getToken(ChameleonScriptParser.ArrayIndexOpenBracket, 0); }
		public TerminalNode ColonSeparator() { return getToken(ChameleonScriptParser.ColonSeparator, 0); }
		public TerminalNode ArrayIndexCloseBracket() { return getToken(ChameleonScriptParser.ArrayIndexCloseBracket, 0); }
		public TerminalNode DefEqualsOperator() { return getToken(ChameleonScriptParser.DefEqualsOperator, 0); }
		public Variable_referenceContext variable_reference() {
			return getRuleContext(Variable_referenceContext.class,0);
		}
		public List<Operand_expressionContext> operand_expression() {
			return getRuleContexts(Operand_expressionContext.class);
		}
		public Operand_expressionContext operand_expression(int i) {
			return getRuleContext(Operand_expressionContext.class,i);
		}
		public Assignment_by_array_sliceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment_by_array_slice; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterAssignment_by_array_slice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitAssignment_by_array_slice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitAssignment_by_array_slice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Assignment_by_array_sliceContext assignment_by_array_slice() throws RecognitionException {
		Assignment_by_array_sliceContext _localctx = new Assignment_by_array_sliceContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_assignment_by_array_slice);
		try {
			setState(461);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(433);
				((Assignment_by_array_sliceContext)_localctx).varRef = variable_reference();
				setState(434);
				match(ArrayIndexOpenBracket);
				setState(435);
				((Assignment_by_array_sliceContext)_localctx).oexprStartIdx = operand_expression();
				setState(436);
				match(ColonSeparator);
				setState(437);
				((Assignment_by_array_sliceContext)_localctx).oexprLengthIdx = operand_expression();
				setState(438);
				match(ArrayIndexCloseBracket);
				setState(439);
				match(DefEqualsOperator);
				setState(440);
				((Assignment_by_array_sliceContext)_localctx).rhsExpr = operand_expression();

				          ((Assignment_by_array_sliceContext)_localctx).varRef.svar.insertSubArray(((Assignment_by_array_sliceContext)_localctx).oexprStartIdx.svar.getValueAsInt(), ((Assignment_by_array_sliceContext)_localctx).oexprLengthIdx.svar.getValueAsInt(), ((Assignment_by_array_sliceContext)_localctx).rhsExpr.svar);
				          ((Assignment_by_array_sliceContext)_localctx).svar = ((Assignment_by_array_sliceContext)_localctx).varRef.svar;
				          ChameleonScripting.getRunningInstance().setVariableByName(((Assignment_by_array_sliceContext)_localctx).varRef.svar.getName(), ((Assignment_by_array_sliceContext)_localctx).varRef.svar);
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(443);
				((Assignment_by_array_sliceContext)_localctx).varRef = variable_reference();
				setState(444);
				match(ArrayIndexOpenBracket);
				setState(445);
				((Assignment_by_array_sliceContext)_localctx).oexprStartIdx = operand_expression();
				setState(446);
				match(ColonSeparator);
				setState(447);
				match(ArrayIndexCloseBracket);
				setState(448);
				match(DefEqualsOperator);
				setState(449);
				((Assignment_by_array_sliceContext)_localctx).rhsExpr = operand_expression();

				          ((Assignment_by_array_sliceContext)_localctx).varRef.svar.insertSubArray(((Assignment_by_array_sliceContext)_localctx).oexprStartIdx.svar.getValueAsInt(), ((Assignment_by_array_sliceContext)_localctx).rhsExpr.svar);
				          ((Assignment_by_array_sliceContext)_localctx).svar = ((Assignment_by_array_sliceContext)_localctx).varRef.svar;
				          ChameleonScripting.getRunningInstance().setVariableByName(((Assignment_by_array_sliceContext)_localctx).varRef.svar.getName(), ((Assignment_by_array_sliceContext)_localctx).varRef.svar);
				     
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(452);
				((Assignment_by_array_sliceContext)_localctx).varRef = variable_reference();
				setState(453);
				match(ArrayIndexOpenBracket);
				setState(454);
				match(ColonSeparator);
				setState(455);
				((Assignment_by_array_sliceContext)_localctx).oexprLengthIdx = operand_expression();
				setState(456);
				match(ArrayIndexCloseBracket);
				setState(457);
				match(DefEqualsOperator);
				setState(458);
				((Assignment_by_array_sliceContext)_localctx).rhsExpr = operand_expression();

				          ((Assignment_by_array_sliceContext)_localctx).varRef.svar.insertSubArray(0, ((Assignment_by_array_sliceContext)_localctx).oexprLengthIdx.svar.getValueAsInt(), ((Assignment_by_array_sliceContext)_localctx).rhsExpr.svar);
				          ((Assignment_by_array_sliceContext)_localctx).svar = ((Assignment_by_array_sliceContext)_localctx).varRef.svar;
				          ChameleonScripting.getRunningInstance().setVariableByName(((Assignment_by_array_sliceContext)_localctx).varRef.svar.getName(), ((Assignment_by_array_sliceContext)_localctx).varRef.svar);
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Function_args_listContext extends ParserRuleContext {
		public List<ScriptVariable> varsList;
		public Operand_expressionContext var;
		public Function_args_listContext argsList;
		public TerminalNode CommaSeparator() { return getToken(ChameleonScriptParser.CommaSeparator, 0); }
		public Operand_expressionContext operand_expression() {
			return getRuleContext(Operand_expressionContext.class,0);
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
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterFunction_args_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitFunction_args_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitFunction_args_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Function_args_listContext function_args_list() throws RecognitionException {
		Function_args_listContext _localctx = new Function_args_listContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_function_args_list);
		try {
			setState(471);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(463);
				((Function_args_listContext)_localctx).var = operand_expression();
				setState(464);
				match(CommaSeparator);
				setState(465);
				((Function_args_listContext)_localctx).argsList = function_args_list();

				          ((Function_args_listContext)_localctx).argsList.varsList.add(((Function_args_listContext)_localctx).var.svar);
				          ((Function_args_listContext)_localctx).varsList = ((Function_args_listContext)_localctx).argsList.varsList;
				     
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(468);
				((Function_args_listContext)_localctx).var = operand_expression();

				          ((Function_args_listContext)_localctx).varsList = new ArrayList<ScriptVariable>();
				          _localctx.varsList.add(((Function_args_listContext)_localctx).var.svar);
				     
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

	@SuppressWarnings("CheckReturnValue")
	public static class Label_statementContext extends ParserRuleContext {
		public Token lblNameWithSep;
		public TerminalNode LabelText() { return getToken(ChameleonScriptParser.LabelText, 0); }
		public Label_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).enterLabel_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChameleonScriptParserListener ) ((ChameleonScriptParserListener)listener).exitLabel_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChameleonScriptParserVisitor ) return ((ChameleonScriptParserVisitor<? extends T>)visitor).visitLabel_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Label_statementContext label_statement() throws RecognitionException {
		Label_statementContext _localctx = new Label_statementContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_label_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(473);
			((Label_statementContext)_localctx).lblNameWithSep = match(LabelText);

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
		"\u0004\u0001r\u01dd\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0001\u0000\u0004\u0000J\b\u0000\u000b\u0000\f\u0000K\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0003\u0000Q\b\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0003\u0001[\b\u0001\u0001\u0002\u0005\u0002^\b\u0002\n\u0002\f\u0002"+
		"a\t\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003\b\u0091\b\b\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t\u00a5\b\t\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u00ad\b\n\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0003\u000b\u00b6\b\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0003\f\u00be\b\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003"+
		"\r\u00d8\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0003\u000e\u00e0\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0003\u000f\u0104\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u010c\b\u0010\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011"+
		"\u0126\b\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0003\u0012\u012e\b\u0012\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u013c\b\u0014\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u0154\b\u0015\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016"+
		"\u015c\b\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0003\u0018\u016b\b\u0018\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0003\u0019\u0176\b\u0019\u0001\u001a\u0001\u001a\u0001\u001a"+
		"\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0003\u001a"+
		"\u0180\b\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u018a\b\u001b\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0003\u001c\u0196\b\u001c\u0001\u001d"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d"+
		"\u019e\b\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e"+
		"\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f"+
		"\u0001\u001f\u0001\u001f\u0003\u001f\u01ad\b\u001f\u0001 \u0001 \u0001"+
		" \u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001"+
		"!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001"+
		"!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0003!\u01ce"+
		"\b!\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0003"+
		"\"\u01d8\b\"\u0001#\u0001#\u0001#\u0001#\u0000\u0000$\u0000\u0002\u0004"+
		"\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \""+
		"$&(*,.02468:<>@BDF\u0000\u0000\u01ee\u0000P\u0001\u0000\u0000\u0000\u0002"+
		"Z\u0001\u0000\u0000\u0000\u0004_\u0001\u0000\u0000\u0000\u0006b\u0001"+
		"\u0000\u0000\u0000\bk\u0001\u0000\u0000\u0000\nt\u0001\u0000\u0000\u0000"+
		"\f\u0081\u0001\u0000\u0000\u0000\u000e\u0085\u0001\u0000\u0000\u0000\u0010"+
		"\u0090\u0001\u0000\u0000\u0000\u0012\u00a4\u0001\u0000\u0000\u0000\u0014"+
		"\u00ac\u0001\u0000\u0000\u0000\u0016\u00b5\u0001\u0000\u0000\u0000\u0018"+
		"\u00bd\u0001\u0000\u0000\u0000\u001a\u00d7\u0001\u0000\u0000\u0000\u001c"+
		"\u00df\u0001\u0000\u0000\u0000\u001e\u0103\u0001\u0000\u0000\u0000 \u010b"+
		"\u0001\u0000\u0000\u0000\"\u0125\u0001\u0000\u0000\u0000$\u012d\u0001"+
		"\u0000\u0000\u0000&\u012f\u0001\u0000\u0000\u0000(\u013b\u0001\u0000\u0000"+
		"\u0000*\u0153\u0001\u0000\u0000\u0000,\u015b\u0001\u0000\u0000\u0000."+
		"\u015d\u0001\u0000\u0000\u00000\u016a\u0001\u0000\u0000\u00002\u0175\u0001"+
		"\u0000\u0000\u00004\u017f\u0001\u0000\u0000\u00006\u0189\u0001\u0000\u0000"+
		"\u00008\u0195\u0001\u0000\u0000\u0000:\u019d\u0001\u0000\u0000\u0000<"+
		"\u019f\u0001\u0000\u0000\u0000>\u01ac\u0001\u0000\u0000\u0000@\u01ae\u0001"+
		"\u0000\u0000\u0000B\u01cd\u0001\u0000\u0000\u0000D\u01d7\u0001\u0000\u0000"+
		"\u0000F\u01d9\u0001\u0000\u0000\u0000HJ\u0003\u0002\u0001\u0000IH\u0001"+
		"\u0000\u0000\u0000JK\u0001\u0000\u0000\u0000KI\u0001\u0000\u0000\u0000"+
		"KL\u0001\u0000\u0000\u0000LM\u0001\u0000\u0000\u0000MN\u0005\u0000\u0000"+
		"\u0001NQ\u0001\u0000\u0000\u0000OQ\u0005\u0000\u0000\u0001PI\u0001\u0000"+
		"\u0000\u0000PO\u0001\u0000\u0000\u0000Q\u0001\u0001\u0000\u0000\u0000"+
		"R[\u0003F#\u0000S[\u00038\u001c\u0000T[\u0003B!\u0000U[\u00030\u0018\u0000"+
		"V[\u0003.\u0017\u0000W[\u0003\u0006\u0003\u0000X[\u0003\b\u0004\u0000"+
		"Y[\u0003\n\u0005\u0000ZR\u0001\u0000\u0000\u0000ZS\u0001\u0000\u0000\u0000"+
		"ZT\u0001\u0000\u0000\u0000ZU\u0001\u0000\u0000\u0000ZV\u0001\u0000\u0000"+
		"\u0000ZW\u0001\u0000\u0000\u0000ZX\u0001\u0000\u0000\u0000ZY\u0001\u0000"+
		"\u0000\u0000[\u0003\u0001\u0000\u0000\u0000\\^\u0003\u0002\u0001\u0000"+
		"]\\\u0001\u0000\u0000\u0000^a\u0001\u0000\u0000\u0000_]\u0001\u0000\u0000"+
		"\u0000_`\u0001\u0000\u0000\u0000`\u0005\u0001\u0000\u0000\u0000a_\u0001"+
		"\u0000\u0000\u0000bc\u0005=\u0000\u0000cd\u0005Q\u0000\u0000de\u0003@"+
		" \u0000ef\u0005R\u0000\u0000fg\u0005U\u0000\u0000gh\u0003\u0004\u0002"+
		"\u0000hi\u0005W\u0000\u0000ij\u0006\u0003\uffff\uffff\u0000j\u0007\u0001"+
		"\u0000\u0000\u0000kl\u0005>\u0000\u0000lm\u0005Q\u0000\u0000mn\u0003@"+
		" \u0000no\u0005R\u0000\u0000op\u0005U\u0000\u0000pq\u0003\u0004\u0002"+
		"\u0000qr\u0005W\u0000\u0000rs\u0006\u0004\uffff\uffff\u0000s\t\u0001\u0000"+
		"\u0000\u0000tu\u0005>\u0000\u0000uv\u0005Q\u0000\u0000vw\u0003@ \u0000"+
		"wx\u0005R\u0000\u0000xy\u0005U\u0000\u0000yz\u0003\u0004\u0002\u0000z"+
		"{\u0005W\u0000\u0000{|\u0005?\u0000\u0000|}\u0005U\u0000\u0000}~\u0003"+
		"\u0004\u0002\u0000~\u007f\u0005W\u0000\u0000\u007f\u0080\u0006\u0005\uffff"+
		"\uffff\u0000\u0080\u000b\u0001\u0000\u0000\u0000\u0081\u0082\u0005j\u0000"+
		"\u0000\u0082\u0083\u0005l\u0000\u0000\u0083\u0084\u0006\u0006\uffff\uffff"+
		"\u0000\u0084\r\u0001\u0000\u0000\u0000\u0085\u0086\u0003\f\u0006\u0000"+
		"\u0086\u0087\u0005N\u0000\u0000\u0087\u0088\u0005l\u0000\u0000\u0088\u0089"+
		"\u0006\u0007\uffff\uffff\u0000\u0089\u000f\u0001\u0000\u0000\u0000\u008a"+
		"\u008b\u0003\u000e\u0007\u0000\u008b\u008c\u0006\b\uffff\uffff\u0000\u008c"+
		"\u0091\u0001\u0000\u0000\u0000\u008d\u008e\u0003\f\u0006\u0000\u008e\u008f"+
		"\u0006\b\uffff\uffff\u0000\u008f\u0091\u0001\u0000\u0000\u0000\u0090\u008a"+
		"\u0001\u0000\u0000\u0000\u0090\u008d\u0001\u0000\u0000\u0000\u0091\u0011"+
		"\u0001\u0000\u0000\u0000\u0092\u0093\u0005m\u0000\u0000\u0093\u00a5\u0006"+
		"\t\uffff\uffff\u0000\u0094\u0095\u0005n\u0000\u0000\u0095\u00a5\u0006"+
		"\t\uffff\uffff\u0000\u0096\u0097\u0005o\u0000\u0000\u0097\u00a5\u0006"+
		"\t\uffff\uffff\u0000\u0098\u0099\u0005p\u0000\u0000\u0099\u00a5\u0006"+
		"\t\uffff\uffff\u0000\u009a\u009b\u0005q\u0000\u0000\u009b\u00a5\u0006"+
		"\t\uffff\uffff\u0000\u009c\u009d\u0003\u0014\n\u0000\u009d\u009e\u0006"+
		"\t\uffff\uffff\u0000\u009e\u00a5\u0001\u0000\u0000\u0000\u009f\u00a0\u0005"+
		"U\u0000\u0000\u00a0\u00a1\u0003\u0016\u000b\u0000\u00a1\u00a2\u0005W\u0000"+
		"\u0000\u00a2\u00a3\u0006\t\uffff\uffff\u0000\u00a3\u00a5\u0001\u0000\u0000"+
		"\u0000\u00a4\u0092\u0001\u0000\u0000\u0000\u00a4\u0094\u0001\u0000\u0000"+
		"\u0000\u00a4\u0096\u0001\u0000\u0000\u0000\u00a4\u0098\u0001\u0000\u0000"+
		"\u0000\u00a4\u009a\u0001\u0000\u0000\u0000\u00a4\u009c\u0001\u0000\u0000"+
		"\u0000\u00a4\u009f\u0001\u0000\u0000\u0000\u00a5\u0013\u0001\u0000\u0000"+
		"\u0000\u00a6\u00a7\u0005@\u0000\u0000\u00a7\u00ad\u0006\n\uffff\uffff"+
		"\u0000\u00a8\u00a9\u0005A\u0000\u0000\u00a9\u00ad\u0006\n\uffff\uffff"+
		"\u0000\u00aa\u00ab\u0005A\u0000\u0000\u00ab\u00ad\u0006\n\uffff\uffff"+
		"\u0000\u00ac\u00a6\u0001\u0000\u0000\u0000\u00ac\u00a8\u0001\u0000\u0000"+
		"\u0000\u00ac\u00aa\u0001\u0000\u0000\u0000\u00ad\u0015\u0001\u0000\u0000"+
		"\u0000\u00ae\u00af\u0005o\u0000\u0000\u00af\u00b6\u0006\u000b\uffff\uffff"+
		"\u0000\u00b0\u00b1\u0005o\u0000\u0000\u00b1\u00b2\u0005P\u0000\u0000\u00b2"+
		"\u00b3\u0003\u0016\u000b\u0000\u00b3\u00b4\u0006\u000b\uffff\uffff\u0000"+
		"\u00b4\u00b6\u0001\u0000\u0000\u0000\u00b5\u00ae\u0001\u0000\u0000\u0000"+
		"\u00b5\u00b0\u0001\u0000\u0000\u0000\u00b6\u0017\u0001\u0000\u0000\u0000"+
		"\u00b7\u00b8\u0003\u0010\b\u0000\u00b8\u00b9\u0006\f\uffff\uffff\u0000"+
		"\u00b9\u00be\u0001\u0000\u0000\u0000\u00ba\u00bb\u0003\u0012\t\u0000\u00bb"+
		"\u00bc\u0006\f\uffff\uffff\u0000\u00bc\u00be\u0001\u0000\u0000\u0000\u00bd"+
		"\u00b7\u0001\u0000\u0000\u0000\u00bd\u00ba\u0001\u0000\u0000\u0000\u00be"+
		"\u0019\u0001\u0000\u0000\u0000\u00bf\u00c0\u0005b\u0000\u0000\u00c0\u00c1"+
		"\u0003\u0018\f\u0000\u00c1\u00c2\u0006\r\uffff\uffff\u0000\u00c2\u00d8"+
		"\u0001\u0000\u0000\u0000\u00c3\u00c4\u0005d\u0000\u0000\u00c4\u00c5\u0003"+
		"\u0018\f\u0000\u00c5\u00c6\u0006\r\uffff\uffff\u0000\u00c6\u00d8\u0001"+
		"\u0000\u0000\u0000\u00c7\u00c8\u0005e\u0000\u0000\u00c8\u00c9\u0003\u0018"+
		"\f\u0000\u00c9\u00ca\u0006\r\uffff\uffff\u0000\u00ca\u00d8\u0001\u0000"+
		"\u0000\u0000\u00cb\u00cc\u0005f\u0000\u0000\u00cc\u00cd\u0003\u0018\f"+
		"\u0000\u00cd\u00ce\u0006\r\uffff\uffff\u0000\u00ce\u00d8\u0001\u0000\u0000"+
		"\u0000\u00cf\u00d0\u0005g\u0000\u0000\u00d0\u00d1\u0003\u0018\f\u0000"+
		"\u00d1\u00d2\u0006\r\uffff\uffff\u0000\u00d2\u00d8\u0001\u0000\u0000\u0000"+
		"\u00d3\u00d4\u0005c\u0000\u0000\u00d4\u00d5\u0003\u0018\f\u0000\u00d5"+
		"\u00d6\u0006\r\uffff\uffff\u0000\u00d6\u00d8\u0001\u0000\u0000\u0000\u00d7"+
		"\u00bf\u0001\u0000\u0000\u0000\u00d7\u00c3\u0001\u0000\u0000\u0000\u00d7"+
		"\u00c7\u0001\u0000\u0000\u0000\u00d7\u00cb\u0001\u0000\u0000\u0000\u00d7"+
		"\u00cf\u0001\u0000\u0000\u0000\u00d7\u00d3\u0001\u0000\u0000\u0000\u00d8"+
		"\u001b\u0001\u0000\u0000\u0000\u00d9\u00da\u0003\u0018\f\u0000\u00da\u00db"+
		"\u0006\u000e\uffff\uffff\u0000\u00db\u00e0\u0001\u0000\u0000\u0000\u00dc"+
		"\u00dd\u0003\u001a\r\u0000\u00dd\u00de\u0006\u000e\uffff\uffff\u0000\u00de"+
		"\u00e0\u0001\u0000\u0000\u0000\u00df\u00d9\u0001\u0000\u0000\u0000\u00df"+
		"\u00dc\u0001\u0000\u0000\u0000\u00e0\u001d\u0001\u0000\u0000\u0000\u00e1"+
		"\u00e2\u0003\u001c\u000e\u0000\u00e2\u00e3\u0005\\\u0000\u0000\u00e3\u00e4"+
		"\u0003\u001c\u000e\u0000\u00e4\u00e5\u0006\u000f\uffff\uffff\u0000\u00e5"+
		"\u0104\u0001\u0000\u0000\u0000\u00e6\u00e7\u0003\u001c\u000e\u0000\u00e7"+
		"\u00e8\u0005[\u0000\u0000\u00e8\u00e9\u0003\u001c\u000e\u0000\u00e9\u00ea"+
		"\u0006\u000f\uffff\uffff\u0000\u00ea\u0104\u0001\u0000\u0000\u0000\u00eb"+
		"\u00ec\u0003\u001c\u000e\u0000\u00ec\u00ed\u0005]\u0000\u0000\u00ed\u00ee"+
		"\u0003\u001c\u000e\u0000\u00ee\u00ef\u0006\u000f\uffff\uffff\u0000\u00ef"+
		"\u0104\u0001\u0000\u0000\u0000\u00f0\u00f1\u0003\u001c\u000e\u0000\u00f1"+
		"\u00f2\u0005^\u0000\u0000\u00f2\u00f3\u0003\u001c\u000e\u0000\u00f3\u00f4"+
		"\u0006\u000f\uffff\uffff\u0000\u00f4\u0104\u0001\u0000\u0000\u0000\u00f5"+
		"\u00f6\u0003\u001c\u000e\u0000\u00f6\u00f7\u0005_\u0000\u0000\u00f7\u00f8"+
		"\u0003\u001c\u000e\u0000\u00f8\u00f9\u0006\u000f\uffff\uffff\u0000\u00f9"+
		"\u0104\u0001\u0000\u0000\u0000\u00fa\u00fb\u0003\u001c\u000e\u0000\u00fb"+
		"\u00fc\u0005a\u0000\u0000\u00fc\u00fd\u0003\u001c\u000e\u0000\u00fd\u00fe"+
		"\u0006\u000f\uffff\uffff\u0000\u00fe\u0104\u0001\u0000\u0000\u0000\u00ff"+
		"\u0100\u0005`\u0000\u0000\u0100\u0101\u0003\u001c\u000e\u0000\u0101\u0102"+
		"\u0006\u000f\uffff\uffff\u0000\u0102\u0104\u0001\u0000\u0000\u0000\u0103"+
		"\u00e1\u0001\u0000\u0000\u0000\u0103\u00e6\u0001\u0000\u0000\u0000\u0103"+
		"\u00eb\u0001\u0000\u0000\u0000\u0103\u00f0\u0001\u0000\u0000\u0000\u0103"+
		"\u00f5\u0001\u0000\u0000\u0000\u0103\u00fa\u0001\u0000\u0000\u0000\u0103"+
		"\u00ff\u0001\u0000\u0000\u0000\u0104\u001f\u0001\u0000\u0000\u0000\u0105"+
		"\u0106\u0003\u001c\u000e\u0000\u0106\u0107\u0006\u0010\uffff\uffff\u0000"+
		"\u0107\u010c\u0001\u0000\u0000\u0000\u0108\u0109\u0003\u001e\u000f\u0000"+
		"\u0109\u010a\u0006\u0010\uffff\uffff\u0000\u010a\u010c\u0001\u0000\u0000"+
		"\u0000\u010b\u0105\u0001\u0000\u0000\u0000\u010b\u0108\u0001\u0000\u0000"+
		"\u0000\u010c!\u0001\u0000\u0000\u0000\u010d\u010e\u0003 \u0010\u0000\u010e"+
		"\u010f\u0005H\u0000\u0000\u010f\u0110\u0003 \u0010\u0000\u0110\u0111\u0006"+
		"\u0011\uffff\uffff\u0000\u0111\u0126\u0001\u0000\u0000\u0000\u0112\u0113"+
		"\u0003 \u0010\u0000\u0113\u0114\u0005I\u0000\u0000\u0114\u0115\u0003 "+
		"\u0010\u0000\u0115\u0116\u0006\u0011\uffff\uffff\u0000\u0116\u0126\u0001"+
		"\u0000\u0000\u0000\u0117\u0118\u0003 \u0010\u0000\u0118\u0119\u0005X\u0000"+
		"\u0000\u0119\u011a\u0003 \u0010\u0000\u011a\u011b\u0006\u0011\uffff\uffff"+
		"\u0000\u011b\u0126\u0001\u0000\u0000\u0000\u011c\u011d\u0003 \u0010\u0000"+
		"\u011d\u011e\u0005Y\u0000\u0000\u011e\u011f\u0003 \u0010\u0000\u011f\u0120"+
		"\u0006\u0011\uffff\uffff\u0000\u0120\u0126\u0001\u0000\u0000\u0000\u0121"+
		"\u0122\u0005Z\u0000\u0000\u0122\u0123\u0003 \u0010\u0000\u0123\u0124\u0006"+
		"\u0011\uffff\uffff\u0000\u0124\u0126\u0001\u0000\u0000\u0000\u0125\u010d"+
		"\u0001\u0000\u0000\u0000\u0125\u0112\u0001\u0000\u0000\u0000\u0125\u0117"+
		"\u0001\u0000\u0000\u0000\u0125\u011c\u0001\u0000\u0000\u0000\u0125\u0121"+
		"\u0001\u0000\u0000\u0000\u0126#\u0001\u0000\u0000\u0000\u0127\u0128\u0003"+
		" \u0010\u0000\u0128\u0129\u0006\u0012\uffff\uffff\u0000\u0129\u012e\u0001"+
		"\u0000\u0000\u0000\u012a\u012b\u0003\"\u0011\u0000\u012b\u012c\u0006\u0012"+
		"\uffff\uffff\u0000\u012c\u012e\u0001\u0000\u0000\u0000\u012d\u0127\u0001"+
		"\u0000\u0000\u0000\u012d\u012a\u0001\u0000\u0000\u0000\u012e%\u0001\u0000"+
		"\u0000\u0000\u012f\u0130\u0003\u0010\b\u0000\u0130\u0131\u0005h\u0000"+
		"\u0000\u0131\u0132\u0003$\u0012\u0000\u0132\u0133\u0005i\u0000\u0000\u0133"+
		"\u0134\u0006\u0013\uffff\uffff\u0000\u0134\'\u0001\u0000\u0000\u0000\u0135"+
		"\u0136\u0003$\u0012\u0000\u0136\u0137\u0006\u0014\uffff\uffff\u0000\u0137"+
		"\u013c\u0001\u0000\u0000\u0000\u0138\u0139\u0003&\u0013\u0000\u0139\u013a"+
		"\u0006\u0014\uffff\uffff\u0000\u013a\u013c\u0001\u0000\u0000\u0000\u013b"+
		"\u0135\u0001\u0000\u0000\u0000\u013b\u0138\u0001\u0000\u0000\u0000\u013c"+
		")\u0001\u0000\u0000\u0000\u013d\u013e\u0003\u0010\b\u0000\u013e\u013f"+
		"\u0005h\u0000\u0000\u013f\u0140\u0003(\u0014\u0000\u0140\u0141\u0005S"+
		"\u0000\u0000\u0141\u0142\u0003(\u0014\u0000\u0142\u0143\u0005i\u0000\u0000"+
		"\u0143\u0144\u0006\u0015\uffff\uffff\u0000\u0144\u0154\u0001\u0000\u0000"+
		"\u0000\u0145\u0146\u0003\u0010\b\u0000\u0146\u0147\u0005h\u0000\u0000"+
		"\u0147\u0148\u0003(\u0014\u0000\u0148\u0149\u0005S\u0000\u0000\u0149\u014a"+
		"\u0005i\u0000\u0000\u014a\u014b\u0006\u0015\uffff\uffff\u0000\u014b\u0154"+
		"\u0001\u0000\u0000\u0000\u014c\u014d\u0003\u0010\b\u0000\u014d\u014e\u0005"+
		"h\u0000\u0000\u014e\u014f\u0005S\u0000\u0000\u014f\u0150\u0003(\u0014"+
		"\u0000\u0150\u0151\u0005i\u0000\u0000\u0151\u0152\u0006\u0015\uffff\uffff"+
		"\u0000\u0152\u0154\u0001\u0000\u0000\u0000\u0153\u013d\u0001\u0000\u0000"+
		"\u0000\u0153\u0145\u0001\u0000\u0000\u0000\u0153\u014c\u0001\u0000\u0000"+
		"\u0000\u0154+\u0001\u0000\u0000\u0000\u0155\u0156\u0003(\u0014\u0000\u0156"+
		"\u0157\u0006\u0016\uffff\uffff\u0000\u0157\u015c\u0001\u0000\u0000\u0000"+
		"\u0158\u0159\u0003*\u0015\u0000\u0159\u015a\u0006\u0016\uffff\uffff\u0000"+
		"\u015a\u015c\u0001\u0000\u0000\u0000\u015b\u0155\u0001\u0000\u0000\u0000"+
		"\u015b\u0158\u0001\u0000\u0000\u0000\u015c-\u0001\u0000\u0000\u0000\u015d"+
		"\u015e\u0005L\u0000\u0000\u015e\u015f\u0003,\u0016\u0000\u015f\u0160\u0005"+
		"R\u0000\u0000\u0160\u0161\u0006\u0017\uffff\uffff\u0000\u0161/\u0001\u0000"+
		"\u0000\u0000\u0162\u0163\u0005\u0001\u0000\u0000\u0163\u0164\u0005R\u0000"+
		"\u0000\u0164\u016b\u0006\u0018\uffff\uffff\u0000\u0165\u0166\u0005\u0001"+
		"\u0000\u0000\u0166\u0167\u0003D\"\u0000\u0167\u0168\u0005R\u0000\u0000"+
		"\u0168\u0169\u0006\u0018\uffff\uffff\u0000\u0169\u016b\u0001\u0000\u0000"+
		"\u0000\u016a\u0162\u0001\u0000\u0000\u0000\u016a\u0165\u0001\u0000\u0000"+
		"\u0000\u016b1\u0001\u0000\u0000\u0000\u016c\u016d\u0003,\u0016\u0000\u016d"+
		"\u016e\u0006\u0019\uffff\uffff\u0000\u016e\u0176\u0001\u0000\u0000\u0000"+
		"\u016f\u0170\u0003.\u0017\u0000\u0170\u0171\u0006\u0019\uffff\uffff\u0000"+
		"\u0171\u0176\u0001\u0000\u0000\u0000\u0172\u0173\u00030\u0018\u0000\u0173"+
		"\u0174\u0006\u0019\uffff\uffff\u0000\u0174\u0176\u0001\u0000\u0000\u0000"+
		"\u0175\u016c\u0001\u0000\u0000\u0000\u0175\u016f\u0001\u0000\u0000\u0000"+
		"\u0175\u0172\u0001\u0000\u0000\u0000\u01763\u0001\u0000\u0000\u0000\u0177"+
		"\u0178\u00032\u0019\u0000\u0178\u0179\u0006\u001a\uffff\uffff\u0000\u0179"+
		"\u0180\u0001\u0000\u0000\u0000\u017a\u017b\u00032\u0019\u0000\u017b\u017c"+
		"\u0005P\u0000\u0000\u017c\u017d\u00034\u001a\u0000\u017d\u017e\u0006\u001a"+
		"\uffff\uffff\u0000\u017e\u0180\u0001\u0000\u0000\u0000\u017f\u0177\u0001"+
		"\u0000\u0000\u0000\u017f\u017a\u0001\u0000\u0000\u0000\u01805\u0001\u0000"+
		"\u0000\u0000\u0181\u0182\u00032\u0019\u0000\u0182\u0183\u0006\u001b\uffff"+
		"\uffff\u0000\u0183\u018a\u0001\u0000\u0000\u0000\u0184\u0185\u0005T\u0000"+
		"\u0000\u0185\u0186\u00034\u001a\u0000\u0186\u0187\u0005V\u0000\u0000\u0187"+
		"\u0188\u0006\u001b\uffff\uffff\u0000\u0188\u018a\u0001\u0000\u0000\u0000"+
		"\u0189\u0181\u0001\u0000\u0000\u0000\u0189\u0184\u0001\u0000\u0000\u0000"+
		"\u018a7\u0001\u0000\u0000\u0000\u018b\u018c\u0003\u0010\b\u0000\u018c"+
		"\u018d\u0005K\u0000\u0000\u018d\u018e\u00036\u001b\u0000\u018e\u018f\u0006"+
		"\u001c\uffff\uffff\u0000\u018f\u0196\u0001\u0000\u0000\u0000\u0190\u0191"+
		"\u0003\u0010\b\u0000\u0191\u0192\u0005J\u0000\u0000\u0192\u0193\u0003"+
		"6\u001b\u0000\u0193\u0194\u0006\u001c\uffff\uffff\u0000\u0194\u0196\u0001"+
		"\u0000\u0000\u0000\u0195\u018b\u0001\u0000\u0000\u0000\u0195\u0190\u0001"+
		"\u0000\u0000\u0000\u01969\u0001\u0000\u0000\u0000\u0197\u0198\u00036\u001b"+
		"\u0000\u0198\u0199\u0006\u001d\uffff\uffff\u0000\u0199\u019e\u0001\u0000"+
		"\u0000\u0000\u019a\u019b\u00038\u001c\u0000\u019b\u019c\u0006\u001d\uffff"+
		"\uffff\u0000\u019c\u019e\u0001\u0000\u0000\u0000\u019d\u0197\u0001\u0000"+
		"\u0000\u0000\u019d\u019a\u0001\u0000\u0000\u0000\u019e;\u0001\u0000\u0000"+
		"\u0000\u019f\u01a0\u0003:\u001d\u0000\u01a0\u01a1\u0005M\u0000\u0000\u01a1"+
		"\u01a2\u0003:\u001d\u0000\u01a2\u01a3\u0005S\u0000\u0000\u01a3\u01a4\u0003"+
		":\u001d\u0000\u01a4\u01a5\u0006\u001e\uffff\uffff\u0000\u01a5=\u0001\u0000"+
		"\u0000\u0000\u01a6\u01a7\u0003:\u001d\u0000\u01a7\u01a8\u0006\u001f\uffff"+
		"\uffff\u0000\u01a8\u01ad\u0001\u0000\u0000\u0000\u01a9\u01aa\u0003<\u001e"+
		"\u0000\u01aa\u01ab\u0006\u001f\uffff\uffff\u0000\u01ab\u01ad\u0001\u0000"+
		"\u0000\u0000\u01ac\u01a6\u0001\u0000\u0000\u0000\u01ac\u01a9\u0001\u0000"+
		"\u0000\u0000\u01ad?\u0001\u0000\u0000\u0000\u01ae\u01af\u0003>\u001f\u0000"+
		"\u01af\u01b0\u0006 \uffff\uffff\u0000\u01b0A\u0001\u0000\u0000\u0000\u01b1"+
		"\u01b2\u0003\u0010\b\u0000\u01b2\u01b3\u0005h\u0000\u0000\u01b3\u01b4"+
		"\u0003@ \u0000\u01b4\u01b5\u0005S\u0000\u0000\u01b5\u01b6\u0003@ \u0000"+
		"\u01b6\u01b7\u0005i\u0000\u0000\u01b7\u01b8\u0005K\u0000\u0000\u01b8\u01b9"+
		"\u0003@ \u0000\u01b9\u01ba\u0006!\uffff\uffff\u0000\u01ba\u01ce\u0001"+
		"\u0000\u0000\u0000\u01bb\u01bc\u0003\u0010\b\u0000\u01bc\u01bd\u0005h"+
		"\u0000\u0000\u01bd\u01be\u0003@ \u0000\u01be\u01bf\u0005S\u0000\u0000"+
		"\u01bf\u01c0\u0005i\u0000\u0000\u01c0\u01c1\u0005K\u0000\u0000\u01c1\u01c2"+
		"\u0003@ \u0000\u01c2\u01c3\u0006!\uffff\uffff\u0000\u01c3\u01ce\u0001"+
		"\u0000\u0000\u0000\u01c4\u01c5\u0003\u0010\b\u0000\u01c5\u01c6\u0005h"+
		"\u0000\u0000\u01c6\u01c7\u0005S\u0000\u0000\u01c7\u01c8\u0003@ \u0000"+
		"\u01c8\u01c9\u0005i\u0000\u0000\u01c9\u01ca\u0005K\u0000\u0000\u01ca\u01cb"+
		"\u0003@ \u0000\u01cb\u01cc\u0006!\uffff\uffff\u0000\u01cc\u01ce\u0001"+
		"\u0000\u0000\u0000\u01cd\u01b1\u0001\u0000\u0000\u0000\u01cd\u01bb\u0001"+
		"\u0000\u0000\u0000\u01cd\u01c4\u0001\u0000\u0000\u0000\u01ceC\u0001\u0000"+
		"\u0000\u0000\u01cf\u01d0\u0003@ \u0000\u01d0\u01d1\u0005P\u0000\u0000"+
		"\u01d1\u01d2\u0003D\"\u0000\u01d2\u01d3\u0006\"\uffff\uffff\u0000\u01d3"+
		"\u01d8\u0001\u0000\u0000\u0000\u01d4\u01d5\u0003@ \u0000\u01d5\u01d6\u0006"+
		"\"\uffff\uffff\u0000\u01d6\u01d8\u0001\u0000\u0000\u0000\u01d7\u01cf\u0001"+
		"\u0000\u0000\u0000\u01d7\u01d4\u0001\u0000\u0000\u0000\u01d8E\u0001\u0000"+
		"\u0000\u0000\u01d9\u01da\u0005r\u0000\u0000\u01da\u01db\u0006#\uffff\uffff"+
		"\u0000\u01dbG\u0001\u0000\u0000\u0000\u001bKPZ_\u0090\u00a4\u00ac\u00b5"+
		"\u00bd\u00d7\u00df\u0103\u010b\u0125\u012d\u013b\u0153\u015b\u016a\u0175"+
		"\u017f\u0189\u0195\u019d\u01ac\u01cd\u01d7";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}