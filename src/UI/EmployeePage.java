package UI;

import javax.swing.*;
import javax.swing.table.*;


import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import utilities.*;

import static utilities.BubbleSort.sortTable;
import static utilities.GetInfoFromDB.*;

public class EmployeePage extends JFrame {

    private final JFrame frame = new JFrame();
    private final JPanel mainPanel = new JPanel(); // Main panel that holds sidebar and content area
    private final JPanel contentPanel = new JPanel(new CardLayout());


    private final String name;
    private final String lastname;
    private final String username;

    EmployeePage(String name, String lastname, String username) {

        this.name = name;
        this.lastname = lastname;
        this.username = username;


       

        frame.setSize(750, 750);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setTitle("Vertex Library - Employee");

        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(10, 20, 30));

        mainPanel.add(createSidebar(new String[]{"Welcome", "Books", "Borrowed Books", "Due Books"}), BorderLayout.WEST);
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

    private JPanel createSidebar(String[] values) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        sidebar.setBackground(new Color(50, 100, 200));
        sidebar.setPreferredSize(new Dimension(200, getHeight())); // Fixed width for sidebar

        for (String valueText : values) {
            JPanel SidebuttonPanel = new JPanel(new GridLayout(1, 1));
            SidebuttonPanel.setPreferredSize(new Dimension(180, 45)); // Set button size
            SidebuttonPanel.setBackground(new Color(255, 255, 255));

            JLabel label = new JLabel(valueText, SwingConstants.CENTER);
            label.setFont(new Font("Roboto", Font.BOLD, 14));
            label.setForeground(new Color(0, 0, 0));
            SidebuttonPanel.add(label);

            // Add hover and click effect
            SidebuttonPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    showPanel(valueText); // Switch panel on click
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    SidebuttonPanel.setBackground(new Color(255, 135, 0)); // Highlight color
                    label.setForeground(Color.WHITE); // White text on hover
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    SidebuttonPanel.setBackground(new Color(255, 255, 255)); // Reset background
                    label.setForeground(Color.BLACK); // Reset text color
                }
            });

            sidebar.add(SidebuttonPanel);
        }

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton);
        logoutButton.setBackground(Color.red);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int res = JOptionPane.showConfirmDialog(null, "Are you sure to log out?", "Logout", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                frame.dispose();
                new LoginForm();
            }
            }
        });
        sidebar.add(logoutButton);

        return sidebar;
    }

   
    private JPanel createWelcomePanel(String name, String lastName, int borrowedBooksCount, int dueBooksCount) {
        // Main panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(245, 245, 245));
    
        // Greeting message
        JLabel greetingLabel = new JLabel("Welcome back, " +username +"!");
        greetingLabel.setFont(new Font("Roboto", Font.BOLD, 28));
        greetingLabel.setForeground(new Color(44, 62, 80));
        greetingLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
        // Quick info panel
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
    
        // Borrowed books count
        JLabel borrowedBooksLabel = createStatCard("Borrowed Books: " + borrowedBooksCount, new Color(52, 152, 219),"Borrowed Books");

    
        // Due books count
        JLabel dueBooksLabel = createStatCard("Due Books: " + dueBooksCount, new Color(231, 76, 60),"Due Books");
    
        // Add the info labels to the info panel
        infoPanel.add(borrowedBooksLabel);
        infoPanel.add(dueBooksLabel);
    
        // Add components to the main panel
        welcomePanel.add(greetingLabel, BorderLayout.NORTH);
        welcomePanel.add(infoPanel, BorderLayout.CENTER);
    
        return welcomePanel;
    }

    private JPanel createBooksPanel() {
        JPanel booksPanel = new JPanel(new BorderLayout());
        booksPanel.setBackground(new Color(230, 230, 230));

        // Header panel with title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(50, 100, 200));

        JLabel titleLabel = new JLabel("Books Page");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        booksPanel.add(headerPanel, BorderLayout.NORTH);

        // Table styling
        JTable bookTable = new JTable();
        styleTable(bookTable);



        JScrollPane scrollPane = new JScrollPane(bookTable);
        booksPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with sorting and back button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 245, 245));

        bottomPanel.add(createBackBtn(), BorderLayout.CENTER);
        booksPanel.add(bottomPanel, BorderLayout.SOUTH);

        updateTable.refreshTable(bookTable, "SELECT ID, BookName, Author, Publisher, Year, " +
                "CASE WHEN Status = 1 THEN 'Available' ELSE 'Unavailable' END AS Status FROM books");
        bookTable.setDefaultEditor(Object.class, null);

        JTableHeader header = bookTable.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = header.columnAtPoint(e.getPoint()); // Get the clicked column index
                sortTable(bookTable, columnIndex); // Sort the table by the clicked column
            }
        });







        return booksPanel;
    }




    private JPanel createBorrowedBooksPanel(String username) {
        JPanel borrowedBooksPanel = new JPanel(new BorderLayout());
        borrowedBooksPanel.setBackground(new Color(230, 230, 230));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(50, 100, 200));

        JLabel titleLabel = new JLabel("Borrowed Books Page");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        borrowedBooksPanel.add(headerPanel, BorderLayout.NORTH);
        // Create a table and set up its model
        JTable borrowedBooksTable = new JTable(fetchBorrowedBooks(username));
       styleTable(borrowedBooksTable);
        JScrollPane scrollPane = new JScrollPane(borrowedBooksTable);


        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 245, 245));

        bottomPanel.add(createBackBtn(), BorderLayout.CENTER);
        borrowedBooksPanel.add(bottomPanel, BorderLayout.SOUTH);
        borrowedBooksPanel.add(scrollPane, BorderLayout.CENTER);
        JTableHeader header = borrowedBooksTable.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = header.columnAtPoint(e.getPoint()); // Get the clicked column index
                sortTable(borrowedBooksTable, columnIndex); // Sort the table by the clicked column
            }
        });

        return borrowedBooksPanel;
    }



    private JPanel createDueBooksPanel(String username){

        GetInfoFromDB.setDue(username);

        JPanel DueBooksPanel = new JPanel(new BorderLayout());
        DueBooksPanel.setBackground(new Color(230, 230, 230));


        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(50, 100, 200));

        JLabel titleLabel = new JLabel("Due Books Page");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        DueBooksPanel.add(headerPanel, BorderLayout.NORTH);
        // Create a table and set up its model
        JTable DueBooksTable = new JTable(GetInfoFromDB.fetchDueBooks(username));
        styleTable(DueBooksTable);
        JScrollPane scrollPane = new JScrollPane(DueBooksTable);
    
        // Add the scroll pane (which contains the table) to the panel
        DueBooksPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 245, 245));

        bottomPanel.add(createBackBtn(), BorderLayout.CENTER);
        DueBooksPanel.add(bottomPanel, BorderLayout.SOUTH);

        JTableHeader header = DueBooksTable.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = header.columnAtPoint(e.getPoint()); // Get the clicked column index
                sortTable(DueBooksTable, columnIndex); // Sort the table by the clicked column
            }
        });

        return DueBooksPanel;
    }



    private JTable styleTable(JTable table){
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setFont(new Font("Roboto", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(50, 100, 200)); // Header background color (blue)
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(255, 215, 0)); // Highlight selected row background
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(230, 230, 230)); // Set grid color for the table

        return table;
    }

    private JLabel createStatCard(String text, Color backgroundColor, String panelName) {
        JLabel card = new JLabel(text, SwingConstants.CENTER);
        card.setFont(new Font("Roboto", Font.BOLD, 18)); // Modern font
        card.setForeground(Color.WHITE); // White text
        card.setOpaque(true);
        card.setBackground(backgroundColor); // Dynamic background color
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1), // Light border
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Inner padding
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Hand cursor on hover

        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(backgroundColor.darker()); // Darken on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(backgroundColor); // Reset on exit
            }

            public void mouseClicked(MouseEvent e) {
                showPanel(panelName); // Change panel on click
            }
        });



        return card;
    }

private JPanel createBackBtn(){
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

    JButton backBtn = new JButton("Back");
        styleButton(backBtn);
        backBtn.addActionListener(e -> showPanel("Welcome"));
        buttonPanel.add(backBtn);
        return buttonPanel;
}




    private void styleButton(JButton button) {
        button.setBackground(new Color(100, 100, 100));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panelName);
        contentPanel.revalidate(); // Ensure layout updates
        contentPanel.repaint();    // Refresh the UI
    }


}
 