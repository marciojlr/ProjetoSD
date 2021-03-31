package Multicast;

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
    private int pedido;
    private final String name;
    public RMI_S_I RMIserver;

    public DadosPartilhados(String name) throws RemoteException, NotBoundException, MalformedURLException {
        this.pedido = 0;
        this.name = name;
        this.RMIserver = (RMI_S_I) Naming.lookup("Server");
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

    public void setRMIserver() throws RemoteException, NotBoundException, MalformedURLException {
        this.RMIserver = (RMI_S_I) Naming.lookup("Server");
    }
}

/*
* Exemplos de ips   224.224.0.0
*                   224.224.1.0
*/
/**
 * Classe que serve para coordenar as ligações entre a mesa de voto
 * e os diferentes terminais de voto
 */
public class MulticastServer extends Thread {

    private final String MULTICAST_ADDRESS;
    private final int PORT = 4321;
    private final DadosPartilhados dados;

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {

        DadosPartilhados dados = new DadosPartilhados(args[0]);
        String ipMesa = args[1];
        String ipVoto = getVoteIp(args[1]);
        System.out.println(ipMesa + " " + ipVoto);
        dados.RMIserver.ping("Mesa " + dados.getName() + " ligada.");
        MulticastServer server = new MulticastServer(dados,ipMesa);
        server.start();
        MulticastUserS u = new MulticastUserS(dados, ipMesa);
        u.start();
        Vote v = new Vote(dados, ipVoto);
        v.start();
    }

    public MulticastServer(DadosPartilhados dados, String ip) {
        super("Server " + (long) (Math.random() * 1000));
        this.dados = dados;
        this.MULTICAST_ADDRESS = ip;
    }

