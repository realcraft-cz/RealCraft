package com.parkour.menu;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.parkour.Parkour;

public enum ParkourMenuType {
	MAIN,
	NEWEST, BEST, LIKED, OWN,
	PREVIOUS, NEXT,
	CREATE, SETTINGS,
	START, CHECKPOINT, FINISH, TEST, RENAME, CONFIRM,
	CLOCK, FLOOR, BIOME, WORLDEDIT,
	COLLABORATORS,
	BACK,
	RESPAWN, RESET, EXIT,
	RATING, RATING_YES, RATING_NO;

	public String getInventoryName(){
		switch(this){
			case MAIN: return "Parkour";
			case NEWEST: return "Parkour > Nejnovejsi";
			case BEST: return "Parkour > Nejlepe hodnocene";
			case LIKED: return "Parkour > Libi se mi";
			case OWN: return "Parkour > Moje parkoury";
			case SETTINGS: return "Parkour > Nastaveni";
			case RENAME: return "Parkour > Nazev parkouru";
			case RATING: return "Parkour > Hodnotit parkour";
			case CLOCK: return "Parkour > Nastaveni > Cas";
			case FLOOR: return "Parkour > Nastaveni > Podlaha";
			case BIOME: return "Parkour > Nastaveni > Biom";
			case COLLABORATORS: return "Pakrour > Nastavení > Spolupracovnici";
			default:break;
		}
		return null;
	}

	public String getItemName(){
		switch(this){
			case MAIN: return "§a§lParkour";
			case NEWEST: return "§b§lNejnovejsi";
			case BEST: return "§e§lNejlepe hodnocene";
			case LIKED: return "§a§lLibi se mi";
			case OWN: return "§7§lMoje parkoury";
			case CREATE: return "§d§lVytvorit parkour";

			case PREVIOUS: return "§6§lPredchozi";
			case NEXT: return "§6§lDalsi";

			case SETTINGS: return "§6§lNastaveni";
			case START: return "§f§lPridat start";
			case CHECKPOINT: return "§e§lPridat checkpoint";
			case FINISH: return "§6§lPridat cil";
			case TEST: return "§7§lOtestovat";
			case RENAME: return "§f§lPrejmenovat";
			case CONFIRM: return "§d§lDokoncit";

			case CLOCK: return "§6§lNastavit cas";
			case FLOOR: return "§6§lNastavit podlahu";
			case BIOME: return "§6§lNastavit biom";
			case WORLDEDIT: return "§6§lWorldEdit";
			case COLLABORATORS: return "§3§lSpolupracovnici";
			case BACK: return "§c§lZpet";

			case RESPAWN: return "§b§lResetovat pozici";
			case RESET: return "§d§lZkusit znovu";
			case EXIT: return "§c§lOpustit parkour";
			case RATING: return "§e§lHodnotit parkour";
			case RATING_YES: return "§a§lLibi se mi";
			case RATING_NO: return "§c§lNelibi se mi";
			default:break;
		}
		return null;
	}

	public Material getMaterial(){
		switch(this){
			case MAIN: return Material.WATCH;
			case NEWEST: return Material.NETHER_STAR;
			case BEST: return Material.GOLD_INGOT;
			case LIKED: return Material.GOLD_NUGGET;
			case OWN: return Material.SULPHUR;
			case CREATE: return Material.ANVIL;

			case PREVIOUS: return Material.PAPER;
			case NEXT: return Material.PAPER;

			case SETTINGS: return Material.BOOK;
			case START: return Material.STONE_PLATE;
			case CHECKPOINT: return Material.IRON_PLATE;
			case FINISH: return Material.GOLD_PLATE;
			case TEST: return Material.LADDER;
			case RENAME: return Material.BOOK_AND_QUILL;
			case CONFIRM: return Material.NETHER_STAR;

			case CLOCK: return Material.WATCH;
			case FLOOR: return Material.GRASS;
			case BIOME: return Material.DEAD_BUSH;
			case WORLDEDIT: return Material.WOOD_AXE;
			case COLLABORATORS: return Material.SKULL_ITEM;
			case BACK: return Material.REDSTONE_BLOCK;

			case RESPAWN: return Material.ARROW;
			case RESET: return Material.SHEARS;
			case EXIT: return Material.REDSTONE_BLOCK;
			case RATING: return Material.GOLD_INGOT;
			case RATING_YES: return Material.EMERALD_BLOCK;
			case RATING_NO: return Material.REDSTONE_BLOCK;
			default:break;
		}
		return null;
	}

	public byte getByte(){
		return (byte)0;
	}

	public ArrayList<String> getLore(){
		ArrayList<String> lore = new ArrayList<String>();
		switch(this){
			case CREATE:
				lore.add("§7Vytvorte vlastni parkour");
				lore.add("§7a ukazte ho vsem ostatnim.");
				break;
			case START:
				lore.add("§7Startovni pozici nastavite");
				lore.add("§7polozenim kamenne naslapne desky.");
				lore.add("§7Tato pozice musi byt pouze jedna.");
				break;
			case CHECKPOINT:
				lore.add("§7Zachytne body (checkpointy) nastavite");
				lore.add("§7polozenim zelezne naslapne desky.");
				lore.add("§7Techto bodu muze byt libovolny pocet.");
				break;
			case FINISH:
				lore.add("§7Cilovou pozici nastavite");
				lore.add("§7polozenim zlate naslapne desky.");
				lore.add("§7Tato pozice musi byt pouze jedna.");
				break;
			case TEST:
				lore.add("§7Pred dokoncenim je potreba parkour");
				lore.add("§7alespon jednou otestovat.");
				break;
			case RENAME:
				lore.add("§7Nazev parkouru musi byt unikatni,");
				lore.add("§7muze byt dlouhy maximalne 31 znaku");
				lore.add("§7a musi obsahovat pouze znaky [a-zA-Z0-9].");
				lore.add("§7Priklad: MujParkour1");
				break;
			case CONFIRM:
				lore.add("§7Dokoncenim parkouru jiz nebudete");
				lore.add("§7moci parkour nadale upravovat.");
				lore.add("§7Pred dokoncenim je potreba parkour");
				lore.add("§7alespon jednou otestovat.");
				break;
			case FLOOR:
				lore.add("§7Vyberte si podlahu, kterou chcete");
				lore.add("§7vyplnit cely spodek parkouru.");
				break;
			case BIOME:
				lore.add("§7Vyberte si biom, ktery chcete");
				lore.add("§7nastavit pro svuj parkour.");
				break;
			case WORLDEDIT:
				lore.add("§7Pomoci nastroje WorldEdit muzete");
				lore.add("§7svuj parkour upravovat efektivneji.");
				break;
			case COLLABORATORS:
				lore.add("§7Pridat muzete az "+Parkour.PARKOUR_COLLABORATORS_LIMIT+" hrace,");
				lore.add("§7kteri mohou stavet s vami.");
				break;
			default:break;
		}
		return lore;
	}

	@SuppressWarnings("deprecation")
	public ItemStack getItemStack(){
		ItemStack item = new ItemStack(this.getMaterial(),1,(short)0,this.getByte());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.getItemName());
		meta.setLore(this.getLore());
		item.setItemMeta(meta);
		return item;
	}
}