package c_s_comunicazionemulticastudp;
import java.io.*;
import java.net.*;

public class ServerMulticast {
    //colore del prompt del Server
    public static final String ANSI_BLUE = "\u001B[34m";
    //colore del prompt del Client
    public static final String RED_BOLD = "\033[1;31m";
    //colore reset
    public static final String RESET = "\033[0m";

    public static void main(String[] args) {
        //oggetto socket UDP
        DatagramSocket dSocket = null;
        //datagramma UDP ricevuto
        DatagramPacket inPacket;
        //datagramma UDP di risposta
        DatagramPacket outPacket;
        //buffer di lettura
        byte[] inBuffer= new byte[256];
        //porta del Server
        int port=2000;
        //messaggio ricevuto 
        String messageIn;
        //messaggio da inviare
        String messageOut;

        try {
            System.out.println(ANSI_BLUE + "SERVER UDP" + RESET);
            //1) SERVER IN ASCOLTO 
            //si crea il socket e si associa alla porta specifica
            dSocket = new DatagramSocket(port);
            System.out.println(ANSI_BLUE + "Apertura porta in corso!" + RESET);

            while(true){
                //si prepara il buffer per il messaggio da ricevere
                inPacket = new DatagramPacket(inBuffer, inBuffer.length);
                dSocket.receive(inPacket);
                
                //2) RICEZIONE MESSAGGIO DAL CLIENT
                //si crea un datagramma UDP in cui trasportare il buffer per tutta la sua lunghezza
                inPacket = new DatagramPacket(inBuffer, inBuffer.length);
                //si attende il pacchetto dal client
                dSocket.receive(inPacket);
                
                //si recupera l'indirizzo IP e la porta UDP del client
                InetAddress clientAddress = inPacket.getAddress();
                int clientPort = inPacket.getPort();

                //si stampa a video il messaggio ricevuto dal client
                messageIn = new String(inPacket.getData(), 0, inPacket.getLength());
                System.out.println(RED_BOLD + "Messaggio ricevuto dal client " + clientAddress + ":" + clientPort + "\n\t" + messageIn + RESET);

                //3)RISPOSTA AL CLIENT
                //si prepara il datagramma con i dati da inviare
                messageOut = "Risposta dal server";
                outPacket = new DatagramPacket(messageOut.getBytes(), messageOut.length(), clientAddress, clientPort);
                
                //si inviano i dati
                dSocket.send(outPacket);
                System.out.println(ANSI_BLUE + "Spedito messaggio al client: " + messageOut + RESET);
                
                //4)INVIO MESSAGGIO AL GRUPPO DOPO UNA SOSPENSIONE 
                //si recupera l'IP gruppo
                InetAddress groupAddress = InetAddress.getByName("239.255.255.250");
                //si inizializza la porta del gruppo
                int groupPort = 1900;
                
                //si prepara il datagramma con i dati da inviare al gruppo
                messageOut= "Benvenuti a tutti!";
                outPacket = new DatagramPacket(messageOut.getBytes(), messageOut.length(), groupAddress, groupPort);
                
                //si inviano i dati
                dSocket.send(outPacket);
                System.out.println(ANSI_BLUE + "Spedito messaggio al gruppo: " + messageOut + RESET);
            }
        } 
        catch (BindException ex) {
            System.err.println("Porta già in uso");
        } 
        catch (UnknownHostException ex) {
            System.err.println("Errore di risoluzione");
        }
        catch (SocketException ex) {
            System.err.println("Errore di creazione del socket e apertura del server");
        }
        catch(IOException ex) {
            System.err.println(ex.getMessage());
        }
        finally{
           if (dSocket != null)
                dSocket.close();
          }
    }
}