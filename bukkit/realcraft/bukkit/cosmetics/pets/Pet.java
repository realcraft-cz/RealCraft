package realcraft.bukkit.cosmetics.pets;

import com.google.common.collect.Sets;
import net.minecraft.server.v1_13_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.CosmeticPlayer;
import realcraft.bukkit.cosmetics.Cosmetics;
import realcraft.bukkit.cosmetics.cosmetic.Cosmetic;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.utils.ItemUtil;
import realcraft.bukkit.utils.Particles;
import realcraft.bukkit.utils.RandomUtil;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Pet extends Cosmetic {

	private static HashMap<CosmeticPlayer,PetEntry> pets = new HashMap<>();

	public Pet(CosmeticType type){
		super(type);
		Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				for(Player player : Bukkit.getServer().getOnlinePlayers()){
					if(Pet.this.isRunning(player)){
						Pet.this.getPetEntry(player).run();
					}
				}
			}
		},5,5);
		Bukkit.getScheduler().runTaskTimerAsynchronously(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				for(Player player : Bukkit.getServer().getOnlinePlayers()){
					if(Pet.this.isRunning(player)){
						Pet.this.getPetEntry(player).effect();
					}
				}
			}
		},3,3);
	}

	@Override
	public void run(Player player){
		this.getPetEntry(player).create();
	}

	@Override
	public void clear(Player player){
		this.getPetEntry(player).remove();
		pets.remove(Cosmetics.getCosmeticPlayer(player));
	}

	public PetEntry getPetEntry(Player player){
		if(!pets.containsKey(Cosmetics.getCosmeticPlayer(player))) pets.put(Cosmetics.getCosmeticPlayer(player),new PetEntry(player));
		return pets.get(Cosmetics.getCosmeticPlayer(player));
	}

	@Override
	public ItemStack getItemStack(){
		ItemStack item = ItemUtil.getHead(this.getTexture());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.getType().getName());
		item.setItemMeta(meta);
		return item;
	}

	private String getTexture(){
		switch(this.getType()){
			case PET_CHICKEN:     return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTYzODQ2OWE1OTljZWVmNzIwNzUzNzYwMzI0OGE5YWIxMWZmNTkxZmQzNzhiZWE0NzM1YjM0NmE3ZmFlODkzIn19fQ==";
			case PET_PIG:         return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0=";
			case PET_SHEEP:       return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjMxZjljY2M2YjNlMzJlY2YxM2I4YTExYWMyOWNkMzNkMThjOTVmYzczZGI4YTY2YzVkNjU3Y2NiOGJlNzAifX19";
			case PET_COW:         return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ2YzZlZGE5NDJmN2Y1ZjcxYzMxNjFjNzMwNmY0YWVkMzA3ZDgyODk1ZjlkMmIwN2FiNDUyNTcxOGVkYzUifX19";
			case PET_OCELOT:      return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY1N2NkNWMyOTg5ZmY5NzU3MGZlYzRkZGNkYzY5MjZhNjhhMzM5MzI1MGMxYmUxZjBiMTE0YTFkYjEifX19";
			case PET_CAT:         return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVjOTVjMWYyYTUwYjM3ZDYxMjZmNzZlNDYxOGE0Y2M3OTI3OWE4Yzg0NDM3MjA1NGRjM2IyMmVhMWNlMjcifX19";
			case PET_WOLF:        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTk1Y2JiNGY3NWVhODc2MTdmMmY3MTNjNmQ0OWRhYzMyMDliYTFiZDRiOTM2OTY1NGIxNDU5ZWExNTMxNyJ9fX0=";
			case PET_RABBIT:      return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM3YTMxN2VjNWMxZWQ3Nzg4Zjg5ZTdmMWE2YWYzZDJlZWI5MmQxZTk4NzljMDUzNDNjNTdmOWQ4NjNkZTEzMCJ9fX0=";
			case PET_KOALA:       return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2EzNWViMTBiOTRlODg4NDI3ZmIyM2M3ODMwODI2NThjZWI4MWYzY2Y1ZDBhYWQyNWQ3ZDQxYTE5NGIyNiJ9fX0=";
			case PET_MONKEY:      return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQyOWNhOWM2YTJlOGJiMTYyNzU3ZjU0M2FkYjYyZWY1OGY2NjVkNGUwZGZjY2U1ZGFmNThkMjhmZDlkZmIifX19";
			case PET_POLARBEAR:   return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ2ZDIzZjA0ODQ2MzY5ZmEyYTM3MDJjMTBmNzU5MTAxYWY3YmZlODQxOTk2NjQyOTUzM2NkODFhMTFkMmIifX19";
			case PET_PENGUIN:     return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDNjNTdmYWNiYjNhNGRiN2ZkNTViNWMwZGM3ZDE5YzE5Y2IwODEzYzc0OGNjYzk3MTBjNzE0NzI3NTUxZjViOSJ9fX0=";
			case PET_WALRUS:      return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdiYWVkYWY5YWQ5NTQ3NGViMWJlNTg5MjQ0NDVkZmM3N2JiZGMyNTJjYzFjODE2NDRjZjcxNTRjNDQxIn19fQ==";
			case PET_SQUID:       return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDE0MzNiZTI0MjM2NmFmMTI2ZGE0MzRiODczNWRmMWViNWIzY2IyY2VkZTM5MTQ1OTc0ZTljNDgzNjA3YmFjIn19fQ==";
			case PET_TIGER:       return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjZiOTZiYmM4YWQ5YmFlMGUyNTRkMzVmZGZiMWRiNDhlODIyZWQ5N2NmNWY3MzlkM2U5NTQ1ZGQ2Y2UifX19";
			case PET_PANDA:       return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDE4OGM5ODBhYWNmYTk0Y2YzMzA4ODUxMmIxYjk1MTdiYTgyNmIxNTRkNGNhZmMyNjJhZmY2OTc3YmU4YSJ9fX0=";
			case PET_CLOWNFISH:   return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzZkMTQ5ZTRkNDk5OTI5NjcyZTI3Njg5NDllNjQ3Nzk1OWMyMWU2NTI1NDYxM2IzMjdiNTM4ZGYxZTRkZiJ9fX0=";
			case PET_BIRD:        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk2MjczNzBmZWRiZDBiYWU3YmFlNmQ2Zjg1ODM1NTU3NjM3ODljMWJkOTNmYTYzOWNmYTNkZmQ0OGUzNDg1MCJ9fX0=";
			case PET_BEE:         return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQ3MzIyZjgzMWUzYzE2OGNmYmQzZTI4ZmU5MjUxNDRiMjYxZTc5ZWIzOWM3NzEzNDlmYWM1NWE4MTI2NDczIn19fQ==";
			case PET_FISH:        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmY5OWI1ODBkNDVhNzg0ZTdhOTY0ZTdkM2IxZjk3Y2VjZTc0OTExMTczYmQyMWMxZDdjNTZhY2RjMzg1ZWQ1In19fQ==";
			case PET_SALMONFISH:  return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWRmYzU3ZDA5MDU5ZTQ3OTlmYTkyYzE1ZTI4NTEyYmNmYWExMzE1NTc3ZmUzYTI3YWVkMzg5ZTRmNzUyMjg5YSJ9fX0=";
			case PET_TORTOISE:    return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTJlNTQ4NDA4YWI3NWQ3ZGY4ZTZkNWQyNDQ2ZDkwYjZlYzYyYWE0ZjdmZWI3OTMwZDFlZTcxZWVmZGRmNjE4OSJ9fX0=";
			case PET_SEAGULL:     return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNiZGU0MzExMWY2OWE3ZmRhNmVjNmZhZjIyNjNjODI3OTYxZjM5MGQ3YzYxNjNlZDEyMzEwMzVkMWIwYjkifX19";
			case PET_FERRET:      return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM2ZWRmN2RlOWFkY2E3MjMwOGE5NGQxYzM4YzM1OGFjYzgyOTE4ZmU4ZmNlZDI1ZDQ3NDgyMGY0Y2I3ODQifX19";
			case PET_ELEPHANT:    return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzA3MWE3NmY2NjlkYjVlZDZkMzJiNDhiYjJkYmE1NWQ1MzE3ZDdmNDUyMjVjYjMyNjdlYzQzNWNmYTUxNCJ9fX0=";
			case PET_FURBY:       return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2JmZjUyNzU2Mjg4OWUxNmE1NDRmMmY5OTZmYmEzZDk1NDFkMGFhY2Y4MTQ2MmJmZmM5ZmI1Y2FkOGFlZGQ1In19fQ==";
			case PET_BLAZE:       return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc4ZWYyZTRjZjJjNDFhMmQxNGJmZGU5Y2FmZjEwMjE5ZjViMWJmNWIzNWE0OWViNTFjNjQ2Nzg4MmNiNWYwIn19fQ==";
			case PET_GHAST:       return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGI2YTcyMTM4ZDY5ZmJiZDJmZWEzZmEyNTFjYWJkODcxNTJlNGYxYzk3ZTVmOTg2YmY2ODU1NzFkYjNjYzAifX19";
			case PET_ENDERMAN:    return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0=";
			case PET_LAVASLIME:   return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzg5NTdkNTAyM2M5MzdjNGM0MWFhMjQxMmQ0MzQxMGJkYTIzY2Y3OWE5ZjZhYjM2Yjc2ZmVmMmQ3YzQyOSJ9fX0=";
			case PET_SLIME:       return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZhZDIwZmMyZDU3OWJlMjUwZDNkYjY1OWM4MzJkYTJiNDc4YTczYTY5OGI3ZWExMGQxOGM5MTYyZTRkOWI1In19fQ==";
			case PET_GUARDIAN:    return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTMyYzI0NTI0YzgyYWIzYjNlNTdjMjA1MmM1MzNmMTNkZDhjMGJlYjhiZGQwNjM2OWJiMjU1NGRhODZjMTIzIn19fQ==";
			case PET_WITHERBOSS:  return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RmNzRlMzIzZWQ0MTQzNjk2NWY1YzU3ZGRmMjgxNWQ1MzMyZmU5OTllNjhmYmI5ZDZjZjVjOGJkNDEzOWYifX19";
			case PET_ENDERDRAGON: return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlY2MwNDA3ODVlNTQ2NjNlODU1ZWYwNDg2ZGE3MjE1NGQ2OWJiNGI3NDI0YjczODFjY2Y5NWIwOTVhIn19fQ==";
		}
		return null;
	}

	private class PetEntry implements Listener {

		private Zombie entity;
		private Player player;
		private PetState state = PetState.FOLLOW;
		private int tick = 0;
		private long followTimeout = 0;
		private long arrivedTimeout = 0;
		private long sitTimeout = 0;
		private long leftClickTimeout = 0;
		private long rightClickTimeout = 0;

		public PetEntry(Player player){
			this.player = player;
			Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		}

		private Zombie getEntity(){
			return entity;
		}

		private void create(){
			Location location = player.getLocation();
			location.setPitch(0f);
			location.add(location.getDirection().setY(0).normalize().multiply(1.5));
			CustomPetEntity zombie = new CustomPetEntity(((CraftWorld)location.getWorld()).getHandle());
			((CraftWorld)location.getWorld()).getHandle().addEntity(zombie,CreatureSpawnEvent.SpawnReason.CUSTOM);
			entity = (Zombie)zombie.getBukkitEntity();
			entity.teleport(location);
			entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_VEX_CHARGE,1f,1f);
			if(entity != null){
				entity.setSilent(true);
				entity.setBaby(true);
				entity.getEquipment().clear();
				entity.getEquipment().setHelmet(Pet.this.getItemStack());
				entity.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
				entity.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
				entity.getEquipment().setItemInMainHandDropChance(0);
				entity.getEquipment().setItemInOffHandDropChance(0);
				entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,Integer.MAX_VALUE,1));
				((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(0);
				this.clearPathfinders(entity);
			}
			else Pet.this.setRunning(player,false);
		}

		private void remove(){
			HandlerList.unregisterAll(this);
			if(entity != null && !entity.isDead()){
				entity.remove();
				entity = null;
			}
		}

		private boolean canBeFriend(){
			return (state != PetState.SITTING && followTimeout < System.currentTimeMillis());
		}

		private void run(){
			tick ++;
			if(tick == 3) tick = 0;
			if(entity != null && entity.isDead()){
				Pet.this.setRunning(player,false);
				return;
			}
			if(state == PetState.FOLLOW){
				PetEntry target = this.getNearestTarget();
				if(target != null && this.canBeFriend() && target.getEntity().getLocation().distanceSquared(entity.getLocation()) <= 5*5){
					state = PetState.FRIEND;
				}
				else this.followOwner();
			}
			else if(state == PetState.FRIEND){
				PetEntry target = this.getNearestTarget();
				if(target != null && this.canBeFriend() && target.getEntity().getLocation().distanceSquared(entity.getLocation()) <= 5*5){
					double distance = entity.getLocation().distanceSquared(target.getEntity().getLocation());
					if(this.getDistanceToOwner() > 5*5){
						followTimeout = System.currentTimeMillis()+2000;
						this.followOwner();
						return;
					}
					if(tick == 0){
						Location targetLocation = target.getEntity().getLocation();
						if(distance <= 2*2) targetLocation.add(RandomUtil.getRandomDouble(-2.0,2.0),0,RandomUtil.getRandomDouble(-2.0,2.0));
						try {
							double speed = (distance > 2*2 ? 0.7 : 0.5);
							((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
							PathEntity path;
							path = ((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(targetLocation.getX(),targetLocation.getY(),targetLocation.getZ());
							if(path != null){
								((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(path,speed);
								((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
							}
						} catch (Exception exception){
						}
					}
					if(tick == 0 && distance <= 1*1){
						if(RandomUtil.getRandomBoolean()) Particles.HEART.display(0f,0f,0f,0,1,entity.getLocation().add(0f,1f,0f),64);
					}
				} else {
					state = PetState.FOLLOW;
				}
			}
			else if(state == PetState.SITTING){
				if(this.getDistanceToOwner() < 4*4){
					Location lookLocation = entity.getLocation().clone().setDirection(player.getLocation().subtract(entity.getLocation()).toVector());
					if(lookLocation.getPitch() < -45) lookLocation.setPitch(-45);
					else if(lookLocation.getPitch() > 45) lookLocation.setPitch(45);
					entity.teleport(lookLocation);
				} else {
					if(entity.getLocation().getPitch() > 1 || entity.getLocation().getPitch() < -1){
						Location lookLocation = entity.getLocation().clone();
						lookLocation.setPitch(0);
						entity.teleport(lookLocation);
					}
				}
			}
		}

		private double getDistanceToOwner(){
			return player.getLocation().distanceSquared(entity.getLocation());
		}

		private PetEntry getNearestTarget(){
			PetEntry target = null;
			double distance = Integer.MAX_VALUE;
			double tmpDist;
			for(PetEntry pokemon : pets.values()){
				if(pokemon.canBeFriend()){
					tmpDist = pokemon.getEntity().getLocation().distanceSquared(entity.getLocation());
					if(pokemon != this && tmpDist < distance){
						target = pokemon;
						distance = tmpDist;
					}
				}
			}
			return target;
		}

		private void followOwner(){
			Location targetLocation = player.getLocation();
			state = PetState.FOLLOW;
			try {
				double speed = 1D;
				double distance = this.getDistanceToOwner();
				if(distance > 4*4){
					((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
					PathEntity path;
					path = ((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(targetLocation.getX(),targetLocation.getY(),targetLocation.getZ());
					if(distance > 32*32 && player.isOnGround()){
						((CraftEntity)entity).getHandle().setLocation(targetLocation.getBlockX(),targetLocation.getBlockY(),targetLocation.getBlockZ(),0,0);
					}
					if(path != null){
						((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(path,speed);
						((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
						arrivedTimeout = System.currentTimeMillis()+600;
					}
				} else {
					if(arrivedTimeout >= System.currentTimeMillis()){
						((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(0);
						if(tick == 0) entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_GHAST_AMBIENT,1f,2f);
					} else {
						speed = 0.5;
						if(tick == 0 && RandomUtil.getRandomBoolean()){
							((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
							targetLocation.add(RandomUtil.getRandomDouble(-4.0,4.0),0,RandomUtil.getRandomDouble(-4.0,4.0));
							PathEntity path;
							path = ((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(targetLocation.getX(),targetLocation.getY(),targetLocation.getZ());
							if(path != null){
								((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(path,speed);
								((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
							}
						}
					}
				}
			} catch (Exception exception){
			}
		}

		private void clearPathfinders(org.bukkit.entity.Entity entity){
			net.minecraft.server.v1_13_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
			try {
				Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
				bField.setAccessible(true);
				Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
				cField.setAccessible(true);
				((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(0);
				bField.set(((EntityInsentient) nmsEntity).goalSelector,Sets.newLinkedHashSet());
				bField.set(((EntityInsentient) nmsEntity).targetSelector,Sets.newLinkedHashSet());
				cField.set(((EntityInsentient) nmsEntity).goalSelector,Sets.newLinkedHashSet());
				cField.set(((EntityInsentient) nmsEntity).targetSelector,Sets.newLinkedHashSet());
			} catch (Exception e){
				e.printStackTrace();
			}
		}

		private void effect(){
			if(entity == null || entity.isDead()) return;
			if(state != PetState.SITTING){
				Particles.CLOUD.display(0.0f,0f,0.0f,0f,4,entity.getLocation().add(0,0.7,0),64);
			}
		}

		@EventHandler
		private void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
			if(event.getHand().equals(EquipmentSlot.HAND) && event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR && event.getRightClicked().equals(entity)){
				event.setCancelled(true);
				if(rightClickTimeout < System.currentTimeMillis()){
					this.rightClick(event.getPlayer());
				}
			}
		}

		@EventHandler
		public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
			if(event.getEntity().equals(entity)){
				event.setCancelled(true);
				if(leftClickTimeout < System.currentTimeMillis()) this.leftClick(event.getDamager());
			}
		}

		@EventHandler
		public void EntityDamageEvent(EntityDamageEvent event){
			if(event.getEntity().equals(entity)){
				event.setCancelled(true);
			}
		}

		@EventHandler
		public void ChunkUnloadEvent(ChunkUnloadEvent event){
			for(Entity entity : event.getChunk().getEntities()){
				if(entity.equals(entity)){
					Pet.this.setRunning(player,false);
					break;
				}
			}
		}

		private void rightClick(Entity damager){
			if(damager instanceof Player && damager.equals(player) && this.getDistanceToOwner() < 3*3 && sitTimeout < System.currentTimeMillis()){
				rightClickTimeout = System.currentTimeMillis()+100;
				if(state == PetState.FOLLOW || state == PetState.FRIEND){
					state = PetState.SITTING;
					sitTimeout = System.currentTimeMillis()+500;
					entity.setAI(false);
					entity.setGravity(false);
					entity.setVelocity(new Vector(0,0,0));
					entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_BAT_HURT,0.2f,1f);
					Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
						public void run(){
							((CraftEntity)entity).getHandle().setLocation(entity.getLocation().getX(),entity.getLocation().getY()-0.7,entity.getLocation().getZ(),0,0);
						}
					});
					this.clearPathfinders(entity);
				}
				else if(state == PetState.SITTING){
					state = PetState.FOLLOW;
					sitTimeout = System.currentTimeMillis()+500;
					entity.setAI(true);
					entity.setGravity(true);
					entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_BAT_HURT,0.2f,1f);
					Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
						public void run(){
							((CraftEntity)entity).getHandle().setLocation(entity.getLocation().getX(),entity.getLocation().getY()+1.0,entity.getLocation().getZ(),0,0);
							entity.setVelocity(new Vector(0,0,0));
						}
					});
					this.clearPathfinders(entity);
				}
			}
		}

		private void leftClick(Entity damager){
			if(damager instanceof Player && damager.equals(player) && this.getDistanceToOwner() < 3*3){
				player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ITEM_PICKUP,1f,1f);
				Particles.SPELL_WITCH.display(0.2f,0.2f,0.2f,0.5f,8,entity.getLocation().add(0f,0.9f,0f),64);
				Pet.this.setEnabled(player,false);
			}
		}
	}

	private enum PetState {
		FOLLOW, FRIEND, SITTING;
	}

	private class CustomPetEntity extends EntityZombie {

		public CustomPetEntity(World world){
			super(world);
		}

		@Override
		protected boolean isTypeNotPersistent(){
			return false;
		}

		@Override
		public NBTTagCompound save(NBTTagCompound nbttagcompound){
			return null;
		}
	}
}