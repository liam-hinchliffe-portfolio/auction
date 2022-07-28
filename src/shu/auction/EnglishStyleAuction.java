package shu.auction;

import javax.swing.*;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EnglishStyleAuction extends Auction {
    public EnglishStyleAuction(Integer ID, String name, Date endDate, Double reservePrice, Double currentBid, List<User> bidders) {
        super(ID, name, endDate, reservePrice, currentBid, bidders);
        this.setAuctionType(AuctionTypes.English);
    }

    protected void placeBid (Double price) {
        Client client = Client.getInstance();
        if (!client.getSocket().isClosed()) {
            if (this.getEndDate().after(Calendar.getInstance().getTime())) {
                if (price > this.getReservePrice()) {
                    if (price > this.getCurrentBid()) {
                        this.setCurrentBid(price);
                        this.addBidder(AuthenticationServer.getInstance().getAuthenticatedUser());
                        AuthenticationServer.getInstance().getAuthenticatedUser().addBiddedAuction(this);
                        Bid bid = new Bid(AuthenticationServer.getInstance().getAuthenticatedUser(), this.getCurrentBid(), null, this);
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
        }
    }
}
