package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Entities.*;
import utilities.*;

import static utilities.GetInfoFromDB.isUsernameTaken;

public class RegistrationForm {
    private final JFrame frame = new JFrame();
    private final JLabel nameLabel, lastNameLabel, usernameLabel, passwordLabel, confirmPasswordLabel, genderLabel, positionLabel, emailLabel;
    private final JTextField nameField, lastNameField, usernameField, emailField;
    private final JComboBox<String> cmbPosition;
    private final JCheckBox maleCheckbox, femaleCheckbox;
    private final JPasswordField passwordField, confirmPasswordField;
    private final JButton RegisterButton;
    private final ButtonGroup genderGroup;
    private final JCheckBox showPasswordCheckbox;
    private final JTable employeeTable; // Reference to table model

    // Constructor
    public RegistrationForm(JTable tableModel) {
        this.employeeTable = tableModel; // Initialize the table model reference

        frame.setTitle("Vertex Library - Create Account");
        // Closing this form should only close the form, not the whole app
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(560, 700);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        Theme.applyIcon(frame);
        frame.add(Theme.pageHeader("Create account", "Add a new staff member to Vertex Library."), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Theme.PAPER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // First Name Label
        nameLabel = new JLabel("First Name:");
        Theme.styleFieldLabel(nameLabel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(nameLabel, gbc);

        // First Name Field
      nameField = new JTextField(15);
        styleTextField(nameField);
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(nameField, gbc);

        // Last Name Label
        lastNameLabel = new JLabel("Last Name:");
        Theme.styleFieldLabel(lastNameLabel);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(lastNameLabel, gbc);

        // Last Name Field
        lastNameField = new JTextField(15);
        styleTextField(lastNameField);
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(lastNameField, gbc);

        // Username Label
        usernameLabel = new JLabel("Username:");
        Theme.styleFieldLabel(usernameLabel);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(usernameLabel, gbc);

        // Username Field
        usernameField = new JTextField(15);
        styleTextField(usernameField);
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(usernameField, gbc);

        // Email Label
        emailLabel = new JLabel("Email:");
        Theme.styleFieldLabel(emailLabel);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(emailLabel, gbc);

        // Email Field
        emailField = new JTextField(15);
        styleTextField(emailField);
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(emailField, gbc);

        // Password Label
        passwordLabel = new JLabel("Password:");
        Theme.styleFieldLabel(passwordLabel);
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(passwordLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(15);
        styleTextField(passwordField);
        gbc.gridx = 1;
        gbc.gridy = 4;
        mainPanel.add(passwordField, gbc);

        // Confirm Password Label
        confirmPasswordLabel = new JLabel("Confirm Password:");
        Theme.styleFieldLabel(confirmPasswordLabel);
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(confirmPasswordLabel, gbc);

        // Confirm Password Field
        confirmPasswordField = new JPasswordField(15);
        styleTextField(confirmPasswordField);
        gbc.gridx = 1;
        gbc.gridy = 5;
        mainPanel.add(confirmPasswordField, gbc);

        // Show Password Checkbox
        showPasswordCheckbox = new JCheckBox("Show password");
        Theme.styleCheckBox(showPasswordCheckbox);
        gbc.gridx = 1;
        gbc.gridy = 6;
        mainPanel.add(showPasswordCheckbox, gbc);

        // Gender Label
        genderLabel = new JLabel("Gender:");
        Theme.styleFieldLabel(genderLabel);
        gbc.gridx = 0;
        gbc.gridy = 7;
        mainPanel.add(genderLabel, gbc);

        // Gender Checkboxes
        maleCheckbox = new JCheckBox("Male");
        Theme.styleCheckBox(maleCheckbox);

        femaleCheckbox = new JCheckBox("Female");
        Theme.styleCheckBox(femaleCheckbox);

        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genderPanel.setOpaque(false);
        genderPanel.add(maleCheckbox);
        genderPanel.add(Box.createHorizontalStrut(16));
        genderPanel.add(femaleCheckbox);
        gbc.gridx = 1;
        gbc.gridy = 7;
        mainPanel.add(genderPanel, gbc);

        genderGroup = new ButtonGroup();
        genderGroup.add(maleCheckbox);
        genderGroup.add(femaleCheckbox);

        // Position Label
        positionLabel = new JLabel("Position:");
        Theme.styleFieldLabel(positionLabel);
        gbc.gridx = 0;
        gbc.gridy = 8;
        mainPanel.add(positionLabel, gbc);

        // Position ComboBox
        cmbPosition = new JComboBox<>(new String[]{"CEO", "Employee"});
        cmbPosition.setFont(Theme.body(14));
        cmbPosition.setBackground(Theme.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 8;
        mainPanel.add(cmbPosition, gbc);

        // Register Button
        RegisterButton = Theme.button("Create account", Theme.ButtonStyle.PRIMARY);
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.insets = new Insets(20, 8, 7, 8);
        mainPanel.add(RegisterButton, gbc);


        // Add action listeners
        RegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration(e);
            }
        });

        showPasswordCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showPasswordCheckbox.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                    confirmPasswordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('•');
                    confirmPasswordField.setEchoChar('•');
                }
            }
        });

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }


    private void styleTextField(JTextField textField) {
        Theme.styleField(textField);
    }

    private void handleRegistration(ActionEvent e){
        Employee emp = new Employee();

        String name = nameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword());
        String position = cmbPosition.getSelectedItem().toString();
        String email = emailField.getText();

        if (name.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields.");
            return;
        }

        if (!maleCheckbox.isSelected() && !femaleCheckbox.isSelected()) {
            JOptionPane.showMessageDialog(null, "Please select your gender.");
            return;
        }

        String gender = maleCheckbox.isSelected() ? "Male" : "Female";

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null, "Passwords do not match!");
            return;
        }

        // Set employee data
        emp.setName(name);
        emp.setLastName(lastName);
        emp.setUsername(username);
        emp.setPosition(position);
        emp.setGender(gender);

        if (isValidPassword(password)) {
            password = HashPass.hashPassword(password);
            emp.setPassword(password);
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Please provide a stronger password.",
                    "Password Validation Failed",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (isValidEmail(email)) {
            emp.setEmail(email);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid email address!");
            return;
        }

        InsertIntoDB db = new InsertIntoDB();
        if (isUsernameTaken(username)) {
            JOptionPane.showMessageDialog(null, "Username is already taken!");
            return;
        }

        InsertIntoDB.InsertIntoDB(emp);

        updateTable.refreshTable(employeeTable, "SELECT Name, Lastname, Username, Position FROM employees");
        int result = JOptionPane.showConfirmDialog(null, "Registration successful!", "Success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                Theme.successIcon());
        if (result == JOptionPane.OK_OPTION || result == JOptionPane.CLOSED_OPTION) {
            frame.dispose();
        }
    }
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        if (!email.contains("@") || !email.contains(".")) {
            return false;
        }

        int atIndex = email.indexOf('@');
        int dotIndex = email.lastIndexOf('.');
        return atIndex < dotIndex && atIndex != 0 && dotIndex != email.length() - 1;
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Password cannot be null or empty.");
            return false;
        }

        if (password.length() < 8) {
            JOptionPane.showMessageDialog(null, "Password must be at least 8 characters long.");
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        // Check strength score
        int strengthScore = 0;
        if (hasUppercase) strengthScore++;
        if (hasLowercase) strengthScore++;
        if (hasDigit) strengthScore++;
        if (hasSpecial) strengthScore++;
        if (password.length() >= 12) strengthScore++; // Extra point for length

        String strengthMessage;
        if (strengthScore <= 2) {
            strengthMessage = "Weak";
        } else if (strengthScore == 3) {
            strengthMessage = "Moderate";
        } else {
            strengthMessage = "Strong";
        }

        if (!strengthMessage.equals("Strong")) {
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "Your password is " + strengthMessage + ".\n" +
                            "Would you like to use it as is or create a stronger one?",
                    "Password Strength",
                    JOptionPane.YES_NO_OPTION
            );
            return choice == JOptionPane.YES_OPTION; // Proceed if user agrees
        }

        return true; // Password is strong
    }
}