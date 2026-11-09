package PT.Minimal.util;

import java.math.BigDecimal;


























public class MValue
  extends Number
{
  protected static BigDecimal ZERO = new BigDecimal(0);
  protected static BigDecimal UM = new BigDecimal(1);
  protected static int DEFAULT_ROUND = 0;
  protected BigDecimal value;
  protected int rmode = 4;
  



  private static final String DEFAULT_UNIT = "EUR";
  


  private static final BigDecimal DEFAULT_RATE = UM;
  
  private String unit;
  
  private BigDecimal rate;
  
  public MValue(String s)
  {
    this(new BigDecimal(s));
  }
  
  public MValue(double d) { this(new BigDecimal(d)); }
  
  public MValue(Number n) {
    this(n, "EUR", DEFAULT_RATE);
  }
  







  public MValue(Number n, String u, BigDecimal r)
  {
    unit = u;
    rate = r;
    if ((n instanceof MValue)) {
      value = convertValue((MValue)n);
    } else if ((n instanceof BigDecimal)) {
      value = ((BigDecimal)n);
    } else {
      value = new BigDecimal(n.doubleValue());
    }
  }
  






  private BigDecimal convertValue(MValue v)
  {
    if (rate.equals(v.getRate())) {
      return v.toBigDecimal();
    }
    return v.convertValue().divide(rate, DEFAULT_ROUND);
  }
  




  private MValue copy(Number n)
  {
    return new MValue(n, unit, rate);
  }
  





  public BigDecimal convertValue()
  {
    return value.multiply(rate);
  }
  



  public MValue add(MValue v)
  {
    return copy(value.add(convertValue(v)));
  }
  
  public MValue add(Number n) { return copy(value.add(convertValue(copy(n)))); }
  



  public MValue subtract(MValue v)
  {
    return copy(value.subtract(convertValue(v)));
  }
  
  public MValue subtract(Number n) { return copy(value.subtract(convertValue(copy(n)))); }
  





  public MValue multiply(MValue v) { throw new RuntimeException("MIN-MV0001E"); }
  
  public MValue multiply(Number n) {
    BigDecimal v = (n instanceof BigDecimal) ? (BigDecimal)n : new BigDecimal(n.doubleValue());
    return copy(value.multiply(v));
  }
  




  public MValue divide(MValue v) { throw new RuntimeException("MIN-MV0002E"); }
  
  public MValue divide(Number n) {
    BigDecimal v = (n instanceof BigDecimal) ? (BigDecimal)n : new BigDecimal(n.doubleValue());
    return copy(value.divide(v, DEFAULT_ROUND));
  }
  

  public MValue abs() { return copy(value.abs()); }
  
  public int compareTo(MValue v) {
    if (rate.equals(v.getRate())) {
      return value.compareTo(v.toBigDecimal());
    }
    return convertValue().compareTo(v.convertValue());
  }
  
  public int compareTo(Number n) { return compareTo(copy(n)); }
  
  public boolean equals(MValue v) {
    if (rate.equals(v.getRate())) {
      return value.equals(v.toBigDecimal());
    }
    return convertValue().equals(v.convertValue());
  }
  
  public boolean equals(Number n) { return equals(copy(n)); }
  
  public MValue max(MValue v)
  {
    if (rate.equals(v.getRate())) {
      return copy(value.max(v.toBigDecimal()));
    }
    return copy(convertValue().max(v.convertValue()));
  }
  
  public MValue max(Number n) { return max(copy(n)); }
  
  public MValue min(Number n)
  {
    MValue v = (n instanceof MValue) ? (MValue)n : copy(n);
    if (rate.equals(v.getRate())) {
      return copy(value.min(v.toBigDecimal()));
    }
    return copy(convertValue().min(v.convertValue()));
  }
  
  public MValue min(MValue v) { return min(copy(v)); }
  


  public String getUnit()
  {
    return unit;
  }
  
  public BigDecimal getRate() { return rate; }
  
  public String toString()
  {
    return "{" + value.toString() + unit + " (" + rate.toString() + ")}";
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
