
import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import javax.swing.*;

public class Signin implements ActionListener {

    public static TCPClient client = new TCPClient();
    public static JTextField user = new JTextField();
    public static JPasswordField pass = new JPasswordField();
    public static JButton btnSignIn = new JButton("Sign In");
    public static JButton btnSignUp = new JButton("Sign Up");
    public static JButton btnGuest = new JButton("Play As Guest");
    private static JLabel OR = new JLabel("- OR -");
    private static JLabel Welcome = new JLabel("Welcome!");
    private static JLabel userLabel = new JLabel("Username:");
    private static JLabel passLabel = new JLabel("Password:");
    public static boolean signed_in = false;
    static String signed_in_user = "Guest";
    private JButton exit = new JButton();
    private JButton adminZone = new JButton("Admin Zone");

    public Signin() {

        //actionListener
        btnSignIn.addActionListener(this);
        btnSignIn.setToolTipText("Sign into user account");
        btnSignUp.addActionListener(this);
        btnGuest.addActionListener(this);
        exit.addActionListener(this);
        adminZone.addActionListener(this);
        //actionListener

        //tooltips
        btnSignUp.setToolTipText("<html><p style='border:solid black 1px;'>signup to get access to<br>all features</p></html>");
        btnGuest.setToolTipText("offline gameplay");
        userLabel.setToolTipText("only letters, up to 8 letters");
        //add
        Launcher.signinPanel.add(userLabel);
        Launcher.signinPanel.add(passLabel);
        Launcher.signinPanel.add(user);
        Launcher.signinPanel.add(pass);
        Launcher.signinPanel.add(btnSignIn);
        Launcher.signinPanel.add(btnSignUp);
        Launcher.signinPanel.add(btnGuest);
        Launcher.signinPanel.add(OR);
        Launcher.signinPanel.add(Welcome);
        Launcher.signinPanel.add(exit);
        Launcher.signinPanel.add(adminZone);
        //add

        //setFont
        user.setFont(new Font("Aharoni", Font.BOLD, 28));
        pass.setFont(user.getFont());
        btnGuest.setFont(new Font("Aharoni", Font.BOLD, 28));
        OR.setFont(new Font("Aharoni", Font.BOLD, 28));
        Welcome.setFont(new Font("Aharoni", Font.BOLD, 72));
        userLabel.setFont(new Font("Aharoni", Font.PLAIN, 18));
        passLabel.setFont(userLabel.getFont());
        btnSignIn.setFont(new Font("Aharoni", Font.PLAIN, 16));
        btnSignUp.setFont(btnSignIn.getFont());
        exit.setFont(btnSignIn.getFont());
        //setFont

        //setBounds
        Welcome.setBounds(630, 50, 1000, 200); //"WELCOME" label
        userLabel.setBounds(250, 290, 200, 40); // "Username" label
        user.setBounds(250, 320, 200, 40); //Username textfield
        passLabel.setBounds(250, 350, 200, 40); // "Password" label
        pass.setBounds(250, 380, 200, 40); // Password textfield
        btnSignIn.setBounds(250, 430, 95, 50); //"Sign In" button
        btnSignUp.setBounds(360, 430, 95, 50); //"Sign Up" button
        btnGuest.setBounds(1100, 360, 250, 70); //"Play as Guest" button
        OR.setBounds(780, 370, 200, 50); //"-OR-" label
        exit.setBounds(50, 50, 100, 96);
        adminZone.setBounds(1400, 700, 100, 50); //adminzone button
        //setBounds

        exit.setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/exit.png")));
    }

    void start(String action, String user, String pass) { // start connection with server
        String message = "";
        try {
            if (client.getClientSocket() == null) {
                client.clientConnect();
            }
            client.sendTo(action + user + "!" + pass + '\n'); // sending player info
            message = client.getFrom();
        }
        catch (Exception d) {
            System.out.println(d);
        }
        if (message.equals("taken")) {
            JOptionPane.showMessageDialog(Launcher.mainFrame, "Username taken, please choose another.");
        }
        else if (message.equals("registered")) {
            JOptionPane.showMessageDialog(Launcher.mainFrame, "Thank you for registering, you can now sign in.");
        }
        else if (message.equals("error")) {
            JOptionPane.showMessageDialog(Launcher.mainFrame, "Username and/or password incorrect\nOr user doesn't exist.");
        }
        else if (message.equals("signedin")) {
            signed_in_user = this.user.getText();
            signed_in = true;
            JOptionPane.showMessageDialog(Launcher.mainFrame, "Welcome " + signed_in_user);
            new WaitingRoom();
            Info.InfoDisplay();
            Launcher.mode3();
            try {
                Signin.client.sendTo("@" + Signin.signed_in_user + " has connected\n");
            }
            catch (Exception e) {
            }
        }
        else {
            JOptionPane.showMessageDialog(Launcher.mainFrame, "User is already logged in.");
        }
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
                try{
                if(InetAddress.getByName("18.224.153.248").isReachable(5000))
                start("signin", user.getText(), String.valueOf(pass.getPassword()));
                else JOptionPane.showMessageDialog(Launcher.mainFrame, "cannot reach server");
                }
                catch(Exception ex)
                {}
            }
        }

        else if (e.getSource() == btnSignUp) {     //sign up
            if (user.getText().toLowerCase().equals("guest")) // username cant be "guest"
            {
                JOptionPane.showMessageDialog(Launcher.mainFrame, "Username cannot be any variation of 'guest'.\nPlease choose another username");
            }
            else if (user.getText().length() > 8) // username too long
            {
                JOptionPane.showMessageDialog(Launcher.mainFrame, "Username must be up to 8 characters");
            }
            else if (user.getText().equals("") || String.valueOf(pass.getPassword()).equals("")) // username or password is empty
            {
                JOptionPane.showMessageDialog(Launcher.mainFrame, "Username and/or Password\ncannot be empty.");
            }
            else if (!user.getText().matches("([a-zA-Z0-9]+)") || !String.valueOf(pass.getPassword()).matches("([a-zA-Z0-9]+)")) {
                JOptionPane.showMessageDialog(Launcher.mainFrame, "Username/Password cannot contain special symbols");
            }
            else {
                start("signup", user.getText(), String.valueOf(pass.getPassword())); // sign up to server
            }
        }
        else if (e.getSource() == btnGuest) { // guest screen
            Launcher.mode2();
            new GuestOptions();
        }
        else if (e.getSource() == exit) {
            Launcher.closeGame();
        }
        else if (e.getSource() == adminZone) {
            Launcher.mode5();
            new AdminZoneSignIn();
        }
    }
}
