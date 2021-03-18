package RMI;

import Classes.Pessoa;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

// Script de Cliente diretamente ligado ao Server
public class AdminConsole {
    private static AdminInterface adminConsole;
    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException{

        adminConsole = (AdminInterface) Naming.lookup("Server");
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
            System.out.println("1-Registar");
            System.out.println("2-");
            System.out.println("3-");
            System.out.println("4-");
            System.out.println("5-");
            option= myObj.nextLine();

            if(option.equals(option)){
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
        }
    }


    public static void  RegistoPessoa(String tipo) throws RemoteException {
        //Recolher informaçao
        String nome;
        int num_eleitor;
        Scanner s = new Scanner(System.in);
        System.out.print("Nome: ");
        nome = s.nextLine();
        System.out.print("Numero Eleitor: ");
        num_eleitor = Integer.parseInt(s.nextLine());

        // Criar objeto
        Pessoa p = new Pessoa(nome,num_eleitor,tipo);

        //chamar funçao registar do server
        try{
            System.out.println("aqui");
            String r;
            r = adminConsole.registarPessoa(p);
            System.out.println(r);
        }catch (RemoteException e){
            while (true){
                try {
                    //Thread.sleep(1000);
                    adminConsole = (AdminInterface) Naming.lookup("Server");
                    break;
                }catch(NotBoundException  | RemoteException |MalformedURLException m){
                    System.out.println("nao conectei");
                }
            }
        }
    }
}
