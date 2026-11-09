package PT.Minimal.entities;

import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MRow;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

public class STOC0 implements PT.Minimal.actions.MExternalCommand
{
  PT.Minimal.geral.MCommand minCommand;
  PT.Minimal.geral.MForm minForm;
  Vector wrows;
  String EOL = "\n";
  private int wnivel = 0;
  
  public STOC0() {}
  
  public void setMCommand(PT.Minimal.geral.MCommand wcmd)
  {
    minCommand = wcmd;
  }
  
  public void setMForm(PT.Minimal.geral.MForm wform) {
    minForm = wform;
  }
  
  public void setRows(Vector wv) {
    wrows = wv;
  }
  
  public void execute() throws MError {
    if (minCommand.getName().equals("GERTOCFILE"))
    {


      ger_TOC_file();
    }
    if (minCommand.getName().equals("GERHTMLFILE"))
    {


      ger_HTML_file();
    }
    if (minCommand.getName().equals("GERMAPFILE"))
    {


      ger_MAP_file();
    }
    if (minCommand.getName().equals("GERIXFILE"))
    {


      ger_IX_file();
    }
    if (minCommand.getName().equals("BRANDFILES"))
    {


      brand_files();
    }
    if (minCommand.getName().equals("GERTOCMENU"))
    {


      ger_TOC_from_MENU();
    }
    if (minCommand.getName().equals("GERGEFILES"))
    {


      ger_GEFiles();
    }
    if (minCommand.getName().equals("GERATRIBFILE")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      System.out.println("Entrou");
      ger_ATRIB_file();
    }
    if (minCommand.getName().equals("GERCMDFILE")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      System.out.println("Entrou");
      ger_CMD_file();
    }
    if (minCommand.getName().equals("GERALL"))
    {


      ger_TOC_from_MENU();
      ger_TOC_file();
      ger_HTML_file();
      ger_MAP_file();
      ger_IX_file();
      ger_GEFiles();
    }
    MError werr = new MError("MIN-000000M");
    werr.show(minForm.getMFrame());
  }
  
  private void ger_TOC_from_MENU() throws MError
  {
    MCursor wcursor = PT.Minimal.geral.MConnection.getMCursor("JMETADATA");
    

    String wcsel = " delete from stoct0 where iobjt = 'MN' and cst = 'EC'";
    wcursor.execStatement(wcsel);
    wcursor.closeStatement();
    
    wcsel = "insert into stoct0 ";
    wcsel = wcsel + "( zk, zutl, zgrp, zhst, zdst, zcst ";
    wcsel = wcsel + ", itoc, xopcao, iobj, iobjt, nopcao, tgif ";
    wcsel = wcsel + ", cst, dst, cstm) ";
    wcsel = wcsel + "select ";
    wcsel = wcsel + "0 , user, '0000', current hour to second, today, 'ro' ";
    wcsel = wcsel + ", imen, 'A'||xopcao, iobj, 'MN', nopcao, tgif ";
    wcsel = wcsel + ", 'EC', today, 'AM' ";
    wcsel = wcsel + "from sment0 ";
    wcsel = wcsel + "where imen[1,4] <> 'EEEE' and imen[6,7] not in ('BR', 'MZ', 'ME', 'MC','ML','MG', 'MU') ";
    wcsel = wcsel + "and iobjt <> '-' ";
    
    wcursor.execStatement(wcsel);
    wcursor.closeStatement();
    

    wcsel = " delete from stoct0 where iobjt = 'AT' and cst = 'EC'";
    wcursor.execStatement(wcsel);
    wcursor.closeStatement();
    
    wcsel = "insert into stoct0 ";
    wcsel = wcsel + "( zk, zutl, zgrp, zhst, zdst, zcst ";
    wcsel = wcsel + ", itoc, xopcao, iobj, iobjt, nopcao, tgif ";
    wcsel = wcsel + ", cst, dst, cstm) ";
    wcsel = wcsel + "select distinct ";
    wcsel = wcsel + "0 , user, '0000', current hour to second, today, 'ro' ";
    wcsel = wcsel + ", iobj[1,5], '7', iobj[1,5]||'AT', 'AT', 'Atributos', ' ' ";
    wcsel = wcsel + ", 'EC', today, 'GE' ";
    wcsel = wcsel + "from sment0 ";
    wcsel = wcsel + "where iobjt = 'J' ";
    
    wcursor.execStatement(wcsel);
    wcursor.closeStatement();
    

    wcsel = "insert into stoct0 ";
    wcsel = wcsel + "( zk, zutl, zgrp, zhst, zdst, zcst ";
    wcsel = wcsel + ", itoc, xopcao, iobj, iobjt, nopcao, tgif ";
    wcsel = wcsel + ", cst, dst, cstm) ";
    wcsel = wcsel + "select distinct ";
    wcsel = wcsel + "0 , user, '0000', current hour to second, today, 'ro' ";
    wcsel = wcsel + ", iobj[1,5], '7', targs[1,5]||'AT', 'AT', 'Atributos', ' ' ";
    wcsel = wcsel + ", 'EC', today, 'GE' ";
    wcsel = wcsel + "from sment0 ";
    wcsel = wcsel + "where iobjt = 'X'  and iobj like 'JUMP%' and targs > ' ' and targs[1,5]  not like '%-%' ";
    wcsel = wcsel + "  and targs[1,5] not in (select iobj[1,5] from sment0 where iobjt='J') ";
    
    wcursor.execStatement(wcsel);
    wcursor.closeStatement();
    

    wcsel = " delete from stoct0 where iobjt = 'CM' and cst = 'EC'";
    wcursor.execStatement(wcsel);
    wcursor.closeStatement();
    
    wcsel = "insert into stoct0 ";
    wcsel = wcsel + "( zk, zutl, zgrp, zhst, zdst, zcst ";
    wcsel = wcsel + ", itoc, xopcao, iobj, iobjt, nopcao, tgif ";
    wcsel = wcsel + ", cst, dst, cstm) ";
    wcsel = wcsel + "select distinct ";
    wcsel = wcsel + "0 , user, '0000', current hour to second, today, 'ro' ";
    wcsel = wcsel + ", iobj[1,5], '7', iobj[1,5]||'CM', 'CM', 'Comandos', ' ' ";
    wcsel = wcsel + ", 'EC', today, 'GE' ";
    wcsel = wcsel + "from sment0 ";
    wcsel = wcsel + "where iobjt = 'J' ";
    
    wcursor.execStatement(wcsel);
    wcursor.closeStatement();
    
    wcsel = "insert into stoct0 ";
    wcsel = wcsel + "( zk, zutl, zgrp, zhst, zdst, zcst ";
    wcsel = wcsel + ", itoc, xopcao, iobj, iobjt, nopcao, tgif ";
    wcsel = wcsel + ", cst, dst, cstm) ";
    wcsel = wcsel + "select distinct ";
    wcsel = wcsel + "0 , user, '0000', current hour to second, today, 'ro' ";
    wcsel = wcsel + ", iobj[1,5], '7', targs[1,5]||'CM', 'CM', 'Comandos', ' ' ";
    wcsel = wcsel + ", 'EC', today, 'GE' ";
    wcsel = wcsel + "from sment0 ";
    wcsel = wcsel + "where iobjt = 'X'  and iobj like 'JUMP%' and targs > ' ' and targs[1,5]  not like '%-%' ";
    wcsel = wcsel + "  and targs[1,5] not in (select iobj[1,5] from sment0 where iobjt='J') ";
    
    wcursor.execStatement(wcsel);
    wcursor.closeStatement();
  }
  
