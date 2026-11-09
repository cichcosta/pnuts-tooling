package PT.Minimal.util;

import java.security.MessageDigest;
import java.util.Vector;
















public class MString
{
  public static final String EOL = System.getProperty("line.separator");
  

  public static final String CR = "\r";
  

  public static final String LF = "\n";
  
  public static final String CRLF = "\r\n";
  

  private MString() {}
  

  public static boolean isLiteral(char c)
  {
    return (c == '"') || (c == '\'');
  }
  







  public static boolean isWord(String s)
  {
    int n = s.length();
    if (n == 0) return false;
    char c = s.charAt(0);
    if (((c != '_' ? 1 : 0) & (!Character.isLetter(c) ? 1 : 0)) != 0) return false;
    for (int i = 1; i < n; i++) {
      c = s.charAt(i);
      if ((c != '_') && (!Character.isLetterOrDigit(c))) return false;
    }
    return true;
  }
  









  public static boolean isNumber(String s)
  {
    s = s.trim();
    int n = s.length();
    if (n == 0) return false;
    char c = s.charAt(0);
    for (int i = (c == '-') || (c == '+') ? 1 : 0; 
        i < n; i++) {
      c = s.charAt(i);
      if ((c != '.') && (!Character.isDigit(c))) return false;
    }
    return true;
  }
  







  public static boolean isInteger(String s)
  {
    s = s.trim();
    int n = s.length();
    if (n == 0) return false;
    char c = s.charAt(0);
    for (int i = (c == '-') || (c == '+') ? 1 : 0; 
        i < n; i++) {
      if (!Character.isDigit(s.charAt(i))) return false;
    }
    return true;
  }
  





  public static String removeAllSpaces(String s)
  {
    return removeAll(s, ' ');
  }
  






  public static String removeAll(String s, char c)
  {
    if (s == null) { return null;
    }
    int n = s.length();
    char[] cc = new char[n];
    int j = 0;
    for (int i = 0; i < n; i++) {
      if (s.charAt(i) != c) { cc[(j++)] = s.charAt(i);
      }
    }
    if (j == 0) return "";
    return new String(cc, 0, j);
  }
  






  public static String removeDupSpaces(String s)
  {
    return removeDup(s, ' ');
  }
  






  public static String removeDup(String s, char c)
  {
    if (s == null) { return null;
    }
    int n = s.length();
    char[] cc = new char[n];
    int j = 0;
    char cant = '\000';
    for (int i = 0; i < n; i++) {
      char ci = s.charAt(i);
      if ((ci != c) || (cant != c)) {
        cc[(j++)] = ci;
      }
      cant = ci;
    }
    
    if (j == 0) return "";
    return new String(cc, 0, j);
  }
  






  public static String trimRight(String s)
  {
    return trimRight(s, ' ');
  }
  






  public static String trimRight(String s, char c)
  {
    if (s == null) { return null;
    }
    int n = s.length() - 1;int i = n;
    while ((i >= 0) && (s.charAt(i) == c)) { i--;
    }
    if (i == n) return s;
    if (i < 0) return "";
    return s.substring(0, i + 1);
  }
  





  public static String trimLeft(String s)
  {
    return trimLeft(s, ' ');
  }
  






  public static String trimLeft(String s, char c)
  {
    if (s == null) { return null;
    }
    int i = 0;int n = s.length();
    while ((i < n) && (s.charAt(i) == c)) { i++;
    }
    if (i == 0) return s;
    if (i == n) return "";
    return s.substring(i);
  }
  






  public static String fillRight(String s, int n)
  {
    return fillRight(s, n, ' ');
  }
  







  public static String fillRight(String s, int n, char c)
  {
    if (s == null) { return null;
    }
    int l = s.length();
    if (l >= n) { return s;
    }
    char[] cc = new char[n];
    System.arraycopy(s.toCharArray(), 0, cc, 0, l);
    for (int i = l; i < n; i++) { cc[i] = c;
    }
    return new String(cc);
  }
  






  public static String fillLeft(String s, int n)
  {
    return fillLeft(s, n, ' ');
  }
  







