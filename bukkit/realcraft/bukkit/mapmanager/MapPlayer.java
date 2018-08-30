package realcraft.bukkit.mapmanager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.events.MapPlayerJoinMapEvent;
import realcraft.bukkit.mapmanager.events.MapPlayerLeaveMapEvent;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.spawn.ServerSpawn;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.BorderUtil;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.share.users.User;

public class MapPlayer {

	private User user;
	private Player player;

	private Map map;

	private MapType menuType;
	private int menuPage = 1;

	private boolean WEByPass;

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

	public MapType getMenuType(){
		return menuType;
	}

	public void setMenuType(MapType menuType){
		this.menuType = menuType;
	}

	public int getMenuPage(){
		return menuPage;
	}

	public void setMenuPage(int menuPage){
		this.menuPage = menuPage;
	}

	public boolean isWEByPass(){
		return WEByPass;
	}

	public void toggleWEByPass(){
		this.WEByPass = !this.WEByPass;
	}

	public void joinMap(Map map){
		this.map = map;
		this.getPlayer().teleport(LocationUtil.getSafeDestination(map.getRegion().getCenterLocation()));
		this.getPlayer().setScoreboard(map.getScoreboard().getScoreboard());
		this.getPlayer().setPlayerTime(map.getTime().getValue(),false);
		this.updateBorder();
		Bukkit.getPluginManager().callEvent(new MapPlayerJoinMapEvent(this,map));
	}

	public void leaveMap(){
		Map oldMap = this.map;
		this.map = null;
		this.getPlayer().teleport(ServerSpawn.getLocation());
		this.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		Bukkit.getPluginManager().callEvent(new MapPlayerLeaveMapEvent(this,oldMap));
	}

	public void updateBorder(){
		if(map != null) BorderUtil.setBorder(this.getPlayer(),map.getRegion().getCenterLocation(),Math.max(map.getType().getDimension().getX(),map.getType().getDimension().getZ()));
	}

	@Override
	public int hashCode(){
		return this.getUser().getId();
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof MapPlayer){
			MapPlayer toCompare = (MapPlayer) object;
			return (toCompare.getUser().equals(this.getUser()));
		}
		return false;
	}
}