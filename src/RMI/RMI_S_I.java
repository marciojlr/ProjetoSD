package RMI;

import Classes.Pessoa;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI_S_I extends Remote {

    public String teste () throws RemoteException;
    public String registarPessoa(String nome, int num_eleitor, String tipo, String password, String departamento, int tel, String morada, int CC, int validade_CC) throws RemoteException;

}
