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

/**
 * Classe que vai conter informação necessária e que tem de ser partilhada
 * entre as diferentes threads a correr no servidor Multicast
 */
class DadosPartilhados{
    int pedido;

    public DadosPartilhados(){
        int n_pedido = 0;
    }

    public int getPedido() {
        return pedido;
    }

    public void setPedido() {
        this.pedido = this.pedido + 1;
    }
}

public class MulticastServer extends Thread {

    private final String MULTICAST_ADDRESS = "224.0.224.0";
    private final int PORT = 4321;

    private Departamento dept;
    private DadosPartilhados dados;
    public static void main(String[] args) {

        try {
            RMI_S_I serverRMI = (RMI_S_I) Naming.lookup("Server");
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
        DadosPartilhados dados = new DadosPartilhados();

        MulticastServer server = new MulticastServer(dados);
        server.start();
        MulticastUserS u = new MulticastUserS(dados);
        u.start();
    }

    public MulticastServer(DadosPartilhados dados) {
        super("Server " + (long) (Math.random() * 1000));
        this.dados = dados;
        //todo: Configurar o departamento da mesa
        //this.dept = dept;
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
        //System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
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

        if(map.get("type").equals("freeTerminal")){
            System.out.println("Este terminal esta livre: " + map.get("id"));
            if(Integer.parseInt(map.get("request")) == dados.getPedido()){
                dados.setPedido();
                send(socket, "type | chosen; id | " + map.get("id"));
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

    private void getCC(MulticastSocket socket) throws IOException {
        System.out.println("Inserir número de Identificação: ");
        //READ FROM INPUT
        Scanner keyboardScanner = new Scanner(System.in);
        String readKeyboard = keyboardScanner.nextLine();

        //SEND DATA TO ALL THE CLIENTS
        String message = "type | free; request | " + dados.getPedido();
        byte[] buffer = message.getBytes();
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
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

