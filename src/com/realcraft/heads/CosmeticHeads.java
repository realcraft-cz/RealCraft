package com.realcraft.heads;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.realcraft.RealCraft;
import com.realcraft.utils.ItemUtil;

public class CosmeticHeads implements Listener {

	RealCraft plugin;
	private static final String invName = "Cosmetic Heads";
	private ArrayList<CosmeticCategory> categories = new ArrayList<CosmeticCategory>();
	private HashMap<Player,Integer> playerPage = new HashMap<Player,Integer>();

	public CosmeticHeads(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		this.loadCategories();
	}

	public void onReload(){
	}

	@SuppressWarnings("unchecked")
	private void loadCategories(){
		File file = new File(RealCraft.getInstance().getDataFolder()+"/heads/categories.yml");
		if(file.exists()){
			FileConfiguration config = new YamlConfiguration();
			try {
				config.load(file);
			} catch (Exception e){
				e.printStackTrace();
			}
			List<Map<String, Object>> tempPoints = (List<Map<String, Object>>) config.get("categories");
			if(tempPoints != null && !tempPoints.isEmpty()){
				for(Map<String, Object> point : tempPoints){
					String id = point.get("id").toString();
					String name = point.get("name").toString();
					String material = point.get("material").toString();
					categories.add(new CosmeticCategory(id,name,material));
				}
			}
		}
	}

	@EventHandler
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		String command = event.getMessage().substring(1).toLowerCase();
		if(command.startsWith("heads") || command.startsWith("hlavy")){
			this.openMenu(event.getPlayer());
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			Player player = (Player) event.getWhoClicked();
			if(event.getInventory().getName().equalsIgnoreCase(invName)){
				event.setCancelled(true);
				if(event.getRawSlot() >= 0 && event.getRawSlot() < 4*9){
					ItemStack item = event.getCurrentItem();
					if(item.getType() != Material.AIR && item.hasItemMeta()){
						for(CosmeticCategory category : categories){
							if(item.getItemMeta().getDisplayName().equalsIgnoreCase(category.getItemStack().getItemMeta().getDisplayName())){
								category.open(player);
								player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
								break;
							}
						}
					}
				}
			}
			else if(event.getInventory().getName().startsWith(invName)){
				for(CosmeticCategory category : categories){
					if(event.getInventory().getName().equalsIgnoreCase(category.getInvName())){
						event.setCancelled(true);
						if(event.getRawSlot() >= 0 && event.getRawSlot() < 6*9){
							ItemStack item = event.getCurrentItem();
							if(item.getType() != Material.AIR && item.hasItemMeta()){
								if(item.getType() == Material.PAPER){
									if(event.getRawSlot() == 45) category.open(player,playerPage.get(player)-1);
									else if(event.getRawSlot() == 53) category.open(player,playerPage.get(player)+1);
									player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
								} else {
									if(event.getRawSlot() == 49){
										this.openMenu(player);
										player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
									} else {
										player.getInventory().addItem(item);
										player.playSound(player.getLocation(),Sound.ENTITY_ITEM_PICKUP,1,1);
									}
								}
							}
						}
						break;
					}
				}
			}
		}
	}

	private void openMenu(Player player){
		Inventory inventory = Bukkit.createInventory(null,4*9,invName);
		int index = 10;
		for(CosmeticCategory category : categories){
			inventory.setItem(index++,category.getItemStack());
			if(index == 17) index = 19;
		}
		player.openInventory(inventory);
	}

	private class CosmeticCategory {

		private String id;
		private String name;
		private String material;
		private ItemStack item = null;
		private ArrayList<CosmeticHead> heads = new ArrayList<CosmeticHead>();

		public CosmeticCategory(String id,String name,String material){
			this.id = id;
			this.name = name;
			this.material = material;
			this.loadHeads();
		}

		public String getId(){
			return id;
		}

		public String getName(){
			return name;
		}

		public String getInvName(){
			return invName+" > "+this.getName();
		}

		public ItemStack getItemStack(){
			if(item == null){
				item = new ItemStack(Material.getMaterial(material));
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§e§l"+this.getName());
				ArrayList<String> lores = new ArrayList<String>();
				lores.add("§7Klikni pro otevreni");
				meta.setLore(lores);
				item.setItemMeta(meta);
			}
			return item;
		}

		public ArrayList<CosmeticHead> getHeads(){
			return heads;
		}

		public void open(Player player){
			this.open(player,1);
		}

		public void open(Player player,int page){
			playerPage.put(player,page);
			Inventory inventory = Bukkit.createInventory(null,6*9,this.getInvName());
			ItemStack item;
			ItemMeta meta;
			ArrayList<CosmeticHead> heads = this.getHeads();
			for(int i=0;i<5*9;i++){
				int index = i+((page-1)*(5*9));
				if(heads.size() > index){
					CosmeticHead head = heads.get(index);
					if(head != null){
						inventory.setItem(i,head.getItemStack());
					}
				}
			}

			item = new ItemStack(Material.SKULL_ITEM);
			meta = item.getItemMeta();
			meta.setDisplayName("§e§lCosmetic Heads");
			ArrayList<String> lores = new ArrayList<String>();
			lores.add("§7Klikni pro navrat");
			meta.setLore(lores);
			item.setItemMeta(meta);
			inventory.setItem(49,item);

			int maxPage = (int)Math.ceil(heads.size()/(5*9.0));
			if(page > 1){
				item = new ItemStack(Material.PAPER);
				meta = item.getItemMeta();
				meta.setDisplayName("§6§lPredchozi");
				item.setItemMeta(meta);
				inventory.setItem(45,item);
			}
			if(page < maxPage){
				item = new ItemStack(Material.PAPER);
				meta = item.getItemMeta();
				meta.setDisplayName("§6§lDalsi");
				item.setItemMeta(meta);
				inventory.setItem(53,item);
			}

			player.openInventory(inventory);
		}

		@SuppressWarnings("unchecked")
		private void loadHeads(){
			File parkourFile = new File(RealCraft.getInstance().getDataFolder()+"/heads/category_"+this.getId()+".yml");
			if(parkourFile.exists()){
				FileConfiguration config = new YamlConfiguration();
				try {
					config.load(parkourFile);
				} catch (Exception e){
					e.printStackTrace();
				}
				List<Map<String, Object>> tempPoints = (List<Map<String, Object>>) config.get("heads");
				if(tempPoints != null && !tempPoints.isEmpty()){
					for(Map<String, Object> point : tempPoints){
						String id = point.get("id").toString();
						String name = point.get("name").toString();
						String value = point.get("value").toString();
						heads.add(new CosmeticHead(id,name,value));
					}
				}
			}
		}
	}

	private class CosmeticHead {

		private String id;
		private String name;
		private String value;
		private ItemStack item = null;

		public CosmeticHead(String id,String name,String value){
			this.id = id;
			this.name = name;
			this.value = value;
		}

		public String getId(){
			return id;
		}

		public String getName(){
			return name;
		}

		public String getValue(){
			return value;
		}

		public ItemStack getItemStack(){
			if(item == null) item = ItemUtil.getHead("§r"+this.getName(),this.getValue());
			return item;
		}
	}
}