import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.nio.channels.FileChannel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentMenu extends JPanel {
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel;
    private int userID; // Logged-in user ID.
    private String userFirstName; // To store user's first name dynamically fetched from the database.

    // Constructor
    public StudentMenu(int userID) {
        this.userID = userID;

        // Fetch user first name from the database on initialization
        this.userFirstName = fetchUserFirstName(userID);

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240)); // Light background.

        // Add the header panel at the top
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Add the sidebar dashboard on the left
        add(createDashboard(), BorderLayout.WEST);

        // Card-based central content area
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);

        // Add placeholder panels for each feature section
        cardPanel.add(createHomePanel(), "homeCard");
        cardPanel.add(createGradesPanel(), "gradesCard");
        cardPanel.add(createPersonalDataPanel(), "personalDataCard");
        cardPanel.add(createAssignmentsPanel(), "assignmentsCard");
        cardPanel.add(createEnrollmentPanel(), "enrollmentCard");
        cardPanel.add(createCoursesPanel(), "coursesCard");
        cardPanel.add(createSchedulePanel(), "scheduleCard");
//        cardPanel.add(createCareerServicesPanel(), "careerServicesCard");
        cardPanel.add(createAnnouncementPanel(), "announcementsCard");
        cardPanel.add(createAccountSettingsPanel(), "accountSettingsCard");
        cardPanel.add(createAboutUsPanel(), "aboutUsCard");

        add(cardPanel, BorderLayout.CENTER);
    }

    /** Fetch the user's first name from the database */
    private String fetchUserFirstName(int userID) {
        String firstName = "Student"; // Default to "Student" if no name is found.
        String query = "SELECT Full_Name FROM Users WHERE User_ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userID); // Look up the user by their ID.
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("Full_Name");
                if (fullName != null && !fullName.isEmpty()) {
                    firstName = fullName.split(" ")[0]; // Extract the first name.
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return firstName;
    }

    /** Create the header panel */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(192, 192, 192)); // Silver-like background.

        // Create a smaller panel to hold the greeting and profile picture
        JPanel userProfilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userProfilePanel.setBackground(new Color(192, 192, 192)); // Maintain consistent background.

        // Add the circular profile picture
        JLabel profilePicture = createCircularImageLabel("src/sleeping cat.jpg", 60);
        userProfilePanel.add(profilePicture);

        // Add the greeting label with a dynamically fetched first name
        JLabel titleLabel = new JLabel("Hello, " + userFirstName, JLabel.LEFT);
        titleLabel.setForeground(new Color(60, 60, 60)); // Dark gray text.
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        userProfilePanel.add(titleLabel);

        headerPanel.add(userProfilePanel, BorderLayout.WEST);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setBackground(new Color(235, 150, 150)); // Light reddish color
        logoutButton.setForeground(new Color(120, 40, 40)); // Darker red text for contrast
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setFocusPainted(false); // Remove focus border
        logoutButton.addActionListener(e -> showLogoutConfirmation());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }

    /** Create a sidebar dashboard with navigation buttons */
    private JPanel createDashboard() {
        JPanel dashboard = new JPanel();
        dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));
        dashboard.setBorder(new EmptyBorder(20, 15, 20, 15));
        dashboard.setBackground(new Color(211, 211, 211)); // Light silver background.

        // Add buttons for the sidebar
        dashboard.add(createSidebarButton("Home", "homeCard"));
        dashboard.add(Box.createVerticalStrut(10));

        dashboard.add(createSidebarButton("Grades", "gradesCard"));
        dashboard.add(Box.createVerticalStrut(10));

        dashboard.add(createSidebarButton("Personal Data", "personalDataCard"));
        dashboard.add(Box.createVerticalStrut(10));

        dashboard.add(createSidebarButton("Assignments", "assignmentsCard"));
        dashboard.add(Box.createVerticalStrut(10));

        dashboard.add(createSidebarButton("Courses", "coursesCard"));
        dashboard.add(Box.createVerticalStrut(10));

        dashboard.add(createSidebarButton("Enrollment", "enrollmentCard"));
        dashboard.add(Box.createVerticalStrut(10));

        dashboard.add(createSidebarButton("Schedule", "scheduleCard"));
        dashboard.add(Box.createVerticalStrut(10));
        dashboard.add(createSidebarButton("Announcements", "announcementsCard"));
        dashboard.add(Box.createVerticalStrut(10));

        dashboard.add(createSidebarButton("Account Settings", "accountSettingsCard"));
        dashboard.add(Box.createVerticalStrut(10));

        dashboard.add(createSidebarButton("About Us", "aboutUsCard"));
        dashboard.add(Box.createVerticalStrut(10));


        try {
            BufferedImage logoImage = ImageIO.read(new File("src/WhiteBoard.png"));
            Image scaledImage = logoImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Scale the logo
            JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));

            // Create the logo panel and set its background
            JPanel logoPanel = new JPanel();
            logoPanel.setLayout(new GridBagLayout()); // Use GridBagLayout to center-align the content in the panel
            logoPanel.setBackground(new Color(211, 211, 211)); // Match the dashboard's background color
            logoPanel.add(logoLabel);

            // Add the logoPanel to the center of the dashboard
            dashboard.setBackground(new Color(211, 211, 211)); // Light silver background for the dashboard
            dashboard.add(logoPanel, BorderLayout.CENTER);
        } catch (IOException e) {
            System.err.println("Logo file not found: " + e.getMessage());
        }

        return dashboard;
    }

    /** Create a sidebar button with navigation functionality */
    private JButton createSidebarButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setBackground(new Color(169, 169, 169)); // Light gray for button.
        button.setForeground(Color.BLACK); // Black text.
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(200, 40));
        button.addActionListener(e -> cardLayout.show(cardPanel, cardName));
        return button;
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

    /** Optional: Reset application state (if needed) */
    private void resetState() {
        // Example: Clear data here (if applicable)
        System.out.println("User session data cleared. Returned to Welcome Page.");
    }


    /** Create a JLabel with a circular image */
    private JLabel createCircularImageLabel(String imagePath, int diameter) {
        try {
            // Load the image
            BufferedImage image = ImageIO.read(new File(imagePath));

            // Resize and crop the image to a circle
            BufferedImage resizedImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedImage.createGraphics();

            g2d.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
            g2d.drawImage(image, 0, 0, diameter, diameter, null);
            g2d.dispose();

            // Return the JLabel with the circular image as an Icon
            return new JLabel(new ImageIcon(resizedImage));
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback if the image cannot be loaded
            return new JLabel("No Image");
        }
    }

    // Placeholder for all individual sections
    /** Create the Home Panel with a welcome message *//*

    /**
     * Create the Home Panel with a welcome message
     */
    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BorderLayout());
        homePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Fetch the user's first and last name
        String userFirstName = ""; // Placeholder
        String userLastName = ""; // Placeholder
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT Full_Name FROM Users WHERE User_ID = ?"
             )) {
            statement.setInt(1, userID); // Logged-in user ID
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String fullName = resultSet.getString("Full_Name");
                String[] nameParts = fullName.split(" ", 2); // Split into first and last names
                if (nameParts.length > 0) userFirstName = nameParts[0];
                if (nameParts.length > 1) userLastName = nameParts[1];
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch user name. Defaulting to Guest.", "Error", JOptionPane.ERROR_MESSAGE);
            userFirstName = "Guest";
            userLastName = "";
        }

        // Welcome message
        String welcomeMessage = String.format(
                """
                        <html>
                        <div style='text-align: center;'>
                            <h1>🏠 Welcome to Whiteboard %s %s!</h1>
                            <p style='font-size: 16px;'>
                                Here, you can easily access everything you need:
                            </p>
                            <p style='font-size: 16px;'>📚 View your Grades</p>
                            <p style='font-size: 16px;'>📝 Submit Assignments</p>
                            <p style='font-size: 16px;'>🗓️ Check Your Schedule</p>
                            <p style='font-size: 16px;'>🎯 Explore Career Services</p>
                            <br />
                            <p style='font-size: 16px;'>
                                Stay organized, stay focused, and achieve your goals!
                            </p>
                            <p style='font-size: 16px;'>
                                If you need help, visit Account Settings or Contact Support.
                            </p>
                        </div>
                        </html>
                        """, userFirstName, userLastName
        );

        // Add the message to the center
        JLabel messageLabel = new JLabel(welcomeMessage);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add message to the panel
        homePanel.add(messageLabel, BorderLayout.CENTER);
        return homePanel;
    }

    /** Create Grades Panel and fetch grades from the database */
    private JPanel createGradesPanel() {
        JPanel gradesPanel = new JPanel();
        gradesPanel.setLayout(new BorderLayout());
        gradesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Your Grades");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gradesPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Course Name", "Grade"};
        Object[][] data = {}; // Placeholder for table data

        // Fetch data from database
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT CourseName, Grade FROM StudentGrades WHERE User_ID = ?"
             )) {
            statement.setInt(1, userID); // Use the logged-in user's ID
            ResultSet resultSet = statement.executeQuery();

            // Populate rows dynamically
            java.util.List<Object[]> rows = new java.util.ArrayList<>();
            while (resultSet.next()) {
                String courseName = resultSet.getString("CourseName");
                double grade = resultSet.getDouble("Grade");
                rows.add(new Object[]{courseName, grade});
            }

            // Convert List to Array for JTable
            data = rows.toArray(new Object[0][]);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch grades. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Create JTable with data
        JTable gradeTable = new JTable(data, columnNames);
        gradeTable.setFillsViewportHeight(true);

        // Set larger font for table content
        Font tableFont = new Font("Arial", Font.PLAIN, 16); // Font for table rows
        gradeTable.setFont(tableFont);
        gradeTable.setRowHeight(30); // Adjust row height to accommodate larger font

        // Set larger font for table header
        Font headerFont = new Font("Arial", Font.BOLD, 18);
        gradeTable.getTableHeader().setFont(headerFont);
        gradeTable.getTableHeader().setReorderingAllowed(false); // Disable column reordering

        // Fixing the size of the JTable
        int tableWidth = 500; // Fixed width
        int tableHeight = 300; // Fixed height
        gradeTable.setPreferredScrollableViewportSize(new Dimension(tableWidth, tableHeight));

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(gradeTable);

        // Ensure the scroll pane respects the table's preferred size
        scrollPane.setPreferredSize(new Dimension(tableWidth, tableHeight));

        gradesPanel.add(scrollPane, BorderLayout.CENTER);

        return gradesPanel;
    }

    /** Create Personal Data Panel to display the student's information */
    private JPanel createPersonalDataPanel() {
        // Main wrapper panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding around the panel
        panel.setBackground(Color.WHITE);

        // Header Label
        JLabel headerLabel = new JLabel("Personal Information", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Larger font size for the title
        headerLabel.setForeground(Color.BLACK); // Accent color for header text
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(headerLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Add spacing below the title

        // Panel to hold the personal data entries in a grid layout
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new GridBagLayout());
        dataPanel.setBackground(new Color(245, 245, 245)); // Light gray background
        dataPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Add padding inside the grid panel

        // GridBagLayout constraints for alignment
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing around grid cells
        gbc.anchor = GridBagConstraints.WEST;

        // Attempt to fetch user details from the database
        try (Connection connection = getConnection()) {
            String query = "SELECT u.Full_Name, u.Email, u.PhoneNumber, u.City, u.Street, u.ZipCode, s.College_Name, u.GPA " +
                    "FROM Users u " +
                    "JOIN Student s ON u.User_ID = s.User_ID " +
                    "WHERE u.User_ID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userID); // Use the logged-in user's ID
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Adding each info entry as a label and value pair
                    addDataRow("Full Name:", resultSet.getString("Full_Name"), dataPanel, gbc);
                    addDataRow("Email:", resultSet.getString("Email"), dataPanel, gbc);
                    addDataRow("Phone Number:", resultSet.getString("PhoneNumber"), dataPanel, gbc);
                    addDataRow("City:", resultSet.getString("City"), dataPanel, gbc);
                    addDataRow("Street:", resultSet.getString("Street"), dataPanel, gbc);
                    addDataRow("Zip Code:", resultSet.getString("ZipCode"), dataPanel, gbc);
                    addDataRow("College:", resultSet.getString("College_Name"), dataPanel, gbc);
                    addDataRow("GPA:", String.format("%.2f", resultSet.getDouble("GPA")), dataPanel, gbc);
                } else {
                    JLabel noDataLabel = new JLabel("No personal information found for the user.");
                    noDataLabel.setForeground(Color.RED);
                    noDataLabel.setFont(new Font("Arial", Font.BOLD, 14));
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    gbc.gridwidth = 2;
                    dataPanel.add(noDataLabel, gbc);
                }
            }
        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("An error occurred while fetching data.");
            errorLabel.setForeground(Color.RED);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            dataPanel.add(errorLabel, gbc);

            // Log the error to the console for debugging purposes
            e.printStackTrace();
        }

        // Add the data panel to the main panel
        JScrollPane scrollPane = new JScrollPane(dataPanel);
        scrollPane.setBorder(null); // Remove scroll pane border
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane);

        return panel;
    }

    /** Helper method to add a data row to the data panel */
    private void addDataRow(String label, String value, JPanel dataPanel, GridBagConstraints gbc) {
        JLabel keyLabel = new JLabel(label);
        keyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE; // Automatically increments the row
        gbc.weightx = 0.3; // Column width ratio
        dataPanel.add(keyLabel, gbc);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 0.7; // Column width ratio
        dataPanel.add(valueLabel, gbc);
    }

    /** Create Courses Panel to show the student's courses */
    private JPanel createCoursesPanel() {
        JPanel coursesPanel = new JPanel(new BorderLayout());
        coursesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Your Enrolled Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        coursesPanel.add(titleLabel, BorderLayout.NORTH);

        // Define columns for Courses Table
        String[] columnNames = {"Course Name", "Duration"};

        // Fetch initial data for courses
        Object[][] enrolledCoursesData = fetchEnrolledClasses();

        // Create JTable and set DefaultTableModel
        JTable coursesTable = new JTable(enrolledCoursesData, columnNames);

        // Set larger font for table rows
        Font tableFont = new Font("Arial", Font.PLAIN, 16); // Font size for table rows
        coursesTable.setFont(tableFont);
        coursesTable.setRowHeight(30); // Adjust row height for larger font

        // Set larger font for column headers
        Font headerFont = new Font("Arial", Font.BOLD, 18); // Font size for header
        coursesTable.getTableHeader().setFont(headerFont);
        coursesTable.getTableHeader().setReorderingAllowed(false); // Disable column reordering

        // Fixing the size of the JTable
        int tableWidth = 500; // Fixed table width
        int tableHeight = 300; // Fixed table height
        coursesTable.setPreferredScrollableViewportSize(new Dimension(tableWidth, tableHeight));

        // Wrap the table in a scroll pane
        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setPreferredSize(new Dimension(tableWidth, tableHeight)); // Match scroll pane size with table

        coursesPanel.add(scrollPane, BorderLayout.CENTER);

        // Add a refresh button (optional, for testing or extra functionality)
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14)); // Adjust font for button
        refreshButton.setBackground(Color.GRAY);
        refreshButton.addActionListener(e -> refreshCoursesPanel(coursesTable));
        coursesPanel.add(refreshButton, BorderLayout.SOUTH);

        return coursesPanel;
    }

    /** Create Schedule Panel to show the student's schedule */
    private JPanel createSchedulePanel() {
        JPanel schedulePanel = new JPanel();
        schedulePanel.setLayout(new BorderLayout());
        schedulePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Your Schedule");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        schedulePanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Course Name", "Date", "Time"};
        Object[][] data = {}; // Placeholder for table data

        // Fetch schedule data from database
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT c.CourseName, s.Schedule_Date, s.Time " +
                             "FROM Enrollment e " +
                             "JOIN Course c ON e.Course_ID = c.Course_ID " +
                             "JOIN Schedule s ON c.Course_ID = s.Course_ID " +
                             "WHERE e.Student_ID = ? " +
                             "ORDER BY s.Schedule_Date, s.Time")) {
            statement.setInt(1, userID); // Use the logged-in student's ID
            ResultSet resultSet = statement.executeQuery();

            // Populate rows dynamically
            java.util.List<Object[]> rows = new java.util.ArrayList<>();
            while (resultSet.next()) {
                String courseName = resultSet.getString("CourseName");
                Date scheduleDate = resultSet.getDate("Schedule_Date");
                Time scheduleTime = resultSet.getTime("Time");
                rows.add(new Object[]{courseName, scheduleDate.toString(), scheduleTime.toString()}); // Format Date & Time
            }

            // Convert List to Array for JTable
            data = rows.toArray(new Object[0][]);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch schedule. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Create JTable with schedule data
        JTable scheduleTable = new JTable(data, columnNames);

        // Set larger font for table rows
        Font tableFont = new Font("Arial", Font.PLAIN, 16); // Font size for table rows
        scheduleTable.setFont(tableFont);
        scheduleTable.setRowHeight(30); // Adjust row height for larger font

        // Set larger font for table header
        Font headerFont = new Font("Arial", Font.BOLD, 18); // Larger font for headers
        scheduleTable.getTableHeader().setFont(headerFont);
        scheduleTable.getTableHeader().setReorderingAllowed(false); // Disable column reordering

        // Fix table size
        int tableWidth = 500; // Fixed table width
        int tableHeight = 300; // Fixed table height
        scheduleTable.setPreferredScrollableViewportSize(new Dimension(tableWidth, tableHeight));

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setPreferredSize(new Dimension(tableWidth, tableHeight)); // Ensure the scroll pane matches the table's size

        schedulePanel.add(scrollPane, BorderLayout.CENTER);

        return schedulePanel;
    }

    //_____________________________________________________________
    private void enrollInCourse(String courseName) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO Enrollment (Student_ID, Course_ID, Enrollment_Date) " +
                             "VALUES (?, (SELECT Course_ID FROM Course WHERE CourseName = ?), CURDATE())")) {
            statement.setInt(1, userID); // Logged-in student's ID
            statement.setString(2, courseName); // Selected course name
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Successfully enrolled in the course: " + courseName);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to enroll in the course: " + courseName, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to enroll in the selected course.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Object[][] fetchEnrolledClasses() {
        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT c.CourseName, c.Duration " +
                             "FROM Enrollment e " +
                             "JOIN Course c ON e.Course_ID = c.Course_ID " +
                             "WHERE e.Student_ID = ?")) {
            statement.setInt(1, userID); // Logged-in student's ID
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String courseName = resultSet.getString("CourseName");
                int duration = resultSet.getInt("Duration");
                rows.add(new Object[]{courseName, duration});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch enrolled classes.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return rows.toArray(new Object[0][]);
    }

    private void refreshEnrollmentTables(JTable availableClassesTable, JTable enrolledClassesTable) {
        Object[][] availableClassesData = fetchAvailableClasses();
        Object[][] enrolledClassesData = fetchEnrolledClasses();

        // Update Available Classes Table
        availableClassesTable.setModel(new DefaultTableModel(availableClassesData, new String[]{"Course Name", "Duration"}));

        // Update Enrolled Classes Table
        enrolledClassesTable.setModel(new DefaultTableModel(enrolledClassesData, new String[]{"Course Name", "Duration"}));
    }

    private Object[][] fetchAvailableClasses() {
        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT c.CourseName, c.Duration " +
                             "FROM Course c " +
                             "WHERE c.Course_ID NOT IN ( " +
                             "    SELECT e.Course_ID " +
                             "    FROM Enrollment e " +
                             "    WHERE e.Student_ID = ?)")) {
            statement.setInt(1, userID); // Logged-in student's ID
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String courseName = resultSet.getString("CourseName");
                int duration = resultSet.getInt("Duration");
                rows.add(new Object[]{courseName, duration});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch available classes.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return rows.toArray(new Object[0][]);
    }

    private void refreshCoursesPanel(JTable coursesTable) {
        // Fetch updated data for enrolled courses
        Object[][] enrolledCoursesData = fetchEnrolledClasses(); // Reuse this method for fetching enrolled courses

        // Update Courses Table Model
        DefaultTableModel coursesModel = new DefaultTableModel(
                enrolledCoursesData,
                new String[]{"Course Name", "Duration"} // Same column names as the table
        );
        coursesTable.setModel(coursesModel); // Set the refreshed model to the table
    }

    /** Create Enrollment Panel to enroll in and display classes */
    private JPanel createEnrollmentPanel() {
        JPanel enrollmentPanel = new JPanel(new BorderLayout());
        enrollmentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title Label
        JLabel titleLabel = new JLabel("Class Enrollment");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        enrollmentPanel.add(titleLabel, BorderLayout.NORTH);

        // Tables for Available Classes and Enrolled Classes
        String[] columnNames = {"Course Name", "Duration"};
        Object[][] availableClassesData = {}; // Placeholder for Available Classes Data
        Object[][] enrolledClassesData = {}; // Placeholder for Enrolled Classes Data

        // Fetch data from database
        availableClassesData = fetchAvailableClasses();
        enrolledClassesData = fetchEnrolledClasses();

        // Available Classes Table
        JTable availableClassesTable = new JTable(availableClassesData, columnNames);

        // Set larger font and row height for the Available Classes table
        Font tableFont = new Font("Arial", Font.PLAIN, 16); // Font for table rows
        availableClassesTable.setFont(tableFont);
        availableClassesTable.setRowHeight(30); // Adjust row height for larger font
        availableClassesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18)); // Font for headers
        availableClassesTable.getTableHeader().setReorderingAllowed(false); // Disable reordering

        JScrollPane availableClassesScrollPane = new JScrollPane(availableClassesTable);

        // Enrolled Classes Table
        JTable enrolledClassesTable = new JTable(enrolledClassesData, columnNames);

        // Set larger font and row height for the Enrolled Classes table
        enrolledClassesTable.setFont(tableFont);
        enrolledClassesTable.setRowHeight(30); // Adjust row height for larger font
        enrolledClassesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18)); // Font for headers
        enrolledClassesTable.getTableHeader().setReorderingAllowed(false); // Disable reordering

        JScrollPane enrolledClassesScrollPane = new JScrollPane(enrolledClassesTable);

        // Fixing the sizes of both tables
        int tableWidth = 500; // Fixed width for the tables
        int tableHeight = 250; // Fixed height for the tables
        availableClassesTable.setPreferredScrollableViewportSize(new Dimension(tableWidth, tableHeight));
        enrolledClassesTable.setPreferredScrollableViewportSize(new Dimension(tableWidth, tableHeight));
        availableClassesScrollPane.setPreferredSize(new Dimension(tableWidth, tableHeight));
        enrolledClassesScrollPane.setPreferredSize(new Dimension(tableWidth, tableHeight));

        // Panels for Tables
        JPanel availableClassesPanel = new JPanel(new BorderLayout());
        JLabel availableLabel = new JLabel("Available Classes:");
        availableLabel.setFont(new Font("Arial", Font.BOLD, 16));
        availableClassesPanel.add(availableLabel, BorderLayout.NORTH);
        availableClassesPanel.add(availableClassesScrollPane, BorderLayout.CENTER);

        JPanel enrolledClassesPanel = new JPanel(new BorderLayout());
        JLabel enrolledLabel = new JLabel("Enrolled Classes:");
        enrolledLabel.setFont(new Font("Arial", Font.BOLD, 16));
        enrolledClassesPanel.add(enrolledLabel, BorderLayout.NORTH);
        enrolledClassesPanel.add(enrolledClassesScrollPane, BorderLayout.CENTER);

        // "Enroll" Button
        JButton enrollButton = new JButton("Enroll >>");
        enrollButton.setFont(new Font("Arial", Font.PLAIN, 14)); // Set smaller font for button
        enrollButton.setPreferredSize(new Dimension(100, 30)); // Smaller size for the button
        enrollButton.setMinimumSize(new Dimension(100, 30)); // Ensure button size doesn't shrink too much
        enrollButton.setMaximumSize(new Dimension(100, 30)); // Prevent the button from being scaled too large

