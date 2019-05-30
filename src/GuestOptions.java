import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;

public class GuestOptions implements ActionListener {

    public static JButton vsAI = new JButton("Play vs AI");
    public static JButton MakevsAI = new JButton("<html><p style='text-align:center;'>Custom<br /><br />Situation<br /><br />vs AI</p></html>");
    private static JButton btnBack = new JButton();
    private JLabel guestMenu = new JLabel("Guest Menu");
    private JLabel or = new JLabel(" - OR -");
    private JButton load = new JButton("Load Game");
    private JFileChooser fc = new JFileChooser();

    public GuestOptions() {   // guest/offline game option screen
        vsAI.addActionListener(this);
        MakevsAI.addActionListener(this);
        btnBack.addActionListener(this);
        load.addActionListener(this);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));

        load.setToolTipText("load game vs comp");
        MakevsAI.setToolTipText("make starting board vs comp");

        Launcher.guestOptionsPanel.add(or);
        Launcher.guestOptionsPanel.add(guestMenu);
        Launcher.guestOptionsPanel.add(btnBack);
        Launcher.guestOptionsPanel.add(vsAI);
        Launcher.guestOptionsPanel.add(MakevsAI);
        Launcher.guestOptionsPanel.add(load);

        or.setBounds(750, 350, 100, 100);
        guestMenu.setBounds(650, 50, 500, 100);
        btnBack.setBounds(50, 50, 100, 100);
        vsAI.setBounds(400, 300, 200, 100);
        MakevsAI.setBounds(1000, 300, 200, 200);
        load.setBounds(400, 400, 200, 100);

        vsAI.setFont(new Font("Aharoni", Font.PLAIN, 28));
        MakevsAI.setFont(vsAI.getFont());
        or.setFont(new Font("Aharoni", Font.BOLD, 34));
        guestMenu.setFont(new Font("Aharoni", Font.BOLD, 48));
        load.setFont(vsAI.getFont());

        btnBack.setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/back.png")));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vsAI) {   // press vs AI button
            JOptionPane.showMessageDialog(null, "vs AI");
            Launcher.gameStarted = true;
            Info.timerStart();
            Launcher.board = new Board("ai", null);
            Info.InfoDisplay();
            Launcher.mode4();
        }
        else if (e.getSource() == MakevsAI) { // press Make vs AI button
            Launcher.board = new Board("make", null);
            Launcher.currentmode = "make";
            Info.InfoDisplay();
            Launcher.mode4();
        }
        else if (e.getSource() == load) {  // load saved game vs ai
            int returnVal = fc.showOpenDialog(Launcher.infoPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();
                Launcher.gameStarted = true;
                Info.timerStart();
                Launcher.board = new Board("file", selectedFile);
                Info.InfoDisplay();
                Launcher.mode4();
            }
        }
        else if (e.getSource() == btnBack) {
            Launcher.mode1();
        }
    }
}
