package realcraft.bukkit.fights;

import realcraft.share.users.User;

public class FightPlayer {

	private User user;

	public FightPlayer(User user){
		this.user = user;
	}

	public User getUser(){
		return user;
	}

	public void reload(){
	}
}