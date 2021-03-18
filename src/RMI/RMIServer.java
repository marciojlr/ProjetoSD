package RMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
/*very good ma friend*/

public class RMIServer extends UnicastRemoteObject implements AdminInterface {

    protected RMIServer() throws RemoteException {
        super();
    }

    public String teste (){
        System.out.println("olaaaa");

        return "olaaaaaa";
    }

    public static void main(String [] args ){
        try{
            AdminInterface admin = new RMIServer();
            Registry r = LocateRegistry.createRegistry(1099);
            r.rebind("Server",admin);
            System.out.println("RMI Server ready...!");
        } catch (RemoteException e){
            System.out.println("Server already connected");
        }

    }

}
