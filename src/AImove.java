
public class AImove {
    private char name;
    private Point from;
    private Point to;
    private int YourScore;
    private int EnemyScore;
    private boolean losePiece;
    
    public AImove(char n, Point f, Point t, int y, int e, boolean l)
    {
        name=n;
        from=f;
        to=t;
        YourScore=y;
        EnemyScore=e;
        losePiece=l;
    }
    public AImove(Point f, Point t)
    {
        from=f;
        to=t;
    }
    public char getName()
    {
        return name;
    }
    
    public boolean getLosePiece()
    {
        return losePiece;
    }
    
    
    /**
 * return point cord of chosen move
 *
 * @param  s    String to/from point cord
 * @return      point cord (x,y)
 */
    public Point getCord(String s)
    {
        return s.equals("from")?from:to;
    }
    
 /**
 * return score of chosen player
 *
 * @param  s    String you/enemy pieces score
 * @return      score of player pieces
 */
    public int getScore(String s)
    {
        return s.equals("you")?YourScore:EnemyScore;
    }
    
    public String toString()
    {
        return name+":"+" "+from.getX()+from.getY()+" "+to.getX()+to.getY()+" "+EnemyScore+" "+YourScore+" "+losePiece;
    }
}
