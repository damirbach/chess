public class MovingPiece {  // selecting piece coordinats

    private int selectedPieces = 0, col1 = 8, row1 = 8, col2 = 8, row2 = 8;

    public int getSelectedPieces() {
        return selectedPieces;
    }

    public void resetSelectedPieces() {
        selectedPieces = 0;
    }

    public int getCol1() {
        return col1;
    }

    public int getRow1() {
        return row1;
    }

    public int getCol2() {
        return col2;
    }

    public int getRow2() {
        return row2;
    }

    public void setColRow1(int row, int col) {
        col1 = col;
        row1 = row;
    }

    public void setColRow2(int row, int col) {
        col2 = col;
        row2 = row;
    }

    public void setSelectedPieces() {
        selectedPieces++;
        if (selectedPieces >= 2) {
            selectedPieces = 0;
            col1 = row1 = col2 = row2 = 8;
        }
    }

    public boolean notSameSquare() //not pressing on same square twice
    {
        return (col1 == col2 && row1 != row2) || (col1 != col2 && row1 == row2) || (col1 != col2 && row1 != row2);
    }
}
