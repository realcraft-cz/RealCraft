package realcraft.bukkit.falling.commands;

import org.apache.commons.lang.StringUtils;
import realcraft.bukkit.falling.FallPlayer;
import realcraft.bukkit.falling.arena.FallArena;
import realcraft.bukkit.utils.DateUtil;

import java.util.List;

public class FallCommandInfo extends FallCommand {

	public FallCommandInfo(){
		super("info");
	}

	@Override
	public void perform(FallPlayer fPlayer,String[] args){
		FallArena arena = fPlayer.getArena();
		if(arena == null){
			fPlayer.sendMessage("§cNejsi na zadnem ostrove.");
			return;
		}
		String[] trusted = new String[arena.getTrusted().size()];
		int idx2 = 0;
		for(FallPlayer fPlayer2 : arena.getTrusted()){
			trusted[idx2++] = fPlayer2.getUser().getRank().getChatColor()+"§l"+fPlayer2.getUser().getName();
		}
		fPlayer.sendMessage("§7§m"+StringUtils.repeat(" ",10)+"§r §6§lOstrov "+arena.getOwner().getRank().getChatColor()+arena.getOwner().getName()+" §7§m"+StringUtils.repeat(" ",47-+("§6§lOstrov "+arena.getOwner().getRank().getChatColor()+arena.getOwner().getName()).length()));
		if(trusted.length > 0) fPlayer.sendMessage("§7Trusted: "+StringUtils.join(trusted,", "));
		fPlayer.sendMessage("§7Vytvoreno: §f"+DateUtil.lastTime(arena.getCreated()));
		fPlayer.sendMessage("§7Odehrany cas: §f"+(Math.round((arena.getTicks()/20f/60/60)*10)/10.0)+" hodin");
	}

	@Override
	public List<String> tabCompleter(FallPlayer fPlayer,String[] args){
		return null;
	}
}