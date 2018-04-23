package realcraft.bukkit.fights;

public enum FightState {
	STARTING, INGAME, ENDING;

	@Override
	public String toString(){
		return this.name().toLowerCase();
	}

	public static FightState fromName(String name){
		return FightState.valueOf(name.toUpperCase());
	}
}