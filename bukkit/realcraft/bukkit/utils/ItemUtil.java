package realcraft.bukkit.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ItemUtil {

	public static ItemStack getHead(String url){
		return ItemUtil.getHead(null,url);
	}

	public static ItemStack getHead(String name,String url){
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(),null);
		profile.getProperties().put("textures",new Property("textures",url));
		Field profileField;
		try {
			profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta,profile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		if(name != null) headMeta.setDisplayName(name);
		head.setItemMeta(headMeta);
		return head;
	}

	public static void removeItems(Inventory inventory,ItemStack item,int amount){
		removeItems(inventory,item,amount,false);
	}

	public static void removeItems(Inventory inventory,ItemStack item,int amount,boolean reverse){
        if(amount <= 0) return;
        int size = inventory.getSize();
        for(int slot=size-1;slot>=0;slot--){
            ItemStack is = inventory.getItem(slot);
            if(is == null) continue;
            if(item.isSimilar(is)){
                int newAmount = is.getAmount() - amount;
                if(newAmount > 0){
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if(amount == 0) break;
                }
            }
        }
    }

	public static String getItemName(ItemStack item){
		return MaterialUtil.getName(item.getType());
	}

	public static void setLetherColor(ItemStack item,String color){
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(Color.fromRGB(Integer.valueOf(color.substring(1,3),16),Integer.valueOf(color.substring(3,5),16),Integer.valueOf(color.substring(5,7),16)));
		item.setItemMeta(meta);
	}

	public static ArrayList<String> getLores(String... lines){
		return new ArrayList<>(Arrays.asList(lines));
	}
}