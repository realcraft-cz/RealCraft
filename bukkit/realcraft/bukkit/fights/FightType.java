package realcraft.bukkit.fights;

public enum FightType {
	PUBLIC, DUEL;

	public String getName(){
		switch(this){
			case PUBLIC: return "§e§lFFA";
			case DUEL: return "§b§lDuely";
		}
		return null;
	}

	@Override
	public String toString(){
		return this.name().toLowerCase();
	}

	public static FightType fromName(String name){
		return FightType.valueOf(name.toUpperCase());
	}
}