package realcraft.bukkit.shops;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.JsonUtil;

public class ShopManager implements Runnable {

	public static final String SHOPS = "shops";
	public static final String TRANSACTIONS = "shops_transactions";
	public static final String WORLD = "world";
	public static final String PREFIX = "§e[ChestShop]§f ";
	public static final int COMMAND_TIMEOUT = 10*1000;
	public static final int SHOP_FEE = 10;

	private static HashMap<Location,Shop> shops = new HashMap<Location,Shop>();
	private static HashMap<Player,ShopPlayer> players = new HashMap<Player,ShopPlayer>();

	public ShopManager(){
		new ShopListeners();
		new ShopMarket();
		this.loadShops();
		this.checkExistence();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,10*20,10*20);
	}

	@Override
	public void run(){
		Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				for(Shop shop : ShopManager.getShops()){
					shop.checkTransactions();
				}
			}
		});
	}

	private void loadShops(){
		ResultSet rs = RealCraft.getInstance().db.query("SELECT shop_id FROM "+SHOPS+" WHERE shop_deleted = '0'");
		try {
			while(rs.next()){
				int id = rs.getInt("shop_id");
				Shop shop = new Shop(id);
				shops.put(shop.getLocation(),shop);
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	private void checkExistence(){
		if(!RealCraft.isTestServer()){
			for(Shop shop : ShopManager.getShops()){
				if(!shop.exists()){
					ShopManager.removeShop(shop);
				}
			}
		}
	}

	public static ArrayList<Shop> getShops(){
		return new ArrayList<Shop>(shops.values());
	}

	public static void removeShop(Shop shop){
		shop.remove();
		shops.remove(shop.getLocation());
	}

	public static Shop getShop(Location location){
		return shops.get(location);
	}

	public static ShopPlayer getPlayer(Player player){
		if(!players.containsKey(player)) players.put(player,new ShopPlayer(player));
		return players.get(player);
	}

	public static void removePlayer(Player player){
		players.remove(player);
	}

	public static void createShop(Player player,Location location){
		PreparedStatement stmt;
		try {
			stmt = RealCraft.getInstance().db.conn.prepareStatement("INSERT INTO "+SHOPS+" (user_id,shop_location,shop_item,shop_price,shop_created) VALUES(?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1,ShopManager.getPlayer(player).getId());
			stmt.setString(2,JsonUtil.toJSONLocation(location));
			stmt.setString(3,JsonUtil.toJSONItem(ShopManager.getPlayer(player).getItem()));
			stmt.setInt(4,ShopManager.getPlayer(player).getPrice());
			stmt.setInt(5,(int)(System.currentTimeMillis()/1000));
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()){
				int id = rs.getInt(1);
				Shop shop = new Shop(id);
				shops.put(shop.getLocation(),shop);
				ShopManager.sendMessage(player,"§aObchod vytvoren: §e"+shop.getItemName()+"§f za §a"+shop.getPrice()+" coins");
				player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
				Users.getUser(player).giveCoins(-SHOP_FEE);
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public static void sendMessage(Player player,String message){
		player.sendMessage(ShopManager.PREFIX+message);
	}
}