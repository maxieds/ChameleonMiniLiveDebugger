// Generated from /Users/mschmidt34/ChameleonMiniLiveDebugger/app/src/main/java/com/maxieds/chameleonminilivedebugger/ScriptingAPI/ScriptingPrimitives.g4 by ANTLR 4.8
package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

     package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ScriptingPrimitivesLexer extends Lexer {
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
		TypeCastString=44;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"WhiteSpaceText", "WhiteSpace", "NewLineBreak", "CStyleBlockComment", 
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
			"TypeCastString"
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
			"TypeCastString"
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


	public ScriptingPrimitivesLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ScriptingPrimitives.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2.\u0168\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\3\2\3\2\6\2^\n\2\r\2\16\2_\3\3\6\3c\n\3\r\3\16\3d\3\3\3\3\3"+
		"\4\3\4\5\4k\n\4\3\4\5\4n\n\4\3\4\3\4\3\5\3\5\3\5\3\5\7\5v\n\5\f\5\16\5"+
		"y\13\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\7\6\u0084\n\6\f\6\16\6\u0087"+
		"\13\6\3\6\3\6\3\6\3\6\3\7\3\7\7\7\u008f\n\7\f\7\16\7\u0092\13\7\3\7\3"+
		"\7\3\7\3\7\3\b\3\b\3\b\5\b\u009b\n\b\3\t\3\t\3\n\6\n\u00a0\n\n\r\n\16"+
		"\n\u00a1\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3"+
		"\13\5\13\u00b1\n\13\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u00b9\n\f\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u00d6\n\r\3\16\3\16\3\17\3\17\7\17\u00dc"+
		"\n\17\f\17\16\17\u00df\13\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3"+
		"\21\3\21\3\21\3\21\7\21\u00ed\n\21\f\21\16\21\u00f0\13\21\3\21\3\21\3"+
		"\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\5\26\u00fd\n\26\3\27\3\27"+
		"\5\27\u0101\n\27\3\30\3\30\3\31\3\31\7\31\u0107\n\31\f\31\16\31\u010a"+
		"\13\31\3\32\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\5\34\u0117"+
		"\n\34\3\35\3\35\3\35\3\35\5\35\u011d\n\35\3\36\3\36\3\36\3\36\5\36\u0123"+
		"\n\36\3\37\3\37\3\37\3 \3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3"+
		"\'\3\'\3\'\5\'\u013a\n\'\3(\3(\3(\3)\3)\3)\3)\3)\3)\3)\3*\3*\3*\3*\3*"+
		"\3*\3*\3*\3+\3+\3+\3+\3+\3+\3+\3+\3,\3,\3,\3,\3,\3,\3,\3,\3,\3,\3-\3-"+
		"\3-\3-\3-\3-\3-\3-\3-\3w\2.\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25"+
		"\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32"+
		"\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.\3\2\b\5\2\13"+
		"\f\17\17\"\"\3\2\f\f\5\2\62;CHch\5\2BHR\u0135\u0137\u0178\5\2C\\aac|\3"+
		"\2\62;\2\u0183\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3"+
		"\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2"+
		"\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3"+
		"\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2"+
		"\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\2"+
		"9\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3"+
		"\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2"+
		"\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\3]\3\2\2\2\5b\3\2\2\2\7"+
		"m\3\2\2\2\tq\3\2\2\2\13\177\3\2\2\2\r\u008c\3\2\2\2\17\u009a\3\2\2\2\21"+
		"\u009c\3\2\2\2\23\u009f\3\2\2\2\25\u00b0\3\2\2\2\27\u00b8\3\2\2\2\31\u00d5"+
		"\3\2\2\2\33\u00d7\3\2\2\2\35\u00d9\3\2\2\2\37\u00e2\3\2\2\2!\u00e8\3\2"+
		"\2\2#\u00f3\3\2\2\2%\u00f5\3\2\2\2\'\u00f7\3\2\2\2)\u00f9\3\2\2\2+\u00fc"+
		"\3\2\2\2-\u0100\3\2\2\2/\u0102\3\2\2\2\61\u0104\3\2\2\2\63\u010b\3\2\2"+
		"\2\65\u010e\3\2\2\2\67\u0116\3\2\2\29\u011c\3\2\2\2;\u0122\3\2\2\2=\u0124"+
		"\3\2\2\2?\u0127\3\2\2\2A\u012a\3\2\2\2C\u012c\3\2\2\2E\u012e\3\2\2\2G"+
		"\u0130\3\2\2\2I\u0132\3\2\2\2K\u0134\3\2\2\2M\u0139\3\2\2\2O\u013b\3\2"+
		"\2\2Q\u013e\3\2\2\2S\u0145\3\2\2\2U\u014d\3\2\2\2W\u0155\3\2\2\2Y\u015f"+
		"\3\2\2\2[^\5\5\3\2\\^\5\7\4\2][\3\2\2\2]\\\3\2\2\2^_\3\2\2\2_]\3\2\2\2"+
		"_`\3\2\2\2`\4\3\2\2\2ac\t\2\2\2ba\3\2\2\2cd\3\2\2\2db\3\2\2\2de\3\2\2"+
		"\2ef\3\2\2\2fg\b\3\2\2g\6\3\2\2\2hj\7\17\2\2ik\7\f\2\2ji\3\2\2\2jk\3\2"+
		"\2\2kn\3\2\2\2ln\7\f\2\2mh\3\2\2\2ml\3\2\2\2no\3\2\2\2op\b\4\2\2p\b\3"+
		"\2\2\2qr\7\61\2\2rs\7,\2\2sw\3\2\2\2tv\13\2\2\2ut\3\2\2\2vy\3\2\2\2wx"+
		"\3\2\2\2wu\3\2\2\2xz\3\2\2\2yw\3\2\2\2z{\7,\2\2{|\7\61\2\2|}\3\2\2\2}"+
		"~\b\5\2\2~\n\3\2\2\2\177\u0080\7\61\2\2\u0080\u0081\7\61\2\2\u0081\u0085"+
		"\3\2\2\2\u0082\u0084\n\3\2\2\u0083\u0082\3\2\2\2\u0084\u0087\3\2\2\2\u0085"+
		"\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0088\3\2\2\2\u0087\u0085\3\2"+
		"\2\2\u0088\u0089\5\7\4\2\u0089\u008a\3\2\2\2\u008a\u008b\b\6\2\2\u008b"+
		"\f\3\2\2\2\u008c\u0090\7%\2\2\u008d\u008f\n\3\2\2\u008e\u008d\3\2\2\2"+
		"\u008f\u0092\3\2\2\2\u0090\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091\u0093"+
		"\3\2\2\2\u0092\u0090\3\2\2\2\u0093\u0094\5\7\4\2\u0094\u0095\3\2\2\2\u0095"+
		"\u0096\b\7\2\2\u0096\16\3\2\2\2\u0097\u009b\5\t\5\2\u0098\u009b\5\13\6"+
		"\2\u0099\u009b\5\r\7\2\u009a\u0097\3\2\2\2\u009a\u0098\3\2\2\2\u009a\u0099"+
		"\3\2\2\2\u009b\20\3\2\2\2\u009c\u009d\t\4\2\2\u009d\22\3\2\2\2\u009e\u00a0"+
		"\5\21\t\2\u009f\u009e\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u009f\3\2\2\2"+
		"\u00a1\u00a2\3\2\2\2\u00a2\24\3\2\2\2\u00a3\u00a4\7\62\2\2\u00a4\u00a5"+
		"\7z\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a7\5\21\t\2\u00a7\u00a8\5\21\t\2"+
		"\u00a8\u00b1\3\2\2\2\u00a9\u00aa\7\62\2\2\u00aa\u00ab\7z\2\2\u00ab\u00ac"+
		"\3\2\2\2\u00ac\u00b1\5\21\t\2\u00ad\u00ae\5\21\t\2\u00ae\u00af\5\21\t"+
		"\2\u00af\u00b1\3\2\2\2\u00b0\u00a3\3\2\2\2\u00b0\u00a9\3\2\2\2\u00b0\u00ad"+
		"\3\2\2\2\u00b1\26\3\2\2\2\u00b2\u00b9\5\25\13\2\u00b3\u00b4\7\62\2\2\u00b4"+
		"\u00b5\7z\2\2\u00b5\u00b6\3\2\2\2\u00b6\u00b9\5\23\n\2\u00b7\u00b9\5\23"+
		"\n\2\u00b8\u00b2\3\2\2\2\u00b8\u00b3\3\2\2\2\u00b8\u00b7\3\2\2\2\u00b9"+
		"\30\3\2\2\2\u00ba\u00bb\7v\2\2\u00bb\u00bc\7t\2\2\u00bc\u00bd\7w\2\2\u00bd"+
		"\u00d6\7g\2\2\u00be\u00bf\7V\2\2\u00bf\u00c0\7t\2\2\u00c0\u00c1\7w\2\2"+
		"\u00c1\u00d6\7g\2\2\u00c2\u00c3\7V\2\2\u00c3\u00c4\7T\2\2\u00c4\u00c5"+
		"\7W\2\2\u00c5\u00d6\7G\2\2\u00c6\u00c7\7h\2\2\u00c7\u00c8\7c\2\2\u00c8"+
		"\u00c9\7n\2\2\u00c9\u00ca\7u\2\2\u00ca\u00d6\7g\2\2\u00cb\u00cc\7H\2\2"+
		"\u00cc\u00cd\7c\2\2\u00cd\u00ce\7n\2\2\u00ce\u00cf\7u\2\2\u00cf\u00d6"+
		"\7g\2\2\u00d0\u00d1\7H\2\2\u00d1\u00d2\7C\2\2\u00d2\u00d3\7N\2\2\u00d3"+
		"\u00d4\7U\2\2\u00d4\u00d6\7G\2\2\u00d5\u00ba\3\2\2\2\u00d5\u00be\3\2\2"+
		"\2\u00d5\u00c2\3\2\2\2\u00d5\u00c6\3\2\2\2\u00d5\u00cb\3\2\2\2\u00d5\u00d0"+
		"\3\2\2\2\u00d6\32\3\2\2\2\u00d7\u00d8\t\5\2\2\u00d8\34\3\2\2\2\u00d9\u00dd"+
		"\7$\2\2\u00da\u00dc\5\33\16\2\u00db\u00da\3\2\2\2\u00dc\u00df\3\2\2\2"+
		"\u00dd\u00db\3\2\2\2\u00dd\u00de\3\2\2\2\u00de\u00e0\3\2\2\2\u00df\u00dd"+
		"\3\2\2\2\u00e0\u00e1\7$\2\2\u00e1\36\3\2\2\2\u00e2\u00e3\7j\2\2\u00e3"+
		"\u00e4\7)\2\2\u00e4\u00e5\3\2\2\2\u00e5\u00e6\5\23\n\2\u00e6\u00e7\7)"+
		"\2\2\u00e7 \3\2\2\2\u00e8\u00e9\7t\2\2\u00e9\u00ea\7)\2\2\u00ea\u00ee"+
		"\3\2\2\2\u00eb\u00ed\5\33\16\2\u00ec\u00eb\3\2\2\2\u00ed\u00f0\3\2\2\2"+
		"\u00ee\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00f1\3\2\2\2\u00f0\u00ee"+
		"\3\2\2\2\u00f1\u00f2\7)\2\2\u00f2\"\3\2\2\2\u00f3\u00f4\7.\2\2\u00f4$"+
		"\3\2\2\2\u00f5\u00f6\7*\2\2\u00f6&\3\2\2\2\u00f7\u00f8\7+\2\2\u00f8(\3"+
		"\2\2\2\u00f9\u00fa\7<\2\2\u00fa*\3\2\2\2\u00fb\u00fd\t\6\2\2\u00fc\u00fb"+
		"\3\2\2\2\u00fd,\3\2\2\2\u00fe\u0101\5+\26\2\u00ff\u0101\t\7\2\2\u0100"+
		"\u00fe\3\2\2\2\u0100\u00ff\3\2\2\2\u0101.\3\2\2\2\u0102\u0103\7&\2\2\u0103"+
		"\60\3\2\2\2\u0104\u0108\5+\26\2\u0105\u0107\5-\27\2\u0106\u0105\3\2\2"+
		"\2\u0107\u010a\3\2\2\2\u0108\u0106\3\2\2\2\u0108\u0109\3\2\2\2\u0109\62"+
		"\3\2\2\2\u010a\u0108\3\2\2\2\u010b\u010c\7?\2\2\u010c\u010d\7?\2\2\u010d"+
		"\64\3\2\2\2\u010e\u010f\7#\2\2\u010f\u0110\7?\2\2\u0110\66\3\2\2\2\u0111"+
		"\u0112\7(\2\2\u0112\u0117\7(\2\2\u0113\u0114\7c\2\2\u0114\u0115\7p\2\2"+
		"\u0115\u0117\7f\2\2\u0116\u0111\3\2\2\2\u0116\u0113\3\2\2\2\u01178\3\2"+
		"\2\2\u0118\u0119\7~\2\2\u0119\u011d\7~\2\2\u011a\u011b\7q\2\2\u011b\u011d"+
		"\7t\2\2\u011c\u0118\3\2\2\2\u011c\u011a\3\2\2\2\u011d:\3\2\2\2\u011e\u0123"+
		"\7#\2\2\u011f\u0120\7p\2\2\u0120\u0121\7q\2\2\u0121\u0123\7v\2\2\u0122"+
		"\u011e\3\2\2\2\u0122\u011f\3\2\2\2\u0123<\3\2\2\2\u0124\u0125\7@\2\2\u0125"+
		"\u0126\7@\2\2\u0126>\3\2\2\2\u0127\u0128\7>\2\2\u0128\u0129\7>\2\2\u0129"+
		"@\3\2\2\2\u012a\u012b\7(\2\2\u012bB\3\2\2\2\u012c\u012d\7~\2\2\u012dD"+
		"\3\2\2\2\u012e\u012f\7`\2\2\u012fF\3\2\2\2\u0130\u0131\7\u0080\2\2\u0131"+
		"H\3\2\2\2\u0132\u0133\7A\2\2\u0133J\3\2\2\2\u0134\u0135\5)\25\2\u0135"+
		"L\3\2\2\2\u0136\u013a\7?\2\2\u0137\u0138\7<\2\2\u0138\u013a\7?\2\2\u0139"+
		"\u0136\3\2\2\2\u0139\u0137\3\2\2\2\u013aN\3\2\2\2\u013b\u013c\7-\2\2\u013c"+
		"\u013d\7?\2\2\u013dP\3\2\2\2\u013e\u013f\7*\2\2\u013f\u0140\7D\2\2\u0140"+
		"\u0141\7{\2\2\u0141\u0142\7v\2\2\u0142\u0143\7g\2\2\u0143\u0144\7+\2\2"+
		"\u0144R\3\2\2\2\u0145\u0146\7*\2\2\u0146\u0147\7U\2\2\u0147\u0148\7j\2"+
		"\2\u0148\u0149\7q\2\2\u0149\u014a\7t\2\2\u014a\u014b\7v\2\2\u014b\u014c"+
		"\7+\2\2\u014cT\3\2\2\2\u014d\u014e\7*\2\2\u014e\u014f\7K\2\2\u014f\u0150"+
		"\7p\2\2\u0150\u0151\7v\2\2\u0151\u0152\7\65\2\2\u0152\u0153\7\64\2\2\u0153"+
		"\u0154\7+\2\2\u0154V\3\2\2\2\u0155\u0156\7*\2\2\u0156\u0157\7D\2\2\u0157"+
		"\u0158\7q\2\2\u0158\u0159\7q\2\2\u0159\u015a\7n\2\2\u015a\u015b\7g\2\2"+
		"\u015b\u015c\7c\2\2\u015c\u015d\7p\2\2\u015d\u015e\7+\2\2\u015eX\3\2\2"+
		"\2\u015f\u0160\7*\2\2\u0160\u0161\7U\2\2\u0161\u0162\7v\2\2\u0162\u0163"+
		"\7t\2\2\u0163\u0164\7k\2\2\u0164\u0165\7p\2\2\u0165\u0166\7i\2\2\u0166"+
		"\u0167\7+\2\2\u0167Z\3\2\2\2\31\2]_djmw\u0085\u0090\u009a\u00a1\u00b0"+
		"\u00b8\u00d5\u00dd\u00ee\u00fc\u0100\u0108\u0116\u011c\u0122\u0139\3\2"+
		"\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}