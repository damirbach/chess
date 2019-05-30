import javax.swing.ImageIcon;

public class Piece {

    public void pieces() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (i) { //switch 1
                    case 0: {
                        switch (j) { //switch 2.1
                            case 7:
                            case 0:
                                Board.Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/rook_white.png")));
                                break;
                            case 6:
                            case 1:
                                Board.Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/knight_white.png")));
                                break;
                            case 5:
                            case 2:
                                Board.Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/bishop_white.png")));
                                break;
                            case 3:
                                Board.Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/king_white.png")));
                                break;
                            case 4:
                                Board.Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/queen_white.png")));
                                break;
                            default:
                                break;
                        }
                        break;  // end switch 2.1
                    }
                    case 1:
                        Board.Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/soldier_white.png")));
                        break;
                    case 6:
                        Board.Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/soldier_black.png")));
                        break;
                    case 7: {
                        switch (j) { //switch 2.2
                            case 7:
                            case 0:
                                Board.Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/rook_black.png")));
                                break;
                            case 6:
                            case 1:
                                Board.Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/knight_black.png")));
                                break;
                            case 5:
                            case 2:
                                Board.Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/bishop_black.png")));
                                break;
                            case 3:
                                Board.Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/king_black.png")));
                                break;
                            case 4:
                                Board.Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/queen_black.png")));
                                break;
                            default:
                                break;
                        }
                        break; //end switch 2.2
                    }
                    default:
                        break;
                }   //end switch 1
            }
        }
    }
}