  private void ger_HTML_file()
    throws MError
  {
    String wconnect = "JMETADATA";
    String wori = "J000A";
    String whtml = "<html>" + EOL + "<head>" + EOL + "</head>" + EOL + "<body>" + EOL + "<font face=\"Trebuchet MS\"> <p>" + EOL + "<font face=\"Trebuchet MS\" color=\"#009FDD\" size=3><b>ÍNDICE OBJECTOS MINIMAL</b></font><p>" + EOL;
    






    String wsep = "  ";
    MCursor stoc_curs = PT.Minimal.geral.MConnection.getMCursor(wconnect);
    String wcsel = " select itoc, iobjt, iobj, xopcao, nopcao, tgif from stoct0 ";
    wcsel = wcsel + " where itoc = '" + wori + "' ";
    wcsel = wcsel + " order by xopcao, nopcao";
    
    wnivel = 0;
    
    System.out.println("STOC:" + wcsel);
    stoc_curs.execQuery(wcsel);
    
    if (stoc_curs.getWdcode() == 0) {
      Vector wv = stoc_curs.getAllRecords();
      stoc_curs.close();
      for (int i = 0; i < wv.size(); i++) {
        Hashtable xstoct = (Hashtable)wv.elementAt(i);
        whtml = whtml + "<br><table border=\"1\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" bordercolor=\"#FFFFFF\" bgcolor=\"#F0ECD8\"  >" + EOL;
        whtml = whtml + "<tr bgcolor=\"#009FDD\"><td><font color=\"#FFFFFF\"><b>" + (String)xstoct.get("nopcao") + "<b></font></td></tr>" + EOL;
        whtml = whtml + "</table><br>" + EOL;
        
        if (((String)xstoct.get("iobjt")).startsWith("M")) {
          whtml = whtml + getMinHTMLMenu((String)xstoct.get("iobj"), (String)xstoct.get("nopcao"), false, wconnect, wsep, "AM", "MN");
        }
      }
    }
    whtml = whtml + "  </tocitem>" + EOL + "</toc>" + EOL;
    








    whtml = whtml + "</font>" + EOL + "</body>" + EOL + "</html>" + EOL;
    



    whtml = changePTchars(whtml);
    
    String wfilename = "mjhelp//man/eeee0.html";
    try {
      File wfile = new File(wfilename);
      if (wfile.exists()) {
        wfile.delete();
      }
      java.io.DataOutputStream fos = new java.io.DataOutputStream(new java.io.FileOutputStream(wfile));
      
      fos.writeBytes(whtml);
      fos.close();
      System.out.println("!***STOC0 write: " + wfilename);
    } catch (java.io.IOException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError("Ficheiro não encontrado");
    }
  }
  
