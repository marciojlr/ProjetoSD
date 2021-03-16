package RMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends UnicastRemoteObject implements AdminInterface {

    protected RMIServer() throws RemoteException {
        super();
    }

    public String teste (){
        System.out.println("olaaaa");

        return "olaaaaaa";
    }

    public static void main(String [] args ) throws RemoteException{
        AdminInterface admin = new RMIServer();
        LocateRegistry.createRegistry(1099).rebind("Server",admin);
        System.out.println("RMI Server ready...!");
    }

}
