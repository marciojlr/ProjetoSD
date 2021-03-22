package Multicast;

import Classes.Pessoa;
import RMI.RMI_S_I;

import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private long SLEEP_TIME = 5000;

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
        MulticastUserS u = new MulticastUserS();
        u.start();
    }


    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));
    }


    public void run() {
        MulticastSocket socket = null;
        long counter = 0;
        System.out.println(this.getName() + " running...");
        try {
            socket = new MulticastSocket(PORT);  // recebe e envia
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                //String message = this.getName() + " packet " + counter++;
                //byte[] buffer = message.getBytes();
                byte[] b = new byte[256];
                //DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                DatagramPacket packet = new DatagramPacket(b, b.length);
                socket.receive(packet);
                String protocolo = new String(packet.getData(), 0, packet.getLength());
                System.out.println(protocolo);
                //para enviar para o rmi
                //separar o protocolo
                //socket.send(packet);

                try { sleep((long) (Math.random() * SLEEP_TIME)); } catch (InterruptedException e) { }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
/*para ler da consola
* type| login ; (...)
* */
class MulticastUserS extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;

    private Pessoa a = new Pessoa("Marcio", 123, "Estudante", "123", "DEI", 911234567, "Ramalheira", 1234567, 20/20);
    private Pessoa b = new Pessoa("Filipe", 456, "Estudante", "123", "DEI", 911234567, "Viseu", 7654321, 20/20);

    public MulticastUserS() { super("User " + (long) (Math.random() * 1000)); }

    public void dadosEleitor(){
        Scanner keyboardScanner = new Scanner(System.in);
        System.out.println("- - - - Bem Vindo - - - -");
        System.out.println("Insira os seus dados de eleitor");

        System.out.print("Numero CC: ");
        String readKeyboard = keyboardScanner.nextLine();
        // verifiicar por RMI se existem os dados das pessoas

        System.out.println(readKeyboard);
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName() + " ready...");
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            Scanner keyboardScanner = new Scanner(System.in);
            while (true) {
                dadosEleitor();
                String readKeyboard = keyboardScanner.nextLine();
                byte[] buffer = readKeyboard.getBytes();

                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}