  private String getMinHTMLMenu(String wori, String wname, boolean hasIcon, String wconnect, String wsep, String wcstm, String wiobjt)
    throws MError
  {
    // Byte code:
    //   0: iload_3
    //   1: ifeq +3 -> 4
    //   4: new 45	java/lang/StringBuffer
    //   7: dup
    //   8: invokespecial 46	java/lang/StringBuffer:<init>	()V
    //   11: aload 5
    //   13: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   16: ldc 77
    //   18: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   21: invokevirtual 49	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   24: astore 5
    //   26: ldc 126
    //   28: astore 8
    //   30: ldc 126
    //   32: astore 9
    //   34: iconst_0
    //   35: istore 10
    //   37: aload 7
    //   39: ldc 97
    //   41: invokevirtual 98	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   44: ifeq +6 -> 50
    //   47: iconst_1
    //   48: istore 10
    //   50: aload 4
    //   52: invokestatic 40	PT/Minimal/geral/MConnection:getMCursor	(Ljava/lang/String;)LPT/Minimal/geral/MCursor;
    //   55: astore 11
    //   57: ldc 127
    //   59: astore 12
    //   61: new 45	java/lang/StringBuffer
    //   64: dup
    //   65: invokespecial 46	java/lang/StringBuffer:<init>	()V
    //   68: aload 12
    //   70: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   73: ldc 79
    //   75: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   78: aload_1
    //   79: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   82: ldc 80
    //   84: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   87: invokevirtual 49	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   90: astore 12
    //   92: new 45	java/lang/StringBuffer
    //   95: dup
    //   96: invokespecial 46	java/lang/StringBuffer:<init>	()V
    //   99: aload 12
    //   101: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   104: ldc 81
    //   106: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   109: invokevirtual 49	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   112: astore 12
    //   114: aload 11
    //   116: aload 12
    //   118: invokevirtual 83	PT/Minimal/geral/MCursor:execQuery	(Ljava/lang/String;)V
    //   121: aload 11
    //   123: invokevirtual 85	PT/Minimal/geral/MCursor:getAllRecords	()Ljava/util/Vector;
    //   126: astore 13
    //   128: aload 11
    //   130: invokevirtual 86	PT/Minimal/geral/MCursor:close	()V
    //   133: aload 13
    //   135: ifnull +20 -> 155
    //   138: aload 13
    //   140: invokevirtual 25	java/util/Vector:size	()I
    //   143: ifle +12 -> 155
    //   146: iload 10
    //   148: ifeq +7 -> 155
    //   151: ldc -128
    //   153: astore 9
    //   155: aload 6
    //   157: ldc 100
    //   159: invokevirtual 98	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   162: ifeq +183 -> 345
    //   165: aload_1
    //   166: ldc -127
    //   168: invokevirtual 10	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   171: ifne +174 -> 345
    //   174: aload_1
    //   175: ldc -126
    //   177: invokevirtual 98	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   180: ifeq +17 -> 197
    //   183: aload_1
    //   184: iconst_4
    //   185: iconst_5
    //   186: invokevirtual 131	java/lang/String:substring	(II)Ljava/lang/String;
    //   189: ldc -124
    //   191: invokevirtual 10	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   194: ifne +26 -> 220
    //   197: aload_1
    //   198: ldc -123
    //   200: invokevirtual 98	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   203: ifeq +75 -> 278
    //   206: aload_1
    //   207: iconst_4
    //   208: iconst_5
    //   209: invokevirtual 131	java/lang/String:substring	(II)Ljava/lang/String;
    //   212: ldc -124
    //   214: invokevirtual 10	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   217: ifeq +61 -> 278
    //   220: new 45	java/lang/StringBuffer
    //   223: dup
    //   224: invokespecial 46	java/lang/StringBuffer:<init>	()V
    //   227: aload 5
    //   229: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   232: aload 9
    //   234: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   237: ldc -122
    //   239: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   242: aload_1
    //   243: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   246: ldc -121
    //   248: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   251: aload_2
    //   252: invokevirtual 136	java/lang/String:toUpperCase	()Ljava/lang/String;
    //   255: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   258: ldc -119
    //   260: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   263: aload_0
    //   264: getfield 3	PT/Minimal/entities/STOC0:EOL	Ljava/lang/String;
    //   267: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   270: invokevirtual 49	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   273: astore 8
    //   275: goto +79 -> 354
    //   278: new 45	java/lang/StringBuffer
    //   281: dup
    //   282: invokespecial 46	java/lang/StringBuffer:<init>	()V
    //   285: aload 5
    //   287: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   290: aload 9
    //   292: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   295: ldc -122
    //   297: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   300: aload_1
    //   301: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   304: ldc -118
    //   306: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   309: aload_2
    //   310: invokevirtual 139	java/lang/String:trim	()Ljava/lang/String;
    //   313: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   316: ldc -116
    //   318: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   321: aload_1
    //   322: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   325: ldc -115
    //   327: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   330: aload_0
    //   331: getfield 3	PT/Minimal/entities/STOC0:EOL	Ljava/lang/String;
    //   334: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   337: invokevirtual 49	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   340: astore 8
    //   342: goto +12 -> 354
    //   345: aload_1
    //   346: ldc -127
    //   348: invokevirtual 10	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   351: ifeq +3 -> 354
    //   354: aload 13
    //   356: ifnull +55 -> 411
    //   359: aload 13
    //   361: invokevirtual 25	java/util/Vector:size	()I
    //   364: ifle +47 -> 411
    //   367: iload 10
    //   369: ifeq +42 -> 411
    //   372: new 45	java/lang/StringBuffer
    //   375: dup
    //   376: invokespecial 46	java/lang/StringBuffer:<init>	()V
    //   379: aload 8
    //   381: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   384: ldc -114
    //   386: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   389: aload_0
    //   390: getfield 3	PT/Minimal/entities/STOC0:EOL	Ljava/lang/String;
    //   393: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   396: invokevirtual 49	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   399: astore 8
    //   401: aload_0
    //   402: aload_0
    //   403: getfield 4	PT/Minimal/entities/STOC0:wnivel	I
    //   406: iconst_1
    //   407: iadd
    //   408: putfield 4	PT/Minimal/entities/STOC0:wnivel	I
    //   411: ldc 126
    //   413: astore 14
    //   415: iconst_0
    //   416: istore 15
    //   418: iload 15
    //   420: aload 13
    //   422: invokevirtual 25	java/util/Vector:size	()I
    //   425: if_icmpge +126 -> 551
    //   428: iload 10
    //   430: ifeq +121 -> 551
    //   433: aload 13
    //   435: iload 15
    //   437: invokevirtual 87	java/util/Vector:elementAt	(I)Ljava/lang/Object;
    //   440: checkcast 88	java/util/Hashtable
    //   443: astore 16
    //   445: getstatic 29	java/lang/System:out	Ljava/io/PrintStream;
    //   448: new 45	java/lang/StringBuffer
    //   451: dup
    //   452: invokespecial 46	java/lang/StringBuffer:<init>	()V
    //   455: ldc -113
    //   457: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   460: aload 16
    //   462: ldc 99
    //   464: invokevirtual 92	java/util/Hashtable:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   467: invokevirtual 124	java/lang/StringBuffer:append	(Ljava/lang/Object;)Ljava/lang/StringBuffer;
    //   470: invokevirtual 49	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   473: invokevirtual 31	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   476: new 45	java/lang/StringBuffer
    //   479: dup
    //   480: invokespecial 46	java/lang/StringBuffer:<init>	()V
    //   483: aload 8
    //   485: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   488: aload_0
    //   489: aload 16
    //   491: ldc 99
    //   493: invokevirtual 92	java/util/Hashtable:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   496: checkcast 93	java/lang/String
    //   499: aload 16
    //   501: ldc 91
    //   503: invokevirtual 92	java/util/Hashtable:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   506: checkcast 93	java/lang/String
    //   509: iconst_1
    //   510: aload 4
    //   512: aload 5
    //   514: aload 16
    //   516: ldc -112
    //   518: invokevirtual 92	java/util/Hashtable:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   521: checkcast 93	java/lang/String
    //   524: aload 16
    //   526: ldc 96
    //   528: invokevirtual 92	java/util/Hashtable:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   531: checkcast 93	java/lang/String
    //   534: invokespecial 102	PT/Minimal/entities/STOC0:getMinHTMLMenu	(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   537: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   540: invokevirtual 49	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   543: astore 8
    //   545: iinc 15 1
    //   548: goto -130 -> 418
    //   551: aload 13
    //   553: ifnull +62 -> 615
    //   556: aload 13
    //   558: invokevirtual 25	java/util/Vector:size	()I
    //   561: ifle +54 -> 615
    //   564: iload 10
    //   566: ifeq +49 -> 615
    //   569: new 45	java/lang/StringBuffer
    //   572: dup
    //   573: invokespecial 46	java/lang/StringBuffer:<init>	()V
    //   576: aload 8
    //   578: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   581: ldc -111
    //   583: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   586: aload_0
    //   587: getfield 3	PT/Minimal/entities/STOC0:EOL	Ljava/lang/String;
    //   590: invokevirtual 47	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   593: invokevirtual 49	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   596: astore 8
    //   598: aload_0
    //   599: aload_0
    //   600: getfield 4	PT/Minimal/entities/STOC0:wnivel	I
    //   603: iconst_1
    //   604: isub
    //   605: putfield 4	PT/Minimal/entities/STOC0:wnivel	I
    //   608: aload_0
    //   609: getfield 4	PT/Minimal/entities/STOC0:wnivel	I
    //   612: ifne +3 -> 615
    //   615: aload 8
    //   617: areturn
    // Line number table:
    //   Java source line #273	-> byte code offset #0
    //   Java source line #275	-> byte code offset #4
    //   Java source line #276	-> byte code offset #26
    //   Java source line #277	-> byte code offset #30
    //   Java source line #278	-> byte code offset #34
    //   Java source line #279	-> byte code offset #37
    //   Java source line #280	-> byte code offset #47
    //   Java source line #283	-> byte code offset #50
    //   Java source line #284	-> byte code offset #57
    //   Java source line #285	-> byte code offset #61
    //   Java source line #286	-> byte code offset #92
    //   Java source line #287	-> byte code offset #114
    //   Java source line #288	-> byte code offset #121
    //   Java source line #289	-> byte code offset #128
    //   Java source line #291	-> byte code offset #133
    //   Java source line #292	-> byte code offset #151
    //   Java source line #296	-> byte code offset #155
    //   Java source line #297	-> byte code offset #174
    //   Java source line #298	-> byte code offset #220
    //   Java source line #300	-> byte code offset #278
    //   Java source line #303	-> byte code offset #345
    //   Java source line #308	-> byte code offset #354
    //   Java source line #309	-> byte code offset #372
    //   Java source line #310	-> byte code offset #401
    //   Java source line #312	-> byte code offset #411
    //   Java source line #313	-> byte code offset #415
    //   Java source line #314	-> byte code offset #433
    //   Java source line #315	-> byte code offset #445
    //   Java source line #316	-> byte code offset #476
    //   Java source line #313	-> byte code offset #545
    //   Java source line #318	-> byte code offset #551
    //   Java source line #319	-> byte code offset #569
    //   Java source line #320	-> byte code offset #598
    //   Java source line #321	-> byte code offset #608
    //   Java source line #325	-> byte code offset #615
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	618	0	this	STOC0
    //   0	618	1	wori	String
    //   0	618	2	wname	String
    //   0	618	3	hasIcon	boolean
    //   0	618	4	wconnect	String
    //   0	618	5	wsep	String
    //   0	618	6	wcstm	String
    //   0	618	7	wiobjt	String
    //   28	588	8	wsubtoc	String
    //   32	259	9	wimage	String
    //   35	530	10	wmenu	boolean
    //   55	74	11	stoc_curs	MCursor
    //   59	58	12	wcsel	String
    //   126	431	13	wv	Vector
    //   413	3	14	wtitle	String
    //   416	130	15	i	int
    //   443	82	16	xstoct	Hashtable
  }
  
