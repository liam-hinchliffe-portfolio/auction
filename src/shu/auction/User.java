package shu.auction;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String username;
    private String password;
    private List<Auction> followedAuctions;
    private List<Auction> biddedAuctions;
    private Double currentBid = null;
    private Double currentMaximumBid = null;
    private Integer ID;

    public User (Integer ID, String username, String password, List<Auction> followedAuctions, List<Auction> biddedAuctions) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.followedAuctions = followedAuctions;
        this.biddedAuctions = biddedAuctions;
    }

    protected void followAuction (Auction auction) {
        followedAuctions.add(auction);
        auction.addFollower(this);

        Client client = Client.getInstance();
        if (!client.getSocket().isClosed()) {
            try {
                client.getObjectOutputStream().writeObject(new FollowEvent(AuthenticationServer.getInstance().getAuthenticatedUser(), auction));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void addBiddedAuction (Auction auction) {
        // Only add each auction to a user once
        Auction foundAuction = biddedAuctions.stream().filter(auctionObj -> auctionObj.getID().equals(auction.getID())).findFirst().orElse(null);
        if (foundAuction == null) biddedAuctions.add(auction);
    }

    protected String getUsername () { return username; }
    protected String getPassword () { return password; }
    protected List<Auction> getFollowedAuctions () { return followedAuctions; }
    protected List<Auction> getBiddedAuctions() { return biddedAuctions; }

    protected Integer getID() {
        return ID;
    }
}
