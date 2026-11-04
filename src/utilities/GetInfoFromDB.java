package utilities;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Utility class to retrieve information from the database.
 */
public class GetInfoFromDB {

    /**
     * Retrieves a list of borrowed books for a specific user from the database.
     *
     * @param ID The user ID for whom the borrowed books are being queried.
     * @return An ArrayList of String arrays, where each array contains:
     *         [bookID, bookName, borrow_date] for each borrowed book.
     */
    public ArrayList<String[]> checkBorrowedBooks(int ID) {
        // SQL query to retrieve book details from the Transactions table
        String sql1 = "SELECT bookID, bookName, borrow_date FROM dbo.Transactions WHERE userID = ? and isReturned = 0";
        ArrayList<String[]> borrowedBooks = new ArrayList<>(); // List to hold borrowed book data

        try (Connection conn = dbConnection.getConnection(); // Establish database connection
             PreparedStatement pstmt = conn.prepareStatement(sql1)) { // Prepare the SQL statement

            // Set the user ID in the query
            pstmt.setInt(1, ID);

            // Execute the query and retrieve the results
            ResultSet rs = pstmt.executeQuery();

            // Iterate through the results and store them in the list
            while (rs.next()) {
                String[] bookData = {
                        rs.getString("bookID"),       // Get the book ID
                        rs.getString("bookName"),     // Get the book name
                        rs.getString("borrow_date")   // Get the borrow date
                };
                borrowedBooks.add(bookData); // Add the book data to the list
            }

        } catch (Exception e) {
            // Print the stack trace if an exception occurs
            e.printStackTrace();
        }

        // Return the list of borrowed books
        return borrowedBooks;
    }


