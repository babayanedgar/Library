package utilities;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import Entities.*;

import static utilities.GetInfoFromDB.isUsernameTaken;

public class InsertIntoDB {

    public InsertIntoDB() {

    }
        public static void InsertIntoDB(Employee emp){
            if (isUsernameTaken(emp.getUsername())) {
                JOptionPane.showMessageDialog(null, "Username already exists! Please choose another one.");
                return;
            }

            String query = "use Library " +
                    "INSERT INTO employees (name, lastname, username, gender,email, password, position) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, emp.getName());
                pstmt.setString(2, emp.getLastName());
                pstmt.setString(3, emp.getUsername());
                pstmt.setString(4, emp.getGender());
                pstmt.setString(5, emp.getEmail());
                pstmt.setString(6, emp.getPassword());
                pstmt.setString(7, emp.getPosition());
                pstmt.executeUpdate();



            } catch (SQLException ex) {
                ex.printStackTrace();

            }
        }

        public static void InsertIntoDB(Books books){
            String query = "use Library " +
                    " INSERT INTO books ( BookName, Author, Publisher, Year, status) VALUES ( ?, ?, ?, ?, 1)";

            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, books.getTitle());
                pstmt.setString(2, books.getAuthor());
                pstmt.setString(3, books.getPublisher());
                pstmt.setInt(4, books.getYear()); // changed to setInt if year is an integer
                pstmt.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }

    /**
     * Borrow a book and record the transaction.
     *
     * @param frame      The parent JFrame for dialog messages.
     * @param userID     The ID of the user borrowing the book.
     * @param returnDate The date the book should be returned.
     * @param bookID     The ID of the book being borrowed.
     * @param bookName   The name of the book being borrowed.
     * @param userName   The name of the user borrowing the book.
     */
    public static void borrowBookWithDate(JFrame frame, int userID, Date returnDate, int bookID, String bookName, String userName) {
        LocalDate today = LocalDate.now();
        Date borrowDate = Date.valueOf(today);


        String insertTransactionSQL = "INSERT INTO dbo.Transactions ( userID, userName, bookID, bookName, borrow_date, return_date, isDue, isReturned) VALUES ( ?, ?, ?, ?, ?, ?, 0, 0)";
        String updateBookSQL = "UPDATE dbo.books SET status = 0 WHERE ID = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement insertTransactionStmt = conn.prepareStatement(insertTransactionSQL);
             PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSQL)) {

            // Insert transaction
            insertTransactionStmt.setInt(1, userID);
            insertTransactionStmt.setString(2, userName);
            insertTransactionStmt.setInt(3, bookID);
            insertTransactionStmt.setString(4, bookName);
            insertTransactionStmt.setDate(5, borrowDate);
            insertTransactionStmt.setDate(6, returnDate);
            insertTransactionStmt.executeUpdate();



            // Update book status
            updateBookStmt.setInt(1, bookID);
            updateBookStmt.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error borrowing the book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


}



