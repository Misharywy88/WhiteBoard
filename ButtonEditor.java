import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class ButtonEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer, ActionListener {
    private JButton button; // The button in the cell
    private String label;   // The button label
    private JTable table;   // Reference to the JTable
    private int currentRow; // Row index for the current click
    private int currentColumn; // Column index for the current click

    public ButtonEditor(JCheckBox jCheckBox) {
        button = new JButton();
        button.addActionListener(this); // Add action listener to the button
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        this.currentRow = row;
        this.currentColumn = column;

        // Set the button text (e.g., Download or Upload)
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        return button;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        // Same as editor, but for rendering
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return label; // Return the button label as the cell value
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fireEditingStopped(); // Notify JTable that the editing is complete

        // You can perform the desired action based on column or cell here
        if (table != null && currentColumn == 4) { // Check if this is the upload button column
            int assignmentID = (int) table.getValueAt(currentRow, 0); // Get the Assignment ID for the row
            System.out.println("Uploading Assignment ID: " + assignmentID);

            // Perform upload operation via AssignmentUploader
            AssignmentUploader uploader = new AssignmentUploader();
            boolean success = uploader.uploadAssignment(assignmentID);

            if (success) {
                JOptionPane.showMessageDialog(null, "Your answer has been uploaded!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to upload the answer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}