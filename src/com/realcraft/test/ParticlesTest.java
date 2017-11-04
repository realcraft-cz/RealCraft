package com.realcraft.test;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.realcraft.RealCraft;

public class ParticlesTest implements CommandExecutor {

	ArrayList<Sound> sounds = new ArrayList<Sound>();
	BukkitTask task = null;
	int currentId;
	float pitch = 1f;

	public ParticlesTest(){
		RealCraft.getInstance().getCommand("efekt").setExecutor(this);
		for(Sound sound : Sound.values()){
			sounds.add(sound);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player player = (Player) sender;
		if(command.getName().equalsIgnoreCase("efekt") && player.hasPermission("group.Manazer")){
			if(task != null){
				task.cancel();
				task = null;
			}
			if(args.length == 0){
				player.sendMessage("/efekt <id|all> [pitch] [speed] [fromid]");
				return true;
			}
			if(args.length > 0){
				pitch = 1f;
				if(args.length > 1) pitch = Float.valueOf(args[1]);
				if(args[0].equalsIgnoreCase("all")){
					currentId = 0;
					int speed = 10;
					if(args.length > 2) speed = Integer.valueOf(args[2]);
					if(args.length > 3) currentId = Integer.valueOf(args[3]);
					task = Bukkit.getScheduler().runTaskTimerAsynchronously(RealCraft.getInstance(),new Runnable(){
						@Override
						public void run(){
							player.playSound(player.getLocation(),sounds.get(currentId),1f,pitch);
							player.sendMessage("Playing sound: "+sounds.get(currentId).toString()+" ["+currentId+"]");
							currentId ++;
						}
					},speed,speed);
				} else {
					int id = Integer.valueOf(args[0]);
					if(sounds.size() > id){
						player.playSound(player.getLocation(),sounds.get(id),1f,pitch);
						player.sendMessage("Playing sound: "+sounds.get(id).toString()+" ["+id+"]");
					}
				}
			}
		}
		return true;
	}
}