package realcraft.bukkit.fights;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum FightKit {
	ARCHER, NINJA, KNIGHT, VIKING, TANK;

	private ArrayList<FightKitItem> items;
	private ArrayList<PotionEffect> effects;

	public String getName(){
		return this.toString();
	}

	private Material getChestplate(){
		switch(this){
			case ARCHER: return Material.GOLD_CHESTPLATE;
			case NINJA: return Material.CHAINMAIL_CHESTPLATE;
			case KNIGHT: return Material.IRON_CHESTPLATE;
			case VIKING: return Material.LEATHER_CHESTPLATE;
			case TANK: return Material.DIAMOND_CHESTPLATE;
		}
		return null;
	}

	private ArrayList<FightKitItem> getItems(){
		if(items == null){
			items = new ArrayList<FightKitItem>();
			if(this == ARCHER){
				items.add(new FightKitItem(0,new ItemStack(Material.WOOD_SWORD)));
				items.add(new FightKitItem(1,new ItemStack(Material.BOW)));
				items.add(new FightKitItem(2,new ItemStack(Material.ARROW,20)));
				items.add(new FightKitItemHelmet(new ItemStack(Material.GOLD_HELMET),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,2)));
				items.add(new FightKitItemChestplate(new ItemStack(Material.GOLD_CHESTPLATE),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,3)));
				items.add(new FightKitItemLeggings(new ItemStack(Material.GOLD_LEGGINGS),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,2)));
				items.add(new FightKitItemBoots(new ItemStack(Material.GOLD_BOOTS),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,2)));
			}
			else if(this == NINJA){
				items.add(new FightKitItem(0,new ItemStack(Material.IRON_SWORD)));
				items.add(new FightKitItemChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,3)));
				items.add(new FightKitItemBoots(new ItemStack(Material.CHAINMAIL_BOOTS),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,2)));
			}
			else if(this == KNIGHT){
				items.add(new FightKitItem(0,new ItemStack(Material.IRON_SWORD)));
				items.add(new FightKitItemHelmet(new ItemStack(Material.CHAINMAIL_HELMET),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)));
				items.add(new FightKitItemChestplate(new ItemStack(Material.IRON_CHESTPLATE),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)));
				items.add(new FightKitItemLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,2)));
				items.add(new FightKitItemBoots(new ItemStack(Material.CHAINMAIL_BOOTS),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)));
			}
			else if(this == VIKING){
				items.add(new FightKitItem(0,new ItemStack(Material.GOLD_AXE),new FightKitEnchant(Enchantment.DURABILITY,5)));
				items.add(new FightKitItemHelmet(new ItemStack(Material.IRON_HELMET),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)));
				items.add(new FightKitItemChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)));
				items.add(new FightKitItemLeggings(new ItemStack(Material.LEATHER_LEGGINGS),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)));
				items.add(new FightKitItemBoots(new ItemStack(Material.LEATHER_BOOTS),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)));
			}
			else if(this == TANK){
				items.add(new FightKitItem(0,new ItemStack(Material.STONE_SWORD)));
				items.add(new FightKitItemHelmet(new ItemStack(Material.IRON_HELMET),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)));
				items.add(new FightKitItemChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)));
				items.add(new FightKitItemLeggings(new ItemStack(Material.IRON_LEGGINGS),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)));
				items.add(new FightKitItemBoots(new ItemStack(Material.IRON_BOOTS),new FightKitEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)));
			}
			items.add(new FightKitItem(7,new ItemStack(Material.COOKED_BEEF,4)));
			items.add(new FightKitItem(8,new ItemStack(Material.GOLDEN_APPLE)));
		}
		return items;
	}

	private ArrayList<PotionEffect> getEffects(){
		if(effects == null){
			effects = new ArrayList<PotionEffect>();
			if(this == NINJA){
				effects.add(new PotionEffect(PotionEffectType.SPEED,Integer.MAX_VALUE,1,false,false));
			}
		}
		return effects;
	}

	public void equipPlayer(FightPlayer fPlayer){
		fPlayer.getPlayer().getInventory().clear();
		for(FightKitItem item : this.getItems()){
			if(item instanceof FightKitItemHelmet) fPlayer.getPlayer().getInventory().setHelmet(item.getItemStack());
			else if(item instanceof FightKitItemChestplate) fPlayer.getPlayer().getInventory().setChestplate(item.getItemStack());
			else if(item instanceof FightKitItemLeggings) fPlayer.getPlayer().getInventory().setLeggings(item.getItemStack());
			else if(item instanceof FightKitItemBoots) fPlayer.getPlayer().getInventory().setBoots(item.getItemStack());
			else fPlayer.getPlayer().getInventory().setItem(item.getIndex(),item.getItemStack());
		}
		for(PotionEffect effect : this.getEffects()){
			fPlayer.getPlayer().addPotionEffect(effect,true);
		}
	}

	public ItemStack getMenuItemStack(){
		ItemStack itemStack = new ItemStack(this.getChestplate());
		ItemMeta meta = itemStack.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ArrayList<String> lore = new ArrayList<String>();
		meta.setDisplayName("§6§l"+this.getName());
		lore.add("");
		for(FightKitItem item : this.getItems()){
			if(item instanceof FightKitItemHelmet || item instanceof FightKitItemChestplate || item instanceof FightKitItemLeggings || item instanceof FightKitItemBoots) lore.add("§8- §7"+item.getItemStack().getType().toString());
			else lore.add("§8- §r"+(item.getItemStack().getAmount() > 1 ? item.getItemStack().getAmount()+"x " : "")+item.getItemStack().getType().toString());
		}
		meta.setLore(lore);
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	private class FightKitItem {

		private int index;
		private ItemStack item;

		public FightKitItem(int index,ItemStack item,FightKitEnchant... enchants){
			this.index = index;
			this.item = item;
			for(FightKitEnchant enchant : enchants){
				ItemMeta meta = this.item.getItemMeta();
				meta.addEnchant(enchant.getEnchantment(),enchant.getLevel(),true);
				this.item.setItemMeta(meta);
			}
		}

		public int getIndex(){
			return index;
		}

		public ItemStack getItemStack(){
			return item;
		}
	}

	private class FightKitEnchant {

		private Enchantment enchant;
		private int level;

		public FightKitEnchant(Enchantment enchant,int level){
			this.enchant = enchant;
			this.level = level;
		}

		public Enchantment getEnchantment(){
			return enchant;
		}

		public int getLevel(){
			return level;
		}
	}

	private class FightKitItemHelmet extends FightKitItem {
		public FightKitItemHelmet(ItemStack item,FightKitEnchant... enchants){
			super(0,item,enchants);
		}
	}

	private class FightKitItemChestplate extends FightKitItem {
		public FightKitItemChestplate(ItemStack item,FightKitEnchant... enchants){
			super(0,item,enchants);
		}
	}

	private class FightKitItemLeggings extends FightKitItem {
		public FightKitItemLeggings(ItemStack item,FightKitEnchant... enchants){
			super(0,item,enchants);
		}
	}

	private class FightKitItemBoots extends FightKitItem {
		public FightKitItemBoots(ItemStack item,FightKitEnchant... enchants){
			super(0,item,enchants);
		}
	}
}