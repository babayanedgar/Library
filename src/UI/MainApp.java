package UI;
/**
 *
 *
 * The MainApp class serves as the entry point for the library management application.
 * 
 * This class initializes the application by launching the login form, where users 
 * can log in to access the system.
 *
 * Copyright (c) 2025. Author - Edgar Babayan
 *
 */
public class MainApp {

    /**
     * The main method is the starting point of the application.
     * It initializes the LoginForm to authenticate the user.
     * 
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Apply the Vertex Library theme, then launch the login form as the first screen
        Theme.install();
        new LoginForm();
    }
}
