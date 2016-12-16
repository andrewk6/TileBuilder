import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
//\a\a\a\a.850.hungerford.drive
public class JOP {

	private static void changeJOP() {
		// These colors are very ugly to encourage YOU to CHANGE them!

		// The font of the message text
		UIManager.put("Label.font", new FontUIResource(new Font("Tempus Sans ITC", Font.BOLD, 20)));
		// The color of the message text
		UIManager.put("OptionPane.messageForeground", Color.white);

		// color for text field (where you are inputting data)
		UIManager.put("TextField.background", Color.black);
		// font for message in text field
		UIManager.put("TextField.font", new FontUIResource(new Font("Dialog", Font.ITALIC, 24)));
		// color for message in text field
		UIManager.put("TextField.foreground", Color.white);

		// The color of the panel
		UIManager.put("Panel.background", Color.black);
		// The color around the outside of the panel
		UIManager.put("OptionPane.background", Color.WHITE);

		// Buttons at bottom
		UIManager.put("Button.background", Color.white);
		UIManager.put("Button.foreground", Color.black);
		UIManager.put("Button.font", new FontUIResource(new Font("Tempus Sans ITC", Font.BOLD, 14)));
	}

	public static void print(String message) {
		changeJOP();
		JFrame frame = new JFrame();
		frame.setVisible(false);
		frame.setLocation(960, 540);
		frame.setAlwaysOnTop(true);
		JOptionPane.showMessageDialog(frame, message);
		frame.dispose();
		
	}

	public static void print(String message, String title) {
		changeJOP();
		JFrame frame = null;
		if (frame == null) {
			frame = new JFrame();
		}
		frame.setVisible(false);
		frame.setLocation(960, 540);
		frame.setAlwaysOnTop(true);
		JOptionPane.showInputDialog(frame, message);
		frame.dispose();
	}

	public static String input(String message) {
		changeJOP();
		JFrame frame = null;
		if (frame == null) {
			frame = new JFrame();
		}
		frame.setVisible(false);
		frame.setLocation(960, 540);
		frame.setAlwaysOnTop(true);
		String[] options = { "delete", "hide", "break" };
		String response = JOptionPane.showInputDialog(frame, message);
		frame.dispose();
		return response;

	}

	public int buttonInput(String message, String title, int panelOption, int messageType, String[] button,
			int defaultChoice) {
		changeJOP();
		JFrame frame = null;
		if (frame == null) {
			frame = new JFrame();
		}
		frame.setVisible(false);
		frame.setLocation(960, 540);
		frame.setAlwaysOnTop(true);
		int response = JOptionPane.showOptionDialog(frame, message, title, panelOption, messageType, null, button,
				defaultChoice);
		frame.dispose();
		return response;
	}

	public static int buttonInput(String message, String[] options) {
		changeJOP();
		JFrame frame = null;
		if (frame == null) {
			frame = new JFrame();
		}
		frame.setVisible(false);
		frame.setLocation(960, 540);
		frame.setAlwaysOnTop(true);
		int response = JOptionPane.showOptionDialog(frame, message, "Option Dialog", 0, 1, null, options, 0);
		frame.dispose();
		return response;
	}

	public static void bOPrint(String message) {
		changeJOP();
		JFrame frame = new JFrame();
		frame.setVisible(false);
		frame.setLocation(960, 540);
		frame.setAlwaysOnTop(true);
		String binaryMessage = "";
		for (String s : message.split(" ")) {
			for (char c : s.toCharArray()) {
				int cur = c;
				while (cur > 0) {
					if (cur % 2 == 0) {
						binaryMessage += "0";
						cur = cur / 2;
					} else {
						binaryMessage += "1";
						cur = cur / 2;
					}
				}
				binaryMessage += ",";
			}
			binaryMessage += "\n";
		}
		JOptionPane.showMessageDialog(frame, binaryMessage);
		frame.dispose();
	}
	
	public static void tag(){
		System.out.println("R4V3N:  (*)>");
	}
}