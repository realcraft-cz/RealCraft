package realcraft.bukkit.trading;

import org.bukkit.inventory.ItemStack;

public class TradeItem {
	private ItemStack itemStack;
	private int index;

	public TradeItem(ItemStack itemStack,int index){
		this.itemStack = itemStack;
		this.index = index;
	}

	public ItemStack getItemStack(){
		return itemStack;
	}

	public int getIndex(){
		return index;
	}
}