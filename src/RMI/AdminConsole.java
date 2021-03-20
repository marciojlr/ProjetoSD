package RMI;

import Classes.Pessoa;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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
            System.out.println("2 - ");
            System.out.println("3 - ");
            System.out.println("4 - ");
            System.out.println("5 - ");
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
}