    public void run() {
        MulticastSocket socket = null;

        try {
            socket = new MulticastSocket(PORT);  // recebe e envia
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            while (true) {
                String message = readMessage(socket);
                readComands(socket, message);
            }
        } catch (IOException | NotBoundException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    public static String getVoteIp(String ip){
        String[] num = ip.split("\\.");
        int last = Integer.parseInt(num[3]);
        last = last+1;

        return num[0] + "." + num[1] + "." + num[2] + "." + last;
    }

    private String readMessage(MulticastSocket socket) throws IOException {
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return new String(packet.getData(), 0, packet.getLength());
    }

    private void readComands(MulticastSocket socket, String message) throws IOException, NotBoundException {
        HashMap<String,String> map = new HashMap();
        String[] pares =  message.split("; ");

        for(String comandos : pares){
            String[] a = comandos.split(" \\| ");
            map.put(a[0],a[1]);
        }

        //MESSAGE TO GET A FREE TERMINAL
        if(map.get("type").equals("freeTerminal")){
            if(Integer.parseInt(map.get("request")) == dados.getPedido()){
                dados.setPedido();
                send(socket, "type | chosen; id | " + map.get("id") + "; userCC | " + map.get("userCC") + "; election | " + map.get("election"));
                System.out.println("POR FAVOR EFETUE O SEU VOTO: " + map.get("id"));
            }
        }
        //MESSAGE TO VERIFY LOGIN CREDENTIALS
        else if(map.get("type").equals("login")){
            boolean accept;
            while(true){
                try{
                    accept = dados.RMIserver.acceptLogin(Integer.parseInt(map.get("userCC")), map.get("username"), map.get("password"));
                    break;
                }catch(Exception e){
                    dados.setRMIserver();
                }
            }
            if(accept){
                send(socket, "type | status; logged | on; id | " + map.get("id"));
            }
            else{
                send(socket, "type | status; logged | off; id | " + map.get("id"));
            }

        }
        //MESSAGE TO GET THE LIST OF CANDIDATES
        else if(map.get("type").equals("candidates")){
            ArrayList<String> candidates;
            while(true){
                try{
                    candidates = dados.RMIserver.getCandidates(map.get("election"));
                    break;
                }catch(Exception e){
                    dados.setRMIserver();
                }
            }
            send(socket, "id | " + map.get("id") + "; type | candidateS; " + candidatesToString(candidates));
        }
    }

    private String candidatesToString(ArrayList<String> candidates){
        String protocol;
        protocol = "list_item | " + candidates.size();

        for(int i=0; i<candidates.size(); i++){
            protocol += "; item_" + i + " | " + candidates.get(i);
        }
        return protocol;
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
    private final int PORT = 4321;
    private final DadosPartilhados dados;

    public MulticastUserS(DadosPartilhados dados, String ip) {
        super("Server" + (long) (Math.random() * 1000));
        this.MULTICAST_ADDRESS = ip;
        this.dados = dados;
    }

    private String chooseElection(ArrayList<String> eleicoes){
        Scanner keyboardScanner = new Scanner(System.in);

        System.out.println("\nSELECIONE A ELEIÇÃO EM QUE PRETENDE VOTAR");
        while (true){
            try{
                int option = 1;
                for(String titulo : eleicoes){
                    System.out.println(option + ". " + titulo);
                    option++;
                }
                System.out.print("> ");
                int election = Integer.parseInt(keyboardScanner.nextLine());
                election = election - 1;
                return eleicoes.get(election);
            } catch (Exception e){
                System.out.println("\n(!) OPÇÃO INVÁLIDA, ESCOLHA OUTRA");
            }
        }
    }

    private void getCC(MulticastSocket socket) throws IOException, NotBoundException {

        System.out.println("Inserir número de Identificação: ");
        System.out.print("> ");
        //INPUT
        Scanner keyboardScanner = new Scanner(System.in);
        String readKeyboard = keyboardScanner.nextLine();
        int CC;
        //VERIFICAR SE O ELEITOR SE ENCONTRA REGISTADO
        try{
            CC = Integer.parseInt(readKeyboard);
        }
        catch(Exception e){
            System.out.println("(!) FORMATO INVÁLIDO DE NÚMERO DE IDENTIFICAÇÃO");
            return;
        }
        boolean registered;
        //VERIFICA SE O ELEITOR SE ENCONTRA REGISTADO
        while(true){
            try{
                registered = dados.RMIserver.isRegistered(CC);
                break;
            }catch(Exception e){
                dados.setRMIserver();
            }
        }

        if(registered){ //REGISTADO

            ArrayList<String> elections;
            while(true){
                try{
                    elections = dados.RMIserver.getElections(CC, dados.getName());
                    break;
                }catch(Exception e){
                    dados.setRMIserver();
                }
            }
            if(elections.size() > 0){ //EXISTEM ELEIÇÕES DISPONÍVEIS NESTA MESA
                String electionName = chooseElection(elections);
                //SEND DATA TO ALL THE CLIENTS
                String message = "type | free; request | " + dados.getPedido() + "; userCC | " + CC + "; election | " + electionName;
                byte[] buffer = message.getBytes();
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            }
            else{ //NÃO HÁ ELEIÇÕES DISPONÍVEIS NESTA MESA
                System.out.println("(!) NÃO EXISTEM ELEIÇÕES A DECORRER NESTA MESA DE VOTO");
            }
        }
        else{ //SE O ELEITOR NÃO ESTIVER REGISTADO
            System.out.println("(!) O UTILIZADOR NÃO SE ENCONTRA REGISTADO");
        }
    }

    public void run() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            while (true) {
                System.out.println("\n----------< MESA " + dados.getName() + " >----------");
                getCC(socket);
                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException | NotBoundException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}

class Vote extends Thread {
    private final String MULTICAST_ADDRESS;
    private final int PORT = 4321;
    private final DadosPartilhados dados;

    public Vote(DadosPartilhados dados, String ip) {
        super("VoteThread " + (long) (Math.random() * 1000));
        this.dados = dados;
        this.MULTICAST_ADDRESS = ip;
    }

    public void receiveVote(MulticastSocket socket) throws IOException, NotBoundException {

        //RECEIVE UDP PACKET WHICH CONTAINS THE INFORMATION
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String message = new String(packet.getData(), 0, packet.getLength());

        HashMap<String,String> map = new HashMap();
        String[] pares =  message.split("; ");

        for(String comandos : pares){
            String[] a = comandos.split(" \\| ");
            map.put(a[0],a[1]);
        }

        if(map.get("type").equals("vote")){
            String option = map.get("option");
            while(true){
                try{
                    dados.RMIserver.vote(map.get("election"), option);
                    break;
                }catch(Exception e){
                    dados.setRMIserver();
                }
            }
        }
        else if(map.get("type").equals("elector")){
            String election = map.get("election");
            String userCC = map.get("userCC");
            System.out.println(userCC + " votou em " + election);
            while(true){
                try{
                    dados.RMIserver.addElector(election,Integer.parseInt(userCC),dados.getName());
                    break;
                }catch(Exception e){
                    dados.setRMIserver();
                }
            }
        }
    }

    public void run() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(PORT);  // recebe e envia
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            while (true) {
                receiveVote(socket);
            }
        } catch (IOException | NotBoundException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}