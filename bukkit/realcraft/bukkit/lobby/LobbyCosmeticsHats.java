package realcraft.bukkit.lobby;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.Cosmetics;
import realcraft.bukkit.cosmetics.hats.Hat;
import realcraft.bukkit.lobby.LobbyCosmeticsMain.LobbyCosmeticsType;

public class LobbyCosmeticsHats {
	static LobbyCosmetics lobbycosmetics;
	static RealCraft plugin;

	static String arrowLeft = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmIwZjZlOGFmNDZhYzZmYWY4ODkxNDE5MWFiNjZmMjYxZDY3MjZhNzk5OWM2MzdjZjJlNDE1OWZlMWZjNDc3In19fQ==";
	static String arrowRight = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJmM2EyZGZjZTBjM2RhYjdlZTEwZGIzODVlNTIyOWYxYTM5NTM0YThiYTI2NDYxNzhlMzdjNGZhOTNiIn19fQ==";

	static int maxPage = 2;

	public static void init(LobbyCosmetics lobbycosmetics){
		LobbyCosmeticsGadgets.lobbycosmetics = lobbycosmetics;
		LobbyCosmeticsGadgets.plugin = RealCraft.getInstance();
	}

	public static String getName(){
		return LobbyCosmeticsMain.LobbyCosmeticsType.HATS.toString();
	}

	public static String getInventoryName(){
		return LobbyCosmeticsMain.LobbyCosmeticsType.HATS.toInventoryName();
	}

	public static void openMenu(Player player){
		openMenu(player,1);
	}

	public static void openMenu(Player player,int page){
		if(page < 1) page = 1;
		else if(page > maxPage) page = maxPage;
		Inventory menu = Bukkit.createInventory(player,6*9,LobbyCosmeticsMain.LobbyCosmeticsType.HATS.toInventoryName());

		if(page == 1){
			setItem(menu,getIndex(0,0),Hat.HatType.ASTRONAUT);
			setItem(menu,getIndex(0,1),Hat.HatType.SCARED);
			setItem(menu,getIndex(0,2),Hat.HatType.ANGEL);
			setItem(menu,getIndex(0,3),Hat.HatType.EMBARASSED);
			setItem(menu,getIndex(0,4),Hat.HatType.KISSY);
			setItem(menu,getIndex(0,5),Hat.HatType.SAD);
			setItem(menu,getIndex(0,6),Hat.HatType.COOL);
			setItem(menu,getIndex(0,7),Hat.HatType.SURPRISED);
			setItem(menu,getIndex(0,8),Hat.HatType.DEAD);
			setItem(menu,getIndex(1,0),Hat.HatType.CRYING);
			setItem(menu,getIndex(1,1),Hat.HatType.BIGSMILE);
			setItem(menu,getIndex(1,2),Hat.HatType.WINK);
			setItem(menu,getIndex(1,3),Hat.HatType.DERP);
			setItem(menu,getIndex(1,4),Hat.HatType.SMILE);
			setItem(menu,getIndex(1,5),Hat.HatType.IRON);
			setItem(menu,getIndex(1,6),Hat.HatType.GOLD);
			setItem(menu,getIndex(1,7),Hat.HatType.DIAMOND);
			setItem(menu,getIndex(1,8),Hat.HatType.PISTON);
			setItem(menu,getIndex(2,0),Hat.HatType.COMMANDBLOCK);
			setItem(menu,getIndex(2,1),Hat.HatType.MUSIC);
			setItem(menu,getIndex(2,2),Hat.HatType.SQUID);
			setItem(menu,getIndex(2,3),Hat.HatType.CHICKEN);
			setItem(menu,getIndex(2,4),Hat.HatType.PIG);
			setItem(menu,getIndex(2,5),Hat.HatType.BLAZE);
			setItem(menu,getIndex(2,6),Hat.HatType.SHEEP);
			setItem(menu,getIndex(2,7),Hat.HatType.GOLEM);
			setItem(menu,getIndex(2,8),Hat.HatType.ENDERMAN);
			setItem(menu,getIndex(3,0),Hat.HatType.MARIO);
			setItem(menu,getIndex(3,1),Hat.HatType.LUIGI);
			setItem(menu,getIndex(3,2),Hat.HatType.BATMAN);
			setItem(menu,getIndex(3,3),Hat.HatType.CHEST);
			setItem(menu,getIndex(3,4),Hat.HatType.SKULL);
			setItem(menu,getIndex(3,5),Hat.HatType.GHOST);
			setItem(menu,getIndex(3,6),Hat.HatType.JACKOLANTERN);
			setItem(menu,getIndex(3,7),Hat.HatType.SCARYCLOW);
			setItem(menu,getIndex(3,8),Hat.HatType.SANTA);
		}
		else if(page == 2){
			setItem(menu,getIndex(0,0),Hat.HatType.SNOWMAN);
			setItem(menu,getIndex(0,1),Hat.HatType.PRESENT);
			setItem(menu,getIndex(0,2),Hat.HatType.ELF);
		}

		ArrayList<String> lore;

		if(page > 1) menu.setItem(getIndex(4,0),getHead("§7§lZpet",arrowLeft));
		else if(page < maxPage) menu.setItem(getIndex(4,8),getHead("§7§lDalsi",arrowRight));

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro navrat");
		lore.add("§7do hlavniho menu.");
		menu.setItem(getIndex(5,3),getItem("§e§lDoplnky",Material.CHEST,(byte)0,1,lore));

		lore = new ArrayList<String>();
		lore.add("§7Klikni pro odebrani helmy.");
		menu.setItem(getIndex(5,5),getItem("§c§lOdebrat helmu",Material.BARRIER,(byte)0,1,lore));

		player.openInventory(menu);
	}

