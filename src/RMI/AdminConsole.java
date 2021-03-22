package RMI;

import Classes.Eleicao;
import Classes.Pessoa;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
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
            System.out.println("Bem vindo!");
            System.out.println("1 - Registar");
            System.out.println("2 - Criar Eleiçao");
            System.out.println("3 - Gerir Listas de candidatos a uma eleiçao");
            System.out.println("4 - ");
            System.out.println("5 - Alterar propriedades de eleição");
            option= myObj.nextLine();

            if(option.equals("1")){
                //Determinar o tipo de Pessoa a criar
                String tipo;
                Scanner s = new Scanner(System.in);
                System.out.println("1-Estudante");
                System.out.println("2-Funcionario");
                System.out.println("3-");
                tipo = s.nextLine();
                try{
                    RegistoPessoa(tipo);
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
                    //funçao
                    gerirListaCandidata();
                }catch (RemoteException e){
                    System.out.println(e);
                }
            }
            else if(option.equals("5")){
                System.out.println("Alterar Propridades de Eleição");
            }
        }
    }

    //Coment
    public static void  RegistoPessoa(String tipo) throws RemoteException {
        //Recolher informaçao
        String nome;
        int num_eleitor;
        Scanner s = new Scanner(System.in);
        System.out.print("Nome: ");
        nome = s.nextLine();
        System.out.print("Numero Eleitor: ");
        num_eleitor = Integer.parseInt(s.nextLine());
        System.out.print("Password: ");
        String password= s.nextLine();
        System.out.print("Departamento: ");
        String Departamento = s.nextLine();
        System.out.print("Telemovel: ");
        int tel=Integer.parseInt(s.nextLine());
        System.out.print("Morada: ");
        String morada = s.nextLine();
        System.out.print("CC: ");
        int CC=Integer.parseInt(s.nextLine());
        System.out.print("CC validade: ");
        int validade_CC=Integer.parseInt(s.nextLine());
        //chamar funçao registar do server
        try{
            System.out.println("aqui");
            String r;
            r = adminConsole.registarPessoa(nome,num_eleitor,tipo,password,Departamento,tel,morada,CC,validade_CC);
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
        String nome;
        int num_eleitor;
        Scanner s = new Scanner(System.in);
        System.out.println("Data de inicio: ");
        int data_inicio = Integer.parseInt(s.nextLine());
        System.out.println("Data de final: ");
        int data_final = Integer.parseInt(s.nextLine());
        System.out.println("Titulo: ");
        String titulo= s.nextLine();
        System.out.println("Descrição: ");
        String Descricao= s.nextLine();
        //preciso alterar
        //Classe departamento
        System.out.println("Departamento: ");
        String dept = s.nextLine();
        //So podem votar pessoas deste tipo
        System.out.println("Grupo de pessoas que pode votar");
        System.out.println("1- Estudante   2- Funcionario   3-Docente ");
        String tipo_Pessoa = s.nextLine();
        try{
            String r;
            r = adminConsole.criarEleicao(data_inicio,data_final,titulo,Descricao,dept,tipo_Pessoa);
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
    /*
    *Adicionar ou remover lista
    *
    *fazer metodo para editar a lista de candidatos eleiçao.listaCandidatos
    *Penso q o metodo pode ser na classe
    *
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
                eleicoes = adminConsole.getListaEleicoes();
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
}
