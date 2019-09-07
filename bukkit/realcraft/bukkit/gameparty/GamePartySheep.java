package realcraft.bukkit.gameparty;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.minecraft.server.v1_14_R1.*;
import net.minecraft.server.v1_14_R1.World;
import org.bukkit.*;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.utils.StringUtil;
import ru.beykerykt.lightapi.LightAPI;

public class GamePartySheep implements Listener {

	private Location location;
	private CustomSheep sheep;
	private Hologram hologramName;
	private int players = 0;

	public GamePartySheep(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		location = LocationUtil.getConfigLocation(RealCraft.getInstance().getConfig(),"gameparty.sheep");
		hologramName = HologramsAPI.createHologram(RealCraft.getInstance(),location.clone().add(0.0,2.2,0.0));
		hologramName.insertTextLine(0,"§d§lParty");
		hologramName.insertTextLine(1,"0 hracu");
		LightAPI.createLight(location.clone().add(0.0,2.0,0.0),15,false);
	}

	public boolean isInChunk(Chunk chunk){
		return (location.getBlockX() >> 4 == chunk.getX() && location.getBlockZ() >> 4 == chunk.getZ());
	}

	public void spawn(){
		this.remove();
		sheep = new CustomSheep(((CraftWorld)location.getWorld()).getHandle());
		sheep.setPositionRotation(location.getX(),location.getY(),location.getZ(),location.getYaw(),location.getPitch());
    	((CraftLivingEntity) sheep.getBukkitEntity()).setRemoveWhenFarAway(false);
    	((CraftWorld)location.getWorld()).getHandle().addEntity(sheep,SpawnReason.CUSTOM);
		sheep.setInvulnerable(false);
		sheep.setNoAI(true);
		((Sheep)sheep.getBukkitEntity()).setColor(this.toDyeColor(ChatColor.getByChar(GameParty.getServer().getColor().charAt(1))));
		for(Entity entity : location.getWorld().getEntities()){
			if(entity.getType() == EntityType.SHEEP && entity.getEntityId() != sheep.getBukkitEntity().getEntityId() && entity.getLocation().distanceSquared(location) < 4){
				entity.remove();
			}
		}
	}

	public void remove(){
		if(sheep != null && !sheep.getBukkitEntity().isDead()){
			sheep.getBukkitEntity().remove();
			sheep = null;
		}
	}

	public void update(){
		if(sheep != null) ((Sheep)sheep.getBukkitEntity()).setColor(this.toDyeColor(ChatColor.getByChar(GameParty.getServer().getColor().charAt(1))));
		this.update(GameParty.getUsers().size());
	}

	public void update(int players){
		if(this.players != players){
			this.players = players;
			hologramName.removeLine(1);
			hologramName.insertTextLine(1,players+" "+StringUtil.inflect(players,new String[]{"hrac","hraci","hracu"}));
		}
		if(sheep == null || sheep.getBukkitEntity().isDead()){
			this.spawn();
		}
	}

	public void click(Player player){
		GameParty.addUser(Users.getUser(player));
	}

	@EventHandler
	public void ChunkLoadEvent(ChunkLoadEvent event){
		if(this.isInChunk(event.getChunk())){
			this.spawn();
		}
	}

	@EventHandler
	public void ChunkUnloadEvent(ChunkUnloadEvent event){
		if(this.isInChunk(event.getChunk())){
			this.remove();
		}
	}

	@EventHandler
	public void EntityDamageEvent(EntityDamageEvent event){
		if(event.getEntity().getType() == EntityType.SHEEP && sheep != null && sheep.getBukkitEntity().getEntityId() == event.getEntity().getEntityId()){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
		if(event.getHand().equals(EquipmentSlot.HAND) && event.getRightClicked().getType() == EntityType.SHEEP && sheep != null && sheep.getBukkitEntity().getEntityId() == event.getRightClicked().getEntityId()){
			event.setCancelled(true);
			this.click(event.getPlayer());
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getEntity().getType() == EntityType.SHEEP && sheep != null && sheep.getBukkitEntity().getEntityId() == event.getEntity().getEntityId()){
			event.setCancelled(true);
			if(event.getDamager() instanceof Player){
				this.click((Player)event.getDamager());
			}
		}
	}

	public DyeColor toDyeColor(ChatColor color){
		if(color == ChatColor.YELLOW) return DyeColor.YELLOW;
		if(color == ChatColor.AQUA) return DyeColor.LIGHT_BLUE;
		if(color == ChatColor.GREEN) return DyeColor.GREEN;
		if(color == ChatColor.RED) return DyeColor.RED;
		if(color == ChatColor.BLUE) return DyeColor.BLUE;
		if(color == ChatColor.LIGHT_PURPLE) return DyeColor.PURPLE;
		if(color == ChatColor.GOLD) return DyeColor.ORANGE;
		if(color == ChatColor.DARK_AQUA) return DyeColor.CYAN;
		return DyeColor.WHITE;
	}

	private class CustomSheep extends EntitySheep {

		public CustomSheep(World world){
			super(EntityTypes.SHEEP,world);
		}

		/*public CustomSheep(World world){
			EntityTypes<? extends EntitySheep> entitytypes = null;
			super(entitytypes,world);
		}*/

		@Override
		public void a(SoundEffect soundeffect,float f,float f1){
		}

		@Override
		public NBTTagCompound save(NBTTagCompound nbttagcompound){
			return null;
		}
	}
}