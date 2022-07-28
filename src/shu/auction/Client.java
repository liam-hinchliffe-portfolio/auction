package shu.auction;

import javax.crypto.*;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Client {
    private static Client single_instance = null;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private ArrayList<Auction> mockedAuctionData;
    private ArrayList<User> mockedUserData;
    private SecretKey secretKey;
    private static Cipher ecipher;
    private static Cipher dcipher;

    protected static Client getInstance() {
        if (single_instance == null) single_instance = new Client();
        return single_instance;
    }

    protected void createConnection () {
        try {
            socket = new Socket("localhost", 8888);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException e) {
            System.err.println("Could not connect to socket server - make sure the socket server is running");
            e.printStackTrace();
            JOptionPane.showMessageDialog(GUI.getInstance().getActiveFrame(), "Could not connect to socket server - make sure the socket server is running");
        }

        // Listen for responses from server on a separate thread
        new Thread(() -> receiveData()).start();
    }

    private void receiveData() {
        try {
            if (objectInputStream != null) {
                while (true) {
                    Object obj = objectInputStream.readObject();
                    Object decryptedObj = null;
                    decryptedObj = (obj instanceof SealedObject && dcipher != null) ? ((SealedObject) obj).getObject(dcipher) : obj;

                    // A bid has been received
                    if (decryptedObj instanceof Bid) {
                        Bid bid = (Bid) decryptedObj;
                        // Find the auction that has been bidded on
                        Auction biddedAuction = Client.getInstance().getMockedAuctionData().stream().filter(auction -> auction.getID().equals(bid.getAuction().getID())).findFirst().orElse(null);
                        if (biddedAuction != null) {
                            // Update maximum bid (for Ebay-style auctions)
                            if (biddedAuction instanceof EbayStyleAuction) ((EbayStyleAuction) biddedAuction).setCurrentMaximumBid(bid.getMaximumBid());
                            // Update the current active bid on the auction
                            biddedAuction.setCurrentBid(bid.getCurrentBid());
                            // Track who has bidded on the auction
                            biddedAuction.addBidder(bid.getUser());
                        }

                        // Notify bidders of new bid
                        User clientIsBidder = biddedAuction.getBidders().stream().filter(bidder -> bidder.getID().equals(AuthenticationServer.getInstance().getAuthenticatedUser().getID())).findFirst().orElse(null);
                        if (clientIsBidder != null) {
                            if (bid.getUser().getID().equals(clientIsBidder.getID())) {
                                JOptionPane.showMessageDialog(GUI.getInstance().getActiveFrame(), "You have placed a bid on " + biddedAuction.getName() + " for £" + biddedAuction.getCurrentBid());
                            } else JOptionPane.showMessageDialog(GUI.getInstance().getActiveFrame(), "You have been outbid on " + biddedAuction.getName() + " for £" + biddedAuction.getCurrentBid());
                        } else {
                            // If the user has not bidded on the auction but has followed the auction
                            User isFollower = biddedAuction.getFollowers().stream().filter(follower -> follower.getID().equals(AuthenticationServer.getInstance().getAuthenticatedUser().getID())).findFirst().orElse(null);
                            if (isFollower != null) JOptionPane.showMessageDialog(GUI.getInstance().getActiveFrame(), "You are following an auction (" + biddedAuction.getName() + ") which has been bidded on for £" + biddedAuction.getCurrentBid());
                        }
                        // Update UI
                        AuctionRowBidLabel auctionRowBidLabelEl = GUI.getInstance().getAuctionRowBidLabels().stream().filter(auctionRowBidLabel -> auctionRowBidLabel.getAuction().getID().equals(biddedAuction.getID())).findFirst().orElse(null);
                        if (auctionRowBidLabelEl != null) auctionRowBidLabelEl.getjLabel().setText("£" + biddedAuction.getCurrentBid());
                    } else if (decryptedObj instanceof AuctionsDataRequest) {
                        if (mockedAuctionData == null) {
                            mockedAuctionData = ((AuctionsDataRequest) decryptedObj).getResponse();
                            if (mockedUserData != null) GUI.getInstance().showLoginUI();
                        } else mockedAuctionData = ((AuctionsDataRequest) decryptedObj).getResponse();
                    } else if (decryptedObj instanceof UsersDataRequest) {
                        if (mockedUserData == null) {
                            mockedUserData = ((UsersDataRequest) decryptedObj).getResponse();
                            if (mockedAuctionData != null) GUI.getInstance().showLoginUI();
                        } else mockedUserData = ((UsersDataRequest) decryptedObj).getResponse();
                    } else if (decryptedObj instanceof FollowEvent) {
                        Object finalDecryptedObj1 = decryptedObj;
                        Auction auction = mockedAuctionData.stream().filter(auctionObj -> auctionObj.getID().equals(((FollowEvent) finalDecryptedObj1).getAuction().getID())).findFirst().orElse(null);
                        Object finalDecryptedObj = decryptedObj;
                        User user = mockedUserData.stream().filter(userObj -> userObj.getID().equals(((FollowEvent) finalDecryptedObj).getUser().getID())).findFirst().orElse(null);
                        if (user != null) auction.addFollower(user);
                    } else if (decryptedObj instanceof SecretKey) {
                        secretKey = (SecretKey) decryptedObj;
                        ecipher = Cipher.getInstance("DES");
                        dcipher = Cipher.getInstance("DES");
                        ecipher.init(Cipher.ENCRYPT_MODE, secretKey);
                        dcipher.init(Cipher.DECRYPT_MODE, secretKey);

                        if (!socket.isClosed()) {
                            try {
                                SealedObject sealedObject = new SealedObject(new AuctionsDataRequest(), ecipher);
                                getObjectOutputStream().writeObject(sealedObject);
                                sealedObject = new SealedObject(new UsersDataRequest(), Client.getEcipher());
                                getObjectOutputStream().writeObject(sealedObject);
                            } catch (IOException | IllegalBlockSizeException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("Error whilst receiving data from server");
            JOptionPane.showMessageDialog(GUI.getInstance().getActiveFrame(), "Error whilst receiving data from server");
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    protected ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    protected ArrayList<Auction> getMockedAuctionData() {
        return mockedAuctionData;
    }

    protected ArrayList<User> getMockedUserData() {
        return mockedUserData;
    }

    public static Cipher getEcipher() {
        return ecipher;
    }
}
