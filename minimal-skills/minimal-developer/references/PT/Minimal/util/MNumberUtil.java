package PT.Minimal.util;

import PT.Minimal.geral.MError;
import PT.Minimal.geral.MHashtable;
import PT.Minimal.geral.MLogin;
import PT.Minimal.geral.MUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Vector;


























public class MNumberUtil
{
  public static int DEFAULT_ROUND = 4;
  


  public static final DecimalFormatSymbols DFS = new DecimalFormatSymbols(Locale.getDefault());
  static { DFS.setGroupingSeparator(' ');
    DFS.setDecimalSeparator('.');
  }
  




  public static BigDecimal toBigDecimal(int n)
  {
    return new BigDecimal(n);
  }
  
  public static BigDecimal toBigDecimal(long n) { return new BigDecimal(n); }
  
  public static BigDecimal toBigDecimal(float n) {
    return new BigDecimal(n);
  }
  
  public static BigDecimal toBigDecimal(double n) { return new BigDecimal(n); }
  
  public static BigDecimal toBigDecimal(Object n) {
    if ((n instanceof Number)) {
      if ((n instanceof BigDecimal)) return (BigDecimal)n;
      if ((n instanceof BigInteger)) return new BigDecimal((BigInteger)n);
      double d = ((Number)n).doubleValue();
      return new BigDecimal(((Number)n).doubleValue());
    }
    return new BigDecimal(n.toString());
  }
  

  public static int toInt(int n)
  {
    return n;
  }
  
  public static int toInt(long n) { return (int)n; }
  
  public static int toInt(float n) {
    return (int)n;
  }
  
  public static int toInt(double n) { return (int)n; }
  
  public static int toInt(Object n) {
    if ((n instanceof Number)) {
      return ((Number)n).intValue();
    }
    String s = n.toString();
    if (!MString.isNumber(s)) {
      throw new NumberFormatException(s);
    }
    int i = s.indexOf('.');
    if (i != -1) {
      return i == 0 ? 0 : Integer.valueOf(s.substring(0, i)).intValue();
    }
    return Integer.valueOf(s).intValue();
  }
  
  public static double pow(double num1, double num2) {
    return Math.pow(num1, num2);
  }
  
  public static double abs(double num) {
    return Math.abs(num);
  }
  
  public static BigDecimal abs(BigDecimal d) { return d.abs(); }
  




  public static double round(double num) { return Math.round(num); }
  
  public static double round(double num, int dec) {
    if (dec == 0) {
      return Math.round(num);
    }
    double x = Math.pow(10.0D, dec);
    return Math.round(num * x) / x;
  }
  
  public static BigDecimal round(BigDecimal d) { return d.setScale(0, DEFAULT_ROUND); }
  

  public static BigDecimal round(BigDecimal d, int dec) { return d.setScale(dec, DEFAULT_ROUND); }
  
  public static BigDecimal round(BigDecimal v, String carred, BigDecimal qarred) {
    int scale = qarred.intValue();
    int mode = -1;
    if ("N".equals(carred)) { mode = 4;
    } else if ("S".equals(carred)) { mode = 0;
    } else if ("I".equals(carred)) mode = 1; else {
      return null;
    }
    return v.movePointRight(scale).setScale(0, mode).movePointLeft(scale);
  }
  



  public static String toString(String value, String format) { return toString(value, format, 0); }
  
  public static String toString(String value, String format, int length) {
    if (value.trim().length() == 0) {
      if (length == 0) {
        return "";
      }
      return MString.fillRight("", length);
    }
    
    return toString(new BigDecimal(MString.removeAllSpaces(value)), format, length);
  }
  
  public static String toString(BigDecimal value, String format) { return toString(value.doubleValue(), format); }
  
  public static String toString(BigDecimal value, String format, int length) {
    return toString(value.doubleValue(), format, length);
  }
  
  public static String toString(double value, String format) { return toString(value, format, 0); }
  
  public static String toString(double value, String format, int length) {
    if (format.charAt(0) == 'N') {
      format = format.substring(1);
    }
    boolean bformat = true;
    if (format.charAt(0) == '*') {
      format = format.substring(1);
      if (value == 0.0D) {
        bformat = false;
      }
    }
    String str = "";
    if (bformat) {
      DecimalFormat wdf = new DecimalFormat(format, DFS);
      try {
        str = wdf.format(value);
      } catch (Exception ex) {
        str = "*********";
      }
    }
    if (length != 0) {
      str = MString.fillLeft(str, length);
    }
    return str;
  }
  




  public static final BigDecimal _ZERO = new BigDecimal(0);
  public static final BigDecimal _UM = new BigDecimal(1);
  
  public static String toText(double value) {
    return toText(new BigDecimal(value));
  }
  
  public static String toText(BigDecimal value) { return toText(value, 0, "", ""); }
  
  public static String toText(Object value) {
    return toText(value, 0, "", "");
  }
  
  public static String toText(Object value, String mcm) {
    return toText(value, -1, mcm);
  }
  
  public static String toText(BigDecimal value, String mcm) { return toText(value, -1, mcm); }
  

  public static String toText(Object value, int scale, String mcm) { return toText(toBigDecimal(value), scale, mcm); }
  
