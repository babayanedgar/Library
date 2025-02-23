package utilities;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.*;

public class RemoveFromDB {
    public RemoveFromDB() {
        // Empty constructor
    }
  // Method to remove the selected employee from the table and the database
public static void removeSelectedEmployee(JTable table) {
    int selectedRow = table.getSelectedRow();
    
    if (selectedRow != -1) {
        Object employeeIdObject = table.getValueAt(selectedRow, 0);
        int employeeId;

        try {
            // Convert the employee ID to an integer
            if (employeeIdObject instanceof Integer) {
                employeeId = (Integer) employeeIdObject;
            } else {
                employeeId = Integer.parseInt(employeeIdObject.toString());
            }

            // Database removal logic
            String deleteEmployeeQuery = "DELETE FROM employees WHERE id = ?";
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(deleteEmployeeQuery)) {

                pstmt.setInt(1, employeeId);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Employee removed successfully.");
                    // Optionally remove the row from the table for immediate UI feedback
                    ((DefaultTableModel) table.getModel()).removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "No employee found with ID: " + employeeId);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error removing employee: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid employee ID format.");
        }
    } else {
        JOptionPane.showMessageDialog(null, "Please select an employee to remove.");
    }
}

public static void removeBook(JTable table) {
    int selectedRow = table.getSelectedRow();
    if (selectedRow != -1) {
        Object bookIDObject = table.getValueAt(selectedRow, 0);
        int bookID;

        try {
            // Convert the book ID to an integer
            if (bookIDObject instanceof Integer) {
                bookID = (Integer) bookIDObject;
            } else {
                bookID = Integer.parseInt(bookIDObject.toString());
            }

            // Prepare the SQL query for deleting the book
            String deleteBookQuery = "DELETE FROM books WHERE id = ?";
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(deleteBookQuery)) {

                pstmt.setInt(1, bookID);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Remove the row from the table for immediate UI feedback
                    ((DefaultTableModel) table.getModel()).removeRow(selectedRow);
                    ImageIcon checkmarkIcon = new ImageIcon("/checkmark.png");

                    JOptionPane.showMessageDialog(null, "Book removed successfully.","Success",JOptionPane.INFORMATION_MESSAGE, checkmarkIcon);
                } else {
                    JOptionPane.showMessageDialog(null, "No book found with ID: " + bookID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error occurred while removing the book.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid book ID format.");
        }
    } else {
        JOptionPane.showMessageDialog(null, "Please select a book to remove.");
    }
}



    /**
     * Return a borrowed book and update the transaction record.
     *
     * @param frame  The parent JFrame for dialog messages.
     * @param userID The ID of the user returning the book.
     * @param bookID The ID of the book being returned.
     */
    public static void returnBook(JFrame frame, String userID, String bookID) {
        try {
            // Update the transaction to mark as returned
            int transactionUpdated = executeUpdateQuery(
                    "UPDATE dbo.Transactions SET isReturned = 1 WHERE userID = ? AND bookID = ? AND isReturned = 0",
                    userID, bookID
            );

            // If a transaction was updated, update the book's status
            if (transactionUpdated > 0) {
                executeUpdateQuery("UPDATE dbo.books SET status = 1 WHERE ID = ?", bookID);
            } else {
                JOptionPane.showMessageDialog(frame, "No borrowed record found for the book and user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error returning the book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Reusable method to execute an update query.
     *
     * @param query  The SQL query to execute.
     * @param params The parameters for the PreparedStatement.
     * @throws SQLException If an SQL error occurs.
     */

    public static int executeUpdateQuery(String query, Object... params) throws SQLException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            // Set parameters dynamically
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            // Return the number of rows affected
            return stmt.executeUpdate();
        }
    }

}
