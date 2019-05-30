import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class WaitingRoom implements ActionListener {

    public static String player = "";
    private static Timer timer = new Timer(); //new timer
    private static JButton[] newgame = new JButton[5];
    private static JButton[] joinView = new JButton[10];
    private static JLabel[] whiteInfo = new JLabel[10];
    private static JLabel[] blackInfo = new JLabel[10];
    private static JLabel[] roomNumber = new JLabel[10];
    static TimerTask task;
    private boolean Connected = false;
    private static boolean inQueue = false;
    public static String ifConnected = "";
    public static ReceiveFromServer t1;
    private static String boardS = "";

    public static Font font = new Font("Aharoni", Font.PLAIN, 16);
    public static Font font2 = new Font("Aharoni", Font.PLAIN, 20);

    private JButton load = new JButton("Load Game");
    private JFileChooser fc = new JFileChooser();

    public WaitingRoom() {
        reInitLabels();
        refreshDisplay();
    }

    private void reInitLabels() {
        for (int i = 0; i < 5; i++) {
            newgame[i] = new JButton();
            newgame[i].setFont(font2);
        }

        newgame[0].addActionListener(this);
        newgame[1].addActionListener(this);
        newgame[2].addActionListener(this);
        newgame[3].addActionListener(this);
        newgame[4].addActionListener(this);
        newgame[0].setBounds(20, 20, 170, 70);
        newgame[0].setText("New (white)");
        newgame[0].setToolTipText("New game as white player");
        newgame[1].setBounds(20, 100, 170, 70);
        newgame[1].setText("New (black)");
        newgame[1].setToolTipText("New game as black player");
        newgame[2].setBounds(210, 20, 170, 70);
        newgame[2].setText("VS AI");
        newgame[2].setToolTipText("New game vs comp");
        newgame[3].setBounds(210, 100, 170, 70);
        newgame[3].setText("Make Custom");
        newgame[3].setToolTipText("Make your own statring board");
        newgame[4].setBounds(400, 100, 170, 70);
        newgame[4].setText("Cancel");
        newgame[4].setToolTipText("cancel started game");
        Launcher.waitingPanel.add(newgame[0]);
        Launcher.waitingPanel.add(newgame[1]);
        Launcher.waitingPanel.add(newgame[2]);
        Launcher.waitingPanel.add(newgame[3]);
        Launcher.waitingPanel.add(newgame[4]);

        for (int i = 0; i < 10; i++) {
            blackInfo[i] = new JLabel();
            whiteInfo[i] = new JLabel();
            roomNumber[i] = new JLabel();
            joinView[i] = new JButton();

            whiteInfo[i].setFont(font);
            blackInfo[i].setFont(font);
            roomNumber[i].setFont(font);
            joinView[i].setFont(font);

            roomNumber[i].setBackground(new Color(224, 224, 224));
            roomNumber[i].setOpaque(true);
            whiteInfo[i].setBackground(new Color(224, 224, 224));
            whiteInfo[i].setOpaque(true);
            blackInfo[i].setBackground(new Color(224, 224, 224));
            blackInfo[i].setOpaque(true);

            whiteInfo[i].setText("  White: -");
            blackInfo[i].setText("  Black: -");
            roomNumber[i].setText("  Room #: -");
            joinView[i].setText("Join");

            whiteInfo[i].setBounds(20, 200 + i * 50, 150, 30);
            blackInfo[i].setBounds(180, 200 + i * 50, 150, 30);
            roomNumber[i].setBounds(340, 200 + i * 50, 100, 30);
            joinView[i].setBounds(500, 200 + i * 50, 150, 30);

            joinView[i].addActionListener(this);
            joinView[i].setEnabled(false);

            Launcher.waitingPanel.add(whiteInfo[i]);
            Launcher.waitingPanel.add(blackInfo[i]);
            Launcher.waitingPanel.add(roomNumber[i]);
            Launcher.waitingPanel.add(joinView[i]);
        }

        load.addActionListener(this);
        load.setToolTipText("<html>Load saved game from file<br>start as white player</html>");
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        Launcher.waitingPanel.add(load);
        load.setBounds(400, 20, 170, 70);
        load.setFont(newgame[0].getFont());

        Launcher.waitingPanel.revalidate();
        Launcher.waitingPanel.repaint();
        t1 = new ReceiveFromServer(Signin.client.getClientSocket());
        t1.start();
    }

    private static void displayRooms(String s) {
        if (Integer.parseInt(s.substring(0, 1)) > 0) {
            int roomcount = Integer.parseInt(s.substring(0, 1));
            String str = s.substring(1);
            for (int i = 0; i < roomcount; i++) {
                roomNumber[i].setText("  Room #:" + str.substring(0, 1));
                str = str.substring(str.indexOf("!") + 1);
                if (str.substring(0, str.indexOf("!")).equals("nop1")) {
                    whiteInfo[i].setText("  White: -");
                }
                else {
                    whiteInfo[i].setText("  White: " + str.substring(0, str.indexOf("!")));
                }
                str = str.substring(str.indexOf("!") + 1);
                if (str.substring(0, str.indexOf("!")).equals("nop2")) {
                    blackInfo[i].setText("  Black: -");
                }
                else {
                    blackInfo[i].setText("  Black: " + str.substring(0, str.indexOf("!")));
                }
                if (i < roomcount - 1) {
                    str = str.substring(str.indexOf("#") + 1);
                }
                else {
                    str = str.substring(str.indexOf("#"));
                }
                if (blackInfo[i].getText().equals("  Black: -") || whiteInfo[i].getText().equals("  White: -")) {
                    joinView[i].setText("Join");
                }
                else if (!blackInfo[i].getText().equals("  Black: -") && !whiteInfo[i].getText().equals("  White: -")) {
                    joinView[i].setText("Room Full");
                }
                if (joinView[i].getText().equals("Room Full")) {
                    joinView[i].setEnabled(false);
                }
                else {
                    joinView[i].setEnabled(!inQueue);
                }
            }
        }
        else {
            for (int i = 0; i < 10; i++) {
                roomNumber[i].setText("  Room #:");
                whiteInfo[i].setText("  White: -");
                blackInfo[i].setText("  Black: -");
                joinView[i].setText("Join");
                joinView[i].setEnabled(false);
            }
        }

        for (int i = 0; i < 4; i++) {
            newgame[i].setEnabled(!inQueue);
        }
        newgame[4].setEnabled(inQueue);
    }

    private void refreshDisplay() {
        try {
            Signin.client.sendTo("showrooms" + '\n');
        }
        catch (Exception e) {
            System.out.println(e + " refreshDisplay");
        }
    }

    public static void refreshDisplay1(String message) {
        displayRooms(message);
    }

    static void checkIfConnected(String name) {
        inQueue = false;
        Info.opponentName.setText(name.substring(0, name.indexOf("&")));
        boardS = name.substring(name.indexOf("&"));
        Launcher.gameStarted = true;
        Launcher.board = new Board("new" + boardS, null);
        Info.playerName.setText(Signin.signed_in_user);
        Info.InfoDisplay();
        Info.timerStart();
        Launcher.mode4();
    }

    public void vsai(String mode) {
        JOptionPane.showMessageDialog(Launcher.mainFrame, mode.equals("make") ? "Make your own board and try to win" : "Game vs AI");
        if (mode.equals("ai")) {
            Launcher.gameStarted = true;
            Info.timerStart();
        }
        Launcher.board = new Board(mode, null);
        Info.InfoDisplay();
        Launcher.mode4();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == load) {
            int returnVal = fc.showOpenDialog(Launcher.infoPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();
                String str = "";
                try {
                    str = new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())));
                    Signin.client.sendTo("newwhite" + Signin.signed_in_user + "&" + str + '\n');
                }
                catch (Exception e1) {

                }
                player = "white";
                inQueue = true;
            }
        }
        try {
            if (e.getSource() == newgame[0]) {
                Signin.client.sendTo("newwhite" + Signin.signed_in_user + '\n');
                player = "white";
                inQueue = true;
            }
            else if (e.getSource() == newgame[1]) {
                Signin.client.sendTo("newblack" + Signin.signed_in_user + '\n');
                player = "black";
                inQueue = true;
            }
            else if (e.getSource() == newgame[2]) {
                vsai("ai");
            }
            else if (e.getSource() == newgame[3]) {
                Launcher.currentmode = "make";
                vsai("make");
            }
            else if (e.getSource() == newgame[4]) {
                inQueue = false;
                timer.cancel();
                timer.purge();
                Signin.client.sendTo("close\n");
            }
            else {
                for (int i = 0; i < 10; i++) {
                    if (e.getSource() == joinView[i]) {
                        if (joinView[i].getText().equals("Join")) {
                            Signin.client.sendTo("join" + roomNumber[i].getText().substring(9) + Signin.signed_in_user + '\n');
                        }
                        else if (joinView[i].getText().equals("View")) {
                            Signin.client.sendTo("view" + roomNumber[i].getText().substring(9) + Signin.signed_in_user + '\n');
                        }
                        break;
                    }
                }
            }
        }
        catch (Exception c) {
            System.out.println("1 " + c);
        }
    }

}
