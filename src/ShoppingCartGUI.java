import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * ShoppingCartGUI
 * <p>
 * GUI for customers to manage products in their shopping cart and make purchases
 *
 * @author Yash Ashtekar
 * @version 18/05/23
 */

public class ShoppingCartGUI extends JComponent implements Runnable {
    public ShoppingCartGUI(ArrayList<Product> products, PrintStream ps) {
        this.products = products;
        this.ps = ps;
    }
    private ArrayList<Product> products;
    JFrame frame;

    PrintStream ps;

    //Top panel
    JLabel titleLabel;

    //Bottom Panel
    JButton marketButton;
    JButton purchaseAllButton;
    JButton purchaseHistoryButton;

    ActionListener bottomListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == marketButton) {
                ps.println("goToCustomerHome");
                frame.dispose();
            } else if (e.getSource() == purchaseHistoryButton) {
                ps.println("goToPurchaseHistory");
                frame.dispose();
            } else if (e.getSource() == purchaseAllButton) {

                if (products.size() == 0) {
                    JOptionPane.showMessageDialog(frame, "Cart is empty.",
                            "Now why would that work?", JOptionPane.INFORMATION_MESSAGE);
                } else
                    ps.println("purchaseCart");
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

        titleLabel = new JLabel("Shopping Cart");

        topPanel.add(titleLabel);

        content.add(topPanel, BorderLayout.NORTH);

        //Center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));

        if (products.size() == 0) {
            centerPanel.add(new JLabel("Shopping Cart is Empty!"));
        } else
            for (Product product : products) {
                String priceString = String.format("%.2f", product.getPrice());
                JPanel productPanel = new JPanel();
                JLabel productLabel = new JLabel(product.getName() + " | " + product.getQuantity() +
                        " | " + product.getStoreName() + " | " + priceString);
                JButton productButton = new JButton("Remove Product");

                productPanel.add(productLabel);
                productPanel.add(productButton);
                productButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ps.println(product.getName() + "\n" + product.getStoreName() + "\nremoveProductCart");
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                productPanel.remove(productLabel);
                                productPanel.remove(productButton);
                                productPanel.repaint();
                            }
                        });
                    }
                });

                centerPanel.add(productPanel);
            }

        JScrollPane scrollPane = new JScrollPane(centerPanel);

        content.add(scrollPane, BorderLayout.CENTER);

        //Bottom Panel
        JPanel bottomPanel = new JPanel();

        marketButton = new JButton("Return to Market");
        marketButton.addActionListener(bottomListener);
        purchaseAllButton = new JButton("Purchase All");
        purchaseAllButton.addActionListener(bottomListener);
        purchaseHistoryButton = new JButton("Purchase History");
        purchaseHistoryButton.addActionListener(bottomListener);

        bottomPanel.add(marketButton);
        bottomPanel.add(purchaseAllButton);
        bottomPanel.add(purchaseHistoryButton);

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
                    "Placeholder description", 10, 9.9, 100, 100.25));
        }
        SwingUtilities.invokeLater(new ShoppingCartGUI(testProducts, new PrintStream(new ByteArrayOutputStream())));
    }

    public void disposeThis() {
        frame.dispose();
    }
}