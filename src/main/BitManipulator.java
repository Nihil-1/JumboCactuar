package main;

public class BitManipulator {

	public static byte setBit(byte b, int index, boolean value) {
		if (index < 0 || index > 7) {
			throw new IllegalArgumentException("bitIndex must be between 0 and 7");
		}

		int encFlagsBitIndex = 7 - index; // Convert left-to-right index to right-to-left

		if (value) {
			return (byte) (b | (1 << encFlagsBitIndex)); // Set bit to 1
		} else {
			return (byte) (b & ~(1 << encFlagsBitIndex)); // Set bit to 0
		}
	}

	public static byte packBooleansIntoByte(boolean[] bits) {
		if (bits.length != 8) {
			throw new IllegalArgumentException("Array must be exactly 8 booleans.");
		}
		byte result = 0;
		for (int i = 0; i < 8; i++) {
			if (bits[i]) {
				result |= (1 << (7 - i)); // 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01
			}
		}
		return result;
	}

	public static boolean[] unpackByteIntoBooleans(byte b) {
		boolean[] bits = new boolean[8];
		for (int i = 0; i < 8; i++) {
			bits[i] = ((b >> (7 - i)) & 1) == 1;
		}
		return bits;
	}

}
