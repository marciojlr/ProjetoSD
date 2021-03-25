package Multicast;

import Classes.Departamento;
import Classes.Pessoa;
import RMI.RMI_S_I;

import java.net.*;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Scanner;

public class MulticastServer extends Thread {

    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private long SLEEP_TIME = 5000;
    private static RMI_S_I serverRMI;

    private Departamento dept;

    public static void main(String[] args) {

        try {
            serverRMI = (RMI_S_I) Naming.lookup("Server");
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        MulticastServer server = new MulticastServer();
        server.start();
        MulticastUserS u = new MulticastUserS();
        u.start();
    }

    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));
        this.dept = dept;
    }

    public void run() {

        MulticastSocket socket = null;
        long counter = 0;
        Pessoa eleitor;
        System.out.println(this.getName() + " running...");
        try {
            socket = new MulticastSocket(PORT);  // recebe e envia
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            while (true) {

                String message = readMessage(socket);
                System.out.println(message);
                readComands(message);
                //eleitor = intruduzirCC();
                //send(socket,"type | free;");
                //byte[] b = new byte[256];
                //DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                //DatagramPacket packet = new DatagramPacket(b, b.length);
                //socket.receive(packet);
                //String protocolo = new String(packet.getData(), 0, packet.getLength());
                //System.out.println(protocolo);
                //para enviar para o rmi
                //separar o protocolo
                //socket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    private String readMessage(MulticastSocket socket) throws IOException {

        byte[] buffer = new byte[256];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
        String message = new String(packet.getData(), 0, packet.getLength());
        return message;
    }

    private void readComands(String message) {
        HashMap<String,String> map = new HashMap();
        String[] pares =  message.split("; ");

        for(String comandos : pares){
            String[] a = comandos.split(" \\| ");
            map.put(a[0],a[1]);
        }

        System.out.println(map.get("type"));

        if(map.get("type").equals("free")){
            System.out.println("Type: " + map.get("type"));

            System.out.println("Username: " + map.get("username"));

            System.out.println("Password: " + map.get("password"));
        }
    }

    private Pessoa intruduzirCC(){
        Scanner s = new Scanner(System.in);
        System.out.println("- - - - Bem vindo - - - -");
        System.out.println("Número do CC");
        int CC=Integer.parseInt(s.nextLine());
        return new Pessoa("Marcio","Estudante","1234",null,1234,null, 910669899, "Coimbra");
    }

    private void send(MulticastSocket socket, String message) throws IOException {
        byte[] buffer = message.getBytes();
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
    }

}
/*para ler da consola
* type| login ; (...)
* */
class MulticastUserS extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;

    //private Pessoa a = new Pessoa("Marcio", "Estudante", "123", "DEI", 123456789, "Ramalheira");
    //private Pessoa b = new Pessoa("Filipe", "Estudante", "123", "DEI", 987654321, "Viseu");

    public MulticastUserS() { super("User " + (long) (Math.random() * 1000)); }

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName() + " ready...");
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            while (true) {
                getCC(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    private void getCC(MulticastSocket socket) throws IOException {
        System.out.println("Inserir número de Identificação: ");
        Scanner keyboardScanner = new Scanner(System.in);
        String readKeyboard = keyboardScanner.nextLine();
        byte[] buffer = readKeyboard.getBytes();

        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
    }
}

