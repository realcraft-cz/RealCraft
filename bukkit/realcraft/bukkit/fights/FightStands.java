package realcraft.bukkit.fights;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.StringUtil;
import ru.beykerykt.lightapi.LightAPI;

public class FightStands implements Listener, Runnable {

	private ArrayList<FightStand> stands = new ArrayList<FightStand>();

	public FightStands(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,20,20);
		this.loadStands();
	}

	@Override
	public void run(){
		for(FightStand stand : stands){
			stand.update(Fights.getFightPlayers(stand.getType()).size());
		}
	}

	@SuppressWarnings("unchecked")
	private void loadStands(){
		List<Map<String, Object>> temps = (List<Map<String, Object>>) Fights.getConfig().get("stands");
		if(temps != null && !temps.isEmpty()){
			for(Map<String, Object> stand : temps){
				FightType type = FightType.fromName(stand.get("type").toString());
				double x = Double.valueOf(stand.get("x").toString());
				double y = Double.valueOf(stand.get("y").toString());
				double z = Double.valueOf(stand.get("z").toString());
				float yaw = Float.valueOf(stand.get("yaw").toString());
				float pitch = Float.valueOf(stand.get("pitch").toString());
				World world = Bukkit.getWorld(stand.get("world").toString());
				if(world == null){
					world = Bukkit.createWorld(new WorldCreator(stand.get("world").toString()));
					if(world == null){
						continue;
					}
				}
				stands.add(new FightStand(type,new Location(world,x,y,z,yaw,pitch)));
			}
		}
	}

	@EventHandler
	public void PlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent event){
		for(FightStand stand : stands){
			if(stand.getType().toString().equals(event.getRightClicked().getCustomName())){
				event.setCancelled(true);
				stand.click(event.getPlayer());
				break;
			}
		}
	}

	@EventHandler
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getEntity().getType() == EntityType.ARMOR_STAND){
			for(FightStand stand : stands){
				if(stand.getType().toString().equals(event.getEntity().getCustomName())){
					if(event.getDamager() instanceof Player){
						event.setCancelled(true);
						stand.click((Player)event.getDamager());
					}
					break;
				}
			}
		}
	}

	@EventHandler
	public void ChunkLoadEvent(ChunkLoadEvent event){
		for(FightStand stand : stands){
			if(stand.isInChunk(event.getChunk())){
				stand.spawn();
			}
		}
	}

	@EventHandler
	public void ChunkUnloadEvent(ChunkUnloadEvent event){
		for(FightStand stand : stands){
			if(stand.isInChunk(event.getChunk())){
				stand.remove();
			}
		}
	}

	private class FightStand {

		private FightType type;
		private Location location;

		private ArmorStand stand;
		private Hologram hologramName;
		private Hologram hologramPlayers;
		private int players = 0;

		public FightStand(FightType type,Location location){
			this.type = type;
			this.location = location;
			this.spawn();
			hologramName = HologramsAPI.createHologram(RealCraft.getInstance(),location.clone().add(0.0,2.9,0.0));
			hologramPlayers = HologramsAPI.createHologram(RealCraft.getInstance(),location.clone().add(0.0,2.6,0.0));

			hologramName.insertTextLine(0,type.getName());
			hologramPlayers.insertTextLine(0,"0 hracu");

			LightAPI.createLight(location.clone().add(0.0,2.0,0.0),15,false);
		}

		public FightType getType(){
			return type;
		}

		public boolean isInChunk(Chunk chunk){
			return (location.getBlockX() >> 4 == chunk.getX() && location.getBlockZ() >> 4 == chunk.getZ());
		}

		public void spawn(){
			this.remove();
			stand = (ArmorStand)location.getWorld().spawnEntity(location,EntityType.ARMOR_STAND);
			stand.setBasePlate(false);
			stand.setArms(true);
			stand.setCustomName(type.toString());
			stand.setCustomNameVisible(false);
			this.equip();
			for(Entity entity : stand.getNearbyEntities(0.5,0.5,0.5)){
				if(entity.getType() == EntityType.ARMOR_STAND){
					entity.remove();
				}
			}
		}

		public void remove(){
			if(stand != null && !stand.isDead()){
				stand.remove();
				stand = null;
			}
		}

		public void update(int players){
			if(this.players != players){
				this.players = players;
				hologramPlayers.removeLine(0);
				hologramPlayers.insertTextLine(0,players+" "+StringUtil.inflect(players,new String[]{"hrac","hraci","hracu"}));
			}
			if(stand == null || stand.isDead()) this.spawn();
		}

		public void click(Player player){
			System.out.println("FightStands: click "+type);
		}

		private void equip(){
			if(type == FightType.PUBLIC){
				stand.getEquipment().setHelmet(new ItemStack(Material.SKULL_ITEM));
				stand.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
				stand.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
				stand.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
				stand.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
			}
			else if(type == FightType.DUEL){
				stand.getEquipment().setHelmet(new ItemStack(Material.SKULL_ITEM));
				stand.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
				stand.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
				stand.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
				stand.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
			}
		}
	}
}