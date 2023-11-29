import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;


/**
 * StoresDashGUI
 * <p>
 * Allows customers to view information on the stores in the marketplace
 *
 * @author Yash Ashtekar
 * @version 19/05/23
 */
@SuppressWarnings("unchecked")
public class StoresDashGUI implements Runnable {
    private PrintStream ps;
    JFrame frame;

    private ArrayList<Store> storeHist;
    private ArrayList<Integer> intList;
    private ArrayList<String> sellerList;

    public StoresDashGUI(ArrayList<Store> storeHist, ArrayList<String> sellerList,
                         ArrayList<Integer> storeValues, PrintStream outputStream) {
        this.storeHist = storeHist;
        this.ps = outputStream;
        this.intList = storeValues;
        this.sellerList = sellerList;
    }

    public void run() {
        frame = new JFrame("The Marketplace");
        Container content = frame.getContentPane();

        content.setLayout(new BorderLayout());

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel title = new JLabel("                             Store Dashboard     ");
        title.setFont(new Font(String.valueOf(title.getFont()), Font.PLAIN, 24));

        JButton back = new JButton("Back");

        JComboBox<String> sortDropdown;
        ActionListener sortListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> parentCb = (JComboBox<String>) e.getSource(); //Initializes parent dropdown
                int sortOption = parentCb.getSelectedIndex();

                if (sortOption > 0) {
                    ps.println(sortOption + "\nsortStores");
                    frame.dispose();
                }

            }
        };

        //Top Panel
        JPanel topPanel = new JPanel();
        content.add(topPanel, BorderLayout.NORTH);
        String[] sortOptions = {"Sort Stores", "Total products sold (Low to High)",
                "Total products sold (High to Low)", "Number of products purchased (Low to High)",
                "Number of products purchased (High to Low)"};
        //1 total lowhigh; 2 total highlow; 3 customer lowhigh; 4 customer highlow
        sortDropdown = new JComboBox<String>(sortOptions);
        sortDropdown.addActionListener(sortListener);

        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });

        topPanel.add(back);
        topPanel.add(title);
        topPanel.add(sortDropdown);

        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ps.println("goToCustomerHome");
                frame.dispose();
            }
        });

        //Center Panel
        JPanel midPanel = new JPanel();
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));
        content.add(midPanel, BorderLayout.CENTER);
        JTextArea storeDash = new JTextArea();
        storeDash.setBackground(frame.getBackground());
        storeDash.setOpaque(true);
        storeDash.setEditable(false);
        storeDash.setFont(new Font(String.valueOf(title.getFont()), Font.PLAIN, 15));

        JScrollPane sp = new JScrollPane(storeDash);
        midPanel.add(sp);


        String storeDashText = "";



        for (int i = 0; i < storeHist.size(); i++) {
            Store s = storeHist.get(i);

            storeDashText += " " + s.getStoreName() + ": owned by " +
                    sellerList.get(i) + " -------------------------- Products: " +
                    intList.get(i) + "\n\n\n";
        }

        storeDash.setText(storeDashText);

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

        ArrayList<Store> stores = new ArrayList<Store>();
        ArrayList<Integer> ints = new ArrayList<Integer>();
        ArrayList<String> names = new ArrayList<String>();

        for (int i = 0; i < 100; i++) {
            stores.add(new Store("Store" + i, new ArrayList<Product>(), new ArrayList<Sale>(), i * 0.45));
            ints.add(i);
            names.add("Seller " + i);
        }

        SwingUtilities.invokeLater(new StoresDashGUI(stores, names,
                ints, new PrintStream(new ByteArrayOutputStream())));
    }
}