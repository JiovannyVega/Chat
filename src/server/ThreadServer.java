package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Thread for Server
 */
public class ThreadServer extends Thread {

    private Socket socket;
    private ArrayList<Socket> clients;
    private HashMap<Socket, String> clientNameList;

    // Constructor de la clase ThreadServer que recibe un socket, una lista de
    // clientes y un HashMap de clientes con su nombre relacionado con su socket
    public ThreadServer(Socket socket, ArrayList<Socket> clients, HashMap<Socket, String> clientNameList) {
        this.socket = socket;
        this.clients = clients;
        this.clientNameList = clientNameList;
    }

    // Metodo run que se ejecuta al iniciar el hilo
    @Override
    public void run() {
        try {
            // Se crea un BufferedReader para leer los mensajes que envia el cliente
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Ciclo para leer los mensajes que envia el cliente
            while (true) {
                // Se lee el mensaje que envia el cliente
                String outputString = input.readLine();
                if (outputString.equals("logout")) {
                    throw new SocketException();
                }
                // Si el cliente no tiene un nombre se guarda en el HashMap
                if (!clientNameList.containsKey(socket)) {
                    String[] messageString = outputString.split(":", 2);
                    clientNameList.put(socket, messageString[0]);
                    System.out.println(messageString[0] + messageString[1]);
                    showMessageToAllClients(socket, messageString[0] + messageString[1]);
                } else {
                    System.out.println(outputString);
                    showMessageToAllClients(socket, outputString);
                }
            }
        } catch (SocketException e) {
            // Si el cliente se desconecta se imprime un mensaje y se elimina de la lista de
            // clientes
            String printMessage = clientNameList.get(socket) + " ha abandonado el chat¤-8355712";
            System.out.println(printMessage);
            showMessageToAllClients(socket, printMessage);
            clients.remove(socket);
            clientNameList.remove(socket);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    // Metodo para enviar un mensaje a todos los clientes
    private void showMessageToAllClients(Socket sender, String outputString) {
        Socket socket;
        PrintWriter printWriter;
        int i = 0;
        while (i < clients.size()) {
            socket = clients.get(i);
            i++;
            try {
                if (socket != sender) {
                    printWriter = new PrintWriter(socket.getOutputStream(), true);
                    printWriter.println(outputString);
                }
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }
}
