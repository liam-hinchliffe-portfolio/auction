package shu.auction;

import javax.swing.*;

public class AuthenticationServer {
    private static AuthenticationServer single_instance = null;
    private boolean isAuthenticated = false;
    private User authenticatedUser = null;

    protected static AuthenticationServer getInstance() {
        if (single_instance == null) single_instance = new AuthenticationServer();

        return single_instance;
    }

    protected boolean login (String username, String password) {
        for (User user: Client.getInstance().getMockedUserData()) {
            if (user.getUsername().equals(username)) {
                if (user.getPassword().equals(password)) {
                    isAuthenticated = true;
                    authenticatedUser = user;
                    return true;
                }
            }
        }

        // If the user cannot be authenticated
        GUI gui = GUI.getInstance();
        JOptionPane.showMessageDialog(gui.loginFrame, "The login credentials you have entered are invalid");
        isAuthenticated = false;
        return false;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}
