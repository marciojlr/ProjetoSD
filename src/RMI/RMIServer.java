package RMI;

import Classes.Eleicao;
import Classes.Pessoa;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

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

    public String registarPessoa(String nome, int num_eleitor, String tipo, String password, String departamento, int tel, String morada, int CC, int validade_CC){

        Pessoa p = new Pessoa(nome,num_eleitor,tipo,password,departamento,tel,morada,CC,validade_CC);
        System.out.println("Estou a registar");
        System.out.println(p.getNome());
        System.out.println(p.getNum_eleitor());
        System.out.println(p.getTipo());

        listaPessoas.add(p);

        return "Registado";
    }

    public String criarEleicao(int data_inicio, int data_final, String titulo, String descricao, String dept, String tipo_Pessoa){

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
        for (Eleicao el: this.listaEleicoes
             ) {
            if(el.getTitulo().equals(e)){
                el.getListaCandidata().remove(el);
                return;
            }
        }

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
