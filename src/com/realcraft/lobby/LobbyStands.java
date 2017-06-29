package com.realcraft.lobby;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
import com.realcraft.RealCraft;
import com.realcraft.ServerType;
import com.realcraft.utils.ItemUtil;
import com.realcraft.utils.RandomUtil;
import com.realcraft.utils.StringUtil;

import ru.beykerykt.lightapi.LightAPI;

public class LobbyStands implements Listener, Runnable {

	RealCraft plugin;
	private ArrayList<LobbyStand> stands = new ArrayList<LobbyStand>();

	public LobbyStands(RealCraft realcraft){
		plugin = realcraft;
		Bukkit.getServer().getPluginManager().registerEvents(this,plugin);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,this,20,20);
		this.loadStands();
	}

	@Override
	public void run(){
		for(LobbyStand stand : stands){
			stand.update();
		}
	}

	@SuppressWarnings("unchecked")
	public void loadStands(){
		File file = new File(RealCraft.getInstance().getDataFolder()+"/stands.yml");
		if(file.exists()){
			FileConfiguration config = new YamlConfiguration();
			try {
				config.load(file);
				List<Map<String, Object>> tmpStands = (List<Map<String, Object>>) config.get("servers");
				if(tmpStands != null && !tmpStands.isEmpty()){
					for(Map<String, Object> stand : tmpStands){
						String server = stand.get("server").toString();
						double x = Double.valueOf(stand.get("x").toString());
						double y = Double.valueOf(stand.get("y").toString());
						double z = Double.valueOf(stand.get("z").toString());
						float yaw = Float.valueOf(stand.get("yaw").toString());
						float pitch = Float.valueOf(stand.get("pitch").toString());
						World world = Bukkit.getServer().getWorld(stand.get("world").toString());
						Location location = new Location(world,x,y,z,yaw,pitch);
						ServerType serverType = ServerType.LOBBY;
						try {
							serverType = ServerType.getByName(server);
						} catch (IllegalArgumentException e){
						}
						stands.add(new LobbyStand(serverType,location));
					}
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void PlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent event){
		for(LobbyStand stand : stands){
			if(stand.getServerType().toString().equals(event.getRightClicked().getCustomName())){
				event.setCancelled(true);
				stand.click(event.getPlayer());
				break;
			}
		}
	}

	@EventHandler
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getEntity().getType() == EntityType.ARMOR_STAND){
			for(LobbyStand stand : stands){
				if(stand.getServerType().toString().equals(event.getEntity().getCustomName())){
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
		for(LobbyStand stand : stands){
			if(stand.isInChunk(event.getChunk())){
				stand.spawn();
			}
		}
	}

	@EventHandler
	public void ChunkUnloadEvent(ChunkUnloadEvent event){
		for(LobbyStand stand : stands){
			if(stand.isInChunk(event.getChunk())){
				stand.remove();
			}
		}
	}

	private class LobbyStand {

		private ServerType server;
		private Location location;
		private ArmorStand stand;
		private Hologram hologramName;
		private Hologram hologramPlayers;
		private int players = 0;

		public LobbyStand(ServerType server,Location location){
			this.server = server;
			this.location = location;
			this.spawn();
			if(server != ServerType.LOBBY) this.init();
		}

		public ServerType getServerType(){
			return server;
		}

		public boolean isInChunk(Chunk chunk){
			return (location.getBlockX() >> 4 == chunk.getX() && location.getBlockZ() >> 4 == chunk.getZ());
		}

		private void init(){
			hologramName = HologramsAPI.createHologram(plugin,location.clone().add(0.0,2.9,0.0));
			hologramPlayers = HologramsAPI.createHologram(plugin,location.clone().add(0.0,2.6,0.0));

			hologramName.insertTextLine(0,server.getColor()+""+ChatColor.BOLD+server.getName());
			hologramPlayers.insertTextLine(0,"0 hracu");

			LightAPI.createLight(location.clone().add(0.0,2.0,0.0),15,false);
		}

		private void spawn(){
			this.remove();
			stand = (ArmorStand)location.getWorld().spawnEntity(location,EntityType.ARMOR_STAND);
			stand.setBasePlate(false);
			stand.setArms(true);
			stand.setCustomName(server.toString());
			stand.setCustomNameVisible(false);
			if(server == ServerType.LOBBY) stand.setSmall(true);
			stand.getEquipment().setHelmet(this.getHelmet());
			stand.getEquipment().setChestplate(this.getChestplate());
			stand.getEquipment().setLeggings(this.getLeggings());
			stand.getEquipment().setBoots(this.getBoots());
			stand.getEquipment().setItemInMainHand(this.getMainHand());
			stand.getEquipment().setItemInOffHand(this.getOffHand());

			for(Entity entity : stand.getNearbyEntities(0.5,0.5,0.5)){
				if(entity.getType() == EntityType.ARMOR_STAND){
					entity.remove();
				}
			}
		}

		private void remove(){
			if(stand != null && !stand.isDead()){
				stand.remove();
				stand = null;
			}
		}

		private void update(){
			if(server != ServerType.LOBBY){
				int tmpPlayers = plugin.lobby.lobbymenu.getPlayersCount(server.toString());
				tmpPlayers = RandomUtil.getRandomInteger(0,50);//TODO: remove
				if(tmpPlayers != players){
					players = tmpPlayers;
					hologramPlayers.removeLine(0);
					hologramPlayers.insertTextLine(0,players+" "+StringUtil.inflect(players,new String[]{"hrac","hraci","hracu"}));
				}
			}
		}

		public void click(Player player){
			if(server == ServerType.LOBBY) return;
			//server.connectPlayer(player);
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
		}

		private ItemStack getHelmet(){
			switch(server){
				case LOBBY: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTllYjlkYTI2Y2YyZDMzNDEzOTdhN2Y0OTEzYmEzZDM3ZDFhZDEwZWFlMzBhYjI1ZmEzOWNlYjg0YmMifX19");
				case SURVIVAL: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcLzQ5ZTJjY2E5YWU4MDYyZTZjYWE0ZDE0YjM0OWVmYTM3ODdlZGI5ZjE1OGMxNDdkN2VkYTA5MjQxOWI3NmFmY2QifX19");
				case CREATIVE: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcLzcyMmU3NmM3OWY2YTc2MzUyMjNjYTk5MzliNWViZmVjMjZlY2E1YzRlM2M2ZDg4ODc1YzJlZTgwMTBjMzU2MWYifX19");
				case PARKOUR: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcLzliOWRiMWYyYTVhOTc5MTY2ZmI3NThlZTRkN2M4Zjk0N2JhNjgyOTY4N2E5OTZlNDZiZTM0Yzc5ZTQ5ODYifX19");
				case BEDWARS: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcLzg4ZDUyNjQyOGFhNWFmNTk0ZGRlMmViN2RhZmI1OGZjYmFhNGRkM2I3ZmUzOGY3NzU3ODUxZTgzNDMyMDRhIn19fQ==");
				case HIDENSEEK: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcLzZmNjhkNTA5YjVkMTY2OWI5NzFkZDFkNGRmMmU0N2UxOWJjYjFiMzNiZjFhN2ZmMWRkYTI5YmZjNmY5ZWJmIn19fQ==");
				case BLOCKPARTY: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7Im1ldGFkYXRhIjp7Im1vZGVsIjoic2xpbSJ9LCJ1cmwiOiJodHRwOlwvXC90ZXh0dXJlcy5taW5lY3JhZnQubmV0XC90ZXh0dXJlXC9mMWVjYzkyMWY4NjkyNjEzZmIzYjExMDQ0ZTZiZDNjZGQ0YmFjMDU1MThmZjg4MmZmMjk4MzIyOTMyZjFlZCJ9fX0=");
				case RAGEMODE: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcL2YxMThlNTc5MjFiOWQ1ZjM2Yzk5YTE3NDZiOWRkMjFkYTliYTJlMDNhOTFmYTE2NWRjOTI1MmVjYzQ5MGI4In19fQ==");
				case PAINTBALL: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcLzY5MzdlYWRjNTNjMzliOTY4OGNjMzk2NTVkMWI3OGZkNjEyYzFjZDYyNWMyYTg5NjM4YzVlMjcyMTZjNmU0ZCJ9fX0=");
				case DOMINATE: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcL2Q5NGUxNDkxNTZhYmVhOWQ0NjcxNTljYjQ4MGI4NTk0MTQ4ZTRjZWYyZWVmOTdiMzExMmZkNWVkZGI0YWMifX19");
				default:break;
			}
			return new ItemStack(Material.SKULL_ITEM);
		}

		private ItemStack getChestplate(){
			ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
			if(server == ServerType.LOBBY) item.setType(Material.AIR);
			return item;
		}

		private ItemStack getLeggings(){
			ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);
			if(server == ServerType.LOBBY) item.setType(Material.AIR);
			return item;
		}

		private ItemStack getBoots(){
			ItemStack item = new ItemStack(Material.LEATHER_BOOTS);
			if(server == ServerType.LOBBY) item.setType(Material.AIR);
			return item;
		}

		private ItemStack getMainHand(){
			switch(server){
				case SURVIVAL: return new ItemStack(Material.IRON_PICKAXE);
				case CREATIVE: return new ItemStack(Material.GRASS);
				case PARKOUR: return new ItemStack(Material.LADDER);
				case BEDWARS: return new ItemStack(Material.BED);
				case HIDENSEEK: return new ItemStack(Material.BOOKSHELF);
				case BLOCKPARTY: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcLzhmOTIzM2MxMjQ3ZTAzZTlmZDI3NzQyNzM3ZTc5ZTRjY2ViZDIyNWE5YjA1OWQ1OTZkNWNkMzRlMjZmMjE2NSJ9fX0=");
				case RAGEMODE: return new ItemStack(Material.BOW);
				case PAINTBALL: return new ItemStack(Material.SNOW_BALL);
				case DOMINATE: return new ItemStack(Material.BEACON);
				default:break;
			}
			return new ItemStack(Material.AIR);
		}

		private ItemStack getOffHand(){
			ItemStack item = new ItemStack(Material.AIR);
			return item;
		}
	}
}