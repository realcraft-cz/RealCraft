package realcraft.bukkit.fights;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum FightRank {

	BRONZE_IV		( 1, 1000, ChatColor.RED, Material.CLAY_BRICK),
	BRONZE_III		( 2, 1100, ChatColor.RED, Material.CLAY_BRICK),
	BRONZE_II		( 3, 1200, ChatColor.RED, Material.CLAY_BRICK),
	BRONZE_I		( 4, 1300, ChatColor.RED, Material.CLAY_BRICK),
	SILVER_IV		( 5, 1400, ChatColor.GRAY, Material.IRON_INGOT),
	SILVER_III		( 6, 1500, ChatColor.GRAY, Material.IRON_INGOT),
	SILVER_II		( 7, 1600, ChatColor.GRAY, Material.IRON_INGOT),
	SILVER_I		( 8, 1700, ChatColor.GRAY, Material.IRON_INGOT),
	GOLD_IV			( 9, 1800, ChatColor.GOLD, Material.GOLD_INGOT),
	GOLD_III		(10, 2000, ChatColor.GOLD, Material.GOLD_INGOT),
	GOLD_II			(11, 2200, ChatColor.GOLD, Material.GOLD_INGOT),
	GOLD_I			(12, 2400, ChatColor.GOLD, Material.GOLD_INGOT),
	DIAMOND_III		(13, 2600, ChatColor.AQUA, Material.DIAMOND),
	DIAMOND_II		(14, 2900, ChatColor.AQUA, Material.DIAMOND),
	DIAMOND_I		(15, 3200, ChatColor.AQUA, Material.DIAMOND),
	EMERALD			(16, 3500, ChatColor.GREEN, Material.EMERALD);

	public static final int DEFAULT_SCORE = SILVER_I.getMinScore();
	public static final int MIN_SCORE = BRONZE_IV.getMinScore();
	public static final int MIN_MATCHES = 10;

	private static final int MATCH_SCORE = 30;
	private static final double RANK_MOD = MATCH_SCORE/(double)FightRank.values().length;
	private static FightRank[] values = FightRank.values();

	public static final String CHAR_UP = "\u2B06";
	public static final String CHAR_DOWN = "\u2B07";
	public static final String CHAR_SET = "\u27BC";

	private int id;
	private int score;
	private ChatColor color;
	private Material material;

	private FightRank(int id,int score,ChatColor color,Material material){
		this.id = id;
		this.score = score;
		this.color = color;
		this.material = material;
	}

	public int getId(){
		return id;
	}

	public int getMinScore(){
		return score;
	}

	public int getMaxScore(){
		return (this.getNextRank().getMinScore()-1);
	}

	public FightRank getNextRank(){
		return values[(this.ordinal()+1)%values.length];
	}

	public ChatColor getChatColor(){
		return color;
	}

	public Material getMaterial(){
		return material;
	}

	public String getName(){
		return this.toString().replace('_',' ');
	}

	public int getRankNumber(){
		switch(this){
			case BRONZE_IV: return 4;
			case BRONZE_III: return 3;
			case BRONZE_II: return 2;
			case BRONZE_I: return 1;
			case SILVER_IV: return 4;
			case SILVER_III: return 3;
			case SILVER_II: return 2;
			case SILVER_I: return 1;
			case GOLD_IV: return 4;
			case GOLD_III: return 3;
			case GOLD_II: return 2;
			case GOLD_I: return 1;
			case DIAMOND_III: return 3;
			case DIAMOND_II: return 2;
			case DIAMOND_I: return 1;
			case EMERALD: return 1;
		}
		return 0;
	}

	public static int getMatchScore(FightRank winner,FightRank loser){
		if(winner == null && loser != null) return (loser.getMinScore()+200)/10;
		else if(winner != null && loser == null) return (winner.getMinScore()+200)/10;
		else if(winner == null && loser == null) return 150;
		return MATCH_SCORE-(int)Math.round((winner.getId()-loser.getId())*RANK_MOD);
	}

	public static FightRank fromScore(int score){
		FightRank rank = FightRank.BRONZE_IV;
		for(FightRank rank2 : values){
			if(score >= rank2.getMinScore()) rank = rank2;
		}
		return rank;
	}
}