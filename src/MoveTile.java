import java.awt.Color;

public class MoveTile {
	private Color col;
	private int value;

	public MoveTile() {
		col = null;
		value = -1;
	}

	public MoveTile(Color col) {
		this.col = col;
		value = -1;
	}

	public MoveTile(int val) {
		col = null;
		this.value = val;
	}

	public MoveTile(Color col, int val) {
		this.col = col;
		value = val;
	}

	public boolean equals(MoveTile comp) { 
		return (((value == comp.getValue()) ? true : false) && ((col == comp.getColor()) ? true : false));
	}

	public String toString() {
		return "Moving Tile-> Num: " + value + " / Color Ref: " + col;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setColor(Color col) {
		this.col = col;
	}

	public Color getColor() {
		return col;
	}
}