// Set a silver-like color using Color.LIGHT_GRAY
        enrollButton.setBackground(Color.LIGHT_GRAY);

// Optional: Make the button foreground text more visible with a contrasting color
        enrollButton.setForeground(Color.BLACK);

// Optional: Remove the border around the button for a clean look
        enrollButton.setBorderPainted(false);

// Add action listener for button functionality
        enrollButton.addActionListener(e -> {
            int selectedRow = availableClassesTable.getSelectedRow(); // Get selected course
            if (selectedRow != -1) {
                String selectedCourseName = (String) availableClassesTable.getValueAt(selectedRow, 0); // Course Name
                int selectedDuration = (int) availableClassesTable.getValueAt(selectedRow, 1); // Course Duration
                enrollInCourse(selectedCourseName); // Enroll student in the selected course
                refreshEnrollmentTables(availableClassesTable, enrolledClassesTable); // Refresh tables
            } else {
                JOptionPane.showMessageDialog(enrollmentPanel, "Please select a course to enroll.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Combine both panels and button into one layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(availableClassesPanel, BorderLayout.WEST);
        centerPanel.add(enrollButton, BorderLayout.CENTER);
        centerPanel.add(enrolledClassesPanel, BorderLayout.EAST);

        enrollmentPanel.add(centerPanel, BorderLayout.CENTER);

        return enrollmentPanel;
    }

    /** Create Assignments Panel to display and manage assignments */
    public JPanel createAssignmentsPanel() {
        // Main panel setup
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("My Assignments");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 60, 60));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Add refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> refreshAssignmentsPanel());
        titlePanel.add(refreshButton, BorderLayout.EAST);

        panel.add(titlePanel, BorderLayout.NORTH);

        // Table with assignments data
        String[] columnNames = {"Assignment ID", "Course", "Assignment Name", "Due Date", "Description", "Status", "Grade", "Download", "Actions"};
        Object[][] data = fetchAssignmentsDataForDownload(userID); // Include files for download

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only make the Download and Actions column editable
                return column == 7 || column == 8;
            }
        };

        JTable assignmentsTable = new JTable(model);
        assignmentsTable.setRowHeight(35);
        assignmentsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        assignmentsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        assignmentsTable.setSelectionBackground(new Color(230, 240, 250));
        assignmentsTable.setGridColor(new Color(240, 240, 240));
        assignmentsTable.setShowVerticalLines(true);

        // Center-align content in all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < assignmentsTable.getColumnCount() - 1; i++) {
            assignmentsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Add button renderer and editor for Actions and Download columns
        assignmentsTable.getColumnModel().getColumn(8).setCellRenderer(new DownloadButtonRenderer());
        assignmentsTable.getColumnModel().getColumn(8).setCellEditor(new DownloadButtonEditor(assignmentsTable, this));

        assignmentsTable.getColumnModel().getColumn(7).setCellRenderer(new SubmitButtonRenderer());
        assignmentsTable.getColumnModel().getColumn(7).setCellEditor(new SubmitButtonEditor(assignmentsTable, this));

        JScrollPane scrollPane = new JScrollPane(assignmentsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /** Fetch assignments for the logged-in student, including download links for instructor uploads */
    private Object[][] fetchAssignmentsDataForDownload(int studentID) {
        ArrayList<Object[]> assignmentsList = new ArrayList<>();

        // Corrected query to reference the correct column 'Assignment_File'
        String query =
                "SELECT a.Assignment_ID, c.CourseName, a.Assignment_Name, a.Due_Date, a.Description, " +
                        "CASE " +
                        "   WHEN s.Submission_ID IS NULL THEN 'Not Submitted' " +
                        "   ELSE s.Submission_Status " +
                        "END AS Status, " +
                        "s.Grade, " +
                        "a.Assignment_File " + // Use the correct column name here
                        "FROM Assignment a " +
                        "JOIN Course c ON a.Course_ID = c.Course_ID " +
                        "JOIN Enrollment e ON c.Course_ID = e.Course_ID " +
                        "LEFT JOIN Submission s ON a.Assignment_ID = s.Assignment_ID AND s.Student_ID = ? " +
                        "WHERE e.Student_ID = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, studentID);
            statement.setInt(2, studentID);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Format grade if it exists
                    Object grade = resultSet.getObject("Grade");
                    String gradeStr = grade == null ? "Not Graded" : String.format("%.1f", resultSet.getDouble("Grade"));

                    // Checking if assignment file exists
                    boolean fileExists = resultSet.getBytes("Assignment_File") != null;

                    assignmentsList.add(new Object[] {
                            resultSet.getInt("Assignment_ID"),
                            resultSet.getString("CourseName"),
                            resultSet.getString("Assignment_Name"),
                            resultSet.getDate("Due_Date"),
                            resultSet.getString("Description"),
                            resultSet.getString("Status"),
                            gradeStr,
                            fileExists ? "Download" : "N/A", // Indicate if a file exists for download
                            "Submit"
                    });
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error fetching assignments: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return assignmentsList.toArray(new Object[0][0]);
    }

    class DownloadButtonRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JButton button = new JButton(value != null ? "Download" : "N/A");
            button.setEnabled(value != null && value.equals("Download"));
            return button;
        }
    }

    class DownloadButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton button;
        private String fileName;
        private int assignmentID; // Store the Assignment ID here


        public DownloadButtonEditor(JTable table, Component parent) {
            button = new JButton("Download");
            button.setBackground(Color.WHITE);
            button.addActionListener(e -> {
                if (fileName != null) {
                    downloadAssignmentFile(fileName, assignmentID); // Pass both fileName and assignmentID
                }
            });
        }

        /** Copies the content of the uploaded file to the target file */
        private void copyFile(File sourceFile, File targetFile) throws IOException {
            try (FileInputStream fileInputStream = new FileInputStream(sourceFile);
                 FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
                 FileChannel sourceChannel = fileInputStream.getChannel();
                 FileChannel targetChannel = fileOutputStream.getChannel()) {
                targetChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            }
        }

        /** Fetch file for downloading by its assignment ID */
        private File getAssignmentFile(int assignmentId) {
            String query = "SELECT FilePath FROM Assignment WHERE Assignment_ID = ?";
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, assignmentId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String filePath = resultSet.getString("FilePath");
                        if (filePath != null && !filePath.isEmpty()) {
                            return new File("assignments/" + filePath); // Construct the full file path
                        }
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        "Error fetching assignment file path: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
            return null; // Return null if no file is found
        }



        private void downloadFile(String fileName) {
            if (fileName != null) {
                File file = new File("assignments/" + fileName);
                if (file.exists()) {
                    // Proceed with the download logic
                } else {
                    JOptionPane.showMessageDialog(null, "File does not exist!");
                }
            }
        }

        @Override
        public Object getCellEditorValue() {
            return fileName;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // Fetch assignmentID (assume it is in the first column of the table)
            assignmentID = (int) table.getValueAt(row, 0); // Assuming assignmentID is in column 0

            // Dynamically fetch the file name based on assignmentID
            fileName = value != null && value.equals("Download") ? getAssignmentFileName(assignmentID) : null;

            return button; // Return the button as the editor component
        }

        private String getAssignmentFileName(Object assignmentId) {
            String filePath = null;
            String query = "SELECT FilePath FROM Assignment WHERE Assignment_ID = ?";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, (int) assignmentId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        filePath = resultSet.getString("FilePath");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return (filePath != null) ? new File(filePath).getName() : null;
        }

        private void downloadAssignmentFile(String fileName, int assignmentID) {
            System.out.println("Downloading file: " + fileName + " for Assignment ID: " + assignmentID);

            // Custom download logic here
            // Example:
            File file = new File("assignments/" + fileName);
            if (file.exists()) {
                // Code to handle file download
                JOptionPane.showMessageDialog(null, "File download started for assignment ID: " + assignmentID);
            } else {
                JOptionPane.showMessageDialog(null, "File does not exist for Assignment ID: " + assignmentID);
            }
        }




    }

    private String getAssignmentFileName(Object assignmentId) {
        String filePath = null;
        String query = "SELECT FilePath FROM Assignment WHERE Assignment_ID = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, (int) assignmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    filePath = resultSet.getString("FilePath");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (filePath != null) ? new File(filePath).getName() : null;
    }



    /** Fetch assignments for the logged-in student from the database */
    private Object[][] fetchAssignmentsData(int studentID) {
        ArrayList<Object[]> assignmentsList = new ArrayList<>();

        String query =
                "SELECT a.Assignment_ID, c.CourseName, a.Assignment_Name, a.Due_Date, a.Description, " +
                        "CASE " +
                        "   WHEN s.Submission_ID IS NULL THEN 'Not Submitted' " +
                        "   ELSE s.Submission_Status " +
                        "END AS Status, " +
                        "s.Grade " +
                        "FROM Assignment a " +
                        "JOIN Course c ON a.Course_ID = c.Course_ID " +
                        "JOIN Enrollment e ON c.Course_ID = e.Course_ID " +
                        "LEFT JOIN Submission s ON a.Assignment_ID = s.Assignment_ID AND s.Student_ID = ? " +
                        "WHERE e.Student_ID = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, studentID);
            statement.setInt(2, studentID);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Format grade if it exists
                    Object grade = resultSet.getObject("Grade");
                    String gradeStr = grade == null ? "Not Graded" : String.format("%.1f", resultSet.getDouble("Grade"));

                    assignmentsList.add(new Object[] {
                            resultSet.getInt("Assignment_ID"),
                            resultSet.getString("CourseName"),
                            resultSet.getString("Assignment_Name"),
                            resultSet.getDate("Due_Date"),
                            resultSet.getString("Description"), // Add Description
                            resultSet.getString("Status"),
                            gradeStr,
                            "Submit" // Placeholder for the button or action
                    });
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error fetching assignments: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return assignmentsList.toArray(new Object[0][0]);
    }

    /** Refresh the assignments panel */
    private void refreshAssignmentsPanel() {
        // Find the index of the assignments panel in the card panel
        int assignmentsPanelIndex = -1;
        for (int i = 0; i < cardPanel.getComponentCount(); i++) {
            if (cardPanel.getComponent(i).getName() != null &&
                    cardPanel.getComponent(i).getName().equals("assignments")) {
                assignmentsPanelIndex = i;
                break;
            }
        }

        if (assignmentsPanelIndex != -1) {
            cardPanel.remove(assignmentsPanelIndex);
            JPanel newAssignmentsPanel = createAssignmentsPanel();
            newAssignmentsPanel.setName("assignments");
            cardPanel.add(newAssignmentsPanel, "assignments", assignmentsPanelIndex);
            cardLayout.show(cardPanel, "assignments");
            cardPanel.revalidate();
            cardPanel.repaint();
        }
    }

    /** Upload assignment file */
    public void uploadAssignment(int assignmentId, String courseName, String assignmentName) {
        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Assignment File to Upload");

        // Show the file dialog
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                // Create directory structure if it doesn't exist
                File submissionsDir = new File("submissions");
                if (!submissionsDir.exists()) {
                    submissionsDir.mkdir();
                }

                // Create a unique filename to store the uploaded file
                String targetFilename = userID + "_" + assignmentId + "_" + selectedFile.getName();
                File targetFile = new File("submissions/" + targetFilename);

                // Copy the file
                copyFile(selectedFile, targetFile);

                // Update the database to record this submission
                recordSubmission(assignmentId, targetFilename);

                JOptionPane.showMessageDialog(this,
                        "Assignment successfully uploaded!",
                        "Upload Complete",
                        JOptionPane.INFORMATION_MESSAGE);

                // Refresh the assignments panel
                refreshAssignmentsPanel();

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error uploading file: " + e.getMessage(),
                        "Upload Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /** Copy a file from source to destination */
    private void copyFile(File source, File dest) throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(dest)) {

            byte[] buffer = new byte[1024];
            int length;

            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }

    /** Fetch file for downloading by its assignment ID */
    private File getAssignmentFile(int assignmentId) {
        String query = "SELECT FilePath FROM Assignment WHERE Assignment_ID = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, assignmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String filePath = resultSet.getString("FilePath");
                    if (filePath != null && !filePath.isEmpty()) {
                        return new File("assignments/" + filePath); // Construct the full file path
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error fetching assignment file path: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null; // Return null if no file is found
    }

    /** Allow students to download a selected assignment file */
    private void downloadAssignmentFile(int assignmentId) {
        File file = getAssignmentFile(assignmentId);
        if (file != null && file.exists()) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Assignment File");
            fileChooser.setSelectedFile(new File(file.getName()));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File destinationFile = fileChooser.getSelectedFile();
                try {
                    copyFile(file, destinationFile); // Reuse the `copyFile` method
                    JOptionPane.showMessageDialog(this,
                            "Assignment file downloaded successfully!",
                            "Download Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this,
                            "Error downloading file: " + e.getMessage(),
                            "File Error",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "No file found for the selected assignment.",
                    "File Not Found",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Record the submission in the database */
    private void recordSubmission(int assignmentId, String filename) {
        // First check if a submission already exists
        String checkQuery = "SELECT Submission_ID FROM Submission WHERE Assignment_ID = ? AND Student_ID = ?";
        String insertQuery = "INSERT INTO Submission (Assignment_ID, Student_ID, Submission_Date, Grade, Submission_Status, FilePath) " +
                "VALUES (?, ?, CURRENT_DATE, NULL, 'Submitted', ?)";
        String updateQuery = "UPDATE Submission SET Submission_Date = CURRENT_DATE, Submission_Status = 'Submitted', FilePath = ? " +
                "WHERE Assignment_ID = ? AND Student_ID = ?";

        try (Connection connection = getConnection()) {
            boolean exists = false;

            // Check if submission exists
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, assignmentId);
                checkStmt.setInt(2, userID);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        exists = true;
                    }
                }
            }

            // Insert or update based on existence
            if (exists) {
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, filename);
                    updateStmt.setInt(2, assignmentId);
                    updateStmt.setInt(3, userID);
                    updateStmt.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, assignmentId);
                    insertStmt.setInt(2, userID);
                    insertStmt.setString(3, filename);
                    insertStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error recording submission: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Custom button renderer for the Actions column
    class SubmitButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton submitButton;

        public SubmitButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            submitButton = new JButton("Submit");
            submitButton.setFont(new Font("Arial", Font.PLAIN, 12));
            submitButton.setBackground(new Color(70, 130, 180));
            submitButton.setForeground(Color.WHITE);
            submitButton.setFocusPainted(false);
            submitButton.setBorderPainted(true);
            add(submitButton);
            setBackground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            // Change button text based on submission status
            String status = (String) table.getValueAt(row, 4);
            if ("Submitted".equals(status) || "Graded".equals(status) || "Late".equals(status)) {
                submitButton.setText("Resubmit");
            } else {
                submitButton.setText("Submit");
            }
            return this;
        }
    }

    // Custom button editor for the Actions column
    class SubmitButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private StudentMenu studentMenu;

        public SubmitButtonEditor(JTable table, StudentMenu studentMenu) {
            super(new JTextField());
            this.table = table;
            this.studentMenu = studentMenu;

            button = new JButton();
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setBackground(new Color(70, 130, 180));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(true);

            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            // Set button text based on submission status
            String status = (String) table.getValueAt(row, 4);
            if ("Submitted".equals(status) || "Graded".equals(status) || "Late".equals(status)) {
                label = "Resubmit";
            } else {
                label = "Submit";
            }
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Get assignment information
                int row = table.getEditingRow();
                int assignmentId = Integer.parseInt(table.getValueAt(row, 0).toString());
                String courseName = (String) table.getValueAt(row, 1);
                String assignmentName = (String) table.getValueAt(row, 2);

                // Open file selection and upload dialog
                studentMenu.uploadAssignment(assignmentId, courseName, assignmentName);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
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

    /** Create Announcement Panel to display announcements from instructors */
    private JPanel createAnnouncementPanel() {
        // Main panel to hold all announcements
        JPanel announcementPanel = new JPanel();
        announcementPanel.setLayout(new BoxLayout(announcementPanel, BoxLayout.Y_AXIS));
        announcementPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding for a cleaner layout

        // Read announcements from a file
        List<String[]> announcements = fetchAnnouncements();

        // Display each announcement as a separate panel
        for (String[] announcement : announcements) {
            String courseName = announcement[0];
            String message = announcement[1];

            // Fetch the instructor's name from the database for the given course
            String instructorName = fetchInstructorForCourse(courseName);

            // Create an individual announcement panel
            JPanel singleAnnouncementPanel = new JPanel();
            singleAnnouncementPanel.setLayout(new BorderLayout());
            singleAnnouncementPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 2),
                    new EmptyBorder(20, 20, 20, 20)
            ));
            singleAnnouncementPanel.setBackground(Color.WHITE);

            // Header: Course Name and Instructor Name
            JLabel headerLabel = new JLabel("Course: " + courseName + " | Instructor: " + instructorName, JLabel.LEFT);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Larger bold font

            // Message of the announcement
            JTextArea messageArea = new JTextArea(message);
            messageArea.setFont(new Font("Arial", Font.PLAIN, 16)); // Larger plain font
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            messageArea.setEditable(false);
            messageArea.setBackground(new Color(245, 245, 245));

            // Add headerLabel and messageArea to the single announcement panel
            singleAnnouncementPanel.add(headerLabel, BorderLayout.NORTH);
            singleAnnouncementPanel.add(messageArea, BorderLayout.CENTER);

            // Add the single announcement panel to the main announcements panel
            announcementPanel.add(singleAnnouncementPanel);
            announcementPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add spacing between announcements
        }

        // Wrap the announcement panel in a scroll pane for vertical scrolling
        JScrollPane scrollPane = new JScrollPane(announcementPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Prevent horizontal scrolling

        // Create a wrapper panel to add the scroll pane and ensure full layout
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);

        return wrapperPanel; // Return the entire announcement panel with scrolling enabled
    }

    /** Fetch announcements from a file */
    private List<String[]> fetchAnnouncements() {
        List<String[]> announcements = new ArrayList<>();

        // Define the file path where announcements are stored
        String filePath = "src/announcements.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Read each line in the file
            while ((line = reader.readLine()) != null) {
                // Assume each line is in the format: courseName|message
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    announcements.add(new String[]{parts[0].trim(), parts[1].trim()});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return announcements;
    }

    /** Fetch instructor's name from the database based on the course name */
    private String fetchInstructorForCourse(String courseName) {
        String instructorName = "Unknown"; // Default value if no instructor is found

        String query = "SELECT u.Full_Name AS InstructorName " +
                "FROM Course c " +
                "INNER JOIN Instructor i ON c.Instructor_ID = i.User_ID " +
                "INNER JOIN Users u ON i.User_ID = u.User_ID " +
                "WHERE c.CourseName = ?";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/elms", "root", "1234");
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, courseName);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                instructorName = resultSet.getString("InstructorName");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return instructorName;
    }

//    private JPanel createCareerServicesPanel() { return createPlaceholderPanel("Career Services Page"); }

    /** A helper to create placeholder panels */
    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(new Color(60, 60, 60));

        panel.add(label);
        return panel;
    }

    /** Database connection setup */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/elms", "root", "1234"); // Update credentials as needed.
    }
    //_____________________________________________________




    /** Main method for testing */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Student Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Test with a sample user ID, e.g., 1
        int testUserID = 1;

        StudentMenu studentMenu = new StudentMenu(testUserID);
        frame.add(studentMenu);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen.
        frame.setVisible(true);
    }
}