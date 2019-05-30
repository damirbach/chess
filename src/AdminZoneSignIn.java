
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Damir Bachtayev
 */
public class AdminZoneSignIn implements ActionListener {

    public static TCPClient client = new TCPClient();
    JTextField user = new JTextField();
    JPasswordField pass = new JPasswordField();
    JButton btnSignIn = new JButton("Sign In");

    public AdminZoneSignIn() {
        Launcher.adminZone.add(user);
        Launcher.adminZone.add(pass);
        Launcher.adminZone.add(btnSignIn);

        user.setBounds(700, 300, 200, 50);
        pass.setBounds(700, 400, 200, 50);
        btnSignIn.setBounds(750, 500, 100, 50);

        btnSignIn.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSignIn) {         // sign in
            if (user.getText().equals("") || String.valueOf(pass.getPassword()).equals("")) {
                JOptionPane.showMessageDialog(Launcher.mainFrame, "Username and/or Password\ncannot be empty.");
            }
            else if (!user.getText().matches("([a-zA-Z0-9]+)") || !String.valueOf(pass.getPassword()).matches("([a-zA-Z0-9]+)")) {
                JOptionPane.showMessageDialog(Launcher.mainFrame, "Username/Password cannot contain special symbols");
            }
            else {
                start(user.getText(), String.valueOf(pass.getPassword()));
            }
        }
    }

    void clear() {
//        try {
//            client.endConnection();
//            client.resetSocket();
//        }
//        catch (Exception e) {
//        }
        Launcher.adminZone.remove(user);
        Launcher.adminZone.remove(pass);
        Launcher.adminZone.remove(btnSignIn);
    }

    void start(String username, String password) {
        String message = "";
        try {
            if (client.getClientSocket() == null) {
                client.clientConnect();
            }
            client.sendTo("." + username + "!" + password + '\n');
            message = client.getFrom();
        }
        catch (Exception d) {
            System.out.println(d);
            JOptionPane.showMessageDialog(Launcher.mainFrame, "Can't connect to server");
        }
        if (message.equals("signedin")) {
            Signin.signed_in_user = this.user.getText();
            Signin.signed_in = true;
            JOptionPane.showMessageDialog(Launcher.mainFrame, "Welcome " + Signin.signed_in_user);
            clear();
            new AdminZone();
            Launcher.repaint();
        }
        else {
            JOptionPane.showMessageDialog(Launcher.mainFrame, "Error, Admin Account Doesn't Exist");
        }
    }
}
