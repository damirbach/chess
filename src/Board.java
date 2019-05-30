import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;

public class Board implements ActionListener {

    public static ChessPiece[][] Squares = new ChessPiece[8][8];
    public MovingPiece mp = new MovingPiece();
    public static MoveCalc b;
    public static TCPClient client;
    public static String player;
    private ChessPiece cp;
    public static boolean game;
    public static String str = "";
    public static boolean gameReady;
    public static boolean helpCord = true;
    public static boolean promotion = false;
    public static ReceiveFromServer t1;
    private static String m;
    private File loadFile;
    private String boardStateStr = "";

    public Board() {
    }

    public Board(String mode, File fl) {
        loadFile = fl;
        m = mode;
        if (!mode.equals("ai") && mode.substring(0, 3).equals("new")) {
            boardStateStr = mode.substring(mode.indexOf("&") + 1);
            mode = mode.substring(0, mode.indexOf("&"));
        }
        initialize(mode);
    }

    public void removeObjects() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Launcher.boardPanel.remove(Squares[i][j]);
            }
        }
    }

    public void initialize(String mode) {
        try {
            try {
                removeObjects();
            }
            catch (Exception c) {
            }
            switch (mode) {
                case "new":
                    player = WaitingRoom.player;    // getting player color
                    gameReady = true;
                    createBoard();
                    if (boardStateStr.equals("")) // board state is empty=new game
                    {
                        b = new MoveCalc();
                        new Piece().pieces();
                    }
                    else {
                        b = new MoveCalc(boardStateStr, null);
                        b.loadFromServer(boardStateStr);
                        setBoard();
                    }
                    game = true;
                    break;
                case "make":
                    b = new MoveCalc("empty", null);
                    player = "white";
                    createBoard();
                    gameReady = false;
                    break;
                case "ai":
                    gameReady = true;
                    b = new MoveCalc();
                    player = "white";
                    createBoard();
                    new Piece().pieces();
                    break;
                case "file":
                    b = new MoveCalc("file", loadFile);
                    player = "white";
                    createBoard();
                    gameReady = true;
                    setBoard();
                    break;
                default:
                    break;
            }
        } //end try
        catch (Exception exc) {
            JOptionPane.showMessageDialog(null, "server not found");   // server is down
        }
    }

    private void createBoard() {     // create board by color of player
        if (player.equals("black")) // create board for black
        {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) { //for j
                    Squares[i][j] = new ChessPiece(i, j);
                    Squares[i][j].setBackground(i % 2 != j % 2 ? Color.BLACK : Color.WHITE);
                    Squares[i][j].addActionListener(this);
                    Squares[i][j].addMouseListener(new HelpPath());
                    Squares[i][j].setPreferredSize(new Dimension(100, 100));
                    Launcher.boardPanel.add(Squares[i][j]);
                } //end for j
            }
        }
        else // create board for white
        if (player.equals("white")) {
            for (int i = 7; i >= 0; i--) {
                for (int j = 7; j >= 0; j--) {//for j
                    Squares[i][j] = new ChessPiece(i, j);
                    Squares[i][j].setBackground(i % 2 != j % 2 ? Color.BLACK : Color.WHITE);
                    Squares[i][j].addActionListener(this);
                    Squares[i][j].addMouseListener(new HelpPath());
                    Squares[i][j].setPreferredSize(new Dimension(100, 100));
                    Launcher.boardPanel.add(Squares[i][j]);
                }//end for j
            }
        }
    }

    public static void resetBoardColor() {  // reset color of board back to begin color
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Squares[i][j].setBackground(i % 2 != j % 2 ? Color.BLACK : Color.WHITE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        cp = (ChessPiece) e.getSource(); // global var
        if (!gameReady && !promotion) {
            int i = cp.getChessRow();
            int j = cp.getChessCol();
            if (Info.timerLabel.getText().equals("Select Piece") || Info.playerTurn.getText().equals("Select Color")) {
                JOptionPane.showMessageDialog(Launcher.mainFrame, "Must select color and piece");
            }
            else {
                addPiece(Info.playerTurn.getText(), Info.getLabel3().charAt(0), i, j);
            }
        }
        else if (!game && m.equals("new")) {
            JOptionPane.showMessageDialog(null, "game not found");
        }
        else if (gameReady) { //check if selected piece is of the same player's turn, prevent selection of empty square
            if (mp.getSelectedPieces() == 0 && b.getPlayer().equals(b.getPieceColor(cp.getChessRow() + 1, cp.getChessCol() + 1))) {
                firstSelection();
            }
            else if (mp.getSelectedPieces() == 1 && mp.notSameSquare()) //check if clicked before and not double click on same square
            {
                secondSelection();
            }
        }
    }

    private void firstSelection() {   // first click on piece
        mp.setColRow1(cp.getChessRow(), cp.getChessCol());
        mp.setSelectedPieces();
        if (helpCord) {
            for (int i = 0; i < 8; i++) // green square for legal move
            {
                for (int j = 0; j < 8; j++) {
                    if (b.legalMove(mp.getRow1() + 1, mp.getCol1() + 1, i + 1, j + 1)) // red square for possible attack on you
                    {
                        b.preNextMove(mp.getRow1() + 1, mp.getCol1() + 1, i + 1, j + 1, false);
                        if (b.isPieceAttackable(i + 1, j + 1)) {
                            Squares[i][j].setBackground(Color.RED);
                        }
                        else {
                            Squares[i][j].setBackground(Color.GREEN);
                        }
                        b.copyall();
                    }
                }
            }
        }
    }

    private void secondSelection() {   // decide where to moev with piece, click on tile
        mp.setColRow2(cp.getChessRow(), cp.getChessCol());
        if (b.preNextMove(mp.getRow1() + 1, mp.getCol1() + 1, mp.getRow2() + 1, mp.getCol2() + 1, true)) // check if move is legal and if king is not under check after move
        {
            b.pieceEat(mp.getRow1() + 1, mp.getCol1() + 1, mp.getRow2() + 1, mp.getCol2() + 1);
            if(Squares[mp.getRow2()][mp.getCol2()].getIcon()!=null)
            {
               pieceAttack();
            }
            Squares[mp.getRow2()][mp.getCol2()].setIcon(Squares[mp.getRow1()][mp.getCol1()].getIcon());
            Squares[mp.getRow1()][mp.getCol1()].setIcon(null);
            castling(mp.getRow1(), mp.getCol1(), mp.getRow2(), mp.getCol2());
            b.checkKingPlayer(mp.getRow2(), mp.getCol2());
            if (b.getCheckOnKing()) //check if enemy king under attack after moving the piece, MoveCalc.java functions
            {
                JOptionPane.showMessageDialog(null, "check by " + b.getPlayer());
            }
            b.changePlayer(); //player's end turn, change color
            Info.counter = 60; // reset timer, show current player color
            resetBoardColor();    // get color back black or white
            if (m.equals("make") || m.equals("ai") || m.equals("file")) {
                AImove(b.randMove());
                if(!b.isWhiteKingSafe())
                    JOptionPane.showMessageDialog(null, "check by " + b.getPlayer());
            }
            else {
                try {
                    Signin.client.sendTo((mp.getRow1() + 1) + "" + (mp.getCol1() + 1) + "" + (mp.getRow2() + 1) + "" + (mp.getCol2() + 1) + '\n'); // send to server your move
                   // JOptionPane.showMessageDialog(null, "your move was sent");
                   // System.out.println("move sent");
                    Thread.sleep(5000);
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "connection lost");   // server is down
                }
            }
            mp.setSelectedPieces();
        }
        else {
            if (b.getCheckOnKing()) {
                JOptionPane.showMessageDialog(null, "still check by enemy");
                mp.setSelectedPieces();
                mp.resetSelectedPieces();
                resetBoardColor();
            }
            else {
                if (!b.ableToMove(mp.getRow1() + 1, mp.getCol1() + 1, mp.getRow2() + 1, mp.getCol2() + 1)) {
                    mp.setSelectedPieces();
                    JOptionPane.showMessageDialog(null, "wrong move");
                    mp.resetSelectedPieces();
                    resetBoardColor();
                }
            }
        }
    }

    public static void waitForAnswer(String msg) {   // analyze string from server
        try {
            if (msg.equals("g")) // if other player left
            {
                JOptionPane.showMessageDialog(Launcher.mainFrame, "other player left, closing game");
                game = false;
                Launcher.gameStarted = false;
                Launcher.mode3();
            }
            else if (msg.equals("*")) {
                JOptionPane.showMessageDialog(Launcher.mainFrame, "other player surrendered, closing game");
                game = false;
                Launcher.gameStarted = false;
                Launcher.mode3();
            }
            else if (!msg.equals("")) {
                int row1 = Integer.parseInt(msg);    // answer from server
                int col2 = row1 % 10;        // translate the answer from server into cords
                int row2 = row1 / 10 % 10;
                int col1 = row1 / 100 % 10;
                row1 /= 1000;
                b.pieceEat(row1, col1, row2, col2);
                if(Squares[row2 - 1][col2 - 1].getIcon()!=null)
                {
                    pieceAttack();
                }
                Squares[row2 - 1][col2 - 1].setIcon(Squares[row1 - 1][col1 - 1].getIcon());
                Squares[row1 - 1][col1 - 1].setIcon(null);
                b.checkKingPlayer(row2 - 1, col2 - 1);
                if (b.getCheckOnKing()) //check if enemy king under attack after moving the piece, MoveCalc.java functions
                {
                    JOptionPane.showMessageDialog(null, "check by " + b.getPlayer());
                }
                b.changePlayer(); //player's end turn, change color
                JOptionPane.showMessageDialog(null, "your turn");
                Info.counter = 60; // reset timer, show current player color
            }
        }
        catch (Exception exc) {
            JOptionPane.showMessageDialog(null, "connection failed " + exc.toString());   // server is down
        }
    }

    public static void showMSG(String st) {
        JOptionPane.showMessageDialog(null, st);
    }

    public void setColorPath(ChessPiece cp) {   // set path fro mouse over piece
        mp.setColRow1(cp.getChessRow(), cp.getChessCol());
        if (!b.getPieceColor(mp.getRow1() + 1, mp.getCol1() + 1).equals("")) {
            if (b.getPlayer().equals(b.getPieceColor(cp.getChessRow() + 1, cp.getChessCol() + 1))) {
                path();
            }
            else {   // enemy path piece
                b.changePlayer();
                path();
                b.changePlayer();
            }
        }
    }

    public void path() {   // show where can piece go, and where it may be eaten
        for (int i = 0; i < 8; i++) // green square for legal move
        {
            for (int j = 0; j < 8; j++) {
                if (b.legalMove(mp.getRow1() + 1, mp.getCol1() + 1, i + 1, j + 1)) // red square for possible attack on you
                {
                    b.preNextMove(mp.getRow1() + 1, mp.getCol1() + 1, i + 1, j + 1, false);
                    if (b.isPieceAttackable(i + 1, j + 1)) {
                        Squares[i][j].setBackground(Color.RED);
                    }
                    else {
                        Squares[i][j].setBackground(Color.GREEN);
                    }
                    b.copyall();
                }
            }
        }
    }

    public static void AImove(AImove move) //get choosen move fro comp
    {
        if (move!=null) {
            //if (m.equals("make") || m.equals("ai") || m.equals("file"))
            while(b.getPieceColor(move.getCord("from").getX(),move.getCord("from").getY()).equals("white"))
                move=b.randMove();
            b.pieceEat(move.getCord("from").getX(), move.getCord("from").getY(), move.getCord("to").getX(), move.getCord("to").getY());
            if(Squares[move.getCord("to").getX() - 1][move.getCord("to").getY() - 1].getIcon()!=null)
            {
               pieceAttack();
            }
            Squares[move.getCord("to").getX() - 1][move.getCord("to").getY() - 1].setIcon(Squares[move.getCord("from").getX() - 1][move.getCord("from").getY() - 1].getIcon());
            Squares[move.getCord("from").getX() - 1][move.getCord("from").getY() - 1].setIcon(null);
            //if (m.equals("make") || m.equals("ai") || m.equals("file"))
            b.changePlayer();
        }
        else  JOptionPane.showMessageDialog(null, "player wins");
    }

    private void castling(int x1, int y1, int x2, int y2) { // check and perform castling
        if (x1 == x2 && x1 == 0 && y1 == 3) {
            if (y1 - y2 == 2) {
                Squares[0][2].setIcon(Squares[0][0].getIcon());
                Squares[0][0].setIcon(null);
                b.doneCastling();
            }
            if (y2 - y1 == 2) {
                Squares[0][4].setIcon(Squares[0][7].getIcon());
                Squares[0][7].setIcon(null);
                b.doneCastling();
            }
        }
    }

    private void addPiece(String p, char c, int i, int j) {  // add piece to board by selected var
        if (b.addpiece(c, i, j, p)) {
            switch (c) {
                case 'p':
                    Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/soldier_" + p + ".png")));
                    break;
                case 'r':
                    Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/rook_" + p + ".png")));
                    break;
                case 'k':
                    Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/knight_" + p + ".png")));
                    break;
                case 'b':
                    Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/bishop_" + p + ".png")));
                    break;
                case 'q':
                    Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/queen_" + p + ".png")));
                    break;
                case 'K':
                    Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/king_" + p + ".png")));
                    break;
                default:
                    break;
            }
        }
        else {
            switch (c) {
                case 'p':
                    JOptionPane.showMessageDialog(null, "only 8 pawns");
                    break;
                case 'r':
                    JOptionPane.showMessageDialog(null, "only 2 rooks");
                    break;
                case 'k':
                    JOptionPane.showMessageDialog(null, "only 2 knights");
                    break;
                case 'b':
                    JOptionPane.showMessageDialog(null, "only 2 bishops, one on white cell, second on black cell");
                    break;
                case 'q':
                    JOptionPane.showMessageDialog(null, "only 1 queen");
                    break;
                case 'K':
                    JOptionPane.showMessageDialog(null, "only 1 king");
                    break;
            }
        }
    }

    private void setBoard() {    // set board by loaded file
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (MoveCalc.arr11[i][j].getName() != 'x') {
                    switch (MoveCalc.arr11[i][j].getName()) {
                        case 'p':
                            Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/soldier_" + MoveCalc.arr11[i][j].getPlayer() + ".png")));
                            break;
                        case 'r':
                            Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/rook_" + MoveCalc.arr11[i][j].getPlayer() + ".png")));
                            break;
                        case 'k':
                            Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/knight_" + MoveCalc.arr11[i][j].getPlayer() + ".png")));
                            break;
                        case 'b':
                            Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/bishop_" + MoveCalc.arr11[i][j].getPlayer() + ".png")));
                            break;
                        case 'q':
                            Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/queen_" + MoveCalc.arr11[i][j].getPlayer() + ".png")));
                            break;
                        case 'K':
                            Squares[i][j].setIcon(new ImageIcon(getClass().getResource("/icons/NewFolder/king_" + MoveCalc.arr11[i][j].getPlayer() + ".png")));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    //animations
    
    private static void pieceAttack()
    {
        Random random = new Random();
        int r2 = random.nextInt(4)+1;
        JOptionPane opt=new JOptionPane(new ImageIcon(Board.class.getClass().getResource("/icons/NewFolder/"+r2+".gif")));
        opt.setBackground(Color.white);
        final JDialog dlg = opt.createDialog("");
        new Thread(new Runnable()
        {
          public void run()
          {
            try
            {
              Thread.sleep(1500);
              dlg.dispose();
            }
            catch ( Throwable th )
            {
            }
          }
        }).start();
        dlg.setVisible(true);
    }
}