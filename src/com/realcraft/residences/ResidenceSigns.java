package com.realcraft.residences;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.realcraft.RealCraft;

public class ResidenceSigns implements Listener {
	RealCraft plugin;

	public ResidenceSigns(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	public void onReload(){
	}

	@EventHandler
	public void onSignInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasBlock() && event.getClickedBlock().getState() instanceof Sign){
			Sign sign = (Sign) event.getClickedBlock().getState();
			if(sign.getLine(0).equalsIgnoreCase(ChatColor.BLUE+""+ChatColor.BOLD+"[Residence]") && sign.getLine(1).length() > 2){
				String res = sign.getLine(1).substring(2);
				player.chat("/res tp "+res);
			}
		}
	}

	@EventHandler
	public void onSignCreate(SignChangeEvent event){
		Player player = event.getPlayer();
		if(event.getLine(0).equalsIgnoreCase("[res]") || event.getLine(0).equalsIgnoreCase("[residence]") || event.getLine(0).equalsIgnoreCase("[rezidence]")){
			String res = event.getLine(1);
			if(res.length() == 0){
				event.setCancelled(true);
				player.sendMessage("§cNeplatna residence!");
				return;
			}
			ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByName(res);
			if(residence == null){
				event.setCancelled(true);
				player.sendMessage("§cNeplatna residence!");
				return;
			}
			if(residence.getOwner() != player.getName() && !player.hasPermission("group.Admin") && !player.hasPermission("group.Moderator")){
				event.setCancelled(true);
				player.sendMessage("§cNejsi vlastnik teto residence!");
				return;
			}
			event.setLine(0,ChatColor.BLUE+""+ChatColor.BOLD+"[Residence]");
			event.setLine(1,ChatColor.DARK_RED+residence.getName());
			event.setLine(2,ChatColor.ITALIC+residence.getOwner());
		}
	}
}