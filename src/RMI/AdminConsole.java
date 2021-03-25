package RMI;

import Classes.Departamento;
import Classes.Eleicao;
import Classes.Pessoa;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

// Script de Cliente diretamente ligado ao Server
public class AdminConsole extends UnicastRemoteObject implements RMI_C_I {

    private static RMI_S_I adminConsole;


    public AdminConsole() throws RemoteException {super();}

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException{
        adminConsole = (RMI_S_I) Naming.lookup("Server");
        RMI_C_I client = new AdminConsole();
        String teste;
        teste = adminConsole.teste((RMI_C_I) client);
        System.out.println(teste);
        menu();


    }

    public static void menu(){
        Scanner myObj = new Scanner(System.in);
        String option;

        while (true){
            System.out.println("======Bem vindo!======");
            System.out.println("1. Registar");
            System.out.println("2. Criar Eleição");
            System.out.println("3. Gerir Listas de candidatos a uma eleição");
            System.out.println("4. Mesas de Voto");
            System.out.println("5. Alterar propriedades de eleição");
            System.out.println("> ");
            option= myObj.nextLine();
            if(option.equals("1")){
                try{
                    RegistoPessoa();
                }catch (RemoteException e){
                    System.out.println(e);
                }

            }
            else if(option.equals("2")){
                try {
                    criaEleicao();
                }catch (RemoteException e){
                    System.out.println(e);
                }
            }
            else if(option.equals("3")){
                try {
                    gerirListaCandidata();
                }catch (RemoteException e){
                    System.out.println(e);
                }
            }
            else if(option.equals("5")){
                try {
                    AlteraPropriedadesEleicao();
                }catch (RemoteException e){
                    System.out.println(e);
                }
            }
        }
    }


    public static void  RegistoPessoa() throws RemoteException {

        Scanner s = new Scanner(System.in);

        System.out.print("Nome: ");
        String nome = s.nextLine();

        String tipo;
        while(true){
            System.out.print("1. Docente\n2. Estudante\n3. Funcionário\n");
            int aux = Integer.parseInt(s.nextLine());
            if(aux == 1){
                tipo = "Docente";
                break;
            }
            else if(aux == 2){
                tipo = "Estudante";
                break;
            }
            else if(aux == 3){
                tipo = "Funcionario";
                break;
            }
        }

        System.out.print("Password: ");
        String password = s.nextLine();
        //System.out.print("Departamento: ");

        Departamento departamento = escolherDept();
        if(departamento == null){

            departamento = criaDepartamento();
            adminConsole.AddDepartamento(departamento);
        }

        System.out.print("CC: ");
        int CC=Integer.parseInt(s.nextLine());
        System.out.println("Validade CC");
        System.out.print("Dia: ");
        int dia = Integer.parseInt(s.nextLine());
        System.out.print("Mês: ");
        int mes = Integer.parseInt(s.nextLine());
        System.out.print("Ano: ");
        int ano = Integer.parseInt(s.nextLine());

        GregorianCalendar CC_val = new GregorianCalendar(dia,mes,ano);
        System.out.print("Telemovel: ");
        int telemovel =Integer.parseInt(s.nextLine());

        System.out.print("Morada: ");
        String morada = s.nextLine();

        //chamar funçao registar do server
        try{
            String r;
            r = adminConsole.registarPessoa(nome, tipo, password, departamento, CC, CC_val, telemovel, morada);
            System.out.println(r);
        }catch (RemoteException e){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    adminConsole.registarPessoa(nome, tipo, password, departamento, CC, CC_val, telemovel, morada);
                    break;
                }catch(NotBoundException  | RemoteException | MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }
    }

    public static void  criaEleicao() throws RemoteException {
        //Recolher informaçao
        Scanner s = new Scanner(System.in);

        System.out.println("Data de inicio: ");
        System.out.print("Dia: ");
        int dia = Integer.parseInt(s.nextLine());
        System.out.print("Mês: ");
        int mes = Integer.parseInt(s.nextLine());
        System.out.print("Ano: ");
        int ano = Integer.parseInt(s.nextLine());

        GregorianCalendar data_inicio = new GregorianCalendar(dia,mes,ano);

        System.out.println("Data de final: ");
        System.out.print("Dia: ");
        int dia2 = Integer.parseInt(s.nextLine());
        System.out.print("Mês: ");
        int mes2 = Integer.parseInt(s.nextLine());
        System.out.print("Ano: ");
        int ano2 = Integer.parseInt(s.nextLine());

        GregorianCalendar data_fim = new GregorianCalendar(dia2,mes2,ano2);

        System.out.println("Titulo: ");
        String titulo= s.nextLine();

        System.out.println("Descrição: ");
        String descricao= s.nextLine();

        //TODO: alterar para a classe departamento
        System.out.println("Departamento: ");
        Departamento departamento = escolherDept();
        if(departamento == null){
            System.out.println("Nao existem departamentos, não é possivel criar eleiçao");
            return;
        }

        //So podem votar pessoas deste tipo
        System.out.println("Grupo de pessoas que pode votar");
        String tipo_Pessoa;
        while(true){
            System.out.print("1. Docente\n2. Estudante\n3. Funcionário\n");
            int aux = Integer.parseInt(s.nextLine());
            if(aux == 1){
                tipo_Pessoa = "Docente";
                break;
            }
            else if(aux == 2){
                tipo_Pessoa = "Estudante";
                break;
            }
            else if(aux == 3){
                tipo_Pessoa = "Funcionario";
                break;
            }
        }
        try{
            String r;
            r = adminConsole.criarEleicao(data_inicio, data_fim , titulo, descricao, departamento, tipo_Pessoa);
            System.out.println(r);
        }catch (RemoteException e){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    adminConsole.criarEleicao(data_inicio, data_fim, titulo, descricao, departamento, tipo_Pessoa);
                    break;
                }catch(NotBoundException  | RemoteException | MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }
    }
    /*
    * Temos eleiçoes de conselho geral ?
    *
     */