  private void ger_TOC_file()
    throws MError
  {
    String wconnect = "JMETADATA";
    String wori = "J000A";
    String wtoc = "<toc>" + EOL + "  <tocitem target=\"J000A\" image=\"toplevelfolder\">Minimal " + EOL;
    
    String wsep = "  ";
    MCursor stoc_curs = PT.Minimal.geral.MConnection.getMCursor(wconnect);
    String wcsel = " select itoc, iobjt, iobj, xopcao, nopcao, tgif from stoct0 ";
    wcsel = wcsel + " where itoc = '" + wori + "' ";
    wcsel = wcsel + " order by xopcao, nopcao";
    
    System.out.println("STOC:" + wcsel);
    stoc_curs.execQuery(wcsel);
    
    if (stoc_curs.getWdcode() == 0) {
      Vector wv = stoc_curs.getAllRecords();
      stoc_curs.close();
      for (int i = 0; i < wv.size(); i++) {
        Hashtable xstoct = (Hashtable)wv.elementAt(i);
        if (((String)xstoct.get("iobjt")).startsWith("M")) {
          wtoc = wtoc + getMinTocMenu((String)xstoct.get("iobj"), (String)xstoct.get("nopcao"), false, wconnect, wsep);
        }
      }
    }
    wtoc = wtoc + "  </tocitem>" + EOL + "</toc>" + EOL;
    

    wtoc = changePTchars(wtoc);
    
    String wfilename = "mjhelp//mjHelpTOC.xml";
    try {
      File wfile = new File(wfilename);
      if (wfile.exists()) {
        wfile.delete();
      }
      java.io.DataOutputStream fos = new java.io.DataOutputStream(new java.io.FileOutputStream(wfile));
      
      fos.writeBytes(wtoc);
      fos.close();
      System.out.println("!***STOC0 write: " + wfilename);
    } catch (java.io.IOException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError("Ficheiro não encontrado");
    }
  }
  
  private String getMinTocMenu(String wori, String wname, boolean hasIcon, String wconnect, String wsep) throws MError {
    if (hasIcon) {}
    
    wsep = wsep + "  ";
    
    String wsubtoc = wsep + "<tocitem target=\"" + wori + "\"> " + wname + EOL;
    
    MCursor stoc_curs = PT.Minimal.geral.MConnection.getMCursor(wconnect);
    String wcsel = " select itoc, iobjt, iobj, xopcao, nopcao, tgif from stoct0 ";
    wcsel = wcsel + " where itoc = '" + wori + "' ";
    wcsel = wcsel + " order by xopcao, nopcao";
    stoc_curs.execQuery(wcsel);
    Vector wv = stoc_curs.getAllRecords();
    stoc_curs.close();
    for (int i = 0; i < wv.size(); i++) {
      Hashtable xstoct = (Hashtable)wv.elementAt(i);
      
      System.out.println(xstoct.get("iobj"));
      wsubtoc = wsubtoc + getMinTocMenu((String)xstoct.get("iobj"), (String)xstoct.get("nopcao"), true, wconnect, wsep);
    }
    



    wsubtoc = wsubtoc + wsep + "</tocitem>" + EOL;
    return wsubtoc;
  }
  
  private String getMinTocMenuItem(String wori, String wname, String wtitle, String wtgif, String wsep) {
    return wsep + "  <tocitem target=\"" + wori + "\"> " + wname + "</tocitem>" + EOL;
  }
  