  public static String toText(BigDecimal value, int scale, String mcm) {
    String wcsel = "select tparms, qqcoef1 from bctxt0 where ictx = 'BCTX*MCM' and ivalk = '" + mcm + "'";
    



    Vector v = MUtil.getCachedValues(wcsel);
    if (v.isEmpty()) {
      throw new MError("MIN-MCM001E", new String[] { mcm });
    }
    
    MHashtable h = (MHashtable)v.firstElement();
    String tparms = h.getString("tparms");
    if (tparms.trim().length() == 0) {
      throw new MError("MIN-MCM002E", new String[] { mcm });
    }
    
    String[] args = MUtil.getArgs(tparms, ';');
    String units = args[0];
    String decimals = args.length > 1 ? args[1] : "";
    
    if (scale < 0) {
      scale = h.getInt("qqcoef1");
    }
    
    return toText(value, scale, units, decimals);
  }
  

  public static String toText(Object value, int scale, String units, String decimals) { return toText(toBigDecimal(value), scale, units, decimals); }
  
  public static String toText(BigDecimal value, int scale, String units, String decimals) {
    if (_ZERO.compareTo(value) == 0) { return "zero";
    }
    String result;
    if (value.signum() < 0) {
      String result = MUtil.lang("\"menos\"") + " ";
      value = value.negate();
    } else {
      result = "";
    }
    
    BigDecimal int_part = value.setScale(0, 1);
    if (_ZERO.compareTo(int_part) != 0) {
      result = result + getText(int_part);
      if ((units != null) && (units.length() != 0)) {
        result = result + " " + units;
      }
    }
    
    BigDecimal dec_part = value.subtract(int_part).movePointRight(scale).setScale(0, 4);
    if (_ZERO.compareTo(dec_part) != 0) {
      if (_ZERO.compareTo(int_part) != 0) {
        result = result + " " + MUtil.lang("\"e\"") + " " + getText(dec_part);
      } else {
        result = result + getText(dec_part);
      }
      if ((decimals != null) && (decimals.length() != 0)) {
        result = result + " " + decimals;
      }
    }
    return MUtil.stringRep(result, "  ", " ");
  }
  
  private static String getText(BigDecimal value) {
    String result = "";
    BigDecimal milhoes = value.movePointLeft(6).setScale(0, 1);
    
    if (_ZERO.compareTo(milhoes) != 0) { result = result + (_UM.compareTo(milhoes) == 0 ? MUtil.lang("\"um milhão\"") : new StringBuffer().append(getMilhares(milhoes, result.length() > 0)).append(" ").append(MUtil.lang("\"milhoes\"")).toString());
    }
    BigDecimal milhares = value.subtract(value.movePointLeft(6).setScale(0, 1).movePointRight(6));
    if (_ZERO.compareTo(milhares) != 0) result = result + getMilhares(milhares, result.length() != 0);
    return result;
  }
  






  private static String getMilhares(BigDecimal value, boolean prefix)
  {
    int milhares = value.movePointLeft(3).intValue();
    int unidades = value.subtract(value.movePointLeft(3).setScale(0, 1).movePointRight(3)).intValue();
    
    String result = "";
    switch (milhares) {
    case 0: 
      result = result + "";
      break;
    case 1: 
      if (((MLogin.hasLoginProperty("LANG")) && (MLogin.getLoginProperty("LANG").equals("US"))) || (MLogin.getLang() == "US")) {
        result = result + " " + MUtil.lang("\"um\"");
      }
      result = result + " " + MUtil.lang("\"mil\"");
      break;
    default: 
      result = result + " " + getMilhar(milhares, false) + MUtil.lang(" \"mil\"");
    }
    if (unidades != 0) result = result + getMilhar(unidades, (result.length() > 0) || (prefix));
    if (prefix) result = (milhares != 0 ? ", " : "") + result;
    return result;
  }
  






  private static String getMilhar(int number, boolean prefix)
  {
    if (number < 100) return getCentena(number, prefix);
    if (number == 100) { return (!prefix ? "" : new StringBuffer().append(" ").append(MUtil.lang("\"e\"")).append(" ").toString()) + MUtil.lang("\"cem\"");
    }
    int centenas = number / 100;
    number %= 100;
    
    String result = number == 0 ? " " + MUtil.lang("\"e\"") + " " : !prefix ? "" : " ";
    
    return result + (number == 0 ? MUtil.lang("\"" + _centenas[centenas] + "\"") : new StringBuffer().append(MUtil.lang(new StringBuffer().append("\"").append(_centenas[centenas]).append("\"").toString())).append(getCentena(number, true)).toString());
  }
  



  private static final String[] _centenas = { null, "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos", "seiscentos", "setecentos", "oitocentos", "novecentos" };
  










  private static String getCentena(int number, boolean prefix)
  {
    String result = prefix ? " " + MUtil.lang("\"e\"") + " " : "";
    
    if (number < 20) { return result + getVintena(number);
    }
    int dezenas = number / 10;
    number %= 10;
    return result + (number == 0 ? MUtil.lang("\"" + _dezenas[dezenas] + "\"") : new StringBuffer().append(MUtil.lang(new StringBuffer().append("\"").append(_dezenas[dezenas]).append("\"").toString())).append(" ").append(MUtil.lang("\"e\"")).append(" ").append(getVintena(number)).toString());
  }
  



  private static final String[] _dezenas = { null, null, "vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa" };
  









  private static String getVintena(int number)
  {
    return MUtil.lang("\"" + _vintena[number] + "\"");
  }
  
  private static final String[] _vintena = { null, "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove", "dez", "onze", "doze", "treze", "catorze", "quinze", "dezasseis", "dezassete", "dezoito", "dezanove" };
  




  public static String extenso(BigDecimal valor, int dec, String moeda)
  {
    return "xx";
  }
  
  private MNumberUtil() {}
}