    public static void gerirListaCandidata() throws RemoteException {
        try{
            ArrayList<Eleicao> eleicoes = adminConsole.getListaEleicoes();

            if(eleicoes.isEmpty()){
                System.out.println("Nao ha nenhuma eleiçao a decorrer");
                return;
            }
            for( Eleicao e : eleicoes){
                //verificar q nao acabou
                System.out.println(e.getTitulo());
            }
            Scanner s = new Scanner(System.in);
            System.out.println("Insira a eleiçao que pretende: ");
            //fazer uma defesa para verificar se é valida ou entao obrigar a escolher um indice
            String eleicao = s.nextLine();
            System.out.println("1-Adicionar  2-Remover");
            String opcao= s.nextLine();
            if(opcao.equals("1")){
                System.out.println("Insira Lista Candidata que deseja inserir: ");
                String nome = s.nextLine();
                adminConsole.AddListaCandidata(eleicao,nome);
            }
            else if(opcao.equals("2")){
                //Display das listas Candidatas;
                //eleicoes = adminConsole.getListaEleicoes();
                for (Eleicao e2:eleicoes
                     ) {
                    if(e2.getTitulo().equals(eleicao)){
                        System.out.println(e2.getListaCandidata());
                        e2.printLista();
                    }
                }
                System.out.println("Insira a Lista Candidata que deseja remover:");
                String nome = s.nextLine();
                adminConsole.RemoveListaCandidata(eleicao,nome);
            }
        }catch (RemoteException e){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    break;
                }catch(NotBoundException  | RemoteException |MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }
    }


    public static void AlteraPropriedadesEleicao() throws RemoteException{
        try{
            ArrayList<Eleicao> eleicoes = adminConsole.getListaEleicoes();
            //TODO: DAR PRINT APENAS DAS ELEGIVEIS
            for (Eleicao e: eleicoes) {
                System.out.println(e.getTitulo());
            }
            Scanner s = new Scanner(System.in);
            System.out.println("Selecione a Eleição que deseja alterar");
            String escolha = s.nextLine();

            System.out.println("Data de inicio: ");
            int data_inicio = Integer.parseInt(s.nextLine());

            System.out.println("Data de final: ");
            int data_final = Integer.parseInt(s.nextLine());

            System.out.println("Titulo: ");
            String titulo= s.nextLine();

            System.out.println("Descrição: ");
            String descricao= s.nextLine();

            adminConsole.AlteraEleicao(escolha,data_inicio,data_final, titulo,descricao);

        }catch(RemoteException e){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    break;
                }catch(NotBoundException  | RemoteException |MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }
    }

    public void CriaMesaVoto(){
        Scanner s = new Scanner(System.in);
        System.out.println("Em que Departamento pretende criar a Mesa de Voto");
        //Dar display dos depts
        Departamento dep = escolherDept();
        //verificar se o departamento ja tem uma mesa
        if(dep.getNome().equals("")){
            System.out.println("O ja tem mesa de voto");
        }
        //se nao tiver crio a mesa e adiciono ao array de mesas

    }

    public void AssociarMesaVoto(){

        //Dar display das eleicoes

        //Escolher a eleiçao que desaja associar uma mesa

        //Escolher mesa

        //Ver é possivel adicionar mesa a eleiçao
    }

    public void ApagarMesaVoto(){

    }

    public static Departamento escolherDept(){
        try{
            ArrayList<Departamento> depts = adminConsole.getListaDepartamentos();
            if( depts.isEmpty()){
                System.out.println("Nao existe departamentos para Associar");
                System.out.println("Crie o seu Departamento");
                return null;
            }
            int i=1;
            System.out.println("Departamentos disponiveis");
            for (Departamento d: depts) {
                System.out.println(i +"- " +d.getNome());
                i++;
            }
            System.out.println("0. Criar Departamento");
            //TODO Defesa para numeros e opçao valida
            Scanner s = new Scanner(System.in);
            System.out.println("Escolha: ");
            int opcao = Integer.parseInt(s.nextLine());
            if(opcao == 0 ){
                return null;
            }

            return depts.get(opcao-1);
        }catch (RemoteException e){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
                    break;
                }catch(NotBoundException  | RemoteException |MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }
        return null;
    }

    public static Departamento criaDepartamento(){
        //TODO Defesa: ver se o departamento a ser criado ja existe
        Scanner s = new Scanner(System.in);
        System.out.println("Insira nome do Departamento");
        String nome = s.nextLine();
        System.out.println("Insira ip");
        String ip = s.nextLine();
        Departamento d = new Departamento(nome,ip);
        return d;
    }
    @Override
    public void newServer() throws RemoteException, NotBoundException, MalformedURLException {
        adminConsole = (RMI_S_I) Naming.lookup("Server");
    }
}
