import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

class TilesPanel extends JPanel {
	private BufferedImage img;
	private Image[][] tiles;
	private boolean[][] selectedTile;
	private int tileSize;
	private boolean multiSel;

	public TilesPanel(BufferedImage img, int tileSize) {
		super();
		if(img.getWidth() > 1200 || img.getHeight() > 1200){
			BufferedImage scrollImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			JScrollPane scroll = new JScrollPane(new JLabel(new ImageIcon(scrollImg)));
			scroll.setOpaque(false);
			scroll.setFocusable(false);
			scroll.setBorder(null);
			scroll.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent me) {
					Component child = me.getComponent();
					Component parent = child.getParent();

					// transform the mouse coordinate to be relative to the parent
					// component:
					int deltax = child.getX() + me.getX();
					int deltay = child.getY() + me.getY();

					// build new mouse event:
					MouseEvent parentMouseEvent = new MouseEvent(parent, MouseEvent.MOUSE_PRESSED, me.getWhen(),
							me.getModifiers(), deltax, deltay, me.getClickCount(), false);
					// dispatch it to the parent component
					parent.dispatchEvent(parentMouseEvent);
				}
			});
			scroll.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent me) {
					Component child = me.getComponent();
					Component parent = child.getParent();

					// transform the mouse coordinate to be relative to the parent
					// component:
					int deltax = child.getX() + me.getX();
					int deltay = child.getY() + me.getY();

					// build new mouse event:
					MouseEvent parentMouseEvent = new MouseEvent(parent, MouseEvent.MOUSE_DRAGGED, me.getWhen(),
							me.getModifiers(), deltax, deltay, me.getClickCount(), false);
					// dispatch it to the parent component
					parent.dispatchEvent(parentMouseEvent);
				}
			});
			scroll.getViewport().setOpaque(false);
			this.add(scroll);
		}
		this.tileSize = tileSize;
		setBackground(Color.GREEN);
		this.img = img;
		tiles = new Image[img.getHeight() / tileSize][img.getWidth() / tileSize];
		selectedTile = new boolean[img.getHeight() / tileSize][img.getWidth() / tileSize];
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				multiSel = false;
				int x = e.getX() / tileSize;
				System.out.println("X: " + x);
				int y = e.getY() / tileSize;
				System.out.println("Y: " + y);
				falsify();
				selectedTile[y][x] = true;
				repaint();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
				System.out.println("Dragging on Tile");
				multiSel = true;
				int x = e.getX() / tileSize;
				System.out.println("D-X: " + x);
				int y = e.getY() / tileSize;
				System.out.println("D-Y: " + y);
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
					selCoords[0] = c2 * tileSize;
					selCoords[1] = c1 * tileSize;
				}
		return selCoords;
	}
	
	public int[][] getMultiSel(){
		int curSpot = 0;
		int[][] selCoords = new int[getNumSelected()][2];
		for(int c1 = 0; c1 < selectedTile.length; c1 ++)
			for(int c2 = 0; c2 < selectedTile[c1].length; c2 ++)
				if(selectedTile[c1][c2]){
					selCoords[curSpot][0] = c2 * tileSize;
					selCoords[curSpot][1] = c1 * tileSize;
					curSpot ++;
				}
		return selCoords;
	}

	public int getNumSelected(){
		int toReturn = 0;
		for(int c1 = 0; c1 < selectedTile.length; c1 ++)
			for(int c2 = 0; c2 < selectedTile[c1].length; c2 ++)
				if(selectedTile[c1][c2])
					toReturn ++;
		return toReturn;
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
				// System.out.println("Getting Image Coords: (" + c2 + ", " + c1
				// + ")");
				tiles[c1][c2] = img.getSubimage(c2 * tileSize, c1 * tileSize, tileSize, tileSize);
			}
	}

	public void paintComponent(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		for (int c1 = 0; c1 < tiles.length; c1++)
			for (int c2 = 0; c2 < tiles[c1].length; c2++)
				g.drawImage(tiles[c1][c2], c2 * tileSize, c1 * tileSize, null);
		if (!isMultiSel() && isSelected()) {
			int[] boxCoords = getSelected();
			g.setColor(new Color(169, 169, 169, 169));
			g.fillRect(boxCoords[0], boxCoords[1], tileSize, tileSize);
		}else if(isMultiSel()){
			int[][] boxCoords = getMultiSel();
			g.setColor(new Color(169, 169, 169, 169));
			for(int c1 = 0; c1 < boxCoords.length; c1 ++)
				g.fillRect(boxCoords[c1][0], boxCoords[c1][1], tileSize, tileSize);
		}
	}

	public BufferedImage getImg() {
		return img;
	}
	public boolean isMultiSel(){
		return multiSel;
	}
	public void setMultiSel(boolean multi){
		this.multiSel = multi;
	}

}