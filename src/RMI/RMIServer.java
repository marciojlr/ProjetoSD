package RMI;

import Classes.ListaCandidata;
import Classes.Pessoa;
import Classes.Eleicao;
import Classes.Departamento;
import Multicast.MulticastServer;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class RMIServer extends UnicastRemoteObject implements RMI_S_I {

    public static  ArrayList<Pessoa> listaPessoas;
    public static ArrayList<Eleicao> listaEleicoes;
    public static ArrayList<Departamento> listaDepartamentos;
    static RMI_C_I client;


    protected RMIServer() throws RemoteException {
        super();
    }

    //LEITURA E ESCRITA DE FICHEIROS OBJETO
    public  void escreveFicheiroPessoas(){

        File f = new File("Pessoas.obj");

        try{
            FileOutputStream os = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(os);

            oos.writeObject(this.listaPessoas);

            oos.close();

        }catch(FileNotFoundException e){
            System.out.println("Erro a criar ficheiro");
        }
        catch(IOException e){
            System.out.println("Erro a escrever para ficheiro");
        }
    }
    public void escreveFicheiroEleicoes(){

        File f = new File("Eleicoes.obj");

        try{
            FileOutputStream os = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(os);

            oos.writeObject(this.listaEleicoes);

            oos.close();

        }catch(FileNotFoundException e){
            System.out.println("Erro a criar ficheiro");
        }
        catch(IOException e){
            System.out.println("Erro a escrever para ficheiro");
        }
    }
    public void escreveFicheiroDepartamentos(){

        File f = new File("Departamentos.obj");

        try{
            FileOutputStream os = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(os);

            oos.writeObject(this.listaDepartamentos);

            oos.close();

        }catch(FileNotFoundException e){
            System.out.println("Erro a criar ficheiro");
        }
        catch(IOException e){
            System.out.println("Erro a escrever para ficheiro");
        }
    }


    public  static void leFicheiroPessoas() throws IOException, ClassNotFoundException {

        File f = new File("Pessoas.obj");
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            listaPessoas = (ArrayList<Pessoa>) ois.readObject();
            ois.close();
        }catch (FileNotFoundException e){
            System.out.println("Erro ao abrir o ficheiro");
            listaPessoas = new ArrayList<Pessoa>();
        } catch(IOException e){
            System.out.println("Erro a escrever para ficheiro");
        }

    }

    public  static void leFicheiroEleicoes() throws IOException, ClassNotFoundException {

        File f = new File("Eleicoes.obj");
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

             listaEleicoes= (ArrayList<Eleicao>) ois.readObject();
            ois.close();
        }catch (FileNotFoundException e){
            System.out.println("Erro ao abrir o ficheiro");
            listaEleicoes = new ArrayList<Eleicao>();
        } catch(IOException e){
            System.out.println("Erro a escrever para ficheiro");
        }

    }

    public  static void leFicheiroDepartamentos() throws IOException, ClassNotFoundException {

        File f = new File("Departamentos.obj");
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            listaDepartamentos = (ArrayList<Departamento>) ois.readObject();
            ois.close();
        }catch (FileNotFoundException e){
            System.out.println("Erro ao abrir o ficheiro");
            listaDepartamentos = new ArrayList<Departamento>();
        } catch(IOException e){
            System.out.println("Erro a escrever para ficheiro");
        }

    }


    //MÉTODOS CHAMADOS PELA CONSOLA DE ADMINISTRAÇÃO
    public String teste (RMI_C_I c){
        client = c;
        System.out.println("olaaaa");

        return "olaaaaaa";
    }

    public ArrayList<Pessoa> getListaPessoas() {
        return listaPessoas;
    }

    public ArrayList<Eleicao> getListaEleicoes() {
        return listaEleicoes;
    }

    public ArrayList<Departamento> getListaDepartamentos() {
        return listaDepartamentos;
    }

    public boolean registarPessoa(String nome, String tipo, String password, Departamento departamento, int CC, GregorianCalendar CC_val, int telemovel, String morada){
        Pessoa p = new Pessoa(nome, tipo, password, departamento, CC, CC_val, telemovel, morada);
        System.out.println(p);
        listaPessoas.add(p);
        escreveFicheiroPessoas();

        return true;
    }

    public String criarEleicao(GregorianCalendar data_inicio, GregorianCalendar data_final, String titulo, String descricao, Departamento dept, String tipo_Pessoa){

        Eleicao e = new Eleicao(data_inicio,data_final,titulo,descricao,dept,tipo_Pessoa);
        System.out.println(e);

        //talvez seja necessario fazer verificaçao se a eleiçao ja existe
        listaEleicoes.add(e);
        escreveFicheiroEleicoes();
        return "\nELEIÇÃO CRIADA COM SUCESSO\n";

    }

    public void AddListaCandidata(Eleicao e, String nome){
        int i=0;
        Eleicao aux=null;
        for (Eleicao e2: this.listaEleicoes) {
            if(e2.getTitulo().equals(e.getTitulo())){
                System.out.println("Entrei aqui");
                e2.addListaCandidata(nome);
                System.out.println(e2.getListaCandidata());
                e2.printLista();
            }
            i++;
        }
        escreveFicheiroEleicoes();


    }

    public void RemoveListaCandidata(Eleicao e, String nome){
        for (Eleicao el: this.listaEleicoes ) {
            if(el.getTitulo().equals(e.getTitulo())){
                System.out.println("Entrei aqui");
                System.out.println(el.getListaCandidata());

                //Outra alternativa
                //el.getListaCandidata().removeIf(l -> l.getNome().equals(nome));

                el.removeListaCandidata(nome);

            }
        }
        escreveFicheiroEleicoes();

    }

    public void  AddMesaVoto(Eleicao e, Departamento d) {
        //TODO Multicast receber o departamento
        //MulticastServer s = new MulticastServer(d);
        //listaMesasVoto.add(s);


       for (Eleicao el: this.listaEleicoes
             ) {
            if( el.getTitulo().equals(e.getTitulo()) && el.getDescricao().equals(e.getDescricao())){
                el.getDept().add(d);
            }
        }
        for (Eleicao el: this.listaEleicoes
        ) {
            if( el.getTitulo().equals(e.getTitulo()) && el.getDescricao().equals(e.getDescricao())){
                System.out.println(el.toString());
            }
        }

        System.out.println("Mesa adicionada com sucesso");

        escreveFicheiroEleicoes();
    }


    public void  RemoverMesaVoto(Eleicao e, Departamento d) {

        //TODO Multicast receber o departamento
        //MulticastServer s = new MulticastServer(d);
        //listaMesasVoto.remove(s);
        for (Eleicao el: this.listaEleicoes
        ) {
            if( el.getTitulo().equals(e.getTitulo()) && el.getDescricao().equals(e.getDescricao())){
                el.removeDepartamento(d);
            }
        }

        System.out.println("Mesa removida com sucesso");
        for (Eleicao el: this.listaEleicoes
        ) {
            if( el.getTitulo().equals(e.getTitulo()) && el.getDescricao().equals(e.getDescricao())){
                System.out.println(el.toString());
            }
        }

        escreveFicheiroEleicoes();
    }

    public String AlteraEleicao(String eleicao, int data_inicio,int data_fim,String titulo, String descricao){

        GregorianCalendar date = (GregorianCalendar) Calendar.getInstance();
        for(Eleicao e : listaEleicoes){
            if (e.getTitulo().equals(eleicao)){
                if(e.getData_inicio().compareTo(date) < 0){
                    return "NAO É POSSIVEL";
                }
                e.setData_inicio(null);
                e.setData_final(null);
                e.setTitulo(titulo);
                e.setDescricao(descricao);
            }
        }
        escreveFicheiroEleicoes();
        return "Propriedades Alteradas com sucesso";
    }

    public void AddDepartamento(Departamento d){
        System.out.println("Novo departamento adicionado: " + d);
        listaDepartamentos.add(d);
        escreveFicheiroDepartamentos();
    }

    public boolean checkDepartamentExist(Departamento d){

        for(Departamento dept : this.listaDepartamentos){
            if(dept.getNome().equals(d.getNome())){
                return false;
            }
        }
        return true;

    }

    //TODO: TESTAR QUANDO A PARTE DOS LOCAIS DE VOTOS ESTIVER FEITA
    public ArrayList<String> LocalVoto(String pessoa){
        ArrayList<String> locais = new ArrayList<>();
        for (Eleicao e: this.listaEleicoes) {
            for(Pessoa p: e.getVotantes()){
                if(p.getNome().equals(pessoa)){
                    String s = "Eleiçao: " + e.getTitulo() + "  " + "Local: " + p.getLocalVoto();
                    locais.add(s);
                }
            }
        }
        return locais;
    }

    //MÉTODOS CHAMADOS PELO SERVIDOR MULTICAST
    public boolean isRegistered(int CC){

        for(Pessoa pessoa : this.listaPessoas){
            if(pessoa.getCC() == CC){
                return true;
            }
        }
        return false;
    }

    public void ping(String message){
        System.out.println(message);
    }

    public boolean acceptLogin(int userCC, String name, String password){
        for(Pessoa pessoa : listaPessoas){
            if(pessoa.getCC() == userCC && pessoa.getNome().equals(name) && pessoa.getPassword().equals(password)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getElections(int userCC, String departamento){
        Pessoa eleitor = null;
        ArrayList<String> eleicoes = new ArrayList<String>();

        //ENCONTRAR ELEITOR
        for(Pessoa p : this.listaPessoas){
            if(p.getCC() == userCC){
                eleitor = p;
            }
        }

        //ENCONTRAR ELEIÇÕES COMUNS HÁ MESA E ELEITORES
        for(Eleicao e : this.listaEleicoes){
            if(e.getTipo_Pessoa().equals(eleitor.getTipo())){
                for(Departamento d : e.getDept()){
                    if(d.getNome().equals(departamento)){
                        eleicoes.add(e.getTitulo());
                    }
                }
            }
        }
        return eleicoes;
    }

    public ArrayList<String> getCandidates(String election){

        ArrayList<String> listas = new ArrayList<String>();

        //ENCONTRAR A ELEIÇÃO
        for(Eleicao e : this.listaEleicoes){
            if(e.getTitulo().equals(election)){
                //RETIRA AS LISTAS CANDIDATAS
                for(ListaCandidata lista : e.getListaCandidata()){
                    listas.add(lista.getNome());
                }
                return listas;
            }
        }
        return listas;
    }

    public void vote(String election, String option){
        int vote;
        try{
            vote = Integer.parseInt(option);
        }catch(Exception e){
            vote = -1;
        }
        for(Eleicao e : this.listaEleicoes){
            if(e.getTitulo().equals(election)){

                //SE FOR UMA OPÇÃO INVÁLIDA CONSIDERA VOTO NULO
                if( vote > e.getListaCandidata().size() || vote < 0 ){
                    e.addVotoNulo();
                    System.out.println("Voto Nulo");

                } // SE A OPÇÃO FOR IGUAL AO TAMANHO DO ARRAY, ESCOLHEU A OPÇÃO DE VOTO EM BRANCO
                else if( vote == e.getListaCandidata().size() ){
                    e.addVotoBranco();
                    System.out.println("Voto em branco");
                    e.addTotalVotos();
                }

                else{
                    e.getListaCandidata().get(vote).addVote();
                    System.out.println(e.getListaCandidata().get(vote));
                    e.addTotalVotos();
                }

                escreveFicheiroEleicoes();
                return;
            }
        }
    }

    public void addElector(String election, int userCC, String department){
        //todo: ter em atenção que aqui se mudarmos um, mudamos todos porque não é copia
    }

    public static void main(String [] args ) throws IOException, ClassNotFoundException {

        boolean failed = true;
        RMI_S_I server = new RMIServer();
        leFicheiroPessoas();
        leFicheiroDepartamentos();
        leFicheiroEleicoes();


        try
        {
            LocateRegistry.createRegistry(1099).rebind("Server",server);
            System.out.println("RMI Server ready...!");
        }
        catch (RemoteException exception){
            System.out.println(exception);
            System.out.println("O server principal já se encontra ligado");

            while(failed){
                try
                {
                    Thread.sleep(5000);
                    System.out.println("A tentar de novo");
                    LocateRegistry.createRegistry(1099).rebind("Server",server);
                    System.out.println("Server Secundario is Ready");
                    failed = false;
                    leFicheiroPessoas();
                    leFicheiroEleicoes();
                    leFicheiroDepartamentos();
                }
                catch (InterruptedException | RemoteException exception2) {
                    System.out.println(exception2);
                }
            }
        }
    }

}
