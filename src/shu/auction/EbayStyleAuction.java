package shu.auction;

import javax.swing.*;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EbayStyleAuction extends Auction {
    private Double currentMaximumBid;

    public EbayStyleAuction(Integer ID, String name, Date endDate, Double reservePrice, Double currentBid, Double currentMaximumBid, List<User> bidders) {
        super(ID, name, endDate, reservePrice, currentBid, bidders);
        this.currentMaximumBid = currentMaximumBid;
        this.setAuctionType(AuctionTypes.Ebay);
    }

    public Double getCurrentMaximumBid() {
        return currentMaximumBid;
    }

    protected void setCurrentMaximumBid(Double currentMaximumBid) {
        this.currentMaximumBid = currentMaximumBid;
    }

    protected void placeBid(Double price) {
        Client client = Client.getInstance();
        if (!client.getSocket().isClosed()) {
            if (this.getEndDate().after(Calendar.getInstance().getTime())) {
                if (price > this.getReservePrice()) {
                    if (price > this.getCurrentBid()) {
                        Double startingBid = this.getCurrentBid();
                        // Increment the bid until it beats the current maximum bid or goes up to the bid the user has entered
                        while (startingBid < price && startingBid < this.getCurrentMaximumBid()) startingBid += 0.5;
                        // Do not let the system auto-increment to a bid more than the user entered
                        if (startingBid > price) startingBid = price;

                        Double priceBeforeMaxBidIncrement = startingBid;
                        // If the auction's current maximum bid is more than the user's maximum bid, increment
                        while (priceBeforeMaxBidIncrement < this.getCurrentMaximumBid() && priceBeforeMaxBidIncrement < startingBid) priceBeforeMaxBidIncrement += 0.5;
                        if (priceBeforeMaxBidIncrement > this.getCurrentMaximumBid()) priceBeforeMaxBidIncrement = this.getCurrentMaximumBid();
                        this.setCurrentBid(priceBeforeMaxBidIncrement);
                        if (price > this.getCurrentMaximumBid()) this.setCurrentMaximumBid(price);
                        System.out.println("Current maximum bid: " + this.getCurrentMaximumBid());
                        this.addBidder(AuthenticationServer.getInstance().getAuthenticatedUser());
                        AuthenticationServer.getInstance().getAuthenticatedUser().addBiddedAuction(this);
                        Bid bid = new Bid(AuthenticationServer.getInstance().getAuthenticatedUser(), this.getCurrentBid(), this.getCurrentMaximumBid(), this);
                        try {
                            client.getObjectOutputStream().writeObject(bid);
                        } catch (IOException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(GUI.getInstance().getActiveFrame(), "There was an error placing your bid. Please try again");
                        }
                    } else
                        JOptionPane.showMessageDialog(GUI.getInstance().getActiveFrame(), "Your bid is not higher than the current bid (£" + this.getCurrentBid() + ")");
                } else
                    JOptionPane.showMessageDialog(GUI.getInstance().getActiveFrame(), "Your bid is below the item's reserve price (£" + this.getReservePrice() + ")");
            } else
                JOptionPane.showMessageDialog(GUI.getInstance().getActiveFrame(), "The auction has expired - your bid has not been placed");
        } else
            System.out.println("Socket is closed");
    }
}