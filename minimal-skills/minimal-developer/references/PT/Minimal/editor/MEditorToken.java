package PT.Minimal.editor;

import java.io.Serializable;











public class MEditorToken
  implements Serializable
{
  public static final int LastScanValue = 152;
  public static final int ForeachScanValue = 153;
  public static final int MaximumScanValue = 154;
  
  MEditorToken(String representation, int scanValue)
  {
    this.representation = representation;
    this.scanValue = scanValue;
  }
  



  public String toString()
  {
    return representation;
  }
  




  public int getScanValue()
  {
    return scanValue;
  }
  



  public String getCategory()
  {
    String nm = getClass().getName();
    int nmStart = nm.lastIndexOf('.') + 1;
    return nm.substring(nmStart, nm.length());
  }
  



  public final int hashCode()
  {
    return scanValue;
  }
  








  public final boolean equals(Object obj)
  {
    if ((obj instanceof MEditorToken)) {
      MEditorToken t = (MEditorToken)obj;
      return scanValue == scanValue;
    }
    return false;
  }
  









  public static final Object TokenAttribute = new AttributeKey(null);
  String representation;
  int scanValue;
  
  public static class Operator extends MEditorToken
  {
    Operator(String representation, int scanValue) {
      super(scanValue);
    }
  }
  
  public static class Value extends MEditorToken {
    Value(String representation, int scanValue) {
      super(scanValue);
    }
  }
  
  public static class Type extends MEditorToken {
    Type(String representation, int scanValue) {
      super(scanValue);
    }
  }
  
  public static class Expression extends MEditorToken {
    Expression(String representation, int scanValue) {
      super(scanValue);
    }
  }
  
  public static class Statement extends MEditorToken {
    Statement(String representation, int scanValue) {
      super(scanValue);
    }
  }
  
  public static class Declaration extends MEditorToken {
    Declaration(String representation, int scanValue) {
      super(scanValue);
    }
  }
  
  public static class Modifier extends MEditorToken {
    Modifier(String representation, int scanValue) {
      super(scanValue);
    }
  }
  
  public static class Punctuation extends MEditorToken {
    Punctuation(String representation, int scanValue) {
      super(scanValue);
    }
  }
  
  public static class Special
    extends MEditorToken {
    Special(String representation, int scanValue) { super(scanValue); }
  }
  
  static class AttributeKey {
    AttributeKey(MEditorToken.1 x0) { this(); }
    

    public String toString()
    {
      return "token";
    }
    

    private AttributeKey() {}
  }
  
  public static final MEditorToken COMMA = new Operator(sun.tools.java.Constants.opNames[0], 0);
  
  public static final MEditorToken ASSIGN = new Operator(sun.tools.java.Constants.opNames[1], 1);
  
  public static final MEditorToken ASGMUL = new Operator(sun.tools.java.Constants.opNames[2], 2);
  
  public static final MEditorToken ASGDIV = new Operator(sun.tools.java.Constants.opNames[3], 3);
  
  public static final MEditorToken ASGREM = new Operator(sun.tools.java.Constants.opNames[4], 4);
  
  public static final MEditorToken ASGADD = new Operator(sun.tools.java.Constants.opNames[5], 5);
  
  public static final MEditorToken ASGSUB = new Operator(sun.tools.java.Constants.opNames[6], 6);
  
  public static final MEditorToken ASGLSHIFT = new Operator(sun.tools.java.Constants.opNames[7], 7);
  
  public static final MEditorToken ASGRSHIFT = new Operator(sun.tools.java.Constants.opNames[8], 8);
  
  public static final MEditorToken ASGURSHIFT = new Operator(sun.tools.java.Constants.opNames[9], 9);
  
  public static final MEditorToken ASGBITAND = new Operator(sun.tools.java.Constants.opNames[10], 10);
  
  public static final MEditorToken ASGBITOR = new Operator(sun.tools.java.Constants.opNames[11], 11);
  
  public static final MEditorToken ASGBITXOR = new Operator(sun.tools.java.Constants.opNames[11], 11);
  
  public static final MEditorToken COND = new Operator(sun.tools.java.Constants.opNames[13], 13);
  
  public static final MEditorToken OR = new Operator(sun.tools.java.Constants.opNames[14], 14);
  
  public static final MEditorToken AND = new Operator(sun.tools.java.Constants.opNames[15], 15);
  
  public static final MEditorToken BITOR = new Operator(sun.tools.java.Constants.opNames[16], 16);
  
  public static final MEditorToken BITXOR = new Operator(sun.tools.java.Constants.opNames[17], 17);
  
  public static final MEditorToken BITAND = new Operator(sun.tools.java.Constants.opNames[18], 18);
  
  public static final MEditorToken NE = new Operator(sun.tools.java.Constants.opNames[19], 19);
  
  public static final MEditorToken EQ = new Operator(sun.tools.java.Constants.opNames[20], 20);
  
  public static final MEditorToken GE = new Operator(sun.tools.java.Constants.opNames[21], 21);
  
  public static final MEditorToken GT = new Operator(sun.tools.java.Constants.opNames[22], 22);
  
  public static final MEditorToken LE = new Operator(sun.tools.java.Constants.opNames[23], 23);
  
  public static final MEditorToken LT = new Operator(sun.tools.java.Constants.opNames[24], 24);
  
  public static final MEditorToken INSTANCEOF = new Operator(sun.tools.java.Constants.opNames[25], 25);
  
  public static final MEditorToken LSHIFT = new Operator(sun.tools.java.Constants.opNames[26], 26);
  
  public static final MEditorToken RSHIFT = new Operator(sun.tools.java.Constants.opNames[27], 27);
  
  public static final MEditorToken URSHIFT = new Operator(sun.tools.java.Constants.opNames[28], 28);
  
  public static final MEditorToken ADD = new Operator(sun.tools.java.Constants.opNames[29], 29);
  
  public static final MEditorToken SUB = new Operator(sun.tools.java.Constants.opNames[30], 30);
  
  public static final MEditorToken DIV = new Operator(sun.tools.java.Constants.opNames[31], 31);
  
  public static final MEditorToken REM = new Operator(sun.tools.java.Constants.opNames[32], 32);
  
  public static final MEditorToken MUL = new Operator(sun.tools.java.Constants.opNames[33], 33);
  
  public static final MEditorToken CAST = new Operator(sun.tools.java.Constants.opNames[34], 34);
  
  public static final MEditorToken POS = new Operator(sun.tools.java.Constants.opNames[35], 35);
  
  public static final MEditorToken NEG = new Operator(sun.tools.java.Constants.opNames[36], 36);
  
  public static final MEditorToken NOT = new Operator(sun.tools.java.Constants.opNames[37], 37);
  
  public static final MEditorToken BITNOT = new Operator(sun.tools.java.Constants.opNames[38], 38);
  
  public static final MEditorToken PREINC = new Operator(sun.tools.java.Constants.opNames[39], 39);
  
  public static final MEditorToken PREDEC = new Operator(sun.tools.java.Constants.opNames[40], 40);
  
  public static final MEditorToken NEWARRAY = new Operator(sun.tools.java.Constants.opNames[41], 41);
  
  public static final MEditorToken NEWINSTANCE = new Operator(sun.tools.java.Constants.opNames[42], 42);
  
  public static final MEditorToken NEWFROMNAME = new Operator(sun.tools.java.Constants.opNames[43], 43);
  
  public static final MEditorToken POSTINC = new Operator(sun.tools.java.Constants.opNames[44], 44);
  
  public static final MEditorToken POSTDEC = new Operator(sun.tools.java.Constants.opNames[45], 45);
  
  public static final MEditorToken FIELD = new Operator(sun.tools.java.Constants.opNames[46], 46);
  
  public static final MEditorToken METHOD = new Operator(sun.tools.java.Constants.opNames[47], 47);
  
  public static final MEditorToken ARRAYACCESS = new Operator(sun.tools.java.Constants.opNames[48], 48);
  
  public static final MEditorToken NEW = new Operator(sun.tools.java.Constants.opNames[49], 49);
  
  public static final MEditorToken INC = new Operator(sun.tools.java.Constants.opNames[50], 50);
  
  public static final MEditorToken DEC = new Operator(sun.tools.java.Constants.opNames[51], 51);
  
  public static final MEditorToken CONVERT = new Operator(sun.tools.java.Constants.opNames[55], 55);
  
  public static final MEditorToken EXPR = new Operator(sun.tools.java.Constants.opNames[56], 56);
  
  public static final MEditorToken ARRAY = new Operator(sun.tools.java.Constants.opNames[57], 57);
  
  public static final MEditorToken GOTO = new Operator(sun.tools.java.Constants.opNames[58], 58);
  



  public static final MEditorToken IDENT = new Value(sun.tools.java.Constants.opNames[60], 60);
  
  public static final MEditorToken BOOLEANVAL = new Value(sun.tools.java.Constants.opNames[61], 61);
  
  public static final MEditorToken BYTEVAL = new Value(sun.tools.java.Constants.opNames[62], 62);
  
  public static final MEditorToken CHARVAL = new Value(sun.tools.java.Constants.opNames[63], 63);
  
  public static final MEditorToken SHORTVAL = new Value(sun.tools.java.Constants.opNames[64], 64);
  
  public static final MEditorToken INTVAL = new Value(sun.tools.java.Constants.opNames[65], 65);
  
  public static final MEditorToken LONGVAL = new Value(sun.tools.java.Constants.opNames[66], 66);
  
  public static final MEditorToken FLOATVAL = new Value(sun.tools.java.Constants.opNames[67], 67);
  
  public static final MEditorToken DOUBLEVAL = new Value(sun.tools.java.Constants.opNames[68], 68);
  
  public static final MEditorToken STRINGVAL = new Value(sun.tools.java.Constants.opNames[69], 69);
  



  public static final MEditorToken BYTE = new Type(sun.tools.java.Constants.opNames[70], 70);
  
  public static final MEditorToken CHAR = new Type(sun.tools.java.Constants.opNames[71], 71);
  
  public static final MEditorToken SHORT = new Type(sun.tools.java.Constants.opNames[72], 72);
  
  public static final MEditorToken INT = new Type(sun.tools.java.Constants.opNames[73], 73);
  
  public static final MEditorToken LONG = new Type(sun.tools.java.Constants.opNames[74], 74);
  
  public static final MEditorToken FLOAT = new Type(sun.tools.java.Constants.opNames[75], 75);
  
  public static final MEditorToken DOUBLE = new Type(sun.tools.java.Constants.opNames[76], 76);
  
  public static final MEditorToken VOID = new Type(sun.tools.java.Constants.opNames[77], 77);
  
  public static final MEditorToken BOOLEAN = new Type(sun.tools.java.Constants.opNames[78], 78);
  



  public static final MEditorToken TRUE = new Expression(sun.tools.java.Constants.opNames[80], 80);
  
  public static final MEditorToken FALSE = new Expression(sun.tools.java.Constants.opNames[81], 81);
  
  public static final MEditorToken THIS = new Expression(sun.tools.java.Constants.opNames[82], 82);
  
  public static final MEditorToken SUPER = new Expression(sun.tools.java.Constants.opNames[83], 83);
  
  public static final MEditorToken NULL = new Expression(sun.tools.java.Constants.opNames[84], 84);
  



  public static final MEditorToken FOREACH = new Statement("foreach", 153);
  public static final MEditorToken IF = new Statement(sun.tools.java.Constants.opNames[90], 90);
  
  public static final MEditorToken ELSE = new Statement(sun.tools.java.Constants.opNames[91], 91);
  
  public static final MEditorToken FOR = new Statement(sun.tools.java.Constants.opNames[92], 92);
  
  public static final MEditorToken WHILE = new Statement(sun.tools.java.Constants.opNames[93], 93);
  
  public static final MEditorToken DO = new Statement(sun.tools.java.Constants.opNames[94], 94);
  
  public static final MEditorToken SWITCH = new Statement(sun.tools.java.Constants.opNames[95], 95);
  
  public static final MEditorToken CASE = new Statement(sun.tools.java.Constants.opNames[96], 96);
  
  public static final MEditorToken DEFAULT = new Statement(sun.tools.java.Constants.opNames[97], 97);
  
  public static final MEditorToken BREAK = new Statement(sun.tools.java.Constants.opNames[98], 98);
  
  public static final MEditorToken CONTINUE = new Statement(sun.tools.java.Constants.opNames[99], 99);
  
  public static final MEditorToken RETURN = new Statement(sun.tools.java.Constants.opNames[100], 100);
  
  public static final MEditorToken TRY = new Statement(sun.tools.java.Constants.opNames[101], 101);
  
  public static final MEditorToken CATCH = new Statement(sun.tools.java.Constants.opNames[102], 102);
  
  public static final MEditorToken FINALLY = new Statement(sun.tools.java.Constants.opNames[103], 103);
  
  public static final MEditorToken THROW = new Statement(sun.tools.java.Constants.opNames[104], 104);
  
  public static final MEditorToken STAT = new Statement(sun.tools.java.Constants.opNames[105], 105);
  
  public static final MEditorToken EXPRESSION = new Statement(sun.tools.java.Constants.opNames[106], 106);
  
  public static final MEditorToken DECLARATION = new Statement(sun.tools.java.Constants.opNames[107], 107);
  
  public static final MEditorToken VARDECLARATION = new Statement(sun.tools.java.Constants.opNames[108], 108);
  



  public static final MEditorToken IMPORT = new Declaration(sun.tools.java.Constants.opNames[110], 110);
  
  public static final MEditorToken CLASS = new Declaration(sun.tools.java.Constants.opNames[111], 111);
  
  public static final MEditorToken EXTENDS = new Declaration(sun.tools.java.Constants.opNames[112], 112);
  
  public static final MEditorToken IMPLEMENTS = new Declaration(sun.tools.java.Constants.opNames[113], 113);
  
  public static final MEditorToken INTERFACE = new Declaration(sun.tools.java.Constants.opNames[114], 114);
  
  public static final MEditorToken PACKAGE = new Declaration(sun.tools.java.Constants.opNames[115], 115);
  



  public static final MEditorToken PRIVATE = new Modifier(sun.tools.java.Constants.opNames[120], 120);
  
  public static final MEditorToken PUBLIC = new Modifier(sun.tools.java.Constants.opNames[121], 121);
  
  public static final MEditorToken PROTECTED = new Modifier(sun.tools.java.Constants.opNames[122], 122);
  
  public static final MEditorToken CONST = new Modifier(sun.tools.java.Constants.opNames[123], 123);
  
  public static final MEditorToken STATIC = new Modifier(sun.tools.java.Constants.opNames[124], 124);
  
  public static final MEditorToken TRANSIENT = new Modifier(sun.tools.java.Constants.opNames[125], 125);
  
  public static final MEditorToken SYNCHRONIZED = new Modifier(sun.tools.java.Constants.opNames[126], 126);
  
  public static final MEditorToken NATIVE = new Modifier(sun.tools.java.Constants.opNames[127], 127);
  
  public static final MEditorToken FINAL = new Modifier(sun.tools.java.Constants.opNames[''], 128);
  
  public static final MEditorToken VOLATILE = new Modifier(sun.tools.java.Constants.opNames[''], 129);
  
  public static final MEditorToken ABSTRACT = new Modifier(sun.tools.java.Constants.opNames[''], 130);
  



  public static final MEditorToken SEMICOLON = new Punctuation(sun.tools.java.Constants.opNames[''], 135);
  
  public static final MEditorToken COLON = new Punctuation(sun.tools.java.Constants.opNames[''], 136);
  
  public static final MEditorToken QUESTIONMARK = new Punctuation(sun.tools.java.Constants.opNames[''], 137);
  
  public static final MEditorToken LBRACE = new Punctuation(sun.tools.java.Constants.opNames[''], 138);
  
  public static final MEditorToken RBRACE = new Punctuation(sun.tools.java.Constants.opNames[''], 139);
  
  public static final MEditorToken LPAREN = new Punctuation(sun.tools.java.Constants.opNames[''], 140);
  
  public static final MEditorToken RPAREN = new Punctuation(sun.tools.java.Constants.opNames[''], 141);
  
  public static final MEditorToken LSQBRACKET = new Punctuation(sun.tools.java.Constants.opNames[''], 142);
  
  public static final MEditorToken RSQBRACKET = new Punctuation(sun.tools.java.Constants.opNames[''], 143);
  
  public static final MEditorToken THROWS = new Punctuation(sun.tools.java.Constants.opNames[''], 144);
  




  public static final MEditorToken ERROR = new Special(sun.tools.java.Constants.opNames[''], 145);
  
  public static final MEditorToken COMMENT = new Special(sun.tools.java.Constants.opNames[''], 146);
  
  public static final MEditorToken TYPE = new Special(sun.tools.java.Constants.opNames[''], 147);
  
  public static final MEditorToken LENGTH = new Special(sun.tools.java.Constants.opNames[''], 148);
  
  public static final MEditorToken INLINERETURN = new Special(sun.tools.java.Constants.opNames[''], 149);
  
  public static final MEditorToken INLINEMETHOD = new Special(sun.tools.java.Constants.opNames[''], 150);
  
  public static final MEditorToken INLINENEWINSTANCE = new Special(sun.tools.java.Constants.opNames[''], 151);
  
  public static final MEditorToken UNSCANNED = new Special("unscanned", 154);
  
  static MEditorToken[] operators = { COMMA, ASSIGN, ASGMUL, ASGDIV, ASGREM, ASGADD, ASGSUB, ASGLSHIFT, ASGRSHIFT, ASGURSHIFT, ASGBITAND, ASGBITOR, ASGBITXOR, COND, OR, AND, BITOR, BITXOR, BITAND, NE, EQ, GE, GT, LE, LT, INSTANCEOF, LSHIFT, RSHIFT, URSHIFT, ADD, SUB, DIV, REM, MUL, CAST, POS, NEG, NOT, BITNOT, PREINC, PREDEC, NEWARRAY, NEWINSTANCE, NEWFROMNAME, POSTINC, POSTDEC, FIELD, METHOD, ARRAYACCESS, NEW, INC, DEC, CONVERT, EXPR, ARRAY, GOTO };
  







  static MEditorToken[] values = { IDENT, BOOLEANVAL, BYTEVAL, CHARVAL, SHORTVAL, INTVAL, LONGVAL, FLOATVAL, DOUBLEVAL, STRINGVAL };
  



  static MEditorToken[] types = { BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, VOID, BOOLEAN };
  


  static MEditorToken[] expressions = { TRUE, FALSE, THIS, SUPER, NULL };
  


  static MEditorToken[] statements = { FOREACH, IF, ELSE, FOR, WHILE, DO, SWITCH, CASE, DEFAULT, BREAK, CONTINUE, RETURN, TRY, CATCH, FINALLY, THROW, STAT, EXPRESSION, DECLARATION, VARDECLARATION };
  




  static MEditorToken[] declarations = { IMPORT, CLASS, EXTENDS, IMPLEMENTS, INTERFACE, PACKAGE };
  


  static MEditorToken[] modifiers = { PRIVATE, PUBLIC, PROTECTED, CONST, STATIC, TRANSIENT, SYNCHRONIZED, NATIVE, FINAL, VOLATILE, ABSTRACT };
  



  static MEditorToken[] punctuations = { SEMICOLON, COLON, QUESTIONMARK, LBRACE, RBRACE, LPAREN, RPAREN, LSQBRACKET, RSQBRACKET, THROWS };
  



  static MEditorToken[] specials = { ERROR, COMMENT, TYPE, LENGTH, INLINERETURN, INLINEMETHOD, INLINENEWINSTANCE, UNSCANNED };
  


  static MEditorToken[] all = { COMMA, ASSIGN, ASGMUL, ASGDIV, ASGREM, ASGADD, ASGSUB, ASGLSHIFT, ASGRSHIFT, ASGURSHIFT, ASGBITAND, ASGBITOR, ASGBITXOR, COND, OR, AND, BITOR, BITXOR, BITAND, NE, EQ, GE, GT, LE, LT, INSTANCEOF, LSHIFT, RSHIFT, URSHIFT, ADD, SUB, DIV, REM, MUL, CAST, POS, NEG, NOT, BITNOT, PREINC, PREDEC, NEWARRAY, NEWINSTANCE, NEWFROMNAME, POSTINC, POSTDEC, FIELD, METHOD, ARRAYACCESS, NEW, INC, DEC, CONVERT, EXPR, ARRAY, GOTO, IDENT, BOOLEANVAL, BYTEVAL, CHARVAL, SHORTVAL, INTVAL, LONGVAL, FLOATVAL, DOUBLEVAL, STRINGVAL, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, VOID, BOOLEAN, TRUE, FALSE, THIS, SUPER, NULL, FOREACH, IF, ELSE, FOR, WHILE, DO, SWITCH, CASE, DEFAULT, BREAK, CONTINUE, RETURN, TRY, CATCH, FINALLY, THROW, STAT, EXPRESSION, DECLARATION, VARDECLARATION, IMPORT, CLASS, EXTENDS, IMPLEMENTS, INTERFACE, PACKAGE, PRIVATE, PUBLIC, PROTECTED, CONST, STATIC, TRANSIENT, SYNCHRONIZED, NATIVE, FINAL, VOLATILE, ABSTRACT, SEMICOLON, COLON, QUESTIONMARK, LBRACE, RBRACE, LPAREN, RPAREN, LSQBRACKET, RSQBRACKET, THROWS, ERROR, COMMENT, TYPE, LENGTH, INLINERETURN, INLINEMETHOD, INLINENEWINSTANCE, UNSCANNED };
}
