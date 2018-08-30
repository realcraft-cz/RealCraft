package realcraft.bukkit.survival.shops;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import realcraft.bukkit.survival.economy.Economy;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.StringUtil;
import realcraft.bukkit.utils.Title;

public class ShopPlayer {

	private Player player;
	private int id;
	private ItemStack item;
	private int price;
	private ShopPlayerCommand command;
	private long commandExpire;

	public ShopPlayer(Player player){
		this.player = player;
		this.id = Users.getUser(player).getId();
	}

	public Player getPlayer(){
		return player;
	}

	public int getId(){
		return id;
	}

	public ItemStack getItem(){
		return item;
	}

	public int getPrice(){
		return price;
	}

	public boolean hasCommand(ShopPlayerCommand type){
		return (type == command && commandExpire >= System.currentTimeMillis());
	}

	public void toCreate(int amount,int price){
		if(amount < 1) amount = 1;
		else if(amount > 64) amount = 64;
		if(price < 1) price = 1;
		else if(price > 9999) price = 9999;
		this.command = ShopPlayerCommand.CREATE;
		this.commandExpire = System.currentTimeMillis()+ShopManager.COMMAND_TIMEOUT;
		this.item = this.getPlayer().getInventory().getItemInMainHand().clone();
		this.item.setAmount((this.item.getMaxStackSize() > amount ? amount : this.item.getMaxStackSize()));
		this.price = price;
		ShopManager.sendMessage(player,"Klikni na truhlu pro vytvoreni obchodu.");
		Title.showTitle(player," ",0.2,10,0.2);
		Title.showSubTitle(player,"§fKlikni na truhlu",0.2,10,0.2);
	}

	public void showStats(){
		int shops = 0;
		int sales = 0;
		int revenue = 0;
		for(Shop shop : ShopManager.getShops()){
			if(shop.getOwner() == this.getId()){
				shops ++;
				sales += shop.getSales();
				revenue += shop.getPrice()*shop.getSales();
			}
		}
		player.sendMessage("");
		player.sendMessage("§e[ChestShop]§f Statistiky obchodu");
		player.sendMessage("§6Pocet obchodu: §e"+shops);
		player.sendMessage("§6Prijem: §a+"+Economy.format(revenue)+" §7("+sales+" "+StringUtil.inflect(sales,new String[]{"prodej","prodeje","prodeju"})+")");
	}

	public void cancelCommand(){
		command = null;
		commandExpire = 0;
	}

	public enum ShopPlayerCommand {
		CREATE;
	}
}