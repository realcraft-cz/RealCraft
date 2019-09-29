package realcraft.bukkit.survival.entitytrackerfixer;

import net.minecraft.server.v1_14_R1.ChunkProviderServer;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.PlayerChunkMap;
import net.minecraft.server.v1_14_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class EntityTrackerFixer {

	private static final int TRACKING_RANGE = 32;
	private static Field trackerField;

	static {
		try {
			trackerField = ReflectionUtils.getField(PlayerChunkMap.EntityTracker.class, true, "tracker");
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public EntityTrackerFixer(){
		Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				EntityTrackerFixer.this.untrackEntities();
			}
		},30*20,30*20);

		Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				EntityTrackerFixer.this.trackEntities();
			}
		},2*20,2*20);
	}

	public void untrackEntities(){
		for(World world : Bukkit.getWorlds()){
			Set<Integer> toRemove = new HashSet<>();
			int removed = 0;
			WorldServer ws = ((CraftWorld)world).getHandle();
			ChunkProviderServer cps = ws.getChunkProvider();

			for(PlayerChunkMap.EntityTracker et : cps.playerChunkMap.trackedEntities.values()) {
				try {
					net.minecraft.server.v1_14_R1.Entity nmsEnt = (net.minecraft.server.v1_14_R1.Entity)trackerField.get(et);
					if(nmsEnt instanceof EntityPlayer){
						continue;
					}
					if(nmsEnt.getBukkitEntity().getCustomName() != null){
						continue;
					}
					boolean remove = false;
					if(et.trackedPlayers.size() == 0){
						remove = true;
					} else if(et.trackedPlayers.size() == 1){
						for(EntityPlayer ep : et.trackedPlayers){
							if(!ep.getBukkitEntity().isOnline()){
								remove = true;
							}
						}
						if(!remove){
							continue;
						}
					}
					if(remove){
						toRemove.add(nmsEnt.getId());
						removed++;
					}
				} catch (IllegalArgumentException | IllegalAccessException e){
					e.printStackTrace();
				}
			}

			for(int id : toRemove) {
				cps.playerChunkMap.trackedEntities.remove(id);
			}
		}
	}

	public void trackEntities(){
		for(World world : Bukkit.getWorlds()){
			WorldServer ws = ((CraftWorld)world).getHandle();
			ChunkProviderServer cps = ws.getChunkProvider();
			Set<net.minecraft.server.v1_14_R1.Entity> trackAgain = new HashSet<>();
			for(Player player : world.getPlayers()) {
				for(Entity ent : player.getNearbyEntities(TRACKING_RANGE, TRACKING_RANGE, TRACKING_RANGE)) {
					if(!cps.playerChunkMap.trackedEntities.containsKey(ent.getEntityId())) {
						trackAgain.add(((CraftEntity)ent).getHandle());
					}
				}
			}
			NMSEntityTracker.trackEntities(cps, trackAgain);
		}
	}
}
