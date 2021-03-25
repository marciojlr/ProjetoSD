package Multicast;

import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The MulticastClient class joins a multicast group and loops receiving
 * messages from that group. The client also runs a MulticastUser thread that
 * loops reading a string from the keyboard and multicasting it to the group.
 * <p>
 * The example IPv4 address chosen may require you to use a VM option to
 * prefer IPv4 (if your operating system uses IPv6 sockets by default).
 * <p>
 * Usage: java -D java.net.preferIPv4Stack=true MulticastClient
 *
 * @author Raul Barbosa
 * @version 1.0
 */
public class MulticastClient extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private boolean free = true;

    public MulticastClient(long number) {
        super("User " + number);
    }

    public static void main(String[] args) {
        long number = (long) (Math.random() * 1000);
        MulticastClient client = new MulticastClient(number);
        client.start();
        MulticastUser user = new MulticastUser(number);
        user.start();
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName());
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                String message = readMessage(socket);
                readComands(socket,message);
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
        //System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
        String message = new String(packet.getData(), 0, packet.getLength());

        return message;
    }

    private void readComands(MulticastSocket socket, String message) throws IOException {
        HashMap<String,String> codes = new HashMap();
        String[] pares =  message.split("; ");

        for(String comandos : pares) {
            String[] a = comandos.split(" \\| ");
            codes.put(a[0], a[1]);
        }

        if(codes.get("type").equals("free")){
            if(free){
                send(socket, "type | freeTerminal; id | " + this.getName());
            }
        }
        else if(codes.get("type").equals("chosen")){
            if(codes.get("id").equals(this.getName())){
                System.out.println(this.getName() + " foi escolhido");
                free = false;
                // abrir voto;
            }
        }
    }

    private void send(MulticastSocket socket, String message) throws IOException {
        byte[] buffer = message.getBytes();
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
    }
}

class MulticastUser extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;

    public MulticastUser(long number) {
        super("User " + number);
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName());
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            Scanner keyboardScanner = new Scanner(System.in);
            while (true) {
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
