import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class NewJPanel extends JPanel {

    private BufferedImage bgImage;
    private URL url;

    public NewJPanel(URL url) {
        try {
            this.url = url;
            bgImage = ImageIO.read(this.url);
        }
        catch (IOException ex) {
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        g.drawImage(bgImage, 0, 0, this.getWidth(), this.getHeight(), null);
    }
}
