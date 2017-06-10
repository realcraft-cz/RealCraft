package com.realcraft.lobby;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.anticheat.AntiCheat;
import com.realcraft.RealCraft;

import net.minecraft.server.v1_12_R1.PacketPlayOutMount;

public class LobbyPlayerRider implements Listener, Runnable {
	RealCraft plugin;

	HashMap<String,String> riding = new HashMap<String,String>();

	public LobbyPlayerRider(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,1*20,1*20);
	}

	public void onReload(){
	}

	@Override
	public void run(){
		for(Player player : plugin.getServer().getOnlinePlayers()){
			if(riding.containsKey(player.getName()) && player.getVehicle() == null){
				Player vehicle = plugin.getServer().getPlayer(riding.get(player.getName()));
				if(vehicle != null){
					this.leaveVehicle(player,vehicle);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		if(event.getRightClicked() instanceof Player && event.getPlayer().getWorld().getName().equalsIgnoreCase("world") && event.getHand() == EquipmentSlot.HAND && event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR){
			Player player = event.getPlayer();
			if(player.getPassenger() == null){
				Entity player2 = event.getRightClicked();
				if(player2.getVehicle() == null && RealCraft.getInstance().lobby.lobbycitizens != null && !RealCraft.getInstance().lobby.lobbycitizens.npcRegistry.isNPC(player2)){
					player.eject();
					player.setPassenger(player2);
					PacketPlayOutMount packet = new PacketPlayOutMount(((CraftPlayer)player).getHandle());
					((CraftPlayer)(player)).getHandle().playerConnection.sendPacket(packet);
					riding.put(player2.getName(),player.getName());
				}
			}
			else if(player.getPassenger() != null && player.getPassenger() instanceof Player){
				Player passenger = (Player)player.getPassenger();
				this.leaveVehicle(passenger,player);
				this.throwPlayer(player,passenger);
			}
		}
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		if(event.getAction() != Action.PHYSICAL){
			Player player = event.getPlayer();
			if(player.getWorld().getName().equalsIgnoreCase("world") && player.getPassenger() != null && player.getPassenger() instanceof Player){
				Player passenger = (Player)player.getPassenger();
				this.leaveVehicle(passenger,player);
				this.throwPlayer(player,passenger);
			}
		}
	}

	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(riding.containsKey(player.getName()) && player.getVehicle() == null){
			Player vehicle = plugin.getServer().getPlayer(riding.get(player.getName()));
			if(vehicle != null){
				this.leaveVehicle(player,vehicle);
			}
		}
	}

	public void throwPlayer(Player player,Player passenger){
		passenger.setFlying(false);
		passenger.teleport(player.getEyeLocation().add(0,1,0));
		passenger.setVelocity(player.getLocation().getDirection().multiply(2));
		passenger.setAllowFlight(false);
		AntiCheat.exempt(passenger,2000);
	}

	public void leaveVehicle(Player player,Player vehicle){
		vehicle.eject();
		PacketPlayOutMount packet = new PacketPlayOutMount(((CraftPlayer)vehicle).getHandle());
		((CraftPlayer)(vehicle)).getHandle().playerConnection.sendPacket(packet);
		riding.remove(player.getName());
	}
}