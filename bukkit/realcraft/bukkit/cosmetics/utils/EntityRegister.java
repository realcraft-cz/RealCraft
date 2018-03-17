package realcraft.bukkit.cosmetics.utils;

import org.bukkit.entity.EntityType;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.MinecraftKey;

public class EntityRegister {

	@SuppressWarnings("deprecation")
	public static void register(String name,EntityType entityType,Class<? extends Entity> customClass){
		MinecraftKey key = new MinecraftKey(name);
		EntityTypes.d.add(key);
        EntityTypes.b.a(entityType.getTypeId(), key, customClass);
	}
}