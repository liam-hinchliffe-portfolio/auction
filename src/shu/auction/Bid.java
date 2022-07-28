package shu.auction;

import java.io.Serializable;

public class Bid implements Serializable {
    private User user;
    private Double currentBid;
    private Double maximumBid;
    private Auction auction;

    public Bid (User user, Double currentBid, Double maximumBid, Auction auction) {
        this.user = user;
        this.currentBid = currentBid;
        this.maximumBid = maximumBid;
        this.auction = auction;
    }

    protected Double getCurrentBid() {
        return currentBid;
    }

    protected Double getMaximumBid() {
        return maximumBid;
    }

    protected Auction getAuction() {
        return auction;
    }

    protected User getUser() {
        return user;
    }
}
