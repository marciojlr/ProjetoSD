package RMI;

import Classes.Eleicao;
import Classes.Pessoa;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

// Script de Cliente diretamente ligado ao Server
public class AdminConsole {

    private static RMI_S_I adminConsole;


    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException{

        adminConsole = (RMI_S_I) Naming.lookup("Server");
        String teste;
        teste= adminConsole.teste();
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
            System.out.println("4. ");
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
        //TODO: alterar para a classe departamento
        System.out.print("Departamento: ");
        String departamento = s.nextLine();

        System.out.print("CC: ");
        int CC=Integer.parseInt(s.nextLine());

        System.out.println("Validade CC");
        System.out.print("Dia: ");
        int dia = Integer.parseInt(s.nextLine());
        System.out.print("Mês: ");
        int mes = Integer.parseInt(s.nextLine());
        System.out.print("Ano: ");
        int ano = Integer.parseInt(s.nextLine());

        System.out.print("Telemovel: ");
        int telemovel =Integer.parseInt(s.nextLine());

        System.out.print("Morada: ");
        String morada = s.nextLine();

        //chamar funçao registar do server
        try{
            String r;
            r = adminConsole.registarPessoa(nome, tipo, password, departamento, CC, dia, mes, ano, telemovel, morada);
            System.out.println(r);
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
    public static void  criaEleicao() throws RemoteException {
        //Recolher informaçao
        Scanner s = new Scanner(System.in);

        System.out.println("Data de inicio: ");
        int data_inicio = Integer.parseInt(s.nextLine());

        System.out.println("Data de final: ");
        int data_final = Integer.parseInt(s.nextLine());

        System.out.println("Titulo: ");
        String titulo= s.nextLine();

        System.out.println("Descrição: ");
        String descricao= s.nextLine();

        //TODO: alterar para a classe departamento
        System.out.println("Departamento: ");
        String departamento = s.nextLine();

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
            r = adminConsole.criarEleicao(null, null, titulo, descricao, departamento, tipo_Pessoa);
            System.out.println(r);
        }catch (RemoteException e){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (RMI_S_I) Naming.lookup("Server");
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

}
