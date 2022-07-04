package realcraft.bukkit.others;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import realcraft.bukkit.RealCraft;

import java.util.ArrayList;

public class WEBlockCompleter implements Listener {

	private static final String[] COMMANDS = new String[]{
			"set",
			"replace",
			"replacenear",
			"brush",
			"walls",
			"cyl",
			"hcyl",
			"sphere",
			"hsphere",
			"fill",
			"mask",
			"gmask",
	};

	public WEBlockCompleter(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler
	public void TabCompleteEvent(TabCompleteEvent event){
		String[] keywords = event.getBuffer().split(" ");
		String keyword = keywords[keywords.length-1].toUpperCase();
		for(String command : COMMANDS){
			if(event.getBuffer().startsWith("/"+command+" ") || event.getBuffer().startsWith("//"+command+" ")){
				if(!event.getCompletions().isEmpty()){
					event.getCompletions().clear();
					ArrayList<String> types = new ArrayList<>();
					for(Material type : Material.values()){
						if(type.toString().toUpperCase().contains(keyword)){
							types.add(type.toString());
						}
					}
					event.setCompletions(types);
				}
				break;
			}
		}
	}
}