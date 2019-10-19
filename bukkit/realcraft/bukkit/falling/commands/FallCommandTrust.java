package realcraft.bukkit.falling.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.falling.FallPlayer;
import realcraft.bukkit.falling.arena.FallArena;
import realcraft.bukkit.falling.arena.FallArenaPermission;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

import java.util.ArrayList;
import java.util.List;

public class FallCommandTrust extends FallCommand {

	public FallCommandTrust(){
		super("trust");
	}

	@Override
	public void perform(FallPlayer fPlayer,String[] args){
		FallArena arena = fPlayer.getOwnArena();
		if(arena == null || !arena.getPermission(fPlayer).isMinimum(FallArenaPermission.OWNER)){
			fPlayer.sendMessage("§cNemas opravneni spravovat tento ostrov.");
			return;
		}
		if(args.length == 0){
			fPlayer.sendMessage("Pridat spoluhrace");
			fPlayer.sendMessage("§6/ff trust §e<player>");
			return;
		}
		User user = Users.getUser(args[0]);
		if(user == null || user.equals(fPlayer.getUser())){
			fPlayer.sendMessage("§cHrac nenalezen.");
			return;
		}
		if(arena.getPermission(FallManager.getFallPlayer(user)).isMinimum(FallArenaPermission.TRUSTED)){
			fPlayer.sendMessage("§cHrac je jiz pridany.");
			return;
		}
		arena.getTrusted().add(FallManager.getFallPlayer(user));
		arena.save();
		FallManager.sendMessage(fPlayer,"§dSpoluhrac §f"+user.getName()+" §dpridan");
	}

	@Override
	public List<String> tabCompleter(FallPlayer fPlayer,String[] args){
		List<String> players = new ArrayList<>();
		for(Player target : Bukkit.getOnlinePlayers()) players.add(target.getName());
		return players;
	}
}