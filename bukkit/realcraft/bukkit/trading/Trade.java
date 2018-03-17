package realcraft.bukkit.trading;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import realcraft.bukkit.RealCraft;

public class Trade implements Listener, Runnable {
	Trading trading;

	private Player player1;
	private Player player2;
	private Inventory inventory1;
	private Inventory inventory2;
	private boolean ready1 = false;
	private boolean ready2 = false;

	private ArrayList<TradeItem> items1 = new ArrayList<TradeItem>();
	private ArrayList<TradeItem> items2 = new ArrayList<TradeItem>();

	private long created = 0;
	private boolean accepted = false;
	private boolean closed = false;
	private boolean processing = false;
	private int countdown = 0;
	private int taskId = -1;

	public Trade(Trading trading,Player player1,Player player2){
		this.trading = trading;
		this.player1 = player1;
		this.player2 = player2;
		this.created = System.currentTimeMillis();
		if(!this.init()) this.close(TradeCloseReason.NONE);
		else {
			trading.plugin.getServer().getPluginManager().registerEvents(this,trading.plugin);
			taskId = trading.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(trading.plugin,this,10,10);
		}
	}

	private boolean init(){
		if(player2 == null){
			player1.sendMessage("§cHrac nenalezen.");
			return false;
		}
		else if(player1 == player2){
			player1.sendMessage("§cNemuzes obchodovat sam se sebou.");
			return false;
		}
		else if(Trading.isPlayerTrading(player1)){
			player1.sendMessage("§cZadost o obchodovani byla jiz odeslana.");
			return false;
		}
		else if(!Trading.isPlayerAbleToTradeRequest(player1)){
			player1.sendMessage("§cDalsi zadost muzes odeslat za §e"+(((Trading.getLastTradeRequest(player1)+(Trading.REQUEST_WAIT_SECONDS*1000))-System.currentTimeMillis())/1000)+" §csekund.");
			return false;
		}
		else if(Trading.isPlayerIgnorePlayer(player2,player1)){
			player1.sendMessage("§cHrac te v obchodovani ignoruje.");
			return false;
		}
		else if(Trading.isPlayerTrading(player2)){
			player1.sendMessage("§cHrac prave obchoduje, vyckej prosim.");
			return false;
		}
		else if(!player1.getWorld().getName().equalsIgnoreCase(player2.getWorld().getName())){
			player1.sendMessage("§cHrac neni ve stejnem svete.");
			return false;
		}
		Trading.addPlayer(player1,this);
		Trading.addPlayer(player2,this);
		Trading.setLastTradeRequest(player1);
		player1.sendMessage("§6Zadost o obchodovani odeslana hraci "+player2.getDisplayName()+"§6.");
		player2.sendMessage("");
		player2.sendMessage("§6Zadost o obchodovani od hrace "+player1.getDisplayName()+"§6.");
		TextComponent message = new TextComponent("§6Muzete ji ");
		TextComponent accept = new TextComponent("§7[§a§lPRIJMOUT§7]");
		TextComponent deny = new TextComponent("§7[§c§lODMITNOUT§7]");
		accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/trade accept"));
		deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/trade deny"));
		accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Klikni pro prijmuti zadosti").create()));
		deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Klikni pro odmitnuti zadosti").create()));
		message.addExtra(accept);
		message.addExtra("§6 nebo ");
		message.addExtra(deny);
		message.addExtra("§6 do 15 sekund.");
		player2.spigot().sendMessage(message);
		player2.playSound(player2.getLocation(),Sound.BLOCK_NOTE_PLING,1,1);
		return true;
	}

	@Override
	public void run(){
		if(!accepted && created+(Trading.REQUEST_TIMEOUT_SECONDS*1000) < System.currentTimeMillis()){
			this.close(TradeCloseReason.TIMEOUTED);
		}
		else if(processing){
			if(countdown < 5){
				countdown ++;
				player1.playSound(player1.getLocation(),Sound.BLOCK_NOTE_HAT,1,1);
				player2.playSound(player2.getLocation(),Sound.BLOCK_NOTE_HAT,1,1);
				this.updateInventories();
			} else {
				this.close(TradeCloseReason.FINISHED);
			}
		}
	}

