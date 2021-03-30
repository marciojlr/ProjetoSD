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
    private Data data;

    public MulticastClient(long number, Data data) {

        super("TERMINAL " + number);
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
        String message = new String(packet.getData(), 0, packet.getLength());
        return message;
    }

    private void getCandidatesList(HashMap<String, String> map){
        int size = Integer.parseInt(map.get("list_item"));
        System.out.println("LISTAS CANDIDATAS");
        for(int i=0; i<size; i++){
            String key = "item_" + i;
            System.out.println(i + ". " + map.get(key));
        }
        //OPÇÃO DE VOTO EM BRANCO
        System.out.println(size + ". Voto Branco");
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
            if(data.isFree()){
                send(socket, "type | freeTerminal; id | " + this.getName() + "; request | " + map.get("request") + "; userCC | " + map.get("userCC") + "; election | " + map.get("election"));
            }
        }
        // MESSAGES TO SPECIFIC TERMINAL
        else if(map.get("id").equals(this.getName())){

            // IF THIS WAS THE CHOSEN TERMINAL
            if(map.get("type").equals("chosen")){
                    int userCC = Integer.parseInt(map.get("userCC"));
                    data.setFree(false);
                    data.setUserCC(userCC);
                    data.setBlocked(false);
                    data.setElectionName(map.get("election"));
            }
            // VERIFY IF LOGIN CREDENTIALS ARE CORRECT
            else if(map.get("type").equals("status")){
                if(map.get("logged").equals("on")){
                    data.setLoggedIn(true);
                    System.out.println("Welcome to eVoting");
                    send(socket, "type | candidates; election | " + data.getElectionName() + "; id | " + this.getName());
                }
                else{
                    data.setLoggedIn(false);
                    System.out.println("\n(!) CREDENCIAIS ERRADAS\n");
                }
            }
            // TODO: alterar o type
            else if(map.get("type").equals("candidateS")){
                getCandidatesList(map);
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
    private String VOTE_ADDRESS = "224.1.224.0";
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private Data data;
    public MulticastUser(long number, Data data) {
        super("TERMINAL " + number);
        this.data = data;
    }

    private void send(MulticastSocket socket, String message) throws IOException {
        byte[] buffer = message.getBytes();
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
    }

    private void sendVote(MulticastSocket socket, String message) throws IOException {
        byte[] buffer = message.getBytes();
        InetAddress group = InetAddress.getByName(VOTE_ADDRESS);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
    }

    public void run() {
        MulticastSocket socket = null;
        MulticastSocket voteSocket = null;
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            voteSocket = new MulticastSocket(); // create socket to send vote
            Scanner keyboardScanner = new Scanner(System.in);

            while (true) {
                System.out.println("----------< " + this.getName() + " >----------");
                System.out.print("Username: ");
                String username = keyboardScanner.nextLine();
                if(data.getBlocked()){
                    System.out.println("\n(!) A MÁQUINA ENCONTRA-SE BLOQUEADA, DIRIJA-SE À MESA DE VOTO\n");
                }
                else{
                    System.out.print("Password: ");
                    String password = keyboardScanner.nextLine();
                    send(socket, "type | login; id | " + this.getName() + "; userCC | " + data.getUserCC() + "; username | " + username + "; password | " + password);
                    Thread.sleep(500);
                    if(data.isLoggedIn()){
                        System.out.print("> ");
                        //INPUT COM OPÇÃO DE VOTO
                        String vote = keyboardScanner.nextLine();
                        sendVote(voteSocket, "type | vote; election | " + data.getElectionName() + "; option | " + vote);
                        sendVote(voteSocket, "type | elector; election | " + data.getElectionName() + "; userCC | " + data.getUserCC());
                        data.setBlocked(true);
                        data.setFree(true);
                        data.setLoggedIn(false);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            socket.close();
            voteSocket.close();
        }
    }
}

class Data{
    private int userCC;
    private boolean free;
    private boolean blocked;
    private boolean loggedIn;
    private String electionName;

    public Data(){
        this.userCC = 0;
        this.free = true;
        this.blocked = true;
        this.loggedIn = false;
        this.electionName = "";
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

    public void setBlocked(boolean blocked){
        this.blocked = blocked;
    }

    public boolean getBlocked(){
        return blocked;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void setElectionName(String electionName) {
        this.electionName = electionName;
    }

    public String getElectionName() {
        return electionName;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public synchronized void espera() throws InterruptedException {
        wait();
    }

    public synchronized void anda() throws InterruptedException {
        notify();
    }
}
