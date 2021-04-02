import java.io.*;
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
    public static ArrayList<RMI_C_I> client= new ArrayList<>();

    protected RMIServer() throws RemoteException {
        super();
    }

    //****************************************** MÉTODOS CHAMADOS PELA CONSOLA DE ADMINISTRAÇÃO *******************************************

    public String teste (RMI_C_I c){
        client.add(c);
        System.out.println("olaaaa");
        escreveFicheiroClients();
        return "olaaaaaa";
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
        for (Eleicao e2: listaEleicoes) {
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
        for (Eleicao el: listaEleicoes ) {
            if(el.getTitulo().equals(e.getTitulo())){
                System.out.println(el.getListaCandidata());
                el.removeListaCandidata(nome);
                escreveFicheiroEleicoes();
                return;
            }
        }
    }

    public void  AddMesaVoto(Eleicao e, Departamento d) {

       for (Eleicao el: listaEleicoes) {
            if( el.getTitulo().equals(e.getTitulo()) && el.getDescricao().equals(e.getDescricao())){
                el.getDept().add(d);
                System.out.println("Mesa adicionada com sucesso");
                escreveFicheiroEleicoes();
                return;
            }
        }
    }

    public void  RemoverMesaVoto(Eleicao e, Departamento d) {

        for (Eleicao el: listaEleicoes) {
            if( el.getTitulo().equals(e.getTitulo()) && el.getDescricao().equals(e.getDescricao())){
                el.removeDepartamento(d);
                System.out.println("Mesa removida com sucesso");
                escreveFicheiroEleicoes();
                return;
            }
        }
    }

    public String AlteraEleicao(String eleicao, GregorianCalendar data_inicio ,GregorianCalendar data_fim,String titulo, String descricao){

        GregorianCalendar date = (GregorianCalendar) Calendar.getInstance();
        for(Eleicao e : listaEleicoes){
            if (e.getTitulo().equals(eleicao)){
                if(e.getData_inicio().compareTo(date) < 0){
                    return "NAO É POSSIVEL";
                }
                e.setData_inicio(data_inicio);
                e.setData_final(data_fim);
                e.setTitulo(titulo);
                e.setDescricao(descricao);
            }
        }
        escreveFicheiroEleicoes();
        return "Propriedades Alteradas com sucesso";
    }

    public boolean AddDepartamento(Departamento d){
        if(checkDepartamentExist(d)){
            listaDepartamentos.add(d);
            escreveFicheiroDepartamentos();

            System.out.println("Novo departamento adicionado ao sistema: " + listaDepartamentos);
            return true;
        }
        return false;
    }

    public boolean checkDepartamentExist(Departamento d){
        for(Departamento dept : listaDepartamentos){
            if(dept.getNome().equals(d.getNome())){
                return false;
            }
        }
        return true;

    }

    public ArrayList<Eleicao> getEleicoesPassadas(){
        ArrayList<Eleicao> listaEleicoesPassadas = new ArrayList<>();
        GregorianCalendar date = (GregorianCalendar) Calendar.getInstance();
        for (Eleicao e : listaEleicoes){
            if(e.getData_final().compareTo(date) < 0){
                listaEleicoesPassadas.add(e);
            }
        }
        return listaEleicoesPassadas;
    }

    public ArrayList<Eleicao> getEleicoesElegiveis(){
        ArrayList<Eleicao> elegieis = new ArrayList<>();
        GregorianCalendar date = (GregorianCalendar) Calendar.getInstance();
        for(Eleicao e : listaEleicoes){
           if(e.getData_inicio().compareTo(date) > 0){
               elegieis.add(e);
           }
        }
        return elegieis;
    }

    public ArrayList<Departamento> getDepartamentosElegiveis(Eleicao e){
        ArrayList<Departamento> deptsElegiveis = new ArrayList<>();
        boolean encontrou = false;
        for (Departamento d: listaDepartamentos) {
            for(Departamento d2 : e.getDept()){
                if(d2.getNome().equals(d.getNome())){
                    encontrou= true;
                }
            }
            if(encontrou == false){
                deptsElegiveis.add(d);
            }
            else{
                encontrou= false;
            }
        }
        return deptsElegiveis;
    }

    public ArrayList<String> LocalVoto(String pessoa){
        ArrayList<String> locais = new ArrayList<>();
        for (Eleicao e: listaEleicoes) {
            for(Pessoa p: e.getVotantes()){
                if(p.getNome().equals(pessoa)){
                    String s = "Eleiçao: " + e.getTitulo() + "  " + "Local: " + p.getLocalVoto();
                    locais.add(s);
                }
            }
        }
        return locais;
    }


    //******************************************** METODOS CHAMADOS PELO SERVIDOR MULTICAST **************************************************

    public void sendNotification(String message, int priority){
        //ENVIAR MENSAGEM AOS CLIENTES
        System.out.println(client);
        ArrayList<Integer> indices = new ArrayList<>();
        int i = 0;
        for(RMI_C_I c : client){
            try {
                c.notification(message, priority);
            } catch (RemoteException e) {
                indices.add(i);
                System.out.println("Notificaçao nao enviada!");

            }
            i++;
        }
        for (int j = indices.size()-1 ; j >= 0 ; j--){
            client.remove(client.get(indices.get(j)));
        }
    }

    public boolean isRegistered(int CC, String department){

        for(Pessoa pessoa : listaPessoas){
            if(pessoa.getCC() == CC){
                String message = "Eleitor com o nome " + pessoa.getNome() + " e CC " + CC + ", chegou à mesa " + department + ".";
                sendNotification(message,0);
                return true;
            }
        }
        return false;
    }

    public void ping(String dept){
        for (Departamento d: listaDepartamentos) {
            if(d.getNome().equals(dept)){
                d.setMesaOn(true);
                break;
            }
        }
        String message = "A mesa " + dept + " encontra-se em funcionamento!";
        sendNotification(message,0);
    }

    public void crash(String department){
        for (Departamento d: listaDepartamentos) {
            if(d.getNome().equals(department)){
                d.setMesaOn(false);
                break;
            }
        }
        String message = "(!) A mesa " + department + " foi abaixo!";
        sendNotification(message,1);
    }

    public boolean acceptLogin(int userCC, String name, String password){
        for(Pessoa pessoa : listaPessoas){
            if(pessoa.getCC() == userCC && pessoa.getNome().equals(name) && pessoa.getPassword().equals(password)){
                String message = "Eleitor com o nome " + name + " e CC " + userCC + ", efetuou login num terminal.";
                sendNotification(message,0);
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getElections(int userCC, String departamento){
        Pessoa eleitor = null;
        ArrayList<String> eleicoes = new ArrayList<>();

        //ENCONTRAR ELEITOR
        for(Pessoa p : listaPessoas){
            if(p.getCC() == userCC){
                eleitor = p;
            }
        }
        GregorianCalendar date = (GregorianCalendar) Calendar.getInstance();
        //ENCONTRAR ELEICOES COMUNS HA MESA E ELEITORES
        for(Eleicao e : listaEleicoes){
            boolean voted = false;
            if(e.getTipo_Pessoa().equals(eleitor.getTipo()) && e.getData_inicio().compareTo(date) < 0 && e.getData_final().compareTo(date) > 0){
                for(Pessoa p : e.getVotantes()){
                    if(p.getCC() == userCC){
                        voted = true;
                        break;
                    }
                }
                for(Departamento d : e.getDept()){
                    if(d.getNome().equals(departamento) && voted == false){
                        eleicoes.add(e.getTitulo());
                        break;
                    }
                }
            }
        }
        return eleicoes;
    }

    public ArrayList<String> getCandidates(String election){

        ArrayList<String> listas = new ArrayList<String>();

        //ENCONTRAR A ELEIÇÃO
        for(Eleicao e : listaEleicoes){
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
        for(Eleicao e : listaEleicoes){
            if(e.getTitulo().equals(election)){

                //SE FOR UMA OPCAOO INVALIDA CONSIDERA VOTO NULO
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
        Pessoa eleitor = null;
        Pessoa pessoa = null;
        //PROCURAR PESSOA
        for(Pessoa p : listaPessoas){
            if(userCC == p.getCC()){
                pessoa = p;
                break;
            }
        }
        //FAZER COPIA DOS DADOS
        if(pessoa != null){
            eleitor = new Pessoa(pessoa.getNome(), userCC, department);
        }
        //ADICIONAR VOTANTE
        for(Eleicao e : listaEleicoes){
            if(e.getTitulo().equals(election)){
                e.getVotantes().add(eleitor);
                break;
            }
        }
        String message = "Eleitor com o nome " + pessoa.getNome() + " e CC " + userCC + ", efetuou o voto na mesa " + department;
        sendNotification(message,0);
    }


    //******************************************** METODOS DE LEITURA E ESCRITA DE OBJETOS **************************************************

    public  void escreveFicheiroPessoas(){

        File f = new File("Pessoas.obj");

        try{
            FileOutputStream os = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(os);

            oos.writeObject(listaPessoas);

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

            oos.writeObject(listaEleicoes);

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

            oos.writeObject(listaDepartamentos);

            oos.close();

        }catch(FileNotFoundException e){
            System.out.println("Erro a criar ficheiro");
        }
        catch(IOException e){
            System.out.println("Erro a escrever para ficheiro");
        }
    }
    public void escreveFicheiroClients(){

        File f = new File("Clients.obj");

        try{
            FileOutputStream os = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(os);

            oos.writeObject(client);

            oos.close();

        }catch(FileNotFoundException e){
            System.out.println("Erro a criar ficheiro");
        }
        catch(IOException e){
            System.out.println("Erro a escrever para ficheiro");
        }
    }

    public static void lerFicheiroClients(){
        File f = new File("Clients.obj");
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            client = (ArrayList<RMI_C_I>) ois.readObject();
            ois.close();

        }catch (FileNotFoundException e){
            System.out.println("Erro ao abrir o ficheiro");
            listaPessoas = new ArrayList<>();
        } catch(IOException e){
            System.out.println("Erro a escrever para ficheiro");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public  static void leFicheiroPessoas() throws ClassNotFoundException {

        File f = new File("Pessoas.obj");
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            listaPessoas = (ArrayList<Pessoa>) ois.readObject();
            ois.close();
        }catch (FileNotFoundException e){
            System.out.println("Erro ao abrir o ficheiro");
            listaPessoas = new ArrayList<>();
        } catch(IOException e){
            System.out.println("Erro a escrever para ficheiro");
        }

    }
    public  static void leFicheiroEleicoes() throws ClassNotFoundException {

        File f = new File("Eleicoes.obj");
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            listaEleicoes= (ArrayList<Eleicao>) ois.readObject();
            ois.close();
        }catch (FileNotFoundException e){
            System.out.println("Erro ao abrir o ficheiro");
            listaEleicoes = new ArrayList<>();
        } catch(IOException e){
            System.out.println("Erro a escrever para ficheiro");
        }

    }
    public  static void leFicheiroDepartamentos() throws ClassNotFoundException {

        File f = new File("Departamentos.obj");
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            listaDepartamentos = (ArrayList<Departamento>) ois.readObject();
            ois.close();
        }catch (FileNotFoundException e){
            System.out.println("Erro ao abrir o ficheiro");
            listaDepartamentos = new ArrayList<>();
        } catch(IOException e){
            System.out.println("Erro a escrever para ficheiro");
        }

    }

    public static void main(String [] args ) throws IOException, ClassNotFoundException {

        boolean failed = true;
        RMI_S_I server = new RMIServer();
        leFicheiroPessoas();
        leFicheiroDepartamentos();
        leFicheiroEleicoes();
        lerFicheiroClients();

        try {
            LocateRegistry.createRegistry(1099).rebind("Server",server);
            System.out.println("RMI Server ready...!");
        } catch (RemoteException exception){
            System.out.println("O server principal já se encontra ligado");

            while(failed){
                try {
                    Thread.sleep(5000);
                    System.out.println("A tentar de novo");
                    LocateRegistry.createRegistry(1099).rebind("Server",server);
                    System.out.println("Server Secundario is Ready");
                    failed = false;
                    leFicheiroPessoas();
                    leFicheiroEleicoes();
                    leFicheiroDepartamentos();
                    lerFicheiroClients();

                } catch (InterruptedException | RemoteException exception2) {
                    System.out.println("Server principal em funcionamento");
                }
            }
        }
    }

}
