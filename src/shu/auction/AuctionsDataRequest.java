package shu.auction;

import java.io.Serializable;
import java.util.ArrayList;

public class AuctionsDataRequest implements Serializable {
    private ArrayList<Auction> response;

    protected void setResponse(ArrayList<Auction> response) {
        this.response = response;
    }

    protected ArrayList<Auction> getResponse() {
        return response;
    }
}
