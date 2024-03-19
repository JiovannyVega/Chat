package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

    public static void main(String[] args) {
        // Declaracion de una lista de clientes y un HashMap para guardar los nombres de
        // los clientes relacionada con su socket
        ArrayList<Socket> clients = new ArrayList<>();
        HashMap<Socket, String> clientNameList = new HashMap<Socket, String>();
        // Metodo para iniciar el servidor
        try (ServerSocket serversocket = new ServerSocket(5000)) {
            System.out.println("El servidor se ha iniciado...");
            // Ciclo para aceptar clientes
            while (true) {
                Socket socket = serversocket.accept();
                clients.add(socket);
                // Se crea un hilo para cada cliente
                ThreadServer ThreadServer = new ThreadServer(socket, clients, clientNameList);
                ThreadServer.start();
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}
