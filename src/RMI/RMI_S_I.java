package RMI;

import Classes.Eleicao;
import Classes.Pessoa;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RMI_S_I extends Remote {

    public String teste () throws RemoteException;
    public String registarPessoa(String nome, int num_eleitor, String tipo, String password, String departamento, int tel, String morada, int CC, int validade_CC) throws RemoteException;
    public String criarEleicao(int data_inicio, int data_final, String titulo, String descricao, String dept, String tipo_Pessoa) throws RemoteException;
    public ArrayList<Eleicao> getListaEleicoes() throws RemoteException;

    public void AddListaCandidata(String e, String nome) throws RemoteException;

    public void RemoveListaCandidata(String e, String nome) throws RemoteException;

}
