package realcraft.bukkit.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.auth.AuthLoginEvent;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.Glow;

public class LobbyMenu implements Listener,PluginMessageListener,Runnable {
	RealCraft plugin;

	static String invName = "Vybrat hru";

	Inventory menu;
	HashMap<Integer,MenuItem> menuItems = new HashMap<Integer,MenuItem>();

	public ArrayList<String> servers = new ArrayList<String>();
	static HashMap<String,Integer> serverPlayers = new HashMap<String,Integer>();

	public LobbyMenu(RealCraft realcraft){
		plugin = realcraft;
		if(!plugin.serverName.equalsIgnoreCase("survival") && !plugin.serverName.equalsIgnoreCase("creative") && !plugin.serverName.equalsIgnoreCase("parkour")) plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin,"BungeeCord");
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin,"BungeeCord",this);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,2*20,2*20);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,new Runnable(){
			@Override
			public void run(){
				getServers();
			}
		},20,10*20);
		this.createMenu();
	}

	public void onReload(){
	}

	@Override
	public void run(){
		this.updatePlayersCount(null);
		this.updateMenu();
	}

	public static ItemStack getItem(){
		ItemStack item = new ItemStack(Material.COMPASS,1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§a§l"+invName);
		item.setItemMeta(meta);
		return item;
	}

	@EventHandler
	public void AuthLoginEvent(AuthLoginEvent event){
		Player player = event.getPlayer();
		player.getInventory().setItem(0,LobbyMenu.getItem());
		player.getInventory().setHeldItemSlot(0);
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(Users.getUser(player).isLogged() && player.getWorld().getName().equalsIgnoreCase("world")){
			player.getInventory().setItem(0,LobbyMenu.getItem());
			player.getInventory().setHeldItemSlot(0);
		}
	}

	@EventHandler
	public void PlayerChangedWorldEvent(final PlayerChangedWorldEvent event){
		if(event.getFrom().getName().equalsIgnoreCase("world")){
			event.getPlayer().getInventory().remove(LobbyMenu.getItem());
		}
		else if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
			event.getPlayer().getInventory().setHeldItemSlot(8);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
				@Override
				public void run(){
					event.getPlayer().getInventory().setHeldItemSlot(0);
					event.getPlayer().getInventory().setItem(0,LobbyMenu.getItem());
				}
			},20);
		}
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getWorld().getName().equalsIgnoreCase("world") && player.getInventory().getItemInMainHand().getType() == Material.COMPASS && (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
			event.setCancelled(true);
			if(Users.getUser(player).isLogged()){
				this.openMenu(player);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world") && event.getItemDrop().getItemStack().getType() == Material.COMPASS){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getInventory().getName().equalsIgnoreCase(invName)){
			if(event.getWhoClicked() instanceof Player && ((Player)event.getWhoClicked()).getWorld().getName().equalsIgnoreCase("world")){
				event.setCancelled(true);
				Player player = (Player) event.getWhoClicked();
				if(menuItems.containsKey(event.getRawSlot())){
					this.closeMenu(player);
					menuItems.get(event.getRawSlot()).onPlayerClick(player);
				}
			}
		}
		else if(event.getWhoClicked() instanceof Player && ((Player)event.getWhoClicked()).getWorld().getName().equalsIgnoreCase("world") && event.getSlotType() == SlotType.QUICKBAR && event.getCurrentItem().getType() == Material.COMPASS){
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			if(Users.getUser(player).isLogged()){
				this.openMenu(player);
			}
		}
	}

	private void openMenu(Player player){
		player.openInventory(menu);
	}

	private void closeMenu(Player player){
		player.closeInventory();
	}

	private void createMenu(){
		menu = Bukkit.createInventory(null,5*9,invName);
		Set<String> positions = plugin.config.getKeys("lobby.menu.slots");
		for(String idx : positions){
			String [] pos = idx.split(":");
			int index = (Integer.valueOf(pos[0])*9)+Integer.valueOf(pos[1]);
			new MenuItem(
				plugin.config.getString("lobby.menu.slots."+idx+".server"),
				plugin.config.getString("lobby.menu.slots."+idx+".name"),
				index,
				plugin.config.getInt("lobby.menu.slots."+idx+".max-players"),
				Material.getMaterial(plugin.config.getString("lobby.menu.slots."+idx+".material")),
				plugin.config.getInt("lobby.menu.slots."+idx+".data"),
				plugin.config.getStringList("lobby.menu.slots."+idx+".info")
			);
		}
		this.updateMenu();
	}

	private void updateMenu(){
		for(MenuItem menuitem: menuItems.values()){
			this.updatePlayersCount(menuitem.getServer());
			menuitem.update();
			menu.setItem(menuitem.getIndex(),menuitem.getItem());
		}
	}

	private class MenuItem {
		String name;
		String server;
		List<String> info;
		ItemStack itemstack;
		int players = 0;
		int maxplayers = 0;
		int index = 0;

		@SuppressWarnings("deprecation")
		public MenuItem(String server,String name,int index,int maxplayers,Material material,int data,List<String> info){
			this.name = ChatColor.translateAlternateColorCodes('&',name);
			this.server = server;
			this.index = index;
			this.maxplayers = maxplayers;
			this.info = info;
			this.itemstack = new ItemStack(material,1,(short)0,(byte)data);
			menuItems.put(this.index,this);
		}

		public int getIndex(){
			return this.index;
		}

		public String getServer(){
			return this.server;
		}

		public void update(){
			this.players = (serverPlayers.containsKey(server) ? serverPlayers.get(server) : 0);
			this.updateItem();
		}

		private void updateItem(){
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("§fHraci: §e"+this.players+"/"+maxplayers);
			if(!info.isEmpty()){
				lore.add("§7");
				for(String info : info){
					lore.add(ChatColor.translateAlternateColorCodes('&',info));
				}
			}
			itemstack.setAmount((this.players == 0 ? 1 : this.players));
			ItemMeta meta = itemstack.getItemMeta();
			meta.setDisplayName(this.name);
			meta.setLore(lore);
			if(this.players > 0) meta.addEnchant(new Glow(255),1,true);
			else meta.removeEnchant(new Glow(255));
			itemstack.setItemMeta(meta);
		}

		public ItemStack getItem(){
			this.updateItem();
			return itemstack;
		}

		public void onPlayerClick(Player player){
			player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
			if(servers.contains(server)){
				if(plugin.serverName.equalsIgnoreCase("lobby") && server.equalsIgnoreCase("lobby")){
					player.teleport(plugin.auth.getServerSpawn());
				}
				else connectPlayerToServer(player,server);
			}
		}
	}

	@Override
	public void onPluginMessageReceived(String channel,Player player,byte[] message){
		if(!channel.equals("BungeeCord")) return;
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if(subchannel.equals("PlayerCount")){
			String server = in.readUTF();
			int players = in.readInt();
			serverPlayers.put(server,players);
		}
		else if(subchannel.equals("GetServers")){
			servers = new ArrayList<String>();
			String [] serverList = in.readUTF().split(", ");
			for(String server : serverList){
				servers.add(server);
			}
		}
	}

	public static int getAllPlayersCount(){
		return (serverPlayers.containsKey("ALL") ? serverPlayers.get("ALL") : 0);
	}

	public int getPlayersCount(String server){
		if(serverPlayers.containsKey(server)) return serverPlayers.get(server);
		return 0;
	}

	private void updatePlayersCount(String server){
		if(server == null){
			server = "ALL";
		}
		else if(!servers.contains(server)){
			return;
		}
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PlayerCount");
		out.writeUTF(server);
		Bukkit.getServer().sendPluginMessage(plugin,"BungeeCord",out.toByteArray());
	}

	private void getServers(){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GetServers");
		Bukkit.getServer().sendPluginMessage(plugin,"BungeeCord",out.toByteArray());
	}

	public static void connectPlayerToServer(Player player,String server){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);
		player.sendPluginMessage(RealCraft.getInstance(),"BungeeCord",out.toByteArray());
	}
}