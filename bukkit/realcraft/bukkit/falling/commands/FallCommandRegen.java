package realcraft.bukkit.falling.commands;

import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.falling.FallPlayer;
import realcraft.bukkit.falling.arena.FallArena;
import realcraft.bukkit.falling.arena.FallArenaPermission;
import realcraft.bukkit.falling.exceptions.FallArenaRegionGenerateTimeoutException;
import realcraft.bukkit.falling.exceptions.FallArenaRegionGeneratingException;
import realcraft.share.utils.RandomUtil;
import realcraft.share.utils.StringUtil;

import java.util.List;

public class FallCommandRegen extends FallCommand {

	public FallCommandRegen(){
		super("regen");
	}

	@Override
	public void perform(FallPlayer fPlayer,String[] args){
		FallArena arena = fPlayer.getArena();
		if(arena == null || arena.getPermission(fPlayer) != FallArenaPermission.OWNER){
			fPlayer.sendMessage("§cNemas opravneni spravovat tento ostrov.");
			return;
		}
		if(args.length == 0){
			fPlayer.setRegenCode(RandomUtil.getRandomHex(2));
			fPlayer.sendMessage("Pro pregenerovani ostrovu napis znovu §6/ff regen "+fPlayer.getRegenCode());
			return;
		}
		if(!args[0].equalsIgnoreCase(fPlayer.getRegenCode())){
			fPlayer.sendMessage("§cKontrolni kod se neshoduje.");
			return;
		}
		if(arena.getRegion().isGenerating()){
			fPlayer.sendMessage("§cOstrov se jiz generuje.");
			return;
		}
		try {
			fPlayer.getPlayer().getInventory().clear();
			fPlayer.getPlayer().getEnderChest().clear();
			arena.getRegion().regenerate();
			FallManager.sendMessage(fPlayer,"§fGeneruji novy ostrov, prosim vyckejte ...");
		} catch (FallArenaRegionGeneratingException e){
			fPlayer.sendMessage("§cOstrov se jiz generuje.");
		} catch (FallArenaRegionGenerateTimeoutException e){
			fPlayer.sendMessage("§cOstrov muzes znovu vygenerovat za "+(int)Math.ceil(e.getRemainingSeconds()/60f)+" "+StringUtil.inflect(e.getRemainingSeconds(),new String[]{"minutu","minuty","minut"})+".");
		}
	}

	@Override
	public List<String> tabCompleter(FallPlayer fPlayer,String[] args){
		return null;
	}
}