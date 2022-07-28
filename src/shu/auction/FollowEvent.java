package shu.auction;

import java.io.Serializable;

public class FollowEvent implements Serializable {
    private User user;
    private Auction auction;

    public FollowEvent (User user, Auction auction) {
        this.user = user;
        this.auction = auction;
    }

    protected Auction getAuction() {
        return auction;
    }

    protected User getUser() {
        return user;
    }
}
