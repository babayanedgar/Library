package UI;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import utilities.*;

import static utilities.BubbleSort.sortTable;
import static utilities.GetInfoFromDB.*;

/**
 * CEOPage is the main interface for the CEO. It provides navigation to 
 * manage employees, books, and view statistics via a user-friendly GUI.
 */

    public class CEOPage extends JFrame {

        private final JFrame frm = new JFrame();
        private final JPanel mainPanel = new JPanel(); // Main panel that holds sidebar and content area
        private final JPanel contentPanel = new JPanel(new CardLayout()); // Content panel with CardLayout
        private JTable employeeTable;
    private final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

    static JLabel allBooksLabel;
    static JLabel allEmployeesLabel;
    static JLabel borrowedBooksLabel;
/**
     * Constructor to initialize and set up the CEOPage interface.
     * @param username Username of the logged-in CEO (can be used for personalization or logging).
     */

        public CEOPage(String username) {
            frm.setSize(750, 750);
            frm.setDefaultCloseOperation(EXIT_ON_CLOSE);
            frm.setTitle("Vertex Library - CEO");

            // Set up the frame layout
            frm.setLayout(new BorderLayout());
            // Set up the main panel with BorderLayout
            mainPanel.setLayout(new BorderLayout());
            mainPanel.setBackground(new Color(10, 20, 30));

            // Add sidebar and content panel to the main panel
            mainPanel.add(createSidebar(new String[]{"Dashboard", "Manage Employees", "Manage Books"}), BorderLayout.WEST);
            mainPanel.add(contentPanel, BorderLayout.CENTER); // Center area with CardLayout

            // Initialize content panels
            initContentPanels();

            // Add main panel to the frame
            frm.add(mainPanel);


            

            frm.setVisible(true);
        }
    /**
     * Creates the sidebar for navigation between sections.
     * @param values Array of section names to include as buttons in the sidebar.
     * @return A JPanel containing the sidebar layout and functionality.
     */
        private JPanel createSidebar(String[] values) {
            JPanel sidebar = new JPanel();
            sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            sidebar.setBackground(new Color(50, 100, 200));
            sidebar.setPreferredSize(new Dimension(200, getHeight())); // Fixed width for sidebar

            for (String valueText : values) {
                JPanel buttonPanel = new JPanel(new GridLayout(1, 1));
                buttonPanel.setPreferredSize(new Dimension(180, 45)); // Set button size
                buttonPanel.setBackground(new Color(255, 255, 255));

                JLabel label = new JLabel(valueText, SwingConstants.CENTER);
                label.setFont(new Font("Roboto", Font.BOLD, 14));
                label.setForeground(new Color(0, 0, 0));
                buttonPanel.add(label);

                // Add hover and click effect
                buttonPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        showPanel(valueText); // Switch panel on click
                    }

                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        buttonPanel.setBackground(new Color(255, 135, 0)); // Highlight color
                        label.setForeground(Color.WHITE); // White text on hover
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        buttonPanel.setBackground(new Color(255, 255, 255)); // Reset background
                        label.setForeground(Color.BLACK); // Reset text color
                    }
                });


                sidebar.add(buttonPanel);
            }
            sidebar.add(Box.createVerticalGlue()); // Push the Logout button to the bottom

            JButton logoutButton = new JButton("Logout");
            logoutButton.setBackground(new Color(255, 69, 58)); // Red color for Logout button
            logoutButton.setForeground(Color.WHITE);
            logoutButton.setFocusPainted(false);
            logoutButton.setPreferredSize(new Dimension(180, 45));
            logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int res = JOptionPane.showConfirmDialog(null, "Are you sure to log out?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (res == JOptionPane.YES_OPTION) {
                        frm.dispose();
                        new LoginForm();
                    }
                }
            });
            sidebar.add(logoutButton);

            return sidebar;
        }

  /**
     * Initializes content panels for Dashboard, Manage Employees, and Manage Books.
     */
  private void initContentPanels() {
          
            JPanel dashboardPanel = dashboardPanel();

            contentPanel.add(dashboardPanel, "Dashboard");

            // Initialize and add the People panel
            JPanel peoplePanel = createPeoplePanel();
            contentPanel.add(peoplePanel, "Manage Employees");

            JPanel libraryPanel = createLibraryPanel();
            contentPanel.add(libraryPanel, "Manage Books");

          


        }
    /**
     * Creates the Dashboard panel for the CEO. This panel includes a welcome section,
     * library statistics, and navigation buttons to other sections (Manage Books and Manage Employees).
     * @return A JPanel representing the Dashboard.
     */
    private JPanel dashboardPanel() {
        // Create the main dashboard panel with a BorderLayout
        JPanel dashboard = new JPanel();
        dashboard.setLayout(new BorderLayout(10, 10)); // Add spacing between components
        dashboard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding around the panel

        // Welcome Section
        JLabel welcomeLabel = new JLabel("Welcome to Vertex Library", SwingConstants.CENTER); // Welcome message
        welcomeLabel.setFont(new Font("Roboto", Font.BOLD, 24)); // Set font for welcome message
        welcomeLabel.setForeground(new Color(34, 112, 147)); // Custom blue-green text color
        dashboard.add(welcomeLabel, BorderLayout.NORTH); // Add the welcome label to the top (North)

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 10, 10)); // Grid layout for displaying statistics
        statsPanel.setBorder(BorderFactory.createTitledBorder("Library Statistics")); // Add a titled border to stats panel

        // Labels to display library statistics
        allBooksLabel = new JLabel("Total Books: " + getTheNumberofAllBooks()); // Display total books
        allEmployeesLabel = new JLabel("Total Employees: " + getTheNumberofAllEMPs()); // Display total employees
        borrowedBooksLabel = new JLabel("Total Borrowed Books: " + getTheNumberofBorrowedBooks()); // Display total borrowed books

        // Set font styles for the statistics labels
        allBooksLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        allEmployeesLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        borrowedBooksLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        // Add the statistics labels to the stats panel
        statsPanel.add(allBooksLabel);
        statsPanel.add(allEmployeesLabel);
        statsPanel.add(borrowedBooksLabel);

        // Add the stats panel to the center of the dashboard
        dashboard.add(statsPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Panel with two buttons in a row
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Add spacing above the buttons

        // Create "Manage Books" button
        JButton manageBooksButton = new JButton("Manage Books");
        manageBooksButton.setBackground(new Color(100, 149, 237)); // Set light blue background color
        manageBooksButton.setForeground(Color.WHITE); // White text color
        manageBooksButton.setFocusPainted(false); // Remove focus border
        manageBooksButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set bold font for button text

        // Create "Manage Employees" button
        JButton manageEmployeesButton = new JButton("Manage Employees");
        manageEmployeesButton.setBackground(new Color(60, 179, 113)); // Set light green background color
        manageEmployeesButton.setForeground(Color.WHITE); // White text color
        manageEmployeesButton.setFocusPainted(false); // Remove focus border
        manageEmployeesButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set bold font for button text

        // Add action listeners to handle button clicks
        manageBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel("Manage Books");
            }
        }); // Switch to Manage Books panel

        manageBooksButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                manageBooksButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                manageBooksButton.setBackground(new Color(100, 149, 237).darker()); // Highlight color
            }

            @Override
            public void mouseExited(MouseEvent e) {
                manageBooksButton.setCursor(Cursor.getDefaultCursor());
                manageBooksButton.setBackground(new Color(100, 149, 237)); // Default button color
            }
        });


        manageEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel("Manage Employees");
            }
        }); // Switch to Manage Employees panel

        manageEmployeesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                manageEmployeesButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                manageEmployeesButton.setBackground(new Color(60, 179, 113).darker()); // Highlight color
            }

            @Override
            public void mouseExited(MouseEvent e) {
                manageEmployeesButton.setCursor(Cursor.getDefaultCursor());
                manageEmployeesButton.setBackground(new Color(60, 179, 113)); // Default button color
            }
        });

        // Add the buttons to the buttons panel
        buttonsPanel.add(manageBooksButton);
        buttonsPanel.add(manageEmployeesButton);

        // Add the buttons panel to the bottom of the dashboard
        dashboard.add(buttonsPanel, BorderLayout.SOUTH);

        // Return the complete dashboard panel
        return dashboard;
    }

          /**
     * Creates the Manage Employees panel, containing a table and control buttons.
     * @return A JPanel containing the Manage Employees interface.
     */

        private JPanel createPeoplePanel() {
            JPanel peoplePanel = new JPanel(new BorderLayout());
            peoplePanel.setBackground(Color.WHITE);
        
            // Table Design
            employeeTable = new JTable();
           styleTable(employeeTable);
        
            // Refresh Table Data
            updateTable.refreshTable(employeeTable, "SELECT ID, Name, Lastname, Username, Position FROM employees");
        
            JScrollPane scrollPane = new JScrollPane(employeeTable);
            peoplePanel.add(scrollPane, BorderLayout.CENTER);
        
            // Button Panel
            buttonPanel.setBackground(new Color(245, 245, 245));
        
            // Add Employee Button
            JButton addEmployeeButton = new JButton("Add Employee");
            styleButton(addEmployeeButton, new Color(50, 150, 250), Color.WHITE);
            addEmployeeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addEmployee();
                    updateTable.refreshTable(employeeTable, "SELECT ID, Name, Lastname, Username, Position FROM employees");
                }
            });


            // Remove Employee Button
            JButton removeEmployeeButton = new JButton("Remove Employee");
            styleButton(removeEmployeeButton, new Color(220, 20, 60), Color.WHITE);
            removeEmployeeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    RemoveFromDB.removeSelectedEmployee(employeeTable);
                    updateTable.refreshTable(employeeTable, "SELECT ID, Name, Lastname, Username, Position FROM employees");
                }
            });
        
            // Back Button
            JButton backBtn = new JButton("Back");
            styleButton(backBtn, new Color(100, 100, 100), Color.WHITE);
            backBtn.addActionListener(e -> showPanel("Dashboard"));
        
            // Add buttons to the panel
            buttonPanel.add(addEmployeeButton);
            buttonPanel.add(removeEmployeeButton);
            buttonPanel.add(backBtn);

            JTableHeader header = employeeTable.getTableHeader();
            header.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int columnIndex = header.columnAtPoint(e.getPoint()); // Get the clicked column index
                    sortTable(employeeTable, columnIndex); // Sort the table by the clicked column
                }
            });

            // Disable table editing
            employeeTable.setDefaultEditor(Object.class, null);
        
            // Add components to the main panel
            peoplePanel.add(buttonPanel, BorderLayout.SOUTH);
        
            return peoplePanel;
        }

   /**
 * Creates the "Manage Books" panel, which includes a table to display book data
 * and buttons to add, remove, or navigate back to the dashboard.
 * @return A JPanel containing the book management interface.
 */
