package main;

public class EnemyData {

	private int id, level;
	private boolean enabled, notTargetable, notVisible, notLoaded;
	private Position position;

	public EnemyData(int id, int level, boolean enabled, boolean notTargetable, boolean notVisible, boolean notLoaded, Position position) {
		this.id = id;
		this.level = level;
		this.enabled = enabled;
		this.notTargetable = notTargetable;
		this.notVisible = notVisible;
		this.notLoaded = notLoaded;
		this.position = position;
	}

	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isNotLoaded() {
		return notLoaded;
	}

	public boolean isNotTargetable() {
		return notTargetable;
	}

	public boolean isNotVisible() {
		return notVisible;
	}

	public Position getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return "EnemyData {" + "id=" + id + ", level=" + level + ", enabled=" + enabled + ", notTargetable=" + notTargetable + ", notVisible=" + notVisible + ", notLoaded=" + notLoaded
				+ ", position=" + position + '}';
	}

}
