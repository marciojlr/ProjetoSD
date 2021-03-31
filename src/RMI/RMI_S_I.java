package RMI;

import Classes.Departamento;
import Classes.Eleicao;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public interface RMI_S_I extends Remote {

    //MÉTODOS CHAMADOS PELA CONSOLA DE ADMINISTRAÇÃO
    String teste (RMI_C_I c) throws RemoteException;
    boolean registarPessoa(String nome, String tipo, String password, Departamento departamento, int CC, GregorianCalendar CC_val, int telemovel, String morada) throws RemoteException;
    String criarEleicao(GregorianCalendar data_inicio, GregorianCalendar data_final, String titulo, String descricao, Departamento dept, String tipo_Pessoa) throws RemoteException;
    ArrayList<Eleicao> getListaEleicoes() throws RemoteException;
    void AddListaCandidata(Eleicao e, String nome) throws RemoteException;
    void RemoveListaCandidata(Eleicao e, String nome) throws RemoteException;
    String AlteraEleicao(String eleicao, int data_inicio,int data_fim,String titulo, String descricao) throws RemoteException;
    ArrayList<Departamento> getListaDepartamentos() throws RemoteException;
    void AddDepartamento(Departamento d) throws RemoteException;
    void  RemoverMesaVoto(Eleicao e, Departamento d) throws RemoteException;
    void  AddMesaVoto(Eleicao e, Departamento d) throws RemoteException;
    ArrayList<String> LocalVoto(String pessoa) throws RemoteException;
    boolean checkDepartamentExist(Departamento d) throws RemoteException;
    ArrayList<Eleicao> getEleicoesPassadas() throws RemoteException;

    //MÉTODOS CHAMADAS PELO SERVIDOR MULTICAST
    boolean isRegistered(int CC) throws RemoteException;
    void ping(String dept) throws RemoteException;
    boolean acceptLogin(int userCC, String name, String password) throws RemoteException;
    ArrayList<String> getElections(int userCC, String departamento) throws RemoteException;
    ArrayList<String> getCandidates(String election) throws  RemoteException;
    void vote(String election, String option) throws RemoteException;
    //TODO: IMPLEMENTAR MÉTODO DE ADICIONAR VOTANTE
    void addElector(String election, int userCC, String department) throws RemoteException;
}
