package realcraft.bukkit.others;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand extends Command implements CommandExecutor, TabCompleter {

	public AbstractCommand(String... names){
		super(names[0]);
		if(names.length > 1) this.setAliases(new ArrayList<>(Arrays.asList(Arrays.copyOfRange(names,1,names.length))));
		this.register();
	}

	private void register(){
		if(Bukkit.getPluginCommand(this.getName()) == null){
			/*try {
				Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
				bukkitCommandMap.setAccessible(true);
				CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
				commandMap.register(this.getName(),this);
			} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}*/
			((CraftServer)Bukkit.getServer()).getCommandMap().register(this.getName(),this);
		} else {
			if(RealCraft.getInstance().getCommand(this.getName()) != null){
				RealCraft.getInstance().getCommand(this.getName()).setExecutor(this);
				RealCraft.getInstance().getCommand(this.getName()).setAliases(this.getAliases());
				RealCraft.getInstance().getCommand(this.getName()).setTabCompleter(this);
			}
			Bukkit.getPluginCommand(this.getName()).setExecutor(this);
			Bukkit.getPluginCommand(this.getName()).setAliases(this.getAliases());
			Bukkit.getPluginCommand(this.getName()).setTabCompleter(this);
		}
	}

	public abstract void perform(Player player,String[] args);
	public List<String> tabCompleter(Player player,String[] args){
		return this.getPlayersCompletions();
	}

	@Override
	public boolean execute(CommandSender sender,String s,String[] args){
		if(!(sender instanceof Player)) return false;
		this.perform((Player)sender,args);
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender,Command command,String s,String[] args){
		if(!(sender instanceof Player)) return false;
		this.perform((Player)sender,args);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args){
		if(!(sender instanceof Player)) return null;
		List<String> list = this.tabCompleter((Player)sender,args);
		if(list == null) list = new ArrayList<>();
		return list;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender,Command command,String s,String[] args){
		if(!(sender instanceof Player)) return null;
		List<String> list = this.tabCompleter((Player)sender,args);
		if(list == null) list = new ArrayList<>();
		return list;
	}

	public List<String> getPlayersCompletions(){
		List<String> completions = new ArrayList<>();
		for(Player target : Bukkit.getOnlinePlayers()) completions.add(target.getName());
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
		public List<String> tabCompleter(Player player,String[] args){
			return null;
		}
	}
}