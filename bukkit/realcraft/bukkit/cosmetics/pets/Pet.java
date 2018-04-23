package realcraft.bukkit.cosmetics.pets;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Sets;

import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.PathEntity;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.Cosmetic;
import realcraft.bukkit.cosmetics.Cosmetics;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public abstract class Pet extends Cosmetic {

	private PetType type;

	HashMap<String,Entity> petEntity = new HashMap<String,Entity>();

	public Pet(PetType type){
		super(type.toString(),CosmeticCategory.PET);
		this.type = type;
		BukkitRunnable runnable = new BukkitRunnable(){
            @Override
            public void run(){
            	for(Player player : Bukkit.getServer().getOnlinePlayers()){
            		if(isRunning(player) && getEntity(player) != null){
            			follow(player);
            			onUpdate(player,getEntity(player));
            		}
            	}
            }
        };
        runnable.runTaskTimer(RealCraft.getInstance(),0,5);
	}

	public PetType getType(){
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
		player.closeInventory();
		Cosmetics.clearPets(player);
		this.setRunning(player,true);
		this.onCreate(player);
		this.getEntity(player).setCustomName("�f"+player.getName()+"'s "+this.getType().toString());
		this.getEntity(player).setCustomNameVisible(true);
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

	public void follow(Player player){
        if(this.getEntity(player) == null) return;

        Entity petEntity = this.getEntity(player);
        Location targetLocation = player.getLocation();
        try {
        	double speed = this.getType().toSpeed();
        	int distance = (int) player.getLocation().distance(petEntity.getLocation());
        	if(distance > 2.0){
		        ((EntityInsentient) ((CraftEntity)petEntity).getHandle()).getNavigation().a(speed);
		        PathEntity path;
		        path = ((EntityInsentient) ((CraftEntity)petEntity).getHandle()).getNavigation().a(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ());
	            if (distance > 20 && player.isOnGround()){
	                ((CraftEntity)petEntity).getHandle().setLocation(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ(), 0, 0);
	            }
	            if (path != null){
	                ((EntityInsentient) ((CraftEntity)petEntity).getHandle()).getNavigation().a(path, speed);
	                ((EntityInsentient) ((CraftEntity)petEntity).getHandle()).getNavigation().a(speed);
	            }
        	} else {
        		((EntityInsentient) ((CraftEntity)petEntity).getHandle()).getNavigation().a(0);
        		this.clearPathfinders(petEntity);
        	}
        } catch (Exception exception){
        	exception.printStackTrace();
        	//((CraftEntity)petEntity).getHandle().setLocation(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ(), 0, 0);
        }
    }

	public void clearPathfinders(org.bukkit.entity.Entity entity) {
        net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(((EntityInsentient) nmsEntity).goalSelector, Sets.newLinkedHashSet());
            bField.set(((EntityInsentient) nmsEntity).targetSelector, Sets.newLinkedHashSet());
            cField.set(((EntityInsentient) nmsEntity).goalSelector, Sets.newLinkedHashSet());
            cField.set(((EntityInsentient) nmsEntity).targetSelector, Sets.newLinkedHashSet());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

	public enum PetType {
		PIGGY, SHEEP, EASTERBUNNY, COW, KITTY, DOG, CHICK;

		public String toString(){
			switch(this){
				case PIGGY: return "�d�lPiggy";
				case SHEEP: return "�f�lSheep";
				case EASTERBUNNY: return "�6�lEasterBunny";
				case COW: return "�c�lCow";
				case KITTY: return "�9�lKitty";
				case DOG: return "�7�lDog";
				case CHICK: return "�e�lChick";
			}
			return null;
		}

		public String toPermission(){
			return "cosmetics.pets."+this.name().toLowerCase();
		}

		public Material toMaterial(){
			switch(this){
				case PIGGY: return Material.MONSTER_EGG;
				case SHEEP: return Material.WOOL;
				case EASTERBUNNY: return Material.CARROT_ITEM;
				case COW: return Material.MILK_BUCKET;
				case KITTY: return Material.RAW_FISH;
				case DOG: return Material.BONE;
				case CHICK: return Material.EGG;
			}
			return Material.AIR;
		}

		public Byte toData(){
			switch(this){
				case PIGGY: return (byte)90;
				case SHEEP: return (byte)0;
				case EASTERBUNNY: return (byte)0;
				case COW: return (byte)0;
				case KITTY: return (byte)0;
				case DOG: return (byte)0;
				case CHICK: return (byte)0;
			}
			return (byte)0;
		}

		public double toSpeed(){
			return 1.05D;
		}

		public EntityType toEntityType(){
			switch(this){
				case PIGGY: return EntityType.PIG;
				case SHEEP: return EntityType.SHEEP;
				case EASTERBUNNY: return EntityType.RABBIT;
				case COW: return EntityType.COW;
				case KITTY: return EntityType.OCELOT;
				case DOG: return EntityType.WOLF;
				case CHICK: return EntityType.CHICKEN;
			}
			return null;
		}

		public ArrayList<String> toLore(){
			ArrayList<String> lore = new ArrayList<String>();
			switch(this){
			case PIGGY:
				lore.add("�7Lorem ipsum dolor sit amet");
				lore.add("�7consectetur adipiscing elit.");
				break;
			case SHEEP:
				lore.add("�7Lorem ipsum dolor sit amet");
				lore.add("�7consectetur adipiscing elit.");
				break;
			case CHICK:
				lore.add("�7Lorem ipsum dolor sit amet");
				lore.add("�7consectetur adipiscing elit.");
				break;
			case COW:
				lore.add("�7Lorem ipsum dolor sit amet");
				lore.add("�7consectetur adipiscing elit.");
				break;
			case DOG:
				lore.add("�7Lorem ipsum dolor sit amet");
				lore.add("�7consectetur adipiscing elit.");
				break;
			case EASTERBUNNY:
				lore.add("�7Lorem ipsum dolor sit amet");
				lore.add("�7consectetur adipiscing elit.");
				break;
			case KITTY:
				lore.add("�7Lorem ipsum dolor sit amet");
				lore.add("�7consectetur adipiscing elit.");
				break;
			default:
				break;
			}
			lore.clear();
			return lore;
		}
	}
}