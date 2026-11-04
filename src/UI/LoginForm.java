package UI;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import Entities.*;
import utilities.*;

import static utilities.GetInfoFromDB.checkPosition;


/**
 * This is the class for the login form
 */
public class LoginForm extends JFrame {

    private final JFrame frame = new JFrame("Vertex Library - Login");

    private final JLabel nameLabel;
    private final JLabel passLabel;
    private final JTextField textField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JCheckBox showPasswordCheckbox;

    public LoginForm() {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 560);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        Theme.applyIcon(frame);

        // Two columns: the logo on white, the sign-in form on paper
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        JPanel brandPanel = new JPanel(new GridBagLayout());
        brandPanel.setBackground(Theme.WHITE);
        brandPanel.add(new JLabel(Theme.logo(340)));
        mainPanel.add(brandPanel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Theme.PAPER);
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 56, 0, 56));
        mainPanel.add(formPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Welcome back");
        title.setFont(Theme.heading(30));
        title.setForeground(Theme.NAVY);
        gbc.gridy = 0;
        formPanel.add(title, gbc);

        JLabel subtitle = new JLabel("Sign in to Vertex Library.");
        subtitle.setFont(Theme.body(14));
        subtitle.setForeground(Theme.SLATE);
        gbc.gridy = 1;
        gbc.insets = new Insets(6, 0, 28, 0);
        formPanel.add(subtitle, gbc);

        // Username
        nameLabel = Theme.fieldLabel("Username");
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 6, 0);
        formPanel.add(nameLabel, gbc);

        textField = new JTextField(15);
        Theme.styleField(textField);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 18, 0);
        formPanel.add(textField, gbc);

        // Password
        passLabel = Theme.fieldLabel("Password");
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 6, 0);
        formPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(15);
        Theme.styleField(passwordField);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 8, 0);
        formPanel.add(passwordField, gbc);

        // Show Password Checkbox
        showPasswordCheckbox = new JCheckBox("Show password");
        Theme.styleCheckBox(showPasswordCheckbox);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 24, 0);
        formPanel.add(showPasswordCheckbox, gbc);

        // Login Button
        loginButton = Theme.button("Log in", Theme.ButtonStyle.PRIMARY);
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(loginButton, gbc);
        frame.getRootPane().setDefaultButton(loginButton); // Enter key logs in

        // Add action listeners
        loginButton.addActionListener(e -> {
            String username = textField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and password cannot be empty.");
                return;
            }

            if (validateUser(username, password)) {
                String position =  checkPosition(username);
                User user = getUserDetails(username);

                switch (position) {
                    case "CEO" -> {
                        new CEOPage(username);
                        frame.dispose();
                    }
                    case "User" -> {
                        new UserPage(user.getName(), user.getLastName(), user.getUsername());
                        frame.dispose();
                    }
                    default -> JOptionPane.showMessageDialog(frame, "User position not recognized.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Username/Password");
            }
        });


        showPasswordCheckbox.addActionListener(e -> {
            if (showPasswordCheckbox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public boolean validateUser(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && HashPass.verifyPassword(password, rs.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during user validation", e);
        }
    }

    private User getUserDetails(String username) {
        String query = "SELECT name, lastname, username FROM users WHERE username = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getString("name"),
                            resultSet.getString("lastname"),
                            resultSet.getString("username")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while retrieving user details", e);
        }

        return null; // Explicitly return null if no user is found
    }

}
