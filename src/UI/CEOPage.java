package UI;
import javax.swing.*;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.*;
import utilities.*;

import static utilities.BubbleSort.sortTable;
import static utilities.GetInfoFromDB.*;

/**
 * CEOPage is the main interface for the CEO. It provides navigation to
 * manage users, books, and view statistics via a user-friendly GUI.
 */

    public class CEOPage extends JFrame {

        private final JFrame frm = new JFrame();
        private final JPanel mainPanel = new JPanel(); // Main panel that holds sidebar and content area
        private final JPanel contentPanel = new JPanel(new CardLayout()); // Content panel with CardLayout
        private JTable userTable;
        private Sidebar sidebar;

    static JLabel allBooksLabel;
    static JLabel allUsersLabel;
    static JLabel borrowedBooksLabel;
/**
     * Constructor to initialize and set up the CEOPage interface.
     * @param username Username of the logged-in CEO (can be used for personalization or logging).
     */

        public CEOPage(String username) {
            frm.setSize(1080, 720);
            frm.setMinimumSize(new Dimension(900, 600));
            frm.setLocationRelativeTo(null);
            frm.setDefaultCloseOperation(EXIT_ON_CLOSE);
            frm.setTitle("Vertex Library - CEO");
            Theme.applyIcon(frm);

            // Set up the frame layout
            frm.setLayout(new BorderLayout());
            // Set up the main panel with BorderLayout
            mainPanel.setLayout(new BorderLayout());
            mainPanel.setBackground(Theme.PAPER);
            contentPanel.setBackground(Theme.PAPER);

            // Add sidebar and content panel to the main panel
            sidebar = new Sidebar("CEO workspace", new String[]{"Dashboard", "Manage Users", "Manage Books"},
                    this::showPanel, this::logout);
            mainPanel.add(sidebar, BorderLayout.WEST);
            mainPanel.add(contentPanel, BorderLayout.CENTER); // Center area with CardLayout

            // Initialize content panels
            initContentPanels();

            // Add main panel to the frame
            frm.add(mainPanel);

            frm.setVisible(true);
        }

    private void logout() {
        int res = JOptionPane.showConfirmDialog(frm, "Are you sure you want to log out?", "Log out", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            frm.dispose();
            new LoginForm();
        }
    }

  /**
     * Initializes content panels for Dashboard, Manage Users, and Manage Books.
     */
  private void initContentPanels() {
            contentPanel.add(dashboardPanel(), "Dashboard");
            contentPanel.add(createPeoplePanel(), "Manage Users");
            contentPanel.add(createLibraryPanel(), "Manage Books");
        }
    /**
     * Creates the Dashboard panel for the CEO. This panel includes a welcome section,
     * library statistics, and navigation buttons to other sections (Manage Books and Manage Users).
     * @return A JPanel representing the Dashboard.
     */
    private JPanel dashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(Theme.PAPER);
        dashboard.add(Theme.pageHeader("Welcome to Vertex Library", "An overview of the collection and its users."), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 28));
        body.setBackground(Theme.PAPER);
        body.setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));

        // Statistics cards
        allBooksLabel = new JLabel(String.valueOf(getTheNumberofAllBooks()));
        allUsersLabel = new JLabel(String.valueOf(getTheNumberofAllUsers()));
        borrowedBooksLabel = new JLabel(String.valueOf(getTheNumberofBorrowedBooks()));

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(Theme.PAPER);
        statsPanel.add(Theme.statCard("Total books", allBooksLabel, Theme.NAVY));
        statsPanel.add(Theme.statCard("Borrowed books", borrowedBooksLabel, Theme.ROYAL));
        statsPanel.add(Theme.statCard("Users", allUsersLabel, Theme.SKY));
        body.add(statsPanel, BorderLayout.NORTH);

        // Quick actions
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonsPanel.setBackground(Theme.PAPER);
        JButton manageBooksButton = Theme.button("Manage books", Theme.ButtonStyle.PRIMARY);
        JButton manageUsersButton = Theme.button("Manage users", Theme.ButtonStyle.SECONDARY);
        manageBooksButton.addActionListener(e -> showPanel("Manage Books"));
        manageUsersButton.addActionListener(e -> showPanel("Manage Users"));
        buttonsPanel.add(manageBooksButton);
        buttonsPanel.add(Box.createHorizontalStrut(12));
        buttonsPanel.add(manageUsersButton);
        body.add(buttonsPanel, BorderLayout.CENTER);

        dashboard.add(body, BorderLayout.CENTER);
        return dashboard;
    }

          /**
     * Creates the Manage Users panel, containing a table and control buttons.
     * @return A JPanel containing the Manage Users interface.
     */

        private JPanel createPeoplePanel() {
            JPanel peoplePanel = new JPanel(new BorderLayout());
            peoplePanel.setBackground(Theme.PAPER);
            peoplePanel.add(Theme.pageHeader("Manage users", "Add, remove and review user accounts. Click a column header to sort."), BorderLayout.NORTH);

            userTable = new JTable();
            Theme.styleTable(userTable);
            updateTable.refreshTable(userTable, "SELECT ID, Name, Lastname, Username, Position FROM users");

            JPanel tableWrap = new JPanel(new BorderLayout());
            tableWrap.setBackground(Theme.PAPER);
            tableWrap.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
            tableWrap.add(Theme.tableScroll(userTable), BorderLayout.CENTER);
            peoplePanel.add(tableWrap, BorderLayout.CENTER);

            JButton addUserButton = Theme.button("Add user", Theme.ButtonStyle.PRIMARY);
            addUserButton.addActionListener(e -> {
                addUser();
                updateTable.refreshTable(userTable, "SELECT ID, Name, Lastname, Username, Position FROM users");
            });

            JButton removeUserButton = Theme.button("Remove user", Theme.ButtonStyle.DANGER);
            removeUserButton.addActionListener(e -> {
                RemoveFromDB.removeSelectedUser(userTable);
                updateTable.refreshTable(userTable, "SELECT ID, Name, Lastname, Username, Position FROM users");
            });

            JButton backBtn = Theme.button("Back", Theme.ButtonStyle.QUIET);
            backBtn.addActionListener(e -> showPanel("Dashboard"));

            JTableHeader header = userTable.getTableHeader();
            header.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int columnIndex = header.columnAtPoint(e.getPoint()); // Get the clicked column index
                    sortTable(userTable, columnIndex); // Sort the table by the clicked column
                }
            });

            // Disable table editing
            userTable.setDefaultEditor(Object.class, null);

            peoplePanel.add(Theme.actionBar(backBtn, removeUserButton, addUserButton), BorderLayout.SOUTH);
            return peoplePanel;
        }

   /**
 * Creates the "Manage Books" panel, which includes a table to display book data
 * and buttons to add, remove, or navigate back to the dashboard.
 * @return A JPanel containing the book management interface.
 */
