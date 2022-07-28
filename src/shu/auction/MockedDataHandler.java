package shu.auction;

import java.util.*;

public class MockedDataHandler {
    private static MockedDataHandler single_instance = null;
    private List<Auction> mockedAuctions = new ArrayList<>(Arrays.asList(
        new EbayStyleAuction(1,"Motorbike", new Date(Calendar.getInstance().getTimeInMillis() + (1 * 60 * 1000)), 10.00, 15.00, 20.00, new ArrayList<>()),
        new EnglishStyleAuction(2, "Mobile Phone", new Date(Calendar.getInstance().getTimeInMillis() + (3 * 60 * 1000)), 10.00, 15.00, new ArrayList<>()),
        new EnglishStyleAuction(3, "Keyboard", new Date(Calendar.getInstance().getTimeInMillis() + (2 * 60 * 1000)), 10.00, 15.0, new ArrayList<>()),
        new EbayStyleAuction(4, "Hammer", new Date(Calendar.getInstance().getTimeInMillis() - (2 * 60 * 1000)), 10.00, 15.00, 20.00, new ArrayList<>())
    ));
    private List<User> mockedUsers = new ArrayList<>(Arrays.asList(
        new User(1, "user", "password", new ArrayList<>(), new ArrayList<>()),
        new User(2, "userTwo", "password", new ArrayList<>(), new ArrayList<>())
    ));

    protected List<Auction> getMockedAuctions() {
        return mockedAuctions;
    }

    protected List<User> getMockedUsers() {
        return mockedUsers;
    }

    protected static MockedDataHandler getInstance() {
        if (single_instance == null) single_instance = new MockedDataHandler();

        return single_instance;
    }
}
