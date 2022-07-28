package shu.auction;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public enum GUI {
    INSTANCE;

    private AuthenticationServer authenticationServer = AuthenticationServer.getInstance();
    protected JFrame loginFrame;
    private JFrame auctionFrame;
    private JPanel columnPanel;
    private Boolean onlyShowExpiredAuctions = false;
    private JFrame activeFrame;
    private List<AuctionRowBidLabel> auctionRowBidLabels =  new ArrayList<>();
    private JFrame loadingFrame;

    GUI() {
        showLoading();
    }

    public static void main(String[] args) { }

    private void openBidWindow (Auction auction) {
        JFrame bidFrame = new JFrame();
        String biddingAmountLabelText = "Enter the amount you wish to bid (£)";
        if (auction instanceof EbayStyleAuction) biddingAmountLabelText = "Enter the maximum amount you wish to bid (£)";
        JLabel biddingAmountLabel = new JLabel(biddingAmountLabelText);
        JLabel currentBidLabel = new JLabel("Current bid: £" + auction.getCurrentBid());
        JFormattedTextField biddingAmountField = new JFormattedTextField();
        if (auction.getCurrentBid() != null && auction.getCurrentBid() > 0) {
            biddingAmountField.setValue(auction.getCurrentBid() + 1);
        } else if (auction.getReservePrice() != null && auction.getReservePrice() > 0) {
            biddingAmountField.setValue(auction.getReservePrice());
        } else biddingAmountField.setValue(1);

        JButton bidBtn = new JButton("Place Bid");
        bidBtn.addActionListener(e -> {
            Double bidAmount = null;
            try {
                bidAmount = Double.parseDouble(biddingAmountField.getText());
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(bidFrame, "You need to enter a number");
                System.out.println("Bid is not a number");
            }
            if (bidAmount > auction.getReservePrice() && bidAmount > auction.getCurrentBid()) {
                bidFrame.setVisible(false);
                if (auction instanceof EbayStyleAuction) ((EbayStyleAuction) auction).placeBid(bidAmount);
                if (auction instanceof EnglishStyleAuction) ((EnglishStyleAuction) auction).placeBid(bidAmount);
            } else System.out.println("Bid amount is not more than reserve price / current maximum bid");
        });

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(biddingAmountLabel);
        jPanel.add(currentBidLabel);
        jPanel.add(biddingAmountField);
        jPanel.add(bidBtn);
        bidFrame.add(jPanel, BorderLayout.CENTER);
        bidFrame.setTitle("Place your bid");
        bidFrame.pack();
        bidFrame.setVisible(true);
    }

    protected void createAuctionRows () {
        auctionRowBidLabels.clear();
        for (Auction auction : Client.getInstance().getMockedAuctionData()) {
            if (!onlyShowExpiredAuctions || (onlyShowExpiredAuctions && auction.getEndDate().before(Calendar.getInstance().getTime()))) {
                JPanel auctionRow = new JPanel(new GridLayout());
                JLabel auctionLabel = new JLabel(auction.getName());
                JLabel auctionCurrentBid = new JLabel("£" + auction.getCurrentBid());
                auctionRowBidLabels.add(new AuctionRowBidLabel(auction, auctionCurrentBid));
                JLabel auctionStyle = new JLabel(auction.getAuctionType() + " Style Auction");
                JButton bidBtn = new JButton("Place Bid");
                bidBtn.addActionListener(e -> openBidWindow(auction));
                long secondsLeft = auction.getEndDate().getTime() - Calendar.getInstance().getTime().getTime();
                String timeLeft = (secondsLeft > 0) ? TimeUnit.MILLISECONDS.toSeconds(secondsLeft) + "sec" : "EXPIRED";
                JLabel auctionTimeLabel = new JLabel(timeLeft);
                JButton followBtn = new JButton("Follow");
                followBtn.addActionListener(e -> authenticationServer.getAuthenticatedUser().followAuction(auction));
                if (secondsLeft <= 0) {
                    bidBtn.setEnabled(false);
                    followBtn.setEnabled(false);
                }
                auctionRow.add(auctionLabel);
                auctionRow.add(auctionTimeLabel);
                auctionRow.add(auctionStyle);
                auctionRow.add(auctionCurrentBid);
                auctionRow.add(followBtn);
                auctionRow.add(bidBtn);
                columnPanel.add(auctionRow);

                new Thread(() -> {
                    while (TimeUnit.MILLISECONDS.toSeconds(auction.getEndDate().getTime() - Calendar.getInstance().getTime().getTime()) > 0) {
                        auctionTimeLabel.setText(TimeUnit.MILLISECONDS.toSeconds(auction.getEndDate().getTime() - Calendar.getInstance().getTime().getTime()) + "sec");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    bidBtn.setEnabled(false);
                    followBtn.setEnabled(false);
                    auctionTimeLabel.setText("EXPIRED");
                }).start();
            }
        }
        columnPanel.revalidate();
        columnPanel.repaint();
    }

    private void onlyShowCompleted (Boolean showCompleted) {
        onlyShowExpiredAuctions = showCompleted;
        while (columnPanel.getComponents().length > 1) {
            columnPanel.remove(columnPanel.getComponents().length - 1);
            columnPanel.revalidate();
            columnPanel.repaint();
        }
        createAuctionRows();
    }

    private void viewFollowed () {
        JFrame followedFrame = new JFrame();
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        if (authenticationServer.getAuthenticatedUser().getFollowedAuctions().size() > 0) {
            for (Auction auction: authenticationServer.getAuthenticatedUser().getFollowedAuctions()) {
                JPanel auctionRow = new JPanel(new GridLayout());
                JLabel auctionLabel = new JLabel(auction.getName());
                JLabel auctionCurrentBid = new JLabel("£" + auction.getCurrentBid());
                auctionRowBidLabels.add(new AuctionRowBidLabel(auction, auctionCurrentBid));
                JLabel auctionStyle = new JLabel(auction.getAuctionType() + " Style Auction");
                JButton bidBtn = new JButton("Place Bid");
                bidBtn.addActionListener(e -> openBidWindow(auction));
                long secondsLeft = auction.getEndDate().getTime() - Calendar.getInstance().getTime().getTime();
                String timeLeft = (secondsLeft > 0) ? TimeUnit.MILLISECONDS.toSeconds(secondsLeft) + "sec" : "EXPIRED";
                JLabel auctionTimeLabel = new JLabel(timeLeft);
                if (secondsLeft <= 0)  bidBtn.setEnabled(false);
                auctionRow.add(auctionLabel);
                auctionRow.add(auctionTimeLabel);
                auctionRow.add(auctionStyle);
                auctionRow.add(auctionCurrentBid);
                auctionRow.add(bidBtn);
                jPanel.add(auctionRow);

                new Thread(() -> {
                    while (TimeUnit.MILLISECONDS.toSeconds(auction.getEndDate().getTime() - Calendar.getInstance().getTime().getTime()) > 0) {
                        auctionTimeLabel.setText(TimeUnit.MILLISECONDS.toSeconds(auction.getEndDate().getTime() - Calendar.getInstance().getTime().getTime()) + "sec");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    bidBtn.setEnabled(false);
                    auctionTimeLabel.setText("EXPIRED");
                }).start();
            }
        } else {
            JLabel jLabel = new JLabel("You are not following any auctions!");
            jPanel.add(jLabel);
        }
        followedFrame.add(jPanel, BorderLayout.CENTER);
        followedFrame.setTitle("Your followed auctions");
        followedFrame.pack();
        followedFrame.setVisible(true);
    }

    private void viewBiddedAuctions () {
        JFrame biddedAuctions = new JFrame();
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        if (authenticationServer.getAuthenticatedUser().getBiddedAuctions().size() > 0) {
            for (Auction auction: authenticationServer.getAuthenticatedUser().getBiddedAuctions()) {
                JPanel auctionRow = new JPanel(new GridLayout());
                JLabel auctionLabel = new JLabel(auction.getName());
                JLabel auctionCurrentBid = new JLabel("£" + auction.getCurrentBid());
                auctionRowBidLabels.add(new AuctionRowBidLabel(auction, auctionCurrentBid));
                JLabel auctionStyle = new JLabel(auction.getAuctionType() + " Style Auction");
                JButton bidBtn = new JButton("Place Bid");
                bidBtn.addActionListener(e -> openBidWindow(auction));
                long secondsLeft = auction.getEndDate().getTime() - Calendar.getInstance().getTime().getTime();
                String timeLeft = (secondsLeft > 0) ? TimeUnit.MILLISECONDS.toSeconds(secondsLeft) + "sec" : "EXPIRED";
                JLabel auctionTimeLabel = new JLabel(timeLeft);
                JButton followBtn = new JButton("Follow");
                followBtn.addActionListener(e -> authenticationServer.getAuthenticatedUser().followAuction(auction));
                if (secondsLeft <= 0) {
                    bidBtn.setEnabled(false);
                    followBtn.setEnabled(false);
                }
                auctionRow.add(auctionLabel);
                auctionRow.add(auctionTimeLabel);
                auctionRow.add(auctionStyle);
                auctionRow.add(auctionCurrentBid);
                auctionRow.add(followBtn);
                auctionRow.add(bidBtn);
                jPanel.add(auctionRow);

                new Thread(() -> {
                    while (TimeUnit.MILLISECONDS.toSeconds(auction.getEndDate().getTime() - Calendar.getInstance().getTime().getTime()) > 0) {
                        auctionTimeLabel.setText(TimeUnit.MILLISECONDS.toSeconds(auction.getEndDate().getTime() - Calendar.getInstance().getTime().getTime()) + "sec");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    bidBtn.setEnabled(false);
                    followBtn.setEnabled(false);
                    auctionTimeLabel.setText("EXPIRED");
                }).start();
            }
        } else {
            JLabel jLabel = new JLabel("You have not bidded on any auctions");
            jPanel.add(jLabel);
        }
        biddedAuctions.add(jPanel, BorderLayout.CENTER);
        biddedAuctions.setTitle("The auctions you've bidded on");
        biddedAuctions.pack();
        biddedAuctions.setVisible(true);
    }

    private void showLoading () {
        loadingFrame = new JFrame();
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel("Loading data...");
        jPanel.add(label);
        loadingFrame.add(jPanel, BorderLayout.CENTER);
        loadingFrame.setTitle("Loading...");
        loadingFrame.pack();
        loadingFrame.setVisible(true);

        Client client = Client.getInstance();
        client.createConnection();
    }


    private void sortByEndDate() {
        Collections.sort(Client.getInstance().getMockedAuctionData());
        while (columnPanel.getComponents().length > 1) {
            columnPanel.remove(columnPanel.getComponents().length - 1);
            columnPanel.revalidate();
            columnPanel.repaint();
        }
        createAuctionRows();
        System.out.println("Sorting by end date");
    }

    private void showAuctions () {
        auctionFrame = new JFrame();
        auctionFrame.setPreferredSize(new Dimension(1000, 500));
        auctionFrame.pack();
        JPanel headingRow = new JPanel(new GridLayout());
        JButton sortBtn = new JButton("Sort by End Time");
        sortBtn.addActionListener(e -> sortByEndDate());
        JButton followedAuctions = new JButton("Followed Auctions");
        followedAuctions.addActionListener(e -> viewFollowed());
        JButton myBidsBtn = new JButton("My Bids");
        myBidsBtn.addActionListener(e -> viewBiddedAuctions());
        JButton showCompletedOnly = new JButton("Only Show Completed");
        showCompletedOnly.addActionListener(e -> {
            if (showCompletedOnly.getText().equals("Only Show Completed")) {
                showCompletedOnly.setText("Show All");
                onlyShowCompleted(true);
            } else {
                showCompletedOnly.setText("Only Show Completed");
                onlyShowCompleted(false);
            }
        });
        headingRow.add(sortBtn);
        headingRow.add(followedAuctions);
        headingRow.add(myBidsBtn);
        headingRow.add(showCompletedOnly);

        columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));
        columnPanel.add(headingRow);
        createAuctionRows();
        auctionFrame.add(columnPanel, BorderLayout.CENTER);
        auctionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        auctionFrame.setTitle("Auctions");
        auctionFrame.pack();
        auctionFrame.setVisible(true);
        activeFrame = auctionFrame;
    }

    protected void showLoginUI () {
        loadingFrame.setVisible(false);
        loginFrame = new JFrame();
        loginFrame.setPreferredSize(new Dimension(400, 160));
        loginFrame.pack();
        JLabel usernameLabel = new JLabel("Username");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password");
        JPasswordField passwordField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            if (password.isBlank() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "You must enter your password");
                return;
            }
            if (username.isBlank() || username.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "You must enter your username");
                return;
            }

            if (authenticationServer.login(username, password)) {
                loginFrame.setVisible(false);
                showAuctions();
            }
        });

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(usernameLabel);
        jPanel.add(usernameField);
        jPanel.add(passwordLabel);
        jPanel.add(passwordField);
        jPanel.add(loginBtn);
        loginFrame.add(jPanel, BorderLayout.CENTER);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setTitle("Login to Auction Application");
        loginFrame.pack();
        loginFrame.setVisible(true);
        activeFrame = loginFrame;
    }

    protected static GUI getInstance() {
        return INSTANCE;
    }

    public JFrame getActiveFrame() {
        return activeFrame;
    }

    protected List<AuctionRowBidLabel> getAuctionRowBidLabels() {
        return auctionRowBidLabels;
    }
}