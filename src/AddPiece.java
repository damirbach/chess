
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddPiece implements ActionListener {

    static int x1, y1;

    public AddPiece() {
    }
// press button for piece name

    @Override
    public void actionPerformed(ActionEvent e) {
        String s = e.toString();
        int i = s.indexOf("cmd=");
        String s1 = s.substring(i);
        int i2 = s1.indexOf(",");
        String s2 = s1.substring(0, i2);
        String s3 = s2.substring(4);
        Info.setLabel3(s3);
    }
}