  private void ger_MAP_file() throws MError
  {
    String wconnect = "JMETADATA";
    String wori = "J000A";
    String wmap = "<map>" + EOL + "<mapID target=\"toplevelfolder\" url=\"images/toplevel.gif\" />" + EOL + "<mapID target=\"top\" url=\"man/J000A.html\" />" + EOL;
    


    MCursor stoc_curs = PT.Minimal.geral.MConnection.getMCursor(wconnect);
    String wcsel = " select distinct iobj, cstm from stoct0 ";
    wcsel = wcsel + " order by iobj";
    
    System.out.println("STOC:" + wcsel);
    stoc_curs.execQuery(wcsel);
    
    if (stoc_curs.getWdcode() == 0) {
      Vector wv = stoc_curs.getAllRecords();
      stoc_curs.close();
      for (int i = 0; i < wv.size(); i++) {
        Hashtable xstoct = (Hashtable)wv.elementAt(i);
        if (((String)xstoct.get("cstm")).startsWith("AM")) {
          wmap = wmap + "<mapID target=\"" + (String)xstoct.get("iobj") + "\" url=\"man/" + (String)xstoct.get("iobj") + ".html\" />" + EOL;
        } else {
          wmap = wmap + "<mapID target=\"" + (String)xstoct.get("iobj") + "\" url=\"aut/" + (String)xstoct.get("iobj") + ".html\" />" + EOL;
        }
      }
    }
    
    wmap = wmap + "</map>" + EOL;
    
    String wfilename = "mjhelp//mjMap.map";
    try {
      File wfile = new File(wfilename);
      if (wfile.exists()) {
        wfile.delete();
      }
      java.io.DataOutputStream fos = new java.io.DataOutputStream(new java.io.FileOutputStream(wfile));
      fos.write(wmap.getBytes());
      fos.close();
      System.out.println("!***STOC0 write: " + wfilename);
    } catch (java.io.IOException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError("Ficheiro não encontrado");
    }
  }
  
  private void ger_IX_file()
    throws MError
  {
    String wconnect = "JMETADATA";
    String wori = "J000A";
    String windex = "<index>" + EOL;
    
    MCursor stoc_curs = PT.Minimal.geral.MConnection.getMCursor(wconnect);
    String wcsel = " select distinct iobj, nopcao, cstm from stoct0 ";
    wcsel = wcsel + " where cstm = 'AM'";
    wcsel = wcsel + " order by nopcao";
    
    System.out.println("STOC:" + wcsel);
    stoc_curs.execQuery(wcsel);
    
    if (stoc_curs.getWdcode() == 0) {
      Vector wv = stoc_curs.getAllRecords();
      stoc_curs.close();
      for (int i = 0; i < wv.size(); i++) {
        Hashtable xstoct = (Hashtable)wv.elementAt(i);
        windex = windex + "  <indexitem target=\"" + (String)xstoct.get("iobj") + "\"> " + (String)xstoct.get("nopcao") + " </indexitem>" + EOL;
      }
    }
    


    windex = windex + "  </index>" + EOL;
    
    windex = changePTchars(windex);
    
    String wfilename = "mjhelp//mjIndex.xml";
    try {
      File wfile = new File(wfilename);
      if (wfile.exists()) {
        wfile.delete();
      }
      java.io.DataOutputStream fos = new java.io.DataOutputStream(new java.io.FileOutputStream(wfile));
      fos.write(windex.getBytes());
      fos.close();
    } catch (java.io.IOException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError("Ficheiro não encontrado");
    }
  }
  
  private void ger_GEFiles() throws MError {
    String wconnect = "JMETADATA";
    
    MCursor stoc_curs = PT.Minimal.geral.MConnection.getMCursor(wconnect);
    String wcsel = " select distinct itoc, iobj, iobjt, cstm from stoct0 ";
    wcsel = wcsel + " where cstm = 'GE'";
    wcsel = wcsel + " order by itoc";
    
    System.out.println("STOC:" + wcsel);
    stoc_curs.execQuery(wcsel);
    
    if (stoc_curs.getWdcode() == 0) {
      Vector wv = stoc_curs.getAllRecords();
      stoc_curs.close();
      for (int i = 0; i < wv.size(); i++) {
        Hashtable xstoct = (Hashtable)wv.elementAt(i);
        System.out.println("STOC File:" + xstoct.get("iobj"));
        if (((String)xstoct.get("iobjt")).startsWith("AT"))
        {
          ger_ATRIB_file(((String)xstoct.get("itoc")).substring(0, 5));
        }
        if (((String)xstoct.get("iobjt")).startsWith("CM")) {
          ger_CMD_file(((String)xstoct.get("itoc")).substring(0, 5));
        }
      }
    }
  }
  
  private void ger_ATRIB_file() throws MError {
    for (int i = 0; i < wrows.size(); i++) {
      MRow xstoct = (MRow)wrows.elementAt(0);
      String wiobj = (String)xstoct.get("iobj");
      ger_ATRIB_file(wiobj.substring(0, 5));
    }
  }
  
