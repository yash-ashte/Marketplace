import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


/**
 * SignUpGUI
 * <p>
 * GUI for users creating a new customer/seller profile
 *
 * @author Yash Ashtekar
 * @version 14/05/23
 */
public class SignUpGUI extends MarketplaceClient implements Runnable {
    JFrame frame;
    JButton signUpButton;
    JButton cancelButton;
    JTextField nameField;
    JTextField emailField;
    JTextField passField;
    JTextField passConfirmField;
    PrintStream ps;


    public SignUpGUI(PrintStream ps) {
        this.ps = ps;
    }


    @Override
    public void run() {

        frame = new JFrame();
        frame.setTitle("The Marketplace - Create New Account");

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        nameField = new JTextField("", 30);
        emailField = new JTextField("", 30);
        passField = new JTextField("", 30);
        passConfirmField = new JTextField("", 30);

        signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameField.getText().isBlank()) { //if conditions checking for any fields that are blank/incorrect
                    JOptionPane.showMessageDialog(null, "Name cannot be empty!", "Sign Up",
                            JOptionPane.ERROR_MESSAGE);
                } else if (emailField.getText().isBlank()) {
                    JOptionPane.showMessageDialog(null, "Email cannot be empty!", "Sign Up",
                            JOptionPane.ERROR_MESSAGE);
                } else if (passField.getText().isBlank()) {
                    JOptionPane.showMessageDialog(null, "Password cannot be empty!", "Sign Up",
                            JOptionPane.ERROR_MESSAGE);
                } else if (!(passConfirmField.getText().equals(passField.getText()))) {
                    JOptionPane.showMessageDialog(null, "Passwords did not match!", "Sign Up",
                            JOptionPane.ERROR_MESSAGE);
                } else if (MarketplaceClient.invalidString(nameField.getText()) ||
                        MarketplaceClient.invalidString(emailField.getText()) ||
                        MarketplaceClient.invalidString(passField.getText())) {

                    JOptionPane.showMessageDialog(null, "Fields cannot contain commas nor special characters.",
                            "The Marketplace", JOptionPane.INFORMATION_MESSAGE);

                } else { //if all conditions pass then prompt seller/customer
                    String[] options = {"Seller", "Customer"};
                    int userSelection = JOptionPane.showOptionDialog(null, "Are you a seller or a customer?",
                            "Select an account type",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                    if (userSelection == 0) {
                        ps.println(nameField.getText() + "\n" + emailField.getText() + "\n" +
                                passField.getText() + "\n" + "signUpSeller");
                        System.out.flush();
                        frame.dispose();
                    } else if (userSelection == 1) {
                        //SwingUtilities.invokeLater(new CustomerMarketplaceGUI());
                        ps.println(nameField.getText() + "\n" + emailField.getText() + "\n" +
                                passField.getText() + "\n" + "signUpCustomer");
                        System.out.flush();
                        frame.dispose();
                    }
                }
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ps.println("close");
                frame.dispose();
            }
        });

        centerPanel.add(new JLabel("Name"));
        centerPanel.add(nameField);

        centerPanel.add(new JLabel("E-Mail"));
        centerPanel.add(emailField);

        centerPanel.add(new JLabel("Password"));
        centerPanel.add(passField);

        centerPanel.add(new JLabel("Confirm Password"));
        centerPanel.add(passConfirmField);

        centerPanel.add(signUpButton);

        centerPanel.add(cancelButton);

        content.add(centerPanel, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
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
        SwingUtilities.invokeLater(new SignUpGUI(new PrintStream(new ByteArrayOutputStream())));
    }

}