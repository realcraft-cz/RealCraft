package realcraft.bukkit.mapmanager.map;

public enum MapPermission {

	NONE(0), BUILD(1), OWNER(2);

	private int id;

	private MapPermission(int id){
		this.id = id;
	}

	private int getId(){
		return id;
	}

	public boolean isMinimum(MapPermission perm){
		return (this.getId() >= perm.getId());
	}

	public boolean isMaximum(MapPermission perm){
		return (this.getId() <= perm.getId());
	}
}