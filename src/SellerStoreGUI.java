import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * SellerStoreGUI
 * <p>
 * GUI for sellers to manage individual store and its products
 *
 * @author Yash Ashtekar
 * @version 17/05/23
 */

public class SellerStoreGUI extends JComponent implements Runnable {
    public SellerStoreGUI(Store store, PrintStream ps) {
        this.store = store;
        this.ps = ps;
    }
    Store store;
    PrintStream ps;
    JFrame frame;

    //Top Panel
    JLabel titleLabel;

    //Left Panel
    JButton addProductButton;

    JTextField importField;
    JButton importButton;

    JTextField exportField;
    JButton exportButton;

    //Right Panel
    JButton customerStatButton;
    JButton backButton;

    //Center Panel
    JLabel instructionLabel;

    //Bottom Panel
    JButton saleHistoryButton;
    JButton storeDelButton;

    @Override
    public void run() {
        frame = new JFrame();
        frame.setTitle("The Marketplace: " + store.getStoreName());

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        //Top Panel
        JPanel topPanel = new JPanel();

        titleLabel = new JLabel("Manage " + store.getStoreName());

        topPanel.add(titleLabel);
        content.add(topPanel, BorderLayout.NORTH);

        //Left Panel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        addProductButton = new JButton("Add Product");

        importField = new JTextField(10);
        importField.setText("Enter Filename");
        importButton = new JButton("Import Product");

        exportField = new JTextField(10);
        exportField.setText("Enter Filename");
        exportButton = new JButton("Export Products");

        leftPanel.add(addProductButton);
        leftPanel.add(importField);
        leftPanel.add(importButton);
        leftPanel.add(exportField);
        leftPanel.add(exportButton);
        content.add(leftPanel, BorderLayout.WEST);

        ActionListener leftListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == addProductButton) {
                    ps.println(store.getStoreName() + "\ngoToCreateProduct");
                    frame.dispose();
                } else if (e.getSource() == importButton) {
                    if (importField.getText().indexOf("\\") == -1) {
                        ps.println(importField.getText() + "\n" + store.getStoreName() + "\nimportProducts");
                    } else {
                        JOptionPane.showMessageDialog(null, "Filename may not contain escape characters.",
                                "The Marketplace", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else if (e.getSource() == exportButton) {
                    if (exportField.getText().indexOf("\\") == -1) {
                        ps.println(exportField.getText() + "\n" + store.getStoreName() + "\nexportProducts");
                    } else {
                        JOptionPane.showMessageDialog(null, "Filename may not contain escape characters.",
                                "The Marketplace", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        };

        addProductButton.addActionListener(leftListener);
        importButton.addActionListener(leftListener);
        exportButton.addActionListener(leftListener);

        //Right Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));

        customerStatButton = new JButton("View Statistics Dashboard");
        rightPanel.add(customerStatButton);

        customerStatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ps.println(store.getStoreName() + "\ngoToCustomerPuchaseDashboard");
                frame.dispose();
            }
        });


        content.add(rightPanel, BorderLayout.EAST);

        //Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        instructionLabel = new JLabel("Click \"Manage Product\" to edit product properties");

        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.PAGE_AXIS));

        if (store.getProducts().size() == 0) {
            scrollPanel.add(new JLabel("No products yet. Create one by using Add Product."));
        } else
            for (Product product : store.getProducts()) {
                JPanel productPanel = new JPanel();
                JLabel productLabel = new JLabel(product.getName());
                JButton productButton = new JButton("Manage Product");

                productPanel.add(productLabel);
                productPanel.add(productButton);
                productButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ps.println(product.getName() + "\n" + product.getStoreName() + "\ngoToManageProduct");
                        frame.dispose();
                    }
                });

                scrollPanel.add(productPanel);
            }

        JScrollPane scrollPane = new JScrollPane(scrollPanel);

        centerPanel.add(instructionLabel);
        centerPanel.add(scrollPane);
        content.add(centerPanel, BorderLayout.CENTER);

        //Bottom Panel
        JPanel bottomPanel = new JPanel();

        saleHistoryButton = new JButton("View Sales History");
        storeDelButton = new JButton("Delete Store");

        bottomPanel.add(saleHistoryButton);
        bottomPanel.add(storeDelButton);

        ActionListener bottomListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == saleHistoryButton) {
                    ps.println(store.getStoreName() + "\ngoToSaleHistory");
                    frame.dispose();
                } else if (e.getSource() == storeDelButton) {
                    int selection = JOptionPane.showConfirmDialog(null, "Are you sure?",
                            "The Marketplace", JOptionPane.YES_NO_OPTION);
                    if (selection == 0) {
                        ps.println(store.getStoreName() + "\ndeleteStore");
                        frame.dispose();
                    }
                }
            }
        };

        saleHistoryButton.addActionListener(bottomListener);
        storeDelButton.addActionListener(bottomListener);

        backButton = new JButton("Back to Stores");
        bottomPanel.add(backButton, BorderLayout.SOUTH);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ps.println("goToSellerHome");
                frame.dispose();
            }
        });

        content.add(bottomPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                ps.println("close");
            }
        });
    }

    public void disposeThis() {
        frame.dispose();
    }

    public static void main(String[] args) {
        ArrayList<Product> testProducts = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            testProducts.add(new Product("Product" + (i + 1), "Store" + (i + 1),
                    "Placeholder description", 10, 9.99, 100, 100.25));
        }
        Store testStore = new Store("Test Store", testProducts, null, 0);
        SwingUtilities.invokeLater(new SellerStoreGUI(testStore, new PrintStream(new ByteArrayOutputStream())));
    }
}