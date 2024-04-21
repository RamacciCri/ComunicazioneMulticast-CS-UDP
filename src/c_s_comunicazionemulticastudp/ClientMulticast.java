package c_s_comunicazionemulticastudp;
import java.io.*;
import java.net.*;

public class ClientMulticast {
    //colore del prompt del Server
    public static final String ANSI_BLUE = "\u001B[34m";
    //colore del prompt del Client
    public static final String RED_BOLD = "\033[1;31m";
    //colore del prompt del gruppo
    public static final String GREEN_UNDERLINED = "\033[4;32m";
    //colore reset
    public static final String RESET = "\033[0m";

    public static void main(String[] args) {
        DatagramSocket dSocket;
        DatagramPacket inPacket;
        DatagramPacket outPacket;

        //buffer di lettura
        byte[] inBuffer= new byte[256];

        //messaggio di richiesta
        String messageOut = "Richiesta comunicazione";
        //messaggio di risposta
        String messageIn;

        try {    
            System.out.println("CLIENT UDP");
            System.out.println(RED_BOLD + "CLIENT UDP" + RESET);
            //1) RICHIESTA AL SERVER
            //si recupera l'IP del server UDP
            InetAddress serverAddress = InetAddress.getLocalHost();
            int port = 2000;
            System.out.println("Indirizzo del server trovato!");
            System.out.println(RED_BOLD + "Indirizzo del server trovato!" + RESET);

            //istanza del socket UDP per la prima comunicazione con il server
            dSocket = new DatagramSocket();

            //si inviano i dati
            outPacket = new DatagramPacket(messageOut.getBytes(), messageOut.length(), serverAddress, port);
            dSocket.send(outPacket);
            System.out.println("Richiesta al server inviata!");
            System.out.println(RED_BOLD + "Richiesta al server inviata!" + RESET);

            //2) RISPOSTA DEL SERVER
            //si prepara il datagramma per ricevere dati dal server
            inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            dSocket.receive(inPacket);
            
            //lettura del messaggio ricevuto e sua visualizzazione
            messageOut = new String(inPacket.getData(),0,inPacket.getLength());
            System.out.println("Lettura dei dati ricevuti dal server");
            System.out.println(ANSI_BLUE + "Lettura dei dati ricevuti dal server" + RESET);

            messageIn = new String(inPacket.getData(), 0, inPacket.getLength());
            System.out.println("Messaggio ricevuto dal server " + serverAddress + ":" + port + "> " + messageIn);
            System.out.println(ANSI_BLUE +"Messaggio ricevuto dal server " + serverAddress + ":" + port + "\n\t" + messageIn + RESET);

            //3) RICEZIONE MESSAGGIO DEL GRUPPO
            //istanza del Multicast socket e unione al gruppo
            InetAddress group = InetAddress.getByName("239.255.255.250");
            int groupPort = 1900;
            MulticastSocket mSocket = new MulticastSocket(groupPort);
            mSocket.joinGroup(group);

            //si prepara il datagramma per ricevere dati dal gruppo
            inPacket = new DatagramPacket(inBuffer,inBuffer.length);
            mSocket.receive(inPacket); 

            //lettura del messaggio ricevuto e sua visualizzazione
            messageIn = new String(inPacket.getData(),0, inPacket.getLength());
            System.out.println("Lettura dei dati ricevuti dai partecipanti al gruppo");
            System.out.println("Messaggio ricevuto dal gruppo " + group + ":" + groupPort + "> " + messageIn);

            System.out.println(GREEN_UNDERLINED + "Lettura dei dati ricevuti dai partecipanti al gruppo" + RESET);
            System.out.println(GREEN_UNDERLINED + "Messaggio ricevuto dal gruppo " + group + ":" + groupPort + "\n\t" + messageIn + RESET);

            //uscita dal gruppo
            mSocket.leaveGroup(group);
        } 
        catch(Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("Errore durante la comunicazione con il server!");
            System.exit(1);
        }    
    }
}
    
