package shu.auction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

enum AuctionTypes {
    Ebay,
    English
}

public class Auction implements Comparable<Auction>, Serializable {
    private String name;
    private Date endDate;
    private Double reservePrice;
    private Double currentBid;
    private List<User> bidders;
    private AuctionTypes auctionType;
    private Integer ID;
    private ArrayList<User> followers = new ArrayList<>();

    public Auction (Integer ID, String name, Date endDate, Double reservePrice, Double currentBid, List<User> bidders) {
        this.ID = ID;
        this.name = name;
        this.endDate = endDate;
        this.reservePrice = reservePrice;
        this.currentBid = currentBid;
        this.bidders = bidders;
    }

    public int compareTo(Auction o) {
        // Make a collection of Auction models sortable by their end date
        return getEndDate().compareTo(o.getEndDate());
    }

    public String getName () { return name; }

    public Date getEndDate () { return endDate; }

    public Double getCurrentBid () { return currentBid; }

    public Double getReservePrice () { return reservePrice; }

    protected void setCurrentBid(Double currentBid) {
        this.currentBid = currentBid;
    }

    protected void addBidder (User user) {
        // Only add each user once to the list of bidders
        User foundUser = bidders.stream().filter(bidder -> bidder.getID().equals(user.getID())).findFirst().orElse(null);
        if (foundUser == null) bidders.add(user);
    }

    protected void setAuctionType(AuctionTypes auctionType) {
        this.auctionType = auctionType;
    }

    protected AuctionTypes getAuctionType() {
        return auctionType;
    }

    protected List<User> getBidders() {
        return bidders;
    }

    protected Integer getID() {
        return ID;
    }

    protected void addFollower (User user) {
        // Only add each user once to the list of followers
        User foundUser = followers.stream().filter(follower -> follower.getID().equals(user.getID())).findFirst().orElse(null);
        if (foundUser == null) followers.add(user);
    }

    protected ArrayList<User> getFollowers() {
        return followers;
    }
}