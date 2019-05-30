
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.Timer;
import javax.swing.*;
import static javax.swing.ScrollPaneConstants.*;

public class Info implements ActionListener {
    public static int counter = 180;
    static boolean isIt = false;
    static JLabel opponentName = new JLabel();
    static JLabel playerName = new JLabel();
    public static TimerTask task;
    public static Timer timer;
    public static JLabel timerLabel = new JLabel("Select Piece");
    public static JLabel playerTurn = new JLabel("Select Color");
    private static JButton surrender = new JButton("Surrender");
    static JButton helpbutton = new JButton("Help");
    static JButton addPawn = new JButton("pawn");
    static JButton addRook = new JButton("rook");
    static JButton addKnight = new JButton("knight");
    static JButton addBishop = new JButton("bishop");
    static JButton addQueen = new JButton("queen");
    static JButton addKing = new JButton("King");
    static JButton setColor = new JButton("Change Color");
    static JButton startGame = new JButton("Start Game");
    static JTextField t1 = new JTextField("");
    static JButton btnSend = new JButton("SEND");
    static JTextArea chatWin = new JTextArea("");
    static JScrollPane chatWinScroll = new JScrollPane(chatWin);
    static JTextField filePath = new JTextField("");
    static JButton btnSave = new JButton("Save");
    static JButton signOut = new JButton("Sign Out");
    public static JLabel winLose=new JLabel();

