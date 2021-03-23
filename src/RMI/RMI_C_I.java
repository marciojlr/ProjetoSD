package RMI;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI_C_I extends Remote {

    public void newServer() throws RemoteException, NotBoundException, MalformedURLException;
}
