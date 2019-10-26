package realcraft.bukkit.falling.commands;

import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.falling.FallPlayer;
import realcraft.bukkit.falling.arena.FallArena;
import realcraft.bukkit.falling.arena.FallArenaPermission;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

import java.util.ArrayList;
import java.util.List;

public class FallCommandUntrust extends FallCommand {

	public FallCommandUntrust(){
		super("untrust");
	}

	@Override
	public void perform(FallPlayer fPlayer,String[] args){
		FallArena arena = fPlayer.getArena();
		if(arena == null || arena.getPermission(fPlayer) != FallArenaPermission.OWNER){
			fPlayer.sendMessage("§cNemas opravneni spravovat tento ostrov.");
			return;
		}
		if(args.length == 0){
			fPlayer.sendMessage("Odebrat spoluhrace");
			fPlayer.sendMessage("§6/ff untrust §e<player>");
			return;
		}
		User user = Users.getUser(args[0]);
		if(user == null || user.equals(fPlayer.getUser())){
			fPlayer.sendMessage("§cHrac nenalezen.");
			return;
		}
		if(arena.getPermission(FallManager.getFallPlayer(user)).isMaximum(FallArenaPermission.NONE)){
			fPlayer.sendMessage("§cHrac je jiz odebrany.");
			return;
		}
		arena.getTrusted().remove(FallManager.getFallPlayer(user));
		arena.save();
		FallManager.sendMessage(fPlayer,"§dSpoluhrac §f"+user.getName()+" §dodebran");
	}

	@Override
	public List<String> tabCompleter(FallPlayer fPlayer,String[] args){
		List<String> players = new ArrayList<>();
		FallArena arena = fPlayer.getArena();
		if(arena != null && arena.getPermission(fPlayer) == FallArenaPermission.OWNER) {
			for(FallPlayer fPlayer2 : arena.getTrusted()){
				if(args.length == 0 || fPlayer2.getUser().getName().toLowerCase().startsWith(args[0].toLowerCase())){
					players.add(fPlayer2.getUser().getName());
				}
			}
		}
		return players;
	}
}