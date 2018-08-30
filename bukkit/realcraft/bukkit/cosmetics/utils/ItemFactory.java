package realcraft.bukkit.cosmetics.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemFactory {

	@SuppressWarnings("deprecation")
	public static ItemStack create(Material material,String displayName,String... lore) {
		ItemStack itemStack = new MaterialData(material).toItemStack(1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(displayName);
		if (lore != null) {
			List<String> finalLore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<String>();
			for (String s : lore)
				if (s != null)
					finalLore.add(s.replace("&", "§"));
			itemMeta.setLore(finalLore);
		}
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	public static ItemStack createSkull(String urlToFormat,String name) {
		String url = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" + urlToFormat;
		ItemStack head = create(Material.PLAYER_HEAD, name);

		if (url.isEmpty()) return head;

		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", url));
		Field profileField;
		try {
			profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		head.setItemMeta(headMeta);
		return head;
	}

	public static ItemStack createColouredLeather(Material armourPart, int red, int green, int blue) {
		ItemStack itemStack = new ItemStack(armourPart);
		LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
		leatherArmorMeta.setColor(Color.fromRGB(red, green, blue));
		itemStack.setItemMeta(leatherArmorMeta);
		return itemStack;
	}
}