  private void ger_ATRIB_file(String went)
    throws MError
  {
    String watrib = "<html>" + EOL + "<head>" + EOL + "</head>" + EOL + "<body>" + EOL;
    



    MRow wmrfc = PT.Minimal.geral.MMData.getMRow("MRFC0");
    String wcsel = "select * from  mrfct0  where i_ob = '" + went.toUpperCase() + "' " + " and i_tc in ('CB','TF','TA','BT','TP', 'PN')" + " and (q_oe > 0 or q_oe = 0 and q_oc > 0) " + " order by q_oe, q_ot";
    




    String wtext = "";
    
    Vector wv = PT.Minimal.geral.MUtil.getRows(wmrfc, wcsel, 999999);
    
    if (wv.size() == 0) {
      watrib = watrib + "Por definir";
    } else {
      wmrfc = (MRow)wv.elementAt(0);
    }
    if ((((String)wmrfc.get("i_tc")).startsWith("TF")) || (((String)wmrfc.get("i_tc")).startsWith("TA"))) {
      watrib = "<table border=\"0\" width=\"650\" cellspacing=\"0\" cellpadding=\"3\" bordercolor=\"#FFFFFF\" bgcolor=\"#F0ECD8\"  >" + EOL;
      watrib = watrib + "<tr bgcolor=\"#009FDD\"><td width=\"550\"><font face=\"Trebuchet MS\"color=\"#FFFFFF\"><b>" + (String)wmrfc.get("i_ob") + "<b></font></td><td width=\"100\"><font face=\"Trebuchet MS\" color=\"#FFFFFF\" size=\"2\"><b>Minimal L</font><font face=\"Arial\" color=\"#FFFFFF\" size=\"2\">&</font><font face=\"Trebuchet MS\" color=\"#FFFFFF\" size=\"2\">W</font><br><font face=\"Trebuchet MS\" color=\"#FFFFFF\" size=\"1\">(Linux <font face=\"Arial\" color=\"#FFFFFF\" size=\"1\">&</font><font face=\"Trebuchet MS\" color=\"#FFFFFF\" size=\"1\"> Windows)<b></font></td></tr></table>" + EOL;
      
      watrib = watrib + "<font face=\"Trebuchet MS\" size=3 color=\"#009FDD\">" + "<p> <b> Painel " + (String)wmrfc.get("i_ob") + "</b> <p>" + EOL + "</font>";
      


      watrib = watrib + "<table border=\"1\" width=\"650\" cellspacing=\"0\" cellpadding=\"3\" bordercolor=\"#FFFFFF\" bgcolor=\"#F0ECD8\"  >" + EOL;
      watrib = watrib + "<tr bgcolor=\"#009FDD\"><td width=\"150\"><font color=\"#FFFFFF\"><b>Label<b></font></td><td width=\"100\"><font color=\"#FFFFFF\"><b>Nome Interno<b></font></td><td width=\"550\"><font color=\"#FFFFFF\"><b>Descrição<b></font></td></tr>" + EOL;
    }
    
    for (int i = 0; i < wv.size(); i++) {
      wmrfc = (MRow)wv.elementAt(i);
      if ((((String)wmrfc.get("i_tc")).startsWith("TF")) || (((String)wmrfc.get("i_tc")).startsWith("TA"))) {
        watrib = watrib + "<tr>" + EOL;
        watrib = watrib + getAtribHtml(wmrfc);
        watrib = watrib + "</tr>" + EOL;
      }
      if (((String)wmrfc.get("i_tc")).startsWith("TP")) {
        watrib = watrib + getTabPanelHtml(wmrfc);
      }
      if (((String)wmrfc.get("i_tc")).startsWith("PN")) {
        watrib = watrib + getPanelHtml(wmrfc);
      }
    }
    if ((((String)wmrfc.get("i_tc")).startsWith("TF")) || (((String)wmrfc.get("i_tc")).startsWith("TA"))) {
      watrib = watrib + "</table><br><br>";
    }
    








    watrib = watrib + "</body>" + EOL + "</html>" + EOL;
    


    watrib = changePTchars(watrib);
    
    String wfilename = "mjhelp/aut/" + went + "AT.html";
    try {
      File wfile = new File(wfilename);
      if (wfile.exists()) {
        wfile.delete();
      }
      java.io.DataOutputStream fos = new java.io.DataOutputStream(new java.io.FileOutputStream(wfile));
      fos.write(watrib.getBytes());
      fos.close();
    } catch (java.io.IOException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError("Ficheiro não encontrado");
    }
  }
  
  private String getTabPanelHtml(MRow wmrfc) throws MError
  {
    String whtm = "";String wtext = "";
    String wobj = ((String)wmrfc.get("i_comp")).toUpperCase();
    String wcsel = "select * from  mrfct0  where i_ob = '" + wobj + "' " + " and i_tc in ('CB','TF','TA','BT','TP', 'PN')" + " and q_oe > 0" + " order by q_oe, q_ot";
    




    Vector wv = PT.Minimal.geral.MUtil.getRows(wmrfc, wcsel, 999999);
    
    for (int i = 0; i < wv.size(); i++) {
      wmrfc = (MRow)wv.elementAt(i);
      if ((((String)wmrfc.get("i_tc")).startsWith("TF")) || (((String)wmrfc.get("i_tc")).startsWith("TA"))) {
        whtm = whtm + getAtribHtml(wmrfc);
      }
      
      if (((String)wmrfc.get("i_tc")).startsWith("TP")) {
        whtm = whtm + getTabPanelHtml(wmrfc);
      }
      if (((String)wmrfc.get("i_tc")).startsWith("PN")) {
        whtm = whtm + getPanelHtml(wmrfc);
      }
    }
    
    return whtm;
  }
  
  private String getPanelHtml(MRow wmrfc) throws MError
  {
    String whtm = "<table border=\"0\" width=\"650\" cellspacing=\"0\" cellpadding=\"3\" bordercolor=\"#FFFFFF\" bgcolor=\"#F0ECD8\"  >" + EOL;
    whtm = whtm + "<tr bgcolor=\"#009FDD\"><td width=\"550\"><font face=\"Trebuchet MS\"color=\"#FFFFFF\"><b>" + (String)wmrfc.get("i_ob") + "<b></font></td><td width=\"100\"><font face=\"Trebuchet MS\" color=\"#FFFFFF\" size=\"2\"><b>Minimal L</font><font face=\"Arial\" color=\"#FFFFFF\" size=\"2\">&</font><font face=\"Trebuchet MS\" color=\"#FFFFFF\" size=\"2\">W</font><br><font face=\"Trebuchet MS\" color=\"#FFFFFF\" size=\"1\">(Linux <font face=\"Arial\" color=\"#FFFFFF\" size=\"1\">&</font><font face=\"Trebuchet MS\" color=\"#FFFFFF\" size=\"1\"> Windows)<b></font></td></tr></table>" + EOL;
    
    whtm = whtm + "<font face=\"Trebuchet MS\" size=3 color=\"#009FDD\">" + "<p> <b> Painel " + (String)wmrfc.get("t_lin") + "</b> <p>" + EOL + "</font>";
    


    String wobj = ((String)wmrfc.get("i_comp")).toUpperCase();
    String wcsel = "select * from  mrfct0  where i_ob = '" + wobj + "' " + " and i_tc in ('CB','TF','TA','BT','TP', 'PN')" + " and q_oe > 0" + " order by q_oe, q_ot";
    




    Vector wv = PT.Minimal.geral.MUtil.getRows(wmrfc, wcsel, 999999);
    
    whtm = whtm + "<table border=\"1\" width=\"650\" cellspacing=\"0\" cellpadding=\"3\" bordercolor=\"#F0ECD8\"   >" + EOL;
    whtm = whtm + "<tr bgcolor=\"#009FDD\"><td width=\"150\"><font face=\"Trebuchet MS\" color=\"#FFFFFF\"><b>Label<b></font></td><td width=\"100\"><font face=\"Trebuchet MS\" color=\"#FFFFFF\"><b>Nome Interno<b></font></td><td width=\"550\"><font face=\"Trebuchet MS\" color=\"#FFFFFF\"><b>Descrição<b></font></td></tr>" + EOL;
    
    for (int i = 0; i < wv.size(); i++) {
      whtm = whtm + "<tr>";
      wmrfc = (MRow)wv.elementAt(i);
      if ((((String)wmrfc.get("i_tc")).startsWith("TF")) || (((String)wmrfc.get("i_tc")).startsWith("TA"))) {
        whtm = whtm + getAtribHtml(wmrfc);
      }
      if (((String)wmrfc.get("i_tc")).startsWith("TP")) {
        whtm = whtm + getTabPanelHtml(wmrfc);
      }
      if (((String)wmrfc.get("i_tc")).startsWith("PN")) {
        whtm = whtm + getPanelHtml(wmrfc);
      }
      whtm = whtm + "</tr>" + EOL;
    }
    whtm = whtm + "</table><br><br>";
    return whtm;
  }
  
