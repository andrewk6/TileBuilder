import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class TileBuilder {
	public static void main(String[] Args) {
		BuilderFrame frame = new BuilderFrame("Test", 320, 320);

		frame.show();
	}
}

class BuilderFrame extends JFrame {
	BufferedImage img;
	JPanel contentPane;

	public BuilderFrame(String title, int width, int height) {
		super(title);
		try {
			img = ImageIO.read(this.getClass().getResourceAsStream("Tiles.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (img != null)
			setBounds(0, 0, img.getWidth() + width + 20, img.getHeight() + height);
		else {
			throw new IllegalStateException("Image failed to load");
		}
		JButton exit = new JButton("Exit");
		exit.setFont(new Font("Monospaced", Font.BOLD, 20));
		exit.setBounds(0, 0, 100, 50);
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		TilesPanel tiles = new TilesPanel(img);
		tiles.setBounds(width, 0, img.getWidth(), getHeight());
		MapPanel mapPane = new MapPanel(img, width, height, tiles);
		mapPane.setBounds(0,0,width,height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test(mapPane);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		contentPane.add(exit);

	}
	public void test(MapPanel mapPane){
		JInternalFrame map = new JInternalFrame();
		map.setBounds(100, 100, mapPane.getSetWidth(), mapPane.getSetHeight());
		map.add(mapPane);
		JInternalFrame tilesFrame = new JInternalFrame();
		TilesPanel tiles = mapPane.getTPane();
		tilesFrame.setBounds(0,0, tiles.getImg().getWidth() + 40, tiles.getImg().getHeight() + 40);
		tilesFrame.add(tiles);
		contentPane.add(map);
		contentPane.add(tilesFrame);
		map.setVisible(true);
		tilesFrame.setVisible(true);
	}
}

class MapPanel extends JPanel{
	private BufferedImage img;
	private Image[][] tiles;
	private int width;
	private int height;
	private TilesPanel tPane;
	
	public MapPanel(BufferedImage img, int width, int height, TilesPanel tPane){
		super();
		this.width = width;
		this.height = height;
		this.img = img;
		this.tPane = tPane;
		setBackground(Color.BLACK);
		tiles = new Image[height / 32][width / 32];
		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
					System.out.println("Yes draw");
					if(SwingUtilities.isLeftMouseButton(e)){
						System.out.println("Left Click");
						if(tPane.isSelected()){
							System.out.println("Drawing");
							tiles[e.getY() / 32][e.getX() / 32] = img.getSubimage(tPane.getSelected()[0], tPane.getSelected()[1], 32, 32);
						}
					}else if(e.getButton() == MouseEvent.BUTTON2){
						System.out.println("Erasing");
						tiles[e.getY() / 32][e.getX() / 32] = null;
					}else{
						System.out.println("Error");
					}
				
				repaint();
			}
		});
		addMouseListener(new MouseAdapter(){		
			public void mousePressed(MouseEvent e){
				if(e.getButton() == MouseEvent.BUTTON1){
					if(tPane.isSelected())
						tiles[e.getY() / 32][e.getX() / 32] = img.getSubimage(tPane.getSelected()[0], tPane.getSelected()[1], 32, 32);
				}else if(e.getButton() == MouseEvent.BUTTON2){
					tiles[e.getY() / 32][e.getX() / 32] = null;
				}
				repaint();
			}
		});
	}
	
	public void paintComponent(Graphics g){
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		for (int c1 = 0; c1 < tiles.length; c1++)
			for (int c2 = 0; c2 < tiles[c1].length; c2++)
				if(tiles[c1][c2] != null)
					g.drawImage(tiles[c1][c2], c2 * 32, c1 * 32, null);
	}
	
	public int getSetWidth(){
		return width;
	}
	public int getSetHeight(){
		return height;
	}
	public TilesPanel getTPane(){
		return tPane;
	}
}

class TilesPanel extends JPanel {
	private BufferedImage img;
	private Image[][] tiles;
	private boolean[][] selectedTile;

	public TilesPanel(BufferedImage img) {
		super();
		setBackground(Color.GREEN);
		this.img = img;
		tiles = new Image[img.getHeight() / 32][img.getWidth() / 32];
		selectedTile = new boolean[img.getHeight() / 32][img.getWidth() / 32];
		 addMouseListener(new MouseAdapter() {
             private Color background;

             @Override
             public void mousePressed(MouseEvent e) {
        		int x = e.getX() / 32;
        		int y = e.getY() / 32;
        		falsify();
        		selectedTile[y][x] = true;
        		repaint();
             }
         });
		falsify();
		splitTiles();
	}

	private void falsify() {
		for (int c1 = 0; c1 < selectedTile.length; c1++)
			for (int c2 = 0; c2 < selectedTile[c1].length; c2++)
				selectedTile[c1][c2] = false;
	}

	public int[] getSelected() {
		int[] selCoords = new int[2];
		for (int c1 = 0; c1 < selectedTile.length; c1++)
			for (int c2 = 0; c2 < selectedTile[c1].length; c2++)
				if (selectedTile[c1][c2]) {
					selCoords[0] = c2 * 32;
					selCoords[1] = c1 * 32;
				}
		return selCoords;
	}

	public boolean isSelected() {
		for (int c1 = 0; c1 < selectedTile.length; c1++)
			for (int c2 = 0; c2 < selectedTile[c1].length; c2++)
				if (selectedTile[c1][c2])
					return true;
		return false;
	}

	private void splitTiles() {
		for (int c1 = 0; c1 < tiles.length; c1++)
			for (int c2 = 0; c2 < tiles[c1].length; c2++) {
				System.out.println("Getting Image Coords: (" + c2 + ", " + c1 + ")");
				tiles[c1][c2] = img.getSubimage(c2 * 32, c1 * 32, 32, 32);
			}
	}

	public void paintComponent(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		for (int c1 = 0; c1 < tiles.length; c1++)
			for (int c2 = 0; c2 < tiles[c1].length; c2++)
				g.drawImage(tiles[c1][c2], c2 * 32, c1 * 32, null);
		if (isSelected()) {
			int[] boxCoords = getSelected();
			g.setColor(new Color(169, 169, 169, 169));
			g.fillRect(boxCoords[0], boxCoords[1], 32, 32);
		}
	}
	
	public BufferedImage getImg(){
		return img;
	}

}