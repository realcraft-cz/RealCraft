package com.gadgets;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cosmetics.Cosmetic;
import com.cosmetics.Cosmetics;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public abstract class Gadget extends Cosmetic {

	private GadgetType type;

	public Gadget(GadgetType type){
		super(type.toString(),CosmeticCategory.GADGET);
		this.type = type;
	}

	public GadgetType getType(){
		return this.type;
	}

	public abstract void onClick(Player player);

	@EventHandler
	public void onPlayerInventoryClick(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getInventory().getItemInMainHand().getType() == type.toMaterial() && player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName() && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(type.toString()) && (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
			event.setCancelled(true);
			if(!this.isRunning(player)){
				this.setRunning(player,true);
				this.removeItems(player.getInventory(),type.toMaterial(),1);
				this.giveAmount(player,-1);
				this.onClick(player);
			}
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			ItemStack item = event.getCurrentItem();
			if(item != null && item.getType() == type.toMaterial() && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(type.toString())){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		if(event.getItemDrop().getItemStack().getType() == type.toMaterial()){
			event.getItemDrop().remove();
			this.clearCosmetic(event.getPlayer());
		}
	}

	@Override
	public void clearCosmetic(Player player){
		ItemStack item = player.getInventory().getItem(5);
		if(item != null && item.getType() == type.toMaterial() && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(type.toString())) player.getInventory().setItem(5,new ItemStack(Material.AIR));
	}

	public void equip(Player player){
		int amount = this.getAmount(player);
		if(amount < 1){
			player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
			return;
		}
		player.closeInventory();
		Cosmetics.clearGadgets(player);
		player.getInventory().setItem(5,this.getItem(type.toString(),type.toMaterial(),type.toData(),amount,type.toLore()));
	}

	public int getAmount(Player player){
		PermissionUser user = PermissionsEx.getUser(player);
		String option = user.getOption(type.toPermission());
		return (option != null ? Integer.valueOf(option) : 0);
	}

	public void giveAmount(Player player,int amount){
		PermissionUser user = PermissionsEx.getUser(player);
		amount += this.getAmount(player);
		if(amount > 64) amount = 64;
		else if(amount < 0) amount = 0;
		user.setOption(type.toPermission(),""+amount);
	}

	public String giveRandomReward(Player player){
		int amount = type.getRandomAmount();
		this.giveAmount(player,amount);
		return this.getCategory().toString()+" > "+type.toString()+" §7("+amount+")";
	}

	@SuppressWarnings("deprecation")
	public ItemStack getItem(String name,Material material,Byte data,int amount,ArrayList<String> lore){
		ItemStack itemstack = new ItemStack(material,amount,(short)0,data);
		ItemMeta meta = itemstack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		itemstack.setItemMeta(meta);
		return itemstack;
	}

	public void removeItems(Inventory inventory,Material type,int amount){
        if (amount <= 0) return;
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) continue;
            if (type == is.getType()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) break;
                }
            }
        }
    }

	public static int getRandomNumber(int min, int max){
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	public enum GadgetType {
		Chickenator, MelonThrower, ColorBomb, ExplosiveSheep, TNT, Tsunami, Firework, GhostParty, FreezeCannon, PartyPopper, PaintballGun, DiamondShower, GoldShower, FoodShower;

		public String toString(){
			switch(this){
				case Chickenator: return "§6§lKureci bomba";
				case MelonThrower: return "§a§lMelounova bomba";
				case ColorBomb: return "§d§lBarevna bomba";
				case ExplosiveSheep: return "§4§lExplozivni ovce";
				case TNT: return "§c§lTNT bomba";
				case Tsunami: return "§9§lTsunami";
				case Firework: return "§c§lOhnostroj";
				case GhostParty: return "§7§lParty duchu";
				case FreezeCannon: return "§b§lMrazici delo";
				case PartyPopper: return "§e§lKonfety";
				case PaintballGun: return "§e§lPaintball zbran";
				case DiamondShower: return "§b§lDiamantova sprcha";
				case GoldShower: return "§e§lZlata sprcha";
				case FoodShower: return "§2§lJidlova sprcha";
			}
			return null;
		}

		public String toPermission(){
			return "cosmetics.gadgets."+this.name().toLowerCase();
		}

		public Material toMaterial(){
			switch(this){
				case Chickenator: return Material.COOKED_CHICKEN;
				case MelonThrower: return Material.MELON_BLOCK;
				case ColorBomb: return Material.WOOL;
				case ExplosiveSheep: return Material.SHEARS;
				case TNT: return Material.TNT;
				case Tsunami: return Material.WATER_BUCKET;
				case Firework: return Material.FIREWORK;
				case GhostParty: return Material.SKULL_ITEM;
				case FreezeCannon: return Material.ICE;
				case PartyPopper: return Material.GOLDEN_CARROT;
				case PaintballGun: return Material.DIAMOND_HOE;
				case DiamondShower: return Material.DIAMOND;
				case GoldShower: return Material.GOLD_INGOT;
				case FoodShower: return Material.APPLE;
			}
			return null;
		}

		public Byte toData(){
			switch(this){
				case Chickenator: return (byte)0;
				case MelonThrower: return (byte)0;
				case ColorBomb: return (byte)3;
				case ExplosiveSheep: return (byte)0;
				case TNT: return (byte)0;
				case Tsunami: return (byte)0;
				case Firework: return (byte)0;
				case GhostParty: return (byte)0;
				case FreezeCannon: return (byte)0;
				case PartyPopper: return (byte)0;
				case PaintballGun: return (byte)0;
				case DiamondShower: return (byte)0;
				case GoldShower: return (byte)0;
				case FoodShower: return (byte)0;
			}
			return null;
		}

		public int getRandomAmount(){
			switch(this){
				case Chickenator: return getRandomNumber(5,10);
				case MelonThrower: return getRandomNumber(5,10);
				case ColorBomb: return getRandomNumber(3,6);
				case ExplosiveSheep: return getRandomNumber(2,4);
				case TNT: return getRandomNumber(2,4);
				case Tsunami: return getRandomNumber(1,4);
				case Firework: return getRandomNumber(10,20);
				case GhostParty: return getRandomNumber(2,4);
				case FreezeCannon: return getRandomNumber(10,20);
				case PartyPopper: return getRandomNumber(30,40);
				case PaintballGun: return getRandomNumber(20,30);
				case DiamondShower: return getRandomNumber(3,6);
				case GoldShower: return getRandomNumber(3,6);
				case FoodShower: return getRandomNumber(3,6);
			}
			return 0;
		}

		public ArrayList<String> toLore(){
			ArrayList<String> lore = new ArrayList<String>();
			switch(this){
			default:
				break;
			}
			lore.clear();
			return lore;
		}
	}
}