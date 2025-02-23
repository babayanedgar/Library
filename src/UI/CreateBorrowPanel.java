/**
 * Copyright (c) 2025. Author - Edgar Babayan
 *
 */

package UI;
import java.awt.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;
import javax.swing.*;
import utilities.*;
import com.toedter.calendar.JDateChooser;

import static utilities.GetInfoFromDB.*;

/**
 * The CreateBorrowPanel class provides a user interface to handle the process of borrowing books.
 * Features include:
 * - Searching for a book by ID and checking its availability.
 * - Borrowing a book by providing user and book details.
 * - Selecting a return date for the borrowed book.
 * 
 * This panel interacts with a database to retrieve and update book availability
 * and manage borrow transactions.
 */
public class CreateBorrowPanel {

    // UI components for borrowing books

    private JTextField BookID, idTextField = new JTextField(15); // Input field for the book's ID
    private JLabel bookid,idLabel = new JLabel("Enter User ID:"),status; // Label for "Book ID"
    private JButton searchButton,borrowBookButton,backButton; // Button to search for a book

    public static JFrame confirmFrame = new JFrame("ConfirmBorrow"); // Frame for borrow confirmation

    /**
     * Creates the main BorrowPanel which includes:
     * - Input fields for book details (name and ID).
     * - Buttons to search and borrow books.
     * - A status display to indicate availability.
     * @return A JPanel containing the borrow interface.
     */
    JPanel BorrowPanel() {
        // Main panel configuration
        JPanel BorrowPanel = new JPanel();
        BorrowPanel.setLayout(new BoxLayout(BorrowPanel, BoxLayout.Y_AXIS)); // Vertical layout
        BorrowPanel.setBackground(new Color(240, 248, 255)); // Light blue background

        // Panel for holding search and borrow buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Centered buttons with spacing
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.setPreferredSize(new Dimension(300, 50));
        buttonPanel.setMaximumSize(new Dimension(300, 50));



        // Label and TextField for Book ID
        bookid = new JLabel("Book ID:");
        bookid.setFont(new Font("Roboto", Font.BOLD, 16));
        bookid.setForeground(new Color(33, 37, 41));
        bookid.setAlignmentX(Component.CENTER_ALIGNMENT);

        BookID = new JTextField();
        BookID.setFont(new Font("Roboto", Font.PLAIN, 16));
        BookID.setForeground(new Color(33, 37, 41));
        BookID.setBackground(Color.WHITE);
        BookID.setAlignmentX(Component.CENTER_ALIGNMENT);
        BookID.setPreferredSize(new Dimension(200, 30));
        BookID.setMaximumSize(new Dimension(200, 30));

        // Label to display the availability status of the book
        status = new JLabel("Status:");
        status.setFont(new Font("Roboto", Font.BOLD, 16));
        status.setForeground(new Color(33, 37, 41));
        status.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Back button configuration
        backButton = new JButton("Back");
        backButton.setFont(new Font("Roboto", Font.BOLD, 16));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(33, 37, 41));
        backButton.setPreferredSize(new Dimension(150, 45));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            // Navigate back to the LibraryPage
            LibraryPage.frame.getContentPane().removeAll();
            LibraryPage.frame.add(LibraryPage.panel, BorderLayout.CENTER);
            LibraryPage.frame.revalidate();
            LibraryPage.frame.repaint();
        });

        // Search button configuration
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Roboto", Font.BOLD, 16));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(new Color(33, 37, 41));
        searchButton.setPreferredSize(new Dimension(120, 40));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> {
            String enteredBookID = BookID.getText().trim();
            if (enteredBookID.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Enter the book ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if the book is available
            boolean isBookAvailable = checkBookAvailability(enteredBookID);
            status.setText(isBookAvailable ? "Available" : "Unavailable" );
        });

        // Borrow button configuration
        borrowBookButton = new JButton("Borrow Book");
        borrowBookButton.setFont(new Font("Roboto", Font.BOLD, 16));
        borrowBookButton.setForeground(Color.WHITE);
        borrowBookButton.setBackground(new Color(33, 37, 41));
        borrowBookButton.setPreferredSize(new Dimension(160, 40));
        borrowBookButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        borrowBookButton.addActionListener(e -> {
            String enteredBookID = BookID.getText().trim();
            if (enteredBookID.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Enter the book ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean isAvailable = checkBookAvailability(enteredBookID);
            if (isAvailable) {
                try {
                    confirmBorrow();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Book is unavailable", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add components to the button panel
        buttonPanel.add(searchButton);
        buttonPanel.add(borrowBookButton);

        // Add components to the main BorrowPanel
        BorrowPanel.add(Box.createVerticalStrut(10));
        BorrowPanel.add(bookid);
        BorrowPanel.add(BookID);
        BorrowPanel.add(Box.createVerticalStrut(20));
        BorrowPanel.add(buttonPanel);
        BorrowPanel.add(Box.createVerticalStrut(20));
        BorrowPanel.add(status);
        BorrowPanel.add(Box.createVerticalStrut(20));

        JPanel backPanel = new JPanel();
        backPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        backPanel.setBackground(new Color(240, 248, 255));
        backPanel.add(backButton);
        BorrowPanel.add(backPanel);


        return BorrowPanel;
    }


    /**
     * Opens a confirmation window to complete the borrowing process.
     * Allows the user to select a return date and confirms the transaction.
     * @throws SQLException if an error occurs during database interaction.
     */
    private void confirmBorrow() throws SQLException {
        confirmFrame.setSize(400, 250); // Slightly larger to accommodate spacing
        confirmFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        confirmFrame.setLayout(new GridBagLayout()); // Use GridBagLayout for better control
        confirmFrame.setResizable(false);

        // Create components
        JLabel dateLabel = new JLabel("Select Return Date:");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Larger font for label
        JDateChooser returnDateChooser = new JDateChooser();
        returnDateChooser.setDateFormatString("dd-MM-yyyy"); // Set date format

        JButton confirmButton = new JButton("Confirm");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14)); // Larger font for button
        confirmButton.setBackground(new Color(70, 130, 180)); // Blue background
        confirmButton.setForeground(Color.WHITE); // White text
        confirmButton.setFocusPainted(false); // Remove focus border
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover

        // Add action listener to the confirm button
        confirmButton.addActionListener(e -> {
            int userID = Integer.parseInt(idTextField.getText().trim());
            int bookID = Integer.parseInt(BookID.getText().trim());

            Date utilDate = returnDateChooser.getDate(); // Get selected date
            if (utilDate == null) {
                JOptionPane.showMessageDialog(confirmFrame, "Please select a return date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            java.sql.Date returnDate = new java.sql.Date(utilDate.getTime());

            // Load checkmark icon
            ImageIcon checkmarkIcon = new ImageIcon(getClass().getResource("/checkmark.png"));

            try {
                // Insert into database
                InsertIntoDB.borrowBookWithDate(LibraryPage.frame, userID, returnDate, bookID, getBookName(bookID), getuserName(userID));
                JOptionPane.showMessageDialog(confirmFrame, "Book borrowed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE, checkmarkIcon);

                confirmFrame.dispose(); // Close the confirmation window
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(confirmFrame, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // GridBagConstraints for layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make components expand horizontally

        // Add User ID label and text field
        gbc.gridx = 0;
        gbc.gridy = 0;
        confirmFrame.add(idLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        confirmFrame.add(idTextField, gbc);

        // Add Book ID label and text field
        gbc.gridx = 0;
        gbc.gridy = 1;
        confirmFrame.add(bookid, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        confirmFrame.add(BookID, gbc);

        // Add Return Date label and date chooser
        gbc.gridx = 0;
        gbc.gridy = 2;
        confirmFrame.add(dateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        confirmFrame.add(returnDateChooser, gbc);

        // Add Confirm button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        confirmFrame.add(confirmButton, gbc);

        // Center the confirmation window relative to the main frame
        confirmFrame.setLocationRelativeTo(LibraryPage.frame);
        confirmFrame.setVisible(true);
    }



}
