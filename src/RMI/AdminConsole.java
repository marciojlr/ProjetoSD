package RMI;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class AdminConsole {

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException{

        AdminInterface adminConsole = (AdminInterface) Naming.lookup("Server");
        String teste;
        teste= adminConsole.teste();
        System.out.println(teste);


    }
}
