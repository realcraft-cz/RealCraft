package realcraft.bukkit.survival.shops;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.survival.economy.Economy;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.DateUtil;
import realcraft.bukkit.utils.ItemUtil;
import realcraft.bukkit.utils.JsonUtil;
import realcraft.bukkit.utils.StringUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Shop {

	private int id;
	private int owner;
	private String ownerName;
	private Location location;
	private ItemStack item;
	private int price;
	private int created;
	private Hologram hologram;
	private boolean inStock = true;
	private int sales;

	@Deprecated
	private boolean valid;

	public Shop(int id){
		this.id = id;
		ResultSet rs = DB.query("SELECT t1.*,t2.user_name,(SELECT COUNT(*) FROM "+ShopManager.TRANSACTIONS+" WHERE shop_id = '"+this.getId()+"') AS shop_sales FROM "+ShopManager.SHOPS+" t1 INNER JOIN authme t2 USING(user_id) WHERE shop_id = '"+this.id+"'");
		try {
			if(rs.next()){
				this.owner = rs.getInt("user_id");
				this.ownerName = rs.getString("user_name");
				this.location = JsonUtil.getJSONLocation(rs.getString("shop_location"),Bukkit.getWorld(ShopManager.WORLD));
				this.item = JsonUtil.getJSONItem(rs.getString("shop_item"));
				if(this.item != null){
					this.price = rs.getInt("shop_price");
					this.created = rs.getInt("shop_created");
					this.sales = rs.getInt("shop_sales");
					hologram = HologramsAPI.createHologram(RealCraft.getInstance(),location.clone().add(0.5,2,0.5));
					hologram.insertTextLine(0,"§e"+this.getItemName());
					hologram.insertTextLine(1,"§a"+Economy.format(this.getPrice()));
					try {
						hologram.insertItemLine(2,this.getItem());
						valid = true;
						if(this.exists()) this.update();
					} catch(IllegalArgumentException e){
						valid = false;
						hologram.delete();
					}
				}
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	@Deprecated
	public boolean isValid(){
		return valid;
	}

	public int getId(){
		return id;
	}

	public int getOwner(){
		return owner;
	}

	public String getOwnerName(){
		return ownerName;
	}

	public Location getLocation(){
		return location;
	}

	public ItemStack getItem(){
		return item;
	}

	public String getItemName(){
		return (this.getItem().getMaxStackSize() == 1 ? "" : this.getItem().getAmount()+"x ")+ItemUtil.getItemName(this.getItem());
	}

	public int getPrice(){
		return price;
	}

	public int getCreated(){
		return created;
	}

	public int getSales(){
		return sales;
	}

	public boolean exists(){
		if(!ShopUtil.isChest(this.location.getBlock())) return false;
		return true;
	}

	public boolean isOwner(Player player){
		return (Users.getUser(player).getId() == this.getOwner());
	}

	public Inventory getInventory(){
		Chest chest = (Chest) location.getBlock().getState();
		return chest.getBlockInventory();
	}

	public int getStock(){
		int stock = 0;
		for(ItemStack item : this.getInventory().getContents()){
			if(item != null && item.getType() == this.getItem().getType()){
				stock += item.getAmount();
			}
		}
		return (int)Math.floor((float)stock/this.getItem().getAmount());
	}

	public void showInfo(Player player){
		player.sendMessage("");
		player.sendMessage("§e[ChestShop]§f #"+this.getId());
		player.sendMessage("§6Vlastnik: §e"+this.getOwnerName());
		player.sendMessage("§6Produkt: §e"+this.getItemName());
		player.sendMessage("§6Cena: §a"+Economy.format(this.getPrice()));
		player.sendMessage("§6Skladem: "+(this.getStock() > 0 ? "§e"+this.getStock() : "§c0")+" ks");
		if(this.isOwner(player)){
			player.sendMessage("§7------------------------------");
			player.sendMessage("§6Prijem: §a+"+Economy.format(this.getPrice()*this.getSales())+" §7("+this.getSales()+" "+StringUtil.inflect(this.getSales(),new String[]{"prodej","prodeje","prodeju"})+")");
			player.sendMessage("§6Vytvoreno: §e"+DateUtil.lastTime(this.getCreated(),true));
		}
	}

	public void buy(Player player){
		if(this.getStock() < 1){
			ShopManager.sendMessage(player,"§cObchod nema dostatecne zasoby.");
			player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
			return;
		}
		if(Users.getUser(player).getMoney() < this.getPrice()){
			ShopManager.sendMessage(player,"§cNemas dostatek penez.");
			player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
			return;
		}
		if(player.getInventory().firstEmpty() == -1){
			ShopManager.sendMessage(player,"§cNemas volne misto v inventari.");
			player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
			return;
		}
		Users.getUser(player).addMoney(-this.getPrice());
		ItemUtil.removeItems(this.getInventory(),this.getItem(),this.getItem().getAmount());
		player.getInventory().addItem(this.getItem().clone());
		ShopManager.sendMessage(player,"Zakoupeno §6"+this.getItemName()+"§f (§c-"+Economy.format(this.getPrice())+"§f)");
		player.playSound(player.getLocation(),Sound.ENTITY_ITEM_PICKUP,1f,1f);
		DB.update("INSERT INTO "+ShopManager.TRANSACTIONS+" (shop_id,user_id,transaction_created) VALUES('"+this.getId()+"','"+Users.getUser(player).getId()+"','"+(int)(System.currentTimeMillis()/1000)+"')");
		this.sales ++;
		this.update();
	}

	public void checkTransactions(){
		Player player = Bukkit.getPlayer(this.getOwnerName());
		if(player != null && Users.getUser(player).getId() == this.getOwner()){
			ResultSet rs = DB.query("SELECT COUNT(*) AS count FROM "+ShopManager.TRANSACTIONS+" WHERE shop_id = '"+this.getId()+"' AND transaction_finished = '0'");
			try {
				if(rs.next()){
					int count = rs.getInt("count");
					if(count > 0){
						Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
							@Override
							public void run(){
								int price = Shop.this.getPrice()*count;
								Users.getUser(player).addMoney(price);
								ShopManager.sendMessage(player,"Prijem: §a+"+Economy.format(price)+" §7("+count+" "+StringUtil.inflect(count,new String[]{"prodej","prodeje","prodeju"})+")");
								player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
							}
						});
					}
				}
				rs.close();
			} catch (SQLException e){
				e.printStackTrace();
			}
			DB.update("UPDATE "+ShopManager.TRANSACTIONS+" SET transaction_finished = '"+(int)(System.currentTimeMillis()/1000)+"' WHERE shop_id = '"+this.getId()+"' AND transaction_finished = '0'");
		}
	}

	public void update(){
		int stock = this.getStock();
		if(stock > 0){
			if(!inStock){
				inStock = true;
				this.updateHologram();
			}
		} else {
			if(inStock){
				inStock = false;
				this.updateHologram();
			}
		}
	}

	private void updateHologram(){
		hologram.clearLines();
		hologram.insertTextLine(0,(inStock ? "§e" : "§7")+this.getItemName());
		hologram.insertTextLine(1,(inStock ? "§a" : "§7")+Economy.format(this.getPrice()));
		hologram.insertItemLine(2,this.getItem());
	}

	public void remove(){
		if(hologram != null) hologram.delete();
		DB.update("UPDATE "+ShopManager.SHOPS+" SET shop_deleted = '"+(int)(System.currentTimeMillis()/1000)+"' WHERE shop_id = '"+this.getId()+"'");
	}
}