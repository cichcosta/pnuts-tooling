package PT.Minimal.geral;


public class MSel
{
  private String wselid;
  
  private String watsel;
  
  private String wpredm;
  private String wpredf;
  private String wpreds;
  private String wgroup;
  private String worder;
  private String wcselm;
  private String wcsels;
  
  public MSel() {}
  
  public MSel(String wid, String wtab, char worig)
  {
    wselid = wid;
    watsel = ("Select a.* from " + wtab + "t" + worig + " a");
    wpredm = " ";
    wpreds = " ";
    wpredf = " ";
    wgroup = " ";
    worder = " ";
  }
  
  public String defCsel() {
    String wcsel = " ";
    wcsel = watsel + " " + wpredf + " " + wpredm + " " + wpreds;
    wcsel = wcsel + " " + wgroup + " " + worder;
    return wcsel;
  }
  
  public String defCmap() {
    return wcselm;
  }
  
  public String defCseg()
  {
    return wcsels;
  }
  
  public String defCrit(String watnome, String watval) {
    String wselat = " ";
    String wprdat = " ";
    String wvlrat = " ";
    
    watnome.trim();
    
    if (watnome.equals("SQL")) {
      wselat = watval;
    } else {
      wvlrat = watval;
    }
    return "";
  }
  
  public String defPred(String wval) {
    wval = trimR(wval);
    if ((wval.equals(" ")) || (wval.equals("%")))
      wval = "";
    if (wval.equals("!!!"))
      wval = " ";
    String wpred = "";
    if (wval.length() > 0) {
      if ((wval.indexOf("%") >= 0) || (wval.indexOf("_") >= 0)) {
        wpred = "like";
      } else
        wpred = "=";
    }
    return wpred;
  }
  
  public String defPred2(String wval1, String wval2) {
    String wpred1 = defPred(wval1);
    String wpred2 = defPred(wval2);
    if ((wpred1.length() == 0) && (wpred2.equals("=")))
      wpred2 = "<=";
    if ((wpred1.equals("=")) && (wpred2.length() > 0)) {
      if ((wpred2.equals("like")) && (wval1.equals("<>")))
        wpred2 = "not like";
      if (wpred2.equals("=")) {
        if (wval1.equals("<>"))
          wpred2 = "<>";
        if (wval1.equals("<="))
          wpred2 = "<=";
        if (wval1.equals(">="))
          wpred2 = ">=";
        if (wval1.equals(">"))
          wpred2 = ">";
        if (wval1.equals("<"))
          wpred2 = "<";
      }
      if ((!wpred2.equals("=")) && (!wpred2.equals("like")))
        wpred1 = "";
      if ((wpred1.equals("=")) && (wpred2.equals("="))) {
        wpred1 = "between";
        wpred2 = "";
      }
    }
    



    return "";
  }
  
  public String trimL(String wtexti) {
    int windi = 0;
    int windf = wtexti.length();
    if (windf > 0) {
      while (wtexti.charAt(windi) == ' ')
        windi++;
      return wtexti.substring(windi, windf);
    }
    return wtexti;
  }
  
  public String trimR(String wtexti) {
    int windf = wtexti.length();
    if (windf > 0) {
      windf--;
      while (wtexti.charAt(windf) == ' ')
        windf--;
      windf++;
      return wtexti.substring(0, windf);
    }
    return wtexti;
  }
}
