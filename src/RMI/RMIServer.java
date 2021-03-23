package RMI;

import Classes.ListaCandidata;
import Classes.Pessoa;
import Classes.Eleicao;
import Classes.Departamento;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class RMIServer extends UnicastRemoteObject implements RMI_S_I {

    ArrayList<Pessoa> listaPessoas = new ArrayList<Pessoa>();
    ArrayList<Eleicao> listaEleicoes = new ArrayList<Eleicao>();

    protected RMIServer() throws RemoteException {
        super();
    }

    public String teste (){
        System.out.println("olaaaa");

        return "olaaaaaa";
    }

    public ArrayList<Pessoa> getListaPessoas() {
        return listaPessoas;
    }

    public ArrayList<Eleicao> getListaEleicoes() {
        return listaEleicoes;
    }

    public String registarPessoa(String nome, String tipo, String password, String departamento, int CC, int dia, int mes, int ano, int telemovel, String morada){

        Pessoa p = new Pessoa(nome, tipo, password, departamento, CC, dia, mes, ano, telemovel, morada);
        System.out.println("Estou a registar");
        System.out.println(p.getNome());
        System.out.println(p.getTipo());

        listaPessoas.add(p);

        return "Registado";
    }

    public String criarEleicao(GregorianCalendar data_inicio, GregorianCalendar data_final, String titulo, String descricao, String dept, String tipo_Pessoa){

        Eleicao e = new Eleicao(data_inicio,data_final,titulo,descricao,dept,tipo_Pessoa);

        System.out.println(e.getTitulo());
        System.out.println(e.getDescricao());

        //talvez seja necessario fazer verificaçao se a eleiçao ja existe
        listaEleicoes.add(e);

        return "eleiçao criada com sucesso";

    }

    public void AddListaCandidata(String e, String nome){
        int i=0;
        Eleicao aux=null;
        for (Eleicao e2: this.listaEleicoes) {
            if(e2.getTitulo().equals(e)){
                System.out.println("Entrei aqui");
                e2.addListaCandidata(nome);
                System.out.println(e2.getListaCandidata());
                e2.printLista();
            }
            i++;
        }


    }

    public void RemoveListaCandidata(String e, String nome){
        for (Eleicao el: this.listaEleicoes ) {
            if(el.getTitulo().equals(e)){
                System.out.println("Entrei aqui");
                System.out.println(el.getListaCandidata());

                //Outra alternativa
                //el.getListaCandidata().removeIf(l -> l.getNome().equals(nome));

                el.removeListaCandidata(nome);

            }
        }

    }
    public String AlteraEleicao(String eleicao, int data_inicio,int data_fim,String titulo, String descricao){

        for(Eleicao e : listaEleicoes){
            if (e.getTitulo().equals(eleicao)){
                e.setData_inicio(null);
                e.setData_final(null);
                e.setTitulo(titulo);
                e.setDescricao(descricao);
            }
        }
        return "Propriedades Alteradas com sucesso";
    }

    public static void main(String [] args ){
        try{
            RMI_S_I admin = new RMIServer();
            LocateRegistry.createRegistry(1099).rebind("Server",admin);
            System.out.println("RMI Server ready...!");
        } catch (RemoteException e){
            System.out.println(e);
        }
    }

}
