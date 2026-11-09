package PT.Minimal.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public abstract interface HelloInterface
  extends Remote
{
  public abstract String say()
    throws RemoteException;
}
