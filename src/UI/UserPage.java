package UI;

import javax.swing.*;
import javax.swing.table.*;


import java.awt.*;
import java.awt.event.*;
import utilities.*;

import static utilities.BubbleSort.sortTable;
import static utilities.GetInfoFromDB.*;

public class UserPage extends JFrame {

    private final JFrame frame = new JFrame();
    private final JPanel mainPanel = new JPanel(); // Main panel that holds sidebar and content area
    private final JPanel contentPanel = new JPanel(new CardLayout());
    private Sidebar sidebar;


    private final String name;
    private final String lastname;
    private final String username;

    UserPage(String name, String lastname, String username) {

        this.name = name;
        this.lastname = lastname;
        this.username = username;

        frame.setSize(1080, 720);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setTitle("Vertex Library - User");
        Theme.applyIcon(frame);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Theme.PAPER);
        contentPanel.setBackground(Theme.PAPER);

        sidebar = new Sidebar("User workspace", new String[]{"Welcome", "Books", "Borrowed Books", "Due Books"},
                this::showPanel, this::logout);
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER); // Center area with CardLayout

        initContentPanels();

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void initContentPanels() {
        JPanel welcomPanel = createWelcomePanel(name, lastname, getTheNumberofBorrowedBooks(username),getTheNumberofDueBooks(username));
        contentPanel.add(welcomPanel, "Welcome");

        JPanel booksPanel = createBooksPanel();
        contentPanel.add(booksPanel, "Books");

        JPanel borrowedBooksPanel = createBorrowedBooksPanel(username);
        contentPanel.add(borrowedBooksPanel, "Borrowed Books");

        JPanel dueBooksPanel = createDueBooksPanel(username);
        contentPanel.add(dueBooksPanel, "Due Books");

    }

    private void logout() {
        int res = JOptionPane.showConfirmDialog(frame, "Are you sure you want to log out?", "Log out", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            frame.dispose();
            new LoginForm();
        }
    }

    private JPanel createWelcomePanel(String name, String lastName, int borrowedBooksCount, int dueBooksCount) {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(Theme.PAPER);
        welcomePanel.add(Theme.pageHeader("Welcome back, " + username + "!", "Here is a summary of your books."), BorderLayout.NORTH);

        // Quick info cards; click one to open the matching list
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        infoPanel.setBackground(Theme.PAPER);
        infoPanel.add(createStatCard("Borrowed books", borrowedBooksCount, Theme.ROYAL, "Borrowed Books"));
        infoPanel.add(createStatCard("Due books", dueBooksCount, Theme.OVERDUE, "Due Books"));

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Theme.PAPER);
        body.setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));
        body.add(infoPanel, BorderLayout.NORTH);
        welcomePanel.add(body, BorderLayout.CENTER);

        return welcomePanel;
    }

    private JPanel createBooksPanel() {
        JTable bookTable = new JTable();
        Theme.styleTable(bookTable);
        updateTable.refreshTable(bookTable, "SELECT ID, BookName, Author, Publisher, Year, " +
                "CASE WHEN Status = 1 THEN 'Available' ELSE 'Unavailable' END AS Status FROM books");
        bookTable.setDefaultEditor(Object.class, null);
        addSortOnHeaderClick(bookTable);

        return tablePage("Books", "The full catalogue with live availability. Click a column header to sort.", bookTable);
    }

    private JPanel createBorrowedBooksPanel(String username) {
        JTable borrowedBooksTable = new JTable(fetchBorrowedBooks(username));
        Theme.styleTable(borrowedBooksTable);
        addSortOnHeaderClick(borrowedBooksTable);

        return tablePage("Borrowed books", "Books currently on loan under your account.", borrowedBooksTable);
    }

    private JPanel createDueBooksPanel(String username){

        GetInfoFromDB.setDue(username);

        JTable DueBooksTable = new JTable(GetInfoFromDB.fetchDueBooks(username));
        Theme.styleTable(DueBooksTable);
        addSortOnHeaderClick(DueBooksTable);

        return tablePage("Due books", "Books past their return date. Please return these first.", DueBooksTable);
    }

    /**
     * Page with a header, a table and a Back button, used by every list in this workspace.
     */
    private JPanel tablePage(String title, String subtitle, JTable table) {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Theme.PAPER);
        page.add(Theme.pageHeader(title, subtitle), BorderLayout.NORTH);

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBackground(Theme.PAPER);
        tableWrap.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        tableWrap.add(Theme.tableScroll(table), BorderLayout.CENTER);
        page.add(tableWrap, BorderLayout.CENTER);

        JButton backBtn = Theme.button("Back", Theme.ButtonStyle.QUIET);
        backBtn.addActionListener(e -> showPanel("Welcome"));
        page.add(Theme.actionBar(backBtn), BorderLayout.SOUTH);
        return page;
    }

    private void addSortOnHeaderClick(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = header.columnAtPoint(e.getPoint()); // Get the clicked column index
                sortTable(table, columnIndex); // Sort the table by the clicked column
            }
        });
    }

    private JPanel createStatCard(String caption, int count, Color accent, String panelName) {
        JPanel card = Theme.statCard(caption, new JLabel(String.valueOf(count)), accent);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Hand cursor on hover

        // Hover tint, and open the matching list on click
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(Theme.SKY_TINT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Theme.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showPanel(panelName);
            }
        });

        return card;
    }

    private void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panelName);
        sidebar.select(panelName);
        contentPanel.revalidate(); // Ensure layout updates
        contentPanel.repaint();    // Refresh the UI
    }


}
