import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * SellerStoreListGUI
 * <p>
 * GUI for sellers to view metrics regarding # purchases by each customer
 * and # sales for each product (Slide 15)
 *
 * @author Yash Ashtekar
 * @version 17/05/23
 */

@SuppressWarnings("unchecked")
public class SellerStoreStatisticsGUI extends JComponent implements Runnable {
    public SellerStoreStatisticsGUI(String storeName, ArrayList<String> customerNames,
                                    ArrayList<String> customerPurchases,
                                    ArrayList<String> productNames, ArrayList<String> productSales,
                                    PrintStream outputStream) {
        this.customerNames = customerNames;
        this.customerPurchases = customerPurchases;
        this.productNames = productNames;
        this.productSales = productSales;
        ps = outputStream;
        this.storeName = storeName;
    }
    private PrintStream ps;
    ArrayList<String> customerNames;
    ArrayList<String> customerPurchases;

    ArrayList<String> productNames;
    ArrayList<String> productSales;
    private String storeName;

    JFrame frame;

    //Top Panel
    JLabel titleLabel;

    JComboBox<String> sortCustomerDropdown;
    ActionListener customerSortListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox<String> parentCb = (JComboBox<String>) e.getSource(); //Initializes parent dropdown
            int sortOption = parentCb.getSelectedIndex(); //1 hightolow 2 lowtohigh

            if (sortOption != 0) { //If clicked on Not the title
                ps.println(storeName + "\n" + (sortOption - 1) + "\nsortSellerCD"); //sortOption 0 highLow ; 1 lowHigh
                frame.dispose();
            }
        }
    };


    JComboBox<String> sortProductDropdown;
    ActionListener productSortListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox<String> parentCb = (JComboBox<String>) e.getSource(); //Initializes parent dropdown
            int sortOption = parentCb.getSelectedIndex(); //1 hightolow 2 lowtohigh

            if (sortOption != 0) { //If clicked on Not the title
                ps.println(storeName + "\n" + (sortOption - 1) + "\nsortSellerPD"); //sortOption 0 highLow ; 1 lowHigh
                frame.dispose();
            }
        }
    };

    //Bottom Panel
    JButton backButton;

    @Override
    public void run() {
        frame = new JFrame();
        frame.setTitle("The Marketplace");

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        //Top Panel
        JPanel topPanel = new JPanel();

        titleLabel = new JLabel("  Store Statistics Dashboard for " + storeName + "  ");

        String[] sortCustomerOptions = {"Customer purchases", "# of purchases(High to Low)",
                "# of purchases(Low to High)"};
        sortCustomerDropdown = new JComboBox<String>(sortCustomerOptions);
        sortCustomerDropdown.addActionListener(customerSortListener);

        String[] sortProductOptions = {"Products sold", "# of sales(High to Low)", "# of sales(Low to High)"};
        sortProductDropdown = new JComboBox<String>(sortProductOptions);
        sortProductDropdown.addActionListener(productSortListener);

        topPanel.add(sortCustomerDropdown);
        topPanel.add(titleLabel);
        topPanel.add(sortProductDropdown);
        content.add(topPanel, BorderLayout.NORTH);

        //Customer List Panel
        JPanel customerListPanel = new JPanel();
        customerListPanel.setLayout(new BoxLayout(customerListPanel, BoxLayout.Y_AXIS));

        if (customerNames.size() == 0) {
            customerListPanel.add(new JLabel("This store has not had any customers yet."));
        } else
            for (int i = 0; i < customerNames.size(); i++) {
                JPanel customerPanel = new JPanel();
                JLabel customerLabel = new JLabel(customerNames.get(i) + ": " + customerPurchases.get(i));

                customerPanel.add(customerLabel);

                customerListPanel.add(customerPanel);
            }

        JScrollPane customerScrollPane = new JScrollPane(customerListPanel);

        //Product List Panel
        JPanel productListPanel = new JPanel();
        productListPanel.setLayout(new BoxLayout(productListPanel, BoxLayout.Y_AXIS));

        if (productNames.size() == 0) {
            productListPanel.add(new JLabel("This store has no products."));
        } else
            for (int i = 0; i < productNames.size(); i++) {
                JPanel productPanel = new JPanel();
                JLabel productLabel = new JLabel(productNames.get(i) + ": " + productSales.get(i));

                productPanel.add(productLabel);

                productListPanel.add(productPanel);
            }

        JScrollPane productScrollPane = new JScrollPane(productListPanel);

        //Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout());
        centerPanel.add(customerScrollPane);
        centerPanel.add(productScrollPane);

        content.add(centerPanel, BorderLayout.CENTER);

        //Bottom Panel
        JPanel bottomPanel = new JPanel();

        backButton = new JButton("Back");

        bottomPanel.add(backButton);
        content.add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ps.println(storeName + "\ngoToStoreInfo");
                frame.dispose();
            }
        });

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

    public static void main(String[] args) {
        ArrayList<String> testNames = new ArrayList<>();
        ArrayList<String> testPurchases = new ArrayList<>();
        ArrayList<String> testProductNames = new ArrayList<>();
        ArrayList<String> testProductSales = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            testNames.add("Customer " + (i + 1));
            testPurchases.add("100");
            testProductNames.add("Product " + (i + 1));
            testProductSales.add("100");
        }
        SwingUtilities.invokeLater(new SellerStoreStatisticsGUI("test", testNames, testPurchases,
                testProductNames, testProductSales, new PrintStream(new ByteArrayOutputStream())));
    }
}