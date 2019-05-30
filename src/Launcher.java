
import icons.NewFolder.Icons;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import static javax.swing.WindowConstants.*;

public class Launcher {

    public static JFrame mainFrame = new JFrame("Chess");
    public static JPanel boardPanel = new JPanel();
    public static NewJPanel infoPanel;
    public static NewJPanel signinPanel;
    public static NewJPanel guestOptionsPanel;
    public static NewJPanel waitingPanel;
    public static NewJPanel adminZone;
    public static Board board;
    public static Info _infoPanel = new Info();
    public static boolean firstInit = false;
    public static boolean gameStarted = false;
    private static int currentMode;
    public static String currentmode = "";
    static WindowListener exitListener = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent e) {
            closeGame();
        }
    };

    public static void panelSettings() {
        boardPanel.setLayout(new GridLayout(8, 8));
        infoPanel.setLayout(null);
        signinPanel.setLayout(null);
        signinPanel.setSize(new Dimension(1600, 800));
        guestOptionsPanel.setLayout(null);
        waitingPanel.setLayout(null);
        adminZone.setLayout(null);
    }

    public static void frameSettings() {
        mainFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(exitListener);
        mainFrame.setMinimumSize(new Dimension(1600, 800));
        mainFrame.setResizable(true);
        mainFrame.setLayout(new GridLayout());
    }

    public static void main(String[] args) {
        infoPanel = new NewJPanel(Icons.class.getResource("1_2.jpg"));
        waitingPanel = new NewJPanel(Icons.class.getResource("1_1.jpg"));
        guestOptionsPanel = new NewJPanel(Icons.class.getResource("2.jpg"));
        signinPanel = new NewJPanel(Icons.class.getResource("1.jpg"));
        adminZone = new NewJPanel(Icons.class.getResource("1.jpg"));

        frameSettings();
        panelSettings();
        mode1();
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public static void mode1() { //signin screen
        currentMode = 1;
        mainFrame.add(signinPanel);
        mainFrame.remove(boardPanel);
        mainFrame.remove(infoPanel);
        mainFrame.remove(waitingPanel);
        mainFrame.remove(guestOptionsPanel);
        mainFrame.remove(adminZone);
        mainFrame.revalidate();
        mainFrame.repaint();
        new Signin();
    }

    public static void mode2() { //guest option screen
        currentMode = 2;
        mainFrame.add(guestOptionsPanel);
        mainFrame.remove(boardPanel);
        mainFrame.remove(infoPanel);
        mainFrame.remove(waitingPanel);
        mainFrame.remove(signinPanel);
        mainFrame.remove(adminZone);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public static void mode3() { //waiting+info screen
        if (currentMode == 4) {
            mainFrame.remove(infoPanel);
        }
        currentMode = 3;
        mainFrame.remove(signinPanel);
        mainFrame.remove(boardPanel);
        mainFrame.add(waitingPanel);
        mainFrame.add(infoPanel);
        mainFrame.remove(guestOptionsPanel);
        mainFrame.remove(adminZone);
        Info.InfoDisplay();
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public static void mode4() { //game+info screen
        if (currentMode == 3) {
            mainFrame.remove(infoPanel);
        }
        currentMode = 4;
        mainFrame.remove(signinPanel);
        mainFrame.add(boardPanel);
        mainFrame.add(infoPanel);
        mainFrame.remove(waitingPanel);
        mainFrame.remove(guestOptionsPanel);
        mainFrame.remove(adminZone);
        Info.InfoDisplay();
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public static void mode5() { //game+info screen
        currentMode = 5;
        mainFrame.remove(signinPanel);
        mainFrame.remove(boardPanel);
        mainFrame.remove(infoPanel);
        mainFrame.remove(waitingPanel);
        mainFrame.remove(guestOptionsPanel);
        mainFrame.add(adminZone);
        //Info.InfoDisplay();
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public static void repaint() {
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public static void closeGame() { // pressed to close window
        int confirm = JOptionPane.showOptionDialog(
                mainFrame, "Are You Sure to Close Application?",
                "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (confirm == 0) {
            try {
                if (!Signin.signed_in_user.toLowerCase().equals("guest")) {
                    Signin.client.sendTo("*" + Signin.signed_in_user + '\n');
                    Signin.client.sendTo("@" + Signin.signed_in_user + " has disconnected\n");
                    Signin.client.endConnection();
                }
                gameStarted = false;
            }
            catch (Exception exc) {
                System.out.println("exitlistener " + exc);
            }
            System.exit(0);
        }
    }
}