	public static ItemStack getHead(String name,String url){
		ItemStack head = new ItemStack(Material.SKULL_ITEM,1,(short)3);
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(),null);
		profile.getProperties().put("textures",new Property("textures",url));
		Field profileField = null;
		try {
			profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta,profile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		headMeta.setDisplayName(name);
		head.setItemMeta(headMeta);
		return head;
	}

	public static int getIndex(int row,int column){
		return (row*9)+column;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getItem(String name,Material material,Byte data,int amount,ArrayList<String> lore){
		ItemStack itemstack = new ItemStack(material,amount,(short)0,data);
		ItemMeta meta = itemstack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		itemstack.setItemMeta(meta);
		return itemstack;
	}

	public static void setItem(Inventory menu,int index,Hat.HatType hat){
		Player player = (Player) menu.getHolder();
		boolean enabled = hat.isEnabled(player);
		if(enabled){
			menu.setItem(index,hat.toItemStack());
		}
		else menu.setItem(index,getItem(hat.toString(),Material.INK_SACK,(byte)8,1,null));
	}

	public static void InventoryClickEvent(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if(item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
			player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
			if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.ASTRONAUT.toString())){
				Hat.HatType.ASTRONAUT.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.SCARED.toString())){
				Hat.HatType.SCARED.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.ANGEL.toString())){
				Hat.HatType.ANGEL.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.EMBARASSED.toString())){
				Hat.HatType.EMBARASSED.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.KISSY.toString())){
				Hat.HatType.KISSY.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.SAD.toString())){
				Hat.HatType.SAD.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.COOL.toString())){
				Hat.HatType.COOL.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.SURPRISED.toString())){
				Hat.HatType.SURPRISED.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.DEAD.toString())){
				Hat.HatType.DEAD.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.CRYING.toString())){
				Hat.HatType.CRYING.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.BIGSMILE.toString())){
				Hat.HatType.BIGSMILE.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.WINK.toString())){
				Hat.HatType.WINK.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.DERP.toString())){
				Hat.HatType.DERP.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.SMILE.toString())){
				Hat.HatType.SMILE.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.IRON.toString())){
				Hat.HatType.IRON.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.GOLD.toString())){
				Hat.HatType.GOLD.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.DIAMOND.toString())){
				Hat.HatType.DIAMOND.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.PISTON.toString())){
				Hat.HatType.PISTON.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.COMMANDBLOCK.toString())){
				Hat.HatType.COMMANDBLOCK.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.MUSIC.toString())){
				Hat.HatType.MUSIC.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.SQUID.toString())){
				Hat.HatType.SQUID.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.CHICKEN.toString())){
				Hat.HatType.CHICKEN.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.PIG.toString())){
				Hat.HatType.PIG.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.BLAZE.toString())){
				Hat.HatType.BLAZE.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.SHEEP.toString())){
				Hat.HatType.SHEEP.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.GOLEM.toString())){
				Hat.HatType.GOLEM.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.ENDERMAN.toString())){
				Hat.HatType.ENDERMAN.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.MARIO.toString())){
				Hat.HatType.MARIO.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.LUIGI.toString())){
				Hat.HatType.LUIGI.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.BATMAN.toString())){
				Hat.HatType.BATMAN.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.CHEST.toString())){
				Hat.HatType.CHEST.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.SKULL.toString())){
				Hat.HatType.SKULL.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.GHOST.toString())){
				Hat.HatType.GHOST.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.JACKOLANTERN.toString())){
				Hat.HatType.JACKOLANTERN.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.SCARYCLOW.toString())){
				Hat.HatType.SCARYCLOW.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.SANTA.toString())){
				Hat.HatType.SANTA.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.SNOWMAN.toString())){
				Hat.HatType.SNOWMAN.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.PRESENT.toString())){
				Hat.HatType.PRESENT.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(Hat.HatType.ELF.toString())){
				Hat.HatType.ELF.equip(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7§lZpet")){
				LobbyCosmeticsHats.openMenu(player,1);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§7§lDalsi")){
				LobbyCosmeticsHats.openMenu(player,2);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase(LobbyCosmeticsType.MAIN.toItemName())){
				LobbyCosmeticsMain.openMenu(player);
			}
			else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§c§lOdebrat helmu")){
				Cosmetics.clearHats(player);
				LobbyCosmeticsMain.openMenu(player);
			}
		}
	}
}