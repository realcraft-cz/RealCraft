package com.realcraft.shops;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.realcraft.RealCraft;
import com.realcraft.playermanazer.PlayerManazer;
import com.realcraft.utils.LocationUtil;
import com.realcraft.utils.strtotime;

import ru.beykerykt.lightapi.LightAPI;

public class ShopMarket implements Runnable {

	public static final String MARKETS = "shops_markets";
	public static final int MIN_PRICE = 10;
	public static final int MAX_PRICE = 65535;

	private static HashMap<Integer,ShopMarketResidence> markets = new HashMap<Integer,ShopMarketResidence>();
	private static HashMap<Location,ShopMarketPlace> places = new HashMap<Location,ShopMarketPlace>();

	public ShopMarket(){
		this.loadPlaces();
		this.loadMarkets();
		ShopMarket.updateMarkets();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,10*20,10*20);
	}

	@Override
	public void run(){
		for(ShopMarketResidence market : ShopMarket.getMarkets()){
			market.checkLastPaid();
		}
	}

	public void loadMarkets(){
		markets.clear();
		ResultSet rs = RealCraft.getInstance().db.query("SELECT t1.*,t2.user_name FROM "+MARKETS+" t1 INNER JOIN authme t2 USING(user_id)");
		try {
			while(rs.next()){
				ShopMarketResidence market = new ShopMarketResidence(rs.getInt("user_id"),rs.getString("user_name"),rs.getInt("market_price"),rs.getString("market_residence"),rs.getInt("market_lastpaid"));
				if(RealCraft.isTestServer()) markets.put(market.getOwner(),market);
				else {
					ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByName(market.getResidence());
					if(residence != null) markets.put(market.getOwner(),market);
					else market.remove();
				}
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void loadPlaces(){
		places.clear();
		File file = new File(RealCraft.getInstance().getDataFolder()+"/markets.yml");
		if(file.exists()){
			FileConfiguration config = new YamlConfiguration();
			try {
				config.load(file);
				List<Map<String, Object>> tmpMarkets = (List<Map<String, Object>>) config.get("markets");
				if(tmpMarkets != null && !tmpMarkets.isEmpty()){
					for(Map<String, Object> market : tmpMarkets){
						double x = Double.valueOf(market.get("x").toString());
						double y = Double.valueOf(market.get("y").toString());
						double z = Double.valueOf(market.get("z").toString());
						float yaw = Float.valueOf(market.get("yaw").toString());
						World world = Bukkit.getServer().getWorld(market.get("world").toString());
						places.put(new Location(world,x,y,z,0f,0f),new ShopMarketPlace(places.size(),new Location(world,x,y,z,yaw,0f)));
					}
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public static ArrayList<ShopMarketResidence> getMarkets(){
		return new ArrayList<ShopMarketResidence>(markets.values());
	}

	public static ArrayList<ShopMarketResidence> getSortedMarkets(){
		ArrayList<ShopMarketResidence> sortedMarkets = ShopMarket.getMarkets();
		Collections.sort(sortedMarkets,new Comparator<ShopMarketResidence>(){
			@Override
			public int compare(ShopMarketResidence market1,ShopMarketResidence market2){
				int compare = Integer.compare(market1.getPrice(),market2.getPrice());
				if(compare > 0) return -1;
				else if(compare < 0) return 1;
				return 0;
			}
		});
		return sortedMarkets;
	}

	public static ShopMarketResidence getMarket(int owner){
		return markets.get(owner);
	}

	public static ShopMarketResidence getMarketByName(String name){
		for(ShopMarketResidence market : ShopMarket.getMarkets()){
			if(market.getResidence().equalsIgnoreCase(name)) return market;
		}
		return null;
	}

	public static ShopMarketResidence getPlayerMarket(Player player){
		return ShopMarket.getMarket(PlayerManazer.getPlayerInfo(player).getId());
	}

	public static ArrayList<ShopMarketPlace> getPlaces(){
		return new ArrayList<ShopMarketPlace>(places.values());
	}

	public static ArrayList<ShopMarketPlace> getSortedPlaces(){
		ArrayList<ShopMarketPlace> sortedPlaces = ShopMarket.getPlaces();
		Collections.sort(sortedPlaces,new Comparator<ShopMarketPlace>(){
			@Override
			public int compare(ShopMarketPlace place1,ShopMarketPlace place2){
				int compare = Integer.compare(place1.getOrder(),place2.getOrder());
				if(compare > 0) return 1;
				else if(compare < 0) return -1;
				return 0;
			}
		});
		return sortedPlaces;
	}

	public static ShopMarketPlace getPlace(Location location){
		return places.get(location);
	}

	public static int getMinimumPrice(){
		int price = Integer.MAX_VALUE;
		if(markets.size() < places.size()) price = MIN_PRICE;
		else {
			for(ShopMarketResidence market : ShopMarket.getMarkets()){
				if(price > market.getPrice()) price = market.getPrice();
			}
			price ++;
		}
		return price;
	}

	public static void updateMarkets(){
		int index = 0;
		ArrayList<ShopMarketResidence> marketsTmp = ShopMarket.getSortedMarkets();
		for(ShopMarketPlace place : ShopMarket.getSortedPlaces()){
			place.setMarket(null);
			if(marketsTmp.size() > index){
				place.setMarket(marketsTmp.get(index));
				index ++;
			}
			place.update();
		}
		int fromIndex = index;
		index = 0;
		for(ShopMarketResidence market : marketsTmp){
			if(index >= fromIndex) ShopMarket.removeMarket(market);
			index ++;
		}
	}

	public static void createMarket(Player player,int price){
		ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByLoc(player.getLocation());
		if(residence == null || !residence.isOwner(player)){
			ShopManager.sendMessage(player,"§cNejsi vlastnik teto residence.");
			return;
		}
		else if(ShopMarket.getMarketByName(residence.getName()) != null){
			ShopManager.sendMessage(player,"§cTato residence je jiz na trhu.");
			return;
		}
		else if(residence.getName().length() > 32){
			ShopManager.sendMessage(player,"§cNazev residence je prilis dlouhy.");
			return;
		}
		else if(price < ShopMarket.getMinimumPrice()){
			ShopManager.sendMessage(player,"§cMinimalni nabidka je "+ShopMarket.getMinimumPrice()+" coins.");
			return;
		}
		else if(price > MAX_PRICE){
			ShopManager.sendMessage(player,"§cMaximalni nabidka je "+MAX_PRICE+" coins.");
			return;
		}
		else if(PlayerManazer.getPlayerInfo(player).getCoins() < price){
			ShopManager.sendMessage(player,"§cNemas dostatek coinu.");
			return;
		} else {
			ShopManager.sendMessage(player,"§aResidence pridana na verejny trh.");
			ShopManager.sendMessage(player,"§7Cena za zverejneni obchodu se plati jednou za den.");
			ShopManager.sendMessage(player,"§7Vyssi nabidka te posune na predni pozice trhu.");
			player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
			PlayerManazer.getPlayerInfo(player).giveCoins(-price);
			ShopMarketResidence market = new ShopMarketResidence(PlayerManazer.getPlayerInfo(player).getId(),player.getName(),price,residence.getName(),(int)(System.currentTimeMillis()/1000));
			markets.put(market.getOwner(),market);
			RealCraft.getInstance().db.update("INSERT INTO "+MARKETS+" (user_id,market_price,market_residence,market_lastpaid) VALUES('"+market.getOwner()+"','"+market.getPrice()+"','"+market.getResidence()+"','"+market.getLastPaid()+"')");
			ShopMarket.updateMarkets();
		}
	}

	public static void renameMarket(ShopMarketResidence market,String name){
		market.rename(name);
		ShopMarket.updateMarkets();
	}

	public static void removeMarket(ShopMarketResidence market){
		market.remove();
		markets.remove(market.getOwner());
		ShopMarket.updateMarkets();
	}

	public static class ShopMarketResidence {

		private int owner;
		private String ownerName;
		private int price;
		private String residence;
		private int lastPaid;

		public ShopMarketResidence(int owner,String ownerName,int price,String residence,int lastPaid){
			this.owner = owner;
			this.ownerName = ownerName;
			this.price = price;
			this.residence = residence;
			this.lastPaid = lastPaid;
		}

		public int getOwner(){
			return owner;
		}

		public String getOwnerName(){
			return ownerName;
		}

		public int getPrice(){
			return price;
		}

		public String getResidence(){
			return residence;
		}

		public int getLastPaid(){
			return lastPaid;
		}

		public void checkLastPaid(){
			int yesterday = (int)(strtotime.strtotime("yesterday").getTime()/1000);
			if(lastPaid > yesterday && lastPaid < yesterday+86400){
				Player player = Bukkit.getPlayer(this.getOwnerName());
				if(player != null && PlayerManazer.getPlayerInfo(player).getId() == this.getOwner()){
					lastPaid = (int)(System.currentTimeMillis()/1000);
					PlayerManazer.getPlayerInfo(player).giveCoins(-this.getPrice(),false);
					RealCraft.getInstance().db.update("UPDATE "+MARKETS+" SET market_lastpaid = '"+lastPaid+"' WHERE user_id = '"+this.getOwner()+"'");
					ShopManager.sendMessage(player,"§fPoplatek za verejny trh: §c-"+this.getPrice()+" coins");
					player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
				}
			}
			else if(lastPaid <= yesterday){
				ShopMarket.removeMarket(this);
			}
		}

		public void rename(String residence){
			this.residence = residence;
			RealCraft.getInstance().db.update("UPDATE "+MARKETS+" SET market_residence = '"+residence+"' WHERE user_id = '"+this.getOwner()+"'");
		}

		public void remove(){
			RealCraft.getInstance().db.update("DELETE FROM "+MARKETS+" WHERE user_id = '"+this.getOwner()+"'");
		}
	}

	public static class ShopMarketPlace {

		private int order;
		private Location location;
		private ShopMarketResidence market;

		public ShopMarketPlace(int order,Location location){
			this.order = order;
			this.location = location;
			LightAPI.createLight(location.getBlock().getRelative(LocationUtil.yawToFace(location.getYaw()).getOppositeFace()).getLocation(),15,false);
		}

		public int getOrder(){
			return order;
		}

		public Sign getSign(){
			return (location.getBlock().getType() == Material.WALL_SIGN ? (Sign) location.getBlock().getState() : null);
		}

		public Sign createSign(){
			location.getBlock().setType(Material.WALL_SIGN);
			org.bukkit.material.Sign matSign = new org.bukkit.material.Sign(Material.WALL_SIGN);
			matSign.setFacingDirection(LocationUtil.yawToFace(location.getYaw()).getOppositeFace());
			Sign sign = this.getSign();
			sign.setData(matSign);
			sign.update();
			return sign;
		}

		public void removeSign(){
			location.getBlock().setType(Material.AIR);
		}

		public void setMarket(ShopMarketResidence market){
			this.market = market;
		}

		public void update(){
			if(market != null){
				Sign sign = this.getSign();
				if(sign == null) sign = this.createSign();
				sign.setLine(0,"");
				sign.setLine(1,"§9§l"+market.getResidence());
				sign.setLine(2,market.getOwnerName());
				sign.setLine(3,"");
				sign.update();
			} else {
				this.removeSign();
			}
		}

		public void teleport(Player player){
			if(market != null){
				ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByName(market.getResidence());
				if(residence != null){
					player.teleport(residence.getTeleportLocation());
					player.getWorld().playSound(residence.getTeleportLocation(),Sound.ENTITY_ENDERMEN_TELEPORT,1f,1f);
				}
			}
		}

		public void showInfo(Player player){
			if(market != null){
				player.sendMessage("");
				player.sendMessage("§e[Market]§f "+market.getResidence());
				player.sendMessage("§6Vlastnik: §e"+market.getOwnerName());
				player.sendMessage("§6Cena za propagaci: §a"+market.getPrice()+" coins");
			}
		}
	}
}