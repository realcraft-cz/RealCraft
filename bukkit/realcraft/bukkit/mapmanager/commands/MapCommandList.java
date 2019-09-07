package realcraft.bukkit.mapmanager.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.mapmanager.MapPlayer;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.MapDataInteger;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.DateUtil;
import realcraft.bukkit.utils.ItemUtil;
import realcraft.share.users.User;

import java.util.ArrayList;
import java.util.List;

public class MapCommandList extends MapCommand implements Listener {

	private static final String INV_NAME = "Mapy";
	private static ItemStack hotbarItem;

	public MapCommandList(){
		super("list");
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@Override
	public void perform(Player player,String[] args){
		player.openInventory(MapCommandList.getMainMenu());
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		return null;
	}

	public static ItemStack getHotbarItem(){
		if(hotbarItem == null){
			hotbarItem = new ItemStack(Material.CLOCK);
			ItemMeta meta = hotbarItem.getItemMeta();
			meta.setDisplayName("§6§lMapy");
			hotbarItem.setItemMeta(meta);
		}
		return hotbarItem;
	}

	public static Inventory getMainMenu(){
		Inventory menu = Bukkit.createInventory(null,6*9,INV_NAME);
		int idx = 0;
		for(MapType type : MapType.values()){
			int amount = MapManager.getMaps(type).size();
			if(amount < 1) amount = 1;
			ItemStack item = new ItemStack(type.getMaterial(),amount);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(type.getColor()+"§l"+type.getName());
			meta.setLore(ItemUtil.getLores("§7Klikni pro otevreni"));
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(meta);
			menu.setItem(11+idx+((idx/5)*4),item);
			idx ++;
		}
		return menu;
	}

	public static void openCategory(Player player,MapType type,int page){
		player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
		MapManager.getMapPlayer(player).setMenuType(type);
		MapManager.getMapPlayer(player).setMenuPage(page);
		Inventory menu = Bukkit.createInventory(player,6*9,INV_NAME+" > "+type.getName());
		ItemStack item;
		ItemMeta meta;

		ArrayList<Map> maps = MapManager.getSortedMaps(type);
		for(int i=0;i<5*9;i++){
			int index = i+((page-1)*(5*9));
			if(maps.size() > index){
				Map map = maps.get(index);
				if(map != null){
					item = new ItemStack(map.getType().getMaterial());
					meta = item.getItemMeta();
					meta.setDisplayName("§f§l"+map.getName()+"§r §7[#"+map.getId()+"]");
					String[] trusted = new String[map.getTrusted().size()];
					int idx = 0;
					for(MapDataInteger value : map.getTrusted().getValues()){
						User user = Users.getUser(value.getValue());
						if(user != null) trusted[idx++] = user.getRank().getChatColor()+"§l"+user.getName();
					}
					ArrayList<String> lores = new ArrayList<>();
					lores.add("§7Autor: "+map.getUser().getRank().getChatColor()+"§l"+map.getUser().getName());
					if(trusted.length > 0) lores.add("§7Trusted: "+StringUtils.join(trusted,", "));
					lores.add("§7Stav: "+map.getState().getColor()+"§l"+map.getState().getName());
					lores.add("§r");
					lores.add("§7Vytvoreno: §f"+DateUtil.lastTime(map.getCreated()));
					lores.add("§7Upraveno: §f"+DateUtil.lastTime(map.getUpdated()));
					lores.add("§r");
					lores.add("§7Klikni pro pripojeni");
					meta.setLore(lores);
					meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					item.setItemMeta(meta);
					menu.setItem(i,item);
				}
			}
		}

		int maxPage = (int)Math.ceil(maps.size()/(5*9.0));

		if(page > 1){
			item = new ItemStack(Material.PAPER);
			meta = item.getItemMeta();
			meta.setDisplayName("§7Predchozi");
			item.setItemMeta(meta);
			menu.setItem(45,item);
		}
		if(page < maxPage){
			item = new ItemStack(Material.PAPER);
			meta = item.getItemMeta();
			meta.setDisplayName("§7Dalsi");
			item.setItemMeta(meta);
			menu.setItem(53,item);
		}

		item = new ItemStack(type.getMaterial());
		meta = item.getItemMeta();
		meta.setDisplayName(type.getColor()+"§l"+type.getName());
		meta.setLore(ItemUtil.getLores("§7Klikni pro navrat zpet"));
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		menu.setItem(49,item);
		player.openInventory(menu);
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			Player player = (Player)event.getWhoClicked();
			MapPlayer mPlayer = MapManager.getMapPlayer(player);
			if(event.getView().getTitle().equalsIgnoreCase(INV_NAME)){
				event.setCancelled(true);
				if(event.getRawSlot() >= 11 && event.getRawSlot() <= 33 && event.getRawSlot()%9 >= 2 && event.getRawSlot()%9 <= 6){
					int idx = ((event.getRawSlot()/9)-1)*5+(event.getRawSlot()%9)-2;
					if(idx >= MapType.values().length) return;
					MapType type = MapType.values()[idx];
					MapCommandList.openCategory(player,type,1);
				}
			}
			else if(mPlayer.getMenuType() != null && event.getView().getTitle().equalsIgnoreCase(INV_NAME+" > "+mPlayer.getMenuType().getName())){
				event.setCancelled(true);
				if(event.getRawSlot() >= 0 && event.getRawSlot() <= 44){
					ArrayList<Map> maps = MapManager.getSortedMaps(mPlayer.getMenuType());
					if(maps.size() > event.getRawSlot()){
						player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
						mPlayer.joinMap(maps.get(event.getRawSlot()+(45*(mPlayer.getMenuPage()-1))));
					}
				}
				else if(event.getRawSlot() == 45 && event.getCurrentItem().getType() == Material.PAPER){
					MapCommandList.openCategory(player,mPlayer.getMenuType(),mPlayer.getMenuPage()-1);
				}
				else if(event.getRawSlot() == 53 && event.getCurrentItem().getType() == Material.PAPER){
					MapCommandList.openCategory(player,mPlayer.getMenuType(),mPlayer.getMenuPage()+1);
				}
				else if(event.getRawSlot() == 49){
					player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
					player.openInventory(MapCommandList.getMainMenu());
				}
			}
		}
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getInventory().getItemInMainHand().isSimilar(MapCommandList.getHotbarItem()) && event.getAction() != Action.PHYSICAL){
			event.setCancelled(true);
			player.openInventory(MapCommandList.getMainMenu());
		}
	}
}