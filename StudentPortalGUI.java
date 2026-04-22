import javax.swing.*;
import java.awt.*;

public class StudentPortalGUI {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public StudentPortalGUI() {
        frame = new JFrame("Student Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 705);
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        ImageIcon iconAPP = new ImageIcon("src/WhiteBoard.png");
        frame.setIconImage(iconAPP.getImage());

        // Initialize CardLayout and cardPanel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Add Welcome Page
// Adding WelcomePagePanel to the cardPanel
        cardPanel.add(new WelcomePagePanel(cardPanel, cardLayout), "Welcome");
        // Add Login Screen
        cardPanel.add(new LoginPanel(cardPanel, cardLayout), "Login");

        cardPanel.add(new RegisterPanel(cardPanel, cardLayout), "Register");

        cardPanel.add(new ForgotPasswordPanel(cardPanel, cardLayout), "ForgotPassword");
        frame.add(cardPanel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new StudentPortalGUI());

    }
}
