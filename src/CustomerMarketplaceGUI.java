import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * CustomerMarketplaceGUI
 *
 * GUI for customers to search and purchase products on marketplace
 *
 * @author Yash Ashtekar
 * @version 12/05/23
 */

@SuppressWarnings("unchecked")
public class CustomerMarketplaceGUI extends JComponent implements Runnable {
    public CustomerMarketplaceGUI(ArrayList<Product> products, PrintStream ps) {
        this.products = products;
        this.ps = ps;
    }

    private ArrayList<Product> products;
    JFrame frame;

    PrintStream ps;

    //Top Panel Elements
    JTextField searchField;
    JButton searchButton;
    JLabel welcomeLabel;
    JButton profileButton;
    JComboBox<String> sortDropdown;
    ActionListener sortListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox<String> parentCb = (JComboBox<String>) e.getSource(); //Initializes parent dropdown
            int sortOption = parentCb.getSelectedIndex();

            if (sortOption != 0) {
                ps.println((sortOption - 1) + "\nsortProducts");
                frame.dispose();
            }
        }
    };


    //Bottom Panel Elements
    JButton dashboardButton;
    JButton allProductsButton;
    JButton cartButton;
    JButton historyButton;

    ActionListener bottomListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == cartButton) {
                ps.println("goToCart");
                frame.dispose();
            } else if (e.getSource() == dashboardButton) {
                ps.println("goToStoreDashboard");
                frame.dispose();
            } else if (e.getSource() == allProductsButton) {
                ps.println("goToCustomerHome");
                frame.dispose();
            } else if (e.getSource() == historyButton) {
                ps.println("goToPurchaseHistory");
                frame.dispose();
            }

        }
    };

    @Override
    public void run() {
        frame = new JFrame();
        frame.setTitle("The Marketplace");

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        //Top Panel
        JPanel topPanel = new JPanel();

        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (searchField.getText().indexOf("\\") == -1) {
                    ps.println(searchField.getText() + "\nsearchProducts");
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Search field cannot contain escape characters.",
                            "The Marketplace", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        welcomeLabel = new JLabel("Welcome to the Marketplace");
        profileButton = new JButton("Edit Profile");
        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ps.println("goToProfile");
            }
        });
        String[] sortOptions = {"Sort Products", "Price(Low to High)", "Price(High to Low)", "Quantity(Low to High)",
                "Quantity(High to Low)"};
        sortDropdown = new JComboBox<String>(sortOptions);
        sortDropdown.addActionListener(sortListener);

        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(welcomeLabel);
        topPanel.add(profileButton);
        topPanel.add(sortDropdown);

        content.add(topPanel, BorderLayout.NORTH);

        //Center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));

        for (Product product : products) {
            String priceString = String.format("$%.2f", product.getPrice());
            JPanel productPanel = new JPanel();
            JLabel productLabel = new JLabel(product.getName() + " | " + product.getStoreName() + " | "
                    + priceString);
            JButton productButton = new JButton("View Product");

            productPanel.add(productLabel);
            productPanel.add(productButton);
            productButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (product.getQuantity() > 0) {
                        String[] options = new String[]{"Add to Cart", "Cancel"};
                        int selection = JOptionPane.showOptionDialog(centerPanel, product.getName() + ": " +
                                        product.getDesc() + "\nSold by: " + product.getStoreName() +
                                        "\nQuantity available: " + product.getQuantity() +
                                        "\nPrice: " + priceString, product.getName(),
                                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                null, options, options[0]);
                        if (selection == 0) {
                            ps.println(product.getName() + "\n" + product.getStoreName() + "\naddToCart");
                        }
                    } else {
                        String[] options = new String[]{"Out of Stock", "Cancel"};
                        JOptionPane.showOptionDialog(centerPanel, product.getName() + ": " +
                                        product.getDesc() + "\nSold by: " + product.getStoreName() +
                                        "\nQuantity available: " + product.getQuantity() + "\nPrice: " +
                                        priceString, product.getName(), JOptionPane.DEFAULT_OPTION,
                                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    }
                }
            });

            centerPanel.add(productPanel);
        }

        if (products.size() == 0) {
            JPanel noProductPanel = new JPanel();
            JPanel comeBackPanel = new JPanel();
            noProductPanel.add(new JLabel("No products currently up for sale: The marketplace is closed."));
            comeBackPanel.add(new JLabel("Come back next time!"));
            centerPanel.add(noProductPanel);
            centerPanel.add(comeBackPanel);
        }

        JScrollPane scrollPane = new JScrollPane(centerPanel);

        content.add(scrollPane, BorderLayout.CENTER);

        //Bottom panel
        JPanel bottomPanel = new JPanel();

        dashboardButton = new JButton("View Stores");
        dashboardButton.addActionListener(bottomListener);

        allProductsButton = new JButton("View All Products");
        allProductsButton.addActionListener(bottomListener);

        cartButton = new JButton("Manage Cart");
        cartButton.addActionListener(bottomListener);

        historyButton = new JButton("View Purchase History");
        historyButton.addActionListener(bottomListener);

        bottomPanel.add(dashboardButton);
        bottomPanel.add(allProductsButton);
        bottomPanel.add(cartButton);
        bottomPanel.add(historyButton);

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

    //Test PSVM
    public static void main(String[] args) {
        ArrayList<Product> testProducts = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            testProducts.add(new Product("Product" + (i + 1), "Store" + (i + 1),
                    "Placeholder description", 10, 9.99, 100, 100.25));
        }
        SwingUtilities.invokeLater(new CustomerMarketplaceGUI(testProducts,
                new PrintStream(new ByteArrayOutputStream())));
    }
}