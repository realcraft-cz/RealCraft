package realcraft.bukkit.chat;

import org.bukkit.event.Listener;

import realcraft.bukkit.RealCraft;

public class ChatNotice implements Listener {
	RealCraft plugin;

	boolean enabled = false;

	public ChatNotice(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.config.getBoolean("chatnotice.enabled")){
			enabled = true;
			plugin.getServer().getPluginManager().registerEvents(this,plugin);
		}
	}

	public void onReload(){
		enabled = false;
		if(plugin.config.getBoolean("chatnotice.enabled")){
			enabled = true;
		}
	}

	/*@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		if(!enabled || event.isCancelled()) return;

		String message = event.getMessage().toLowerCase();
		String nfdNormalizedString = Normalizer.normalize(message, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		message = pattern.matcher(nfdNormalizedString).replaceAll("");

		for(Player player : plugin.getServer().getOnlinePlayers()){
			if(player != event.getPlayer() && plugin.playermanazer.getPlayerInfo(player).getNoticeChat()){
				if(containsNoticeWords(player,message)){
					player.playSound(player.getLocation(),plugin.playermanazer.getPlayerInfo(player).getNoticeSound(),1,1);

				}
			}
		}
	}

	public boolean containsNoticeWords(Player player,String message){
		String [] words = plugin.playermanazer.getPlayerInfo(player).getNoticeWords();
		for(String word : words){
			Pattern p = Pattern.compile(word);
		    Matcher m = p.matcher(message);
			if(m.find()) return true;
		}
		return false;
	}*/
}