  public static String fillLeft(String s, int n, char c)
  {
    if (s == null) { return null;
    }
    int l = s.length();
    if (l >= n) { return s;
    }
    char[] cc = new char[n];
    for (int i = 0; i < n - l; i++) cc[i] = c;
    System.arraycopy(s.toCharArray(), 0, cc, n - l, l);
    
    return new String(cc);
  }
  








  public static String splitLeft(String s, int n)
  {
    return splitLeft(s, n, ' ');
  }
  








  public static String splitLeft(String s, int n, char c)
  {
    if (s == null) { return null;
    }
    if (s.length() <= n) { return trimRight(s, c);
    }
    
    for (int i = n; (i >= 0) && (s.charAt(i) != c); i--) {}
    
    if (i >= 0) return trimRight(s.substring(0, i), c);
    return "";
  }
  







  public static String splitRight(String s, int n)
  {
    return splitRight(s, n, ' ');
  }
  








  public static String splitRight(String s, int n, char c)
  {
    if (s == null) { return null;
    }
    if (s.length() <= n) { return "";
    }
    
    for (int i = n; (i >= 0) && (s.charAt(i) != c); i--) {}
    
    if (i >= 0) return trimLeft(s.substring(i), c);
    return trimLeft(s, c);
  }
  






  public static String[] split(String s, int n)
  {
    return split(s, n, ' ');
  }
  







  public static String[] split(String s, int n, char c)
  {
    if (s == null) { return null;
    }
    Vector v = new Vector();
    while (s != null) {
      if (s.length() <= n) {
        v.addElement(trimRight(s, c));
        s = null;
      }
      else {
        for (int i = n; (i >= 0) && (s.charAt(i) != c); i--) {}
        for (int j = i; (j >= 0) && (s.charAt(j) == c); j--) {}
        
        if (j >= 0) {
          v.addElement(s.substring(0, j + 1));
        } else {
          v.addElement("");
        }
        if (i >= 0) {
          s = trimLeft(s.substring(i + 1), c);
        } else {
          s = null;
        }
      }
    }
    String[] list = new String[v.size()];
    v.copyInto(list);
    v.removeAllElements();
    return list;
  }
  







  public static String[] split(String s, char c)
  {
    if (s == null) { return null;
    }
    Vector v = new Vector();
    int len = s.length();
    
    int i = 0; for (int j = 0; i < len; i++) {
      if (s.charAt(i) == c) {
        if (j < i) {
          v.addElement(s.substring(j, i));
        } else {
          v.addElement("");
        }
        j = i + 1;
      }
    }
    if (j < i) {
      v.addElement(s.substring(j, i));
    } else {
      v.addElement("");
    }
    
    String[] list = new String[v.size()];
    v.copyInto(list);
    v.removeAllElements();
    return list;
  }
  








  public static String[] splitLines(String s, int n)
  {
    return splitLines(s, n, ' ', '\n');
  }
  









  public static String[] splitLines(String s, int n, char c, char eol)
  {
    if (s == null) { return null;
    }
    String[] lines = split(s, eol);
    Vector v = new Vector();
    for (int i = 0; i < lines.length; i++) {
      String[] chars = split(lines[i], n, c);
      for (int j = 0; j < chars.length; j++) {
        v.addElement(chars[j]);
      }
    }
    String[] list = new String[v.size()];
    v.copyInto(list);
    v.removeAllElements();
    return list;
  }
  






  public static String join(String[] s)
  {
    return join(s, ' ');
  }
  






  public static String join(String[] s, char c)
  {
    if ((s == null) || (s.length == 0)) { return null;
    }
    StringBuffer sb = new StringBuffer(s[0]);
    int i = 1; for (int n = s.length; i < n; i++) {
      sb.append(c).append(s[i]);
    }
    return sb.toString();
  }
  






  public static String join(String[] s, String t)
  {
    if ((s == null) || (s.length == 0)) { return null;
    }
    StringBuffer sb = new StringBuffer(s[0]);
    int i = 1; for (int n = s.length; i < n; i++) {
      sb.append(t).append(s[i]);
    }
    return sb.toString();
  }
  









  public static String max(String s1, String s2)
  {
    if ((s1 == null) || (s2 == null)) { return null;
    }
    return s1.compareTo(s2) > 0 ? s1 : s2;
  }
  







