import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

public class TileBuilder {
	public static void main(String[] Args) {
		// int tileSize = Integer.parseInt(JOptionPane.showInputDialog("Enter
		// tile size"));
		// boolean goodSize = false;
		// int width, height;
		// width = height = 0;
		// do{
		// String input = JOptionPane.showInputDialog("Enter map size, divisible
		// by tile size form : WIDTHxHEIGHT");
		// try{
		// width = Integer.parseInt(input.split("x")[0]);
		// height = Integer.parseInt(input.split("x")[1]);
		// goodSize = true;
		// }catch(IllegalArgumentException e){
		// JOptionPane.showMessageDialog(null, "Invalid input");
		// }catch(ArrayIndexOutOfBoundsException e){
		// JOptionPane.showMessageDialog(null, "Invalid input");
		// }
		// }while(!goodSize);
		// BuilderFrame frame = new BuilderFrame("Test", tileSize, width,
		// height);

		BuilderFrame frame = new BuilderFrame("Test", 32, 320, 320);

		frame.show();
//		while(!frame.isWorking()){
//			System.out.println("Clear");
//		}
//		frame.setVisible(false);
//		frame.disable();
//		frame.dispose();
//		frame = null;
//		BuilderFrame frame2 = new BuilderFrame("Test 2", 32, 320, 320);
//		frame2.show();
	}
}