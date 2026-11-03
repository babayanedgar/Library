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
        BorrowPanel.setBackground(Theme.PAPER);

        JPanel header = Theme.pageHeader("Borrow a book", "Look up a book by its ID, then borrow it for a member.");
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, header.getPreferredSize().height));

        // Panel for holding search and borrow buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Centered buttons with spacing
        buttonPanel.setBackground(Theme.PAPER);
        buttonPanel.setPreferredSize(new Dimension(360, 64));
        buttonPanel.setMaximumSize(new Dimension(360, 64));



        // Label and TextField for Book ID
        bookid = Theme.fieldLabel("Book ID");
        bookid.setFont(Theme.strong(14));
        bookid.setAlignmentX(Component.CENTER_ALIGNMENT);

        BookID = new JTextField();
        Theme.styleField(BookID);
        BookID.setAlignmentX(Component.CENTER_ALIGNMENT);
        BookID.setPreferredSize(new Dimension(240, 40));
        BookID.setMaximumSize(new Dimension(240, 40));

        // Label to display the availability status of the book
        status = new JLabel("Status: search for a book");
        status.setFont(Theme.strong(15));
        status.setForeground(Theme.SLATE);
        status.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Back button configuration
        backButton = Theme.button("Back", Theme.ButtonStyle.QUIET);
        backButton.addActionListener(e -> {
            // Navigate back to the LibraryPage
            LibraryPage.frame.getContentPane().removeAll();
            LibraryPage.frame.add(LibraryPage.panel, BorderLayout.CENTER);
            LibraryPage.frame.revalidate();
            LibraryPage.frame.repaint();
        });

        // Search button configuration
        searchButton = Theme.button("Search", Theme.ButtonStyle.SECONDARY);
        searchButton.addActionListener(e -> {
            String enteredBookID = BookID.getText().trim();
            if (enteredBookID.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Enter the book ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if the book is available
            boolean isBookAvailable = checkBookAvailability(enteredBookID);
            status.setText(isBookAvailable ? "●  Available" : "●  Unavailable" );
            status.setForeground(isBookAvailable ? Theme.AVAILABLE : Theme.ROYAL);
        });

        // Borrow button configuration
        borrowBookButton = Theme.button("Borrow book", Theme.ButtonStyle.PRIMARY);
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
        BorrowPanel.add(header);
        BorrowPanel.add(Box.createVerticalStrut(40));
        BorrowPanel.add(bookid);
        BorrowPanel.add(Box.createVerticalStrut(8));
        BorrowPanel.add(BookID);
        BorrowPanel.add(Box.createVerticalStrut(12));
        BorrowPanel.add(buttonPanel);
        BorrowPanel.add(Box.createVerticalStrut(16));
        BorrowPanel.add(status);
        BorrowPanel.add(Box.createVerticalStrut(28));

        JPanel backPanel = new JPanel();
        backPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        backPanel.setBackground(Theme.PAPER);
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
        confirmFrame.setTitle("Vertex Library - Confirm Borrow");
        confirmFrame.setSize(460, 300); // Slightly larger to accommodate spacing
        confirmFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        confirmFrame.setLayout(new GridBagLayout()); // Use GridBagLayout for better control
        confirmFrame.getContentPane().setBackground(Theme.PAPER);
        confirmFrame.setResizable(false);
        Theme.applyIcon(confirmFrame);

        // Create components
        Theme.styleFieldLabel(idLabel);
        Theme.styleField(idTextField);
        JLabel dateLabel = Theme.fieldLabel("Return date:");
        JDateChooser returnDateChooser = new JDateChooser();
        returnDateChooser.setDateFormatString("dd-MM-yyyy"); // Set date format
        returnDateChooser.setFont(Theme.body(14));

        JButton confirmButton = Theme.button("Confirm borrow", Theme.ButtonStyle.PRIMARY);

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

            try {
                // Insert into database
                InsertIntoDB.borrowBookWithDate(LibraryPage.frame, userID, returnDate, bookID, getBookName(bookID), getuserName(userID));
                JOptionPane.showMessageDialog(confirmFrame, "Book borrowed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE, Theme.successIcon());

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
