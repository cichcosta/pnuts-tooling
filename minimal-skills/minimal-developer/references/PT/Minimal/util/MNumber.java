package PT.Minimal.util;

import java.math.BigDecimal;
import java.math.BigInteger;





















public class MNumber
  extends Number
{
  public static final BigDecimal ZERO = new BigDecimal(0);
  



  public static final BigDecimal UM = new BigDecimal(1);
  


  protected static int DEFAULT_ROUND = 4;
  
  protected BigDecimal value;
  
  protected int rmode = DEFAULT_ROUND;
  


  public MNumber(BigDecimal D) { value = D; }
  
  public MNumber(BigDecimal D, int round_mode) {
    value = D;
    rmode = round_mode;
  }
  
  public MNumber(BigInteger I) { value = new BigDecimal(I); }
  
  public MNumber(BigInteger I, int i) {
    value = new BigDecimal(I, i);
  }
  
  public MNumber(double d) { value = new BigDecimal(d); }
  
  public MNumber(String s) {
    value = new BigDecimal(s);
  }
  

  public MNumber(Number n) { this(n, DEFAULT_ROUND); }
  
  public MNumber(Number n, int round_mode) {
    if ((n instanceof MNumber)) {
      MNumber m = (MNumber)n;
      value = m.toBigDecimal();
      rmode = m.getRoundMode();
      return;
    }
    if ((n instanceof BigDecimal)) {
      value = ((BigDecimal)n);
      return;
    }
    if ((n instanceof BigInteger)) {
      value = new BigDecimal((BigInteger)n);
      return;
    }
    throw new IllegalArgumentException(n.getClass().toString());
  }
  


  protected MNumber copy(Number n)
  {
    return new MNumber(n, rmode);
  }
  


  protected MNumber make(Number n)
  {
    return new MNumber(n);
  }
  


  public int getRoundMode() { return rmode; }
  
  public int setRoundMode(int round_mode) {
    int i = rmode;
    rmode = round_mode;
    return i;
  }
  
  public MNumber round() { return copy(value.setScale(0, rmode)); }
  
  public MNumber round(int dec) {
    return copy(value.setScale(dec, rmode));
  }
  

  public String toString(String format)
  {
    return toString(format, 0);
  }
  
  public String toString(String format, int length) { return MNumberUtil.toString(doubleValue(), format, length); }
  


  public MNumber abs()
  {
    return copy(value.abs());
  }
  
  public int compareTo(MNumber m) {
    return value.compareTo(m.toBigDecimal());
  }
  
  public int compareTo(Number n) { return compareTo(make(n)); }
  
  public boolean equals(MNumber m)
  {
    return value.compareTo(m.toBigDecimal()) == 0;
  }
  
  public boolean equals(Number n) { return equals(make(n)); }
  
  public MNumber max(MNumber m)
  {
    if (compareTo(m) > 0) return m;
    return this;
  }
  
  public MNumber max(Number n) { return max(make(n)); }
  
  public MNumber min(MNumber m)
  {
    if (compareTo(m) < 0) return m;
    return this;
  }
  
  public MNumber min(Number n) { return min(make(n)); }
  
  public MNumber movePointLeft(int n)
  {
    return copy(value.movePointLeft(n));
  }
  
  public MNumber movePointRight(int n) { return copy(value.movePointRight(n)); }
  
  public MNumber negate() {
    return copy(value.negate());
  }
  
  public int scale() { return value.scale(); }
  
  public MNumber setScale(int n) {
    return copy(value.setScale(n));
  }
  
  public MNumber setScale(int n, int r) { return copy(value.setScale(n, r)); }
  
  public int signum() {
    return value.signum();
  }
  
  public MNumber inverse() {
    return copy(UM.divide(value, value.scale(), rmode));
  }
  




  public BigDecimal toBigDecimal()
  {
    return value;
  }
  
  public int intValue() {
    return value.intValue();
  }
  
  public long longValue() {
    return value.longValue();
  }
  
  public float floatValue() {
    return value.floatValue();
  }
  
  public double doubleValue() {
    return value.doubleValue();
  }
}
