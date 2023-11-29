import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * ChangeProfileGUI
 * <p>
 * GUI for users to modify their profile info
 *
 * @author Yash Ashtekar
 * @version 12/05/23
 */
public class ChangeProfileGUI extends JComponent implements Runnable {

    public ChangeProfileGUI(ArrayList<String> profileInfo, PrintStream outputStream) {
        this.ps = outputStream;
        this.profileInfo = profileInfo;
    }

    JButton updateProfileButton;

    JButton removeProfileButton;
    ArrayList<String> profileInfo;

    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == updateProfileButton) {
                if (!(passConfirmField.getText().equals(passField.getText()))) {
                    JOptionPane.showMessageDialog(null, "Passwords did not match!", "The Marketplace",
                            JOptionPane.ERROR_MESSAGE);
                } else if (MarketplaceClient.invalidString(nameField.getText()) ||
                        MarketplaceClient.invalidString(emailField.getText()) ||
                        MarketplaceClient.invalidString(passField.getText())) {

                    JOptionPane.showMessageDialog(null, "Fields cannot contain commas nor special characters.",
                            "The Marketplace", JOptionPane.INFORMATION_MESSAGE);

                } else {
                    ps.println(nameField.getText() + "\n" + emailField.getText() + "\n" + passField.getText() +
                            "\nupdateProfile");
                    frame.dispose();
                }
            }
        }
    };

    ActionListener deleteActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int choice = JOptionPane.showConfirmDialog(null, "Are you sure?", "The Marketplace",
                    JOptionPane.YES_NO_OPTION);

            if (choice == 0) {
                ps.println("deleteAccount");
                JOptionPane.showMessageDialog(null, "Deleted account.",
                        "The Marketplace", JOptionPane.PLAIN_MESSAGE);
                frame.dispose();
                ps.println("close");
            }
        }
    };

    private JTextField nameField;
    private JTextField emailField;
    private JTextField passField;
    private JTextField passConfirmField;
    JFrame frame;

    private PrintStream ps;

    public void run() {
        frame = new JFrame();
        frame.setTitle("The Marketplace");

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        nameField = new JTextField("", 30);
        nameField.setText(profileInfo.get(0));
        JLabel nameLabel = new JLabel("Name");
        emailField = new JTextField("", 30);
        emailField.setText(profileInfo.get(1));
        JLabel emailLabel = new JLabel("Email");
        passField = new JTextField("", 30);
        passField.setText(profileInfo.get(2));
        JLabel passLabel = new JLabel("Password");
        passConfirmField = new JTextField("", 30);
        JLabel passConfirmLabel = new JLabel("Confirm Password");

        JPanel bottomPanel = new JPanel();
        updateProfileButton = new JButton("Update Profile");
        removeProfileButton = new JButton("Delete Profile");

        centerPanel.add(nameLabel);
        centerPanel.add(nameField);
        centerPanel.add(emailLabel);
        centerPanel.add(emailField);
        centerPanel.add(passLabel);
        centerPanel.add(passField);
        centerPanel.add(passConfirmLabel);
        centerPanel.add(passConfirmField);

        bottomPanel.add(updateProfileButton);
        bottomPanel.add(removeProfileButton);

        updateProfileButton.addActionListener(actionListener);
        removeProfileButton.addActionListener(deleteActionListener);

        content.add(centerPanel, BorderLayout.CENTER);
        content.add(bottomPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        PrintStream testStream = new PrintStream(new ByteArrayOutputStream());
        SwingUtilities.invokeLater(new ChangeProfileGUI(new ArrayList<String>(List.of("Jim",
                "jim@gmail.com", "password123")), testStream));
    }

}