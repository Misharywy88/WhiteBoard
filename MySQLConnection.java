import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLConnection {

    public static Connection getConnection() {
        // -------------------------------------------------------
        // Database connection details
        // Update these values to match your local MySQL setup
        // -------------------------------------------------------
        String url = "jdbc:mysql://localhost:3306/elms"; // Database name: elms
        String user = "root";                             // MySQL username
        String password = "1234";                         // Change this to your MySQL root password
        // -------------------------------------------------------

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("An error occurred while connecting to the database.");
            e.printStackTrace();
            return null;
        }
    }

    public static String getUserFirstName(int userID) {
        String query = "SELECT Full_Name FROM Users WHERE User_ID = ?";
        Connection connection = getConnection();
        if (connection == null) {
            return null;
        }

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("Full_Name");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while fetching the user name.");
            e.printStackTrace();
        }

        return null;
    }

    public static String getPersonalInfo(int userID) {
        String query = "SELECT Full_Name, Email, PhoneNumber, City, GPA FROM Users WHERE User_ID = ?";
        Connection connection = getConnection();
        if (connection == null) {
            return null;
        }

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String fullName = rs.getString("Full_Name");
                String email = rs.getString("Email");
                String phone = rs.getString("PhoneNumber");
                String city = rs.getString("City");
                double gpa = rs.getDouble("GPA");

                return String.format("""
                        Full Name: %s
                        Email: %s
                        Phone Number: %s
                        City: %s
                        GPA: %.2f
                        """, fullName, email, phone, city, gpa);
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while fetching personal info.");
            e.printStackTrace();
        }

        return "Personal Info not found or inaccessible.";
    }

    public static void main(String[] args) {
        // Example usage for testing
        int userID = 1; // Replace with a valid User_ID during testing
        String personalInfo = getPersonalInfo(userID);
        if (personalInfo != null) {
            System.out.println(personalInfo);
        } else {
            System.out.println("User not found.");
        }
    }
}
