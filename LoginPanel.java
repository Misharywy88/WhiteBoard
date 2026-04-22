import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPanel extends JPanel {

    public LoginPanel(JPanel cardPanel, CardLayout cardLayout) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Set the preferred size of the login panel
        setPreferredSize(new Dimension(200, 1000));

        // Set background color of the login panel
        setBackground(new Color(0x999893));

        // === Logo at the top ===
        ImageIcon logoIcon = new ImageIcon("src/WhiteBoard.png"); // Replace with your logo path
        Image image = logoIcon.getImage();
        Image resizedImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        JLabel logoLabel = new JLabel(resizedIcon);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(logoLabel, gbc);

        // === Username field with icon ===
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        ImageIcon usernameIcon = new ImageIcon("src/user.png"); // Replace with your username icon path
        Image userImage = usernameIcon.getImage();
        Image resizedImageUser = userImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        ImageIcon resizedIconUser = new ImageIcon(resizedImageUser);

        JLabel usernameImageLabel = new JLabel(resizedIconUser);
        JTextField usernameField = new JTextField(12);
        usernameField.setPreferredSize(new Dimension(140, 20));

        usernamePanel.add(usernameImageLabel);
        usernamePanel.add(usernameField);
        usernamePanel.setBackground(new Color(0x999893));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(usernamePanel, gbc);

        // === Password field with icon ===
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        ImageIcon passwordIcon = new ImageIcon("src/pass.png"); // Replace with your password icon path
        Image passwordImage = passwordIcon.getImage();
        Image resizedImagePass = passwordImage.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        ImageIcon resizedPass = new ImageIcon(resizedImagePass);

        JLabel passwordImageLabel = new JLabel(resizedPass);
        JPasswordField passwordField = new JPasswordField(12);
        passwordField.setPreferredSize(new Dimension(140, 20));

        passwordPanel.add(passwordImageLabel);
        passwordPanel.add(passwordField);
        passwordPanel.setBackground(new Color(0x999893));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(passwordPanel, gbc);

        // === Login button ===
        ImageIcon loginIcon = new ImageIcon("src/LoginButton.png");
        JButton loginButton = new JButton(loginIcon);
        loginButton.setPreferredSize(new Dimension(200, 50));
        loginButton.setMaximumSize(new Dimension(200, 50));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand symbol


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Authenticate the user
                UserAuthenticationResult authResult = authenticateUserAndFetchID(username, password);

                if (authResult.getUserID() > 0) {
                    if (authResult.getUserRole().equals("Student")) {
                        // Navigate to the StudentMenu
                        cardPanel.add(new StudentMenu(authResult.getUserID()), "StudentMenu");
                        cardLayout.show(cardPanel, "StudentMenu");
                    } else if (authResult.getUserRole().equals("Instructor")) {
                        // Navigate to the InstructorMenu
                        cardPanel.add(new InstructorMenu(authResult.getUserID()), "InstructorMenu");
                        cardLayout.show(cardPanel, "InstructorMenu");
                    } else if (authResult.getUserRole().equals("Admin")) {
                        // Navigate to the AdminMenu
                        cardPanel.add(new AdminMenu(authResult.getUserID()), "AdminMenu");
                        cardLayout.show(cardPanel, "AdminMenu");
                    } else {
                        JOptionPane.showMessageDialog(cardPanel, "Unknown role", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(cardPanel, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        // === Forgot Password button ===
        ImageIcon forgotPasswordIcon = new ImageIcon("src/ForgotPasswordButton.png");
        JButton forgotPasswordButton = new JButton(forgotPasswordIcon);
        forgotPasswordButton.setPreferredSize(new Dimension(200, 50));
        forgotPasswordButton.setMaximumSize(new Dimension(200, 50));
        forgotPasswordButton.setFocusPainted(false);
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setOpaque(false);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand symbol

        forgotPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "ForgotPassword"); // Navigate to ForgotPasswordPanel
            }
        });

        gbc.gridy = 4;
        add(forgotPasswordButton, gbc);

        // === Register button ===
        ImageIcon registerIcon = new ImageIcon("src/RegisterButton.png");
        JButton registerButton = new JButton(registerIcon);
        registerButton.setPreferredSize(new Dimension(200, 50));
        registerButton.setMaximumSize(new Dimension(200, 50));
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setOpaque(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand symbol


        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Register");
            }
        });

        gbc.gridy = 5;
        add(registerButton, gbc);
    }

    /**
     * Authenticate a user with a database query and fetch their user ID and role.
     *
     * @param username The entered username.
     * @param password The entered password.
     * @return A UserAuthenticationResult containing user ID and role if authentication succeeds; otherwise, returns a result with -1 and null.
     */
    private UserAuthenticationResult authenticateUserAndFetchID(String username, String password) {
        int userID = -1;
        String userRole = null;

        // Database connection information
        final String DB_URL = "jdbc:mysql://localhost:3306/elms";
        final String DB_USER = "root";
        final String DB_PASS = "1234";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Use prepared statement to prevent SQL injection
            String query = "SELECT User_ID, Role FROM Users WHERE Username = ? AND Password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userID = resultSet.getInt("User_ID");
                userRole = resultSet.getString("Role");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return new UserAuthenticationResult(userID, userRole);
    }

    /**
     * A utility class to hold authentication results.
     */
    private static class UserAuthenticationResult {
        private final int userID;
        private final String userRole;

        public UserAuthenticationResult(int userID, String userRole) {
            this.userID = userID;
            this.userRole = userRole;
        }

        public int getUserID() {
            return userID;
        }

        public String getUserRole() {
            return userRole;
        }
    }
}