import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MoveCalc {

    private PieceInfo[][] arr = new PieceInfo[8][8]; //2d array for chess board
    private PieceInfo[][] arrtmp = new PieceInfo[8][8];
    private static PieceInfo[] white = new PieceInfo[16]; //p,p,p,p,p,p,p,p,r,r,k,k,b,b,q,K
    private static PieceInfo[] black = new PieceInfo[16];
    private PieceInfo[] whiteNext = new PieceInfo[16];
    private PieceInfo[] blackNext = new PieceInfo[16];
    private boolean checkOnKing;
    private String playerColor; //white or black
    private ArrayList<AImove> AImove = new ArrayList<AImove>(); //list of moves for AI
    private ArrayList<AImove> AImove2 = new ArrayList<AImove>(); //list of moves for AI
    private ArrayList<AImove> AImove3 = new ArrayList<AImove>(); //list of moves for AI
    private ArrayList<AImove> AImoveAtt = new ArrayList<AImove>(); //list of moves for AI, eat piece
    private ArrayList<AImove> AImoveBest = new ArrayList<AImove>(); //list of moves for AI, max AI min Player
    public static boolean whiteKing = false;
    public static boolean blackKing = false;
    public static boolean whiteBishop1 = true;
    public static boolean whiteBishop2 = true;
    public static boolean blackBishop1 = true;
    public static boolean blackBishop2 = true;
    private boolean castling = false;
    public static PieceInfo[][] arr11 = new PieceInfo[8][8];
    private ArrayList<Node> advMove=new ArrayList<Node>();
     private ArrayList<Node> advMove2=new ArrayList<Node>();
    private Node nodeTree=new Node();
    private AImove twoStepMove=null;
    private boolean twoStep=true;

    public MoveCalc(String s, File f) {   // construct
        for (int i = 0; i < 8; i++) //create board of empty pieces, 'x'
        {
            for (int j = 0; j < 8; j++) {
                arr[i][j] = new PieceInfo();
            }
        }
        checkOnKing = false;
        playerColor = "white";
        for (int i = 0; i < 16; i++) {
            white[i] = new PieceInfo(false);
            black[i] = new PieceInfo(false);
        }
        if (s.equals("file")) {
            try {
                loadFromFile(f);
            }
            catch (Exception e1) {
            }
        }
    }

    public MoveCalc() //set board
    {
        checkOnKing = false;
        playerColor = "white"; //white always goes first
        for (int i = 0; i < 8; i++) //create board of empty pieces, 'x'
        {
            for (int j = 0; j < 8; j++) {
                arr[i][j] = new PieceInfo();
            }
        }
        for (int j = 0; j < 8; j++) {
            setName('p', 1, j); //pawn
            setName('p', 6, j); //pawn
            white[j] = new PieceInfo('p', 1, j, "white");
            black[j] = new PieceInfo('p', 6, j, "black");
        }
        setName('r', 0, 7); //rook
        white[8] = new PieceInfo('r', 0, 7, "white");
        setName('r', 7, 0);
        black[8] = new PieceInfo('r', 7, 0, "black");
        setName('r', 7, 7);
        black[9] = new PieceInfo('r', 7, 7, "black");
        setName('r', 0, 0);
        white[9] = new PieceInfo('r', 0, 0, "white");//rook
        setName('k', 0, 6); //knight
        white[10] = new PieceInfo('k', 0, 6, "white");
        setName('k', 7, 1);
        black[10] = new PieceInfo('k', 7, 1, "black");
        setName('k', 7, 6);
        black[11] = new PieceInfo('k', 7, 6, "black");
        setName('k', 0, 1);
        white[11] = new PieceInfo('k', 0, 1, "white");//knight
        setName('b', 0, 5);//bishop
        white[12] = new PieceInfo('b', 0, 5, "white");
        setName('b', 7, 2);
        black[12] = new PieceInfo('b', 7, 2, "black");
        setName('b', 7, 5);
        black[13] = new PieceInfo('b', 7, 5, "black");
        setName('b', 0, 2);
        white[13] = new PieceInfo('b', 0, 2, "white");//bishop
        setName('q', 0, 4);//queen
        white[14] = new PieceInfo('q', 0, 4, "white");
        setName('q', 7, 4);
        black[14] = new PieceInfo('q', 7, 4, "black");//queen
        setName('K', 0, 3); //King
        white[15] = new PieceInfo('K', 0, 3, "white");
        setName('K', 7, 3);
        black[15] = new PieceInfo('K', 7, 3, "black");//king
        setPlayers();
    }

    public boolean getCheckOnKing() {
        return this.checkOnKing;
    }

    public void setCheckOnKing(boolean b) {
        this.checkOnKing = b;
    }
    public PieceInfo[] getArr(String p)
    {
        if(p.equals("white")) return white;
        return black;
    }
    public void changePlayer() //change player color at end of turn, called in Board.java
    {
        if (playerColor.equals("white")) {
            playerColor = "black";
        }
        else {
            playerColor = "white";
        }
    }

    public static void saveToFile(String file) {  // save current board state on txt file
        try {
            File savefile = new File(file);
            BufferedWriter writer = new BufferedWriter(new FileWriter(savefile)); // write into file
            writer.append("white");   // first white
            for (int i = 0; i < 16; i++) {
                if (white[i].getingame()) {
                    writer.append("" + white[i].getName() + white[i].getX() + white[i].getY());
                }
            }
            writer.append("black");   // second black
            for (int i = 0; i < 16; i++) {
                if (black[i].getingame()) {
                    writer.append("" + black[i].getName() + black[i].getX() + black[i].getY());
                }
            }
            writer.close();
            savefile.setReadOnly();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    private void loadFromFile(File saveFile) throws Exception {   // load board state from txt file
        String str = new String(Files.readAllBytes(Paths.get(saveFile.getAbsolutePath())));
        String wh = str.substring(5, str.indexOf("black"));
        while (wh != "") {
            addpiece(wh.charAt(0), (int) ((wh.charAt(1)) - 48), (int) ((wh.charAt(2)) - 48), "white");
            if (wh.length() > 3) {
                wh = wh.substring(3);
            }
            else {
                wh = "";
            }
        }
        String bl = str.substring(str.indexOf("black") + 5);
        while (bl != "") {
            addpiece(bl.charAt(0), (int) ((bl.charAt(1)) - 48), (int) ((bl.charAt(2)) - 48), "black");
            if (bl.length() > 3) {
                bl = bl.substring(3);
            }
            else {
                bl = "";
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                arr11[i][j] = new PieceInfo();
            }
        }
        for (int i = 0; i < 16; i++) {
            if (white[i].getingame()) {
                arr11[white[i].getX()][white[i].getY()].setName(white[i].getName());
                arr11[white[i].getX()][white[i].getY()].setPlayer("white");
            }
            if (black[i].getingame()) {
                arr11[black[i].getX()][black[i].getY()].setName(black[i].getName());
                arr11[black[i].getX()][black[i].getY()].setPlayer("black");
            }
        }
    }

    public void loadFromServer(String str) throws Exception {  // load board state by string from server
        String wh = str.substring(5, str.indexOf("black"));
        while (wh != "") {
            addpiece(wh.charAt(0), (int) ((wh.charAt(1)) - 48), (int) ((wh.charAt(2)) - 48), "white");
            if (wh.length() > 3) {
                wh = wh.substring(3);
            }
            else {
                wh = "";
            }
        }
        String bl = str.substring(str.indexOf("black") + 5);
        while (bl != "") {
            addpiece(bl.charAt(0), (int) ((bl.charAt(1)) - 48), (int) ((bl.charAt(2)) - 48), "black");
            if (bl.length() > 3) {
                bl = bl.substring(3);
            }
            else {
                bl = "";
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                arr11[i][j] = new PieceInfo();
            }
        }
        for (int i = 0; i < 16; i++) {
            if (white[i].getingame()) {
                arr11[white[i].getX()][white[i].getY()].setName(white[i].getName());
                arr11[white[i].getX()][white[i].getY()].setPlayer("white");
            }
            if (black[i].getingame()) {
                arr11[black[i].getX()][black[i].getY()].setName(black[i].getName());
                arr11[black[i].getX()][black[i].getY()].setPlayer("black");
            }
        }
    }

    public String getPlayer() {
        return playerColor;
    }

    public String getPieceColor(int x1, int y1) {
        return arr[x1 - 1][y1 - 1].getPlayer();
    }

    private void setName(char c, int i, int j) {
        arr[i][j].setName(c);
    }

    public char getName(int i, int j) {
        return arr[i][j].getName();
    }

    private void setPlayers() //set color of pieces, white at top (0-1 index x), black at bottom (6-7 index x)
    {
        for (int j = 0; j < 8; j++) {
            arr[0][j].setPlayer("white");
            arr[1][j].setPlayer("white");
            arr[6][j].setPlayer("black");
            arr[7][j].setPlayer("black");
        }
    }

    public void drawBoard() {  // draw board
        for (int i = 0; i < 9; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        for (int i = 0; i < 8; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < 8; j++) {
                System.out.print(getName(i, j) + " ");
            }
            System.out.println();
        }
    }

      public void pieceEat(int x1, int y1, int x2, int y2) //eat piece, update array of piece for each player (1..8)
    {
        int i = 0, j = 0;
        boolean b = true, b1 = true;
        if (playerColor.equals("white")) //player white
        {
            while (i < 16 && b) //search for what piece is on that cord
            {
                if (white[i] != null && white[i].getingame() && white[i].getX() == (x1 - 1) && white[i].getY() == (y1 - 1)) {
                    b = false;
                }
                if (b) {
                    i++;
                }
            }
            while (j < 16 && b1) //black piece cord
            {
                if (black[j] != null && black[j].getingame() && black[j].getX() == (x2 - 1) && black[j].getY() == (y2 - 1)) //search for what piece is on that cord
                {
                    b1 = false;
                }
                if (b1) {
                    j++;
                }
            }
            if (!b1) //in case of white piece eat black piece, update black array
            {
                black[j].setingame(false);
            }
            white[i].setX(x2 - 1); //update white array
            white[i].setY(y2 - 1);
        }
        else if (playerColor.equals("black")) //player black
        {
            while (i < 16 && b) //search for type of black piece
            {
                if (black[i] != null && black[i].getingame() && black[i].getX() == (x1 - 1) && black[i].getY() == (y1 - 1)) {
                    b = false;
                }
                if (b) {
                    i++;
                }
            }
            while (j < 16 && b1) //search for white piece in case of eating it
            {
                if (white[j] != null && white[j].getingame() && white[j].getX() == (x2 - 1) && white[j].getY() == (y2 - 1)) {
                    b1 = false;
                }
                if (b1) {
                    j++;
                }
            }
            if (!b1) { //if white piece found for eating
                white[j].setingame(false);
            }
            black[i].setX(x2 - 1); //update black piece cord in array
            black[i].setY(y2 - 1);
        }
        arr[x2 - 1][y2 - 1].copyPiece(arr[x1 - 1][y1 - 1]);
        arr[x1 - 1][y1 - 1] = new PieceInfo();
        boolean bc = castling(y1, y2);
        if (x1 == 1 && y1 == 4 && x1 == x2 && Math.abs(y1 - y2) == 2 && !bc) {
            if (y1 - y2 == 2) {
                makeCastling("left");
            }
            else if (y2 - y1 == 2) {
                makeCastling("right");
            }
        }
    }

    private void printPieceOn() {
        for (int i = 0; i < 16; i++) {
            System.out.println(black[i].getPlayer() + " " + black[i].getName() + " " + black[i].getingame() + " " + black[i].getX() + " " + black[i].getY());
            System.out.println(white[i].getPlayer() + " " + white[i].getName() + " " + white[i].getingame() + " " + white[i].getX() + " " + white[i].getY() + "\n");
        }
    }

    private boolean pawnPath(int x1, int y1, int x2, int y2) //check is path possible and clear
    {
        if (arr[x1 - 1][y1 - 1].getPlayer().equals("black")) // black pawn goes up
        {
            if (arr[x2 - 1][y2 - 1].getPlayer().equals("") && ((y1 == y2 && x1 - x2 == 1) || (x1 - 1 == 6 && y1 == y2 && x1 - x2 == 2 && arr[x2][y2 - 1].getPlayer().equals("")))) {
                return true; //goes straight down by 1 or 2
            }
            if (arr[x2 - 1][y2 - 1].getPlayer().equals("white") && x1 - x2 == 1 && Math.abs(y1 - y2) == 1) {
                return true; //eat white piece by diagonal down by 1
            }
        }
        else //white pawn goes down
        {
            if (arr[x2 - 1][y2 - 1].getPlayer().equals("") && ((y1 == y2 && x2 - x1 == 1) || (x1 - 1 == 1 && y1 == y2 && x2 - x1 == 2 && arr[x1][y2 - 1].getPlayer().equals("")))) {
                return true; //goes straight up by 1 or 2
            }
            if (arr[x2 - 1][y2 - 1].getPlayer().equals("black") && x2 - x1 == 1 && Math.abs(y1 - y2) == 1) {
                return true; //eat black piece by diagonal up by 1
            }
        }
        return false;
    }

    private boolean bishopPath(int x1, int y1, int x2, int y2) // check if path is possible and clear, 1-8
    {
        x1--;
        y1--;
        x2--;
        y2--;
        if (Math.abs(x1 - x2) == Math.abs(y1 - y2)) {
            boolean pathClear = true;
            if (x1 > x2 && y1 > y2) //left up path
            {
                for (int i = x1 - 1, j = y1 - 1; i > x2 && j > y2 && pathClear; i--, j--) {
                    if (arr[i][j].getName() != 'x') {
                        pathClear = false;
                    }
                }
            }
            if (x1 < x2 && y1 > y2 && pathClear) // left down path
            {
                for (int i = x1 + 1, j = y1 - 1; i < x2 && j > y2 && pathClear; i++, j--) {
                    if (arr[i][j].getName() != 'x') {
                        pathClear = false;
                    }
                }
            }
            if (x1 < x2 && y1 < y2 && pathClear) // right down path
            {
                for (int i = x1 + 1, j = y1 + 1; i < x2 && j < y2 && pathClear; i++, j++) {
                    if (arr[i][j].getName() != 'x') {
                        pathClear = false;
                    }
                }
            }
            if (x1 > x2 && y1 < y2 && pathClear) // right up path
            {
                for (int i = x1 - 1, j = y1 + 1; i > x2 && j < y2 && pathClear; i--, j++) {
                    if (arr[i][j].getName() != 'x') {
                        pathClear = false;
                    }
                }
            }
            if (pathClear && !arr[x1][y1].getPlayer().equals(arr[x2][y2].getPlayer())) //path is clear and not same color
            {
                return true;
            }
        }
        return false;
    }

    private boolean knightPath(int x1, int y1, int x2, int y2) //check if path is possible,not same color, 1-8
    {
        if (!(arr[x1 - 1][y1 - 1].getPlayer().equals(arr[x2 - 1][y2 - 1].getPlayer()))) {
            return (Math.abs(x1 - x2) == 2 && Math.abs(y1 - y2) == 1) || (Math.abs(x1 - x2) == 1 && Math.abs(y1 - y2) == 2);
        }
        return false;
    }

    private boolean rookPath(int x1, int y1, int x2, int y2) // check if path is possible and clear, 1-8
    {
        boolean pathClear = true;
        if (x1 == x2) //vertical
        {
            if (y1 > y2) // move left
            {
                for (int i = y1 - 2; i > y2 - 1 && pathClear; i--)//check path till dentination
                {
                    if (arr[x1 - 1][i].getName() != 'x') {
                        pathClear = false;
                    }
                }
            }
            if (y1 < y2) //move right
            {
                for (int i = y1; i < y2 - 1 && pathClear; i++)//check path till dentination
                {
                    if (arr[x1 - 1][i].getName() != 'x') {
                        pathClear = false;
                    }
                }
            }
        }
        else if (y1 == y2) //horizontal
        {
            if (x1 > x2) //move up
            {
                for (int i = x1 - 2; i > x2 - 1 && pathClear; i--)//check path till dentination
                {
                    if (arr[i][y1 - 1].getName() != 'x') {
                        pathClear = false;
                    }
                }
            }
            if (x1 < x2) //move down
            {
                for (int i = x1; i < x2 - 1 && pathClear; i++)//check path till dentination
                {
                    if (arr[i][y1 - 1].getName() != 'x') {
                        pathClear = false;
                    }
                }
            }
        }
        else {
            pathClear = false;
        }

        if (pathClear) {
            if (!arr[x1 - 1][y1 - 1].getPlayer().equals(arr[x2 - 1][y2 - 1].getPlayer())) {
                return true;
            }
        }
        return false;
    }

    private boolean kingPath(int x1, int y1, int x2, int y2) // 1-8
    {
        if (x1 == 1 && x1 == x2) {
            if (y1 - y2 == 2 && kingPath(x1, y1, x2, y2 + 1)) {
                return castling(y1, y2);
            }
            else if (y2 - y1 == 2 && kingPath(x1, y1, x2, y2 - 1)) {
                return castling(y1, y2);
            }
        }
        return (Math.abs(x1 - x2) < 2 && Math.abs(y1 - y2) < 2 && !arr[x1 - 1][y1 - 1].getPlayer().equals(arr[x2 - 1][y2 - 1].getPlayer()) && kingPathAdv(x2, y2));
    }

    private boolean enemyKingPath(int x1, int y1, int x2, int y2) // check if enemt king is near
    {
        return (Math.abs(x1 - x2) < 2 && Math.abs(y1 - y2) < 2);
    }

    private boolean kingPathAdv(int x2, int y2) {  // more check for king move
        if (playerColor.equals("white")) {
            return !enemyKingPath(black[15].getX() + 1, black[15].getY() + 1, x2, y2);
        }
        else {
            return !enemyKingPath(white[15].getX() + 1, white[15].getY() + 1, x2, y2);
        }
    }

    public String getStringPiece(int x, int y) //return string of piece info
    {
        String pl = arr[x - 1][y - 1].getPlayer();
        char ch = arr[x - 1][y - 1].getName();
        return "player=" + pl + " piece=" + ch + " cord=" + x + "," + y;
    }

    public boolean ableToMove(int x1, int y1, int x2, int y2) //check if able to move, old (x1,y1) to new (x2,y2)
    {
        if (legalMove(x1, y1, x2, y2)) // if legal
        {
            pieceEat(x1, y1, x2, y2); // move piece
            if (playerColor.equals("white")) // if white check white king for check, else check black king
            {
                if( isWhiteKingSafe())
                    return true;
                else 
                    copyall();
            }
            else {
                if( isBlackKingSafe())
                    return true;
                else copyall();
            }
        }
        return false; // not legal
    }

    public boolean legalMove(int x1, int y1, int x2, int y2) //check for what piece is selected and if legal moving
    {
        boolean legal;
        switch (getName(x1 - 1, y1 - 1)) {
            case 'p': //pawn
                legal = pawnPath(x1, y1, x2, y2);
                break;
            case 'b': //bishop
                legal = bishopPath(x1, y1, x2, y2);
                break;
            case 'k': //knight
                legal = knightPath(x1, y1, x2, y2);
                break;
            case 'r': //rook
                legal = rookPath(x1, y1, x2, y2);
                break;
            case 'q': //queen, goes like rook or bishop
                legal = (rookPath(x1, y1, x2, y2) || bishopPath(x1, y1, x2, y2));
                break;
            case 'K': //king
                legal = kingPath(x1, y1, x2, y2);
                break;
            default:
                legal = false;
                break;
        }
        return legal;
    }

    public boolean isWhiteKingSafe() //check if white king safe
    {
        boolean isSafe = true;
        for (int i = 0; i < 15 && isSafe; i++) {
            if (black[i] != null && black[i].getX() != -1) {
                isSafe = !legalMove(black[i].getX() + 1, black[i].getY() + 1, white[15].getX() + 1, white[15].getY() + 1); //check for every black piece
            }
        }
        return isSafe;
    }

    public boolean isBlackKingSafe() // look for each white piece if they can attack king
    {
        boolean isSafe = true;
        for (int i = 0; i < 15 && isSafe; i++) {
            if (white[i] != null && white[i].getX() != -1) {
                isSafe = !legalMove(white[i].getX() + 1, white[i].getY() + 1, black[15].getX() + 1, black[15].getY() + 1);
            }
        }
        return isSafe;
    }

    public boolean isPieceAttackable(int x2, int y2) // check if piece on that cord  can be attacked
    {
        boolean attackable = false;
        if (playerColor.equals("white")) 
        {
            for (int i = 0; i < 16 && !attackable; i++) 
                if (black[i].getingame()) 
                {
                    if(i==15) changePlayer();
                    attackable = legalMove(black[i].getX() + 1, black[i].getY() + 1, x2, y2);
                    if(i==15) changePlayer();
                }
        }    
         if (playerColor.equals("black")) 
                    for (int i = 0; i < 16 && !attackable; i++) 
                        if (white[i].getingame()) 
                        {
                            if(i==15) changePlayer();
                            attackable = legalMove(white[i].getX() + 1, white[i].getY() + 1, x2, y2);
                            if(i==15) changePlayer();
                        }
   
        return attackable;
    }

    public boolean preNextMove(int x1, int y1, int x2, int y2, boolean colorSquare) //build cord for next move
    {
        copyArrPiece2(arr, arrtmp); //temporary board for next move check
        copyArrPiece(white, whiteNext);
        copyArrPiece(black, blackNext);
        boolean nextMove = false;
        if (ableToMove(x1, y1, x2, y2)) {
            if (playerColor.equals("white")) {
                nextMove = isWhiteKingSafe();
            }
            else {
                nextMove = isBlackKingSafe();
            }
        }
        if (colorSquare) {
            copyall();     // copy back the original arrays
        }
        return nextMove;
    }

    public void copyall() {
        copyArrPiece2(arrtmp, arr);   // return board state to start of the move
        copyArrPiece(whiteNext, white);
        copyArrPiece(blackNext, black);
    }

    private void copyArrPiece(PieceInfo[] baseA, PieceInfo[] copy) //copy PieceInfo[] array
    {
        for (int i = 0; i < 16; i++) {
            copy[i] = new PieceInfo(baseA[i]);
        }
    }

    private void copyArrPiece2(PieceInfo[][] baseA, PieceInfo[][] copy) //copy PieceInfo[][] matrix
    {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copy[i][j] = new PieceInfo(baseA[i][j]);
            }
        }
    }

    public void checkKingPlayer(int x, int y) // if current piece move will check on enemy king
    {
        if (playerColor.equals("white")) {
            checkOnKing = legalMove(x + 1, y + 1, black[15].getX() + 1, black[15].getY() + 1);
        }
        else {
            checkOnKing = legalMove(x + 1, y + 1, white[15].getX() + 1, white[15].getY() + 1);
        }
    }
    
    
//    private void buildTreeMove()
//    {
//        ArrayList<String> moveList = new ArrayList<String>();
//        for (int h = 0; h < 16; h++) {
//            if(black[h].getingame())
//            {
//            int x = black[h].getX();
//            int y = black[h].getY();
//            for (int i = 0; i < 8; i++) {
//                for (int j = 0; j < 8; j++) {
//                    PieceInfo tmpPc1=new PieceInfo(arr[i][j]);
//                    PieceInfo tmpPc2=new PieceInfo(arr[x][y]);
//                    if (legalMove(x + 1, y + 1, i + 1, j + 1)) {
//                        
//                        if (preNextMove(x + 1, y + 1, i + 1, j + 1, false)) {
//                            resetUnderAttack(black);
//                            setUnderAttack(black);
//                            for (int a = 0; a < 16; a++) {
//                                resetUnderAttack(white);
//                                if (legalMove(x + 1, y + 1, white[a].getX() + 1, white[a].getY() + 1)) {
//                                    if (arr[i][j].getingame()) {
//                                        white[a].setUnderAttack(true);
//                                    }
//                                    moveList.add(black[h].getName() + ": " + (x + 1) + "" + (y + 1) + " " + (i + 1) + "" + (j + 1) + " " + playerScore(black, 0) + " " + playerScore(white, 0) + "\n");
//                                }
//                            }
//                            moveList.add(black[h].getName() + ": " + (x + 1) + "" + (y + 1) + " " + (i + 1) + "" + (j + 1) + " " + playerScore(black, 0) + " " + playerScore(white, 0) + "\n");
//                        }
//                        
//                        black[h].setX(x);
//                        black[h].setY(y);
//                        arr[i][j]=tmpPc1;
//                        arr[x][y]=tmpPc2;
//                        for(int n=0;n<16;n++)
//                            if(white[n].getX()==i && white[n].getY()==j)
//                                white[n].setingame(true);
//                    }
//                }
//            }
//        }
//        }
//        
//        
//         for(int v=0;v<moveList.size();v++)
//             if(moveList.get(v).substring(0, 1).equals("x"))
//                 moveList.remove(v);
//        
//            int[] score = new int[moveList.size()];
//            for (int i = 0; i < moveList.size(); i++) // create score array for comp score
//            {
//                score[i] = Integer.parseInt(moveList.get(i).substring(9, 11));
//            }
//            int max = score[0];
//            for (int i = 1; i < score.length; i++) // search for max score
//            {
//                if (max < score[i]) {
//                    max = score[i];
//                }
//            }
//            for (int i = 0; i < moveList.size(); i++) {
//                if (score[i] != max) {
//                    moveList.remove(i);  // take only max score
//                }
//            }
//            int[] scorePL = new int[moveList.size()]; // create player's score array
//            for (int i = 0; i < moveList.size(); i++) {
//                scorePL[i] = Integer.parseInt(moveList.get(i).substring(9, 11));
//            }
//            int min = scorePL[0]; // create minimum score for player
//            for (int i = 1; i < scorePL.length; i++) // searhc for min score etry
//            {
//                if (min > scorePL[i]) {
//                    min = scorePL[i];
//                }
//            }
//            for (int i = 0; i < moveList.size(); i++) // best of the best moves, comp max score, player min score
//            {
//                if (scorePL[i] != min) {
//                    moveList.remove(i);
//                }
//            }
//            for(int i=0;i<moveList.size();i++)
//                advMove2.add(func1(new Node(moveList.get(i)),0,white,black));
//    }
//
//    private Node func1(Node newNode, int level, PieceInfo[] you, PieceInfo[] opp)
//    {
//        
//        if(level==2)
//        {
//            return newNode;
//        }
//           newNode.setChildren(new ArrayList<Node>());
//        ArrayList<String> moveList = new ArrayList<String>();
////        int[] arr2=getCord(newNode.getValue());
////        if(playerColor.equals("black"))
////        {
////            copyArrPiece(opp,black);
////            copyArrPiece(you,white);
////        }
////        else 
////        {
////            copyArrPiece(opp,white);
////            copyArrPiece(you,black);
////        }
////        pieceEat(arr2[0],arr2[1],arr2[2],arr2[3]);
////        if(playerColor.equals("black"))
////        {
////            copyArrPiece(black,opp);
////            copyArrPiece(white,you);
////        }
////        else 
////        {
////            copyArrPiece(white,opp);
////            copyArrPiece(black,you);
////        }
//        changePlayer();
//        for (int h = 0; h < 16; h++) {
//            if(you[h].getingame())
//            {
//            int x = you[h].getX();
//            int y = you[h].getY();
//            for (int i = 0; i < 8; i++) {
//                for (int j = 0; j < 8; j++) {
//                    PieceInfo tmpPc1=new PieceInfo(arr[i][j]);
//                    PieceInfo tmpPc2=new PieceInfo(arr[x][y]);
//                    if (legalMove(x + 1, y + 1, i + 1, j + 1)) {
//                        
//                        if (preNextMove(x + 1, y + 1, i + 1, j + 1, false)) {
//                            resetUnderAttack(you);
//                            setUnderAttack(you);
//                            for (int a = 0; a < 16; a++) {
//                                resetUnderAttack(opp);
//                                if (legalMove(x + 1, y + 1, opp[a].getX() + 1, opp[a].getY() + 1)) {
//                                    if (arr[i][j].getingame()) {
//                                        opp[a].setUnderAttack(true);
//                                    }
//                                    PieceInfo[] tmp=new PieceInfo[16];
//                                    copyArrPiece(you,tmp);
//                                    copyArrPiece(opp,you);
//                                    copyArrPiece(tmp,opp);
//                                    newNode.getChildren().add(func1(new Node(moveList.get(moveList.size()-1)),level+1,you,opp));
//                                    moveList.add(you[h].getName() + ": " + (x + 1) + "" + (y + 1) + " " + (i + 1) + "" + (j + 1) + " " + playerScore(you, 0) + " " + playerScore(opp, 0) + "\n");
//                                }
//                            }
//                            moveList.add(you[h].getName() + ": " + (x + 1) + "" + (y + 1) + " " + (i + 1) + "" + (j + 1) + " " + playerScore(you, 0) + " " + playerScore(opp, 0) + "\n");
//                        }
//                        PieceInfo[] tmp=new PieceInfo[16];
//                        copyArrPiece(you,tmp);
//                        copyArrPiece(opp,you);
//                        copyArrPiece(tmp,opp);
//                        newNode.getChildren().add(func1(new Node(moveList.get(moveList.size()-1)),level+1,you,opp));
//                        changePlayer();
//                        you[h].setX(x);
//                        you[h].setY(y);
//                        arr[i][j]=tmpPc1;
//                        arr[x][y]=tmpPc2;
//                        for(int n=0;n<16;n++)
//                            if(opp[n].getX()==i && opp[n].getY()==j)
//                                opp[n].setingame(true);
//                    }
//                }
//            }
//        }
//        }
//        
//        
////         for(int v=0;v<moveList.size();v++)
////             if(moveList.get(v).substring(0, 1).equals("x"))
////                 moveList.remove(v);
////        
////            int[] score = new int[moveList.size()];
////            for (int i = 0; i < moveList.size(); i++) // create score array for comp score
////            {
////                score[i] = Integer.parseInt(moveList.get(i).substring(9, 11));
////            }
////            int max = score[0];
////            for (int i = 1; i < score.length; i++) // search for max score
////            {
////                if (max < score[i]) {
////                    max = score[i];
////                }
////            }
////            for (int i = 0; i < moveList.size(); i++) {
////                if (score[i] != max) {
////                    moveList.remove(i);  // take only max score
////                }
////            }
////            int[] scorePL = new int[moveList.size()]; // create player's score array
////            for (int i = 0; i < moveList.size(); i++) {
////                scorePL[i] = Integer.parseInt(moveList.get(i).substring(9, 11));
////            }
////            int min = scorePL[0]; // create minimum score for player
////            for (int i = 1; i < scorePL.length; i++) // searhc for min score etry
////            {
////                if (min > scorePL[i]) {
////                    min = scorePL[i];
////                }
////            }
////            for (int i = 0; i < moveList.size(); i++) // best of the best moves, comp max score, player min score
////            {
////                if (scorePL[i] != min) {
////                    moveList.remove(i);
////                }
////            }
//         
//            
////        for(int i=0;i<moveList.size();i++)
////            newNode.getChildren().add(func1(new Node(moveList.get(i)),level+1,you,opp));
//        
//        changePlayer();
//     //   copyall();
//     if(newNode.getChildren().size()==0)
//     {
//          PieceInfo[] tmp=new PieceInfo[16];
//                        copyArrPiece(you,tmp);
//                        copyArrPiece(opp,you);
//                        copyArrPiece(tmp,opp);
//                        newNode.getChildren().add(func1(new Node(moveList.get(moveList.size()-1)),level+1,you,opp));
//     }
//        return newNode;
//    }
    
    private AImove getAllMoves() // get all possible moves for comp
    {
        AImove.clear();
        AImoveAtt.clear();
        AImoveBest.clear();
        advMove.clear();
        for (int h = 0; h < 16; h++) {
            if(black[h].getingame() && black[h].getName()!='x')
            {
            int x = black[h].getX();
            int y = black[h].getY();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    PieceInfo tmpPc=new PieceInfo(arr[i][j]);
                    PieceInfo tmpPc2=new PieceInfo(arr[x][y]);
                        if (preNextMove(x + 1, y + 1, i + 1, j + 1, false)) {
                            boolean lp=false;
                            if(tmpPc.getName()!='x') lp=true;
                            resetUnderAttack(black);
                            setUnderAttack(black);
                            for (int a = 0; a < 16; a++) {
                                resetUnderAttack(white);
                                if (white[a].getingame() && legalMove(black[h].getX() + 1, black[h].getY() + 1, white[a].getX() + 1, white[a].getY() + 1)) {
                                        white[a].setUnderAttack(true);
                                         AImove.add(new AImove(black[h].getName(),new Point((x + 1),(y + 1)),new Point((i + 1),(j + 1)),playerScore(black, 0),playerScore(white, 0),lp));
                                         System.out.println(AImove.get(AImove.size()-1));
                                }
                                
                            }
                            AImove.add(new AImove(black[h].getName(),new Point((x + 1),(y + 1)),new Point((i + 1),(j + 1)),playerScore(black, 0),playerScore(white, 0),lp));
                          if(twoStep)
                          {    
                              advMove.add(new Node(new AImove(black[h].getName(),new Point((x + 1),(y + 1)),new Point((i + 1),(j + 1)),playerScore(black, 0),playerScore(white, 0),lp), new ArrayList<Node>()));   
                            getAllMoves2(); 
                          }
                        }
                      //  copyall();
                        black[h].setX(x);
                        black[h].setY(y);
                        arr[i][j]=tmpPc;
                        arr[x][y]=tmpPc2;
                        for(int n=0;n<16;n++)
                            if(white[n].getName()!='x' && white[n].getX()==i && white[n].getY()==j)
                                white[n].setingame(true);
                    }
                
            }
        }
        }
        if (twoStep)
        {
        if(!advMove.isEmpty())
        {
      //  printTree();
        int m1=99;
        ArrayList<Integer> bestIndex2=new ArrayList<Integer>();
        for(int i=0;i<advMove.size();i++)
        {
            if(advMove.get(i)!=null && advMove.get(i).getChildren()!=null)
            for(int j=0;j<advMove.get(i).getChildren().size();j++)
                if(advMove.get(i).getChildren().get(j)!=null && advMove.get(i).getChildren().get(j).getChildren()!=null && advMove.get(i).getChildren().get(j).getChildren().size()>0 && advMove.get(i).getChildren().get(j).getChildren().get(0).getValue().getScore("enemy")<m1)
                    m1=advMove.get(i).getChildren().get(j).getChildren().get(0).getValue().getScore("enemy");
        }
        
        for(int i=0;i<advMove.size();i++)
        {
            int count=0;
            if(advMove.get(i)!=null && advMove.get(i).getChildren()!=null)
            {
            for(int j=0;j<advMove.get(i).getChildren().size();j++)
                if(advMove.get(i).getChildren().get(j)!=null && advMove.get(i).getChildren().get(j).getChildren()!=null && advMove.get(i).getChildren().get(j).getChildren().size()>0 && advMove.get(i).getChildren().get(j).getChildren().get(0).getValue().getScore("enemy")==m1)
                {
                    if(bestIndex2.isEmpty())
                    bestIndex2.add(i);
                    else
                    {
                        if(bestIndex2.size()%2!=0 && bestIndex2.get(bestIndex2.size()-1)==i)
                            count++;
                        else bestIndex2.add(i);
                    }
                }
            if(bestIndex2.size()%2!=0) bestIndex2.add(count);
            }
        }
        int minCount=bestIndex2.get(1);
        for(int i=3;i<bestIndex2.size();i+=2)
            if(bestIndex2.get(i)>minCount) minCount=bestIndex2.get(i);
        ArrayList<Integer> movesIndex2=new ArrayList<Integer>();
        for(int i=0;i<bestIndex2.size()-1;i+=2)
            if(bestIndex2.get(i+1)==minCount) movesIndex2.add(bestIndex2.get(i));
        for(int i=0;i<movesIndex2.size();i++)
            if(advMove.get(movesIndex2.get(i)).getValue().getLosePiece())
                return advMove.get(movesIndex2.get(i)).getValue();
            Random random = new Random();
            int r2 = random.nextInt(movesIndex2.size());
            //int r4=random.nextInt(movesIndex.size());
            System.out.println("--------------");
           // int r3=r2;
            for(int i=0;i<advMove.size();i++)
            {
                if(advMove.get(i).getValue().getScore("enemy")<=m1)
                {
                   return twoStepMove=advMove.get(i).getValue();
                }
            }
           return twoStepMove=advMove.get(movesIndex2.get(r2)).getValue();
            
        }
        else return twoStepMove=null;
        }
        return null;
    }
    
        private void getAllMoves2() // get all possible moves for comp
    {
        changePlayer();
        AImove2.clear();
        for (int h = 0; h < 16; h++) {
            if(white[h].getingame() && white[h].getName()!='x')
            {
            int x = white[h].getX();
            int y = white[h].getY();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    PieceInfo tmpPc3=new PieceInfo(arr[i][j]);
                        PieceInfo tmpPc4=new PieceInfo(arr[x][y]);
                    if (legalMove(x + 1, y + 1, i + 1, j + 1)) {
                        nodeTree.clear();
                        if (preNextMove(x + 1, y + 1, i + 1, j + 1, false)) {
                            resetUnderAttack(white);
                            setUnderAttack(white);
                            for (int a = 0; a < 16; a++) {
                                resetUnderAttack(black);
                                if (legalMove(white[h].getX() + 1, white[h].getY() + 1, black[a].getX() + 1, black[a].getY() + 1)) {
                                    if (arr[i][j].getingame()) {
                                        black[a].setUnderAttack(true);
                                    }
                                  //  AImove2.add(white[h].getName() + ": " + (x + 1) + "" + (y + 1) + " " + (i + 1) + "" + (j + 1) + " " + playerScore(white, 0) + " " + playerScore(black, 0) + "\n");
                                }
                            }
                            AImove2.add(new AImove(white[h].getName(),new Point((x + 1),(y + 1)),new Point((i + 1),(j + 1)),playerScore(white, 0), playerScore(black, 0),false));
                            //advMove.get(advMove.size()-1).getChildren().add(new Node(white[h].getName() + ": " + (x + 1) + "" + (y + 1) + " " + (i + 1) + "" + (j + 1) + " " + playerScore(white, 0) + " " + playerScore(black, 0) + "\n"));
                            //nodeTree.clear();
                            nodeTree=new Node(new AImove(white[h].getName(),new Point((x + 1),(y + 1)),new Point((i + 1),(j + 1)),playerScore(white, 0),playerScore(black, 0),false),new ArrayList<Node>());
                            getAllMoves3();    
                           // nodeTree.setChildren(new ArrayList<Node>());
                            for(int z=0;z<AImove3.size();z++)
                                nodeTree.getChildren().add(new Node(AImove3.get(z),null));
                            //nodeTree.setChildren(nodeTree.getChildren());                                                         
                                                 
                        } 
                        advMove.get(advMove.size()-1).getChildren().add(new Node(nodeTree.getValue(),nodeTree.getChildren()));
                        if(h==15 && i==x)
                        {
                            if(white[8].getingame() && white[8].getY()-y==1)
                            {
                                arr[0][7]=new PieceInfo(arr[0][4]);
                                arr[0][4]=new PieceInfo();
                                white[8].setY(7);
                            }
                            else if(white[9].getingame() && white[9].getY()-y==-1)
                            {
                                arr[0][0]=new PieceInfo(arr[0][2]);
                                arr[0][2]=new PieceInfo();
                                white[9].setY(0);
                            }
                        }
                        white[h].setX(x);
                        white[h].setY(y);
                        
                        arr[i][j]=tmpPc3;
                        arr[x][y]=tmpPc4;
                        for(int n=0;n<16;n++)
                            if(black[n].getName()!='x' && black[n].getX()==i && black[n].getY()==j)
                                black[n].setingame(true);
                       // copyall();
                    }
                }
            }
        }
        }
         int min=99;
                        for(int i2=0;i2<advMove.get(advMove.size()-1).getChildren().size();i2++)
                            if(advMove.get(advMove.size()-1).getChildren().get(i2).getValue()!=null){
                                if(advMove.get(advMove.size()-1).getChildren().get(i2).getValue().getScore("enemy")<min)
                                min=advMove.get(advMove.size()-1).getChildren().get(i2).getValue().getScore("enemy");
                            }
                            else 
                            {
                               advMove.get(advMove.size()-1).getChildren().remove(i2);
                               i2--;
                            }
                        
                        for(int i2=0;i2<advMove.get(advMove.size()-1).getChildren().size();i2++)
                            if(advMove.get(advMove.size()-1).getChildren().get(i2).getValue().getScore("enemy")!=min)
                            {
                                advMove.get(advMove.size()-1).getChildren().remove(i2);
                                i2--;
                            }
                        
        changePlayer();
    }
        
         private void getAllMoves3() // get all possible moves for comp
    {
        changePlayer();
        AImove3.clear();
        for (int h = 0; h < 16; h++) {
            if(black[h].getingame() && black[h].getName()!='x')
            {
            int x = black[h].getX();
            int y = black[h].getY();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    PieceInfo tmpPc5=new PieceInfo(arr[i][j]);
                    PieceInfo tmpPc6=new PieceInfo(arr[x][y]);
                        if (preNextMove(x + 1, y + 1, i + 1, j + 1, false)) {
                            boolean lp=false;
                            if(tmpPc5.getName()!='x') lp=true;
                            resetUnderAttack(black);
                            setUnderAttack(black);
                            for (int a = 0; a < 16; a++) {
                                resetUnderAttack(white);
                                if (legalMove(black[h].getX() + 1, black[h].getY() + 1, white[a].getX() + 1, white[a].getY() + 1)) {
                                    if (arr[i][j].getingame()) {
                                        white[a].setUnderAttack(true);
                                    }
                                    AImove3.add(new AImove(black[h].getName(),new Point((x + 1),(y + 1)),new Point((i + 1),(j + 1)),playerScore(black, 0),playerScore(white, 0),lp));
                                }
                            }
                            AImove3.add(new AImove(black[h].getName(),new Point((x + 1),(y + 1)),new Point((i + 1),(j + 1)),playerScore(black, 0),playerScore(white, 0),lp));
                        }
                       // copyall();
                        black[h].setX(x);
                        black[h].setY(y);
                        arr[i][j]=tmpPc5;
                        arr[x][y]=tmpPc6;
                        for(int n=0;n<16;n++)
                            if(white[n].getName()!='x' && white[n].getX()==i && white[n].getY()==j)
                                white[n].setingame(true);
                    }
                }
            }
        
        }
        changePlayer();
         int max=0;
            
            for (int i = 0; i <AImove3.size() ; i++) // search for max score
            {
                if (max < AImove3.get(i).getScore("you")) {
                    max = AImove3.get(i).getScore("you");
                }
            }
            for (int i = 0; i < AImove3.size(); i++) {
                if (AImove3.get(i).getScore("you") != max) {
                    AImove3.remove(i);
                    i--;
                }
            }
            
            int min =99; // create minimum score for player
            for (int i = 1; i < AImove3.size(); i++) // searhc for min score etry
            {
                if (min > AImove3.get(i).getScore("enemy")) {
                    min = AImove3.get(i).getScore("enemy");
                }
            }
            for (int i = 0; i < AImove3.size(); i++) // best of the best moves, comp max score, player min score
            {
                if (AImove3.get(i).getScore("enemy") != min) {
                    AImove3.remove(i);
                    i--;
                }
            }
    }
         
         
    public AImove timeOutMove(String you,String enemy)
    {
        ArrayList<AImove> rndM=new ArrayList<AImove>();
        for (int h = 0; h < 16; h++) {
            int x=-1,y=-1;
            if(you.equals("white") && white[h].getingame() && white[h].getName()!='x')
            {
            x = white[h].getX();
            y = white[h].getY();
            }
            else  if(you.equals("black") && black[h].getingame() && black[h].getName()!='x')
            {
                x = black[h].getX();
                y = black[h].getY();
            }
            if(x!=-1 && y!=-1)
            {
            PieceInfo tmpPc=new PieceInfo(arr[x][y]);
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    PieceInfo tmpPc2=new PieceInfo(arr[i][j]);
                    
                        if (preNextMove(x + 1, y + 1, i + 1, j + 1, false)) {
                            rndM.add(new AImove(new Point(x,y),new Point(i,j)));
                        }
                        arr[i][j]=tmpPc;
                        arr[x][y]=tmpPc2;
                        if(you.equals("white"))
                        {
                        white[h].setX(x);
                        white[h].setY(y);
                        }
                        else {
                            black[h].setX(x);
                            black[h].setY(y);
                        }
                        
                        for(int n=0;n<16;n++)
                            if(enemy.equals("black") && black[n].getName()!='x' && black[n].getX()==i && black[n].getY()==j)
                                black[n].setingame(true);
                            else if(enemy.equals("white") && white[n].getName()!='x' && white[n].getX()==i && white[n].getY()==j)
                               white[n].setingame(true);
                    }
                }
            }
        }
        for(int i=0;i<rndM.size();i++)
            if(rndM.get(i)==null)
            {
                rndM.remove(i);
                i--;
            }
        Random r=new Random();
        return rndM.get(r.nextInt(rndM.size()));
    }
         
    private void printTree()
    {
        for(int i=0;i<advMove.size();i++)
            advMove.get(i).print();
    }

    private void setUnderAttack(PieceInfo[] arr) // set all pieces of color if attackable
    {
        for (int i = 0; i < 16; i++) {
            if (arr[i].getingame() && isPieceAttackable(arr[i].getX() + 1, arr[i].getY() + 1)) {
                arr[i].setUnderAttack(true);
            }
        }
    }

    private void resetUnderAttack(PieceInfo[] arr) // reset all pieces of color to unattackable
    {
        for (int i = 0; i < 16; i++) {
            if (arr[i].getingame()) {
                arr[i].setUnderAttack(false);
            }
        }
    }

    private String getAllList() { // toString of comp moves
        System.out.println("player score=" + playerScore(black, 0));
        String s = "all----\n";
        for (int i = 0, j = AImove.size(); i < j; i++) {
            s = s + AImove.get(i);
        }
        return s;
    }

    public AImove randMove() // pick random move from best moves list
    {
       // buildTreeMove();
        if(twoStep)
        return  getAllMoves();
        else {
        chooseMoves();
        int ind = AImoveBest.size();   // if size=0, aka list empty, comp lose, player win
        if (ind != 0) {
            Random random = new Random();
            int r2 = random.nextInt(ind);
            return AImoveBest.get(r2);
        }
        return null;
        }
    }
       public void randMove(int index) // pick random move from best moves list
    { 
            Random random = new Random();
            int r2 = random.nextInt(index);
//            for(int i=0;i<advMove.size();i++)
//                System.out.println(advMove.get(i).getValue());
//            System.out.println(r2);
            twoStepMove=AImove.get(r2);
    }

    private void chooseMoves() // move= p: xy xy 41 41
    {
        if (AImove.isEmpty()) {
            Board.showMSG("player win"); // comp dont have any move
        }
        else {
            
            int max = AImove.get(0).getScore("you");
            for (int i = 1; i < AImove.size(); i++) // search for max score
            {
                if (max < AImove.get(i).getScore("you")) {
                    max = AImove.get(i).getScore("you");
                }
            }
            for (int i = 0; i < AImove.size(); i++) {
                if (AImove.get(i).getScore("you") == max) {
                    AImoveAtt.add(AImove.get(i));  // take only max score
                }
            }
            
            int min = AImoveAtt.get(0).getScore("enemy"); // create minimum score for player
            for (int i = 1; i < AImoveAtt.size(); i++) // searhc for min score etry
            {
                if (min > AImoveAtt.get(i).getScore("enemy")) {
                    min = AImoveAtt.get(i).getScore("enemy");
                }
            }
            for (int i = 0; i < AImoveAtt.size(); i++) // best of the best moves, comp max score, player min score
            {
                if (AImoveAtt.get(i).getScore("enemy") == min) {
                    AImoveBest.add(AImoveAtt.get(i));
                }
            }
        }
    }

