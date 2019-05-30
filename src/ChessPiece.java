import javax.swing.*;

public class ChessPiece extends JButton {

    private int chessPieceRow, chessPieceCol;
    private boolean selected = false;

    public ChessPiece(int chessRow, int chessCol) {
        super();
        chessPieceRow = chessRow;
        chessPieceCol = chessCol;
    }

    public int getChessRow() { // get row number
        return chessPieceRow;
    }

    public int getChessCol() { // get column number
        return chessPieceCol;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean select) {
        selected = select;
    }
}
