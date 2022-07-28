package shu.auction;

import javax.swing.*;

public class AuctionRowBidLabel {
    private Auction auction;
    private JLabel jLabel;

    public AuctionRowBidLabel (Auction auction, JLabel jLabel) {
        this.auction = auction;
        this.jLabel = jLabel;
    }

    protected Auction getAuction() {
        return auction;
    }

    protected JLabel getjLabel() {
        return jLabel;
    }
}
