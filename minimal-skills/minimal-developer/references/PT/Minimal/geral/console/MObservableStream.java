package PT.Minimal.geral.console;

import java.io.ByteArrayOutputStream;
import java.util.Vector;






public class MObservableStream
  extends ByteArrayOutputStream
{
  public MObservableStream() {}
  
  Vector streamObservers = new Vector();
  
  void addStreamObserver(MStreamObserver o) {
    streamObservers.addElement(o);
  }
  
  void removeStreamObserver(MStreamObserver o) {
    streamObservers.removeElement(o);
  }
  
  void notifyObservers() {
    for (int i = 0; i < streamObservers.size(); i++) {
      ((MStreamObserver)streamObservers.elementAt(i)).streamChanged();
    }
  }
  
  public void write(byte[] b, int off, int len) {
    super.write(b, off, len);
    notifyObservers();
  }
}
