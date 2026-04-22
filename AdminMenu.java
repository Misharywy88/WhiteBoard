import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class AdminMenu extends JPanel {
    // UI Constants
    private static final Color LIGHT_BACKGROUND = new Color(240, 240, 240);
    private static final Color HEADER_BACKGROUND = new Color(192, 192, 192);
    private static final Color DASHBOARD_BACKGROUND = new Color(211, 211, 211);
    private static final Color BUTTON_BACKGROUND = new Color(169, 169, 169);
    private static final Color TEXT_COLOR = new Color(60, 60, 60);
    private static final String UI_FONT = "Arial";

    // Class variables
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel;
    private int userID;
    private String userFirstName;
    private DefaultTableModel pendingRegistrationsModel;
    private JTable pendingTable;


    // Constructor
    public AdminMenu(int userID) {
        this.userID = userID;
        setLayout(new BorderLayout());

        // Fetch the user's first name from the database
        this.userFirstName = fetchUserFirstName(userID);

        // Create main card panel for switching between different views
        cardPanel = new JPanel();
        cardPanel.setLayout(cardLayout);

        // Add header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Create and add the dashboard sidebar
        JPanel dashboardPanel = createDashboard();
        dashboardPanel.setPreferredSize(new Dimension(200, getHeight()));
        add(dashboardPanel, BorderLayout.WEST);

        // Create panels for different sections
        JPanel allStudentsPanel = createAllStudentsPanel();
        JPanel allInstructorsPanel = createAllInstructorsPanel();
        JPanel allCoursesPanel = createAllCoursesPanel();
        JPanel manageUsersPanel = createManageUsersPanel();
        JPanel addCoursePanel = createAddCoursePanel();
        JPanel addAdminPanel = createAddAdminPanel();
        JPanel accountSettingsPanel = createAccountSettingsPanel();
        JPanel aboutUsPanel = createAboutUsPanel();

        // Add panels to card layout
        cardPanel.add(allStudentsPanel, "students");
        cardPanel.add(allInstructorsPanel, "instructors");
        cardPanel.add(allCoursesPanel, "courses");
        cardPanel.add(manageUsersPanel, "users");
        cardPanel.add(addCoursePanel, "addCourse");
        cardPanel.add(addAdminPanel, "addAdmin");
        cardPanel.add(createPendingRegistrationsPanel(), "pendingRegistrations");
        cardPanel.add(createAccountSettingsPanel(), "accountSettings");
        cardPanel.add(createAboutUsPanel(), "aboutUs");

        // Show all students panel by default
        cardLayout.show(cardPanel, "students");

        // Add card panel to main layout
        add(cardPanel, BorderLayout.CENTER);
    }

    /** Fetch the user's first name from the database */
    private String fetchUserFirstName(int userID) {
        String firstName = "Admin"; // Default value

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT Full_Name FROM Users WHERE User_ID = ?")) {

            statement.setInt(1, userID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String fullName = resultSet.getString("Full_Name");
                    // Extract first name from full name
                    if (fullName != null && fullName.contains(" ")) {
                        firstName = fullName.split(" ")[0];
                    } else if (fullName != null) {
                        firstName = fullName;
                    }
                }
            }
        } catch (SQLException e) {
            logError("Error fetching user name", e);
        }

        return firstName;
    }

    /** Create the header panel */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(HEADER_BACKGROUND);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + userFirstName);
        welcomeLabel.setFont(new Font(UI_FONT, Font.BOLD, 16));
        welcomeLabel.setForeground(TEXT_COLOR);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font(UI_FONT, Font.PLAIN, 14));
        logoutButton.setBackground(new Color(235, 150, 150)); // Light reddish color
        logoutButton.setForeground(new Color(120, 40, 40)); // Darker red text for contrast
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setFocusPainted(false); // Remove focus border
        logoutButton.addActionListener(e -> showLogoutConfirmation());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }

    /** Show confirmation dialog before logging out */
    private void showLogoutConfirmation() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            // Go back to login screen
            Container container = getParent();
            while (!(container instanceof JFrame)) {
                container = container.getParent();
                if (container == null) {
                    return;
                }
            }

            JFrame frame = (JFrame) container;
            frame.getContentPane().removeAll();

            // Initialize CardLayout and cardPanel for login screen
            CardLayout mainCardLayout = new CardLayout();
            JPanel mainCardPanel = new JPanel(mainCardLayout);

            // Add Welcome Page
            mainCardPanel.add(new WelcomePagePanel(mainCardPanel, mainCardLayout), "Welcome");
            // Add Login Screen
            mainCardPanel.add(new LoginPanel(mainCardPanel, mainCardLayout), "Login");
            // Add Register Screen
            mainCardPanel.add(new RegisterPanel(mainCardPanel, mainCardLayout), "Register");
            // Add ForgotPassword Screen
            mainCardPanel.add(new ForgotPasswordPanel(mainCardPanel, mainCardLayout), "ForgotPassword");

            // Add the main card panel to the frame
            frame.getContentPane().add(mainCardPanel);

            // Show the login screen
            mainCardLayout.show(mainCardPanel, "Login");

            frame.revalidate();
            frame.repaint();
        }
    }

    /** Create a panel that displays all instructors */
    private JPanel createAllInstructorsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel title
        JLabel titleLabel = new JLabel("All Instructors");
        titleLabel.setFont(new Font(UI_FONT, Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table with instructor data
        String[] columnNames = {"ID", "Name", "Email", "College"};
        Object[][] data = fetchInstructorInfo();

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font(UI_FONT, Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font(UI_FONT, Font.BOLD, 14));
        table.getTableHeader().setBackground(HEADER_BACKGROUND);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setGridColor(new Color(211, 211, 211));
        table.setShowVerticalLines(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Center align the text in all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /** Create dashboard with navigation buttons */
    private JPanel createDashboard() {
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setBackground(DASHBOARD_BACKGROUND);
        dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));
        dashboardPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Dashboard title
        JLabel dashboardLabel = new JLabel("Dashboard");
        dashboardLabel.setFont(new Font(UI_FONT, Font.BOLD, 20));
        dashboardLabel.setForeground(TEXT_COLOR);
        dashboardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashboardPanel.add(dashboardLabel);

        // Add spacing
        dashboardPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Add navigation buttons
        dashboardPanel.add(createSidebarButton("All Students", "students"));
        dashboardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        dashboardPanel.add(createSidebarButton("All Instructors", "instructors"));
        dashboardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        dashboardPanel.add(createSidebarButton("All Courses", "courses"));
        dashboardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        dashboardPanel.add(createSidebarButton("Manage Users", "users"));
        dashboardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        dashboardPanel.add(createSidebarButton("Add Course", "addCourse"));
        dashboardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        dashboardPanel.add(createSidebarButton("Add Admin", "addAdmin"));
        dashboardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        dashboardPanel.add(createSidebarButton("Pending Registrations", "pendingRegistrations")); // Add this button
        dashboardPanel.add(Box.createVerticalStrut(10));
        dashboardPanel.add(createSidebarButton("Account settings", "accountSettings"));
        dashboardPanel.add(Box.createVerticalStrut(10));
        dashboardPanel.add(createSidebarButton("About Us", "aboutUs"));

        return dashboardPanel;
    }

    /** Create a sidebar button */
    private JButton createSidebarButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        button.setBackground(BUTTON_BACKGROUND);
        button.setForeground(TEXT_COLOR);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addActionListener(e -> cardLayout.show(cardPanel, cardName));

        return button;
    }

    /** Create panel for managing users */
    private JPanel createManageUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel title
        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(new Font(UI_FONT, Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Create a tabbed pane for different user categories
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(UI_FONT, Font.PLAIN, 16));

        // Add tabs for different user groups
        tabbedPane.addTab("All Users", createManageAllUsersPanel());
        tabbedPane.addTab("Students", createManageStudentsPanel());
        tabbedPane.addTab("Instructors", createManageInstructorsPanel());

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    /** Create panel for managing all users */
    private JPanel createManageAllUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Table with all users
        String[] columnNames = {"ID", "Name", "Email", "Username", "Role", "Actions"};
        Object[][] data = fetchAllUsers();

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only make the Actions column editable
                return column == 5;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font(UI_FONT, Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font(UI_FONT, Font.BOLD, 14));
        table.getTableHeader().setBackground(HEADER_BACKGROUND);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setGridColor(new Color(211, 211, 211));
        table.setShowVerticalLines(true);

        // Center align the text in all columns except the last one (Actions)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set up renderer and editor for the Actions column
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonsRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonsEditor(table, this));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /** Fetch all users from the database */
    private Object[][] fetchAllUsers() {
        ArrayList<Object[]> userList = new ArrayList<>();
        String query = "SELECT User_ID, Full_Name, Email, Username, Role FROM Users ORDER BY User_ID";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                userList.add(new Object[]{
                        resultSet.getInt("User_ID"),
                        resultSet.getString("Full_Name"),
                        resultSet.getString("Email"),
                        resultSet.getString("Username"),
                        resultSet.getString("Role"),
                        "Actions"  // Placeholder for action buttons
                });
            }
        } catch (SQLException e) {
            logError("Error fetching users", e);
            JOptionPane.showMessageDialog(this,
                    "Error fetching users: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return userList.toArray(new Object[0][0]);
    }

    /** Create panel for managing students */
    private JPanel createManageStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BACKGROUND);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Table with student data
        String[] columnNames = {"ID", "Name", "Email", "Student Number", "College", "GPA"};
        Object[][] data = fetchStudentInfo();

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font(UI_FONT, Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font(UI_FONT, Font.BOLD, 14));
        table.getTableHeader().setBackground(HEADER_BACKGROUND);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setGridColor(new Color(211, 211, 211));
        table.setShowVerticalLines(true);

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /** Create panel for managing instructors */
    private JPanel createManageInstructorsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BACKGROUND);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Table with instructor data
        String[] columnNames = {"ID", "Name", "Email", "College"};
        Object[][] data = fetchInstructorInfo();

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font(UI_FONT, Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font(UI_FONT, Font.BOLD, 14));
        table.getTableHeader().setBackground(HEADER_BACKGROUND);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setGridColor(new Color(211, 211, 211));
        table.setShowVerticalLines(true);

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /** Handle user deletion */
    public void deleteUser(int userId) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this user? This action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "DELETE FROM Users WHERE User_ID = ?")) {

                statement.setInt(1, userId);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                            "User deleted successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshAllUserPanels();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to delete user. User may not exist.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                logError("Error deleting user", e);
                JOptionPane.showMessageDialog(this,
                        "Error deleting user: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Handle changing user role */
    public void changeUserRole(int userId, String currentRole) {
        String[] roles = {"Student", "Instructor", "Admin"};
        String newRole = (String) JOptionPane.showInputDialog(
                this,
                "Select new role for user:",
                "Change User Role",
                JOptionPane.QUESTION_MESSAGE,
                null,
                roles,
                currentRole
        );

        if (newRole != null && !newRole.equals(currentRole)) {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "UPDATE Users SET Role = ? WHERE User_ID = ?")) {

                statement.setString(1, newRole);
                statement.setInt(2, userId);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    // Handle the role-specific tables
                    if (currentRole.equals("Student")) {
                        removeStudentRecord(userId);
                    } else if (currentRole.equals("Instructor")) {
                        removeInstructorRecord(userId);
                    }

                    if (newRole.equals("Student")) {
                        addStudentRecord(userId);
                    } else if (newRole.equals("Instructor")) {
                        addInstructorRecord(userId);
                    }

                    JOptionPane.showMessageDialog(this,
                            "User role updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshAllUserPanels();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update user role. User may not exist.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                logError("Error changing user role", e);
                JOptionPane.showMessageDialog(this,
                        "Error changing user role: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Add a student record when role changes to Student */
    private void addStudentRecord(int userId) {
        String studentNumber = "STU" + String.format("%05d", userId);

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO Student (User_ID, Student_Number, College_Name, Date_Of_Birth) VALUES (?, ?, ?, CURDATE())")) {

            statement.setInt(1, userId);
            statement.setString(2, studentNumber);
            statement.setString(3, "College of Engineering"); // Default college
            statement.executeUpdate();

        } catch (SQLException e) {
            logError("Error adding student record", e);
        }
    }

    /** Remove a student record when role changes from Student */
    private void removeStudentRecord(int userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM Student WHERE User_ID = ?")) {

            statement.setInt(1, userId);
            statement.executeUpdate();

        } catch (SQLException e) {
            logError("Error removing student record", e);
        }
    }

    /** Add an instructor record when role changes to Instructor */
    private void addInstructorRecord(int userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO Instructor (User_ID, College_Name) VALUES (?, ?)")) {

            statement.setInt(1, userId);
            statement.setString(2, "College of Engineering"); // Default college
            statement.executeUpdate();

        } catch (SQLException e) {
            logError("Error adding instructor record", e);
        }
    }

    /** Remove an instructor record when role changes from Instructor */
    private void removeInstructorRecord(int userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM Instructor WHERE User_ID = ?")) {

            statement.setInt(1, userId);
            statement.executeUpdate();

        } catch (SQLException e) {
            logError("Error removing instructor record", e);
        }
    }

    /** Refresh all user management panels after a change */
    private void refreshAllUserPanels() {
        // Replace the manage users panel with a fresh one
        cardPanel.remove(cardPanel.getComponent(3)); // Index 3 is the manage users panel
        cardPanel.add(createManageUsersPanel(), "users", 3);

        // Also refresh the all students and all instructors panels
        cardPanel.remove(cardPanel.getComponent(0)); // Index 0 is the all students panel
        cardPanel.add(createAllStudentsPanel(), "students", 0);

        cardPanel.remove(cardPanel.getComponent(1)); // Index 1 is the all instructors panel
        cardPanel.add(createAllInstructorsPanel(), "instructors", 1);

        cardPanel.revalidate();
        cardPanel.repaint();
    }

    // Custom renderer for action buttons in tables
    class ButtonsRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton;
        private JButton deleteButton;

        public ButtonsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

            editButton = new JButton("Edit");
            editButton.setFont(new Font(UI_FONT, Font.PLAIN, 12));
            editButton.setPreferredSize(new Dimension(60, 25));

            deleteButton = new JButton("Delete");
            deleteButton.setFont(new Font(UI_FONT, Font.PLAIN, 12));
            deleteButton.setPreferredSize(new Dimension(70, 25));

            add(editButton);
            add(deleteButton);
            setBackground(LIGHT_BACKGROUND);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    // Custom editor for action buttons in tables
    class ButtonsEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;
        private JTable table;
        private AdminMenu admin;

        public ButtonsEditor(JTable table, AdminMenu admin) {
            this.table = table;
            this.admin = admin;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

            editButton = new JButton("Edit");
            editButton.setFont(new Font(UI_FONT, Font.PLAIN, 12));
            editButton.setPreferredSize(new Dimension(60, 25));

            deleteButton = new JButton("Delete");
            deleteButton.setFont(new Font(UI_FONT, Font.PLAIN, 12));
            deleteButton.setPreferredSize(new Dimension(70, 25));

            panel.add(editButton);
            panel.add(deleteButton);
            panel.setBackground(LIGHT_BACKGROUND);

            editButton.addActionListener(e -> {
                int row = table.convertRowIndexToModel(table.getEditingRow());
                int userId = ((Number) table.getModel().getValueAt(row, 0)).intValue();
                String currentRole = (String) table.getModel().getValueAt(row, 4);
                admin.changeUserRole(userId, currentRole);
                fireEditingStopped();
            });

            deleteButton.addActionListener(e -> {
                int row = table.convertRowIndexToModel(table.getEditingRow());
                int userId = ((Number) table.getModel().getValueAt(row, 0)).intValue();
                admin.deleteUser(userId);
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }

    /** Create a circular profile image */
    private JLabel createCircularImageLabel(String imagePath, int diameter) {
        BufferedImage image;
        try {
            // Attempt to load the image from path
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                image = ImageIO.read(imageFile);
            } else {
                // Create a placeholder if image doesn't exist
                JPanel placeholder = new JPanel();
                placeholder.setBackground(new Color(100, 149, 237)); // Cornflower blue
                placeholder.setPreferredSize(new Dimension(diameter, diameter));
                image = createImageFromPanel(placeholder, diameter, diameter);
            }
        } catch (IOException e) {
            // Create a placeholder on error
            JPanel placeholder = new JPanel();
            placeholder.setBackground(new Color(100, 149, 237)); // Cornflower blue
            placeholder.setPreferredSize(new Dimension(diameter, diameter));
            image = createImageFromPanel(placeholder, diameter, diameter);
        }

        // Resize the image to the desired diameter
        BufferedImage resized = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, diameter, diameter, null);
        g2d.dispose();

        // Create a circular mask
        BufferedImage circularImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        g2d = circularImage.createGraphics();
        g2d.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
        g2d.drawImage(resized, 0, 0, null);
        g2d.dispose();

        return new JLabel(new ImageIcon(circularImage));
    }
    /** Create image from panel for fallback profile picture */
    private BufferedImage createImageFromPanel(JPanel panel, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        panel.setSize(width, height);
        panel.paintComponents(g2d);
        g2d.dispose();
        return image;
    }

    /** Database connection setup */
    private Connection getConnection() throws SQLException {
        // Database connection information
        final String DB_URL = "jdbc:mysql://localhost:3306/elms";
        final String DB_USER = "root";
        final String DB_PASS = "1234";

        // Establish database connection
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    /** Logging errors */
    private void logError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }

    /** Create All Students Panel to display all students from the database */
    private JPanel createAllStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel title
        JLabel titleLabel = new JLabel("All Students");
        titleLabel.setFont(new Font(UI_FONT, Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table with student data
        String[] columnNames = {"ID", "Name", "Email", "Student Number", "College", "GPA"};
        Object[][] data = fetchStudentInfo();

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font(UI_FONT, Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font(UI_FONT, Font.BOLD, 14));
        table.getTableHeader().setBackground(HEADER_BACKGROUND);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setGridColor(new Color(211, 211, 211));
        table.setShowVerticalLines(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Center align the text in all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /** Method to fetch all students from the database */
    private Object[][] fetchStudentInfo() {
        ArrayList<Object[]> studentList = new ArrayList<>();
        String query = "SELECT u.User_ID, u.Full_Name, u.Email, s.Student_Number, s.College_Name, u.GPA " +
                "FROM Users u JOIN Student s ON u.User_ID = s.User_ID " +
                "WHERE u.Role = 'Student'";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                studentList.add(new Object[]{
                        resultSet.getInt("User_ID"),
                        resultSet.getString("Full_Name"),
                        resultSet.getString("Email"),
                        resultSet.getString("Student_Number"),
                        resultSet.getString("College_Name"),
                        resultSet.getFloat("GPA")
                });
            }

        } catch (SQLException e) {
            logError("Error fetching student information", e);
            JOptionPane.showMessageDialog(this,
                    "Error fetching student data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return studentList.toArray(new Object[0][0]);
    }

    /** Method to fetch all instructors from the database */
    private Object[][] fetchInstructorInfo() {
        ArrayList<Object[]> instructorList = new ArrayList<>();
        String query = "SELECT u.User_ID, u.Full_Name, u.Email, i.College_Name " +
                "FROM Users u JOIN Instructor i ON u.User_ID = i.User_ID " +
                "WHERE u.Role = 'Instructor'";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                instructorList.add(new Object[]{
                        resultSet.getInt("User_ID"),
                        resultSet.getString("Full_Name"),
                        resultSet.getString("Email"),
                        resultSet.getString("College_Name")
                });
            }

        } catch (SQLException e) {
            logError("Error fetching instructor information", e);
            JOptionPane.showMessageDialog(this,
                    "Error fetching instructor data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return instructorList.toArray(new Object[0][0]);
    }

    /** Create Pending Registrations Panel */
    private JPanel createPendingRegistrationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Pending User Registrations");
        titleLabel.setFont(new Font(UI_FONT, Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font(UI_FONT, Font.BOLD, 14));
        refreshButton.setBackground(BUTTON_BACKGROUND);
        refreshButton.addActionListener(e -> refreshPendingRegistrationsData());
        headerPanel.add(refreshButton, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Table with pending registrations data
        String[] columnNames = {"ID", "Full Name", "Email", "Role", "Request Date", "Status", "Actions"};
        Object[][] data = fetchPendingRegistrations();

        pendingRegistrationsModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make only Actions column editable
                return column == 6;
            }
        };

        pendingTable = new JTable(pendingRegistrationsModel);
        pendingTable.setName("pendingRegistrationsTable");
        pendingTable.setRowHeight(35);
        pendingTable.getTableHeader().setFont(new Font(UI_FONT, Font.BOLD, 14));
        pendingTable.setFont(new Font(UI_FONT, Font.PLAIN, 14));
        pendingTable.setSelectionBackground(new Color(230, 240, 250));
        pendingTable.setGridColor(new Color(240, 240, 240));
        pendingTable.setShowVerticalLines(true);

        // Center-align content in all columns except the last one
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < pendingTable.getColumnCount() - 1; i++) {
            pendingTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Add approval/rejection buttons renderer and editor for Actions column
        pendingTable.getColumnModel().getColumn(6).setCellRenderer(new RegistrationActionsRenderer());
        pendingTable.getColumnModel().getColumn(6).setCellEditor(new RegistrationActionsEditor(pendingTable, this));

        JScrollPane scrollPane = new JScrollPane(pendingTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private Object[][] fetchPendingRegistrations() {
        ArrayList<Object[]> pendingList = new ArrayList<>();

        // Modified query to only fetch registrations with "Pending" status
        String query = "SELECT Request_ID, Full_Name, Email, Role, RequestDate, Status " +
                "FROM PendingRegistrations " +
                "WHERE Status = 'Pending' " +  // Only fetch pending registrations
                "ORDER BY RequestDate DESC";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Collect Data for each Record
            while (resultSet.next()) {
                pendingList.add(new Object[] {
                        resultSet.getInt("Request_ID"),
                        resultSet.getString("Full_Name"),
                        resultSet.getString("Email"),
                        resultSet.getString("Role"),
                        resultSet.getTimestamp("RequestDate"),
                        resultSet.getString("Status"),
                        "Actions"  // Placeholder for any interactive buttons; adjust logic as required
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error fetching pending registrations: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return pendingList.toArray(new Object[0][0]);
    }

    /** Refresh just the table data without rebuilding the entire panel */
    private void refreshPendingRegistrationsData() {
        // Clear existing data
        pendingRegistrationsModel.setRowCount(0);

        // Fetch fresh data
        Object[][] newData = fetchPendingRegistrations();

        // Add rows to the model
        for (Object[] row : newData) {
            pendingRegistrationsModel.addRow(row);
        }

        // Update UI
        pendingRegistrationsModel.fireTableDataChanged();
    }

    /** This is preserved for compatibility but now uses the more efficient method */
    private void refreshPendingRegistrationsPanel() {
        refreshPendingRegistrationsData();
    }

    /** Approve a registration request */
    public void approveRegistration(int requestId) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                try (Connection connection = getConnection()) {
                    connection.setAutoCommit(false);  // Start transaction

                    // Get the row index for later removal
                    int rowIndex = -1;
                    for (int i = 0; i < pendingTable.getRowCount(); i++) {
                        if ((int) pendingTable.getValueAt(i, 0) == requestId) {
                            rowIndex = i;
                            break;
                        }
                    }

                    // First get the registration details
                    String selectQuery = "SELECT * FROM PendingRegistrations WHERE Request_ID = ?";
                    try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                        selectStmt.setInt(1, requestId);

                        try (ResultSet rs = selectStmt.executeQuery()) {
                            if (rs.next()) {
                                // Insert into Users table
                                String insertQuery = "INSERT INTO Users (Full_Name, DateOfBirth, PhoneNumber, Email, Username, Password, Role) VALUES (?, ?, ?, ?, ?, ?, ?)";
                                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                                    insertStmt.setString(1, rs.getString("Full_Name"));
                                    insertStmt.setDate(2, rs.getDate("DateOfBirth"));
                                    insertStmt.setString(3, rs.getString("PhoneNumber"));
                                    insertStmt.setString(4, rs.getString("Email"));
                                    insertStmt.setString(5, rs.getString("Username"));
                                    insertStmt.setString(6, rs.getString("Password"));
                                    insertStmt.setString(7, rs.getString("Role"));

                                    insertStmt.executeUpdate();

                                    // Get the newly inserted user ID
                                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                                        if (generatedKeys.next()) {
                                            int newUserId = generatedKeys.getInt(1);

                                            // If role is Student or Instructor, add to respective tables
                                            String role = rs.getString("Role");
                                            if ("Student".equals(role)) {
                                                addStudentRecord(newUserId);
                                            } else if ("Instructor".equals(role)) {
                                                addInstructorRecord(newUserId);
                                            }
                                        }
                                    }
                                }

                                // Update status in PendingRegistrations
                                String updateQuery = "UPDATE PendingRegistrations SET Status = 'Approved' WHERE Request_ID = ?";
                                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                                    updateStmt.setInt(1, requestId);
                                    updateStmt.executeUpdate();
                                }

                                // Remove from table model on UI thread
                                final int finalRowIndex = rowIndex;
                                if (finalRowIndex != -1) {
                                    SwingUtilities.invokeLater(() -> {
                                        pendingRegistrationsModel.removeRow(finalRowIndex);
                                    });
                                }
                            }
                        }
                    }

                    connection.commit();  // Commit transaction
                    return true;

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(AdminMenu.this,
                                "Registration approved successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(AdminMenu.this,
                                "Error approving registration.",
                                "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminMenu.this,
                            "Error approving registration: " + e.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    /** Reject a registration request */
    public void rejectRegistration(int requestId) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                String updateQuery = "UPDATE PendingRegistrations SET Status = 'Rejected' WHERE Request_ID = ?";

                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement(updateQuery)) {

                    // Get the row index for later removal
                    int rowIndex = -1;
                    for (int i = 0; i < pendingTable.getRowCount(); i++) {
                        if ((int) pendingTable.getValueAt(i, 0) == requestId) {
                            rowIndex = i;
                            break;
                        }
                    }

                    statement.setInt(1, requestId);
                    int rowsUpdated = statement.executeUpdate();

                    if (rowsUpdated > 0) {
                        // Remove from table model on UI thread
                        final int finalRowIndex = rowIndex;
                        if (finalRowIndex != -1) {
                            SwingUtilities.invokeLater(() -> {
                                pendingRegistrationsModel.removeRow(finalRowIndex);
                            });
                        }
                        return true;
                    }
                    return false;

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(AdminMenu.this,
                                "Registration rejected successfully.",
                                "Rejected", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(AdminMenu.this,
                                "Error rejecting registration.",
                                "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminMenu.this,
                            "Error rejecting registration: " + e.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }
    // Custom renderer for approval/rejection buttons
    class RegistrationActionsRenderer extends JPanel implements TableCellRenderer {
        private JButton approveButton;
        private JButton rejectButton;

        public RegistrationActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            setBackground(LIGHT_BACKGROUND);

            approveButton = new JButton("Approve");
            approveButton.setBackground(new Color(100, 180, 100));
            approveButton.setForeground(Color.WHITE);
            approveButton.setFocusPainted(false);

            rejectButton = new JButton("Reject");
            rejectButton.setBackground(new Color(180, 100, 100));
            rejectButton.setForeground(Color.WHITE);
            rejectButton.setFocusPainted(false);

            add(approveButton);
            add(rejectButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    // Custom editor for approval/rejection buttons
    class RegistrationActionsEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton approveButton;
        private JButton rejectButton;
        private JTable table;
        private AdminMenu adminMenu;

        public RegistrationActionsEditor(JTable table, AdminMenu adminMenu) {
            this.table = table;
            this.adminMenu = adminMenu;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setBackground(LIGHT_BACKGROUND);

            approveButton = new JButton("Approve");
            approveButton.setBackground(new Color(100, 180, 100));
            approveButton.setForeground(Color.WHITE);
            approveButton.setFocusPainted(false);
            approveButton.addActionListener(e -> {
                int row = table.getEditingRow();
                if (row >= 0) {
                    int requestId = (int) table.getValueAt(row, 0);
                    adminMenu.approveRegistration(requestId);
                }
                fireEditingStopped();
            });

            rejectButton = new JButton("Reject");
            rejectButton.setBackground(new Color(180, 100, 100));
            rejectButton.setForeground(Color.WHITE);
            rejectButton.setFocusPainted(false);
            rejectButton.addActionListener(e -> {
                int row = table.getEditingRow();
                if (row >= 0) {
                    int requestId = (int) table.getValueAt(row, 0);
                    adminMenu.rejectRegistration(requestId);
                }
                fireEditingStopped();
            });

            panel.add(approveButton);
            panel.add(rejectButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }

    /** Create All Courses Panel to display all courses from the database */
    private JPanel createAllCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel title with header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("All Courses");
        titleLabel.setFont(new Font(UI_FONT, Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font(UI_FONT, Font.BOLD, 14));
        refreshButton.setBackground(BUTTON_BACKGROUND);
        refreshButton.addActionListener(e -> refreshCoursesPanel());
        headerPanel.add(refreshButton, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Get instructors for combobox
        ArrayList<String[]> instructors = fetchInstructorsForDropdown();
        String[] instructorNames = new String[instructors.size() + 1];
        instructorNames[0] = "Not Assigned";
        int[] instructorIds = new int[instructors.size() + 1];
        instructorIds[0] = -1;

        for (int i = 0; i < instructors.size(); i++) {
            instructorIds[i + 1] = Integer.parseInt(instructors.get(i)[0]);
            instructorNames[i + 1] = instructors.get(i)[1];
        }

        // Table with course data
        String[] columnNames = {"ID", "Course Name", "Duration (Weeks)", "Instructor", "Actions"};
        Object[][] data = fetchAllCourses();

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all columns except ID and Actions column editable
                return column != 0 && column != 4;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return Integer.class; // Make sure duration column is treated as integers
                }
                return super.getColumnClass(columnIndex);
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font(UI_FONT, Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font(UI_FONT, Font.BOLD, 14));
        table.getTableHeader().setBackground(HEADER_BACKGROUND);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setGridColor(new Color(211, 211, 211));
        table.setShowVerticalLines(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Center align the text in all columns except the last one (Actions)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Setup instructor dropdown editor for instructor column
        JComboBox<String> instructorComboBox = new JComboBox<>(instructorNames);
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(instructorComboBox));

        // Add renderer and editor for the Actions column
        table.getColumnModel().getColumn(4).setCellRenderer(new CourseButtonsRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new CourseButtonsEditor(table, this));

        // Add table model listener to handle edits
        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() != 4) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (row >= 0 && column >= 0) {
                    try {
                        int courseId = (int) model.getValueAt(row, 0);
                        String courseName = (String) model.getValueAt(row, 1);
                        int duration = 0;

                        // Try to parse the duration as an integer
                        Object durationObj = model.getValueAt(row, 2);
                        if (durationObj instanceof Integer) {
                            duration = (Integer) durationObj;
                        } else if (durationObj instanceof String) {
                            duration = Integer.parseInt((String) durationObj);
                        }

                        // Get instructor selection
                        String instructorName = (String) model.getValueAt(row, 3);
                        int instructorId = -1;

                        // Find the corresponding instructor ID
                        for (int i = 0; i < instructorNames.length; i++) {
                            if (instructorNames[i].equals(instructorName)) {
                                instructorId = instructorIds[i];
                                break;
                            }
                        }

                        // Update the course in the database
                        updateCourse(courseId, courseName, duration, instructorId);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Duration must be a valid number.",
                                "Input Error", JOptionPane.ERROR_MESSAGE);

                        // Refresh the panel to revert the invalid input
                        refreshCoursesPanel();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /** Update course in the database */
    private void updateCourse(int courseId, String courseName, int duration, int instructorId) {
        if (courseName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Course name cannot be empty.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            refreshCoursesPanel();
            return;
        }

        if (duration <= 0 || duration > 52) {
            JOptionPane.showMessageDialog(this,
                    "Duration must be between 1 and 52 weeks.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            refreshCoursesPanel();
            return;
        }

        String query = "UPDATE Course SET CourseName = ?, Duration = ?, Instructor_ID = ? WHERE Course_ID = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, courseName);
            statement.setInt(2, duration);

            // Handle null instructor
            if (instructorId > 0) {
                statement.setInt(3, instructorId);
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);
            }

            statement.setInt(4, courseId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Success message would appear too often when editing, so we'll skip it
                // JOptionPane.showMessageDialog(this,
                //     "Course updated successfully!",
                //     "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update course. Course may not exist.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            logError("Error updating course", e);
            JOptionPane.showMessageDialog(this,
                    "Error updating course: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Delete course from the database */
    public void deleteCourse(int courseId) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this course? This action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM Course WHERE Course_ID = ?";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, courseId);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Course deleted successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshCoursesPanel();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to delete course. Course may not exist.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                logError("Error deleting course", e);
                JOptionPane.showMessageDialog(this,
                        "Error deleting course: " + e.getMessage() +
                                "\nNote: You cannot delete a course that has enrollments or assignments.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Refresh courses panel */
    private void refreshCoursesPanel() {
        cardPanel.remove(cardPanel.getComponent(2)); // Index 2 is the all courses panel
        cardPanel.add(createAllCoursesPanel(), "courses", 2);
        cardLayout.show(cardPanel, "courses");
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    // Custom renderer for action buttons in course table
    class CourseButtonsRenderer extends JPanel implements TableCellRenderer {
        private JButton deleteButton;

        public CourseButtonsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

            deleteButton = new JButton("Delete");
            deleteButton.setFont(new Font(UI_FONT, Font.PLAIN, 12));
            deleteButton.setPreferredSize(new Dimension(70, 25));
            deleteButton.setBackground(new Color(220, 53, 69));
            deleteButton.setForeground(Color.WHITE);

            add(deleteButton);
            setBackground(LIGHT_BACKGROUND);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    // Custom editor for action buttons in course table
    class CourseButtonsEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton deleteButton;
        private JTable table;
        private AdminMenu admin;

        public CourseButtonsEditor(JTable table, AdminMenu admin) {
            this.table = table;
            this.admin = admin;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

            deleteButton = new JButton("Delete");
            deleteButton.setFont(new Font(UI_FONT, Font.PLAIN, 12));
            deleteButton.setPreferredSize(new Dimension(70, 25));
            deleteButton.setBackground(new Color(220, 53, 69));
            deleteButton.setForeground(Color.WHITE);

            panel.add(deleteButton);
            panel.setBackground(LIGHT_BACKGROUND);

            deleteButton.addActionListener(e -> {
                int row = table.convertRowIndexToModel(table.getEditingRow());
                int courseId = ((Number) table.getModel().getValueAt(row, 0)).intValue();
                admin.deleteCourse(courseId);
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }

    /** Method to fetch all courses from the database */
    private Object[][] fetchAllCourses() {
        ArrayList<Object[]> courseList = new ArrayList<>();
        String query = "SELECT c.Course_ID, c.CourseName, c.Duration, u.Full_Name AS Instructor_Name " +
                "FROM Course c LEFT JOIN Instructor i ON c.Instructor_ID = i.User_ID " +
                "LEFT JOIN Users u ON i.User_ID = u.User_ID";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String instructorName = resultSet.getString("Instructor_Name");
                if (instructorName == null) {
                    instructorName = "Not Assigned";
                }

                courseList.add(new Object[]{
                        resultSet.getInt("Course_ID"),
                        resultSet.getString("CourseName"),
                        resultSet.getInt("Duration"),
                        instructorName,
                        "Actions"  // Placeholder for action buttons
                });
            }

        } catch (SQLException e) {
            logError("Error fetching course information", e);
            JOptionPane.showMessageDialog(this,
                    "Error fetching course data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return courseList.toArray(new Object[0][0]);
    }
    /** Create Account Settings Panel */
    private JPanel createAccountSettingsPanel() {
        JPanel accountSettingsPanel = new JPanel(new BorderLayout());
        accountSettingsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Account Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        accountSettingsPanel.add(titleLabel, BorderLayout.NORTH);

        // Create a form to edit all the user's information
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // Form fields
        JTextField fullNameField = new JTextField();
        JTextField dobField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField zipField = new JTextField();
        JTextField cityField = new JTextField();
        JTextField streetField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        // Adding labels and fields to the form panel
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(fullNameField);

        formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        formPanel.add(dobField);

        formPanel.add(new JLabel("Phone Number:"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Zip Code:"));
        formPanel.add(zipField);

        formPanel.add(new JLabel("City:"));
        formPanel.add(cityField);

        formPanel.add(new JLabel("Street:"));
        formPanel.add(streetField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        accountSettingsPanel.add(formPanel, BorderLayout.CENTER);

        // Add Save button
        JButton saveButton = new JButton("Save Changes");
        accountSettingsPanel.add(saveButton, BorderLayout.SOUTH);

        // Populate the text fields with the user's current information
        populateUserFields(fullNameField, dobField, phoneField, zipField, cityField, streetField, emailField, usernameField, passwordField);

        // Action listener for the Save button
        saveButton.addActionListener(e -> {
            // Capture updated user information from the form
            String fullName = fullNameField.getText();
            String dob = dobField.getText();
            String phone = phoneField.getText();
            String zip = zipField.getText();
            String city = cityField.getText();
            String street = streetField.getText();
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Update the database with the new information
            updateUserInformation(fullName, dob, phone, zip, city, street, email, username, password);
        });

        return accountSettingsPanel;
    }

    /** Populate user fields with current data from the database */
    private void populateUserFields(JTextField fullNameField, JTextField dobField, JTextField phoneField,
                                    JTextField zipField, JTextField cityField, JTextField streetField,
                                    JTextField emailField, JTextField usernameField,
                                    JPasswordField passwordField) {
        String query = "SELECT Full_Name, DateOfBirth, PhoneNumber, ZipCode, City, Street, Email, Username, Password FROM Users WHERE User_ID = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/elms", "root", "1234");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userID); // Use the logged-in user's ID
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                fullNameField.setText(rs.getString("Full_Name"));
                dobField.setText(rs.getString("DateOfBirth"));
                phoneField.setText(rs.getString("PhoneNumber"));
                zipField.setText(rs.getString("ZipCode"));
                cityField.setText(rs.getString("City"));
                streetField.setText(rs.getString("Street"));
                emailField.setText(rs.getString("Email"));
                usernameField.setText(rs.getString("Username"));
                passwordField.setText(rs.getString("Password")); // Note: In real systems, passwords should not be stored/retrieved in plain text
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching user data from the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Update user information in the database */
    private void updateUserInformation(String fullName, String dob, String phone, String zip, String city,
                                       String street, String email, String username, String password) {
        String updateQuery = "UPDATE Users SET Full_Name = ?, DateOfBirth = ?, PhoneNumber = ?, ZipCode = ?, City = ?, Street = ?, Email = ?, Username = ?, Password = ? WHERE User_ID = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/elms", "root", "1234");
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, fullName);
            stmt.setString(2, dob);
            stmt.setString(3, phone);
            stmt.setString(4, zip);
            stmt.setString(5, city);
            stmt.setString(6, street);
            stmt.setString(7, email);
            stmt.setString(8, username);
            stmt.setString(9, password);
            stmt.setInt(10, userID); // Use the logged-in user's ID

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Your information has been successfully updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No changes were made.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    /** Create Add Course Panel to add new courses to the database */


    private JPanel createAddCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel title
        JLabel titleLabel = new JLabel("Add New Course");
        titleLabel.setFont(new Font(UI_FONT, Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(LIGHT_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Course Name
        JLabel courseNameLabel = new JLabel("Course Name:");
        courseNameLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(courseNameLabel, gbc);

        JTextField courseNameField = new JTextField(20);
        courseNameField.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(courseNameField, gbc);

        // Department
        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(departmentLabel, gbc);

        JTextField departmentField = new JTextField(20);
        departmentField.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(departmentField, gbc);

        // Duration
        JLabel durationLabel = new JLabel("Duration (weeks):");
        durationLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(durationLabel, gbc);

        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 52, 1));
        durationSpinner.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(durationSpinner, gbc);

        // Instructor dropdown
        JLabel instructorLabel = new JLabel("Instructor:");
        instructorLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(instructorLabel, gbc);

        // Get instructors for dropdown
        ArrayList<String[]> instructors = fetchInstructorsForDropdown();
        String[] instructorOptions = new String[instructors.size() + 1];
        instructorOptions[0] = "Select Instructor";
        for (int i = 0; i < instructors.size(); i++) {
            instructorOptions[i + 1] = instructors.get(i)[1]; // Instructor name
        }

        JComboBox<String> instructorComboBox = new JComboBox<>(instructorOptions);
        instructorComboBox.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(instructorComboBox, gbc);

        // Add Course Button
        JButton addCourseButton = new JButton("Add Course");
        addCourseButton.setFont(new Font(UI_FONT, Font.BOLD, 16));
        addCourseButton.setBackground(BUTTON_BACKGROUND);
        addCourseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        formPanel.add(addCourseButton, gbc);

        // Add action to button
        addCourseButton.addActionListener(e -> {
            String courseName = courseNameField.getText().trim();
            String department = departmentField.getText().trim();
            int duration = (Integer) durationSpinner.getValue();
            int instructorIndex = instructorComboBox.getSelectedIndex();

            // Validate inputs
            if (courseName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a course name.",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get instructor ID (if selected)
            int instructorId = -1;
            if (instructorIndex > 0) {
                instructorId = Integer.parseInt(instructors.get(instructorIndex - 1)[0]);
            }

            // Add course to database
            boolean success = addCourseToDatabase(courseName, department, duration, instructorId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Course added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear form
                courseNameField.setText("");
                departmentField.setText("");
                durationSpinner.setValue(8);
                instructorComboBox.setSelectedIndex(0);

                // Refresh courses panel
                cardPanel.remove(cardPanel.getComponent(2)); // Index 2 is the all courses panel
                cardPanel.add(createAllCoursesPanel(), "courses", 2);
                cardPanel.revalidate();
                cardPanel.repaint();
            }
        });

        // Add form to panel
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    /** Fetch instructors for dropdown */
    private ArrayList<String[]> fetchInstructorsForDropdown() {
        ArrayList<String[]> instructorList = new ArrayList<>();
        String query = "SELECT u.User_ID, u.Full_Name FROM Users u JOIN Instructor i ON u.User_ID = i.User_ID WHERE u.Role = 'Instructor'";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String[] instructor = new String[2];
                instructor[0] = String.valueOf(resultSet.getInt("User_ID"));
                instructor[1] = resultSet.getString("Full_Name");
                instructorList.add(instructor);
            }

        } catch (SQLException e) {
            logError("Error fetching instructors for dropdown", e);
        }

        return instructorList;
    }

    /** Add course to database */
    private boolean addCourseToDatabase(String courseName, String department, int duration, int instructorId) {
        String query = "INSERT INTO Course (CourseName, Duration, Instructor_ID) VALUES (?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, courseName);
            preparedStatement.setInt(2, duration);

            if (instructorId > 0) {
                preparedStatement.setInt(3, instructorId);
            } else {
                preparedStatement.setNull(3, java.sql.Types.INTEGER);
            }

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            logError("Error adding course to database", e);
            JOptionPane.showMessageDialog(this,
                    "Error adding course: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /** Create Add Admin Panel to add new administrators to the system */
    private JPanel createAddAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel title
        JLabel titleLabel = new JLabel("Add New Administrator");
        titleLabel.setFont(new Font(UI_FONT, Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Create a scrollable form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(LIGHT_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Full Name
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(fullNameLabel, gbc);

        JTextField fullNameField = new JTextField(20);
        fullNameField.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);

        // Date of Birth
        JLabel dobLabel = new JLabel("Date of Birth:");
        dobLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(dobLabel, gbc);

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.setBackground(LIGHT_BACKGROUND);

        String[] days = new String[31];
        for (int i = 0; i < 31; i++) days[i] = String.format("%02d", i + 1);
        JComboBox<String> dayComboBox = new JComboBox<>(days);
        dayComboBox.setPreferredSize(new Dimension(60, 25));

        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        JComboBox<String> monthComboBox = new JComboBox<>(months);
        monthComboBox.setPreferredSize(new Dimension(60, 25));

        String[] years = new String[100];
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int i = 0; i < 100; i++) years[i] = String.valueOf(currentYear - i);
        JComboBox<String> yearComboBox = new JComboBox<>(years);
        yearComboBox.setPreferredSize(new Dimension(80, 25));

        datePanel.add(yearComboBox);
        datePanel.add(new JLabel("-"));
        datePanel.add(monthComboBox);
        datePanel.add(new JLabel("-"));
        datePanel.add(dayComboBox);

        gbc.gridx = 1;
        formPanel.add(datePanel, gbc);

        // Phone Number
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(phoneLabel, gbc);

        JTextField phoneField = new JTextField(20);
        phoneField.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        // Zip Code
        JLabel zipLabel = new JLabel("Zip Code:");
        zipLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(zipLabel, gbc);

        JTextField zipField = new JTextField(20);
        zipField.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(zipField, gbc);

        // City
        JLabel cityLabel = new JLabel("City:");
        cityLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(cityLabel, gbc);

        JTextField cityField = new JTextField(20);
        cityField.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(cityField, gbc);

        // Street
        JLabel streetLabel = new JLabel("Street:");
        streetLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(streetLabel, gbc);

        JTextField streetField = new JTextField(20);
        streetField.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(streetField, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Confirm Password
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 9;
        formPanel.add(confirmPasswordLabel, gbc);

        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        // College Name
        JLabel collegeLabel = new JLabel("College:");
        collegeLabel.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 10;
        formPanel.add(collegeLabel, gbc);

        String[] colleges = {"College of Computer Science", "College of Engineering", "College of Business",
                "College of Science", "College of Medicine", "College of Arts"};
        JComboBox<String> collegeComboBox = new JComboBox<>(colleges);
        collegeComboBox.setFont(new Font(UI_FONT, Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(collegeComboBox, gbc);

        // Add Admin Button
        JButton addAdminButton = new JButton("Add Administrator");
        addAdminButton.setFont(new Font(UI_FONT, Font.BOLD, 16));
        addAdminButton.setBackground(BUTTON_BACKGROUND);
        addAdminButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        formPanel.add(addAdminButton, gbc);

        // Add action to button
        addAdminButton.addActionListener(e -> {
            String fullName = fullNameField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String zipCode = zipField.getText().trim();
            String city = cityField.getText().trim();
            String street = streetField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String college = (String) collegeComboBox.getSelectedItem();

            // Create date string in format YYYY-MM-DD
            String dateOfBirth = yearComboBox.getSelectedItem() + "-" +
                    monthComboBox.getSelectedItem() + "-" +
                    dayComboBox.getSelectedItem();

            // Validate inputs
            if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() ||
                    phone.isEmpty() || zipCode.isEmpty() || city.isEmpty() ||
                    street.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all fields.",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Passwords do not match.",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!email.matches(".*@iau\\.edu\\.sa$")) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid email address ending with @iau.edu.sa.",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!phone.matches("^05[0-9]{8}$")) {
                JOptionPane.showMessageDialog(this,
                        "Phone number must start with 05 and be 10 digits in total.",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Add admin to database
            boolean success = addAdminToDatabase(fullName, dateOfBirth, phone, zipCode,
                    city, street, email, username, password, college);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Administrator added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear form
                fullNameField.setText("");
                phoneField.setText("");
                zipField.setText("");
                cityField.setText("");
                streetField.setText("");
                emailField.setText("");
                usernameField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");

                // Reset dropdowns
                dayComboBox.setSelectedIndex(0);
                monthComboBox.setSelectedIndex(0);
                yearComboBox.setSelectedIndex(0);
                collegeComboBox.setSelectedIndex(0);

                // Refresh user panels
                refreshAllUserPanels();
            }
        });

        // Create a scroll pane for the form
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /** Add admin to database */
    private boolean addAdminToDatabase(String fullName, String dateOfBirth, String phone,
                                       String zipCode, String city, String street,
                                       String email, String username, String password,
                                       String college) {
        // Check if username or email already exists
        if (isUsernameTaken(username) || isEmailTaken(email)) {
            JOptionPane.showMessageDialog(this,
                    "Username or email already exists in the system.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // First insert into Users table with all required fields
        String usersQuery = "INSERT INTO Users (Full_Name, DateOfBirth, PhoneNumber, ZipCode, City, Street, " +
                "Email, Username, Password, Role, Created_At) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'Admin', CURRENT_TIMESTAMP)";

        try (Connection connection = getConnection();
             PreparedStatement userStatement = connection.prepareStatement(usersQuery, Statement.RETURN_GENERATED_KEYS)) {

            userStatement.setString(1, fullName);
            userStatement.setString(2, dateOfBirth);
            userStatement.setString(3, phone);
            userStatement.setString(4, zipCode);
            userStatement.setString(5, city);
            userStatement.setString(6, street);
            userStatement.setString(7, email);
            userStatement.setString(8, username);
            userStatement.setString(9, password);

            int rowsAffected = userStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Get the auto-generated User_ID
                try (ResultSet generatedKeys = userStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);

                        // Now insert into Admin table
                        String adminQuery = "INSERT INTO Admin (User_ID, College_Name) VALUES (?, ?)";
                        try (PreparedStatement adminStatement = connection.prepareStatement(adminQuery)) {
                            adminStatement.setInt(1, userId);
                            adminStatement.setString(2, college);
                            return adminStatement.executeUpdate() > 0;
                        }
                    }
                }
            }

            return false;

        } catch (SQLException e) {
            logError("Error adding administrator to database", e);
            JOptionPane.showMessageDialog(this,
                    "Error adding administrator: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    /** Check if username is already taken */
    private boolean isUsernameTaken(String username) {
        String query = "SELECT COUNT(*) AS count FROM Users WHERE Username = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("count") > 0;
                }
            }

        } catch (SQLException e) {
            logError("Error checking if username is taken", e);
        }

        return false;
    }

    /** Check if email is already taken */
    private boolean isEmailTaken(String email) {
        String query = "SELECT COUNT(*) AS count FROM Users WHERE Email = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("count") > 0;
                }
            }

        } catch (SQLException e) {
            logError("Error checking if email is taken", e);
        }

        return false;

    }

    /** Create About Us Panel */
    private JPanel createAboutUsPanel() {
        JPanel aboutUsPanel = new JPanel(new BorderLayout());
        aboutUsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("About Us", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        aboutUsPanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea aboutUsTextArea = new JTextArea();
        aboutUsTextArea.setEditable(false); // Make the text area read-only
        aboutUsTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        aboutUsTextArea.setLineWrap(true);
        aboutUsTextArea.setWrapStyleWord(true);
        aboutUsTextArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JScrollPane scrollPane = new JScrollPane(aboutUsTextArea);
        aboutUsPanel.add(scrollPane, BorderLayout.CENTER);

        // Read the contents of the about_us.txt file and set to the text area
        try {
            String aboutUsContent = readFile("src/about_us.txt");
            aboutUsTextArea.setText(aboutUsContent);
        } catch (IOException e) {
            e.printStackTrace();
            aboutUsTextArea.setText("Error: Unable to load About Us content.");
        }

        return aboutUsPanel;
    }

    /** Read the contents of about_us.txt file line by line */
    private String readFile(String fileName) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString();
    }
    /** Main method for testing */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Admin Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        int testUserID = 4;

        AdminMenu adminMenu = new AdminMenu(testUserID);
        frame.add(adminMenu);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen.
        frame.setVisible(true);
    }
}