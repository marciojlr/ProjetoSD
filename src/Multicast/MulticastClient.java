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
    private Data data;

    public MulticastClient(long number, Data data) {

        super("User " + number);
        this.data = data;
    }

    public static void main(String[] args) {
        long number = (long) (Math.random() * 1000);
        Data data = new Data();
        MulticastClient client = new MulticastClient(number, data);
        client.start();
        MulticastUser user = new MulticastUser(number, data);
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
        HashMap<String,String> map = new HashMap();
        String[] pares =  message.split("; ");

        for(String comandos : pares) {
            String[] a = comandos.split(" \\| ");
            map.put(a[0], a[1]);
        }

        //MESSAGE TO ALL TERMINALS
        if(map.get("type").equals("free")){
            if(free){
                send(socket, "type | freeTerminal; id | " + this.getName() + "; request | " + map.get("request") + "; userCC | " + map.get("userCC"));
            }
        }
        // MESSAGES TO SPECIFIC TERMINAL
        else if(map.get("id").equals(this.getName())){

            // IF THIS WAS THE CHOSEN TERMINAL
            if(map.get("type").equals("chosen")){
                    System.out.println(this.getName() + " foi escolhido");
                    free = false;
                    int userCC = Integer.parseInt(map.get("userCC"));
                    data.setUserCC(userCC);
                    data.setBlocked();
                    System.out.println("O user : " + userCC + " tem acesso e a maquina esta " + data.getBlocked());
            }
            // VERIFY IF LOGIN CREDENTIALS ARE CORRECT
            else if(map.get("type").equals("loginC")){
                System.out.println(message);
                if(map.get("status").equals("on")){
                    System.out.println("Credenciais corretas");
                }
                else{
                    System.out.println("Credenciais erradas");
                }
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
    private Data data;
    public MulticastUser(long number, Data data) {
        super("User " + number);
        this.data = data;
    }

    private void send(MulticastSocket socket, String message) throws IOException {
        byte[] buffer = message.getBytes();
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName());
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            Scanner keyboardScanner = new Scanner(System.in);
            while (true) {
                //String readKeyboard = keyboardScanner.nextLine();
                System.out.print("Username: ");
                String username = keyboardScanner.nextLine();
                //byte[] buffer = readKeyboard.getBytes();

                if(data.getBlocked()){
                    System.out.println("A máquina encontra-se bloquada, por favor dirija-se à mesa de voto");
                }
                else{
                    System.out.print("Password: ");
                    String password = keyboardScanner.nextLine();
                    send(socket, "type | login; id | " + this.getName() + "; userCC | " + data.getUserCC() + "; username | " + username + "; password | " + password);

                }

                //InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                //DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                //socket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}

class Data{
    private int userCC;
    private boolean blocked;
    public Data(){
        this.userCC = 0;
        blocked = true;
    }

    public void setUserCC(int userCC){
        this.userCC = userCC;
    }

    public int getUserCC() {
        return userCC;
    }

    public void clearUser(){
        this.userCC = 0;
    }

    public void setBlocked(){
        this.blocked = !this.blocked;
    }

    public boolean getBlocked(){
        return blocked;
    }
}
