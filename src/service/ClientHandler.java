package service;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class handle every client to broadcast their messages, also remove clients from the client list.
 *
 * @autor Paula Sanchez
 * @autor Diana Neira
 * @autor Ramon Barrios
 */
public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    /**
     * Constructor for the ClientHandler Class
     * when a client is created the server prints it
     *
     * @param socket
     */
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            System.out.println(clientUsername + " ha entrado al chat");
            broadcastMessage("SERVER: " + clientUsername + " ha entrado al chat!");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * run method overridden as this class implements the runnable interface
     * is executed when we start the thread, as the {@link Thread} class receives a runnable object
     * read the message from the client and call broadcastMessage method
     */
    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient.equalsIgnoreCase(clientUsername + ": chao")) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    /**
     * send the message to other clients except for the same client who is sending
     * does not return something (void)
     *
     * @param messageToSend message that will be sent to other clients
     */

    private void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    /**
     * remove the client and let other clients know that this client has abandoned the chat
     * the server also will print that this client has abandoned the chat
     * does not return something (void)
     */
    public void removeClientHandler() {
        clientHandlers.remove(this);
        System.out.println(clientUsername + " ha abandonado el chat");
        broadcastMessage("SERVER: " + clientUsername + " ha abandonado el chat!");
    }

    /**
     * calls the close method and remove the client handler
     * does not return something (void)
     *
     * @param socket         socket object
     * @param bufferedReader
     * @param bufferedWriter
     */
    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        Client.close(socket, bufferedReader, bufferedWriter);
    }

}
