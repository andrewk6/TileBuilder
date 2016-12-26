import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class ToolPane extends JPanel{
	public static final int SELECTION = 0;
	public static final int DRAW = 1;
	public static final int POINT = 0;
	public static final int RECT = 1;
	public static final int LINE = 2;
	
	private int selection, tool;
	private boolean toolChange;
	
	public ToolPane(){
		super();
		selection = 0;
		tool = 0;
		setLayout(new BorderLayout());
		JPanel toolPane = new JPanel();
		toolPane.setOpaque(false);
		toolPane.setBorder(new MatteBorder(0, 0, 0, 4, Color.black));
		toolPane.setLayout(new FlowLayout());
		JPanel selectPane = new JPanel();
		selectPane.setOpaque(false);
		selectPane.setLayout(new FlowLayout());
		try {
			JButton selTool = new JButton(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("SelectImg.png"))));
			selTool.setToolTipText("Select Tiles");
			selTool.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					selection = SELECTION;
				}
			});
			JButton drawTool = new JButton(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("PaintImg.png"))));
			drawTool.setToolTipText("Draw Tiles");
			drawTool.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					selection = DRAW;
				}
			});
			toolPane.add(selTool);
			toolPane.add(drawTool);
			JButton pointSel = new JButton(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("Point Tool.png"))));
			pointSel.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					tool = POINT;
				}
			});
			pointSel.setToolTipText("Draw/Select a single tile at a time");
			JButton squareSel = new JButton(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("Rectangle Tool.png"))));
			squareSel.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					tool = RECT;
				}
			});
			squareSel.setToolTipText("Draw/Select a square");
			JButton lineTool = new JButton(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("Line Tool.png"))));
			lineTool.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					tool = LINE;
				}
			});
			lineTool.setToolTipText("Draw/Select a line");
			selectPane.add(pointSel);
			selectPane.add(lineTool);
			selectPane.add(squareSel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		add(toolPane, BorderLayout.WEST);
		add(selectPane, BorderLayout.CENTER);
//		toolFrameI.add(toolFrame);
		setVisible(true);
		setBounds(0, 0, 400, 65);
		setBackground(Color.white);
	}

	public int getSelection() {
		return selection;
	}

	public int getTool() {
		return tool;
	}
}