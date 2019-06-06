package realcraft.bukkit.survival.shops.market;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.survival.economy.Economy;
import realcraft.bukkit.survival.shops.ShopManager;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.ItemUtil;
import realcraft.bukkit.utils.strtotime;
import realcraft.share.users.User;

public class ShopMarketResidence {

	private User user;
	private String residence;
	private int price;
	private int lastPaid;

	private ItemStack item;

	public ShopMarketResidence(User user,String residence,int price,int lastPaid){
		this.user = user;
		this.residence = residence;
		this.price = price;
		this.lastPaid = lastPaid;
	}

	public User getUser(){
		return user;
	}

	public String getResidence(){
		return residence;
	}

	public int getPrice(){
		return price;
	}

	public int getLastPaid(){
		return lastPaid;
	}

	public void checkLastPaid(){
		int yesterday = (int)(strtotime.strtotime("yesterday").getTime()/1000);
		if(lastPaid > yesterday && lastPaid < yesterday+86400){
			if(this.getUser().getMoney() < this.getPrice() || this.getUser().getLastLogged() < (System.currentTimeMillis()/1000)-(7*86400)){
				ShopMarket.removeMarket(this);
				return;
			}
			lastPaid = (int)(System.currentTimeMillis()/1000);
			this.getUser().addMoney(-this.getPrice());
			DB.update("UPDATE "+ShopMarket.MARKETS+" SET market_lastpaid = '"+lastPaid+"' WHERE user_id = '"+this.getUser().getId()+"'");
			Player player = Users.getPlayer(this.getUser());
			if(player != null){
				ShopManager.sendMessage(player,"§fPoplatek za verejny trh: §c-"+Economy.format(this.getPrice()));
				player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
			}
		}
		else if(lastPaid <= yesterday){
			ShopMarket.removeMarket(this);
		}
	}

	public ItemStack getItemStack(){
		if(item == null){
			item = ItemUtil.getHead(this.getUser().getSkin().getValue());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§9§l"+this.getResidence());
			meta.setLore(ItemUtil.getLores(
					"§7Vlastnik: "+this.getUser().getRank().getChatColor()+"§l"+this.getUser().getName(),
					"§7Klikni pro teleport"
			));
			item.setItemMeta(meta);
		}
		return item;
	}

	public void rename(String residence){
		this.residence = residence;
		DB.update("UPDATE "+ShopMarket.MARKETS+" SET market_residence = '"+residence+"' WHERE user_id = '"+this.getUser().getId()+"'");
	}

	public void remove(){
		DB.update("DELETE FROM "+ShopMarket.MARKETS+" WHERE user_id = '"+this.getUser().getId()+"'");
	}
}
