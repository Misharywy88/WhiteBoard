import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class AssignmentUploader {

    boolean uploadAssignment(int assignmentID) {
        // Choose File Dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Assignment PDF to Upload");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            if (file.getName().endsWith(".pdf")) {
                // Try to save PDF to the database and return the result
                return savePDFToDatabase(file, assignmentID);

            } else {
                JOptionPane.showMessageDialog(null,
                        "Please upload a valid PDF file.",
                        "Invalid File",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return false; // Return false if operation was cancelled or invalid file was selected
    }

    private boolean savePDFToDatabase(File file, int assignmentID) {
        String dbUrl = "jdbc:mysql://localhost:3306/elms";
        String dbUser = "root"; // Replace with your database username
        String dbPassword = "1234"; // Replace with your database password

        String sql = "UPDATE Assignment SET File = ? WHERE Assignment_ID = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql);
             FileInputStream fis = new FileInputStream(file)) {

            // Bind the parameters
            stmt.setBinaryStream(1, fis, (int) file.length());
            stmt.setInt(2, assignmentID);

            // Execute update
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Assignment uploaded successfully!");
                return true; // Return true if the upload was successful
            } else {
                JOptionPane.showMessageDialog(null, "Failed to upload the assignment.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving assignment: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false; // Return false if there was an error
    }
}