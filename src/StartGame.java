import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class StartGame implements ActionListener {
// start game when in make option

    @Override
    public void actionPerformed(ActionEvent e) {
        String s = "";
        if (!MoveCalc.whiteKing) {
            s += "white\\";
        }
        if (!MoveCalc.blackKing) {
            s += "black";
        }
        if (s.equals("")) {
            JOptionPane.showMessageDialog(null, "start game");
            Launcher.gameStarted = true;
            Board.gameReady = true;
            Info.startGame.setEnabled(false);
            Launcher.currentmode = "";
            Info.InfoDisplay();
            Info.timerStart();
        }
        else {
            JOptionPane.showMessageDialog(null, s + " is missing");
        }
    }
}
