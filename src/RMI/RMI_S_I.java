package RMI;

import Classes.Eleicao;
import Classes.Pessoa;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public interface RMI_S_I extends Remote {

    public String teste (RMI_C_I c) throws RemoteException;
    public String ping () throws RemoteException;
    public String registarPessoa(String nome, String tipo, String password, String departamento, int CC, GregorianCalendar CC_val, int telemovel, String morada) throws RemoteException;
    public String criarEleicao(GregorianCalendar data_inicio, GregorianCalendar data_final, String titulo, String descricao, String dept, String tipo_Pessoa) throws RemoteException;
    public ArrayList<Eleicao> getListaEleicoes() throws RemoteException;

    public void AddListaCandidata(String e, String nome) throws RemoteException;

    public void RemoveListaCandidata(String e, String nome) throws RemoteException;

    public String AlteraEleicao(String eleicao, int data_inicio,int data_fim,String titulo, String descricao) throws RemoteException;

}
