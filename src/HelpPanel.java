import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class HelpPanel implements ActionListener { // not added anywhere

    public HelpPanel() {
    }
// set help with path of pieces

    public static void setHelp() {
        if (Board.helpCord) {
            Board.helpCord = false;
        }
        else {
            Board.helpCord = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setHelp();
        JOptionPane.showMessageDialog(null, Board.helpCord);
    }

}
