import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * CustomerCartInfoGUI
 *
 * GUI for sellers to see which of their products are in customer carts
 *
 * @author Yash Ashtekar
 * @version 12/05/23
 */
public class CustomerCartInfoGUI extends JFrame implements Runnable {

    private PrintStream ps;
    private JButton backButton;
    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == backButton) {
                ps.println("goToSellerHome");
                frame.dispose();
            }
        }
    };

    ArrayList<Product> products;
    int total;

    JFrame frame;

    public CustomerCartInfoGUI(ArrayList<Product> products, int total, PrintStream outputStream) {
        this.ps = outputStream;
        this.products = products;
        this.total = total;
    }

    public void run() {
        frame = new JFrame();
        frame.setTitle("The Marketplace");

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        //Top panel
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Number of products currently in customer carts: "));
        JTextField number = new JTextField(String.valueOf(total));
        number.setEditable(false);
        number.setFont(new Font(number.getFont().getName(), Font.BOLD, 22));
        topPanel.add(number);
        content.add(topPanel, BorderLayout.NORTH);

        //Bottom Panel
        JPanel bottomPanel = new JPanel();
        backButton = new JButton("Back");
        bottomPanel.add(backButton, BorderLayout.WEST);
        backButton.addActionListener(actionListener);
        content.add(bottomPanel, BorderLayout.SOUTH);

        //Center Panel
        JPanel centerPanel = new JPanel();
        //Products in Carts panel
        JPanel productsInCartPanel = new JPanel();
        productsInCartPanel.setLayout(new BoxLayout(productsInCartPanel, BoxLayout.Y_AXIS));

        if (total == 0) {
            productsInCartPanel.add(new JLabel("No products in carts yet."));
        } else {
            for (Product p : products) {
                JPanel productPanel = new JPanel();
                JPanel productPanel2 = new JPanel();

                JLabel productLabel = new JLabel("Name: " + p.getName() + "; Store: " +
                        p.getStoreName() + "; Description: " + p.getDesc());
                JLabel productLabel2 = new JLabel("Amount currently in customer carts: " + p.getQuantity());

                productPanel.add(productLabel);
                productPanel2.add(productLabel2);

                productsInCartPanel.add(productPanel);
                productsInCartPanel.add(productPanel2);
                productsInCartPanel.add(new JSeparator());
            }
        }

        JScrollPane inCartScrollPane = new JScrollPane(productsInCartPanel);

        centerPanel.setLayout(new GridLayout());
        centerPanel.add(inCartScrollPane);
        content.add(centerPanel, BorderLayout.CENTER);

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
        ArrayList<Product> testProducts = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            testProducts.add(new Product("Product " + i, "Test Store",
                    "Test Description. This product is great.", 10, 9.99, 3, 5.43));
        }

        int testInt = 100;
        PrintStream testStream = new PrintStream(new ByteArrayOutputStream());
        SwingUtilities.invokeLater(new CustomerCartInfoGUI(testProducts, testInt, testStream));
    }
}