//    public int[] getCord(String m) // get cord from string, example==>"p: 77 57" (piece: xy xy)
//    {
//        int[] arrCord = new int[4];
//        String s1 = m.substring(3, 5);
//        String s2 = m.substring(6, 8);
//        arrCord[0] = Integer.parseInt(s1);
//        arrCord[1] = arrCord[0] % 10;
//        arrCord[0] /= 10;
//        arrCord[2] = Integer.parseInt(s2);
//        arrCord[3] = arrCord[2] % 10;
//        arrCord[2] /= 10;
//        return arrCord;
//    }

    private int playerScore(PieceInfo[] plArr, int index) // total score of pieces for player
    {
        if (index < 16) {
            if (plArr[index].getingame() && !plArr[index].getUnderAttack() && plArr[index].getName() == 'p') {
                return 1 + playerScore(plArr, index + 1);
            }
            if (plArr[index].getingame() && !plArr[index].getUnderAttack() && plArr[index].getName() == 'r') {
                return 2 + playerScore(plArr, index + 1);
            }
            if (plArr[index].getingame() && !plArr[index].getUnderAttack() && plArr[index].getName() == 'k') {
                return 3 + playerScore(plArr, index + 1);
            }
            if (plArr[index].getingame() && !plArr[index].getUnderAttack() && plArr[index].getName() == 'b') {
                return 4 + playerScore(plArr, index + 1);
            }
            if (plArr[index].getingame() && !plArr[index].getUnderAttack() && plArr[index].getName() == 'q') {
                return 5 + playerScore(plArr, index + 1);
            }
            if (plArr[index].getingame() && !plArr[index].getUnderAttack() && plArr[index].getName() == 'K') {
                return 10 + playerScore(plArr, index + 1);
            }
            return 0 + playerScore(plArr, index + 1);
        }
        return 0;
    }

    public boolean addpiece(char p, int x, int y, String pl) // add piece to the board
    {
        boolean b = false;
        if (pl.equals("white")) {
            switch (p) {
                case 'p': {
                    int i = 0;
                    for (; i < 8 && white[i].getingame(); i++);
                    if (i != 8) {
                        white[i] = new PieceInfo(p, x, y, pl);
                        if (black[15].getingame()) {
                            b = isBlackKingSafe();
                        }
                        else {
                            b = true;
                        }
                    }
                    break;
                }
                case 'r': {
                    if (!white[8].getingame()) {
                        white[8] = new PieceInfo(p, x, y, pl);
                        if (black[15].getingame()) {
                            b = isBlackKingSafe();
                        }
                        else {
                            b = true;
                        }
                    }
                    else if (!white[9].getingame()) {
                        white[9] = new PieceInfo(p, x, y, pl);
                        if (black[15].getingame()) {
                            b = isBlackKingSafe();
                        }
                        else {
                            b = true;
                        }
                    }
                    break;
                }
                case 'k': {
                    if (!white[10].getingame()) {
                        white[10] = new PieceInfo(p, x, y, pl);
                        if (black[15].getingame()) {
                            b = isBlackKingSafe();
                        }
                        else {
                            b = true;
                        }
                    }
                    else if (!white[11].getingame()) {
                        white[11] = new PieceInfo(p, x, y, pl);
                        if (black[15].getingame()) {
                            b = isBlackKingSafe();
                        }
                        else {
                            b = true;
                        }
                    }
                    break;
                }
                case 'b': {
                    if (!white[12].getingame() && whiteBishop1) {
                        white[12] = new PieceInfo(p, x, y, pl);
                        if (black[15].getingame()) {
                            b = isBlackKingSafe();
                        }
                        else {
                            b = true;
                        }
                    }
                    else if (!white[13].getingame() && whiteBishop2) {
                        white[13] = new PieceInfo(p, x, y, pl);
                        if (black[15].getingame()) {
                            b = isBlackKingSafe();
                        }
                        else {
                            b = true;
                        }
                    }
                    break;
                }
                case 'q': {
                    if (!white[14].getingame()) {
                        white[14] = new PieceInfo(p, x, y, pl);
                        if (black[15].getingame()) {
                            b = isBlackKingSafe();
                        }
                        else {
                            b = true;
                        }
                    }
                    break;
                }
                case 'K': {
                    if (!white[15].getingame()) {
                        white[15] = new PieceInfo(p, x, y, pl);
                        whiteKing = true;
                        if (black[15].getingame()) {
                            b = isBlackKingSafe();
                        }
                        else {
                            b = true;
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
        else {
            switch (p) {
                case 'p': {
                    int i = 0;
                    for (; i < 8 && black[i].getingame(); i++);
                    if (i != 8) {
                        black[i] = new PieceInfo(p, x, y, pl);
                        b = true;
                    }
                    break;
                }
                case 'r': {
                    if (!black[8].getingame()) {
                        black[8] = new PieceInfo(p, x, y, pl);
                        b = true;
                    }
                    else if (!black[9].getingame()) {
                        black[9] = new PieceInfo(p, x, y, pl);
                        b = true;
                    }
                    break;
                }
                case 'k': {
                    if (!black[10].getingame()) {
                        black[10] = new PieceInfo(p, x, y, pl);
                        b = true;
                    }
                    else if (!black[11].getingame()) {
                        black[11] = new PieceInfo(p, x, y, pl);
                        b = true;
                    }
                    break;
                }
                case 'b': {
                    if (!black[12].getingame() && blackBishop1) {
                        black[12] = new PieceInfo(p, x, y, pl);
                        b = true;
                    }
                    else if (!black[13].getingame() && blackBishop1) {
                        black[13] = new PieceInfo(p, x, y, pl);
                        b = true;
                    }
                    break;
                }
                case 'q': {
                    if (!black[14].getingame()) {
                        black[14] = new PieceInfo(p, x, y, pl);
                        b = true;
                    }
                    break;
                }
                case 'K': {
                    if (!black[15].getingame()) {
                        black[15] = new PieceInfo(p, x, y, pl);
                        blackKing = true;
                        b = true;
                    }
                    break;
                }
                default:
                    break;
            }
        }
        if (b) {
            arr[x][y] = new PieceInfo(p, x, y, pl);
        }
        return b;
    }

    //advanced rules
    private boolean castling(int y1, int y2) // check if castling possible
    {
        boolean b = false;
        if (isWhiteKingSafe() && white[15].getX() == 0 && white[15].getY() == 3 && !castling) // king is safe and on his starting position
        {
            if (y1 - y2 == 2) {
                if (!isPieceAttackable(1, 3)) // System.out.println("piece not attackable");
                {
                    if (white[8].getX() == 0 && white[8].getY() == 0 && rookPath(white[8].getX() + 1, white[8].getY() + 1, 1, 3)) {
                        b = true;
                        return b;
                    }
                    else if (white[9].getX() == 0 && white[9].getY() == 0 && rookPath(white[9].getX() + 1, white[9].getY() + 1, 1, 3)) // left castling
                    {
                        b = true;
                        return b;
                    }
                }
            }
        }
        if (y2 - y1 == 2 && !isPieceAttackable(1, 5) && ((white[9].getX() == 0 && white[9].getY() == 7 && rookPath(white[9].getX() + 1, white[9].getY() + 1, 1, 5)) || (white[8].getX() == 0 && white[8].getY() == 7 && rookPath(white[8].getX() + 1, white[8].getY() + 1, 1, 5)))) // right castling
        {
            b = true;
            return b;
        }
        return b;
    }

    public void makeCastling(String side) // which side castling done, left or right
    {
        if (side.equals("left")) {
            pieceEat(1, 1, 1, 3);
        }
        else {
            pieceEat(1, 8, 1, 5);
        }
    }

    public void doneCastling() // set castling true, cant do it again
    {
        castling = true;
    }
}