private JPanel createLibraryPanel() {
    JPanel libraryPanel = new JPanel(new BorderLayout());
    libraryPanel.setBackground(Theme.PAPER);
    libraryPanel.add(Theme.pageHeader("Manage books", "The full catalogue with live availability. Click a column header to sort."), BorderLayout.NORTH);

    // Set up the table to display book data
    JTable bookTable = new JTable();
    Theme.styleTable(bookTable);
    updateTable.refreshTable(bookTable, "SELECT ID, BookName, Author, Publisher, Year, " +
            "CASE WHEN Status = 1 THEN 'Available' ELSE 'Unavailable' END AS Status FROM books");

    JPanel tableWrap = new JPanel(new BorderLayout());
    tableWrap.setBackground(Theme.PAPER);
    tableWrap.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
    tableWrap.add(Theme.tableScroll(bookTable), BorderLayout.CENTER);
    libraryPanel.add(tableWrap, BorderLayout.CENTER);

    JButton addBookButton = Theme.button("Add book", Theme.ButtonStyle.PRIMARY);
    addBookButton.addActionListener(e -> {
        new BookAdditionForm(bookTable); // Open the Book Addition form
        updateTable.refreshTable(bookTable, "SELECT ID, BookName, Author, Publisher, Year, " +
                "CASE WHEN Status = 1 THEN 'Available' ELSE 'Unavailable' END AS Status FROM books");
    });

    JButton removeBookButton = Theme.button("Remove book", Theme.ButtonStyle.DANGER);
    removeBookButton.addActionListener(e -> {
        RemoveFromDB.removeBook(bookTable); // Remove the selected book from the database
        updateTable.refreshTable(bookTable, "SELECT ID, BookName, Author, Publisher, Year, " +
                "CASE WHEN Status = 1 THEN 'Available' ELSE 'Unavailable' END AS Status FROM books");
    });

    JButton backBtn = Theme.button("Back", Theme.ButtonStyle.QUIET);
    backBtn.addActionListener(e -> showPanel("Dashboard"));

    JTableHeader header = bookTable.getTableHeader();
    header.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int columnIndex = header.columnAtPoint(e.getPoint()); // Get the clicked column index
            sortTable(bookTable, columnIndex); // Sort the table by the clicked column
        }
    });

    // Disable editing for the table to prevent unintended changes
    bookTable.setDefaultEditor(Object.class, null);

    libraryPanel.add(Theme.actionBar(backBtn, removeBookButton, addBookButton), BorderLayout.SOUTH);
    return libraryPanel;
}

    private void addUser() {
        new RegistrationForm(userTable);
    }

     // Method to show the specified panel based on the name
     private void showPanel(String panelName) {
         CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
         cardLayout.show(contentPanel, panelName);
         sidebar.select(panelName);
     }


}
