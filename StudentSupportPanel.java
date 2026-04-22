import javax.swing.*;
import java.awt.*;

public class StudentSupportPanel extends JPanel {

    public StudentSupportPanel(JPanel cardPanel, CardLayout cardLayout) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0xC0C0C0));  // Silver-like color

        // Title
        JLabel titleLabel = new JLabel("Student Support", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        add(titleLabel);

        // Dummy Support Options
        JLabel supportLabel = new JLabel("<html>Contact Support<br>Health Services<br>Counseling Services<br>Academic Assistance</html>");
        supportLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(supportLabel);
    }
}
