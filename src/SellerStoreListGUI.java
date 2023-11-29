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
 * GUI for sellers to manage their list of stores
 *
 * @author Yash Ashtekar
 * @version 17/05/23
 */

public class SellerStoreListGUI extends JComponent implements Runnable {
    public SellerStoreListGUI(ArrayList<Store> stores, PrintStream ps) {
        this.stores = stores;
        this.ps = ps;
    }
    private ArrayList<Store> stores;
    private PrintStream ps;

    static JFrame frame;

    //Top Panel
    JLabel titleLabel;

    //Center Panel
    JLabel instructionLabel;

    //Bottom Panel
    JButton cartButton;
    JButton addStoreButton;
    JButton profileButton;

    @Override
    public void run() {
        frame = new JFrame();
        frame.setTitle("The Marketplace");

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        //Top Panel
        JPanel topPanel = new JPanel();

        titleLabel = new JLabel("The Marketplace - Store List");

        topPanel.add(titleLabel);

        content.add(topPanel, BorderLayout.NORTH);

        //Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));

        if (stores.size() == 0) {
            JLabel noStores = new JLabel("You currently have no stores!");
            JPanel noStorePanel = new JPanel();
            noStorePanel.add(noStores);
            centerPanel.add(noStorePanel);
        } else
            for (Store store : stores) {
                JPanel productPanel = new JPanel();
                JLabel productLabel = new JLabel(store.getStoreName());
                JButton productButton = new JButton("View Store");

                productPanel.add(productLabel);
                productPanel.add(productButton);
                productButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ps.println(store.getStoreName() + "\ngoToStoreInfo");
                        frame.dispose();
                    }
                });

                centerPanel.add(productPanel);
            }

        JScrollPane scrollPane = new JScrollPane(centerPanel);

        content.add(scrollPane, BorderLayout.CENTER);

        //Bottom Panel
        JPanel bottomPanel = new JPanel();

        cartButton = new JButton("View Shopping Cart Metrics");
        addStoreButton = new JButton("Add Store");
        profileButton = new JButton("Edit Profile");

        bottomPanel.add(cartButton);
        bottomPanel.add(addStoreButton);
        bottomPanel.add(profileButton);

        ActionListener bottomListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == cartButton) {
                    ps.println("goToCartsInfo");
                    frame.dispose();
                } else if (e.getSource() == addStoreButton) {

                    String storeName = JOptionPane.showInputDialog(frame, "Enter store name.",
                            "The Marketplace", JOptionPane.PLAIN_MESSAGE);

                    if (storeName != null && MarketplaceClient.invalidString(storeName)) {

                        JOptionPane.showMessageDialog(null, "Fields cannot contain commas nor special characters.",
                                "The Marketplace", JOptionPane.INFORMATION_MESSAGE);
                    } else if (storeName != null && !storeName.isBlank())
                        ps.println(storeName + "\naddStore");


                } else if (e.getSource() == profileButton) {
                    ps.println("goToProfile");
                }

            }
        };

        cartButton.addActionListener(bottomListener);
        addStoreButton.addActionListener(bottomListener);
        profileButton.addActionListener(bottomListener);

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

    //test PSVM
    public static void main(String[] args) {
        ArrayList<Product> testProducts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            testProducts.add(new Product("Product" + (i + 1), "Store" + (i + 1),
                    "Placeholder description", 10, 9.99, 100, 100.25));
        }
        ArrayList<Store> testStores = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            testStores.add(new Store("Store" + (i + 1), testProducts, null, 0));
        }
        SwingUtilities.invokeLater(new SellerStoreListGUI(testStores, new PrintStream(new ByteArrayOutputStream())));
    }
}