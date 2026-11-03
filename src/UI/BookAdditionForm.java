package UI;

import Entities.*;
import utilities.*;

import javax.swing.*;
import java.awt.*;

import static utilities.GetInfoFromDB.getTheNumberofAllBooks;


/**
 * A form for adding a new book to the system.
 * Provides a user-friendly interface to input book details.
 */
public class BookAdditionForm extends JFrame {
    private final JTextField titleField; // Text field for entering the book title
    private final JTextField authorField; // Text field for entering the book author
    private final JTextField publisherField; // Text field for entering the book publisher
    private final JTextField yearField; // Text field for entering the book publication year
    private final JButton addButton; // Button to add the book
    private final JButton cancelButton; // Button to cancel the operation
    private final JTable bookTable; // Table to display books
    public static Books book = new Books(); // Static instance of the Books class

    /**
     * Constructor to initialize the Book Addition Form.
     *
     * @param bookTable The JTable that displays books, used for refreshing after adding a new book.
     */
    public BookAdditionForm(JTable bookTable) {
        this.bookTable = bookTable;

        // Frame setup
        setTitle("Vertex Library - Add Book");
        setSize(460, 440);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the form
        Theme.applyIcon(this);
        setLayout(new BorderLayout());
        add(Theme.pageHeader("Add a book", "New books are added as Available."), BorderLayout.NORTH);

        // Main Panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.PAPER);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 28, 8, 28));

        // Create and add components
        panel.add(createInputField("Title", titleField = new JTextField(20)));
        panel.add(createInputField("Author", authorField = new JTextField(20)));
        panel.add(createInputField("Publisher", publisherField = new JTextField(20)));
        panel.add(createInputField("Year", yearField = new JTextField(20)));

        addButton = Theme.button("Add book", Theme.ButtonStyle.PRIMARY);
        cancelButton = Theme.button("Cancel", Theme.ButtonStyle.QUIET);

        addButton.addActionListener(e -> addBook());
        cancelButton.addActionListener(e -> dispose());

        add(panel, BorderLayout.CENTER);
        add(Theme.actionBar(cancelButton, addButton), BorderLayout.SOUTH);
        getRootPane().setDefaultButton(addButton);
        setVisible(true);
    }

    /**
     * Helper method to create input fields with labels.
     *
     * @param label The label for the input field.
     * @param field The text field to be added.
     * @return A JPanel containing the label and text field.
     */
    private JPanel createInputField(String label, JTextField field) {
        JPanel inputPanel = new JPanel(new BorderLayout(12, 0));
        inputPanel.setBackground(Theme.PAPER);
        JLabel jLabel = Theme.fieldLabel(label);
        jLabel.setPreferredSize(new Dimension(80, 0));
        Theme.styleField(field);
        inputPanel.add(jLabel, BorderLayout.WEST);
        inputPanel.add(field, BorderLayout.CENTER);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, inputPanel.getPreferredSize().height));
        return inputPanel;
    }

    /**
     * Handles adding a new book to the database and refreshing the book table.
     */
    private void addBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String publisher = publisherField.getText().trim();
        boolean status = true;
        int year;

        // Validate input
        if (title.isEmpty() || author.isEmpty() || publisher.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            year = Integer.parseInt(yearField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Year must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create and insert new book
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setYear(year);
        book.setStatus(status);
        InsertIntoDB.InsertIntoDB(book);
        // Refresh book table
        updateTable.refreshTable(bookTable, "SELECT ID, BookName, Author, Publisher, Year, " +
                "CASE WHEN Status = 1 THEN 'Available' ELSE 'Unavailable' END AS Status FROM books");
        CEOPage.allBooksLabel.setText(String.valueOf(getTheNumberofAllBooks()));

        // Show success message
        JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE, Theme.successIcon());
        dispose();
    }
}