    public Info() {
        surrender.addActionListener(this);
        btnSend.addActionListener(this);
        addPawn.addActionListener(new AddPiece());
        addRook.addActionListener(new AddPiece());
        addKnight.addActionListener(new AddPiece());
        addBishop.addActionListener(new AddPiece());
        addQueen.addActionListener(new AddPiece());
        addKing.addActionListener(new AddPiece());
        setColor.addActionListener(new PieceColor());
        startGame.addActionListener(new StartGame());
        helpbutton.addActionListener(new HelpPanel());
        btnSave.addActionListener(this);
        signOut.addActionListener(this);
        //actionListener

        //tooltips
        surrender.setToolTipText("<html>Surrender to your opponent<br>Note in vs player:<br>Get +1 to loss count<br>Opponent get +1 to win count<html>");
        helpbutton.setToolTipText("enable/disable help with piece move");

        //setBounds
        addPawn.setBounds(50, 350, 200, 25);
        addRook.setBounds(50, 375, 200, 25);
        addKnight.setBounds(50, 400, 200, 25);
        addBishop.setBounds(50, 425, 200, 25);
        addQueen.setBounds(50, 450, 200, 25);
        addKing.setBounds(50, 475, 200, 25);
        setColor.setBounds(50, 500, 200, 25);
        startGame.setBounds(50, 525, 200, 25);
        helpbutton.setBounds(50, 325, 150, 25);
        opponentName.setBounds(50, 50, 200, 50);
        playerTurn.setBounds(50, 100, 200, 50);
        timerLabel.setBounds(50, 150, 200, 50);
        playerName.setBounds(50, 200, 200, 200);
        t1.setBounds(480, 610, 300, 25);
        btnSend.setBounds(480, 640, 100, 40);
        btnSave.setBounds(200, 700, 100, 50);
        filePath.setBounds(100, 700, 100, 50);
        surrender.setBounds(400, 700, 200, 50);
        signOut.setBounds(650, 0, 100, 40);
        chatWinScroll.setBounds(480, 50, 300, 550);
        winLose.setBounds(150,200,200,200);
        //setBounds

        //Font+other
        btnSend.setFont(WaitingRoom.font);
        chatWin.setEditable(false);
        chatWin.setFont(new Font("Aharoni", Font.PLAIN, 16));
        chatWinScroll.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        surrender.setFont(btnSend.getFont());
        filePath.setFont(WaitingRoom.font2);
        btnSave.setFont(btnSend.getFont());
        playerName.setFont(WaitingRoom.font2);
        winLose.setFont(playerName.getFont());
        opponentName.setFont(WaitingRoom.font2);
        playerTurn.setFont(WaitingRoom.font2);
        timerLabel.setFont(new Font("Aharoni", Font.PLAIN, 24));
        signOut.setFont(new Font("Aharoni", Font.PLAIN, 14));
        //Font+other
        ////

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSend) {
            if (t1.getText().equals("")) {
                JOptionPane.showMessageDialog(Launcher.mainFrame, "Cannot send empty message");
            }
            else {
                chatWin.append("You: " + t1.getText() + '\n');
                try {
                    Signin.client.sendTo("@" + Signin.signed_in_user + ": " + t1.getText() + '\n');
                }
                catch (Exception ex) {
                }
                t1.setText("");
            }
        }
        else if (e.getSource() == surrender) {
            try {
                Signin.client.sendTo("surrender\n");
            }
            catch (Exception et) {
            }
            Board.game = false;
            Launcher.gameStarted = false;
            if (Signin.signed_in_user.toLowerCase().equals("guest")) {
                Launcher.mode2();
            }
            else {
                Launcher.mode3();
            }
            Launcher.currentmode = "";
            InfoDisplay();
        }
        else if (e.getSource() == btnSave) {
            try {
                if (filePath.getText().equals("")) {
                    JOptionPane.showMessageDialog(Launcher.mainFrame, "name save is empty");
                }
                else {
                    MoveCalc.saveToFile(filePath.getText() + ".txt");
                    JOptionPane.showMessageDialog(Launcher.mainFrame, filePath.getText() + " file saved");
                    filePath.setText("");
                }
            }
            catch (Exception et) {
            }
        }
        else if (e.getSource() == signOut) {
            _signOut();
        }
    }

    public static void chatWindow(String str) { // write into chat window
        chatWin.append(str + '\n');
    }

    public static void setLabel3(String s) {
        timerLabel.setText(s);
    }

    public static String getLabel3() {
        return timerLabel.getText();
    }

    public static void InfoDisplay() { //display info about rooms
        removeAll();
        playerName.setText(Signin.signed_in_user);
        if (!Signin.signed_in_user.equals("Guest")) {
            Launcher.infoPanel.add(t1);
            Launcher.infoPanel.add(btnSend);
            Launcher.infoPanel.add(chatWinScroll);
            Launcher.infoPanel.add(playerName);
            Launcher.infoPanel.add(opponentName);
            Launcher.infoPanel.add(winLose);
            if (!Launcher.gameStarted) {
                Launcher.infoPanel.add(signOut);
            }
        }
        Launcher.infoPanel.add(playerTurn);
        Launcher.infoPanel.add(timerLabel);
        Launcher.infoPanel.add(surrender);
        Launcher.infoPanel.add(helpbutton);
        Launcher.infoPanel.add(btnSave);
        Launcher.infoPanel.add(filePath);
        

        opponentName.setVisible(Launcher.gameStarted);
        //playerName.setVisible(Launcher.gameStarted);
        playerTurn.setVisible(Launcher.gameStarted);
        timerLabel.setVisible(Launcher.gameStarted);
        surrender.setVisible(Launcher.gameStarted);
        surrender.setEnabled(Launcher.gameStarted);
        helpbutton.setVisible(Launcher.gameStarted);
        helpbutton.setEnabled(Launcher.gameStarted);
        btnSave.setVisible(Launcher.gameStarted);
        filePath.setVisible(Launcher.gameStarted);
        btnSave.setEnabled(Launcher.gameStarted);
        filePath.setEnabled(Launcher.gameStarted);
        if (Launcher.currentmode.equals("make")) {
            Launcher.infoPanel.add(addPawn);
            Launcher.infoPanel.add(addRook);
            Launcher.infoPanel.add(addKnight);
            Launcher.infoPanel.add(addBishop);
            Launcher.infoPanel.add(addQueen);
            Launcher.infoPanel.add(addKing);
            Launcher.infoPanel.add(setColor);
            Launcher.infoPanel.add(startGame);
            timerLabel.setVisible(true);
            playerTurn.setVisible(true);
        }
    }

    public static void timerStart() {
        timer = new Timer(); //new timer

        counter = 60; //setting the counter to 10 sec
        task = new TimerTask() {
            public void run() {
                if (Launcher.gameStarted) {
                    playerTurn.setText(Board.b.getPlayer() + " Player Turn");
                    timerLabel.setText(Integer.toString(counter)); //the timer lable to counter.
                    counter--;
                    if (counter == 0) {
                        
                        JOptionPane.showMessageDialog(null, "Time Out, Turn Ended");
//                        String you=Board.b.getPlayer();
//                        String enemy=you.equals("white")?"black":"white";
//                        AImove tmp1=Board.b.timeOutMove(you, enemy);
//                        Board.AImove(tmp1);
                        //func1(tmp1);
                        counter = 60;
                        Board.b.changePlayer();
                    }
                }
                else {
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 1000, 1000); // =  timer.scheduleAtFixedRate(task, delay, period);
    }

    private static void func1(AImove aim)
    {
        Board.AImove(aim);
    }
    
    private static void removeAll() { //remove/dispose all components
        Launcher.infoPanel.remove(opponentName);
        //Launcher.infoPanel.remove(playerName);
        Launcher.infoPanel.remove(timerLabel);
        Launcher.infoPanel.remove(playerTurn);
        Launcher.infoPanel.remove(surrender);
        Launcher.infoPanel.remove(helpbutton);
        Launcher.infoPanel.remove(addPawn);
        Launcher.infoPanel.remove(addRook);
        Launcher.infoPanel.remove(addKnight);
        Launcher.infoPanel.remove(addBishop);
        Launcher.infoPanel.remove(addQueen);
        Launcher.infoPanel.remove(addKing);
        Launcher.infoPanel.remove(setColor);
        Launcher.infoPanel.remove(startGame);
        Launcher.infoPanel.remove(t1);
        Launcher.infoPanel.remove(btnSend);
        Launcher.infoPanel.remove(chatWinScroll);
        Launcher.infoPanel.remove(filePath);
        Launcher.infoPanel.remove(btnSave);
        Launcher.infoPanel.remove(signOut);
    }

    void _signOut() { // pressed to close window
        int confirm = JOptionPane.showOptionDialog(
                Launcher.mainFrame, "Are You Sure to Sign Out?",
                "Sign Out Confirmation", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (confirm == 0) {
            try {
                if (!Signin.signed_in_user.toLowerCase().equals("guest")) {
                    Signin.client.sendTo("*" + Signin.signed_in_user + '\n');
                    Signin.client.sendTo("@" + Signin.signed_in_user + " has disconnected\n");
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
}
