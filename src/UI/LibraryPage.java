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
   protected JLabel welcomeText = new JLabel("Welcome to Vertex Library", SwingConstants.CENTER);
    protected final JButton borrowButton;
    protected final JButton returnButton;






    public LibraryPage() {
        frame.setTitle("Vertex Library");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 620);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        Theme.applyIcon(frame);
        panel.setLayout(new GridBagLayout()); // Use GridBagLayout for flexibility
        panel.setBackground(Theme.WHITE);

        // Welcome text
        welcomeText.setFont(Theme.heading(28));
        welcomeText.setForeground(Theme.NAVY);
        JLabel subtitle = new JLabel("Choose one of the options below.", SwingConstants.CENTER);
        subtitle.setFont(Theme.body(15));
        subtitle.setForeground(Theme.SLATE);

        // Create Borrow Panel
        CreateBorrowPanel bPanel = new CreateBorrowPanel();

        // Borrow Button
        borrowButton = Theme.button("Borrow books", Theme.ButtonStyle.PRIMARY);
        borrowButton.setFont(Theme.strong(16));
        borrowButton.setPreferredSize(new Dimension(280, 52));
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
        returnButton = Theme.button("Return books", Theme.ButtonStyle.SECONDARY);
        returnButton.setFont(Theme.strong(16));
        returnButton.setPreferredSize(new Dimension(280, 52));
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
        gbc.insets = new Insets(6, 10, 6, 10); // Add padding

        // Logo and welcome text
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel(Theme.logo(260)), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(18, 10, 4, 10);
        panel.add(welcomeText, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 10, 28, 10);
        panel.add(subtitle, gbc);

        // Add borrow button
        gbc.gridy = 3;
        gbc.insets = new Insets(6, 10, 6, 10);
        panel.add(borrowButton, gbc);

        // Add return button
        gbc.gridy = 4;
        panel.add(returnButton, gbc);

        // Add panel to frame
        frame.add(panel, BorderLayout.CENTER);

        // Make the frame resizable
        frame.setResizable(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Theme.install();
        new LibraryPage();
    }
}
