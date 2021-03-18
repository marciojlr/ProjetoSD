package RMI;

import Classes.Pessoa;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdminInterface extends Remote {

    public String teste () throws RemoteException;
    public String registarPessoa(Pessoa p) throws RemoteException;

}
