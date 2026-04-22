// Dependency imports
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.regex.*;

public class RegisterPanel extends JPanel {
    public RegisterPanel(JPanel cardPanel, CardLayout cardLayout) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        setBackground(new Color(0x999893));

        // Title
        JLabel title = new JLabel("Register", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;

        // Form fields and labels are unchanged:
        gbc.gridy++;
        add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        JTextField firstNameField = new JTextField(12);
        add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        JTextField lastNameField = new JTextField(12);
        add(lastNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(12);
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Date of Birth (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        JTextField dobField = new JTextField(12);
        add(dobField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        JTextField phoneField = new JTextField(12);
        add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(12);
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        JTextField usernameField = new JTextField(12);
        add(usernameField, gbc);

        setBackground(new Color(0x999893)); // Parent container's background color

        // Gender Radio Buttons remain unchanged
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Select Gender:"), gbc);
        JRadioButton maleRadio = new JRadioButton("Male");
        maleRadio.setBackground(new Color(0x999893)); // Set the background color to match
        maleRadio.setOpaque(true); //
        JRadioButton femaleRadio = new JRadioButton("Female");
        femaleRadio.setBackground(new Color(0x999893)); // Set the background color to match
        femaleRadio.setOpaque(true); //
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        JPanel genderPanel = new JPanel();
        genderPanel.setBackground(new Color(0x999893)); // Applies a consistent background to the RegisterPanel
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        gbc.gridx = 1;
        add(genderPanel, gbc);

        // Role selection is unchanged
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Sign up as:"), gbc);
        JRadioButton studentRadio = new JRadioButton("Student");
        studentRadio.setOpaque(true); //
        studentRadio.setBackground(new Color(0x999893)); // Set the background color to match

        JRadioButton instructorRadio = new JRadioButton("Instructor");
        instructorRadio.setOpaque(false); //
        instructorRadio.setBackground(new Color(0x999893)); // Set the background color to match

        JRadioButton adminRadio = new JRadioButton("Admin");
        adminRadio.setOpaque(false); //
        adminRadio.setBackground(new Color(0x999893)); // Set the background color to match

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(studentRadio);
        roleGroup.add(instructorRadio);
        roleGroup.add(adminRadio);
        JPanel rolePanel = new JPanel();
        rolePanel.setBackground(new Color(0x999893)); // Applies a consistent background to the RegisterPanel

        rolePanel.add(studentRadio);
        rolePanel.add(instructorRadio);
        rolePanel.add(adminRadio);
        gbc.gridx = 1;
        add(rolePanel, gbc);

        // Register button
        JButton registerButton = new JButton();

// Set the button's icon to the image
        ImageIcon registerButtonIcon = new ImageIcon("src/RegisterButton.png");
        registerButton.setIcon(registerButtonIcon);

// Remove button appearance like borders, focus, and content area for a clean image look
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setOpaque(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand symbol


// Add the button to the layout
        gbc.gridx = 1; // Set X position
        gbc.gridy++; // Adjust Y position to move it down
        add(registerButton, gbc);

        // Create the "<-" button
        JButton backButton = new JButton("<---");
        backButton.setBackground(new Color(0x999893));
// Set GridBagConstraints to position it at the bottom left
        gbc.gridx = 0; // First column
        gbc.gridy = GridBagConstraints.RELATIVE; // Relative position at the bottom
        gbc.anchor = GridBagConstraints.SOUTHWEST; // Align to the bottom-left corner
        gbc.insets = new Insets(10, 10, 10, 10); // Add some padding around the button

// Add an ActionListener to go back to LoginPage
        backButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Login"); // Navigate to the LoginPage
        });

// Add the button to the RegisterPanel
        add(backButton, gbc);

        // Register button action listener
        registerButton.addActionListener(e -> {
            // Collect form data
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String fullName = firstName + " " + lastName;
            String email = emailField.getText().trim();
            String dob = dobField.getText();
            String phone = phoneField.getText().trim();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            String role;
            if (studentRadio.isSelected()) role = "Student";
            else if (instructorRadio.isSelected()) role = "Instructor";
            else role = "Admin";
            String gender = maleRadio.isSelected() ? "Male" : "Female";

            // Gender validation
            if (!maleRadio.isSelected() && !femaleRadio.isSelected()) {
                JOptionPane.showMessageDialog(this, "Please select your gender.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Client-side validation (following the given constraints)

            if (!Pattern.matches("^[A-Za-z ]+$", fullName)) {
                JOptionPane.showMessageDialog(this, "Full name must contain only alphabets and spaces.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Pattern.matches("^05[0-9]{8}$", phone)) {
                JOptionPane.showMessageDialog(this, "Phone number must start with '05' and be exactly 10 digits long.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.endsWith("@iau.edu.sa")) {
                JOptionPane.showMessageDialog(this, "Email must end with '@iau.edu.sa'.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Pattern.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}$", password)) {
                JOptionPane.showMessageDialog(this,
                        "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, and one digit.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/elms",
                    "root",
                    "1234")) {
                String query = "INSERT INTO PendingRegistrations (Full_Name, DateOfBirth, PhoneNumber, Email, Username, Password, Role, Gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, fullName);
                stmt.setDate(2, Date.valueOf(dob));
                stmt.setString(3, phone);
                stmt.setString(4, email);
                stmt.setString(5, username);
                stmt.setString(6, password);
                stmt.setString(7, role);
                stmt.setString(8, gender);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Registration request submitted! You'll be able to login once an administrator approves your account.");
                    cardLayout.show(cardPanel, "Login");
                }

            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Username or Email already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An error occurred while processing your request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

    }
}