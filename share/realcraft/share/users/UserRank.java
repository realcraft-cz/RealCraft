package realcraft.share.users;

import net.md_5.bungee.api.ChatColor;

public enum UserRank {

	HRAC, VIP, BUILDER, ADMIN, MANAZER;

	public int getId(){
		switch(this){
			case HRAC: return 0;
			case VIP: return 40;
			case BUILDER: return 45;
			case ADMIN: return 60;
			case MANAZER: return 90;
		}
		return 0;
	}

	public String getName(){
		switch(this){
			case HRAC: return "Hrac";
			case VIP: return "VIP";
			case BUILDER: return "Builder";
			case ADMIN: return "Admin";
			case MANAZER: return "Manazer";
		}
		return null;
	}

	public String getChatColor(){
		switch(this){
			case HRAC: return ChatColor.DARK_AQUA.toString();
			case VIP: return ChatColor.AQUA.toString();
			case BUILDER: return ChatColor.DARK_PURPLE.toString();
			case ADMIN: return ChatColor.GREEN.toString();
			case MANAZER: return ChatColor.DARK_RED.toString();
		}
		return ChatColor.WHITE.toString();
	}

	public boolean isMinimum(UserRank rank){
		return (this.getId() >= rank.getId());
	}

	public boolean isMaximum(UserRank rank){
		return (this.getId() <= rank.getId());
	}

	public static UserRank fromId(int id){
		for(UserRank rank : UserRank.values()){
			if(rank.getId() == id) return rank;
		}
		return HRAC;
	}
}