import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.time.*;
import java.time.temporal.ChronoUnit;


/**
 * PurchaseHistoryGUI
 * <p>
 * Allows customers to view and export their purchase history
 *
 * @author Yash Ashtekar
 * @version 14/05/23
 */
public class PurchaseHistoryGUI extends JComponent implements Runnable {
    JTextField fn;
    ArrayList<Purchase> purchases;
    PrintStream ps;

    public PurchaseHistoryGUI(ArrayList<Purchase> purchases, PrintStream ps) {
        this.purchases = purchases;
        this.ps = ps;
    }

    public void run() {
        JFrame frame = new JFrame("The Marketplace");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel title = new JLabel("Purchase History");
        title.setFont(new Font(String.valueOf(title.getFont()), Font.PLAIN, 24));

        JPanel topPanel = new JPanel();
        content.add(topPanel, BorderLayout.NORTH);
        topPanel.add(title);

        JPanel midPanel = new JPanel();
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));
        content.add(midPanel, BorderLayout.CENTER);
        JLabel phTitle = new JLabel("Your Purchase History is as follows:");
        phTitle.setBackground(Color.GRAY);
        JTextArea purchaseHist = new JTextArea();
        purchaseHist.setBackground(Color.LIGHT_GRAY);
        purchaseHist.setOpaque(true);
        purchaseHist.setEditable(false);
        JScrollPane sp = new JScrollPane(purchaseHist);
        purchaseHist.setLineWrap(true);
        midPanel.add(phTitle);
        //midPanel.add(purchaseHist);
        midPanel.add(sp);


        String purchaseHistory = "";

        for (Purchase p : purchases) {
            if (p.getQuantity() > 1)
                purchaseHistory += "" + p.getProduct().getName() + ": " + p.getQuantity() +
                        " were purchased on " + p.getDatePurchased() + " at " +
                        p.getTimePurchased().truncatedTo(ChronoUnit.SECONDS) + "\n\n";
            else
                purchaseHistory += "" + p.getProduct().getName() + ": " + p.getQuantity() +
                        " was purchased on " + p.getDatePurchased() + " at " +
                        p.getTimePurchased().truncatedTo(ChronoUnit.SECONDS) + "\n\n";
        }

        if (!purchaseHistory.isBlank())
            purchaseHist.setText(purchaseHistory);
        else
            purchaseHist.setText("No purchases made yet.");



        JPanel botPanel = new JPanel(new FlowLayout());
        content.add(botPanel, BorderLayout.SOUTH);
        JButton market = new JButton("Back to Marketplace");
        fn  = new JTextField("Enter Filename Here");
        fn.setColumns(20);
        JButton export = new JButton("Export");
        JButton cart = new JButton("Manage Cart");
        botPanel.add(market);
        botPanel.add(fn);
        botPanel.add(export);
        botPanel.add(cart);

        market.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ps.println("goToCustomerHome");
                frame.dispose();
            }
        });

        cart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ps.println("goToCart");
                frame.dispose();
            }
        });

        export.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (fn.getText().indexOf("\\") == -1) {
                    String file = fn.getText();
                    ps.println(file + "\n" + "exportHistory");
                } else {
                    JOptionPane.showMessageDialog(null, "Filename may not contain escape characters.",
                            "The Marketplace", JOptionPane.INFORMATION_MESSAGE);
                }

            }
        });






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


    //Test
    public static void main(String[] args) {
        Product p = new Product("My Product", "The Store", null, -1, -1, -1, -1);
        Product p2 = new Product("Product 2", "Walmart", null, -1, -1, -1, -1);
        Product p3 = new Product("Product 3", "Target", null, -1, -1, -1, -1);


        Purchase pur1 = new Purchase(p, 2, 4, LocalDate.of(2022, 11, 10), LocalTime.of(2, 20, 4));
        Purchase pur2 = new Purchase(p2, 1, 8, LocalDate.of(2022, 11, 10), LocalTime.of(2, 20, 4));
        Purchase pur3 = new Purchase(p3, 10, 4, LocalDate.of(2022, 11, 10), LocalTime.of(2, 20, 4));

        ArrayList<Purchase> purchaseList = new ArrayList<Purchase>();
        purchaseList.add(pur1);
        purchaseList.add(pur2);
        purchaseList.add(pur3);

        SwingUtilities.invokeLater(new PurchaseHistoryGUI(purchaseList, new PrintStream(new ByteArrayOutputStream())));
    }
}