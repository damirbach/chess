import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class HelpPath implements MouseListener {  // move action

    Board b1;
    public MovingPiece mp = new MovingPiece();

    public HelpPath() {
        b1 = new Board();
    }

    @Override
    public void mouseClicked(MouseEvent e) {  //nothing
    }

    @Override
    public void mousePressed(MouseEvent e) {//nothing
    }

    @Override
    public void mouseReleased(MouseEvent e) {//nothing
    }

    @Override
    public void mouseEntered(MouseEvent e) {

        if (Board.gameReady && Board.helpCord) {
            ChessPiece cp = (ChessPiece) e.getSource();
            b1.setColorPath(cp);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (Board.gameReady && Board.helpCord) {
            b1.resetBoardColor();
        }
    }
}
