package realcraft.bukkit.falling.arena;

public enum FallArenaPermission {

	NONE(0), TRUSTED(1), OWNER(2);

	private int id;

	private FallArenaPermission(int id){
		this.id = id;
	}

	private int getId(){
		return id;
	}

	public boolean isMinimum(FallArenaPermission perm){
		return (this.getId() >= perm.getId());
	}

	public boolean isMaximum(FallArenaPermission perm){
		return (this.getId() <= perm.getId());
	}
}