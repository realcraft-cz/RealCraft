package realcraft.bungee.skins.exceptions;

@SuppressWarnings("serial")
public class SkinsLimitException extends Exception {
	private int seconds;

	public SkinsLimitException(int seconds){
		super();
		this.seconds = seconds;
	}

	public int getRemainingSeconds(){
		return seconds;
	}
}