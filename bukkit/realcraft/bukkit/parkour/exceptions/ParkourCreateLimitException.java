package realcraft.bukkit.parkour.exceptions;

@SuppressWarnings("serial")
public class ParkourCreateLimitException extends Exception {
	private long lastCreated;

	public ParkourCreateLimitException(long lastCreated){
		super();
		this.lastCreated = lastCreated;
	}

	public long getLastCreated(){
		return lastCreated;
	}
}