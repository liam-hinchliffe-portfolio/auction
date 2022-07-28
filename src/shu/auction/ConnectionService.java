package shu.auction;

import javax.crypto.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ConnectionService extends Thread {
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private MockedDataHandler mockedDataHandler = MockedDataHandler.getInstance();

    public ConnectionService(Socket socket) {
        try {
            objectOutputStream = createOutputStream(socket);
            objectInputStream = createInputStream(socket);
            objectOutputStream.writeObject(Server.getSecretKey());
        } catch (IOException e) {
            System.err.println("Error creating input / output stream for ConnectionService instace");
        }
    }

    public static ObjectOutputStream createOutputStream (Socket socket) throws IOException {
        return new ObjectOutputStream(socket.getOutputStream());
    }

    public static ObjectInputStream createInputStream (Socket socket) throws IOException {
        return new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object obj;
                while ((obj = objectInputStream.readObject()) != null) {
                    Object decryptedObj = null;
                    decryptedObj = (obj instanceof SealedObject && Server.getDcipher() != null) ? ((SealedObject) obj).getObject(Server.getDcipher()) : obj;

                    System.out.println("Server received" + decryptedObj);
                    if (decryptedObj instanceof AuctionsDataRequest) {
                        ((AuctionsDataRequest) decryptedObj).setResponse((ArrayList<Auction>) mockedDataHandler.getMockedAuctions());
                    } else if (decryptedObj instanceof UsersDataRequest) {
                        ((UsersDataRequest) decryptedObj).setResponse((ArrayList<User>) mockedDataHandler.getMockedUsers());
                    }
                    this.sendToAnyone(decryptedObj);
                }
            } catch (SocketException e) {}
            catch (IOException | NullPointerException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }
        }
    }

    protected void sendMessage(Object object) {
        try {
            SealedObject sealedObject = new SealedObject((Serializable) object, Server.getEcipher());
            objectOutputStream.writeObject(sealedObject);
        } catch (IOException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    private void sendToAnyone(Object object) {
        for (ConnectionService connection : Server.getConnections()) connection.sendMessage(object);
    }
}