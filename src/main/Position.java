package main;

public class Position {
	private short x;
	private short y;
	private short z;

	public Position(short x, short y, short z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}

	// Getters
	public short getX() {
		return x;
	}

	public short getY() {
		return y;
	}

	public short getZ() {
		return z;
	}

	// Setters
	public void setX(short x) {
		this.x = x;
	}

	public void setY(short y) {
		this.y = y;
	}

	public void setZ(short z) {
		this.z = z;
	}
}
