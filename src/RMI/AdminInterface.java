package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdminInterface extends Remote {

    public String teste () throws RemoteException;


}
