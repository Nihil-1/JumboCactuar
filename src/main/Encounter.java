package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Encounter {

	private int battleStage;
	private byte battleFlags;
	private int mainCamData, mainCam, mainCamAnim;
	private int secCamData, secCam, secCamAnim;
	private byte notVisFlags;
	private byte notLoadedFlags;
	private byte notTargFlags;
	private byte enabledFlags;
	private Position[] positions = new Position[8];
	private int[] enemies = new int[8];
	private int[] unknowns = new int[56];
	private int[] levels = new int[8];

	public Encounter(Encounter other) {
		this.battleStage = other.battleStage;
		this.battleFlags = other.battleFlags;
		this.mainCamData = other.mainCamData;
		this.mainCam = other.mainCam;
		this.mainCamAnim = other.mainCamAnim;
		this.secCamData = other.secCamData;
		this.secCam = other.secCam;
		this.secCamAnim = other.secCamAnim;
		this.notVisFlags = other.notVisFlags;
		this.notLoadedFlags = other.notLoadedFlags;
		this.notTargFlags = other.notTargFlags;
		this.enabledFlags = other.enabledFlags;

		this.positions = new Position[8];
		for (int i = 0; i < 8; i++) {
			this.positions[i] = other.getPosition(i);
		}

		this.enemies = other.enemies.clone();
		this.unknowns = other.unknowns.clone();
		this.levels = other.levels.clone();
	}

	public Encounter(byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		battleStage = buf.get(0x00) & 0xFF;

		battleFlags = buf.get(0x01);

		mainCamData = buf.get(0x02) & 0xFF;
		secCamData = buf.get(0x03) & 0xFF;
		mainCam = (mainCamData >> 4) & 0x0F;
		mainCamAnim = mainCamData & 0x0F;
		secCam = (secCamData >> 4) & 0x0F;
		secCamAnim = secCamData & 0x0F;

		notVisFlags = buf.get(0x04);
		notLoadedFlags = buf.get(0x05);
		notTargFlags = buf.get(0x06);
		enabledFlags = buf.get(0x07);

		for (int i = 0; i < positions.length; i++) {
			int newPosOffset = 0x08 + i * 6;
			short x = buf.getShort(newPosOffset);
			short y = buf.getShort(newPosOffset + 2);
			short z = buf.getShort(newPosOffset + 4);
			positions[i] = new Position(x, y, z);
		}

		for (int i = 0; i < enemies.length; i++) {
			enemies[i] = (buf.get(0x38 + i) & 0xFF) - 0x10;
		}

		for (int i = 0; i < unknowns.length; i++) {
			unknowns[i] = buf.get(0x40 + i) & 0xFF;
		}

		for (int i = 0; i < levels.length; i++) {
			levels[i] = buf.get(0x78 + i) & 0xFF;
		}
	}

	@Override
	public String toString() {
		String sceneToString = "Stage: " + battleStage;
		sceneToString += "\n Flags: \t\t";
		sceneToString += (battleFlags & 0x01) != 0 ? "| NoEscape |" : "";
		sceneToString += (battleFlags & 0x02) != 0 ? " NoVictoryFanfare |" : "";
		sceneToString += (battleFlags & 0x04) != 0 ? " ShowTimer |" : "";
		sceneToString += (battleFlags & 0x08) != 0 ? " NoExpGain |" : "";
		sceneToString += (battleFlags & 0x10) != 0 ? " NoExpScreen |" : "";
		sceneToString += (battleFlags & 0x20) != 0 ? " Surprise |" : "";
		sceneToString += (battleFlags & 0x40) != 0 ? " BackAttack |" : "";
		sceneToString += (battleFlags & 0x80) != 0 ? " Scripted |" : "";
		sceneToString += "\n Main Camera: \t\t" + mainCam + ", Anim: " + mainCamAnim;
		sceneToString += "\n Secondary Camera: \t" + secCam + ", Anim: " + secCamAnim;
		sceneToString += "\n NOT Visible Flags: \t" + String.format("%8s", Integer.toBinaryString(notVisFlags & 0xFF)).replace(' ', '0');
		sceneToString += "\n NOT Loaded Flags: \t" + String.format("%8s", Integer.toBinaryString(notLoadedFlags & 0xFF)).replace(' ', '0');
		sceneToString += "\n NOT Targetable Flags: \t" + String.format("%8s", Integer.toBinaryString(notTargFlags & 0xFF)).replace(' ', '0');
		sceneToString += "\n Enabled Flags: \t" + String.format("%8s", Integer.toBinaryString(enabledFlags & 0xFF)).replace(' ', '0');
		sceneToString += "\n Positions: \t\t" + Arrays.toString(positions);
		sceneToString += "\n Enemies: \t\t" + Arrays.toString(enemies);
		sceneToString += "\n Levels: \t\t" + Arrays.toString(levels);
		return sceneToString;
	}

	public void writeUnknownsToFile() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/output.txt"))) {
			for (int i = 0; i < unknowns.length; i++) {
				writer.write(Integer.toString(unknowns[i]));
				if ((i + 1) % 16 == 0) {
					writer.newLine(); // newline every 16 numbers
				} else {
					writer.write(" ");
				}
			}
			System.out.println("Unknowns written to file.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getBattleStage() {
		return battleStage;
	}

	public void setBattleStage(int battleStage) {
		this.battleStage = battleStage;
	}

	public byte getBattleFlags() {
		return battleFlags;
	}

	public void setBattleFlags(byte battleFlags) {
		this.battleFlags = battleFlags;
	}

	public int getMainCam() {
		return mainCam;
	}

	public void setMainCam(int mainCam) {
		this.mainCam = mainCam;
	}

	public int getMainCamAnim() {
		return mainCamAnim;
	}

	public void setMainCamAnim(int mainCamAnim) {
		this.mainCamAnim = mainCamAnim;
	}

	public int getSecondaryCam() {
		return secCam;
	}

	public void setSecondaryCam(int secCam) {
		this.secCam = secCam;
	}

	public int getSecondaryCamAnim() {
		return secCamAnim;
	}

	public void setSecondaryCamAnim(int secCamAnim) {
		this.secCamAnim = secCamAnim;
	}

	public byte getNotVisibleFlags() {
		return notVisFlags;
	}

	public void setNotVisibleFlags(byte notVisFlags) {
		this.notVisFlags = notVisFlags;
	}

	public byte getNotLoadedFlags() {
		return notLoadedFlags;
	}

	public void setNotLoadedFlags(byte notLoadedFlags) {
		this.notLoadedFlags = notLoadedFlags;
	}

	public byte getNotTargetableFlags() {
		return notTargFlags;
	}

	public void setNotTargetableFlags(byte notTargFlags) {
		this.notTargFlags = notTargFlags;
	}

	public byte getEnabledFlags() {
		return enabledFlags;
	}

	public void setEnabledFlags(byte enabledFlags) {
		this.enabledFlags = enabledFlags;
	}

	public Position getPosition(int index) {
		return positions[index];
	}

	public void setPosition(int index, Position position) {
		positions[index] = position;
	}

	public int getEnemy(int index) {
		return enemies[index];
	}

	public void setEnemy(int index, int enemy) {
		this.enemies[index] = enemy;
	}

	public int[] getUnknowns() {
		return unknowns;
	}

	public void setUnknowns(int[] unknowns) {
		this.unknowns = unknowns;
	}

	public int getLevel(int index) {
		return levels[index];
	}

	public void setLevel(int index, int level) {
		this.levels[index] = level;
	}

	public byte[] toBytes() {
		ByteBuffer buf = ByteBuffer.allocate(128).order(ByteOrder.LITTLE_ENDIAN);
		buf.put((byte) battleStage);
		buf.put(battleFlags);
		buf.put((byte) mainCamData);
		buf.put((byte) secCamData);
		buf.put(notVisFlags);
		buf.put(notLoadedFlags);
		buf.put(notTargFlags);
		buf.put(enabledFlags);
		for (Position p : positions) {
			buf.putShort(p.getX());
			buf.putShort(p.getY());
			buf.putShort(p.getZ());
		}
		for (int enemy : enemies) {
			buf.put((byte) (enemy + 0x10));
		}
		for (int unknown : unknowns) {
			buf.put((byte) unknown);
		}
		for (int level : levels) {
			buf.put((byte) level);
		}
		return buf.array();
	}

}
