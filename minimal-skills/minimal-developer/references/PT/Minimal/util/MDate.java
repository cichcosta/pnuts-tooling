package PT.Minimal.util;

import PT.Minimal.geral.MError;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;











public class MDate
{
  public static final String DATE = "yyyy-MM-dd";
  public static final String DATE_Y = "yyyy";
  public static final String DATE_YM = "yyyy-MM";
  public static final String DATE_YD = "yyyy-DDD";
  public static final String TIME = "HH:mm:ss";
  public static final String TIME_H = "HH";
  public static final String TIME_HM = "HH:mm";
  public static final String TIME_MILI = "HH:mm:ss:SSS";
  
  private static TimeZone getDefaultTimeZone()
  {
    TimeZone.setDefault(new SimpleTimeZone(0, "GMT-1", 2, -1, 1, 3600000, 9, -1, 1, 7200000));
    




    return TimeZone.getDefault();
  }
  
  static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
  static final SimpleDateFormat DATE_FORMAT_Y = new SimpleDateFormat("yyyy", Locale.getDefault());
  static final SimpleDateFormat DATE_FORMAT_YM = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
  
  static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MMMM", Locale.getDefault());
  static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("EEEE", Locale.getDefault());
  
  static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
  static final SimpleDateFormat TIME_FORMAT_H = new SimpleDateFormat("HH", Locale.getDefault());
  static final SimpleDateFormat TIME_FORMAT_HM = new SimpleDateFormat("HH:mm", Locale.getDefault());
  static final SimpleDateFormat TIME_FORMAT_MILI = new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault());
  

  private final SimpleDateFormat format;
  

  private final GregorianCalendar calendar;
  

  private GregorianCalendar copyCalendar()
  {
    return new GregorianCalendar(calendar.get(1), calendar.get(2), calendar.get(5));
  }
  

  public static GregorianCalendar toGregorianCalendar(String date)
  {
    int len = date.length();
    if (len <= 4) {
      date = date + "-01-01";
    } else if (len <= 7) {
      date = date + "-01";
    }
    
    return toGregorianCalendar(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)), Integer.parseInt(date.substring(8, 10)));
  }
  
  public static GregorianCalendar toGregorianCalendar(int anos, int meses, int dias)
  {
    try {
      return new GregorianCalendar(anos, meses - 1, dias);
    } catch (Exception ex) {
      throw new MError(ex);
    }
  }
  
  public static SimpleDateFormat getDateFormat(String format) {
    if (format.equals("yyyy-MM-dd")) {
      return DATE_FORMAT;
    }
    if (format.equals("yyyy-MM")) {
      return DATE_FORMAT_YM;
    }
    if (format.equals("yyyy")) {
      return DATE_FORMAT_Y;
    }
    return new SimpleDateFormat(format, Locale.getDefault());
  }
  
  public static SimpleDateFormat getTimeFormat(String format) {
    if (format.equals("HH:mm:ss")) {
      return TIME_FORMAT;
    }
    if (format.equals("HH:mm")) {
      return TIME_FORMAT_HM;
    }
    if (format.equals("HH")) {
      return TIME_FORMAT_H;
    }
    return new SimpleDateFormat(format, Locale.getDefault());
  }
  


  public MDate copy() { return new MDate(calendar, format); }
  
  private MDate(GregorianCalendar cal, SimpleDateFormat dtf) {
    format = dtf;
    calendar = cal;
  }
  
  public MDate(String date, String fmt) { format = getDateFormat(fmt);
    calendar = toGregorianCalendar(date);
  }
  
  public MDate(String date) { int n = date.length();
    if (n == 4) { format = DATE_FORMAT_Y;
    } else if (n == 7) format = DATE_FORMAT_YM; else
      format = DATE_FORMAT;
    calendar = toGregorianCalendar(date);
  }
  
  public MDate() { format = DATE_FORMAT;
    calendar = new GregorianCalendar();
  }
  
  public static MDate valueOf(String date) {
    return new MDate(date);
  }
  
  public static MDate valueOf(String date, String format) { return new MDate(date, format); }
  
  public MDate setFormat(String fmt)
  {
    return new MDate(calendar, getDateFormat(fmt));
  }
  
  public Date toDate()
  {
    return calendar.getTime();
  }
  
  public String toString() {
    try {
      return format.format(calendar.getTime());
    } catch (Exception ex) {}
    return null;
  }
  
  public String toString(String fmt) {
    try {
      return getDateFormat(fmt).format(calendar.getTime());
    } catch (Exception ex) {}
    return null;
  }
  
  public static String getToday(String format) {
    try {
      return getDateFormat(format).format(new Date());
    } catch (Exception ex) {}
    return null;
  }
  
  public static String getToday() {
    try { return DATE_FORMAT.format(new Date());
    } catch (Exception ex) {}
    return null;
  }
  
  public static String getTime(String format)
  {
    try {
      return getTimeFormat(format).format(new Date());
    } catch (Exception ex) {}
    return null;
  }
  
  public static String getTime() {
    try { return TIME_FORMAT.format(new Date());
    } catch (Exception ex) {}
    return null;
  }
  
  public MDate inicioMes()
  {
    GregorianCalendar cal = copyCalendar();
    cal.set(5, 1);
    return new MDate(cal, format);
  }
  
  public MDate fimMes()
  {
    GregorianCalendar cal = copyCalendar();
    cal.set(5, 1);
    cal.roll(5, false);
    return new MDate(cal, format);
  }
  
  public MDate inicioAno()
  {
    GregorianCalendar cal = copyCalendar();
    cal.set(2, 0);
    cal.set(5, 1);
    return new MDate(cal, format);
  }
  
  public MDate fimAno()
  {
    GregorianCalendar cal = copyCalendar();
    cal.set(2, 11);
    cal.set(5, 31);
    return new MDate(cal, format);
  }
  
  public MDate diaSeguinte()
  {
    GregorianCalendar cal = copyCalendar();
    cal.add(5, 1);
    return new MDate(cal, format);
  }
  
  public MDate diaAnterior()
  {
    GregorianCalendar cal = copyCalendar();
    cal.add(5, -1);
    return new MDate(cal, format);
  }
  
  public MDate diaUtilSeguinte()
  {
    GregorianCalendar cal = copyCalendar();
    int i;
    do {
      cal.add(5, 1);
      i = cal.get(7);
    } while ((i == 1) || (i == 7));
    return new MDate(cal, format);
  }
  
  public MDate diaUtilAnterior()
  {
    GregorianCalendar cal = copyCalendar();
    













    int day = cal.get(7);
    if (day == 2) {
      cal.add(5, -3);
    } else if (day == 1) {
      cal.add(5, -2);
    } else {
      cal.add(5, -1);
    }
    return new MDate(cal, format);
  }
  
  public MDate mesSeguinte()
  {
    GregorianCalendar cal = copyCalendar();
    cal.add(2, 1);
    return new MDate(cal, format);
  }
  
  public MDate mesAnterior()
  {
    GregorianCalendar cal = copyCalendar();
    cal.add(2, -1);
    return new MDate(cal, format);
  }
  


  public MDate somar(int[] ymd) { return somar(ymd[0], ymd[1], ymd[2]); }
  
  public MDate somar(int anos, int meses, int dias) {
    GregorianCalendar cal = copyCalendar();
    cal.add(1, anos);
    cal.add(2, meses);
    cal.add(5, dias);
    return new MDate(cal, format);
  }
  
  public MDate somar(String date) { GregorianCalendar cal = copyCalendar();
    GregorianCalendar calx = toGregorianCalendar(date);
    
    cal.add(1, calx.get(1));
    cal.add(2, calx.get(2));
    cal.add(5, calx.get(5));
    return new MDate(cal, format);
  }
  
  public MDate subtrair(int anos, int meses, int dias)
  {
    Exception ex = new Exception("\nMétodo subtrair não pode ser utilizado!!!\nUtilizar o método diferenca!!!");
    ex.printStackTrace();
    GregorianCalendar cal = copyCalendar();
    cal.add(5, -dias);
    cal.add(2, -(meses + 1));
    cal.add(1, -anos);
    return new MDate(cal, format);
  }
  
  public MDate subtrair(String date) { GregorianCalendar calx = toGregorianCalendar(date);
    return subtrair(calx.get(1), calx.get(2), calx.get(5));
  }
  
  protected int[] diferenca(GregorianCalendar cald)
  {
    GregorianCalendar cal = copyCalendar();
    if (cal.equals(cald)) {
      return new int[] { 0, 0, 0 };
    }
    if (cal.before(cald)) {
      GregorianCalendar c = cal;
      cal = cald;
      cald = c;
    }
    

    int day = cal.get(5);
    int dayd = cald.get(5);
    int days; int days; if (day == dayd) {
      days = 0;
    } else {
      cal.add(5, -dayd);
      days = cal.get(5);
    }
    
    int month = cal.get(2);
    int monthd = cald.get(2);
    int months = month - monthd;
    
    int years = cal.get(1) - cald.get(1);
    if (months < 0) {
      months += 12;
      years--;
    } else if (months >= 12) {
      months -= 12;
      years++;
    }
    return new int[] { years, months, days };
  }
  
  public int[] diferenca(int anos, int meses, int dias) { return diferenca(toGregorianCalendar(anos, meses, dias)); }
  
  public int[] diferenca(String date) {
    return diferenca(toGregorianCalendar(date));
  }
  
  public int[] diferenca(MDate date) { return diferenca(date.getCalendar()); }
  
  public int diferencaAnos(int anos, int meses, int dias)
  {
    int[] result = diferenca(anos, meses, dias);
    return result[0];
  }
  
  public int diferencaAnos(String date) { int[] result = diferenca(date);
    return result[0];
  }
  
  public int diferencaAnos(MDate date) { int[] result = diferenca(date);
    return result[0];
  }
  
  public int diferencaMeses(int anos, int meses, int dias) {
    int[] result = diferenca(anos, meses, dias);
    return result[0] * 12 + result[1];
  }
  
  public int diferencaMeses(String date) { int[] result = diferenca(date);
    return result[0] * 12 + result[1];
  }
  
  public int diferencaMeses(MDate date) { int[] result = diferenca(date);
    return result[0] * 12 + result[1];
  }
  















  public int diferencaDias(MDate date)
  {
    GregorianCalendar cal = date.getCalendar();
    long dif = Math.abs(calendar.getTime().getTime() - cal.getTime().getTime());
    int x = new BigDecimal(dif).divide(new BigDecimal(86400000), 0, 4).intValue();
    return x;
  }
  
  public int diferencaDias(String date) { GregorianCalendar cal = toGregorianCalendar(date);
    long dif = Math.abs(calendar.getTime().getTime() - cal.getTime().getTime());
    int x = new BigDecimal(dif).divide(new BigDecimal(86400000), 0, 4).intValue();
    return x;
  }
  
  public MDate somarDias(int dias)
  {
    return somar(0, 0, dias);
  }
  
  public MDate setAno(int ano)
  {
    GregorianCalendar cal = copyCalendar();
    cal.set(1, ano);
    return new MDate(cal, format);
  }
  
  public MDate setMes(int mes) { GregorianCalendar cal = copyCalendar();
    cal.set(2, mes - 1);
    return new MDate(cal, format);
  }
  
  public MDate setDia(int dia) { GregorianCalendar cal = copyCalendar();
    cal.set(5, dia);
    return new MDate(cal, format);
  }
  
  public GregorianCalendar getCalendar() {
    return copyCalendar();
  }
  
  public int getAno() { return calendar.get(1); }
  
  public int getMes() {
    return calendar.get(2) + 1;
  }
  
  public int getDia() { return calendar.get(5); }
  
  public int diaSemana() {
    return calendar.get(7);
  }
  
  public String extensoDia()
  {
    return DAY_FORMAT.format(toDate());
  }
  
  public String extensoMes() {
    return MONTH_FORMAT.format(toDate());
  }
  
  public int compareTo(MDate date) {
    GregorianCalendar cal = date.getCalendar();
    if (calendar.after(cal)) return 1;
    if (calendar.before(cal)) return -1;
    return 0;
  }
  
  public boolean equals(MDate date) { return calendar.equals(date.getCalendar()); }
}
