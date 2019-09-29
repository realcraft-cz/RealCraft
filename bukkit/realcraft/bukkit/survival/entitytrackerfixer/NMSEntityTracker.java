package realcraft.bukkit.survival.entitytrackerfixer;

import net.minecraft.server.v1_14_R1.ChunkProviderServer;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.PlayerChunkMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class NMSEntityTracker {

	private static Method addEntityMethod;
	private static Method removeEntityMethod;

	static {
		try {
			addEntityMethod = getPrivateMethod(PlayerChunkMap.class, "addEntity", new Class[] {Entity.class});
			removeEntityMethod = getPrivateMethod(PlayerChunkMap.class, "removeEntity", new Class[] {Entity.class});
		} catch (NoSuchMethodException | SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public static void trackEntities(ChunkProviderServer cps,Set<Entity> trackList) {
		try {
			for(net.minecraft.server.v1_14_R1.Entity entity : trackList) {
				if(cps.playerChunkMap.trackedEntities.containsKey(entity.getId())) {
					continue;
				}
				addEntityMethod.invoke(cps.playerChunkMap, entity);
			}
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void untrackEntities(ChunkProviderServer cps, Set<net.minecraft.server.v1_14_R1.Entity> untrackList) {
		try {
			for(net.minecraft.server.v1_14_R1.Entity entity : untrackList) {
				removeEntityMethod.invoke(cps.playerChunkMap, entity);
			}
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static Method getPrivateMethod(Class<? extends Object> clazz, String methodName, @SuppressWarnings("rawtypes") Class[] params)
			throws NoSuchMethodException, SecurityException {
		Method method = clazz.getDeclaredMethod(methodName, params);
		method.setAccessible(true);
		return method;
	}
}
