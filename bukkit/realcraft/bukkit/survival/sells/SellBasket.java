package realcraft.bukkit.survival.sells;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.ItemUtil;

import java.util.ArrayList;

public class SellBasket {

	private static final int MAX_SLOTS = 6;
	private ArrayList<BasketItem> items = new ArrayList<>();

	private Player player;

	public SellBasket(Player player){
		this.player = player;
	}

	public ArrayList<BasketItem> getItems(){
		return items;
	}

	private BasketItem getFreeSimilarItem(SellItem item){
		for(BasketItem item2 : items){
			if(item2.getItem().getType() == item.getType() && item2.getAmount() < item.getType().getMaxStackSize()) return item2;
		}
		return null;
	}

	private int getBasketAmount(Material type){
		int amount = 0;
		for(BasketItem item : items){
			if(item.getItem().getType() == type) amount += item.getAmount();
		}
		return amount;
	}

	private int getInventoryAmount(Material type){
		int amount = 0;
		for(ItemStack item : player.getInventory().getContents()){
			if(item != null && item.getType() == type) amount += item.getAmount();
		}
		return amount;
	}

	public boolean hasNextEnough(Material type,int amount){
		return (this.getInventoryAmount(type) >= this.getBasketAmount(type)+amount);
	}

	public int getPrice(){
		int price = 0;
		for(BasketItem item : items){
			price += (int)Math.floor((item.getItem().getPrice()/(item.getItem().getAmount()*1.0))*item.getAmount());
		}
		return price;
	}

	public void addItem(SellItem item,int amount) throws FullBasketException, NotEnoughtItemsException {
		if(!this.hasNextEnough(item.getType(),1)) throw new NotEnoughtItemsException();
		BasketItem basketItem = this.getFreeSimilarItem(item);
		if(basketItem != null && basketItem.getAmount() < item.getType().getMaxStackSize()){
			int freeAmount = item.getType().getMaxStackSize()-basketItem.getAmount();
			if(amount > freeAmount) amount = freeAmount;
			basketItem.setAmount(basketItem.getAmount()+amount);
		} else {
			if(items.size() >= MAX_SLOTS) throw new FullBasketException();
			basketItem = new BasketItem(item,amount);
			items.add(basketItem);
		}
		this.fixAmount(basketItem);
	}

	public void removeItem(int index,boolean shift) throws EmptySlotException {
		if(items.size() <= index) throw new EmptySlotException();
		if(shift) items.remove(index);
		else {
			items.get(index).setAmount(items.get(index).getAmount()-items.get(index).getItem().getAmount());
			if(items.get(index).getAmount() < 1){
				items.remove(index);
			}
		}
	}

	private void fixAmount(BasketItem item){
		int diff = (this.getBasketAmount(item.getItem().getType())-this.getInventoryAmount(item.getItem().getType()));
		if(diff > 0){
			ArrayList<BasketItem> toRemove = new ArrayList<>();
			for(BasketItem _item : items){
				if(_item.getItem().getType() == item.getItem().getType()){
					if(diff < _item.getAmount()){
						_item.setAmount(_item.getAmount()-diff);
						diff = 0;
					}
					else {
						toRemove.add(_item);
						diff = -_item.getAmount();
					}
				}
			}
			for(BasketItem _item : toRemove){
				items.remove(_item);
			}
		}
	}

	public void checkout() throws EmptyBasketException {
		if(this.getItems().size() < 1) throw new EmptyBasketException();
		if(this.getPrice() < 1) throw new EmptyBasketException();
		Users.getUser(player).addMoney(this.getPrice());
		for(BasketItem item : items){
			ItemUtil.removeItems(player.getInventory(),new ItemStack(item.getItem().getType()),item.getAmount());
		}
	}

	public class BasketItem {

		private SellItem item;
		private int amount;

		public BasketItem(SellItem item,int amount){
			this.item = item;
			this.amount = amount;
		}

		public SellItem getItem(){
			return item;
		}

		public int getAmount(){
			return amount;
		}

		public void setAmount(int amount){
			this.amount = amount;
		}
	}

	public class FullBasketException extends Exception {
	}

	public class EmptyBasketException extends Exception {
	}

	public class NotEnoughtItemsException extends Exception {
	}

	public class EmptySlotException extends Exception {
	}
}