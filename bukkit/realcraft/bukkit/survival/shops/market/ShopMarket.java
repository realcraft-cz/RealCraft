package realcraft.bukkit.survival.shops.market;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.survival.economy.Economy;
import realcraft.bukkit.survival.shops.ShopManager;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ShopMarket implements Runnable {

	public static final String MARKETS = "shops_markets";
	public static final int MAX_MARKETS = 28;
	public static final int MIN_PRICE = 100;
	public static final int MAX_PRICE = 65535;

	private static HashMap<User,ShopMarketResidence> markets = new HashMap<>();

	public ShopMarket(){
		ShopMarket.loadMarkets();
		new ShopMarketMenu();
		Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(),this,60*20,60*20);
	}

	@Override
	public void run(){
		if(!RealCraft.isTestServer()){
			for(ShopMarketResidence market : this.getMarkets()){
				market.checkLastPaid();
			}
		}
	}

	public static ArrayList<ShopMarketResidence> getMarkets(){
		return new ArrayList<>(markets.values());
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

	public static ShopMarketResidence getMarket(User user){
		return markets.get(user);
	}

	public static ShopMarketResidence getMarket(Player player){
		return ShopMarket.getMarket(Users.getUser(player));
	}

	public static ShopMarketResidence getMarket(String name){
		for(ShopMarketResidence market : ShopMarket.getMarkets()){
			if(market.getResidence().equalsIgnoreCase(name)) return market;
		}
		return null;
	}

	public static int getMinPrice(){
		int price = Integer.MAX_VALUE;
		if(markets.size() < MAX_MARKETS) price = MIN_PRICE;
		else {
			for(ShopMarketResidence market : ShopMarket.getMarkets()){
				if(price > market.getPrice()) price = market.getPrice();
			}
			price ++;
		}
		return price;
	}

	public static void loadMarkets(){
		markets.clear();
		ResultSet rs = DB.query("SELECT * FROM "+MARKETS);
		try {
			while(rs.next()){
				ShopMarketResidence market = new ShopMarketResidence(Users.getUser(rs.getInt("user_id")),rs.getString("market_residence"),rs.getInt("market_price"),rs.getInt("market_lastpaid"));
				if(RealCraft.isTestServer()) markets.put(market.getUser(),market);
				else {
					ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByName(market.getResidence());
					if(residence != null) markets.put(market.getUser(),market);
					else market.remove();
				}
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public static void createMarket(Player player,int price){
		ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByLoc(player.getLocation());
		if(residence == null || !residence.isOwner(player)){
			ShopManager.sendMessage(player,"§cNejsi vlastnik teto residence.");
			return;
		}
		if(ShopMarket.getMarket(residence.getName()) != null){
			ShopManager.sendMessage(player,"§cTato residence je jiz na trhu.");
			return;
		}
		if(ShopMarket.getMarket(player) != null){
			ShopManager.sendMessage(player,"§cJednu residenci jiz na verejnem trhu mas.");
			return;
		}
		if(residence.getName().length() > 32){
			ShopManager.sendMessage(player,"§cNazev residence je prilis dlouhy (max 32 znaku).");
			return;
		}
		if(price < ShopMarket.getMinPrice()){
			ShopManager.sendMessage(player,"§cMinimalni nabidka je "+Economy.format(ShopMarket.getMinPrice()));
			return;
		}
		if(price > MAX_PRICE){
			ShopManager.sendMessage(player,"§cMaximalni nabidka je "+Economy.format(MAX_PRICE));
			return;
		}
		if(Users.getUser(player).getMoney() < price){
			ShopManager.sendMessage(player,"§cNemas dostatek penez.");
			return;
		}
		ShopManager.sendMessage(player,"§aResidence pridana na verejny trh.");
		ShopManager.sendMessage(player,"§7Cena za zverejneni obchodu se plati jednou za den.");
		ShopManager.sendMessage(player,"§7Vyssi nabidka te posune na predni pozice trhu.");
		player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
		Users.getUser(player).addMoney(-price);
		ShopMarketResidence market = new ShopMarketResidence(Users.getUser(player),residence.getName(),price,(int)(System.currentTimeMillis()/1000));
		markets.put(market.getUser(),market);
		DB.update("INSERT INTO "+MARKETS+" (user_id,market_price,market_residence,market_lastpaid) VALUES('"+market.getUser().getId()+"','"+market.getPrice()+"','"+market.getResidence()+"','"+market.getLastPaid()+"')");
	}

	public static void renameMarket(ShopMarketResidence market,String name){
		market.rename(name);
	}

	public static void removeMarket(ShopMarketResidence market){
		market.remove();
		markets.remove(market.getUser());
	}
}