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
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.TableCellRenderer;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class InstructorMenu extends JPanel {
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel;
    private int userID; // Logged-in user ID.
    private String userFirstName; // To store user's first name dynamically fetched from the database.
    private JTable submissionsTable;

    // Constructor
    public InstructorMenu(int userID) {
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
        cardPanel.add(createSendAnnouncementPanel(), "announcementsCard");
        cardPanel.add(createAssignmentsPanel(), "assignmentsCard");
        cardPanel.add(createCreateAssignmentPanel(), "createAssignment"); // Add this line
        cardPanel.add(createCourseListPanel(), "courseListCard");
        cardPanel.add(createStudentListPanel(), "studentListCard");
        cardPanel.add(createAccountSettingsPanel(), "accountSettingsCard");
        cardPanel.add(createAboutUsPanel(), "aboutUsCard");

        add(cardPanel, BorderLayout.CENTER);
    }

    /** Fetch the user's first name from the database */
    private String fetchUserFirstName(int userID) {
        String firstName = "Instructor"; // Default to "Instructor" if no name is found.
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

    /** Create a sidebar dashboard with navigation buttons */
    private JPanel createDashboard() {
        JPanel dashboard = new JPanel();
        dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));
        dashboard.setBorder(new EmptyBorder(20, 15, 20, 15));
        dashboard.setBackground(new Color(211, 211, 211)); // Light silver background.

        // Add buttons for the sidebar with updated labels
        dashboard.add(createSidebarButton("Grade Assignment", "assignmentsCard"));
        dashboard.add(Box.createVerticalStrut(10));
        dashboard.add(createSidebarButton("Create Assignment", "createAssignment")); // Add this line
        dashboard.add(Box.createVerticalStrut(10));

        dashboard.add(createSidebarButton("Send Announcements", "announcementsCard"));
        dashboard.add(Box.createVerticalStrut(10));
        dashboard.add(createSidebarButton("Course List", "courseListCard"));
        dashboard.add(Box.createVerticalStrut(10));
        dashboard.add(createSidebarButton("Student List", "studentListCard"));
        dashboard.add(Box.createVerticalStrut(10));
        dashboard.add(createSidebarButton("Gradebook", "gradesCard"));
        dashboard.add(Box.createVerticalStrut(10));
        dashboard.add(createSidebarButton("AccountSettings", "accountSettingsCard"));
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

    // Class-level variable for gradesTable
    private JTable gradesTable; // Moved gradesTable to class level

    private JPanel createGradesPanel() {
        JPanel gradesPanel = new JPanel();
        gradesPanel.setLayout(new BorderLayout());

        // Panel title
        JLabel titleLabel = new JLabel("Grades Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30)); // Increased title font size
        gradesPanel.add(titleLabel, BorderLayout.NORTH);

        // Table to display grades
        String[] columnNames = {"Student Name", "Course Name", "Grade"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            // Make only the "Grade" column editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only Grade column (index 2) is editable
            }
        };

        // Initialize the class-level gradesTable variable
        gradesTable = new JTable(tableModel);

        // Customize font size for the table
        gradesTable.setFont(new Font("Arial", Font.PLAIN, 18)); // Increased font size for table content
        gradesTable.setRowHeight(30); // Increased row height for better spacing

        // Customize font size for table header
        gradesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20)); // Larger header font
        gradesTable.getTableHeader().setPreferredSize(new Dimension(50, 40)); // Increased header height

        // Fetch grades data from the database
        fetchGrades(userID, tableModel);

        // Add table to the center scroll pane
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        gradesPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel at the bottom
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Update button
        JButton updateButton = new JButton("Update Grades");
        updateButton.setBackground(new Color(0xC0C0C0)); // Silver
        updateButton.setFont(new Font("Arial", Font.BOLD, 20)); // Larger font size for button
        buttonsPanel.add(updateButton);

        gradesPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Update grades when the Update button is clicked
        updateButton.addActionListener(e -> updateGrades(tableModel));

        return gradesPanel;
    }

    /** Fetch grades of students for courses taught by this instructor */
    private void fetchGrades(int instructorId, DefaultTableModel tableModel) {
        String dbUrl = "jdbc:mysql://localhost:3306/elms";
        String dbUser = "root";
        String dbPassword = "1234";

        String query = "SELECT u.Full_Name, c.CourseName, e.Grade " +
                "FROM Users u " +
                "JOIN Student s ON u.User_ID = s.User_ID " +
                "JOIN Enrollment e ON s.User_ID = e.Student_ID " +
                "JOIN Course c ON e.Course_ID = c.Course_ID " +
                "WHERE c.Instructor_ID = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            // Clear existing rows
            tableModel.setRowCount(0);

            // Populate grades into the table model
            while (rs.next()) {
                String studentName = rs.getString("Full_Name");
                String courseName = rs.getString("CourseName");
                Double grade = rs.getObject("Grade") != null ? rs.getDouble("Grade") : null;

                tableModel.addRow(new Object[]{studentName, courseName, grade != null ? grade : "N/A"});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Error fetching grades from the database: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Update grades in the database */
    private void updateGrades(DefaultTableModel tableModel) {
        String dbUrl = "jdbc:mysql://localhost:3306/elms";
        String dbUser = "root";
        String dbPassword = "1234";

        String updateQuery = "UPDATE Enrollment SET Grade = ? WHERE Student_ID = ? AND Course_ID = ?";

        boolean success = true; // Indicate if all updates succeeded
        StringBuilder errorMessages = new StringBuilder(); // Collect multiple error messages

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            // Iterate through table rows to process updates
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String studentName = (String) tableModel.getValueAt(i, 0); // Student name
                String courseName = (String) tableModel.getValueAt(i, 1);  // Course name
                Object gradeObj = tableModel.getValueAt(i, 2);             // Editable grade column

                // Skip rows with "N/A" grade
                if (gradeObj == null || gradeObj.toString().trim().isEmpty() || gradeObj.toString().equalsIgnoreCase("N/A")) {
                    continue;
                }

                try {
                    // Parse grade as a double
                    double grade = Double.parseDouble(gradeObj.toString());

                    // Validate grade bounds (0-100)
                    if (grade < 0 || grade > 100) {
                        throw new NumberFormatException("Grade out of range");
                    }

                    // Fetch student and course IDs
                    int studentId = fetchStudentIdByNameAndCourse(studentName, courseName, conn);
                    int courseId = fetchCourseIdByName(courseName, conn);

                    // Execute the update query
                    stmt.setDouble(1, grade);
                    stmt.setInt(2, studentId);
                    stmt.setInt(3, courseId);
                    stmt.executeUpdate();

                } catch (NumberFormatException ex) {
                    success = false;
                    errorMessages.append("Invalid grade for '").append(studentName).append("' in '")
                            .append(courseName).append("'. Must be a number between 0-100.\n");
                } catch (Exception ex) {
                    success = false;
                    errorMessages.append("Failed to update grade for '").append(studentName).append("' in '")
                            .append(courseName).append("': ").append(ex.getMessage()).append("\n");
                }
            }

            // Display the appropriate success or error message
            if (success) {
                JOptionPane.showMessageDialog(
                        null,
                        "All grades updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Some errors occurred while updating grades:\n" + errorMessages.toString(),
                        "Errors",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Database connection error: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Fetch the student ID based on their name and course (utility function) */
    private int fetchStudentIdByNameAndCourse(String studentName, String courseName, Connection conn) throws Exception {
        String query = "SELECT s.User_ID FROM Users u " +
                "JOIN Student s ON u.User_ID = s.User_ID " +
                "JOIN Enrollment e ON s.User_ID = e.Student_ID " +
                "JOIN Course c ON e.Course_ID = c.Course_ID " +
                "WHERE u.Full_Name = ? AND c.CourseName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentName);
            stmt.setString(2, courseName);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("User_ID");
            } else {
                throw new Exception("Student ID not found for: " + studentName + " in course: " + courseName);
            }
        }
    }

    /** Fetch the course ID based on its name (utility function) */
    private int fetchCourseIdByName(String courseName, Connection conn) throws Exception {
        String query = "SELECT Course_ID FROM Course WHERE CourseName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseName);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Course_ID");
            } else {
                throw new Exception("Course ID not found for: " + courseName);
            }
        }
    }

    private JPanel createSendAnnouncementPanel() {
        JPanel sendAnnouncementPanel = new JPanel();
        sendAnnouncementPanel.setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Send Announcement", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30)); // Large panel title font
        sendAnnouncementPanel.add(titleLabel, BorderLayout.NORTH);

        // Main panel layout for the selection and text field (left and right)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2, 10, 10)); // 1 row, 2 columns (left: course list, right: announcement input)

        // === Left Panel: Written List of Courses ===
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());

        JLabel courseListLabel = new JLabel("Select a Course:", JLabel.CENTER);
        courseListLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Fetch courses dynamically for the instructor
        String[] courseNames = fetchInstructorCourses(userID);
        DefaultListModel<String> courseListModel = new DefaultListModel<>();
        for (String course : courseNames) {
            courseListModel.addElement(course);
        }

        // Create JList for courses
        JList<String> courseList = new JList<>(courseListModel);
        courseList.setFont(new Font("Arial", Font.PLAIN, 14));
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only one course can be selected
        courseList.setVisibleRowCount(10); // Display up to 10 courses at a time
        courseList.setFixedCellHeight(30); // Make each course in the list more visually distinct
        JScrollPane courseScrollPane = new JScrollPane(courseList); // Scroll pane in case of many courses

        leftPanel.add(courseListLabel, BorderLayout.NORTH);
        leftPanel.add(courseScrollPane, BorderLayout.CENTER);

        // === Right Panel: Announcement Text Field ===
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        JLabel announcementLabel = new JLabel("Write Announcement:", JLabel.CENTER);
        announcementLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JTextArea announcementField = new JTextArea();
        announcementField.setFont(new Font("Arial", Font.PLAIN, 14));
        announcementField.setRows(6); // Approximate height (6 rows)
        announcementField.setMargin(new Insets(10, 10, 10, 10)); // Add padding inside the text area
        announcementField.setLineWrap(true); // Automatically wrap text
        announcementField.setWrapStyleWord(true); // Wrap by words
        JScrollPane scrollPane = new JScrollPane(announcementField); // Add scroll for long text

        rightPanel.add(announcementLabel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Add left and right panels to the main panel
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        // === Bottom Panel: Send Button ===
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton sendButton = new JButton("Send Announcement");
        sendButton.setBackground(new Color(0xC0C0C0));
        sendButton.setFont(new Font("Arial", Font.BOLD, 18));
        sendButton.setFocusPainted(false);

        bottomPanel.add(sendButton);

        sendAnnouncementPanel.add(mainPanel, BorderLayout.CENTER);
        sendAnnouncementPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Handle Send Announcement Button Click
        sendButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue(); // Get the currently selected course
            String announcementText = announcementField.getText().trim(); // Get announcement content

            if (selectedCourse == null) {
                JOptionPane.showMessageDialog(
                        sendAnnouncementPanel,
                        "Please select a course.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } else if (announcementText.isEmpty()) {
                JOptionPane.showMessageDialog(
                        sendAnnouncementPanel,
                        "Please enter an announcement.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } else {
                // Append announcement to file
                appendToAnnouncementsFile(selectedCourse, announcementText);

                // Clear the text field after successful announcement
                announcementField.setText("");
            }
        });

        return sendAnnouncementPanel;
    }

    /** Fetch the list of available courses taught by the instructor */
    private String[] fetchInstructorCourses(int instructorID) {
        String dbUrl = "jdbc:mysql://localhost:3306/elms"; // Database URL
        String dbUser = "root"; // Database username
        String dbPassword = "1234"; // Database password

        String query = "SELECT CourseName FROM Course WHERE Instructor_ID = ?";
        List<String> courses = new ArrayList<>(); // List to store fetched courses

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the instructorID parameter in the query
            stmt.setInt(1, instructorID);

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // Process the results
            while (rs.next()) {
                String courseName = rs.getString("CourseName");
                System.out.println("Course found: " + courseName); // Debug log
                courses.add(courseName);
            }

            // Check if no courses were found
            if (courses.isEmpty()) {
                System.out.println("No courses found for Instructor_ID: " + instructorID);
            }

        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Error retrieving instructor courses: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return courses.toArray(new String[0]); // Convert list to array and return
    }

    /** Append a new announcement to announcements.txt in the specified format */
    private void appendToAnnouncementsFile(String courseName, String message) {
        // Define the path to the file
        String filePath = "src/announcements.txt"; // Adjust based on your project directory

        // Use try-with-resources to ensure the writer is properly closed
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) { // 'true' enables append mode
            // Append the announcement in the specified format
            writer.newLine();
            writer.write(courseName + "|" + message);
            JOptionPane.showMessageDialog(
                    null,
                    "Announcement sent successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException e) {
            // Handle any I/O exceptions
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred while writing the announcement to the file: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JPanel createCourseListPanel() {
        // Create a panel with BorderLayout
        JPanel courseListPanel = new JPanel();
        courseListPanel.setLayout(new BorderLayout());

        // Title Label
        JLabel titleLabel = new JLabel("Your Courses", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30)); // Large title font
        courseListPanel.add(titleLabel, BorderLayout.NORTH);

        // Fetch the instructor's courses dynamically
        String[] courseNames = fetchInstructorCourses(userID);

        // If there are no courses, show a simple placeholder
        if (courseNames == null || courseNames.length == 0) {
            JPanel noCoursesPanel = new JPanel();
            noCoursesPanel.setLayout(new BorderLayout());
            JLabel noCoursesLabel = new JLabel("No courses found.", JLabel.CENTER);
            noCoursesLabel.setFont(new Font("Arial", Font.BOLD, 20));
            courseListPanel.add(noCoursesLabel, BorderLayout.CENTER);
            return courseListPanel;
        }

        // Create a DefaultListModel and populate it with the course names
        DefaultListModel<String> courseListModel = new DefaultListModel<>();
        for (String course : courseNames) {
            courseListModel.addElement(course); // Add each course to the model
        }

        // Create a JList to display the courses
        JList<String> courseList = new JList<>(courseListModel);
        courseList.setFont(new Font("Arial", Font.PLAIN, 16));
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Single course selection
        courseList.setVisibleRowCount(10); // Show up to 10 courses without scrolling
        courseList.setFixedCellHeight(40); // Add more vertical space for readability

        // Add a scroll pane to the course list in case there are many courses
        JScrollPane courseScrollPane = new JScrollPane(courseList);
        courseScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add padding around the scroll pane
        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BorderLayout());
        listContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        listContainer.add(courseScrollPane, BorderLayout.CENTER);

        // Add the course list with scroll pane to the center of the panel
        courseListPanel.add(listContainer, BorderLayout.CENTER);

        // Handle selection behavior (optional)
        courseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedCourse = courseList.getSelectedValue();
                System.out.println("Selected course: " + selectedCourse);
                // Further logic for selected course can be added here
            }
        });

        return courseListPanel;
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

    private JPanel createStudentListPanel() {
        // Create the main panel
        JPanel studentListPanel = new JPanel();
        studentListPanel.setLayout(new BorderLayout());

        // Title Label
        JLabel titleLabel = new JLabel("Students Taught", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        studentListPanel.add(titleLabel, BorderLayout.NORTH);

        // Fetch the list of students dynamically based on the instructor's courses
        String[][] students = fetchStudentsForInstructor(userID);

        // Check if no students are found
        if (students == null || students.length == 0) {
            JLabel noStudentsLabel = new JLabel("No students found.", JLabel.CENTER);
            noStudentsLabel.setFont(new Font("Arial", Font.BOLD, 20));
            studentListPanel.add(noStudentsLabel, BorderLayout.CENTER);
            return studentListPanel;
        }

        // Column names for the table
        String[] columnNames = { "Student ID", "Name", "Email", "Course Name" };

        // Create the JTable to display student data
        JTable studentTable = new JTable(students, columnNames);
        studentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        studentTable.setRowHeight(30); // Set row height for readability
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16)); // Header font
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Enable single row selection

        // Add the table to a scroll pane (in case of many rows)
        JScrollPane tableScrollPane = new JScrollPane(studentTable);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add padding around the table
        JPanel tableContainer = new JPanel();
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tableContainer.add(tableScrollPane, BorderLayout.CENTER);

        // Add the table container to the main panel
        studentListPanel.add(tableContainer, BorderLayout.CENTER);

        return studentListPanel;
    }

    private String[][] fetchStudentsForInstructor(int instructorID) {
        String dbUrl = "jdbc:mysql://localhost:3306/elms"; // Database URL
        String dbUser = "root"; // Database username
        String dbPassword = "1234"; // Database password

        String query =
                "SELECT s.User_ID AS Student_ID, " +
                        "       u.Full_Name AS Name, " +
                        "       u.Email AS Email, " +
                        "       c.CourseName AS CourseName " +
                        "FROM Student s " +
                        "INNER JOIN Users u ON s.User_ID = u.User_ID " +
                        "INNER JOIN Enrollment e ON s.User_ID = e.Student_ID " +
                        "INNER JOIN Course c ON e.Course_ID = c.Course_ID " +
                        "WHERE c.Instructor_ID = ?";

        List<String[]> studentData = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the instructor ID parameter
            stmt.setInt(1, instructorID);

            // Execute query
            ResultSet rs = stmt.executeQuery();

            // Process the result set
            while (rs.next()) {
                String studentID = rs.getString("Student_ID");
                String name = rs.getString("Name");
                String email = rs.getString("Email");
                String courseName = rs.getString("CourseName");

                // Debug output to validate data
                System.out.println("Fetched Student: " + studentID + ", " + name + ", " + email + ", " + courseName);

                studentData.add(new String[]{studentID, name, email, courseName});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Error fetching student data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        // Convert List to 2D array
        return studentData.toArray(new String[0][0]);
    }

    private JPanel createAssignmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel title with header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Student Submissions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 60, 60));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> refreshSubmissionsPanel());
        headerPanel.add(refreshButton, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Table with submissions data
        String[] columnNames = {"Submission ID", "Student", "Course", "Assignment", "Submission Date", "Status", "Grade", "Actions"};
        Object[][] data = fetchStudentSubmissions();

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make Grade and Actions columns editable
                return column == 6 || column == 7;
            }
        };

        JTable submissionsTable = new JTable(model);
        submissionsTable.setRowHeight(35);
        submissionsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        submissionsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        submissionsTable.setSelectionBackground(new Color(230, 240, 250));
        submissionsTable.setGridColor(new Color(240, 240, 240));
        submissionsTable.setShowVerticalLines(true);

        // Center-align content in all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < submissionsTable.getColumnCount() - 1; i++) {
            submissionsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Add button renderer and editor for Actions column
        submissionsTable.getColumnModel().getColumn(7).setCellRenderer(new DownloadButtonRenderer());
        submissionsTable.getColumnModel().getColumn(7).setCellEditor(new DownloadButtonEditor(submissionsTable, this));

        // Make Grade column editable for grading submissions
        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 6) {
                int row = e.getFirstRow();
                if (row >= 0) {
                    try {
                        int submissionId = (int) model.getValueAt(row, 0);
                        String gradeStr = (String) model.getValueAt(row, 6);
                        if (!gradeStr.equals("Not Graded")) {
                            double grade = Double.parseDouble(gradeStr);
                            if (grade >= 0 && grade <= 100) {
                                updateSubmissionGrade(submissionId, grade);
                                model.setValueAt("Graded", row, 5); // Update status column
                            } else {
                                JOptionPane.showMessageDialog(this,
                                        "Grade must be between 0 and 100",
                                        "Invalid Grade", JOptionPane.ERROR_MESSAGE);
                                refreshSubmissionsPanel();
                            }
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Please enter a valid number for grade",
                                "Invalid Grade", JOptionPane.ERROR_MESSAGE);
                        refreshSubmissionsPanel();
                    }
                }
            }
        });

        // Store table reference for use in other methods
        this.submissionsTable = submissionsTable;

        JScrollPane scrollPane = new JScrollPane(submissionsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add instructions for grading
        JPanel instructionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        instructionsPanel.setBackground(Color.WHITE);
        JLabel instructionsLabel = new JLabel("To grade a submission, edit the value in the Grade column (0-100)");
        instructionsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        instructionsPanel.add(instructionsLabel);
        panel.add(instructionsPanel, BorderLayout.SOUTH);

        return panel;
    }

    /** Update submission grade in the database */
    private void updateSubmissionGrade(int submissionId, double grade) {
        String query = "UPDATE Submission SET Grade = ?, Submission_Status = 'Graded' WHERE Submission_ID = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, grade);
            statement.setInt(2, submissionId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                // Update successful
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update grade. Submission ID not found.",
                        "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error updating grade: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /** Refresh the submissions panel with updated data */
    private void refreshSubmissionsPanel() {
        DefaultTableModel model = (DefaultTableModel) submissionsTable.getModel();
        model.setDataVector(fetchStudentSubmissions(), new String[]{"Submission ID", "Student", "Course", "Assignment", "Submission Date", "Status", "Grade", "Actions"});

        // Re-apply custom renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < submissionsTable.getColumnCount() - 1; i++) {
            submissionsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Re-add button renderer and editor for Actions column
        submissionsTable.getColumnModel().getColumn(7).setCellRenderer(new DownloadButtonRenderer());
        submissionsTable.getColumnModel().getColumn(7).setCellEditor(new DownloadButtonEditor(submissionsTable, this));
    }

    /** Fetch student submissions for display in the table */
    private Object[][] fetchStudentSubmissions() {
        ArrayList<Object[]> submissionsList = new ArrayList<>();

        String query =
                "SELECT s.Submission_ID, u.Full_Name, c.CourseName, a.Assignment_Name, " +
                        "s.Submission_Date, s.Submission_Status, s.Grade, s.FilePath " +
                        "FROM Submission s " +
                        "JOIN Assignment a ON s.Assignment_ID = a.Assignment_ID " +
                        "JOIN Course c ON a.Course_ID = c.Course_ID " +
                        "JOIN Student st ON s.Student_ID = st.User_ID " +
                        "JOIN Users u ON st.User_ID = u.User_ID " +
                        "WHERE c.Instructor_ID = ? " +
                        "ORDER BY s.Submission_Date DESC";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userID);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Format grade if it exists
                    Object grade = resultSet.getObject("Grade");
                    String gradeStr = grade == null ? "Not Graded" : String.format("%.1f", resultSet.getDouble("Grade"));

                    submissionsList.add(new Object[] {
                            resultSet.getInt("Submission_ID"),
                            resultSet.getString("Full_Name"),
                            resultSet.getString("CourseName"),
                            resultSet.getString("Assignment_Name"),
                            resultSet.getDate("Submission_Date"),
                            resultSet.getString("Submission_Status"),
                            gradeStr,
                            "Download"  // Placeholder for button
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error fetching submissions: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return submissionsList.toArray(new Object[0][0]);
    }

    /** Get the file path for a submission */
    public String getSubmissionFilePath(int row) {
        String query = "SELECT FilePath FROM Submission WHERE Submission_ID = ?";
        String filePath = null;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Ensure this retrieves the correct Submission_ID
            int submissionId = (int) submissionsTable.getValueAt(row, 0);
            statement.setInt(1, submissionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    filePath = resultSet.getString("FilePath"); // Retrieve the file path
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error retrieving file path: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return filePath; // Returns the file path or null if not found
    }

    /** Create a panel for instructors to upload assignments */
    private JPanel createCreateAssignmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel title with header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Create New Assignment");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 60, 60));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Course selection
        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(courseLabel, gbc);

        String[] courses = fetchInstructorCourses(userID);
        JComboBox<String> courseComboBox = new JComboBox<>(courses);
        courseComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(courseComboBox, gbc);

        // Assignment name
        JLabel nameLabel = new JLabel("Assignment Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(nameField, gbc);

        // Due date
        JLabel dueDateLabel = new JLabel("Due Date (YYYY-MM-DD):");
        dueDateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(dueDateLabel, gbc);

        JTextField dueDateField = new JTextField(10);
        dueDateField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(dueDateField, gbc);

        // Description
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(descriptionLabel, gbc);

        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(descriptionScrollPane, gbc);

        // File upload button
        JLabel fileLabel = new JLabel("Assignment File:");
        fileLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(fileLabel, gbc);

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.setBackground(Color.WHITE);

        JLabel selectedFileLabel = new JLabel("No file selected");
        selectedFileLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        JButton browseButton = new JButton("Browse...");
        browseButton.setFont(new Font("Arial", Font.BOLD, 14));
        browseButton.setBackground(new Color(70, 130, 180));
        browseButton.setForeground(Color.WHITE);

        // File reference to store the selected file
        final File[] selectedFile = new File[1];

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Assignment File");
            int result = fileChooser.showOpenDialog(panel);

            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
                selectedFileLabel.setText(selectedFile[0].getName());
            }
        });

        filePanel.add(browseButton);
        filePanel.add(selectedFileLabel);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(filePanel, gbc);

        // Submit button
        JButton submitButton = new JButton("Create Assignment");
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitButton.setBackground(new Color(70, 130, 180));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> {
            // Validate input
            String courseName = (String) courseComboBox.getSelectedItem();
            String assignmentName = nameField.getText().trim();
            String dueDate = dueDateField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (courseName == null || courseName.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Please select a course.",
                        "Missing Information", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (assignmentName.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Please enter an assignment name.",
                        "Missing Information", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dueDate.isEmpty() || !dueDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(panel,
                        "Please enter a valid due date in the format YYYY-MM-DD.",
                        "Invalid Date Format", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Please enter an assignment description.",
                        "Missing Information", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedFile[0] == null) {
                JOptionPane.showMessageDialog(panel,
                        "Please select a file to upload.",
                        "Missing File", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create the assignment and upload the file
            boolean success = createAssignment(courseName, assignmentName, dueDate, description, selectedFile[0]);

            if (success) {
                // Clear the form
                nameField.setText("");
                dueDateField.setText("");
                descriptionArea.setText("");
                selectedFile[0] = null;
                selectedFileLabel.setText("No file selected");

                JOptionPane.showMessageDialog(panel,
                        "Assignment created successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(submitButton, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Instructions panel
        JPanel instructionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        instructionsPanel.setBackground(Color.WHITE);
        JLabel instructionsLabel = new JLabel("Create new assignments for your students to complete.");
        instructionsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        instructionsPanel.add(instructionsLabel);
        panel.add(instructionsPanel, BorderLayout.SOUTH);

        return panel;
    }

    /** Create a new assignment and upload the associated file */
    private boolean createAssignment(String courseName, String assignmentName, String dueDate,
                                     String description, File file) {
        // First get the course ID from the course name
        int courseId = getCourseIdByName(courseName);

        if (courseId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Error: Could not find course ID for " + courseName,
                    "Course Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Insert the assignment in the database
        String insertQuery = "INSERT INTO Assignment (Course_ID, Assignment_Name, Description, Due_Date) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, courseId);
            statement.setString(2, assignmentName);
            statement.setString(3, description);
            statement.setString(4, dueDate);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                // Get the generated assignment ID
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int assignmentId = generatedKeys.getInt(1);

                        // Now upload the file
                        return uploadAssignmentFile(assignmentId, file);
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error creating assignment: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return false;
    }

    /** Get the course ID from the course name */
    private int getCourseIdByName(String courseName) {
        String query = "SELECT Course_ID FROM Course WHERE CourseName = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, courseName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("Course_ID");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error retrieving course ID: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return -1;
    }

    /** Upload the assignment file to the database */
    private boolean uploadAssignmentFile(int assignmentId, File file) {
        // Create directory structure if it doesn't exist
        File assignmentsDir = new File("assignments/");
        if (!assignmentsDir.exists()) {
            assignmentsDir.mkdir();
        }

        // Create a unique filename to store the uploaded file
        String targetFilename = "assignment_" + assignmentId + "_" + file.getName();
        File targetFile = new File("assignments/" + targetFilename);

        try {
            // Copy the file
            copyFile(file, targetFile);

            // Update the assignment to include the file path
            String updateQuery = "UPDATE Assignment SET FilePath = ? WHERE Assignment_ID = ?";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(updateQuery)) {

                statement.setString(1, targetFilename);
                statement.setInt(2, assignmentId);

                int rowsUpdated = statement.executeUpdate();

                return rowsUpdated > 0;
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error uploading file: " + e.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error updating assignment with file path: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return false;
    }




    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(new Color(60, 60, 60));

        panel.add(label);
        return panel;
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

    /** Database connection setup */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/elms", "root", "1234"); // Update credentials as needed.
    }
    /** Download the submission file */
    public void downloadSubmission(String filePath, int row) {
        if (filePath == null || filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "File path not found for this submission.",
                    "File Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File sourceFile = new File("submissions/" + filePath);

        if (!sourceFile.exists()) {
            JOptionPane.showMessageDialog(this,
                    "File not found on server: " + filePath,
                    "File Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Extract original filename from the stored path (remove userID_assignmentID_ prefix)
        String originalFileName = filePath.substring(filePath.indexOf('_', filePath.indexOf('_') + 1) + 1);

        // Create a file chooser for the destination
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Submission File");
        fileChooser.setSelectedFile(new File(originalFileName));

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File destFile = fileChooser.getSelectedFile();

            try {
                copyFile(sourceFile, destFile);
                JOptionPane.showMessageDialog(this,
                        "File downloaded successfully!",
                        "Download Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error downloading file: " + e.getMessage(),
                        "Download Error", JOptionPane.ERROR_MESSAGE);
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

    // Custom button renderer for the Actions column
    class DownloadButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton downloadButton;

        public DownloadButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            downloadButton = new JButton("Download");
            downloadButton.setFont(new Font("Arial", Font.PLAIN, 12));
            downloadButton.setBackground(new Color(46, 139, 87));
            downloadButton.setForeground(Color.WHITE);
            downloadButton.setFocusPainted(false);
            downloadButton.setBorderPainted(true);
            add(downloadButton);
            setBackground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    // Custom button editor for the Actions column
    class DownloadButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private InstructorMenu instructorMenu;

        public DownloadButtonEditor(JTable table, InstructorMenu instructorMenu) {
            super(new JTextField());
            this.table = table;
            this.instructorMenu = instructorMenu;

            button = new JButton();
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setBackground(new Color(46, 139, 87));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(true);

            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = "Download";
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int row = table.convertRowIndexToModel(table.getEditingRow());
                String filePath = instructorMenu.getSubmissionFilePath(row);
                instructorMenu.downloadSubmission(filePath, row);
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

    // Main method for testing
    public static void main(String[] args) {
        JFrame frame = new JFrame("Instructor Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Test with a sample user ID, e.g., 1
        int testUserID = 2;

        InstructorMenu instructorMenu = new InstructorMenu(testUserID);
        frame.add(instructorMenu);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen.
        frame.setVisible(true);
    }

}
