package realcraft.bukkit.falling;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import realcraft.bukkit.falling.arena.FallArena;
import realcraft.bukkit.falling.arena.FallArenaPermission;
import realcraft.bukkit.falling.arena.FallArenaRegion;
import realcraft.bukkit.falling.events.FallPlayerJoinArenaEvent;
import realcraft.bukkit.falling.events.FallPlayerLeaveArenaEvent;
import realcraft.bukkit.falling.exceptions.FallArenaLockedException;
import realcraft.bukkit.spawn.ServerSpawn;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.BorderUtil;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.share.users.User;

import java.util.ArrayList;
import java.util.Comparator;

public class FallPlayer {

	private User user;
	private Player player;

	private FallArena arena;

	private boolean WEByPass;
	private String regenCode;

	public FallPlayer(User user){
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

	public FallArena getArena(){
		return arena;
	}

	public FallArena getOwnArena(){
		for(FallArena arena : FallManager.getArenas()){
			if(arena.getOwner().equals(this.getUser())){
				return arena;
			}
		}
		return null;
	}

	public ArrayList<FallArena> getArenas(){
		ArrayList<FallArena> arenas = new ArrayList<>();
		for(FallArena arena : FallManager.getArenas()){
			if(arena.getPermission(this).isMinimum(FallArenaPermission.TRUSTED)){
				arenas.add(arena);
			}
		}
		return arenas;
	}

	public ArrayList<FallArena> getSortedArenas(){
		ArrayList<FallArena> arenas = new ArrayList<>(this.getArenas());
		arenas.sort(new Comparator<FallArena>(){
			@Override
			public int compare(FallArena arena1,FallArena arena2){
				return Boolean.compare(arena2.getOwner().equals(FallPlayer.this.getUser()),arena1.getOwner().equals(FallPlayer.this.getUser()));
			}
		});
		return arenas;
	}

	public boolean isWEByPass(){
		return WEByPass;
	}

	public void toggleWEByPass(){
		this.WEByPass = !this.WEByPass;
	}

	public String getRegenCode(){
		return regenCode;
	}

	public void setRegenCode(String regenCode){
		this.regenCode = regenCode;
	}

	public void joinArena(FallArena arena) throws FallArenaLockedException {
		if(arena.isLocked() && arena.getPermission(this) == FallArenaPermission.NONE){
			throw new FallArenaLockedException(arena);
		}
		this.arena = arena;
		this.getPlayer().teleport(LocationUtil.getSafeDestination(arena.getRegion().getCenterLocation().clone().add(0,1,0)));
		this.updateBorder();
		Bukkit.getPluginManager().callEvent(new FallPlayerJoinArenaEvent(this,arena));
	}

	public void leaveArena(){
		FallArena oldMap = this.arena;
		this.arena = null;
		this.getPlayer().teleport(ServerSpawn.getLocation());
		if(oldMap != null){
			Bukkit.getPluginManager().callEvent(new FallPlayerLeaveArenaEvent(this,oldMap));
		}
	}

	public void updateBorder(){
		if(arena != null){
			BorderUtil.setBorder(this.getPlayer(),arena.getRegion().getCenterLocation(),FallArenaRegion.ARENA_FULLSIZE/2);
		}
	}

	public void sendMessage(String message){
		this.sendMessage(message,false);
	}

	public void sendMessage(String message,boolean prefix){
		if(prefix){
			FallManager.sendMessage(this.getPlayer(),message);
		} else {
			this.getPlayer().sendMessage(message);
		}
	}

	@Override
	public int hashCode(){
		return this.getUser().getId();
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof FallPlayer){
			FallPlayer toCompare = (FallPlayer) object;
			return (toCompare.getUser().equals(this.getUser()));
		}
		return false;
	}
}