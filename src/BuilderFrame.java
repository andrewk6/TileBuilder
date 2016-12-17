import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import javafx.scene.layout.Background;

public class BuilderFrame extends JFrame {
	private boolean working;
	private final Font FONT = new Font("Monospaced", Font.BOLD, 20);
	private BufferedImage img;
	private JDesktopPane contentPane;
	private JPanel mainPane;
	private JPanel menuPane;
	private int tileSize;
	private JInternalFrame tilesFrame;
	private JInternalFrame map;
	private JInternalFrame moveFrame;
	private MapPanel mapPane;

	public BuilderFrame(String title, int tileSize, int width, int height) {
		super(title);
		working = false;
		this.tileSize = tileSize;
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
		exit.setFont(FONT);
		exit.setBounds(0, 0, 100, 50);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		contentPane = new JDesktopPane();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		mainPane = new JPanel();
		mainPane.setLayout(null);
		menuPane = new JPanel();
		menuPane.setBackground(Color.white);
		menuPane.setBorder(new MatteBorder(0, 0, 3, 0, Color.black));
		contentPane.add(mainPane, BorderLayout.CENTER);
		contentPane.add(menuPane, BorderLayout.NORTH);
		mapPane = new MapPanel(img, width, height, new TilesPanel(img, tileSize), new MovePanel(tileSize), tileSize);
		buildInternalFrames(mapPane, mapPane.getMPane());
		JButton background = new JButton("Background");
		background.setFont(FONT);
		background.setBounds(0, 0, 100, 50);
		background.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				int option = JOP.buttonInput("Background Option: ", new String[]{
						"Load Background", "Clear Background", "Cancel"});
				if(option == 0){
					JFileChooser fc = new JFileChooser();
					if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
						try {
							mapPane.setBackground(ImageIO.read(fc.getSelectedFile()));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}else if(option == 1){
					mapPane.clearBackground();
				}
			}
		});
		menuPane.add(background);
		JButton tileSheet = new JButton("Load Tiles");
		tileSheet.setFont(FONT);
		tileSheet.setBounds(0,0,100,50);
		tileSheet.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				try {
					mapPane.setTileMap(new TilesPanel(ImageIO.read(this.getClass().getResourceAsStream("terrain.png")), tileSize));
					resetTilesPanel();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		menuPane.add(tileSheet);
		JButton newMap = new JButton("New Map");
		newMap.setFont(FONT);
		newMap.setBounds(0, 0, 100, 50);
		newMap.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				TilesPanel tPane = mapPane.getTPane();
				MovePanel movePane = mapPane.getMPane();
				mapPane = new MapPanel(mapPane.getTPane().getImg(), 640, 640, mapPane.getTPane(), mapPane.getMPane(), tileSize);
				map.dispose();
				map = null;
				buildMapFrame(mapPane, moveFrame);
				resetTilesPanel();
//				working = true;
			}
		});
		menuPane.add(newMap);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		menuPane.add(exit); 

	}
	
	public void resetTilesPanel(){
		tilesFrame.dispose();
		tilesFrame = null;
		buildTileFrame(mapPane);
		
	}

	public void buildInternalFrames(MapPanel mapPane, MovePanel movePane) {
		moveFrame = new JInternalFrame();
		moveFrame.setBounds(400, 400, movePane.getSelHeight() + tileSize, movePane.getSelWidth() + tileSize);
		moveFrame.add(movePane);
		
		buildTileFrame(mapPane);
		buildMapFrame(mapPane, moveFrame);
		
		mainPane.add(moveFrame);
		moveFrame.setVisible(false);
	}

	private void buildTileFrame(MapPanel mapPane) {
		tilesFrame = new JInternalFrame();
		TilesPanel tiles = mapPane.getTPane();
		tiles.setPreferredSize(new Dimension(tiles.getImg().getWidth(), tiles.getImg().getHeight()));
//		tilesFrame.setBounds(100, 100, (390 < tiles.getWidth()) ? 390 : tiles.getWidth() + tileSize / 2, 
//				(390 < mapPane.getWidth()) ? 390 : mapPane.getSetHeight() + tileSize / 2);
		tilesFrame.setBounds(500, 500, 390, 390);
		JScrollPane scroll = new JScrollPane(tiles);
//		scroll.setOpaque(false);
//		scroll.getViewport().setOpaque(false);
		tilesFrame.add(scroll);
		mainPane.add(tilesFrame);
		tilesFrame.setVisible(true);
	}
	
	public void buildMapFrame(MapPanel mapPane, JInternalFrame moveFrame){
		map = new JInternalFrame();
		JPanel buttonsPane = buildLayerButtons(mapPane, moveFrame);
//		map.setBounds(100, 100, (390 < mapPane.getWidth()) ? 390 : mapPane.getWidth() + tileSize / 2, 
//				(390 < mapPane.getHeight()) ? 390 : mapPane.getSetHeight() + tileSize / 2);
		map.setBounds(100, 100, 390, 390);
		map.setLayout(new BorderLayout());
		mapPane.setPreferredSize(new Dimension(mapPane.getSetWidth() + tileSize / 2, mapPane.getSetHeight() + tileSize / 2));
		JScrollPane scroll = new JScrollPane(mapPane);
		map.add(scroll, BorderLayout.CENTER);
		map.add(buttonsPane, BorderLayout.SOUTH);

		mainPane.add(map);
		map.setVisible(true);
	}

	public JPanel buildLayerButtons(MapPanel mapPane, JInternalFrame moveFrame) {
		JPanel bPane = new JPanel();
		JButton layer1 = new JButton("Layer 1");
		layer1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mapPane.setWorkingLayer(0);
				mapPane.setDrawMove(false);
				moveFrame.setVisible(false);
				mapPane.repaint();
			}
		});
		JButton layer2 = new JButton("Layer 2");
		layer2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mapPane.setWorkingLayer(1);
				mapPane.setDrawMove(false);
				moveFrame.setVisible(false);
				mapPane.repaint();
			}
		});
		JButton layer3 = new JButton("Layer 3");
		layer3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Layer 3");
				mapPane.setWorkingLayer(2);
				mapPane.setDrawMove(false);
				moveFrame.setVisible(false);
				mapPane.repaint();
			}
		});
		JButton moveLayer = new JButton("Move Layer");
		moveLayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Changing Layer");
				mapPane.setWorkingLayer(3);
				mapPane.setDrawMove(true);
				moveFrame.setVisible(true);
				mapPane.repaint();
			}
		});
		bPane.setLayout(new FlowLayout());
		bPane.add(layer1);
		bPane.add(layer2);
		bPane.add(layer3);
		bPane.add(moveLayer);
		return bPane;
	}
	
	public boolean isWorking(){
		return working;
	}
	
	public void setWorking(boolean working){
		this.working = working;
	}
}