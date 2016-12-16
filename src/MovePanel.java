import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class MovePanel extends JPanel {
	private final int ROWS = 5;
	private final int COLUMNS = 2;
	private MoveTile[][] moveTiles;
	private int width, height;
	private boolean[][] selectedTile;
	private int tileSize;

	public MovePanel(int tileSize) {
		this.width = tileSize * ROWS;
		this.height = tileSize * COLUMNS;
		moveTiles = new MoveTile[ROWS][COLUMNS];
		fillTiles();
		this.tileSize = tileSize;
		selectedTile = new boolean[ROWS][COLUMNS];
		fillColors();
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int x = e.getX() / tileSize;
				int y = e.getY() / tileSize;
				falsify();
				selectedTile[y][x] = true;
				repaint();
			}
		});
		falsify(); 
	}

	private void fillColors() {
		moveTiles[0][0].setColor(Color.green);
		moveTiles[0][1].setColor(Color.red);
		moveTiles[1][0].setColor(Color.cyan);
		moveTiles[1][1].setColor(Color.orange);
		moveTiles[2][0].setColor(Color.yellow);
		moveTiles[2][1].setColor(Color.white);
		moveTiles[3][0].setColor(Color.cyan);
		moveTiles[3][1].setColor(Color.orange);
		moveTiles[4][0].setColor(Color.yellow);
		moveTiles[4][1].setColor(Color.white);
	}

	private void fillTiles() {
		int moveNum = 0;
		for (int c1 = 0; c1 < moveTiles.length; c1++) {
			for (int c2 = 0; c2 < moveTiles[c1].length; c2++) {
				moveTiles[c1][c2] = new MoveTile(moveNum);
				moveNum++;
			}
		}
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
					selCoords[0] = c1 * tileSize;
					selCoords[1] = c2 * tileSize;
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

	public void paintComponent(Graphics g) {
		g.setColor(Color.gray);
		g.setFont(new Font("Monospaced", Font.BOLD, 32));
		g.fillRect(0, 0, getWidth(), getHeight());
		for (int c1 = 0; c1 < ROWS; c1++)
			for (int c2 = 0; c2 < COLUMNS; c2++) {
				g.setColor(moveTiles[c1][c2].getColor());
				g.fillRect((c2 * tileSize), (c1 * tileSize), tileSize, tileSize);
				g.setColor(Color.black);
				repaint();
				g.drawString("" + moveTiles[c1][c2].getValue(), (c2 * tileSize),
						(c1 * tileSize) + (tileSize - tileSize / 4));
			}
		if (isSelected()) {
			int[] coords = getSelected();
			g.setColor(Color.black);
			g.fillRect(coords[1], coords[0], tileSize, tileSize);
			g.setColor(Color.white);
			g.drawString("" + moveTiles[coords[0] / tileSize][coords[1] / tileSize].getValue(), coords[1],
					coords[0] + (tileSize - tileSize / 4));
		}
	}

	public int getSelWidth() {
		return this.width;
	}

	public int getSelHeight() {
		return this.height;
	}

	public MoveTile getTile(int x, int y) {
		return moveTiles[x][y];
	}
}

