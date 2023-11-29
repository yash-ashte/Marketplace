import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * AddProductGUI
 * <p>
 * GUI for sellers to add a new product to their store
 *
 * @author Yash Ashtekar
 * @version 12/05/23
 */
public class AddProductGUI extends JComponent implements Runnable {

    public AddProductGUI(String storeName, PrintStream ps) {
        this.ps = ps;
        this.storeName = storeName;
    }

    private PrintStream ps;
    JFrame frame;

    String storeName;

    private JTextField productNameField;
    private JTextField priceField;
    private JTextField quantityField;
    private JTextField descriptionField;

    private JButton addProductButton;
    private JButton cancelButton;
    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == addProductButton) {
                if (productNameField.getText().isEmpty() || priceField.getText().isEmpty()
                        || quantityField.getText().isEmpty() || descriptionField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields!", "The Marketplace",
                            JOptionPane.ERROR_MESSAGE);
                } else if (MarketplaceClient.invalidString(productNameField.getText()) ||
                        MarketplaceClient.invalidString(descriptionField.getText())) {

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

                            ps.println(productNameField.getText() + "\n" + storeName + "\n" + priceField.getText() +
                                    "\n" + quantityField.getText() + "\n" + descriptionField.getText() +
                                    "\naddProductToStore");
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
            }
        }
    };

    public void run() {
        frame = new JFrame();
        frame.setTitle("The Marketplace");

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        //Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        productNameField = new JTextField("", 30);
        priceField = new JTextField("", 30);
        quantityField = new JTextField("", 30);
        descriptionField = new JTextField("", 30);

        centerPanel.add(new JLabel("Product name"));
        centerPanel.add(productNameField);
        centerPanel.add(new JLabel("Price"));
        centerPanel.add(priceField);
        centerPanel.add(new JLabel("Initial quantity"));
        centerPanel.add(quantityField);
        centerPanel.add(new JLabel("Description"));
        centerPanel.add(descriptionField);

        content.add(centerPanel, BorderLayout.CENTER);

        //Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cancelButton = new JButton("Cancel");
        addProductButton = new JButton("Add Product");

        bottomPanel.add(cancelButton);
        bottomPanel.add(addProductButton);

        cancelButton.addActionListener(actionListener);
        addProductButton.addActionListener(actionListener);

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
        System.out.println("hello");
        PrintStream testStream = new PrintStream(new ByteArrayOutputStream());
        SwingUtilities.invokeLater(new AddProductGUI("test", testStream));
    }

}