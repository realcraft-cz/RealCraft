package realcraft.bukkit.falling.arena;

public class FallArena {

	private int id;
	private FallArenaRegion region = new FallArenaRegion(this);

	public FallArena(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public FallArenaRegion getRegion(){
		return region;
	}
}