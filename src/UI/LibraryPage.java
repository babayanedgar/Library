/**
 * Copyright (c) 2025. Author - Edgar Babayan
 *
 */

package UI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/* 
 *  this is the class of the app for library computer 
 *   @return frame LibraryPage Frame
 */

public class LibraryPage extends JFrame {
    protected final static JFrame frame = new JFrame();
    protected static JPanel panel = new JPanel();
   protected JLabel welcomeText = new JLabel("Welcome to Vertex Library! " +
    "Choose one of the options" );
    protected final JButton borrowButton;
    protected final JButton returnButton;






    public LibraryPage() {
        frame.setTitle("Vertex Library");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 500);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        panel.setLayout(new GridBagLayout()); // Use GridBagLayout for flexibility
        panel.setBackground(new Color(240, 248, 255));

        // Welcome text
        welcomeText.setFont(new Font("Roboto", Font.BOLD, 20));
        welcomeText.setForeground(new Color(33, 37, 41));

        // Create Borrow Panel
        CreateBorrowPanel bPanel = new CreateBorrowPanel();

        // Borrow Button
        borrowButton = new JButton("Borrow Books");
        borrowButton.setFont(new Font("Roboto", Font.BOLD, 16));
        borrowButton.setPreferredSize(new Dimension(200, 50));
        borrowButton.setBackground(new Color(70, 130, 180));
        borrowButton.setForeground(Color.WHITE);
        borrowButton.setFocusPainted(false);
        borrowButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        borrowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().removeAll();
                frame.add(bPanel.BorrowPanel(), BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }
        });

        // Create Return Panel
        ReturnPanel rPanel = new ReturnPanel();

        // Return Button
        returnButton = new JButton("Return Books");
        returnButton.setFont(new Font("Roboto", Font.BOLD, 16));
        returnButton.setPreferredSize(new Dimension(200, 50));
        returnButton.setBackground(new Color(70, 130, 180));
        returnButton.setForeground(Color.WHITE);
        returnButton.setFocusPainted(false);
        returnButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().removeAll();
                frame.add(rPanel.ReturnPanel(), BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }
        });

        // GridBagConstraints for positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding

        // Add welcome text
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(welcomeText, gbc);

        // Add borrow button
        gbc.gridy = 1;
        panel.add(borrowButton, gbc);

        // Add return button
        gbc.gridy = 2;
        panel.add(returnButton, gbc);

        // Add panel to frame
        frame.add(panel, BorderLayout.CENTER);

        // Make the frame resizable
        frame.setResizable(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        new LibraryPage();
    }
}
