package shu.auction;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int serverPort = 8888;
    private ServerSocket serverSocket;
    private static List<ConnectionService> connections = new ArrayList<ConnectionService>();
    private static SecretKey secretKey;
    private static Cipher ecipher;
    private static Cipher dcipher;
    public Server() {
        try {
            secretKey = KeyGenerator.getInstance("DES").generateKey();
            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, secretKey);
            dcipher.init(Cipher.DECRYPT_MODE, secretKey);
            serverSocket = createServerSocket(serverPort);
            // Continue to accept new socket connections from clients
            while (true) {
                Socket socket = createClientSocket(serverSocket);
                // Create a new thread for the socket
                ConnectionService service = new ConnectionService(socket);
                // Create a list of sockets
                connections.add(service);
                service.start();
                System.out.println("Connected: " + socket);
            }
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
            System.err.println("Error whilst starting socket server");
            JOptionPane.showMessageDialog(GUI.getInstance().getActiveFrame(), "Error whilst starting socket server");
        }
    }

    public static ServerSocket createServerSocket (Integer serverPort) throws IOException {
        return new ServerSocket(serverPort);
    }

    public static List<ConnectionService> getConnections () {
        return connections;
    }

    public static Socket createClientSocket (ServerSocket serverSocket) throws IOException {
        return serverSocket.accept();
    }

    public static SecretKey getSecretKey () {
        return secretKey;
    }

    public static Cipher getEcipher() {
        return ecipher;
    }

    public static Cipher getDcipher() {
        return dcipher;
    }

    public static void main(String[] args) {
        new Server();
    }
}