  public static String max(String[] list)
  {
    if ((list == null) || (list.length == 0)) { return null;
    }
    String s = null;
    for (int i = 0; i < list.length; i++) {
      String sx = list[i];
      if ((sx == null) || (sx.compareTo(s) > 0)) {
        s = sx;
      }
    }
    return s;
  }
  









  public static String min(String s1, String s2)
  {
    if ((s1 == null) || (s2 == null)) { return null;
    }
    return s1.compareTo(s2) < 0 ? s1 : s2;
  }
  







  public static String min(String[] list)
  {
    if ((list == null) || (list.length == 0)) { return null;
    }
    String s = null;
    for (int i = 0; i < list.length; i++) {
      String sx = list[i];
      if ((sx == null) || (sx.compareTo(s) < 0)) {
        s = sx;
      }
    }
    return s;
  }
  









  public static boolean between(String s, String s1, String s2)
  {
    if (s == null) { return false;
    }
    return (s.compareTo(s1) >= 0) && (s.compareTo(s2) <= 0);
  }
  








  public static boolean betweenIgnoreCase(String s, String s1, String s2)
  {
    if (s == null) { return false;
    }
    s = s.toUpperCase();
    return (s.compareTo(s1.toUpperCase()) >= 0) && (s.compareTo(s2.toUpperCase()) <= 0);
  }
  







  public static int in(String s, String[] list)
  {
    if ((s == null) || (list == null)) { return -1;
    }
    for (int i = 0; i < list.length; i++) {
      if (s.equals(list[i])) {
        return i;
      }
    }
    return -1;
  }
  







  public static int inIgnoreCase(String s, String[] list)
  {
    if ((s == null) || (list == null)) { return -1;
    }
    s = s.toUpperCase();
    for (int i = 0; i < list.length; i++)
    {
      if (s.equals(list[i].toUpperCase())) {
        return i;
      }
    }
    return -1;
  }
  








  public static int in(String s, Vector v)
  {
    if ((s == null) || (v == null)) { return -1;
    }
    synchronized (v) {
      int i = 0; for (int n = v.size(); i < n; i++) {
        if (s.equalsIgnoreCase(v.elementAt(i).toString())) {
          return i;
        }
      }
    }
    return -1;
  }
  








  public static int inIgnoreCase(String s, Vector v)
  {
    if ((s == null) || (v == null)) { return -1;
    }
    s = s.toUpperCase();
    synchronized (v) {
      int i = 0; for (int n = v.size(); i < n; i++)
      {
        if (s.equals(v.elementAt(i).toString().toUpperCase())) {
          return i;
        }
      }
    }
    return -1;
  }
  









  public static boolean intersects(String s1, String s2, String s1x, String s2x)
  {
    if ((s1 == null) || (s2 == null) || (s1x == null) || (s2x == null)) { return false;
    }
    return (s1.compareTo(s2x) <= 0) && (s2.compareTo(s1x) >= 0);
  }
  









  public static boolean contains(String s1, String s2, String s1x, String s2x)
  {
    if ((s1 == null) || (s2 == null) || (s1x == null) || (s2x == null)) { return false;
    }
    return (s1.compareTo(s1x) <= 0) && (s2.compareTo(s2x) >= 0);
  }
  







  public static String mdigest(String s)
  {
    try
    {
      MessageDigest md = (MessageDigest)MessageDigest.getInstance("MD5").clone();
      

      md.update(s.getBytes("ISO-8859-1"));
      


      return convert(md.digest());
    } catch (Exception ex) {}
    return s;
  }
  







  public static String convert(byte[] bytes)
  {
    StringBuffer sb = new StringBuffer(bytes.length * 2);
    for (int i = 0; i < bytes.length; i++) {
      sb.append(convertDigit(bytes[i] >> 4));
      sb.append(convertDigit(bytes[i] & 0xF));
    }
    return sb.toString();
  }
  






  private static char convertDigit(int value)
  {
    value &= 0xF;
    if (value >= 10) {
      return (char)(value - 10 + 97);
    }
    return (char)(value + 48);
  }
}
