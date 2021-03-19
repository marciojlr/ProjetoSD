package RMI;

import Classes.Pessoa;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RMIServer extends UnicastRemoteObject implements RMI_S_I {

    ArrayList<Pessoa> listaPessoas = new ArrayList<Pessoa>();

    protected RMIServer() throws RemoteException {
        super();
    }

    public String teste (){
        System.out.println("olaaaa");

        return "olaaaaaa";
    }

    public String registarPessoa(Pessoa p){

        System.out.println("Estou a registar");
        System.out.println(p.getNome());
        System.out.println(p.getNum_eleitor());
        System.out.println(p.getTipo());

        listaPessoas.add(p);

        return "Registado";
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
