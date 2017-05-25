package com.realcraft.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.realcraft.RealCraft;
import com.realcraft.auth.AuthLoginEvent;
import com.realcraft.playermanazer.PlayerManazer;
import com.realcraft.utils.Glow;
import com.realcraft.utils.ItemUtil;
import com.realcraft.utils.StringUtil;
import com.realcraft.utils.Title;

public class LobbyLottery implements Listener {

	RealCraft plugin;
	public static final String LOTTERIES = "lotteries";
	private static final int REPEAT_LIMIT = 10;
	private static final String invName = "Loterie";
	private static final String arrowUp = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ4Yjc2OGM2MjM0MzJkZmIyNTlmYjNjMzk3OGU5OGRlYzExMWY3OWRiZDZjZDg4ZjIxMTU1Mzc0YjcwYjNjIn19fQ==";
	private static final String arrowDown = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRhZGQ3NTVkMDg1MzczNTJiZjdhOTNlM2JiN2RkNGQ3MzMxMjFkMzlmMmZiNjcwNzNjZDQ3MWY1NjExOTRkZCJ9fX0=";
	private static final double[] multipliers = new double[]{1.5,3,10,50,200};
	private HashMap<Player,LobbyLotteryInventory> lotteries = new HashMap<Player,LobbyLotteryInventory>();
	private Random random = new Random();

