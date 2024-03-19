package client;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ThreadClient implements Runnable {

    private BufferedReader cin;
    private Chat chat;

    // Constructor
    public ThreadClient(Socket socket, Chat chat) throws IOException {
        this.cin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.chat = chat;
    }

    // Metodo para recibir mensajes del servidor
    @Override
    public void run() {
        try {
            // Ciclo para recibir mensajes del servidor
            while (true) {
                // Se lee el mensaje del servidor y se imprime en la consola
                String message = cin.readLine();
                System.out.println(message);
                String[] mensajeColor = message.split("¤", 2);
                String[] mensajeNombre = mensajeColor[0].split(":", 2);
                chat.appendToPane(chat.chatBox, mensajeNombre[0], new Color(Integer.parseInt(mensajeColor[1])));
                if (mensajeNombre.length == 2) {
                    chat.appendToPane(chat.chatBox, ":" + mensajeNombre[1] + "\n", Color.BLACK);
                } else {
                    chat.appendToPane(chat.chatBox, "\n", Color.GRAY);
                }
            }
        } catch (SocketException e) {
            System.out.println("You left the chat-room");
        } catch (IOException exception) {
            System.out.println(exception);
        } finally {
            try {
                cin.close();
            } catch (Exception exception) {
                System.out.println(exception);
            }
        }
    }
}