private JPanel createLibraryPanel() {
    // Main panel with BorderLayout for managing books
    JPanel libraryPanel = new JPanel(new BorderLayout());
    libraryPanel.setBackground(Color.WHITE); // Set background color to white for a clean look

    // Set up the table to display book data
    JTable bookTable = new JTable(); // Create a new JTable for books
    styleTable(bookTable);
    // Populate the table with book data from the database
    updateTable.refreshTable(bookTable, "SELECT ID, BookName, Author, Publisher, Year, " +
            "CASE WHEN Status = 1 THEN 'Available' ELSE 'Unavailable' END AS Status FROM books");

    // Add the table to a scroll pane for scrolling functionality
    JScrollPane scrollPane = new JScrollPane(bookTable);
    libraryPanel.add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the center of the panel

    // Create a button panel for managing books
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Button layout
    buttonPanel.setBackground(new Color(245, 245, 245)); // Light gray background for the button panel

    // Add Book Button
    JButton addBookButton = new JButton("Add Book"); // Button to add a new book
    styleButton(addBookButton, new Color(50, 150, 250), Color.WHITE); // Apply styles to the button
    addBookButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new BookAdditionForm(bookTable); // Open the Book Addition form
            updateTable.refreshTable(bookTable, "SELECT ID, BookName, Author, Publisher, Year, " +
                    "CASE WHEN Status = 1 THEN 'Available' ELSE 'Unavailable' END AS Status FROM books");
        }
    });
    addBookButton.addMouseMotionListener(new MouseAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            addBookButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        }
    });

    // Remove Book Button
    JButton removeBookButton = new JButton("Remove Book"); // Button to remove a selected book
    styleButton(removeBookButton, new Color(220, 20, 60), Color.WHITE); // Apply styles to the button
    removeBookButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            RemoveFromDB.removeBook(bookTable); // Remove the selected book from the database
            updateTable.refreshTable(bookTable, "SELECT ID, BookName, Author, Publisher, Year, " +
                    "CASE WHEN Status = 1 THEN 'Available' ELSE 'Unavailable' END AS Status FROM books");
        }
    });

    // Back Button
    JButton backBtn = new JButton("Back"); // Button to navigate back to the dashboard
    styleButton(backBtn, new Color(100, 100, 100), Color.WHITE); // Apply styles to the button
    backBtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            showPanel("Dashboard");
        }
    }); // Switch back to the Dashboard panel

    // Add buttons to the button panel
    buttonPanel.add(addBookButton);
    buttonPanel.add(removeBookButton);
    buttonPanel.add(backBtn);

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

    // Add the button panel to the bottom of the library panel
    libraryPanel.add(buttonPanel, BorderLayout.SOUTH);

    // Return the complete library panel
    return libraryPanel;
}





    // Helper method to style buttons
    private void styleButton(JButton button, Color bgColor, Color textColor) {
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
    
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    

    private void addEmployee() {
        new RegistrationForm(employeeTable);
    }

    private JTable styleTable(JTable table){
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setFont(new Font("Roboto", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(50, 100, 200));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(255, 215, 0));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(230, 230, 230));

        return table;
    }




    


  
   
 
 
     // Method to show the specified panel based on the name
     private void showPanel(String panelName) {
         CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
         cardLayout.show(contentPanel, panelName);
     }
 

}
