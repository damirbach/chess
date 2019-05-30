public class PieceInfo {  // piece class

    private char name;
    private int x;
    private int y;
    private String player;
    private boolean ingame;
    private boolean underAttack;

    public PieceInfo(char c, int x, int y, String pl) {
        this.name = c;
        this.x = x;
        this.y = y;
        this.player = pl;
        this.ingame = true;
        this.underAttack = false;
    }

    public PieceInfo(char c, int x, int y, String pl, boolean b, boolean b2) {
        this.name = c;
        this.x = x;
        this.y = y;
        this.player = pl;
        this.ingame = b;
        this.underAttack = b2;
    }

    public PieceInfo(PieceInfo p) {
        this.name = p.name;
        this.x = p.x;
        this.y = p.y;
        this.player = p.player;
        this.ingame = p.ingame;
        this.underAttack = p.underAttack;
    }

    public PieceInfo() {
        x = 0;
        y = 0;
        name = 'x';
        player = "";
        ingame = false;
        underAttack = false;
    }

    public PieceInfo(boolean b) {
        x = 0;
        y = 0;
        name = 'x';
        player = "";
        ingame = b;
        underAttack = false;
    }

    public void copyPiece(PieceInfo p) {
        this.name = p.name;
        this.x = p.x;
        this.y = p.y;
        this.player = p.player;
        this.ingame = p.ingame;
        this.underAttack = p.underAttack;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public char getName() {
        return name;
    }

    public void setName(char c) {
        name = c;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String pl) {
        player = pl;
    }

    public boolean getingame() {
        return ingame;
    }

    public void setingame(boolean b) {
        ingame = b;
    }

    public boolean getUnderAttack() {
        return underAttack;
    }

    public void setUnderAttack(boolean b) {
        underAttack = b;
    }
}
