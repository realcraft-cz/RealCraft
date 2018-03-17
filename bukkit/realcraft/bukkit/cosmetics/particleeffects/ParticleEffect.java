package realcraft.bukkit.cosmetics.particleeffects;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.Cosmetic;
import realcraft.bukkit.cosmetics.Cosmetics;
import realcraft.bukkit.utils.Particles;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public abstract class ParticleEffect extends Cosmetic {

	private ParticleEffectType type;

	HashMap<String,Boolean> isMoving = new HashMap<String,Boolean>();

	public ParticleEffect(ParticleEffectType type){
		super(type.toString(),CosmeticCategory.PARTICLEEFFECT);
		this.type = type;
		BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run(){
            	for(Player player : Bukkit.getServer().getOnlinePlayers()){
            		if(isRunning(player)){
            			onUpdate(player,isMoving(player));
            		}
            	}
            }
        };
        runnable.runTaskTimerAsynchronously(RealCraft.getInstance(),0,type.toRepeatDelay());
	}

	public ParticleEffectType getType(){
		return this.type;
	}

	public abstract void onUpdate(Player player,boolean moving);

	@Override
	public void clearCosmetic(Player player){
		this.setRunning(player,false);
	}

	public void equip(Player player){
		if(!this.isEnabled(player)){
			player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
			return;
		}
		player.closeInventory();
		Cosmetics.clearParticleEffects(player);
		this.setRunning(player,true);
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

	public boolean isMoving(Player player){
		return (isMoving.containsKey(player.getName()) && isMoving.get(player.getName()) == true);
	}

	public void setMoving(Player player,boolean moving){
		isMoving.put(player.getName(),moving);
	}

	@EventHandler
    public void onMove(PlayerMoveEvent event) {
		if(isRunning(event.getPlayer())){
			if((event.getFrom().getX() != event.getTo().getX() || event.getFrom().getY() != event.getTo().getY() || event.getFrom().getZ() != event.getTo().getZ())){
				setMoving(event.getPlayer(),true);
			} else {
				setMoving(event.getPlayer(),false);
			}
		}
    }

	public enum ParticleEffectType {
		RAINCLOUD, SNOWCLOUD, BLOODHELIX, FROSTLORD, FLAMERINGS, INLOVE, GREENSPARKS, FROZENWALK, MUSIC, ENCHANTED, INFERNO, ANGELWINGS, SUPERHERO, SANTAHAT, CRUSHEDCANDYCANE, ENDERAURA, FLAMEFAIRY;

		public String toString(){
			switch(this){
				case RAINCLOUD: return "§9§lRain Cloud";
				case SNOWCLOUD: return "§f§lSnow Cloud";
				case BLOODHELIX: return "§4§lBlood Helix";
				case FROSTLORD: return "§b§lFrost Lord";
				case FLAMERINGS: return "§c§lFlame Rings";
				case INLOVE: return "§c§lIn Love";
				case GREENSPARKS: return "§a§lGreen Sparks";
				case FROZENWALK: return "§b§lFrozen Walk";
				case MUSIC: return "§9§lMusic";
				case ENCHANTED: return "§7§lEnchanted";
				case INFERNO: return "§4§lInferno";
				case ANGELWINGS: return "§f§lAngel Wings";
				case SUPERHERO: return "§4§lSuper Hero";
				case SANTAHAT: return "§4§lSanta §f§lHat";
				case CRUSHEDCANDYCANE: return "§4§lCrushed §f§lCandy §4§lCane";
				case ENDERAURA: return "§d§lEnder Aura";
				case FLAMEFAIRY: return "§6§lFlame Fairy";
			}
			return null;
		}

		public String toPermission(){
			return "cosmetics.effects."+this.name().toLowerCase();
		}

		public Material toMaterial(){
			switch(this){
				case RAINCLOUD: return Material.INK_SACK;
				case SNOWCLOUD: return Material.SNOW_BALL;
				case BLOODHELIX: return Material.REDSTONE;
				case FROSTLORD: return Material.PACKED_ICE;
				case FLAMERINGS: return Material.BLAZE_POWDER;
				case INLOVE: return Material.RED_ROSE;
				case GREENSPARKS: return Material.EMERALD;
				case FROZENWALK: return Material.SNOW_BALL;
				case MUSIC: return Material.RECORD_7;
				case ENCHANTED: return Material.BOOK;
				case INFERNO: return Material.NETHER_STALK;
				case ANGELWINGS: return Material.FEATHER;
				case SUPERHERO: return Material.GLOWSTONE_DUST;
				case SANTAHAT: return Material.BEACON;
				case CRUSHEDCANDYCANE: return Material.INK_SACK;
				case ENDERAURA: return Material.EYE_OF_ENDER;
				case FLAMEFAIRY: return Material.BLAZE_POWDER;
			}
			return Material.AIR;
		}

		public Byte toData(){
			switch(this){
				case RAINCLOUD: return (byte)0;
				case SNOWCLOUD: return (byte)0;
				case BLOODHELIX: return (byte)0;
				case FROSTLORD: return (byte)0;
				case FLAMERINGS: return (byte)0;
				case INLOVE: return (byte)0;
				case GREENSPARKS: return (byte)0;
				case FROZENWALK: return (byte)0;
				case MUSIC: return (byte)0;
				case ENCHANTED: return (byte)0;
				case INFERNO: return (byte)0;
				case ANGELWINGS: return (byte)0;
				case SUPERHERO: return (byte)0;
				case SANTAHAT: return (byte)0;
				case CRUSHEDCANDYCANE: return (byte)0;
				case ENDERAURA: return (byte)0;
				case FLAMEFAIRY: return (byte)0;
			}
			return (byte)0;
		}

		public Particles getEffect(){
			switch(this){
				case RAINCLOUD: return Particles.DRIP_WATER;
				case SNOWCLOUD: return Particles.SNOW_SHOVEL;
				case BLOODHELIX: return Particles.REDSTONE;
				case FROSTLORD: return Particles.SNOW_SHOVEL;
				case FLAMERINGS: return Particles.FLAME;
				case INLOVE: return Particles.HEART;
				case GREENSPARKS: return Particles.VILLAGER_HAPPY;
				case FROZENWALK: return Particles.SNOW_SHOVEL;
				case MUSIC: return Particles.FLAME;
				case ENCHANTED: return Particles.ENCHANTMENT_TABLE;
				case INFERNO: return Particles.FLAME;
				case ANGELWINGS: return Particles.REDSTONE;
				case SUPERHERO: return Particles.REDSTONE;
				case SANTAHAT: return Particles.REDSTONE;
				case CRUSHEDCANDYCANE: return Particles.ITEM_CRACK;
				case ENDERAURA: return Particles.PORTAL;
				case FLAMEFAIRY: return Particles.FLAME;
			}
			return null;
		}

		public int toRepeatDelay(){
			switch(this){
				case RAINCLOUD: return 1;
				case SNOWCLOUD: return 1;
				case BLOODHELIX: return 1;
				case FROSTLORD: return 1;
				case FLAMERINGS: return 1;
				case INLOVE: return 4;
				case GREENSPARKS: return 1;
				case FROZENWALK: return 1;
				case MUSIC: return 6;
				case ENCHANTED: return 4;
				case INFERNO: return 1;
				case ANGELWINGS: return 2;
				case SUPERHERO: return 2;
				case SANTAHAT: return 2;
				case CRUSHEDCANDYCANE: return 1;
				case ENDERAURA: return 1;
				case FLAMEFAIRY: return 1;
			}
			return 1;
		}

		public ArrayList<String> toLore(){
			ArrayList<String> lore = new ArrayList<String>();
			switch(this){
			case RAINCLOUD:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case ANGELWINGS:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case BLOODHELIX:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case CRUSHEDCANDYCANE:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case ENCHANTED:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case ENDERAURA:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case FLAMEFAIRY:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case FLAMERINGS:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case FROSTLORD:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case FROZENWALK:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case GREENSPARKS:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case INFERNO:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case INLOVE:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case MUSIC:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case SANTAHAT:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case SNOWCLOUD:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			case SUPERHERO:
				lore.add("§7Lorem ipsum dolor sit amet");
				lore.add("§7consectetur adipiscing elit.");
				break;
			default:
				break;
			}
			lore.clear();
			return lore;
		}
	}
}