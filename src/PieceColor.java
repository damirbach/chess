import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PieceColor implements ActionListener {
// change color of piece in make option

    @Override
    public void actionPerformed(ActionEvent e) {
        if (Info.playerTurn.getText() == "white") {
            Info.playerTurn.setText("black");
        }
        else {
            Info.playerTurn.setText("white");
        }
    }
}