    public static boolean isUsernameTaken(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static DefaultTableModel fetchBorrowedBooks(String username) {
        String[] columnNames = {"Transaction ID", "BookName", "Borrow Date", "Return Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        String query = "SELECT t.transactionID, b.BookName, t.borrow_date, t.return_date " +
                "FROM dbo.transactions t " +
                "INNER JOIN dbo.books b ON t.bookID = b.ID " +
                "INNER JOIN dbo.users u ON t.userID = u.ID " +
                "WHERE u.Username = ?";

        try (ResultSet rs = executeSelectQuery(query, username)) {
            while (rs.next()) {
                int transactionID = rs.getInt("transactionID");
                String bookName = rs.getString("BookName");
                String borrowDate = rs.getString("borrow_date");
                String returnDate = rs.getString("return_date");

                model.addRow(new Object[]{transactionID, bookName, borrowDate, returnDate});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching borrowed books: " + e.getMessage());
        }

        return model;
    }


    public static DefaultTableModel fetchDueBooks(String username) {
        String[] columnNames = {"Transaction ID", "BookName", "Borrow Date", "Return Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        String query = "SELECT t.transactionID, b.BookName, t.borrow_date, t.return_date " +
                "FROM dbo.transactions t " +
                "INNER JOIN dbo.books b ON t.bookID = b.ID " +
                "INNER JOIN dbo.users u ON t.userID = u.ID " +
                "WHERE u.Username = ? AND t.isDue = 1";

        try (ResultSet rs = executeSelectQuery(query, username)) {
            while (rs.next()) {
                int transactionID = rs.getInt("transactionID");
                String bookName = rs.getString("BookName");
                String borrowDate = rs.getString("borrow_date");
                String returnDate = rs.getString("return_date");

                model.addRow(new Object[]{transactionID, bookName, borrowDate, returnDate});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching borrowed books: " + e.getMessage());
        }

        return model;
    }

    public static int getTheNumberofBorrowedBooks(String username) {
        int number = 0;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try {
            // Step 1: Establish a database connection
            connection = dbConnection.getConnection();

            // Step 2: Write the SQL query
            String query = "SELECT COUNT(*) AS borrowedCount " +
                    "FROM transactions t " +
                    "INNER JOIN users u ON t.userID = u.ID " +
                    "WHERE u.Username = ? AND t.isReturned = 0";

            // Step 3: Prepare and execute the statement
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            resultSet = pstmt.executeQuery();

            // Step 4: Process the result
            if (resultSet.next()) {
                number = resultSet.getInt("borrowedCount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Step 5: Close all resources
            try {
                if (resultSet != null) resultSet.close();
                if (pstmt != null) pstmt.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return number;
    }

    public static int getTheNumberofDueBooks(String username) {
        int number = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Step 1: Establish a database connection
            connection = dbConnection.getConnection();

            // Step 2: Write the SQL query
            String query = "SELECT COUNT(*) AS borrowedCount " +
                    "FROM transactions t " +
                    "INNER JOIN users u ON t.userID = u.ID " +
                    "WHERE u.Username = ? AND t.isDue = 1";

            // Step 3: Prepare and execute the statement
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            // Step 4: Process the result
            if (resultSet.next()) {
                number = resultSet.getInt("borrowedCount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Step 5: Close all resources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return number;
    }



    public static void setDue(String username) {
        // Query to get the current date
        String currentDateQuery = "SELECT CAST(GETDATE() AS DATE);";

        // Query to get the return date for the user's books
        String returnDateQuery = "SELECT t.transactionID, t.return_date " +
                "FROM dbo.Transactions t " +
                "INNER JOIN dbo.users u ON t.userID = u.ID " +
                "WHERE u.Username = ? AND t.isReturned = 0";

        // Query to update the isDue flag for overdue books
        String updateDueQuery = "UPDATE dbo.Transactions SET isDue = 1 WHERE transactionID = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(currentDateQuery);
             PreparedStatement pstmt1 = conn.prepareStatement(returnDateQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateDueQuery)) {

            // Step 1: Get today's date
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) return; // Exit if no date is returned
            String todayDate = rs.getString(1);
            rs.close();

            // Step 2: Get the return dates for the user's books
            pstmt1.setString(1, username);
            ResultSet rs1 = pstmt1.executeQuery();

            // Step 3: Compare dates and update isDue flag if overdue
            while (rs1.next()) {
                String returnDate = rs1.getString("return_date");
                int transactionID = rs1.getInt("transactionID");

                // Compare today's date with the return date
                if (returnDate != null && todayDate.compareTo(returnDate) > 0) {
                    // If today's date is after the return date, mark as due
                    updateStmt.setInt(1, transactionID);
                    updateStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.err.println("Error updating due status: " + e.getMessage());
        }
    }






    public static String checkPosition(String username) {

        String query = "SELECT position FROM users WHERE username = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("position"); // Return position if found (e.g., "CEO" or "User")
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null; // Return null if position not found or query fails
    }


    /**
     * Checks if a book is available in the database based on its ID.
     * @param bookID The ID of the book to check.
     * @return true if the book is available, false otherwise.
     */
    public static boolean checkBookAvailability(String bookID) {
        String query = "SELECT status FROM dbo.books WHERE ID = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bookID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("status") == 1; // 1 indicates "Available"
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to unavailable if an error occurs
    }


    /**
     * Retrieves the username corresponding to a given user ID from the database.
     * @param ID The user ID to search for.
     * @return The username if found, or an error message if not found.
     * @throws SQLException if an error occurs during database interaction.
     */
    public static String getuserName(int ID) throws SQLException {
        final String sql = "SELECT name FROM users WHERE id = ?";
        String username = null;
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                username = rs.getString("name");
            } else {
                username = "No user found for this ID";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            username = "Error: " + e.getMessage();
        }
        return username;
    }

    public static String getBookName(int ID) throws SQLException {
        final String sql = "SELECT BookName FROM books WHERE id = ?";
        String bookName = null;
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bookName = rs.getString("BookName");

            }
        }
        return bookName;
    }

    public static int getTheNumberofAllBooks(){
        int number = 0;

        String sql = "Select COUNT(*) from dbo.books";

        try (
                Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()
        ){

            if (rs.next()) {
                number = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }


        return number;

    }

    public static int getTheNumberofAllUsers(){
        int number = 0;

        String sql = "Select COUNT(*) from dbo.users";

        try (
                Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()
        ){

            if (rs.next()) {
                number = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }


        return number;

    }


    public  static int getTheNumberofBorrowedBooks(){
        int number = 0;

        String sql = "Select COUNT(*) from dbo.books where Status = 0";

        try (
                Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()
        ){

            if (rs.next()) {
                number = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }


        return number;

    }


    public static ResultSet executeSelectQuery(String query, Object... params) throws SQLException {
        Connection conn = dbConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);

        // Set parameters dynamically
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        // Return the ResultSet (caller is responsible for closing it)
        return stmt.executeQuery();
    }
}
