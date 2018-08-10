package realcraft.bukkit.lobby;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.anticheat.AntiCheat;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.utils.Particles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class LobbyElytra implements Listener {

	private static final int JUMP_TIMEOUT = 500;
	private ArrayList<LobbyElytraPlatform> platforms = new ArrayList<>();
	private ArrayList<LobbyElytraBooster> boosters = new ArrayList<>();

	private HashMap<Player,Long> lastJumps = new HashMap<>();

	private FileConfiguration config;

	public LobbyElytra(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		this.loadPlatforms();
		this.loadBoosters();
	}

	private FileConfiguration getConfig(){
		if(config == null){
			File file = new File(RealCraft.getInstance().getDataFolder() + "/elytra.yml");
			if(file.exists()){
				config = new YamlConfiguration();
				try {
					config.load(file);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		return config;
	}

	private void loadPlatforms(){
		for(String key : this.getConfig().getConfigurationSection("platforms").getKeys(false)){
			ConfigurationSection section = this.getConfig().getConfigurationSection("platforms."+key);
			Location location1 = LocationUtil.getConfigLocation(section,"minLoc");
			Location location2 = LocationUtil.getConfigLocation(section,"maxLoc");
			platforms.add(new LobbyElytraPlatform(location1,location2));
		}
	}

	private void loadBoosters(){
		for(String key : this.getConfig().getConfigurationSection("boosters").getKeys(false)){
			ConfigurationSection section = this.getConfig().getConfigurationSection("boosters."+key);
			Location location1 = LocationUtil.getConfigLocation(section,"minLoc");
			Location location2 = LocationUtil.getConfigLocation(section,"maxLoc");
			boosters.add(new LobbyElytraBooster(location1,location2));
		}
	}

	@EventHandler
	public void EntityDamageEvent(EntityDamageEvent event){
		if(event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.ADVENTURE){
			if(!lastJumps.containsKey(player)) lastJumps.put(player,0L);
			if(player.isGliding()){
				if(lastJumps.get(player) + JUMP_TIMEOUT < System.currentTimeMillis()){
					for(LobbyElytraBooster booster : boosters){
						if(booster.isPlayerInside(event.getFrom(),event.getTo())){
							lastJumps.put(player,System.currentTimeMillis());
							player.getWorld().playSound(player.getLocation().add(0,3,0),Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1f,1f);
							for(int i=0;i<10;i++){
								Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
									@Override
									public void run(){
										if(player.isGliding()){
											player.setVelocity(player.getLocation().getDirection().multiply(2));
											Particles.FIREWORKS_SPARK.display(0.2f,0.2f,0.2f,0,4,player.getLocation(),32);
										}
									}
								},i*2);
							}
						}
					}
				}
			} else {
				if(lastJumps.get(player) + JUMP_TIMEOUT < System.currentTimeMillis()){
					for(LobbyElytraPlatform platform : platforms){
						if(platform.isPlayerInside(player)){
							lastJumps.put(player,System.currentTimeMillis());
							player.setVelocity(player.getVelocity().setY(2.5));
							player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
							player.getWorld().playSound(player.getLocation().add(0,5,0),Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1f,1f);
							AntiCheat.exempt(player,2000);
							Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
								@Override
								public void run(){
									if(!player.isGliding()) player.setGliding(true);
								}
							},30);
							for(int i=0;i<30;i++){
								Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
									@Override
									public void run(){
										Particles.FIREWORKS_SPARK.display(0.2f,0.2f,0.2f,0,6,player.getLocation(),32);
									}
								},i);
							}
						}
					}
				}
			}
		}
	}

	private class LobbyElytraPlatform {

		private Location minLoc;
		private Location maxLoc;

		public LobbyElytraPlatform(Location minLoc,Location maxLoc){
			this.minLoc = minLoc;
			this.maxLoc = maxLoc;
		}

		public boolean isPlayerInside(Player player){
			Location location = player.getLocation();
			return (location.getBlockX() >= minLoc.getBlockX() && location.getBlockX() <= maxLoc.getBlockX()
					&& location.getBlockY() >= minLoc.getBlockY() && location.getBlockY() <= maxLoc.getBlockY()
					&& location.getBlockZ() >= minLoc.getBlockZ() && location.getBlockZ() <= maxLoc.getBlockZ());
		}
	}

	private class LobbyElytraBooster {

		private Location minLoc;
		private Location maxLoc;

		public LobbyElytraBooster(Location minLoc,Location maxLoc){
			this.minLoc = minLoc;
			this.maxLoc = maxLoc;
		}

		public boolean isPlayerInside(Location from,Location to){
			Vector R1 = Vector.getMinimum(from.toVector(),to.toVector());
			Vector R2 = Vector.getMaximum(from.toVector(),to.toVector());
			Vector S1 = minLoc.toVector();
			Vector S2 = maxLoc.toVector();
			return this.checkLineBox(R1,R2,S1,S2);
		}

		private boolean checkLineBox(Vector B1,Vector B2,Vector L1,Vector L2){
			if (L2.getX() < B1.getX() && L1.getX() < B1.getX()) return false;
			if (L2.getX() > B2.getX() && L1.getX() > B2.getX()) return false;
			if (L2.getY() < B1.getY() && L1.getY() < B1.getY()) return false;
			if (L2.getY() > B2.getY() && L1.getY() > B2.getY()) return false;
			if (L2.getZ() < B1.getZ() && L1.getZ() < B1.getZ()) return false;
			if (L2.getZ() > B2.getZ() && L1.getZ() > B2.getZ()) return false;
			if (L1.getX() > B1.getX() && L1.getX() < B2.getX() && L1.getY() > B1.getY() && L1.getY() < B2.getY() && L1.getZ() > B1.getZ() && L1.getZ() < B2.getZ()) return true;
			if ((this.getIntersection(L1.getX() - B1.getX(), L2.getX() - B1.getX()))
			|| (this.getIntersection(L1.getY() - B1.getY(), L2.getY() - B1.getY()))
			|| (this.getIntersection(L1.getZ() - B1.getZ(), L2.getZ() - B1.getZ()))
			|| (this.getIntersection(L1.getX() - B2.getX(), L2.getX() - B2.getX()))
			|| (this.getIntersection(L1.getY() - B2.getY(), L2.getY() - B2.getY()))
			|| (this.getIntersection(L1.getZ() - B2.getZ(), L2.getZ() - B2.getZ()))) return true;
			return false;
		}

		private boolean getIntersection(double fDst1,double fDst2){
			if((fDst1 * fDst2) >= 0.0f) return false;
			if(fDst1 == fDst2) return false;
			return true;
		}
	}
}