import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
				falsify();
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
	
	private void drawGridLines(Graphics g) {
		g.setColor(Color.black);
		for (int c1 = tileSize; c1 < ((img.getWidth() < img.getHeight()) ? img.getHeight() : img.getWidth()); c1 += tileSize) {
			if(!(c1 > img.getWidth()))
				g.drawLine(c1, 0, c1, img.getHeight());
			if(!(c1 > img.getHeight()))
				g.drawLine(0, c1, img.getWidth(), c1);
		}
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
		drawGridLines(g);
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
	
	public ArrayList<ArrayList<BufferedImage>> getImages(){
		ArrayList<ArrayList<BufferedImage>> toReturn = new ArrayList<ArrayList<BufferedImage>>();
		for(int c1 = 0; c1 < selectedTile.length; c1 ++){
			toReturn.add(new ArrayList<BufferedImage>());
			for(int c2 = 0; c2 < selectedTile[c1].length; c2 ++){
				if(selectedTile[c1][c2]){
					toReturn.get(c1).add(img.getSubimage(c1 * tileSize, c2 * tileSize, tileSize, tileSize));
				}
			}
		}
		return toReturn;
	}
	//Trim 2d array created by directly turning tiles into an array, square it out then add it to the larger one
	public Image[][] multiUpdate(Image[][] layer1, int x, int y) {
		int startY = 0;
		Image[][] trimmed = trim(buildArray());
		Image[][] moddedLayer = new Image[layer1.length][layer1[0].length];
		for(int c1 = 0; c1 < trimmed.length; c1 ++){
			for(int c2 = 0; c2 < trimmed[c2].length; c2 ++){
				if(trimmed[c1][c2] != null){
					if(((c1 + x) < moddedLayer.length) && ((c2 + y) < moddedLayer[c1].length)){
						moddedLayer[c1 + x][c2 + y] = trimmed[c1][c2];
					}
				}
			}
		}
		return moddedLayer;
	}
	public Image[][] buildArray(){
		Image[][] tilesTemp = tiles;
		for(int c1 = 0; c1 < tilesTemp.length; c1 ++){
			for(int c2 = 0; c2 < tilesTemp[c1].length; c2 ++){
				if(!selectedTile[c1][c2]){
					tilesTemp[c1][c2] = null;
				}
			}
		}
		return tilesTemp;
	}
	public Image[][] trim(Image[][] imgArray){
		int firstCol = -1;
		int curCol = 0;
		while(firstCol < 0){
			for(int c1 = 0; c1 < imgArray.length; c1 ++)
				if(imgArray[c1][curCol] != null)
					firstCol = curCol;
				else
					curCol ++;
		}
		int lastCol = -1;
		curCol = imgArray[0].length - 1;;
		while(firstCol < 0){
			for(int c1 = 0; c1 < imgArray.length; c1 ++)
				if(imgArray[c1][curCol] != null)
					lastCol = curCol;
				else
					curCol --;
		}
		Image[][] toReturn = new Image
				[imgArray.length]
						[lastCol - firstCol];
		for(int c1 = 0; c1 < imgArray.length; c1 ++){
			for(int c2 = firstCol; c2 < lastCol; c2 ++){
				toReturn[c1][c2-firstCol] = imgArray[c1][c2];
			}
		}
		return toReturn;
	}
	
	public boolean[] fillFalse(int length){
		boolean[] toReturn = new boolean[length];
		for(int c1 = 0; c1 < toReturn.length; c1 ++)
			toReturn[c1] = false;
		return toReturn;
	}
	
//	public Dimension getPreferredSize()
//	{
//	    return new Dimension(300, 300);
//	}
}