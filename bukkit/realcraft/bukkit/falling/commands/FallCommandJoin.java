package realcraft.bukkit.falling.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.falling.FallPlayer;
import realcraft.bukkit.falling.arena.FallArena;
import realcraft.bukkit.falling.exceptions.FallArenaLockedException;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

import java.util.ArrayList;
import java.util.List;

public class FallCommandJoin extends FallCommand {

	public FallCommandJoin(){
		super("join");
	}

	@Override
	public void perform(FallPlayer fPlayer,String[] args){
		if(args.length == 0){
			fPlayer.sendMessage("Teleport na hracuv ostrov");
			fPlayer.sendMessage("§6/ff join §e<player>");
			return;
		}
		User user = Users.getUser(args[0]);
		if(user == null || user.equals(fPlayer.getUser())){
			fPlayer.sendMessage("§cHrac nenalezen.");
			return;
		}
		FallArena arena = FallManager.getFallPlayer(user).getOwnArena();
		if(arena == null){
			fPlayer.sendMessage("§cHrac nema vlastni ostrov.");
			return;
		}
		try {
			fPlayer.joinArena(arena);
		} catch (FallArenaLockedException e){
			fPlayer.sendMessage("§cOstrov je zamknuty.");
		}
	}

	@Override
	public List<String> tabCompleter(FallPlayer fPlayer,String[] args){
		List<String> players = new ArrayList<>();
		for(Player target : Bukkit.getOnlinePlayers()) players.add(target.getName());
		return players;
	}
}