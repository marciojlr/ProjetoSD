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

    public static void main(String[] args) {
        MulticastClient client = new MulticastClient();
        client.start();
        MulticastUser user = new MulticastUser();
        user.start();
    }

    public void run() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                String message = new String(packet.getData(), 0, packet.getLength());
                readMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    private void readMessage(String message){
        HashMap<String,String> map = new HashMap();

        String[] pares =  message.split("; ");

        for(String comandos : pares){
            String[] a = comandos.split(" \\| ");
            map.put(a[0],a[1]);
        }

        if(map.get("type").equals("login")){
            System.out.println(map.get("type"));

            System.out.println("Username: " + map.get("username"));

            System.out.println("Password: " + map.get("password"));
        }

    }
}

class MulticastUser extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;

    public MulticastUser() {
        super("User " + (long) (Math.random() * 1000));
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName() + " ready...");
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
