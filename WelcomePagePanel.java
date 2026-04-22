import javax.swing.*;
import java.awt.*;

public class WelcomePagePanel extends JPanel {
    public WelcomePagePanel(JPanel cardPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());
        setBackground(new Color(0x999893));

        // Panel to hold the logo and buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(new Color(0x999893));

        // Logo below the header, just above the buttons
        ImageIcon logoIcon = new ImageIcon("src/WhiteBoard.png");
        Image image = logoIcon.getImage();
        Image resizedImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        JLabel logoLabel = new JLabel(resizedIcon);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add some padding to move it lower
        centerPanel.add(logoLabel, BorderLayout.NORTH); // Place the logo at the top of the center panel

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Icons for buttons
        ImageIcon staffIcon = new ImageIcon("src/StaffButton.png");
        ImageIcon studentIcon = new ImageIcon("src/Student.png");

        JButton guestButton = new JButton(studentIcon);
        guestButton.setPreferredSize(new Dimension(200, 50));
        guestButton.setFocusPainted(false);
        guestButton.setBorderPainted(false);
        guestButton.setContentAreaFilled(false);
        guestButton.setOpaque(false);
        guestButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand symbol

        JButton staffButton = new JButton(staffIcon);
        staffButton.setPreferredSize(new Dimension(200, 50));
        staffButton.setPreferredSize(new Dimension(200, 50));
        staffButton.setFocusPainted(false);
        staffButton.setBorderPainted(false);
        staffButton.setContentAreaFilled(false);
        staffButton.setOpaque(false);
        staffButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand symbol


        // Add action listeners to buttons for navigation
        staffButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
        guestButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));

        buttonPanel.add(staffButton, gbc);
        gbc.gridy = 1;
        buttonPanel.add(guestButton, gbc);

        buttonPanel.setBackground(new Color(0x999893));

        // Add buttons panel to centerPanel
        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        // Add the centerPanel (with logo and buttons) to the main panel
        add(centerPanel, BorderLayout.CENTER);

        // Copyright text at the bottom
        JPanel copyrightPanel = new JPanel();
        copyrightPanel.setBackground(Color.GRAY);
        JLabel copyrightLabel = new JLabel("WhiteBoard Copyright", JLabel.CENTER);
        copyrightLabel.setForeground(Color.WHITE);
        copyrightPanel.add(copyrightLabel);
        add(copyrightPanel, BorderLayout.SOUTH);
    }
}