	public LobbyLottery(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	public LobbyLotteryInventory getLottery(Player player){
		if(!lotteries.containsKey(player)) lotteries.put(player,new LobbyLotteryInventory(player));
		return lotteries.get(player);
	}

	@EventHandler
	public void AuthLoginEvent(AuthLoginEvent event){
		Player player = event.getPlayer();
		ItemStack item = new ItemStack(Material.ENCHANTMENT_TABLE,1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§d§l"+invName);
		item.setItemMeta(meta);
		player.getInventory().setItem(8,item);
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(PlayerManazer.getPlayerInfo(player).isLogged() && player.getWorld().getName().equalsIgnoreCase("world")){
			ItemStack item = new ItemStack(Material.ENCHANTMENT_TABLE,1);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§d§l"+invName);
			item.setItemMeta(meta);
			player.getInventory().setItem(8,item);
		}
	}

	@EventHandler
	public void PlayerChangedWorldEvent(final PlayerChangedWorldEvent event){
		final ItemStack item = new ItemStack(Material.ENCHANTMENT_TABLE,1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§d§l"+invName);
		item.setItemMeta(meta);
		if(event.getFrom().getName().equalsIgnoreCase("world")){
			event.getPlayer().getInventory().remove(item);
		}
		else if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
				@Override
				public void run(){
					event.getPlayer().getInventory().setItem(8,item);
				}
			},20);
		}
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getWorld().getName().equalsIgnoreCase("world") && player.getInventory().getItemInMainHand().getType() == Material.ENCHANTMENT_TABLE && (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
			event.setCancelled(true);
			if(PlayerManazer.getPlayerInfo(player).isLogged()){
				this.openMenu(player);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world") && event.getItemDrop().getItemStack().getType() == Material.ENCHANTMENT_TABLE){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getInventory().getName().equalsIgnoreCase(invName)){
			if(event.getWhoClicked() instanceof Player && ((Player)event.getWhoClicked()).getWorld().getName().equalsIgnoreCase("world")){
				event.setCancelled(true);
				Player player = (Player) event.getWhoClicked();
				if(!this.getLottery(player).isRunning()){
					if(event.getRawSlot() >= 0 && event.getRawSlot() < 5*9) this.getLottery(player).selectNumber(event.getRawSlot());
					else if(event.getRawSlot() == 45) this.getLottery(player).addCoins(-1,event.isShiftClick());
					else if(event.getRawSlot() == 47) this.getLottery(player).addCoins(1,event.isShiftClick());
					else if(event.getRawSlot() == 52) this.getLottery(player).confirm();
					else if(event.getRawSlot() == 53) this.getLottery(player).close();
				}
			}
		}
		else if(event.getWhoClicked() instanceof Player && ((Player)event.getWhoClicked()).getWorld().getName().equalsIgnoreCase("world") && event.getSlotType() == SlotType.QUICKBAR && event.getCurrentItem().getType() == Material.ENCHANTMENT_TABLE){
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			if(PlayerManazer.getPlayerInfo(player).isLogged()){
				this.openMenu(player);
			}
		}
	}

	@EventHandler
	public void InventoryCloseEvent(InventoryCloseEvent event){
		if(event.getInventory().getName().equalsIgnoreCase(invName)){
			if(event.getPlayer() instanceof Player && ((Player)event.getPlayer()).getWorld().getName().equalsIgnoreCase("world")){
				Player player = (Player) event.getPlayer();
				if(!this.getLottery(player).isRunning()){
					this.getLottery(player).reset();
				}
			}
		}
	}

	private void openMenu(Player player){
		this.getLottery(player).open();
	}

	private class LobbyLotteryInventory implements Runnable {
		private Player player;
		private boolean[] numbers = new boolean[5*9];
		private Inventory inventory;
		private int coins = 100;

		private boolean running = false;
		private int task = -1;
		private int currentRun = 0;
		private boolean[] randoms = new boolean[numbers.length];

		public LobbyLotteryInventory(Player player) {
			this.player = player;
			inventory = Bukkit.createInventory(null,6*9,invName);
		}

		@Override
		public void run(){
			if(!player.isOnline()){
				this.reset();
				Bukkit.getScheduler().cancelTask(task);
				return;
			}
			if(player.getOpenInventory() != inventory) player.openInventory(inventory);
			this.update();
			currentRun ++;
			if(currentRun == numbers.length+1){
				Bukkit.getScheduler().cancelTask(task);
				this.finish();
			}
			else player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,0.5f,1f);
		}

		public void open(){
			if(PlayerManazer.getPlayerInfo(player).getLastLotteryTime()+REPEAT_LIMIT > System.currentTimeMillis()/1000){
				player.sendMessage("§d[Loterie] §cDalsi losovani muzes provest za "+(((PlayerManazer.getPlayerInfo(player).getLastLotteryTime()+REPEAT_LIMIT)-(System.currentTimeMillis()/1000))/60)+" minut");
				player.getPlayer().playSound(player.getPlayer().getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
				return;
			}
			inventory.clear();
			player.openInventory(inventory);
			this.update();
		}

		@SuppressWarnings("deprecation")
		public void update(){
			inventory.clear();
			ItemStack item;
			ItemMeta meta;
			ArrayList<String> lore;

			int winnumberindex = 0;
			for(int i=0;i<numbers.length;i++){
				if(this.isRunning()) item = new ItemStack(Material.STAINED_GLASS_PANE,i+1,(short)0,(randoms[i] ? (currentRun >= i ? (byte)5 : (byte)7) : (currentRun == i ? (byte)0 : (byte)7)));
				else item = new ItemStack(Material.STAINED_GLASS_PANE,i+1,(short)0,(byte)7);
				meta = item.getItemMeta();
				lore = new ArrayList<String>();
				if(numbers[i]) meta.addEnchant(new Glow(255),1,true);
				meta.setDisplayName("§f§lCislo "+(i+1));
				lore.add("§7Klikni pro oznaceni pole");
				meta.setLore(lore);
				item.setItemMeta(meta);
				inventory.setItem(i,item);
				if(currentRun == i && randoms[i] && numbers[i]) player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
				if(currentRun >= i && randoms[i] && numbers[i]){
					item = new ItemStack(Material.EMERALD);
					meta = item.getItemMeta();
					meta.setDisplayName("§f§lCislo "+(i+1));
					item.setItemMeta(meta);
					inventory.setItem(45+(winnumberindex++),item);
				}
			}

			if(!this.isRunning()){
				item = new ItemStack(Material.EMERALD_BLOCK);
				meta = item.getItemMeta();
				meta.setDisplayName("§a§lLosovat");
				lore = new ArrayList<String>();
				lore.add("§7Klikni pro zakoupeni");
				lore.add("§7a spusteni losovani.");
				meta.setLore(lore);
				item.setItemMeta(meta);
				inventory.setItem(52,item);

				item = new ItemStack(Material.BARRIER);
				meta = item.getItemMeta();
				meta.setDisplayName("§c§lZrusit");
				item.setItemMeta(meta);
				inventory.setItem(53,item);

				inventory.setItem(45,ItemUtil.getHead("§c[-] Snizit sazku",arrowDown));
				inventory.setItem(47,ItemUtil.getHead("§a[+] Zvysit sazku",arrowUp));

				item = new ItemStack(Material.EMERALD,coins/100);
				meta = item.getItemMeta();
				meta.setDisplayName("§f§lSazka: §a"+coins+" coins");
				lore = new ArrayList<String>();
				lore.add("§r");
				lore.add("§fUhadnuta cisla a vyhry:");
				int selected = this.getNumbers().size();
				for(int i=multipliers.length-1;i>=0;i--){
					lore.add((selected >= (i+1) ? "§f" : "§7")+(i+1)+" "+StringUtil.inflect(i+1,new String[]{"cislo","cisla","cisel"})+" - "+(selected >= (i+1) ? "§a" : "§7")+(int)(coins*multipliers[i])+" coins");
				}
				meta.setLore(lore);
				item.setItemMeta(meta);
				inventory.setItem(46,item);
			}
		}

		public void reset(){
			coins = 100;
			running = false;
			currentRun = 0;
			numbers = new boolean[numbers.length];
			randoms = new boolean[numbers.length];
		}

		public void close(){
			this.reset();
			player.closeInventory();
		}

		public boolean isRunning(){
			return running;
		}

		public ArrayList<Integer> getNumbers(){
			ArrayList<Integer> selected = new ArrayList<Integer>();
			for(int i=0;i<numbers.length;i++){
				if(numbers[i]) selected.add(i);
			}
			return selected;
		}

		public void selectNumber(int index){
			if(!numbers[index] && this.getNumbers().size() == multipliers.length){
				player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
				return;
			}
			numbers[index] = !numbers[index];
			player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
			this.update();
		}

		public void addCoins(int coins,boolean shiftclick){
			this.coins += coins*(shiftclick ? 1000 : 100);
			if(this.coins > 5000) this.coins = 5000;
			else if(this.coins < 100) this.coins = 100;
			if(this.coins > PlayerManazer.getPlayerInfo(player).getCoins()){
				this.coins = PlayerManazer.getPlayerInfo(player).getCoins();
				player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
			}
			player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
			this.update();
		}

		public void confirm(){
			if(PlayerManazer.getPlayerInfo(player).getCoins() >= coins){
				PlayerManazer.getPlayerInfo(player).giveCoins(-coins);
				currentRun = -1;
				running = true;
				this.update();
				currentRun = 0;
				for(int i=0;i<multipliers.length;i++) this.chooseRandomNumbers();
				task = Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,10,4);
				player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
			}
		}

		private void chooseRandomNumbers(){
			int index = random.nextInt(numbers.length);
			if(randoms[index] == true) this.chooseRandomNumbers();
			randoms[index] = true;
		}

		private void finish(){
			final int betcoins = coins;
			int winnumbers = 0;
			for(int i=0;i<numbers.length;i++){
				if(numbers[i] && randoms[i]) winnumbers ++;
			}
			int wincoins = (int)(winnumbers > 0 ? coins*multipliers[winnumbers-1] : 0);
			this.update();
			this.reset();
			if(wincoins > 0){
				wincoins = PlayerManazer.getPlayerInfo(player).giveCoins(wincoins);
				Bukkit.broadcastMessage("§d[Loterie] §6"+player.getName()+" §fuhodl "+winnumbers+" "+StringUtil.inflect(winnumbers,new String[]{"cislo","cisla","cisel"})+" a ziskava §a+"+wincoins+" coins");
			}
			final int givencoins = wincoins;
			player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
			Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
				public void run(){
					LobbyLotteryInventory.this.close();
					if(givencoins > 0) PlayerManazer.getPlayerInfo(player).runCoinsEffect("§aVyhra v loterii",givencoins);
					else {
						Title.showTitle(player,"§cBez vyhry",0.0,4,0.5);
						Title.showSubTitle(player,"§fNic jsi nevyhral, zkus to priste.",0.0,4,0.5);
					}
				}
			},20);
			final int winnumbers2 = winnumbers;
			Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					RealCraft.getInstance().db.update("INSERT INTO "+LOTTERIES+" (user_id,lottery_bet,lottery_win,lottery_numbers,lottery_created) VALUES('"+PlayerManazer.getPlayerInfo(player).getId()+"','"+betcoins+"','"+givencoins+"','"+winnumbers2+"','"+(System.currentTimeMillis()/1000)+"')");
					PlayerManazer.getPlayerInfo(player).loadLastLotteryTime();
				}
			});
		}
	}
}