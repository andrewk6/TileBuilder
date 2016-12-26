import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class MapPanel extends JPanel {
	private BufferedImage img;
	private Image[][] layer1, layer2, layer3, previewLayer;
	private MoveTile[][] walking;
	private int drawingLayer;
	private int width;
	private int height;
	private TilesPanel tPane;
	private int tileSize;
	private boolean drawMoveLayer;
	private Image background;
	private MovePanel mPane;
	private ToolPane toolPane;
	private int[][] multiDrawRef;

	public MapPanel(BufferedImage img, int width, int height, TilesPanel tPane, MovePanel mPane, 
			int tileSize, ToolPane toolPane) {
		super();
		multiDrawRef = new int[2][2];
		this.toolPane = toolPane;
		setPreferredSize(new Dimension(width, height));
		background = null;
		setBackground(Color.GRAY);
		this.tileSize = tileSize;
		this.width = width;
		this.height = height;
		this.img = img;
		this.tPane = tPane;
		this.mPane = mPane;
		setBackground(Color.BLACK);
		drawingLayer = 0;
		layer1 = new Image[height / tileSize][width / tileSize];
		layer2 = new Image[height / tileSize][width / tileSize];
		layer3 = new Image[height / tileSize][width / tileSize];
		previewLayer = new Image[height / tileSize][width / tileSize];
		walking = new MoveTile[height / tileSize][width / tileSize];
		fillWalking();
//		nullWalking();
		buildListeners();
	}

	public void fillWalking(){
		for(int c1 = 0; c1 < walking.length; c1 ++)
			for(int c2 = 0; c2 < walking[c1].length; c2 ++)
				walking[c1][c2] = new MoveTile(Color.RED, 1);
	}
	
	public void buildListeners() {
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				int x, y;
				x = e.getX() / tileSize;
				y = e.getY() / tileSize;
				System.out.println("Locs: (" + x + ", " + y + ")");
				try {
					if (getWorkingLayer() >= 0 && getWorkingLayer() < 3) {
						if (previewLayer[e.getY() / tileSize][e.getX() / tileSize] == null) {
							clearPreview();
							if (tPane.isSelected()) {
								previewLayer[e.getY() / tileSize][e.getX() / tileSize] = img.getSubimage(
										tPane.getSelected()[0], tPane.getSelected()[1], tileSize, tileSize);
							}
							repaint();
						}
					} else {
						clearPreview();
						repaint();
					}
				} catch (ArrayIndexOutOfBoundsException err) {
					clearPreview();
					repaint();
				}
			}

			private void clearPreview() {
				for (int c1 = 0; c1 < previewLayer.length; c1++)
					for (int c2 = 0; c2 < previewLayer[c1].length; c2++)
						previewLayer[c1][c2] = null;
			}

			public void mouseDragged(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && !e.isShiftDown()) {
					if (tPane.isSelected() || mPane.isSelected()) {
						if(toolPane.getTool() == ToolPane.POINT){
							System.out.println("Drawing Layer");
							switch (drawingLayer) {
							case 0:
								if (tPane.isSelected() && !tPane.isMultiSel())
									layer1[e.getY() / tileSize][e.getX() / tileSize] = img.getSubimage(
											tPane.getSelected()[0], tPane.getSelected()[1], tileSize, tileSize);
								else if (tPane.isMultiSel()) {
									System.out.println("Multipane Drawing");
									layer1 = tPane.multiUpdate(layer1, e.getY() / tileSize, e.getX() / tileSize);
								}
								break;
							case 1:
								if (tPane.isSelected())
									layer2[e.getY() / tileSize][e.getX() / tileSize] = img.getSubimage(
											tPane.getSelected()[0], tPane.getSelected()[1], tileSize, tileSize);
								break;
							case 2:
								if (tPane.isSelected())
									layer3[e.getY() / tileSize][e.getX() / tileSize] = img.getSubimage(
											tPane.getSelected()[0], tPane.getSelected()[1], tileSize, tileSize);
								break;
							case 3:
								if (mPane.isSelected())
									walking[e.getY() / tileSize][e.getX() / tileSize] = mPane
											.getTile(mPane.getSelected()[0] / tileSize, mPane.getSelected()[1] / tileSize);
								break;
							}
						}else{
							multiDrawRef[1][0] = e.getX() / tileSize;
							multiDrawRef[1][1] = e.getY() / tileSize;
						}
					}
				} else if (SwingUtilities.isRightMouseButton(e) && !e.isShiftDown()) {
					System.out.println("Erasing");
					switch (drawingLayer) {
					case 0:
						if (tPane.isSelected())
							layer1[e.getY() / tileSize][e.getX() / tileSize] = null;
						break;
					case 1:
						if (tPane.isSelected())
							layer2[e.getY() / tileSize][e.getX() / tileSize] = null;
						break;
					case 2:
						if (tPane.isSelected())
							layer3[e.getY() / tileSize][e.getX() / tileSize] = null;
						break;
					case 3:
						if (mPane.isSelected())
							walking[e.getY() / tileSize][e.getX() / tileSize] = null;
						break;
					}
				} else if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()) {
					if (tPane.isSelected() || mPane.isSelected()) {
						System.out.println("Drawing Layer");
						switch (drawingLayer) {
						case 0:
							if (tPane.isSelected())
								fill(layer1, e.getY() / tileSize,
										e.getX() / tileSize, img.getSubimage(tPane.getSelected()[0],
												tPane.getSelected()[1], tileSize, tileSize),
										layer1[e.getY() / tileSize][e.getX() / tileSize]);
							break;
						case 1:
							if (tPane.isSelected())
								fill(layer2, e.getY() / tileSize,
										e.getX() / tileSize, img.getSubimage(tPane.getSelected()[0],
												tPane.getSelected()[1], tileSize, tileSize),
										layer2[e.getY() / tileSize][e.getX() / tileSize]);
							break;
						case 2:
							if (tPane.isSelected())
								fill(layer3, e.getY() / tileSize,
										e.getX() / tileSize, img.getSubimage(tPane.getSelected()[0],
												tPane.getSelected()[1], tileSize, tileSize),
										layer3[e.getY() / tileSize][e.getX() / tileSize]);
							break;
						case 3:
							if (mPane.isSelected())
								walking[e.getY() / tileSize][e.getX() / tileSize] = mPane
										.getTile(mPane.getSelected()[0] / tileSize, mPane.getSelected()[1] / tileSize);
							break;
						}
					}
				} else if (SwingUtilities.isRightMouseButton(e) && e.isShiftDown()) {
					System.out.println("Erasing");
					switch (drawingLayer) {
					case 0:
						if (tPane.isSelected())
							try {
								fill(layer1, e.getY() / tileSize, e.getX() / tileSize,
										ImageIO.read(this.getClass().getResourceAsStream("Blank Image.png")),
										layer1[e.getY() / tileSize][e.getX() / tileSize]);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						break;
					case 1:
						if (tPane.isSelected())
							try {
								fill(layer2, e.getY() / tileSize, e.getX() / tileSize,
										ImageIO.read(this.getClass().getResourceAsStream("Blank Image.png")),
										layer2[e.getY() / tileSize][e.getX() / tileSize]);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						break;
					case 2:
						if (tPane.isSelected())
							try {
								fill(layer3, e.getY() / tileSize, e.getX() / tileSize,
										ImageIO.read(this.getClass().getResourceAsStream("Blank Image.png")),
										layer3[e.getY() / tileSize][e.getX() / tileSize]);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						break;
					case 3:
						if (mPane.isSelected())
							walking[e.getY() / tileSize][e.getX() / tileSize] = mPane
									.getTile(mPane.getSelected()[0] / tileSize, mPane.getSelected()[1] / tileSize);
						break;
					}
					clearEmpty();
				} else {
					System.out.println("Error");
				}
				repaint();
			}
		});
		addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent e) {
				clearPreview();
				repaint();
			}
			
			public void mouseReleased(MouseEvent e){
				if(toolPane.getTool() == ToolPane.RECT){
					int length = 0, width = 0;
					length = multiDrawRef[1][0] - multiDrawRef[0][0];
					width = multiDrawRef[1][1] - multiDrawRef[0][1];
					if(width < 0 && length > 0){
						
					}
				}
			}

			private void clearPreview() {
				for (int c1 = 0; c1 < previewLayer.length; c1++)
					for (int c2 = 0; c2 < previewLayer[c1].length; c2++)
						previewLayer[c1][c2] = null;
			}

			public void mousePressed(MouseEvent e) {
				System.out.println("Clicked");
				if (SwingUtilities.isLeftMouseButton(e) && !e.isShiftDown()) {
					if (tPane.isSelected() || mPane.isSelected()) {
						if(toolPane.getTool() == ToolPane.POINT){
							System.out.println("Drawing Layer");
							switch (drawingLayer) {
							case 0:
								if (tPane.isSelected() && !tPane.isMultiSel())
									layer1[e.getY() / tileSize][e.getX() / tileSize] = img.getSubimage(
											tPane.getSelected()[0], tPane.getSelected()[1], tileSize, tileSize);
								else if (tPane.isMultiSel()) {
									System.out.println("Multipane Drawing");
									layer1 = tPane.multiUpdate(layer1, e.getY() / tileSize, e.getX() / tileSize);
								}
								break;
							case 1:
								if (tPane.isSelected())
									layer2[e.getY() / tileSize][e.getX() / tileSize] = img.getSubimage(
											tPane.getSelected()[0], tPane.getSelected()[1], tileSize, tileSize);
								break;
							case 2:
								if (tPane.isSelected())
									layer3[e.getY() / tileSize][e.getX() / tileSize] = img.getSubimage(
											tPane.getSelected()[0], tPane.getSelected()[1], tileSize, tileSize);
								break;
							case 3:
								if (mPane.isSelected())
									walking[e.getY() / tileSize][e.getX() / tileSize] = mPane
											.getTile(mPane.getSelected()[0] / tileSize, mPane.getSelected()[1] / tileSize);
								break;
							}
						}else{
							multiDrawRef[0][0] = e.getX() / tileSize;
							multiDrawRef[0][1] = e.getY() / tileSize;
						}
					}
				} else if (SwingUtilities.isRightMouseButton(e) && !e.isShiftDown()) {
					System.out.println("Erasing");
					switch (drawingLayer) {
					case 0:
						if (tPane.isSelected())
							layer1[e.getY() / tileSize][e.getX() / tileSize] = null;
						break;
					case 1:
						if (tPane.isSelected())
							layer2[e.getY() / tileSize][e.getX() / tileSize] = null;
						break;
					case 2:
						if (tPane.isSelected())
							layer3[e.getY() / tileSize][e.getX() / tileSize] = null;
						break;
					case 3:
						if (mPane.isSelected())
							walking[e.getY() / tileSize][e.getX() / tileSize] = null;
						break;
					}
				} else if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()) {
					if (tPane.isSelected() || mPane.isSelected()) {
						System.out.println("Drawing Layer");
						switch (drawingLayer) {
						case 0:
							if (tPane.isSelected())
								fill(layer1, e.getY() / tileSize,
										e.getX() / tileSize, img.getSubimage(tPane.getSelected()[0],
												tPane.getSelected()[1], tileSize, tileSize),
										layer1[e.getY() / tileSize][e.getX() / tileSize]);
							break;
						case 1:
							if (tPane.isSelected())
								fill(layer2, e.getY() / tileSize,
										e.getX() / tileSize, img.getSubimage(tPane.getSelected()[0],
												tPane.getSelected()[1], tileSize, tileSize),
										layer2[e.getY() / tileSize][e.getX() / tileSize]);
							break;
						case 2:
							if (tPane.isSelected())
								fill(layer3, e.getY() / tileSize,
										e.getX() / tileSize, img.getSubimage(tPane.getSelected()[0],
												tPane.getSelected()[1], tileSize, tileSize),
										layer3[e.getY() / tileSize][e.getX() / tileSize]);
							break;
						case 3:
							if (mPane.isSelected())
								walking[e.getY() / tileSize][e.getX() / tileSize] = mPane
										.getTile(mPane.getSelected()[0] / tileSize, mPane.getSelected()[1] / tileSize);
							break;
						}
					}
				} else if (SwingUtilities.isRightMouseButton(e) && e.isShiftDown()) {
					System.out.println("Erasing");
					switch (drawingLayer) {
					case 0:
						if (tPane.isSelected())
							try {
								fill(layer1, e.getY() / tileSize, e.getX() / tileSize,
										ImageIO.read(this.getClass().getResourceAsStream("Blank Image.png")),
										layer1[e.getY() / tileSize][e.getX() / tileSize]);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						break;
					case 1:
						if (tPane.isSelected())
							try {
								fill(layer2, e.getY() / tileSize, e.getX() / tileSize,
										ImageIO.read(this.getClass().getResourceAsStream("Blank Image.png")),
										layer2[e.getY() / tileSize][e.getX() / tileSize]);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						break;
					case 2:
						if (tPane.isSelected())
							try {
								fill(layer3, e.getY() / tileSize, e.getX() / tileSize,
										ImageIO.read(this.getClass().getResourceAsStream("Blank Image.png")),
										layer3[e.getY() / tileSize][e.getX() / tileSize]);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						break;
					case 3:
						if (mPane.isSelected())
							walking[e.getY() / tileSize][e.getX() / tileSize] = mPane
									.getTile(mPane.getSelected()[0] / tileSize, mPane.getSelected()[1] / tileSize);
						break;
					}
					clearEmpty();
				} else {
					System.out.println("Error");
				}
				repaint();
			}
		});
	}

	public void clearEmpty() {
		try {
			Image clear = ImageIO.read(this.getClass().getResourceAsStream("Tiles.png"));
			for (int c1 = 0; c1 < layer1.length; c1++)
				for (int c2 = 0; c2 < layer1[c1].length; c2++) {
					if (layer1[c1][c2] != null)
						if (layer1[c1][c2].equals(clear))
							layer1[c1][c2] = null;
					if (layer2[c1][c2] != null)
						if (layer2[c1][c2].equals(clear))
							layer2[c1][c2] = null;
					if (layer3[c1][c2] != null)
						if (layer3[c1][c2].equals(clear))
							layer3[c1][c2] = null;
					if (walking[c1][c2] != null)
						if (walking[c1][c2].getValue() == -2)
							walking[c1][c2] = null;
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void nullWalking() {
		for (int c1 = 0; c1 < walking.length; c1++)
			for (int c2 = 0; c2 < walking[c1].length; c2++)
				walking[c1][c2] = null;
	}

	public void fill(Image[][] layer, int x, int y, Image newTile, Image oldTile) {
		if (x < 0)
			return;
		if (y < 0)
			return;
		if (x >= layer.length)
			return;
		if (y >= layer[x].length)
			return;

		Image img = layer[x][y];
		if ((img != null && oldTile == null) || img == null && oldTile != null) {
			return;
		}
		if (oldTile != null)
			if (!imgToSign(oldTile).equals(imgToSign(img))) {
				return;
			}
		System.out.println("Painting and moving");
		layer[x][y] = newTile;

		fill(layer, x - 1, y, newTile, oldTile);
		fill(layer, x + 1, y, newTile, oldTile);
		fill(layer, x, y - 1, newTile, oldTile);
		fill(layer, x, y + 1, newTile, oldTile);
	}

	public String imgToSign(Image img) {
		BufferedImage bimg = (BufferedImage) img;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			ByteBuffer bb = ByteBuffer.allocate(4 * bimg.getWidth());
			for (int y = bimg.getHeight() - 1; y >= 0; y--) {
				bb.clear();
				for (int x = bimg.getWidth() - 1; x >= 0; x--) {
					bb.putInt(bimg.getRGB(x, y));
				}
				md.update(bb.array());
			}
			byte[] digBytes = md.digest();
			StringBuilder sb = new StringBuilder();
			for (byte b : digBytes) {
			  sb.append(String.format("%02X", b & 0xff));
			}
			String signature = sb.toString();
			return signature;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	public void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		if (background != null)
			g.drawImage(background, 0, 0, null);
		for (int c1 = 0; c1 < layer1.length; c1++)
			for (int c2 = 0; c2 < layer1[c1].length; c2++) {
				if (layer1[c1][c2] != null)
					g.drawImage(layer1[c1][c2], c2 * tileSize, c1 * tileSize, null);
				if (layer2[c1][c2] != null)
					g.drawImage(layer2[c1][c2], c2 * tileSize, c1 * tileSize, null);
				if (layer3[c1][c2] != null)
					g.drawImage(layer3[c1][c2], c2 * tileSize, c1 * tileSize, null);
				if (previewLayer[c1][c2] != null)
					g.drawImage(previewLayer[c1][c2], c2 * tileSize, c1 * tileSize, null);
			}
		if (drawMoveLayer) {
			System.out.println("Drawing Move");
			drawingMoveTiles(g);
		}
		drawGridLines(g);
	}

	private void drawingMoveTiles(Graphics g) {
		System.out.println("Drawing tile");
		for (int c1 = 0; c1 < walking.length; c1++)
			for (int c2 = 0; c2 < walking[c1].length; c2++) {
				if (walking[c1][c2] != null) {
					g.setColor(new Color(walking[c1][c2].getColor().getRed(), walking[c1][c2].getColor().getGreen(),
							walking[c1][c2].getColor().getBlue(), 80));
					g.fillRect(c2 * tileSize, c1 * tileSize, tileSize, tileSize);
					g.setFont(new Font("Monospaced", Font.BOLD, 32));
					g.setColor(Color.cyan);
					g.drawString("" + walking[c1][c2].getValue(), c2 * tileSize, c1 * tileSize + tileSize);
				}
			}
	}

	private void drawGridLines(Graphics g) {
		g.setColor(Color.WHITE);
		for (int c1 = tileSize; c1 < ((width < height) ? height : width); c1 += tileSize) {
			g.drawLine(c1, 0, c1, getHeight());
			g.drawLine(0, c1, getWidth(), c1);
		}

	}

	public int getSetWidth() {
		return width;
	}

	public int getSetHeight() {
		return height;
	}

	public TilesPanel getTPane() {
		return tPane;
	}

	public MovePanel getMPane() {
		return mPane;
	}

	public void setMPane(MovePanel mPane) {
		this.mPane = mPane;
	}

	public int getTileSize() {
		return tileSize;
	}

	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	public int getWorkingLayer() {
		return drawingLayer;
	}

	public void setWorkingLayer(int layer) {
		drawingLayer = layer;
	}

	public boolean isDrawMove() {
		return drawMoveLayer;
	}

	public void setDrawMove(boolean drawing) {
		drawMoveLayer = drawing;
	}

	public void setBackground(Image background) {
		this.background = background.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		repaint();
	}

	public void clearBackground() {
		this.background = null;
		repaint();
	}

	public void setTileMap(TilesPanel tiles) {
		this.tPane = tiles;
		img = tiles.getImg();
	}
	
	public BufferedImage exportImg(){
		BufferedImage collapsedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = collapsedImage.getGraphics();
		g.drawImage(background, 0, 0, null);
		for (int c1 = 0; c1 < layer1.length; c1++)
			for (int c2 = 0; c2 < layer1[c1].length; c2++) {
				if (layer1[c1][c2] != null)
					g.drawImage(layer1[c1][c2], c2 * tileSize, c1 * tileSize, null);
				if (layer2[c1][c2] != null)
					g.drawImage(layer2[c1][c2], c2 * tileSize, c1 * tileSize, null);
				if (layer3[c1][c2] != null)
					g.drawImage(layer3[c1][c2], c2 * tileSize, c1 * tileSize, null);
			}
		return collapsedImage;
	}
	
	public MoveTile[][] exportMove(){
		return walking;
	}
}