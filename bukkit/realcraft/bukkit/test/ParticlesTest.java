package realcraft.bukkit.test;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;

public class ParticlesTest implements CommandExecutor {

	public ParticlesTest(){
		RealCraft.getInstance().getCommand("efekt").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("efekt") && player.hasPermission("group.Manazer")){
			if(args.length == 0){
				player.sendMessage("/efekt <effect> [count] [speed]");
				return true;
			}
			try {
				Particle particle = Particle.valueOf(args[0].toUpperCase());
				int count = 10;
				float speed = 0.1f;
				if(args.length > 1) count = Integer.valueOf(args[1]);
				if(args.length > 2) speed = Float.valueOf(args[2]);
				Location location = player.getLocation();
				location.setPitch(0f);
				location.add(0,1,0);
				location.add(location.getDirection().setY(0).normalize().multiply(3));
				player.spawnParticle(particle,location,count,0,0,0,speed);
				player.sendMessage("§7Effect:§r "+particle.toString());
			} catch (Exception e){
				player.sendMessage("§cEffect not found!");
				return true;
			}
		}
		return true;
	}
}