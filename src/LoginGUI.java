import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.BoxLayout;


/**
 * LoginGUI
 *
 * Allows users to log in
 *
 * @author Yash Ashtekar
 * @version 14/05/23
 */
public class LoginGUI extends JComponent implements Runnable {
    JFrame frame;
    private PrintStream ps;

    public LoginGUI(PrintStream ps) {
        this.ps = ps;
    }


    public void run() {
        frame = new JFrame("The Marketplace - LOGIN");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        JLabel title = new JLabel("Login Page");
        //frame.setSize(300, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel();
        content.add(topPanel, BorderLayout.NORTH);
        topPanel.add(title);

        //topPanel.add(name);
        JPanel midPanel = new JPanel();

        content.add(midPanel, BorderLayout.CENTER);


        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("E-mail");
        JLabel pwd = new JLabel("Password");
        JTextField n = new JTextField();
        // n.setSize(midPanel.getWidth(), 20);
        n.setMaximumSize(new Dimension(Integer.MAX_VALUE, n.getMinimumSize().height));
        JPasswordField p = new JPasswordField();
        p.setEchoChar('*');
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getMinimumSize().height));
        JCheckBox shwPwd = new JCheckBox("Show Password");
        //shwPwd.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton login = new JButton("LOGIN");
        JButton signUp = new JButton("SIGN UP");
        //name.setBackground(Color.BLACK);
        // name.setAlignmentX(Component.CENTER_ALIGNMENT);
        //name.setSize(300, 72 );
        //name.setOpaque(true);
        midPanel.add(name);
        midPanel.add(n);
        midPanel.add(pwd);
        midPanel.add(p);
        midPanel.add(shwPwd);
        midPanel.add(buttonPanel);
        buttonPanel.add(login);
        buttonPanel.add(signUp);
        frame.pack();
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                ps.println("close");
            }
        });
        shwPwd.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    p.setEchoChar((char) 0);
                } else {
                    p.setEchoChar('*');
                }
            }
        });

        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (n.getText().isBlank()) {
                    JOptionPane.showMessageDialog(null, "E-mail cannot be empty!", "Log In",
                            JOptionPane.ERROR_MESSAGE);
                } else if (new String(p.getPassword()).isBlank()) {
                    JOptionPane.showMessageDialog(null, "Password cannot be empty!",
                            "Log In", JOptionPane.ERROR_MESSAGE);
                } else {
                    String username = n.getText();
                    String pwd = new String(p.getPassword());

                    ps.println(username + "\n" + pwd + "\n" + "logIn");
                }

            }
        });

        signUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ps.println("goToSignUp");
                frame.dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new LoginGUI(new PrintStream(new ByteArrayOutputStream())));
    }

}