  private String getAtribHtml(MRow wmrfc) throws MError {
    String wtext = "";String wtlin = "";
    
    wtlin = (String)wmrfc.get("t_lin");
    if ((wtlin.trim().equals("")) || (!((String)wmrfc.get("c_label")).trim().equals("S"))) {
      wtlin = "<td bgcolor=\"#F0ECD8\"><font face=\"Trebuchet MS\" SIZE=2 color=\"#009FDD\"> &nbsp; </font></td>";
    } else {
      wtlin = "<td bgcolor=\"#F0ECD8\"><font face=\"Trebuchet MS\" SIZE=2 color=\"#009FDD\"> " + wtlin.trim() + "</font></td>";
    }
    


    String whtm = wtlin + " <td bgcolor=\"#FFFFFF\">" + ((String)wmrfc.get("i_comp")).trim() + "</td>";
    


    MCursor wcursor = PT.Minimal.geral.MConnection.getMCursor("JMETADATA");
    try {
      if (wmrfc.hasText(wcursor)) {
        wtext = wmrfc.getAssociatedText(wcursor);
      } else {
        wtext = (String)wmrfc.get("t_txt");
      }
    } finally {
      wcursor.close();
    }
    if (wtext.trim().length() != 0) {
      whtm = whtm + "<td> " + wtext.trim() + "</td>";
    } else {
      whtm = whtm + "<td> &nbsp; </td>";
    }
    
    return whtm;
  }
  
  private void ger_CMD_file() throws MError {
    for (int i = 0; i < wrows.size(); i++) {
      MRow xstoct = (MRow)wrows.elementAt(0);
      String wiobj = (String)xstoct.get("iobj");
      ger_CMD_file(wiobj.substring(0, 5));
    }
  }
  
  private void ger_CMD_file(String went)
    throws MError
  {
    String watrib = "<html>" + EOL + "<head>" + EOL + "</head>" + EOL + "<body>" + EOL;
    



    MRow xsment = PT.Minimal.geral.MMData.getMRow("SMEN0");
    String wcsel = " select * from sment0 ";
    wcsel = wcsel + " where imen = '" + went.trim() + "BR' ";
    wcsel = wcsel + " order by xopcao, nopcao";
    Vector wv = PT.Minimal.geral.MUtil.getRows(xsment, wcsel, 999999);
    String whtm = "";
    if (wv.size() == 0) {
      watrib = watrib + "Por definir";
    }
    else if (((String)((MRow)wv.elementAt(0)).get("imen")).trim().length() == 7) {
      whtm = "<table border=\"0\" width=\"650\" cellspacing=\"0\" cellpadding=\"3\" bordercolor=\"#FFFFFF\" bgcolor=\"#F0ECD8\"  >" + EOL;
      whtm = whtm + "<tr bgcolor=\"#009FDD\"><td width=\"550\"><font face=\"Trebuchet MS\"color=\"#FFFFFF\"><b>" + (String)((MRow)wv.elementAt(0)).get("imen") + "<b></font></td><td width=\"100\"><font face=\"Trebuchet MS\" color=\"#FFFFFF\" size=\"2\"><b>Minimal L</font><font face=\"Arial\" color=\"#FFFFFF\" size=\"2\">&</font><font face=\"Trebuchet MS\" color=\"#FFFFFF\" size=\"2\">W</font><br><font face=\"Trebuchet MS\" color=\"#FFFFFF\" size=\"1\">(Linux <font face=\"Arial\" color=\"#FFFFFF\" size=\"1\">&</font><font face=\"Trebuchet MS\" color=\"#FFFFFF\" size=\"1\"> Windows)<b></font></td></tr></table>" + EOL;
    }
    
    for (int i = 0; i < wv.size(); i++) {
      xsment = (MRow)wv.elementAt(i);
      if (!((String)xsment.get("iobjt")).startsWith("-")) {
        if (i > 0) {
          whtm = "";
        }
        if (((String)xsment.get("iobjt")).startsWith("M")) {
          watrib = watrib + getMenuHtml(xsment, whtm);
        }
        if (((String)xsment.get("iobjt")).startsWith("X")) {
          watrib = watrib + getMenuItemHtml(xsment);
        }
      }
    }
    







    watrib = watrib + "</body>" + EOL + "</html>" + EOL;
    


    watrib = changePTchars(watrib);
    String wfilename = "mjhelp/aut/" + went + "CM.html";
    try {
      File wfile = new File(wfilename);
      if (wfile.exists()) {
        wfile.delete();
      }
      java.io.DataOutputStream fos = new java.io.DataOutputStream(new java.io.FileOutputStream(wfile));
      fos.write(watrib.getBytes());
      fos.close();
    } catch (java.io.IOException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError("Ficheiro não encontrado");
    }
  }
  
