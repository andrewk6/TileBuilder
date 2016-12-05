import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TileBuilder{
	public static void main(String[]Args){
		BuilderFrame frame = new BuilderFrame("Test", 32, 32);
		frame.show();
	}
	
}

class BuilderFrame extends JFrame{
	BufferedImage img;
	public BuilderFrame(String title, int width, int height){
		super(title);
		try {
			img = ImageIO.read(this.getClass().getResourceAsStream("Tiles.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(img != null)
			setBounds(0, 0, img.getWidth() + width, img.getHeight() + height);
		else{
			throw new IllegalStateException("Image failed to load");
		}
		JPanel contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		TilesPanel tiles = new TilesPanel();
		tiles.setBounds(width, 0, img.getWidth(), img.getHeight());
		contentPane.add(tiles);
		
		
	}
}

class TilesPanel extends JPanel{
	public TilesPanel(){
		super();
		setBackground(Color.black);
	}
}