package RMI;

import Classes.Departamento;
import Classes.Eleicao;
import Classes.Pessoa;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public interface RMI_S_I extends Remote {

    public String teste (RMI_C_I c) throws RemoteException;
    public boolean registarPessoa(String nome, String tipo, String password, Departamento departamento, int CC, GregorianCalendar CC_val, int telemovel, String morada) throws RemoteException;
    public String criarEleicao(GregorianCalendar data_inicio, GregorianCalendar data_final, String titulo, String descricao, Departamento dept, String tipo_Pessoa) throws RemoteException;
    public ArrayList<Eleicao> getListaEleicoes() throws RemoteException;
    public void AddListaCandidata(String e, String nome) throws RemoteException;
    public void RemoveListaCandidata(String e, String nome) throws RemoteException;
    public String AlteraEleicao(String eleicao, int data_inicio,int data_fim,String titulo, String descricao) throws RemoteException;
    public ArrayList<Departamento> getListaDepartamentos() throws RemoteException;
    public void AddDepartamento(Departamento d) throws RemoteException;
    public boolean isRegisted(int CC) throws RemoteException;
    public void ping(String message) throws RemoteException;
    public boolean acceptLogin(int userCC, String name, String password) throws RemoteException;
    public ArrayList<String> getElections(int userCC, String departamento) throws RemoteException;
    public ArrayList<String> getCandidates(String election) throws  RemoteException;
    public void  AddMesaVoto(Eleicao e, Departamento d) throws RemoteException;
    public void  RemoverMesaVoto(Eleicao e, Departamento d) throws RemoteException;

    public ArrayList<String> LocalVoto(String pessoa) throws RemoteException;

    public boolean checkDepartamentExist(Departamento d) throws RemoteException;
}
