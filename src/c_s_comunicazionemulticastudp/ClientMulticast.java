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
        //socket UDP
        DatagramSocket dSocket = null;
        //socket multicast UDP
        MulticastSocket mSocket = null;
        //Datagramma UDP di risposta ricevuto
        DatagramPacket inPacket;
        //Datagramma UDP con la richiesta da inviare
        DatagramPacket outPacket;
        //numero di porta server
        int port = 2000;
        //numero di porta gruppo
        int groupPort = 1900;
        //indirizzo del server
        InetAddress serverAddress;
        //indirizzo gruppo multicast UDP
        InetAddress group;

        //buffer di lettura
        byte[] inBuffer= new byte[256];
        byte[] inBufferG = new byte[1024];

        //messaggio di richiesta
        String messageOut = "Richiesta comunicazione";
        //messaggio di risposta
        String messageIn;

        try {    
            System.out.println(RED_BOLD + "CLIENT UDP" + RESET);
            //1) RICHIESTA AL SERVER
            //si recupera l'IP del server UDP
            serverAddress = InetAddress.getLocalHost();
            System.out.println(RED_BOLD + "Indirizzo del server trovato!" + RESET);

            //istanza del socket UDP per la prima comunicazione con il server
            dSocket = new DatagramSocket();
            
            //si prepara il datagramma con i dati da inviare
            outPacket = new DatagramPacket(messageOut.getBytes(), messageOut.length(), serverAddress, port);
            
            //si inviano i dati
            dSocket.send(outPacket);
            System.out.println(RED_BOLD + "Richiesta al server inviata!" + RESET);

            //2) RISPOSTA DEL SERVER
            //si prepara il datagramma per ricevere dati dal server
            inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            dSocket.receive(inPacket);
            
            //lettura del messaggio ricevuto e sua visualizzazione
            messageOut = new String(inPacket.getData(),0,inPacket.getLength());
            System.out.println(ANSI_BLUE + "Lettura dei dati ricevuti dal server" + RESET);

            messageIn = new String(inPacket.getData(), 0, inPacket.getLength());
            System.out.println(ANSI_BLUE +"Messaggio ricevuto dal server " + serverAddress + ":" + port + "\n\t" + messageIn + RESET);

            //3) RICEZIONE MESSAGGIO DEL GRUPPO
            //istanza del Multicast socket e unione al gruppo
            group = InetAddress.getByName("239.255.255.250");
            mSocket = new MulticastSocket(groupPort);
            mSocket.joinGroup(group);

            //si prepara il datagramma per ricevere dati dal gruppo
            inPacket = new DatagramPacket(inBuffer,inBuffer.length);
            mSocket.receive(inPacket); 

            //lettura del messaggio ricevuto e sua visualizzazione
            messageIn = new String(inPacket.getData(),0, inPacket.getLength());
            
            System.out.println(GREEN_UNDERLINED + "Lettura dei dati ricevuti dai partecipanti al gruppo" + RESET);
            System.out.println(GREEN_UNDERLINED + "Messaggio ricevuto dal gruppo " + group + ":" + groupPort + "\n\t" + messageIn + RESET);

            //uscita dal gruppo
            mSocket.leaveGroup(group);
        } 
        catch (UnknownHostException ex) {
                    System.err.println("Errore di risoluzione");
                } 
        catch (SocketException ex) {
                    System.err.println("Errore di creazione socket");
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("Errore durante la comunicazione con il server!");
            System.exit(1);
        }    
        finally{
                    if (dSocket != null)
                        dSocket.close();
                    if (mSocket != null)
                        mSocket.close();
        }
    }
}
    