  private String getMenuHtml(MRow xesnmt, String whtm) throws MError
  {
    whtm = whtm + "<font face=\"Trebuchet MS\" SIZE=3 color=\"#009FDD\">" + "<p> <b> Menu " + (String)xesnmt.get("nopcao") + "</b> <p>" + EOL + "</font>" + "<font face=\"Trebuchet MS\" SIZE=1 color=\"#000000\">";
    



    whtm = whtm + "<table border=\"1\" width=\"650\" cellspacing=\"0\" cellpadding=\"3\" bordercolor=\"#FOECD8\" >" + EOL;
    whtm = whtm + "<tr bgcolor=\"#009FDD\"><td width=\"250\"><font face=\"Trebuchet MS\" color=\"#FFFFFF\"><b>Item Menu<b></font></td><td width=\"100\"><font face=\"Trebuchet MS\" color=\"#FFFFFF\"><b>Comando<b></font></td><td width=\"450\"><font face=\"Trebuchet MS\" color=\"#FFFFFF\"><b>Descrição<b></font></td></tr>" + EOL;
    
    MRow xsment = PT.Minimal.geral.MMData.getMRow("SMEN0");
    String wcsel = " select * from sment0 ";
    wcsel = wcsel + " where imen = '" + xesnmt.get("iobj") + "' ";
    wcsel = wcsel + " order by xopcao, nopcao";
    Vector wv = PT.Minimal.geral.MUtil.getRows(xsment, wcsel, 999999);
    
    for (int i = 0; i < wv.size(); i++) {
      whtm = whtm + "<tr>";
      xsment = (MRow)wv.elementAt(i);
      if (!((String)xsment.get("iobjt")).startsWith("-")) {
        if (((String)xsment.get("iobjt")).startsWith("M")) {
          whtm = whtm + getMenuHtml(xsment, whtm);
        }
        if (((String)xsment.get("iobjt")).startsWith("X")) {
          whtm = whtm + getMenuItemHtml(xsment);
        }
      }
      whtm = whtm + "</tr>";
    }
    whtm = whtm + "</table></font><br>";
    
    return whtm;
  }
  
  private String getMenuItemHtml(MRow xsment) throws MError
  {
    String wtext = "";String wcmd = "N/A";
    String whtm = "<td witdh=150 bgcolor=\"#FOECD8\"><font face=\"Trebuchet MS\" SIZE=2 color=\"#009FDD\"> " + (String)xsment.get("nopcao") + "</font></td> ";
    
    MCursor wcursor = PT.Minimal.geral.MConnection.getMCursor("JMETADATA");
    try {
      if (xsment.hasText(wcursor)) {
        wtext = xsment.getAssociatedText(wcursor);
      } else {
        MRow xmcfxt = PT.Minimal.geral.MMData.getMRow("MCFX0");
        String wcsel = " select * from mcfxt0 ";
        wcsel = wcsel + " where i_ob = '" + ((String)xsment.get("imen")).substring(0, 5) + "' ";
        wcsel = wcsel + " and i_cmd = '" + (String)xsment.get("iobj") + "' ";
        Vector wv = PT.Minimal.geral.MUtil.getRows(xmcfxt, wcsel, 999999);
        if (wv.size() > 0) {
          xmcfxt = (MRow)wv.elementAt(0);
          if (xmcfxt.hasText(wcursor)) {
            wtext = xmcfxt.getAssociatedText(wcursor);
          } else {
            wtext = (String)xmcfxt.get("t_ajuda");
          }
          wcmd = ((String)xmcfxt.get("i_cmd")).trim();
        }
      }
    } finally {
      wcursor.close();
    }
    
    whtm = whtm + " <td ><font face=\"Trebuchet MS\" size=2>" + wcmd + "</font><fontsize=2></td><td><font face=\"Trebuchet MS\" size=2> " + wtext + "&nbsp;</font></td>";
    


    whtm = whtm + EOL;
    return whtm;
  }
  


  private String changePTchars(String ws)
  {
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "í", "&iacute;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "ú", "&uacute;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "ó", "&oacute;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "ô", "&ocirc;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "õ", "&otilde;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "é", "&eacute;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "ê", "&ecirc;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "ç", "&ccedil;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "á", "&aacute;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "ã", "&atilde;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "â", "&acirc;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "à", "&agrave;");
    
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "Í", "&Iacute;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "Ú", "&Uacute;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "Ó", "&Oacute;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "Ô", "&Ocirc;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "Õ", "&Otilde;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "É", "&Eacute;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "Ê", "&Ecirc;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "Ç", "&Ccedil;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "Á", "&Aacute;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "Ã", "&Atilde;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "Â", "&Acirc;");
    ws = PT.Minimal.geral.MUtil.stringRep(ws, "À", "&Agrave;");
    
    return ws;
  }
  
  private void brand_files() throws MError
  {
    File wfiledir = new File("mjhelp\\man");
    if (wfiledir.isDirectory()) {
      String[] wlist = wfiledir.list();
      for (int i = 0; i < wlist.length; i++) {
        if (wlist[i].endsWith("html")) {
          System.out.println(wlist[i]);
          File wfile = new File("mjhelp\\man\\" + wlist[i]);
          if ((wfile.canRead()) && (wfile.canWrite())) {
            brand_file(wfile);
          }
        }
      }
    }
  }
  
  private void brand_file(File wfile) throws MError {
    String wtext = "";
    try {
      java.io.BufferedInputStream wstream = new java.io.BufferedInputStream(new java.io.FileInputStream(wfile));
      

      byte[] b = new byte[(int)wfile.length()];
      wstream.read(b, 0, b.length);
      wstream.close();
      wtext = new String(b);
    } catch (java.io.FileNotFoundException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError("Ficheiro não encontrado");
    } catch (java.io.IOException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError("Ficheiro não encontrado");
    }
    

    try
    {
      java.io.DataOutputStream fos = new java.io.DataOutputStream(new java.io.FileOutputStream(wfile));
      fos.write(wtext.getBytes());
      fos.close();
    } catch (java.io.IOException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError("Ficheiro não encontrado");
    }
  }
  




  private String makeTextChanges(File wfile, String wtext)
  {
    int wini = wtext.indexOf("<!--FOOTER_START-->");
    int wend = wtext.indexOf("<!--FOOTER_END-->");
    if ((wend == -1) && (wini == -1))
    {
      wini = wtext.indexOf("</body");
      if (wini == -1) {
        wini = wtext.indexOf("</body");
      }
      if (wini == -1) {
        wini = wtext.indexOf("</html");
      }
      if (wini == -1) {
        wini = wtext.indexOf("</html");
      }
      wend = wini;
    }
    System.out.println("wini:" + wini + " wend:" + wend);
    if ((wend != -1) && (wini != -1)) {
      wtext = wtext.substring(0, wini) + wtext.substring(wend);
    }
    



    return wtext;
  }
}
