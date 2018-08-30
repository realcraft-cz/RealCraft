package realcraft.bukkit.survival.sells;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Sells {

	private static final String PREFIX = "§6[Vykupna]§r ";
	private static HashMap<Integer,SellItem> items = new HashMap<>();

	public Sells(){
		new SellMenu();
		new SellTrader();
		register(Material.GRASS_BLOCK,16,8);
		register(Material.DIRT,16,4);
		register(Material.GRAVEL,16,8);
		register(Material.COBBLESTONE,16,4);
		register(Material.MOSSY_COBBLESTONE,16,16);
		register(Material.STONE,16,12);
		register(Material.GRANITE,16,12);
		register(Material.DIORITE,16,12);
		register(Material.ANDESITE,16,12);
		register(Material.OAK_LOG,16,16);
		register(Material.SPRUCE_LOG,16,16);
		register(Material.BIRCH_LOG,16,16);
		register(Material.JUNGLE_LOG,16,16);
		register(Material.ACACIA_LOG,16,16);
		register(Material.DARK_OAK_LOG,16,16);
		register(Material.SAND,16,4);
		register(Material.SANDSTONE,16,12);
		register(Material.RED_SAND,16,4);
		register(Material.RED_SANDSTONE,16,12);
		register(Material.GLASS,16,16);
		register(Material.CLAY,16,20);
		register(Material.MYCELIUM,4,12);
		register(Material.OBSIDIAN,4,40);
		register(Material.NETHERRACK,16,8);
		register(Material.GLOWSTONE,4,40);
		register(Material.SOUL_SAND,16,16);
		register(Material.NETHER_BRICKS,16,32);
		register(Material.END_STONE,16,16);
		register(Material.END_STONE_BRICKS,4,16);
		register(Material.PRISMARINE,2,40);
	}

	public static HashMap<Integer,SellItem> getItems(){
		return items;
	}

	public static SellItem getItem(int index){
		return items.get(index);
	}

	private int idx;
	private void register(Material type,int amount,int price){
		items.put(idx++,new SellItem(type,amount,price));
	}

	public static void sendMessage(Player player,String message){
		player.sendMessage(PREFIX+message);
	}
}