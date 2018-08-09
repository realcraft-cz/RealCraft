package realcraft.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.chat.ChatCommandSpy;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand implements Listener {

	private String[] names;

	public AbstractCommand(String... names){
		this.names = names;
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	public boolean match(String command){
		for(String name : names){
			if(name.equalsIgnoreCase(command)) return true;
		}
		return false;
	}

	public abstract void perform(Player player,String[] args);
	public List<String> onTabComplete(Player player,String[] args){
		throw new NotImplementedException();
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).split(" ")[0].toLowerCase();
		if(this.match(command)){
			event.setCancelled(true);
			String[] args = event.getMessage().substring(1).split(" ");
			args = Arrays.copyOfRange(args,1,args.length);
			ChatCommandSpy.sendCommandMessage(player,event.getMessage().substring(1).toLowerCase());
			this.perform(player,args);
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void TabCompleteEvent(TabCompleteEvent event){
		if(event.getSender() instanceof Player){
			Player player = (Player)event.getSender();
			String command = event.getBuffer().split(" ")[0].toLowerCase();
			if(this.match(command)){
				event.getCompletions().clear();
				String[] args = event.getBuffer().split(" ");
				args = Arrays.copyOfRange(args,1,args.length);
				try {
					List<String> completions = this.onTabComplete(player,args);
					if(completions != null) event.getCompletions().addAll(completions);
				} catch (NotImplementedException e){
				}
			}
		}
	}

	public List<String> getPlayersCompletions(){
		List<String> completions = new ArrayList<>();
		for(Player target : Bukkit.getOnlinePlayers()){
			completions.add(target.getName());
		}
		return completions;
	}

	public static class NullCommand extends AbstractCommand {

		public NullCommand(String... names){
			super(names);
		}

		@Override
		public void perform(Player player,String[] args){
			player.sendMessage("Unknown command. Type \"/help\" for help.");
		}

		@Override
		public List<String> onTabComplete(Player player,String[] args){
			return null;
		}
	}
}