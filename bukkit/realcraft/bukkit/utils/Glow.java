package realcraft.bukkit.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import realcraft.bukkit.RealCraft;

import java.lang.reflect.Field;

public class Glow extends Enchantment {

	private static Glow glow;

	public Glow(NamespacedKey key){
		super(key);
	}

	@Override
	public boolean canEnchantItem(ItemStack arg0){
		return false;
	}

	@Override
	public boolean conflictsWith(Enchantment arg0){
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget(){
		return null;
	}

	@Override
	public int getMaxLevel(){
		return 0;
	}

	@Override
	public String getName(){
		return null;
	}

	@Override
	public int getStartLevel(){
		return 0;
	}

	@Override
	public boolean isCursed(){
		return false;
	}

	@Override
	public boolean isTreasure(){
		return false;
	}

	public static void registerGlow(){
		try {
		    Field f = Enchantment.class.getDeclaredField("acceptingNew");
		    f.setAccessible(true);
		    f.set(null, true);
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
		try {
		    Glow glow = new Glow(new NamespacedKey(RealCraft.getInstance(),"glow"));
		    Enchantment.registerEnchantment(glow);
		}
		catch (IllegalArgumentException e){
		}
		catch(Exception e){
		    e.printStackTrace();
		}
	}

	public static Glow getGlow(){
		if(glow == null) glow = new Glow(new NamespacedKey(RealCraft.getInstance(),"glow"));
		return glow;
	}
}