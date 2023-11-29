import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * EditProductGUI
 *
 * GUI for sellers to edit a product in their store
 *
 * @author Yash Ashtekar
 * @version 13/05/23
 */
public class EditProductGUI extends JComponent implements Runnable {
    private PrintStream ps;
    private JTextField priceField;
    private JTextField quantityField;
    private JTextField descriptionField;

    JFrame frame;

    private ArrayList<String> details;
    //Name, Storename, Price, Desc, Quant
    private String storeName;

    private JButton editProductButton;
    private JButton cancelButton;
    private JButton deleteButton;
    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == editProductButton) {
                //ProductName, StoreName, Price, Quant, Desc

                if (priceField.getText().isEmpty() || quantityField.getText().isEmpty() ||
                        descriptionField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields!", "The Marketplace",
                            JOptionPane.ERROR_MESSAGE);
                } else if (MarketplaceClient.invalidString(descriptionField.getText())) {

                    JOptionPane.showMessageDialog(null, "Fields cannot contain commas nor special characters.",
                            "The Marketplace", JOptionPane.INFORMATION_MESSAGE);

                } else {
                    try {
                        double price = Double.parseDouble(priceField.getText());
                        int quant = Integer.parseInt(quantityField.getText());

                        if (price < 0 || quant < 0) {
                            JOptionPane.showMessageDialog(null, "Price and quantity must be positive!",
                                    "The Marketplace", JOptionPane.ERROR_MESSAGE);
                        } else {

                            ps.println(details.get(0) + "\n" + details.get(1) + "\n" +
                                    priceField.getText() + "\n" + quantityField.getText() +
                                    "\n" + descriptionField.getText() + "\nupdateProduct");
                            frame.dispose();

                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Quantity and Price must be numbers!", "The Marketplace",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

            } else if (e.getSource() == cancelButton) {
                ps.println(storeName + "\ngoToStoreInfo");
                frame.dispose();
            } else if (e.getSource() == deleteButton) {

                int selection = JOptionPane.showConfirmDialog(null, "Are you sure?",
                        "The Marketplace", JOptionPane.YES_NO_OPTION);

                if (selection == 0) {
                    ps.println(details.get(0) + "\n" + storeName + "\ndeleteProduct");

                    ps.println(storeName + "\ngoToStoreInfo");

                    frame.dispose();
                }
            }
        }
    };

    public EditProductGUI(ArrayList<String> details, PrintStream outputStream) {
        this.ps = outputStream;
        this.details = details;
        this.storeName  = details.get(1);
    }

    public void run() {
        frame = new JFrame();
        frame.setTitle("The Marketplace");

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        //Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        priceField = new JTextField("", 30);
        quantityField = new JTextField("", 30);
        descriptionField = new JTextField("", 30);

        centerPanel.add(new JLabel("Price"));
        centerPanel.add(priceField);
        priceField.setText(details.get(2));
        centerPanel.add(new JLabel("Quantity"));
        centerPanel.add(quantityField);
        quantityField.setText(details.get(4));
        centerPanel.add(new JLabel("Description"));
        centerPanel.add(descriptionField);
        descriptionField.setText(details.get(3));

        content.add(centerPanel, BorderLayout.CENTER);

        deleteButton = new JButton("Delete this Product");

        //Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cancelButton = new JButton("Cancel");
        editProductButton = new JButton("Confirm edits to Product");

        bottomPanel.add(cancelButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(editProductButton);

        cancelButton.addActionListener(actionListener);
        deleteButton.addActionListener(actionListener);
        editProductButton.addActionListener(actionListener);

        content.add(bottomPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                ps.println("close");
            }
        });
    }

    public static void main(String[] args) {
        PrintStream testStream = new PrintStream(new ByteArrayOutputStream());
        SwingUtilities.invokeLater(new EditProductGUI(new ArrayList<String>(List.of("Test", "storeTest", "3.55",
                "Test Description", "4")),
                testStream));
    }

}