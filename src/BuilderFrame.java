import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import javafx.scene.layout.Background;

public class BuilderFrame extends JFrame {
	private final Font FONT = new Font("Monospaced", Font.BOLD, 20);
	private BufferedImage img;
	private JPanel contentPane;
	private JPanel mainPane;
	private JPanel menuPane;
	private int tileSize;

	public BuilderFrame(String title, int tileSize, int width, int height) {
		super(title);
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
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		mainPane = new JPanel();
		mainPane.setLayout(null);
		menuPane = new JPanel();
		menuPane.setBackground(Color.white);
		menuPane.setBorder(new MatteBorder(0, 0, 3, 0, Color.black));
		contentPane.add(mainPane, BorderLayout.CENTER);
		contentPane.add(menuPane, BorderLayout.NORTH);
		TilesPanel tiles = new TilesPanel(img, tileSize);
		tiles.setBounds(width, 0, img.getWidth(), getHeight());
		MovePanel movePane = new MovePanel(tileSize);
		MapPanel mapPane = new MapPanel(img, width, height, tiles, movePane, tileSize);
		mapPane.setBounds(0, 0, width, height);
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
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		buildInternalFrames(mapPane, movePane);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		menuPane.add(exit);

	}

	public void buildInternalFrames(MapPanel mapPane, MovePanel movePane) {
		JInternalFrame tilesFrame = new JInternalFrame();
		TilesPanel tiles = mapPane.getTPane();
		tilesFrame.setBounds(300, 300, tiles.getImg().getWidth() + 40, tiles.getImg().getHeight() + 40);
		tilesFrame.add(tiles);

		JInternalFrame moveFrame = new JInternalFrame();
		moveFrame.setBounds(400, 400, movePane.getSelHeight() + tileSize, movePane.getSelWidth() + tileSize);
		moveFrame.add(movePane);

		JInternalFrame map = new JInternalFrame();
		JPanel buttonsPane = buildLayerButtons(mapPane, moveFrame);
		map.setBounds(100, 100, (390 > mapPane.getWidth()) ? 390 : mapPane.getWidth(), mapPane.getSetHeight() + 80);
		map.setLayout(new BorderLayout());
		map.add(mapPane, BorderLayout.CENTER);
		map.add(buttonsPane, BorderLayout.SOUTH);

		mainPane.add(map);
		mainPane.add(tilesFrame);
		mainPane.add(moveFrame);
		map.setVisible(true);
		tilesFrame.setVisible(true);
		moveFrame.setVisible(false);
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
}