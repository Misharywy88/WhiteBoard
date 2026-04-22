import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AssignmentDownloader {

    boolean downloadAssignment(int assignmentID) {
        String dbUrl = "jdbc:mysql://localhost:3306/elms";
        String dbUser = "root";
        String dbPassword = "1234";

        String sql = "SELECT File FROM Assignment WHERE Assignment_ID = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, assignmentID); // Set the Assignment ID
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                InputStream inputStream = rs.getBinaryStream("File");

                // Prompt the user for the save location
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Assignment");
                fileChooser.setSelectedFile(new File("assignment_" + assignmentID + ".pdf"));
                int saveResult = fileChooser.showSaveDialog(null);

                if (saveResult == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();

                    // Write the file to the chosen location
                    try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            fos.write(buffer, bytesRead, bytesRead);
                        }
                        JOptionPane.showMessageDialog(null, "Assignment downloaded successfully!");
                    } catch (IOException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error saving the file.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error downloading assignment: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public static void main(String[] args) {
        AssignmentDownloader downloader = new AssignmentDownloader();
        downloader.downloadAssignment(1); // Example: Download file for Assignment_ID = 1
    }
}