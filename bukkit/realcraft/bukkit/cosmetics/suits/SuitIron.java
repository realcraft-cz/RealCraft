package realcraft.bukkit.cosmetics.suits;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SuitIron extends Suit {

	public SuitIron(SuitType type){
	    super(type);
	}

	@Override
	public void onCreate(Player player){
		ItemStack itemStack;
		ItemMeta itemMeta;

		itemStack = new ItemStack(Material.IRON_HELMET);
		itemMeta = itemStack.getItemMeta();
		itemMeta.addEnchant(Enchantment.DURABILITY,10,true);
		itemStack.setItemMeta(itemMeta);
		player.getInventory().setHelmet(itemStack);

		itemStack = new ItemStack(Material.IRON_CHESTPLATE);
		itemMeta = itemStack.getItemMeta();
		itemMeta.addEnchant(Enchantment.DURABILITY,10,true);
		itemStack.setItemMeta(itemMeta);
		player.getInventory().setChestplate(itemStack);

		itemStack = new ItemStack(Material.IRON_LEGGINGS);
		itemMeta = itemStack.getItemMeta();
		itemMeta.addEnchant(Enchantment.DURABILITY,10,true);
		itemStack.setItemMeta(itemMeta);
		player.getInventory().setLeggings(itemStack);

		itemStack = new ItemStack(Material.IRON_BOOTS);
		itemMeta = itemStack.getItemMeta();
		itemMeta.addEnchant(Enchantment.DURABILITY,10,true);
		itemStack.setItemMeta(itemMeta);
		player.getInventory().setBoots(itemStack);
	}

	@Override
	public void onUpdate(Player player){
	}
}