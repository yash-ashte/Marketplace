import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


/**
 * SaleHistoryGUI
 *
 * Allows sellers to view their sale history
 *
 * @author Yash Ashtekar
 * @version 15/05/23
 */
public class SaleHistoryGUI extends JComponent implements Runnable {


    ArrayList<Sale> saleHist;
    PrintStream ps;

    public SaleHistoryGUI(ArrayList<Sale> saleHist, PrintStream ps) {
        this.ps = ps;
        this.saleHist = saleHist;
    }

    @Override
    public void run() {
        JFrame frame = new JFrame("The Marketplace");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel title = new JLabel("Sale History");
        title.setFont(new Font(String.valueOf(title.getFont()), Font.PLAIN, 24));

        JPanel topPanel = new JPanel();
        content.add(topPanel, BorderLayout.NORTH);
        topPanel.add(title);

        JPanel midPanel = new JPanel();
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));
        content.add(midPanel, BorderLayout.CENTER);
        JLabel phTitle = new JLabel("Your Store's Sale History is as follows");
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


        String saleHistString = "";
        for (Sale sale : saleHist) {

            String revenue = String.format("%.2f", (sale.getRevenue()));

            if (sale.getQuantity() > 1)
                saleHistString += sale.getProductName() + ": " + sale.getQuantity() +
                        " were purchased by " + sale.getCustomerEmail() +
                        ", generating a revenue of $" + revenue + "\n\n";
            else
                saleHistString += sale.getProductName() + ": " + sale.getQuantity() +
                        " was purchased by " + sale.getCustomerEmail() +
                        ", generating a revenue of $" + revenue + "\n\n";
        }

        if (!saleHistString.isBlank())
            purchaseHist.setText(saleHistString);
        else
            purchaseHist.setText("No sales made yet.");

        JPanel botPanel = new JPanel(new FlowLayout());
        content.add(botPanel, BorderLayout.SOUTH);
        JButton market = new JButton("Back to Marketplace");
        botPanel.add(market);

        //JButton market = new JButton("Back to Marketplace");
        market.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ps.println("goToSellerHome");
                frame.dispose();
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

    public static void main(String[] args) {

        Sale s = new Sale("email1", "Test Product", 20, 4);
        Sale s2 = new Sale("email2", "TestProduct2", 24.3, 9);

        SwingUtilities.invokeLater(new SaleHistoryGUI(new ArrayList<Sale>(List.of(s, s2)) ,
                new PrintStream(new ByteArrayOutputStream())));
    }
}