package UI;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import utilities.*;


public class ReturnPanel {
    
     JPanel ReturnPanel() {
        JPanel ReturnBooksPanel = new JPanel();
        ReturnBooksPanel.setLayout(new BorderLayout());
        ReturnBooksPanel.setBackground(new Color(240, 248, 255));

        // Left Panel for User ID and Buttons
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(240, 248, 255));

        JLabel UserId = new JLabel("Enter User ID:");
        UserId.setFont(new Font("Roboto", Font.BOLD, 16));
        UserId.setForeground(new Color(33, 37, 41));
        UserId.setAlignmentX(Component.LEFT_ALIGNMENT);
        UserId.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JTextField userIDField = new JTextField();
        userIDField.setFont(new Font("Roboto", Font.PLAIN, 16));
        userIDField.setForeground(new Color(33, 37, 41));
        userIDField.setBackground(new Color(255, 255, 255));
        userIDField.setAlignmentX(Component.LEFT_ALIGNMENT);
        userIDField.setPreferredSize(new Dimension(200, 30));
        userIDField.setMaximumSize(new Dimension(200, 30));
        userIDField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        userIDField.setCaretColor(new Color(0, 0, 0));

        JButton checkBorrowedBooks = new JButton("Check Borrowed Books");
        checkBorrowedBooks.setFont(new Font("Roboto", Font.BOLD, 16));
        checkBorrowedBooks.setForeground(Color.WHITE);
        checkBorrowedBooks.setBackground(new Color(33, 37, 41));
        checkBorrowedBooks.setPreferredSize(new Dimension(150, 45));
        checkBorrowedBooks.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkBorrowedBooks.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        checkBorrowedBooks.setFocusPainted(false);
        checkBorrowedBooks.setCursor(new Cursor(Cursor.HAND_CURSOR));
        



        JButton ReturnButton = new JButton("Return selected book(s)");
        ReturnButton.setFont(new Font("Roboto", Font.BOLD, 16));
        ReturnButton.setForeground(Color.WHITE);
        ReturnButton.setBackground(new Color(33, 37, 41));
        ReturnButton.setPreferredSize(new Dimension(150, 45));
        ReturnButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        ReturnButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        ReturnButton.setFocusPainted(false);
        ReturnButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Roboto", Font.BOLD, 16));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(33, 37, 41));
        backButton.setPreferredSize(new Dimension(150, 45));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LibraryPage.frame.getContentPane().removeAll();
                LibraryPage.frame.add(LibraryPage.panel, BorderLayout.CENTER);
                LibraryPage.frame.revalidate();
                LibraryPage.frame.repaint();
            }
        });

        leftPanel.add(Box.createVerticalStrut(30));
        leftPanel.add(UserId);
        leftPanel.add(userIDField);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(checkBorrowedBooks);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(ReturnButton);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(backButton);
        leftPanel.add(Box.createVerticalStrut(30));
         JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(new Color(240, 248, 255));

        JLabel tableLabel = new JLabel("Borrowed Books:");
        tableLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        tableLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(tableLabel, BorderLayout.NORTH);
        

      // Define table headers
    String[] columnNames = {"Book ID", "Book Name", "Borrowed Date"};
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
    
// Create the JTable
    JTable table = new JTable(tableModel);
    table.setFillsViewportHeight(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

// Add the table to a scroll pane
    JScrollPane tableScrollPane = new JScrollPane(table);
    tableScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

checkBorrowedBooks.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        try {
            int userID = Integer.parseInt(userIDField.getText().trim());
            ArrayList<String[]> borrowedBooks = new GetInfoFromDB().checkBorrowedBooks(userID);

            // Clear existing rows in the table
            tableModel.setRowCount(0);

            if (borrowedBooks.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No borrowed books found for this User ID.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Add retrieved rows to the table
                for (String[] bookData : borrowedBooks) {
                    tableModel.addRow(bookData);
                }

                // Ensure the table is displayed in the right panel
                rightPanel.removeAll();
                rightPanel.add(tableLabel, BorderLayout.NORTH);
                rightPanel.add(tableScrollPane, BorderLayout.CENTER);
                rightPanel.revalidate();
                rightPanel.repaint();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter a valid User ID", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
});

ReturnButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        // Get the selected row in the table
        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) { 
            String bookId = (String) table.getValueAt(selectedRow, 0);  

            try {
                int userID = Integer.parseInt(userIDField.getText().trim());  

                RemoveFromDB.returnBook(LibraryPage.frame, String.valueOf(userID), bookId);

                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.removeRow(selectedRow);

                ImageIcon checkmarkIcon = new ImageIcon(getClass().getResource("/checkmark.png"));

                JOptionPane.showMessageDialog(LibraryPage.frame, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE,checkmarkIcon);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(LibraryPage.frame, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(LibraryPage.frame, "Please select a book to return.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
});



        
        

        ReturnBooksPanel.add(leftPanel, BorderLayout.WEST);
        ReturnBooksPanel.add(rightPanel, BorderLayout.CENTER);

        return ReturnBooksPanel;

    }
    




}
