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
        ReturnBooksPanel.setBackground(Theme.PAPER);
        ReturnBooksPanel.add(Theme.pageHeader("Return a book", "Find a member's loans by user ID, select a book and return it."), BorderLayout.NORTH);

        // Left Panel for User ID and Buttons
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Theme.PAPER);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 16));
        Dimension buttonSize = new Dimension(240, 44);

        JLabel UserId = Theme.fieldLabel("User ID");
        UserId.setAlignmentX(Component.LEFT_ALIGNMENT);
        UserId.setBorder(BorderFactory.createEmptyBorder(10, 0, 8, 0));

        JTextField userIDField = new JTextField();
        Theme.styleField(userIDField);
        userIDField.setAlignmentX(Component.LEFT_ALIGNMENT);
        userIDField.setPreferredSize(new Dimension(240, 40));
        userIDField.setMaximumSize(new Dimension(240, 40));

        JButton checkBorrowedBooks = Theme.button("Check borrowed books", Theme.ButtonStyle.SECONDARY);
        checkBorrowedBooks.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkBorrowedBooks.setMaximumSize(buttonSize);

        JButton ReturnButton = Theme.button("Return selected book", Theme.ButtonStyle.PRIMARY);
        ReturnButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        ReturnButton.setMaximumSize(buttonSize);

        JButton backButton = Theme.button("Back", Theme.ButtonStyle.QUIET);
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.setMaximumSize(buttonSize);
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
        leftPanel.add(Box.createVerticalStrut(16));
        leftPanel.add(checkBorrowedBooks);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(ReturnButton);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(backButton);
        leftPanel.add(Box.createVerticalStrut(30));
         JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout(0, 10));
        rightPanel.setBackground(Theme.PAPER);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 16, 32, 32));

        JLabel tableLabel = new JLabel("Borrowed books");
        tableLabel.setFont(Theme.strong(14));
        tableLabel.setForeground(Theme.NAVY);
        rightPanel.add(tableLabel, BorderLayout.NORTH);
        

      // Define table headers
    String[] columnNames = {"Book ID", "Book Name", "Borrowed Date"};
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
    
// Create the JTable
    JTable table = new JTable(tableModel);
    Theme.styleTable(table);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

// Add the table to a scroll pane
    JScrollPane tableScrollPane = Theme.tableScroll(table);
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

                JOptionPane.showMessageDialog(LibraryPage.frame, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE, Theme.successIcon());

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
