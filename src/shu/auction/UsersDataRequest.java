package shu.auction;

import java.io.Serializable;
import java.util.ArrayList;

public class UsersDataRequest implements Serializable {
    private ArrayList<User> response;

    protected void setResponse(ArrayList<User> response) {
        this.response = response;
    }

    protected ArrayList<User> getResponse() {
        return response;
    }
}
