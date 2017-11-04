package com.mounts;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.cosmetics.Cosmetic;
import com.cosmetics.Cosmetics;
import com.realcraft.RealCraft;

import net.minecraft.server.v1_12_R1.EntityCreature;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public abstract class Mount extends Cosmetic {

	private MountType type;

	HashMap<String,Entity> petEntity = new HashMap<String,Entity>();

	public Mount(MountType type){
		super(type.toString(),CosmeticCategory.MOUNT);
		this.type = type;
		BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run(){
            	for(Player player : Bukkit.getServer().getOnlinePlayers()){
            		if(isRunning(player) && getEntity(player) != null){
            			if(player.getVehicle() == getEntity(player)) onUpdate(player,getEntity(player));
            			else clearCosmetic(player);
            		}
            	}
            }
        };
        runnable.runTaskTimerAsynchronously(RealCraft.getInstance(),0,type.toRepeatDelay());
	}

	public MountType getType(){
		return this.type;
	}

	public Entity getEntity(Player player){
		return petEntity.get(player.getName());
	}

	public void setEntity(Player player,Entity entity){
		petEntity.put(player.getName(),entity);
	}

	public abstract void onCreate(Player player);
	public abstract void onClear(Player player);
	public abstract void onUpdate(Player player,Entity entity);

	@Override
	public void clearCosmetic(Player player){
		this.setRunning(player,false);
		this.onClear(player);
	}

	public void equip(Player player){
		if(!this.isEnabled(player)){
			player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
			return;
		}
		if(player.getLocation().getBlockY() < 60){
			player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
			return;
		}
		player.closeInventory();
		Cosmetics.clearMounts(player);
		this.setRunning(player,true);
		this.onCreate(player);
	}

	public boolean isEnabled(Player player){
		PermissionUser user = PermissionsEx.getUser(player);
		String option = user.getOption(type.toPermission());
		if(player.hasPermission("group.iVIP") || player.hasPermission("group.gVIP") || player.hasPermission("group.dVIP")) return true;
		return (option != null ? Boolean.valueOf(option) : false);
	}

	public void setEnabled(Player player,boolean enabled){
		PermissionUser user = PermissionsEx.getUser(player);
		user.setOption(type.toPermission(),""+enabled);
	}

	public String giveReward(Player player){
		this.setEnabled(player,true);
		return this.getCategory().toString()+" > "+type.toString();
	}

	public void move(Player player){
		Vector vel = player.getLocation().getDirection().setY(0).normalize().multiply(4);
        Location loc = player.getLocation().add(vel);
        try {
        	Mount.move((Creature)this.getEntity(player),loc);
        } catch (Exception e){
        	this.clearCosmetic(player);
        }
	}

	public static void move(Creature entity,Location loc){
        if(entity == null) return;
        EntityCreature ec = ((CraftCreature) (entity)).getHandle();
        ec.P = 1;
        if(loc == null) return;
        ec.getNavigation().a(loc.getX(), loc.getY(), loc.getZ(), (1.0D + 2.0D * 0.5d) * 1.0D);
    }

	@EventHandler
    public void teleportEvent(PlayerTeleportEvent event){
		Player player = event.getPlayer();
		if(this.getEntity(player) != null){
			if ((event.getFrom().getBlockX() != event.getTo().getBlockX()
	                || event.getFrom().getBlockY() != event.getTo().getBlockY()
	                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()
	                || !event.getFrom().getWorld().getName().equalsIgnoreCase(event.getTo().getWorld().getName()))){
				this.clearCosmetic(player);
			}
		}
	}

	@EventHandler
    public void VehicleExitEvent(VehicleExitEvent event){
        if(event.getVehicle() != null && event.getExited() != null && event.getExited() instanceof Player){
        	Player player = (Player) event.getExited();
        	if(this.getEntity(player) != null){
        		this.clearCosmetic(player);
        	}
        }
    }

	public enum MountType {
		INFERNALHORROR, WALKINGDEAD, GLACIALSTEED, SNAKE, NYANSHEEP, PIG, SPIDER, SLIME;

		public String toString(){
			switch(this){
				case INFERNALHORROR: return "§4§lInfernal Horror";
				case WALKINGDEAD: return "§2§lWalking Dead";
				case GLACIALSTEED: return "§b§lGlacial Steed";
				case SNAKE: return "§6§lSnake";
				case NYANSHEEP: return "§4§lNy§6§la§e§ln §a§lSh§b§lee§d§lp";
				case PIG: return "§d§lPiggy";
				case SPIDER: return "§c§lSpider";
				case SLIME: return "§a§lSlime";
			}
			return null;
		}

		public String toPermission(){
			return "cosmetics.mounts."+this.name().toLowerCase();
		}

		public Material toMaterial(){
			switch(this){
				case INFERNALHORROR: return Material.BONE;
				case WALKINGDEAD: return Material.ROTTEN_FLESH;
				case GLACIALSTEED: return Material.PACKED_ICE;
				case SNAKE: return Material.SEEDS;
				case NYANSHEEP: return Material.STAINED_GLASS;
				case PIG: return Material.PORK;
				case SPIDER: return Material.WEB;
				case SLIME: return Material.SLIME_BALL;
			}
			return Material.AIR;
		}

		public Byte toData(){
			switch(this){
				case INFERNALHORROR: return (byte)0;
				case WALKINGDEAD: return (byte)0;
				case GLACIALSTEED: return (byte)0;
				case SNAKE: return (byte)0;
				case NYANSHEEP: return (byte)9;
				case PIG: return (byte)0;
				case SPIDER: return (byte)0;
				case SLIME: return (byte)0;
			}
			return (byte)0;
		}

		public int toRepeatDelay(){
			switch(this){
				case INFERNALHORROR: return 2;
				case WALKINGDEAD: return 2;
				case GLACIALSTEED: return 2;
				case SNAKE: return 2;
				case NYANSHEEP: return 1;
				case PIG: return 2;
				case SPIDER: return 20;
				case SLIME: return 20;
			}
			return 1;
		}

		public EntityType toEntityType(){
			switch(this){
				case INFERNALHORROR: return EntityType.SKELETON_HORSE;
				case WALKINGDEAD: return EntityType.ZOMBIE_HORSE;
				case GLACIALSTEED: return EntityType.HORSE;
				case SNAKE: return EntityType.SHEEP;
				case NYANSHEEP: return EntityType.SHEEP;
				case PIG: return EntityType.PIG;
				case SPIDER: return EntityType.SPIDER;
				case SLIME: return EntityType.SLIME;
			}
			return null;
		}

		public ArrayList<String> toLore(){
			ArrayList<String> lore = new ArrayList<String>();
			switch(this){
			default:
				break;
			}
			lore.clear();
			return lore;
		}
	}
}