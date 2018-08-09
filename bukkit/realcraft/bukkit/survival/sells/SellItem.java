package realcraft.bukkit.survival.sells;

import org.bukkit.Material;

public class SellItem {

	private Material type;
	private int amount;
	private int price;

	public SellItem(Material type,int amount,int price){
		this.type = type;
		this.amount = amount;
		this.price = price;
	}

	public Material getType(){
		return type;
	}

	public int getAmount(){
		return amount;
	}

	public int getPrice(){
		return price;
	}
}