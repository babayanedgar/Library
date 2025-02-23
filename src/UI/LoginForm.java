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

    private final JFrame frame = new JFrame("Login Form");

    private final JLabel nameLabel;
    private final JLabel passLabel;
    private final JTextField textField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JCheckBox showPasswordCheckbox;

    public LoginForm() {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
       // ImageIcon backgroundImage = new ImageIcon("backgroundLogin.jpg");

        java.net.URL logoOneUrl = getClass().getResource("/backgroundLogin.jpg");
        ImageIcon logoOne = new ImageIcon(logoOneUrl );
        // Custom Panel with Gradient Background
        JPanel mainPanel = new JPanel() {
           @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(logoOne.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Constraints for positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label
        nameLabel = new JLabel("Username:");
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(nameLabel, gbc);

        // Username Field
        textField = new JTextField(15);
        textField.setOpaque(false);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setForeground(Color.BLACK);
        textField.setBackground(new Color(0, 0, 0, 0));
        textField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(textField, gbc);

        // Password Label
        passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.BLACK);
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(passLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(15);
        passwordField.setOpaque(false);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setForeground(Color.BLACK);
        passwordField.setBackground(new Color(0, 0, 0, 0));
        passwordField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(passwordField, gbc);

        // Show Password Checkbox
        showPasswordCheckbox = new JCheckBox("Show Password");
        showPasswordCheckbox.setForeground(Color.BLACK);
        showPasswordCheckbox.setOpaque(false);
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(showPasswordCheckbox, gbc);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(30, 144, 255));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover Effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(0, 102, 204));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(30, 144, 255));
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(loginButton, gbc);

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
                Employee employee = getUserDetails(username);

                switch (position) {
                    case "CEO" -> {
                        new CEOPage(username);
                        frame.dispose();
                    }
                    case "Employee" -> {
                        new EmployeePage(employee.getName(), employee.getLastName(), employee.getUsername());
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
        String query = "SELECT password FROM employees WHERE username = ?";

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

    private Employee getUserDetails(String username) {
        String query = "SELECT name, lastname, username FROM employees WHERE username = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return new Employee(
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
