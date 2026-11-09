package PT.Minimal.geral;

import java.io.PrintStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MMail
{
  static final String sMsgCharset = "iso-8859-1";
  
  public MMail() {}
  
  boolean temAttachment = false;
  Hashtable hMailProperties = new Hashtable();
  Vector vAttach = new Vector();
  
  public void setTo(String to) {
    hMailProperties.put("To", to);
  }
  
  public void setFrom(String from) {
    hMailProperties.put("From", from);
  }
  
  public void setMailServer(String server) {
    hMailProperties.put("Server", server);
  }
  
  public void setSubject(String subject) {
    hMailProperties.put("Subject", subject);
  }
  
  public void setText(String text) {
    hMailProperties.put("Text", text);
  }
  
  public void setAttachments(Vector v) {
    if (v != null) {
      vAttach = v;
      temAttachment = true;
    }
  }
  
  public void sendMail()
  {
    Properties props = System.getProperties();
    props.put("mail.smtp.host", hMailProperties.get("Server"));
    
    Session session = Session.getDefaultInstance(props, null);
    session.setDebug(false);
    try
    {
      MimeMessage msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress((String)hMailProperties.get("From")));
      InternetAddress[] address = { new InternetAddress((String)hMailProperties.get("To")) };
      msg.setRecipients(javax.mail.Message.RecipientType.TO, address);
      msg.setSubject((String)hMailProperties.get("Subject"));
      
      MimeBodyPart mbp1 = new MimeBodyPart();
      String msgText1 = (String)hMailProperties.get("Text");
      
      mbp1.setText(msgText1, "iso-8859-1");
      
      Multipart mp = new MimeMultipart();
      mp.addBodyPart(mbp1);
      
      if (temAttachment)
      {
        for (int i = 0; i < vAttach.size(); i++)
        {
          String path = (String)vAttach.get(i);
          MimeBodyPart mbp2 = new MimeBodyPart();
          FileDataSource fds = new FileDataSource(path);
          mbp2.setDataHandler(new javax.activation.DataHandler(fds));
          int ipos = 0;
          for (int j = path.length() - 1; path.charAt(j) == '\\'; j--) {
            System.out.println("CHAR: " + path.charAt(j));
            ipos = j;
          }
          mbp2.setFileName(path.substring(ipos, path.length()));
          mp.addBodyPart(mbp2);
        }
      }
      
      msg.setContent(mp);
      msg.setSentDate(new Date());
      javax.mail.Transport.send(msg);
    }
    catch (MessagingException mex) {
      mex.printStackTrace();
      Exception ex = null;
      if ((ex = mex.getNextException()) != null) {
        ex.printStackTrace();
      }
    }
  }
}