	public void accept(Player player){
		if(player.getName().equals(player1.getName())){
			player1.sendMessage("§cNemas zadnou zadost o obchodovani.");
			return;
		}
		this.open();
	}

	public void deny(Player player){
		if(player.getName().equals(player1.getName())){
			player1.sendMessage("§cNemas zadnou zadost o obchodovani.");
			return;
		}
		this.close(TradeCloseReason.DENIED);
	}

	private void open(){
		this.accepted = true;
		inventory1 = Bukkit.createInventory(player1,5*9,TradingUtils.getInventoryName(player1,player2));
		inventory2 = Bukkit.createInventory(player2,5*9,TradingUtils.getInventoryName(player2,player1));
		player1.openInventory(inventory1);
		player2.openInventory(inventory2);
		player1.playSound(player1.getLocation(),Sound.BLOCK_CHEST_OPEN,0.5f,1);
		player2.playSound(player2.getLocation(),Sound.BLOCK_CHEST_OPEN,0.5f,1);
		this.updateInventories();
	}

	private void updateInventories(){
		this.updateInventory(inventory1);
		this.updateInventory(inventory2);
	}

	@SuppressWarnings("deprecation")
	private void updateInventory(Inventory inventory){
		inventory.clear();
		ItemStack glassLoaded = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)0,(byte)5);
		ItemStack glassUnLoaded = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)0,(byte)15);
		ItemStack glassReady = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)0,(byte)5);
		ItemStack glassNotReady = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)0,(byte)14);
		ItemMeta meta;
		for(int i=0;i<5;i++){
			if(countdown > i) inventory.setItem((i*9)+4,glassLoaded);
			else inventory.setItem((i*9)+4,glassUnLoaded);
		}
		if(inventory.getHolder().equals(player1)){
			for(TradeItem item : items1) inventory.setItem(item.getIndex(),item.getItemStack());
			for(TradeItem item : items2) inventory.setItem(item.getIndex()+5,item.getItemStack());
			meta = glassNotReady.getItemMeta();
			meta.setDisplayName("§aKlikni pro potvrzeni");
			glassNotReady.setItemMeta(meta);
			meta = glassReady.getItemMeta();
			meta.setDisplayName("§cKlikni pro zruseni");
			glassReady.setItemMeta(meta);
			for(int i=0;i<4;i++){
				if(ready1) inventory.setItem((4*9)+i,glassReady);
				else inventory.setItem((4*9)+i,glassNotReady);
			}
			meta = glassNotReady.getItemMeta();
			meta.setDisplayName("§c"+player2.getName()+" §cneni pripraven");
			glassNotReady.setItemMeta(meta);
			meta = glassReady.getItemMeta();
			meta.setDisplayName("§a"+player2.getName()+" §aje pripraven");
			glassReady.setItemMeta(meta);
			for(int i=0;i<4;i++){
				if(ready2) inventory.setItem((4*9)+i+5,glassReady);
				else inventory.setItem((4*9)+i+5,glassNotReady);
			}
		}
		else if(inventory.getHolder().equals(player2)){
			for(TradeItem item : items2) inventory.setItem(item.getIndex(),item.getItemStack());
			for(TradeItem item : items1) inventory.setItem(item.getIndex()+5,item.getItemStack());
			meta = glassNotReady.getItemMeta();
			meta.setDisplayName("§aKlikni pro potvrzeni");
			glassNotReady.setItemMeta(meta);
			meta = glassReady.getItemMeta();
			meta.setDisplayName("§cKlikni pro zruseni");
			glassReady.setItemMeta(meta);
			for(int i=0;i<4;i++){
				if(ready2) inventory.setItem((4*9)+i,glassReady);
				else inventory.setItem((4*9)+i,glassNotReady);
			}
			meta = glassNotReady.getItemMeta();
			meta.setDisplayName("§c"+player1.getName()+" §cneni pripraven");
			glassNotReady.setItemMeta(meta);
			meta = glassReady.getItemMeta();
			meta.setDisplayName("§a"+player1.getName()+" §aje pripraven");
			glassReady.setItemMeta(meta);
			for(int i=0;i<4;i++){
				if(ready1) inventory.setItem((4*9)+i+5,glassReady);
				else inventory.setItem((4*9)+i+5,glassNotReady);
			}
		}
	}

	private void updateItems(){
		Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				items1.clear();
				items2.clear();
				for(int i=0;i<inventory1.getSize();i++){
					if(TradingUtils.isOwnerSlot(i) && inventory1.getItem(i) != null) items1.add(new TradeItem(inventory1.getItem(i),i));
				}
				for(int i=0;i<inventory2.getSize();i++){
					if(TradingUtils.isOwnerSlot(i) && inventory2.getItem(i) != null) items2.add(new TradeItem(inventory2.getItem(i),i));
				}
				updateInventories();
			}
		});
	}

	private void purchase(){
		HashMap<Integer,ItemStack> leftOver1 = new HashMap<Integer,ItemStack>();
		HashMap<Integer,ItemStack> leftOver2 = new HashMap<Integer,ItemStack>();
		for(TradeItem item : items1){
			leftOver1.putAll(player2.getInventory().addItem(item.getItemStack()));
			if(!leftOver1.isEmpty()) for(ItemStack itemStack : leftOver1.values()) player2.getWorld().dropItemNaturally(player2.getLocation(),itemStack);
		}
		for(TradeItem item : items2){
			leftOver2.putAll(player1.getInventory().addItem(item.getItemStack()));
			if(!leftOver2.isEmpty()) for(ItemStack itemStack : leftOver2.values()) player1.getWorld().dropItemNaturally(player1.getLocation(),itemStack);
		}
	}

	private void restore(){
		HashMap<Integer,ItemStack> leftOver1 = new HashMap<Integer,ItemStack>();
		HashMap<Integer,ItemStack> leftOver2 = new HashMap<Integer,ItemStack>();
		for(TradeItem item : items1){
			leftOver1.putAll(player1.getInventory().addItem(item.getItemStack()));
			if(!leftOver1.isEmpty()) for(ItemStack itemStack : leftOver1.values()) player1.getWorld().dropItemNaturally(player1.getLocation(),itemStack);
		}
		for(TradeItem item : items2){
			leftOver2.putAll(player2.getInventory().addItem(item.getItemStack()));
			if(!leftOver2.isEmpty()) for(ItemStack itemStack : leftOver2.values()) player2.getWorld().dropItemNaturally(player2.getLocation(),itemStack);
		}
	}

	private void close(TradeCloseReason reason){
		if(!closed){
			closed = true;
			this.cancel();
			if(reason != TradeCloseReason.NONE){
				if(player1.getOpenInventory().getTopInventory().equals(inventory1)) player1.closeInventory();
				if(player2.getOpenInventory().getTopInventory().equals(inventory2)) player2.closeInventory();
			}
			if(reason == TradeCloseReason.DENIED){
				player1.sendMessage("§6Zadost o obchodovani byla odmitnuta.");
				player2.sendMessage("§6Odmitnul jsi zadost o obchodovani.");
			}
			else if(reason == TradeCloseReason.TIMEOUTED){
				player1.sendMessage("§6Zadost o obchodovani vyprsela.");
			}
			else if(reason == TradeCloseReason.EXITED){
				this.restore();
				player1.sendMessage("§6Obchodovani bylo zruseno.");
				player2.sendMessage("§6Obchodovani bylo zruseno.");
				player1.playSound(player1.getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
				player2.playSound(player1.getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
			}
			else if(reason == TradeCloseReason.FINISHED){
				this.purchase();
				player1.sendMessage("§aObchod byl uspesne dokoncen.");
				player2.sendMessage("§aObchod byl uspesne dokoncen.");
				player1.playSound(player1.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
				player2.playSound(player1.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
			}
			if(reason != TradeCloseReason.NONE){
				Trading.removePlayer(player1);
				Trading.removePlayer(player2);
			}
			trading.plugin.getServer().getScheduler().cancelTask(taskId);
		}
	}

	private void start(){
		if(!processing){
			processing = true;
			countdown = 0;
		}
	}

	private void cancel(){
		if(processing){
			processing = false;
			countdown = 0;
			ready1 = false;
			ready2 = false;
		}
	}

	private void setReady(Player player){
		if(player.equals(player1)) ready1 = true;
		if(player.equals(player2)) ready2 = true;
		if(ready1 && ready2) this.start();
	}

	private void notReady(Player player){
		if(player.equals(player1)) ready1 = false;
		if(player.equals(player2)) ready2 = false;
		if(!ready1 || !ready2) this.cancel();
	}

	private boolean isReady(Player player){
		if(player.equals(player1)) return ready1;
		if(player.equals(player2)) return ready2;
		return false;
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getInventory().equals(inventory1) || event.getInventory().equals(inventory2)){
			Player player = (Player)event.getWhoClicked();
			if(TradingUtils.isStarterSlot(event.getRawSlot())){
				if(!this.isReady(player)) this.setReady(player);
				else this.notReady(player);
			}
			else this.notReady(player);
			if(!TradingUtils.isOwnerSlot(event.getRawSlot()) && event.getClickedInventory() != null && event.getClickedInventory().getType() != InventoryType.PLAYER) event.setCancelled(true);
			if(event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.PLAYER && event.isShiftClick()){
				boolean added = false;
				ItemStack item = event.getCurrentItem();
				if(event.getInventory().equals(inventory1)){
					for(int i=0;i<inventory1.getSize();i++){
						if(TradingUtils.isOwnerSlot(i) && inventory1.getItem(i) == null){
							inventory1.setItem(i,item);
							event.setCancelled(true);
							event.setCurrentItem(null);
							added = true;
							break;
						}
					}
				}
				else if(event.getInventory().equals(inventory2)){
					for(int i=0;i<inventory2.getSize();i++){
						if(TradingUtils.isOwnerSlot(i) && inventory2.getItem(i) == null){
							inventory2.setItem(i,item);
							event.setCancelled(true);
							event.setCurrentItem(null);
							added = true;
							break;
						}
					}
				}
				if(!added) event.setCancelled(true);
			}
			this.updateItems();
		}
	}

	@EventHandler
	public void InventoryDragEvent(InventoryDragEvent event){
		if(event.getInventory().equals(inventory1) || event.getInventory().equals(inventory2)){
			if(event.getInventory().getType() != InventoryType.PLAYER){
				for(Integer index : event.getRawSlots()){
					if(!TradingUtils.isOwnerSlot(index) && !TradingUtils.isPlayerSlot(index)){
						event.setCancelled(true);
					}
				}
			}
			this.updateItems();
		}
	}

	@EventHandler
	public void InventoryCloseEvent(InventoryCloseEvent event){
		if(event.getInventory().equals(inventory1) || event.getInventory().equals(inventory2)) this.close(TradeCloseReason.EXITED);
	}

	@EventHandler
	public void PlayerDeathEvent(PlayerDeathEvent event){
		if(event.getEntity().equals(player1) || event.getEntity().equals(player2)) this.close(TradeCloseReason.EXITED);
	}

	@EventHandler
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		if(event.getPlayer().equals(player1) || event.getPlayer().equals(player2)) this.close(TradeCloseReason.EXITED);
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		if(event.getPlayer().equals(player1) || event.getPlayer().equals(player2)) this.close(TradeCloseReason.EXITED);
	}
}