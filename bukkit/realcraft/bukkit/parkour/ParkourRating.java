package realcraft.bukkit.parkour;

public class ParkourRating {

	private ParkourArena arena;
	private int author;
	private int value;
	private int created;

	public ParkourRating(ParkourArena arena,int author,int value,int created){
		this.arena = arena;
		this.author = author;
		this.value = value;
		this.created = created;
	}

	public ParkourArena getArena(){
		return arena;
	}

	public int getAuthor(){
		return author;
	}

	public int getValue(){
		return value;
	}

	public int getCreated(){
		return created;
	}
}