package Multicast;

import Classes.Departamento;
import RMI.RMI_S_I;

import java.net.*;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Classe que vai conter informação necessária e que tem de ser partilhada
 * entre as diferentes threads a correr no servidor Multicast
 */
class DadosPartilhados{
    int pedido;
    String name;
    RMI_S_I RMIserver;

    public DadosPartilhados(String name) throws RemoteException, NotBoundException, MalformedURLException {
        this.pedido = 0;
        this.name = name;
        this.RMIserver = (RMI_S_I) Naming.lookup("Server");
        System.out.println(this.name);
    }

    public int getPedido() {
        return pedido;
    }

    public void setPedido() {
        this.pedido = this.pedido + 1;
    }

    public String getName(){
        return this.name;
    }
}

/**
 * Classe que serve para coordenar as ligações entre a mesa de voto
 * e os diferentes terminais de voto
 */
public class MulticastServer extends Thread {

    private final String MULTICAST_ADDRESS = "224.0.224.0";
    private final int PORT = 4321;

    private Departamento dept;
    private DadosPartilhados dados;
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {

        DadosPartilhados dados = new DadosPartilhados(args[0]);
        dados.RMIserver.ping("Mesa " + dados.getName() + " ligada.");
        MulticastServer server = new MulticastServer(dados);
        server.start();
        MulticastUserS u = new MulticastUserS(dados);
        u.start();
        Vote v = new Vote(dados);
        v.start();
    }

    public MulticastServer(DadosPartilhados dados) {
        super("Server " + (long) (Math.random() * 1000));
        this.dados = dados;
        //todo: Configurar o departamento da mesa
        //this.dept = dept;
    }

    public void run() {
        System.out.println(this.getName() + " running...");
        MulticastSocket socket = null;

        try {
            socket = new MulticastSocket(PORT);  // recebe e envia
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            while (true) {
                String message = readMessage(socket);
                readComands(socket, message);
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

    private void readComands(MulticastSocket socket, String message) throws IOException {
        HashMap<String,String> map = new HashMap();
        String[] pares =  message.split("; ");

        for(String comandos : pares){
            String[] a = comandos.split(" \\| ");
            map.put(a[0],a[1]);
        }

        //MESSAGE TO GET A FREE TERMINAL
        if(map.get("type").equals("freeTerminal")){
            System.out.println("Este terminal esta livre: " + map.get("id"));
            if(Integer.parseInt(map.get("request")) == dados.getPedido()){
                dados.setPedido();
                send(socket, "type | chosen; id | " + map.get("id") + "; userCC | " + map.get("userCC") + "; election | " + map.get("election"));
                System.out.println("POR FAVOR EFETUE O SEU VOTO NO TERMINAL " + map.get("id"));
            }
        }
        //MESSAGE TO VERIFY LOGIN CREDENTIALS
        else if(map.get("type").equals("login")){
            if(dados.RMIserver.acceptLogin(Integer.parseInt(map.get("userCC")), map.get("username"), map.get("password"))){
                send(socket, "type | status; logged | on; id | " + map.get("id"));
            }
            else{
                send(socket, "type | status; logged | off; id | " + map.get("id"));
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

/**
 * Classe para escrever input do lado das mesa de voto
 */
class MulticastUserS extends Thread {

    private final String MULTICAST_ADDRESS;
    private final int PORT;
    private DadosPartilhados dados;

    public MulticastUserS(DadosPartilhados dados) {
        super("Server" + (long) (Math.random() * 1000));
        this.MULTICAST_ADDRESS = "224.0.224.0";
        this.PORT = 4321;
        this.dados = dados;
    }

    private String chooseElection(ArrayList<String> eleicoes){
        Scanner keyboardScanner = new Scanner(System.in);
        int election;

        System.out.println("SELECIONE A ELEIÇÃO EM QUE PRETENDE VOTAR");
        while (true){
            try{
                int option = 0;
                for(String titulo : eleicoes){
                    System.out.println(option + ". " + titulo);
                    option++;
                }
                election = Integer.parseInt(keyboardScanner.nextLine());
                return eleicoes.get(election);
            } catch (Exception e){
                System.out.println("\nOPÇÃO INVÁLIDA, ESCOLHA OUTRA");
            }
        }
    }

    private void getCC(MulticastSocket socket) throws IOException {
        System.out.println("Inserir número de Identificação: ");
        //READ FROM INPUT
        Scanner keyboardScanner = new Scanner(System.in);
        String readKeyboard = keyboardScanner.nextLine();
        //VERIFY IF ELECTOR IS REGISTERED
        int CC = Integer.parseInt(readKeyboard);

        // SE O ELEITOR SE ENCONTRAR REGISTADO
        if(dados.RMIserver.isRegisted(CC)){
            //ELEIÇÕES ELEGÍVEIS A VOTAR
            ArrayList<String> eleicoes = dados.RMIserver.getElections(CC, dados.getName());
            if(eleicoes.size() > 0){

                String electionName = chooseElection(eleicoes);

                //SEND DATA TO ALL THE CLIENTS
                String message = "type | free; request | " + dados.getPedido() + "; userCC | " + CC + "; election | " + electionName;
                byte[] buffer = message.getBytes();
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            }
            else{ // SE NÃO HOUVER ELEIÇÕES DISPONÍVEIS NESTA MESA
                System.out.println("NÃO EXISTEM ELEIÇÕES A DECORRER NESTA MESA DE VOTO");
            }
        }
        else{ // SE O ELEITOR NÃO ESTIVER REGISTADO
            System.out.println("O utilizador não se encontra nos registos");
        }
    }

    public void run() {

        MulticastSocket socket = null;

        System.out.println(this.getName() + " ready...");
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            while (true) {
                getCC(socket);
                Thread.sleep(500);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}

class Vote extends Thread {
    private final String MULTICAST_ADDRESS = "224.1.224.0";
    private final int PORT = 4321;
    private DadosPartilhados dados;

    public Vote(DadosPartilhados dados) {
        super("VoteThread " + (long) (Math.random() * 1000));
        this.dados = dados;
    }

    public void receiveVote(MulticastSocket socket) throws IOException {
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println(message);
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName() + " running...");
        try {
            socket = new MulticastSocket(PORT);  // recebe e envia
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            while (true) {
                receiveVote(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}