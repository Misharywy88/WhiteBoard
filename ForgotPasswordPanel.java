import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ForgotPasswordPanel extends JPanel {

    public ForgotPasswordPanel(JPanel cardPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());
        setBackground(new Color(0xD3D3D3)); // Silver-like background color

        // Panel title
        JLabel titleLabel = new JLabel("Forgot Password", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Center form panel for input
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(0x999893)); // Silver background for uniformity

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email input field
        JLabel emailLabel = new JLabel("Enter your registered email:");
        JTextField emailField = new JTextField(20);

        // Password field for the new password
        JLabel newPasswordLabel = new JLabel("Enter your new password:");
        JPasswordField newPasswordField = new JPasswordField(20);

        // Buttons with hover effects
        JButton resetButton = new JButton("Reset Password");
        JButton backButton = new JButton("Back to Login");

        // Style buttons with a silver theme
        styleButton(resetButton);
        styleButton(backButton);

        // Add components to the form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(newPasswordLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(newPasswordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(resetButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(backButton, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Add button functionality
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String newPassword = new String(newPasswordField.getPassword());
                if (email.isEmpty() || newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            ForgotPasswordPanel.this,
                            "Please enter all fields.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (!isValidEmail(email)) {
                    JOptionPane.showMessageDialog(
                            ForgotPasswordPanel.this,
                            "Invalid email format.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    boolean emailExists = checkEmailInDatabase(email);
                    if (emailExists) {
                        // Update password
                        boolean updated = updatePasswordInDatabase(email, newPassword);
                        if (updated) {
                            JOptionPane.showMessageDialog(
                                    ForgotPasswordPanel.this,
                                    "Your password has been successfully reset.",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        } else {
                            JOptionPane.showMessageDialog(
                                    ForgotPasswordPanel.this,
                                    "Error resetting your password. Please try again.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                                ForgotPasswordPanel.this,
                                "This email is not registered.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Login"); // Navigate back to the LoginPanel
            }
        });
    }

    // Helper method to style buttons with a silver-like palette and hover effects
    private void styleButton(JButton button) {
        button.setBackground(new Color(0xC0C0C0)); // Silver background
        button.setForeground(Color.BLACK); // Black text
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Bold font
        button.setFocusPainted(false); // Remove focus border
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding for better appearance
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Set cursor to hand symbol

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0xA9A9A9)); // Darker silver on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0xC0C0C0)); // Revert to original silver color
            }
        });
    }

    /** Validate the email format */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    /** Check if the entered email exists in the database */
    private boolean checkEmailInDatabase(String email) {
        boolean exists = false;
        String dbUrl = "jdbc:mysql://localhost:3306/elms";
        String dbUser = "root";
        String dbPassword = "1234";

        String query = "SELECT COUNT(*) FROM Users WHERE Email = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Database connection error.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return exists;
    }

    /** Update the password in the database */
    private boolean updatePasswordInDatabase(String email, String newPassword) {
        boolean updated = false;
        String dbUrl = "jdbc:mysql://localhost:3306/elms";
        String dbUser = "root";
        String dbPassword = "1234";

        String query = "UPDATE Users SET Password = ? WHERE Email = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newPassword); // Set the new password
            stmt.setString(2, email);       // Set the email condition
            int rowsAffected = stmt.executeUpdate();

            updated = rowsAffected > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Database connection error.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return updated;
    }
}