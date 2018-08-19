package realcraft.bukkit.mapmanager;

import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

public class MapPlayer {

	private User user;
	private Player player;

	private Map map;

	public MapPlayer(User user){
		this.user = user;
	}

	public User getUser(){
		return user;
	}

	public Player getPlayer(){
		if(player == null || !player.isOnline() || !player.isValid()){
			player = Users.getPlayer(this.getUser());
		}
		return player;
	}

	public Map getMap(){
		return map;
	}

	public void setMap(Map map){
		this.map = map;
	}
}