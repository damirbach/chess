
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class AdminZone implements ActionListener {

    JButton dispUsers = new JButton("Refresh List");
    JButton moreInfo = new JButton("More Info");
    JButton[] edit = new JButton[4];
    DefaultListModel DLM = new DefaultListModel();
    JList list = new JList(DLM);
    JButton signout = new JButton("Sign out");
    JScrollPane scrollBar = new JScrollPane(list);
    JLabel[] info = new JLabel[4];
    JTextField[] details = new JTextField[4];
    String[] oldInfo = new String[4];

    public AdminZone() {
        for (int i = 0; i < 4; i++) {
            info[i] = new JLabel();
            details[i] = new JTextField();
            Launcher.adminZone.add(info[i]);
            Launcher.adminZone.add(details[i]);
            info[i].setBounds(800, 50 + i * 50, 100, 50);
            details[i].setBounds(900, 50 + i * 50, 150, 50);
            details[i].setEnabled(false);
            details[i].setFont(new Font("Aharoni", Font.PLAIN, 24));
            edit[i] = new JButton();
            Launcher.adminZone.add(edit[i]);
            edit[i].addActionListener(this);
            edit[i].setBounds(800 + i * 100, 600, 80, 50);
        }
        signout.addActionListener(this);
        dispUsers.addActionListener(this);
        moreInfo.addActionListener(this);

        Launcher.adminZone.add(signout);
        Launcher.adminZone.add(dispUsers);
        Launcher.adminZone.add(moreInfo);
        //Launcher.adminZone.add(list);
        Launcher.adminZone.add(scrollBar);

        signout.setBounds(50, 50, 100, 50);
        dispUsers.setBounds(450, 50, 200, 50);
        moreInfo.setBounds(700, 400, 100, 50);
        scrollBar.setBounds(450, 100, 200, 500);
        dispUsers.setFont(new Font("Aharoni", Font.PLAIN, 28));

        list.setFont(new Font("Aharoni", Font.PLAIN, 24));
        info[0].setText("Username: ");
        info[1].setText("Password: ");
        info[2].setText("Wins: ");
        info[3].setText("Losses: ");

        edit[0].setText("New");
        edit[1].setText("Edit");
        edit[2].setText("Delete");
        edit[3].setText("Cancel");
        edit[3].setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == moreInfo) {
            try {
                AdminZoneSignIn.client.sendTo("=" + list.getSelectedValue().toString() + '\n');
                resetDetails();
                dispMore(AdminZoneSignIn.client.getFrom());
            }
            catch (Exception d) {
                JOptionPane.showMessageDialog(Launcher.mainFrame, "Error!\nMust display list and select value", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (e.getSource() == dispUsers) {
            try {
                resetDetails();
                fillList();
            }
            catch (Exception d) {

            }
        }
        else if (e.getSource() == signout) {
            int confirm = JOptionPane.showOptionDialog(
                    Launcher.mainFrame, "Are You Sure to Sign Out?",
                    "Sign Out Confirmation", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (confirm == 0) {
                try {
                    if (!Signin.signed_in_user.toLowerCase().equals("guest")) {
                        Signin.client.endConnection();
                        Signin.client.resetSocket();
                    }
                    Launcher.gameStarted = false;
                    Signin.signed_in_user = "Guest";
                    Launcher.currentmode = "";
                }
                catch (Exception exc) {
                    System.out.println("exitlistener " + exc);
                }
                Launcher.mode1();
            }
        }
        else {
            if (e.getSource() == edit[0]) { //new
                String str;
                if (edit[0].getText().equals("New")) {
                    for (int i = 0; i < 4; i++) {
                        oldInfo[i] = details[i].getText();
                    }
                    edit[0].setText("Save");
                    edit[1].setEnabled(false);
                    edit[2].setEnabled(false);
                    edit[3].setEnabled(true);
                    for (int i = 0; i < 4; i++) {
                        details[i].setText("");
                        details[i].setEnabled(true);
                    }
                }
                else if (edit[0].getText().equals("Save")) {
                    edit[0].setText("New");
                    edit[1].setEnabled(true);
                    edit[2].setEnabled(true);
                    edit[3].setEnabled(false);
                    ///////
                    if (details[0].getText().toLowerCase().equals("guest")) // username cant be "guest"
                    {
                        JOptionPane.showMessageDialog(Launcher.mainFrame, "Username cannot be any variation of 'guest'.\nPlease choose another username");
                    }
                    else if (details[0].getText().length() > 8) // username too long
                    {
                        JOptionPane.showMessageDialog(Launcher.mainFrame, "Username must be up to 8 characters");
                    }
                    else if (details[0].getText().equals("") || details[1].getText().equals("")) // username or password is empty
                    {
                        JOptionPane.showMessageDialog(Launcher.mainFrame, "Username and/or Password\ncannot be empty.");
                    }
                    else if (!details[0].getText().matches("([a-zA-Z0-9]+)") || !details[1].getText().matches("([a-zA-Z0-9]+)")) {
                        JOptionPane.showMessageDialog(Launcher.mainFrame, "Username/Password cannot contain special symbols");
                    }
                    else if (!details[2].getText().equals("") || !details[3].getText().equals("")) {
                        if (!details[2].getText().matches("([0-9]+)") || !details[3].getText().matches("([0-9]+)")) {
                            JOptionPane.showMessageDialog(Launcher.mainFrame, "Win/Lose fields must contain numbers only.\nLeave blank for default value of 0.");
                        }
                        else if (Integer.parseInt(details[2].getText()) < 0 || Integer.parseInt(details[3].getText()) < 0) {
                            JOptionPane.showMessageDialog(Launcher.mainFrame, "Win/Lose values must be greater/equal to 0.\nLeave blank for default value of 0.");
                        }
                    }
                    else {
                        try {
                            str = "#" + details[0].getText() + "#" + details[1].getText() + "#" + (details[2].getText().equals("") ? "0" : details[2].getText()) + "#" + (details[3].getText().equals("") ? "0" : details[3].getText()) + "#";
                            AdminZoneSignIn.client.sendTo("?" + str + '\n');
                            JOptionPane.showMessageDialog(Launcher.mainFrame, AdminZoneSignIn.client.getFrom());
                            fillList();
                        }
                        catch (Exception ex) {
                            JOptionPane.showMessageDialog(Launcher.mainFrame, "user new");
                        }
                    }
                }
            }
            else if (e.getSource() == edit[1]) {//edit
                String str = "";
                if (edit[1].getText().equals("Edit")) {
                    for (int i = 0; i < 4; i++) {
                        oldInfo[i] = details[i].getText();
                    }
                    edit[1].setText("Save");
                    edit[0].setEnabled(false);
                    edit[2].setEnabled(false);
                    edit[3].setEnabled(true);
                    for (int i = 1; i < 4; i++) {
                        //details[i].setText("");
                        details[i].setEnabled(true);
                    }
                }
                else if (edit[1].getText().equals("Save")) {
                    boolean execute = true;
                    edit[1].setText("Edit");
                    edit[0].setEnabled(true);
                    edit[2].setEnabled(true);
                    edit[3].setEnabled(false);
                    for (int i = 0; i < 4; i++) {
                        details[i].setEnabled(false);
                        str += details[i].getText() + "#";
                    }
                    ///////
                    if (details[0].getText().toLowerCase().equals("guest")) // username cant be "guest"
                    {
                        JOptionPane.showMessageDialog(Launcher.mainFrame, "Username cannot be any variation of 'guest'.\nPlease choose another username");
                        execute = false;
                    }
                    else if (details[0].getText().length() > 8) // username too long
                    {
                        JOptionPane.showMessageDialog(Launcher.mainFrame, "Username must be up to 8 characters");
                        execute = false;
                    }
                    else if (details[0].getText().equals("") || details[1].getText().equals("")) // username or password is empty
                    {
                        JOptionPane.showMessageDialog(Launcher.mainFrame, "Username and/or Password\ncannot be empty.");
                        execute = false;
                    }
                    else if (!details[0].getText().matches("([a-zA-Z0-9]+)") || !details[1].getText().matches("([a-zA-Z0-9]+)")) {
                        JOptionPane.showMessageDialog(Launcher.mainFrame, "Username/Password cannot contain special symbols");
                        execute = false;
                    }
                    else if (!details[2].getText().equals("") || !details[3].getText().equals("")) {
                        if (!details[2].getText().matches("([0-9]+)") || !details[3].getText().matches("([0-9]+)")) {
                            JOptionPane.showMessageDialog(Launcher.mainFrame, "Win/Lose fields must contiain numbers only.\nLeave blank for default value of 0.");
                            execute = false;
                        }
                        else if (Integer.parseInt(details[2].getText()) < 0 || Integer.parseInt(details[3].getText()) < 0) {
                            JOptionPane.showMessageDialog(Launcher.mainFrame, "Win/Lose values must be greater/equal to 0.\nLeave blank for default value of 0.");
                            execute = false;
                        }
                    }
                    if (execute) {
                        try {
                            str = "#" + details[0].getText() + "#" + details[1].getText() + "#" + (details[2].getText().equals("") ? "0" : details[2].getText()) + "#" + (details[3].getText().equals("") ? "0" : details[3].getText()) + "#";
                            AdminZoneSignIn.client.sendTo("-" + str + '\n');
                            JOptionPane.showMessageDialog(Launcher.mainFrame, AdminZoneSignIn.client.getFrom());
                            fillList();
                        }
                        catch (Exception ex) {
                            //JOptionPane.showMessageDialog(Launcher.mainFrame, "user edit");
                        }
                    }
                }
            }
            else if (e.getSource() == edit[2]) {//delete
                int confirm = JOptionPane.showOptionDialog(
                        Launcher.mainFrame, "Delete user " + details[0].getText() + "?",
                        "User Deletion Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    try {
                        AdminZoneSignIn.client.sendTo("<" + details[0].getText() + '\n');
                        JOptionPane.showMessageDialog(Launcher.mainFrame, AdminZoneSignIn.client.getFrom());
                        for (int i = 0; i < 4; i++) {
                            details[i].setText("");
                        }
                    }
                    catch (Exception exc) {
                        JOptionPane.showMessageDialog(Launcher.mainFrame, "user deletion");
                    }
                    fillList();
                }
            }
            else if (e.getSource() == edit[3]) {//cancel
                edit[0].setText("New");
                edit[0].setEnabled(true);
                edit[1].setText("Edit");
                edit[1].setEnabled(true);
                edit[2].setText("Delete");
                edit[2].setEnabled(true);
                edit[3].setText("Cancel");
                edit[3].setEnabled(false);
                for (int i = 0; i < 4; i++) {
                    details[i].setText(oldInfo[i]);
                    details[i].setEnabled(false);
                }
            }
        }
    }

    void fillList() {
        DLM.clear();
        try {
            AdminZoneSignIn.client.sendTo("+\n");
            String msg = AdminZoneSignIn.client.getFrom();
            System.out.println(msg);
            int count = Integer.parseInt(msg.substring(0, msg.indexOf("#")));
            msg = msg.substring(msg.indexOf("#"));
            for (int i = 0; i < count; i++) {
                msg = msg.substring(msg.indexOf("#") + 1);
                DLM.addElement(msg.substring(0, msg.indexOf("#")));
                //DLM.addElement(i + "");
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(Launcher.mainFrame, "fillList error");
        }
    }

    void resetDetails() {
        for (int i = 0; i < 4; i++) {
            details[i].setText("");
            details[i].setEnabled(false);
        }
        edit[0].setText("New");
        edit[1].setText("Edit");
        edit[2].setText("Delete");
        edit[3].setText("Cancel");
        edit[0].setEnabled(true);
        edit[1].setEnabled(true);
        edit[2].setEnabled(true);
        edit[3].setEnabled(false);
    }

    void dispMore(String msg) {
        try {
            for (int i = 0; i < 3; i++) {
                details[i].setText(msg.substring(0, msg.indexOf('#')));
                msg = msg.substring(msg.indexOf('#') + 1);
            }
            details[3].setText(msg);
            for (int i = 0; i < 4; i++) {
                info[i].setVisible(true);
                details[i].setVisible(true);
            }
        }
        catch (Exception e) {

        }
    }

}
