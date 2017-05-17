package com.suits;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.scheduler.BukkitRunnable;

import com.cosmetics.Cosmetic;
import com.cosmetics.Cosmetics;
import com.realcraft.RealCraft;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public abstract class Suit extends Cosmetic {

	private SuitType type;

	public Suit(SuitType type){
		super(type.toString(),CosmeticCategory.SUIT);
		this.type = type;
		if(this.type == SuitType.RAVE){
			BukkitRunnable runnable = new BukkitRunnable(){
	            @Override
	            public void run(){
	            	for(Player player : Bukkit.getServer().getOnlinePlayers()){
	            		if(isRunning(player)){
	            			onUpdate(player);
	            		}
	            	}
	            }
	        };
	        runnable.runTaskTimerAsynchronously(RealCraft.getInstance(),0,1);
		}
	}

	public SuitType getType(){
		return this.type;
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player && event.getSlotType() == SlotType.ARMOR && isRunning(((Player)event.getWhoClicked()))){
			event.setCancelled(true);
		}
	}

	public abstract void onCreate(Player player);
	public abstract void onUpdate(Player player);

	@Override
	public void clearCosmetic(Player player){
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		this.setRunning(player,false);
	}

	public void equip(Player player){
		if(!this.isEnabled(player)){
			player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
			return;
		}
		player.closeInventory();
		Cosmetics.clearSuits(player);
		this.setRunning(player,true);
		this.onCreate(player);
	}

	public boolean isEnabled(Player player){
		PermissionUser user = PermissionsEx.getUser(player);
		String option = user.getOption(type.toPermission());
		if(player.hasPermission("group.iVIP") || player.hasPermission("group.gVIP") || player.hasPermission("group.dVIP")) return true;
		return (option != null ? Boolean.valueOf(option) : false);
	}

	public void setEnabled(Player player,boolean enabled){
		PermissionUser user = PermissionsEx.getUser(player);
		user.setOption(type.toPermission(),""+enabled);
	}

	public String giveReward(Player player){
		this.setEnabled(player,true);
		return this.getCategory().toString()+" > "+type.toString();
	}

	public enum SuitType {
		RAVE, DIAMOND, GOLD, IRON;

		public String toString(){
			switch(this){
				case RAVE: return "§b§lR§a§la§e§lv§6§le §f§lbrneni";
				case DIAMOND: return "§b§lDiamond §f§lbrneni";
				case GOLD: return "§6§lGold §f§lbrneni";
				case IRON: return "§7§lIron §f§lbrneni";
			}
			return null;
		}

		public String toPermission(){
			return "cosmetics.suits."+this.name().toLowerCase();
		}

		public Material toMaterial(){
			switch(this){
				case RAVE: return Material.LEATHER_CHESTPLATE;
				case DIAMOND: return Material.DIAMOND_CHESTPLATE;
				case GOLD: return Material.GOLD_CHESTPLATE;
				case IRON: return Material.IRON_CHESTPLATE;
			}
			return Material.AIR;
		}

		public Byte toData(){
			return (byte)0;
		}

		public ArrayList<String> toLore(){
			ArrayList<String> lore = new ArrayList<String>();
			switch(this){
			case RAVE:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case DIAMOND:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case GOLD:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case IRON:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			default:
				break;
			}
			lore.clear();
			return lore;
		}
	}
}