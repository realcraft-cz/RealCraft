package realcraft.bukkit.lobby;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.*;
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
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.BungeeMessages;
import realcraft.bukkit.utils.ItemUtil;
import realcraft.bukkit.utils.StringUtil;
import realcraft.share.ServerType;
import ru.beykerykt.lightapi.LightAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	public void onDisable(){
		for(LobbyStand stand : stands){
			stand.remove();
		}
		stands.clear();
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
			LightAPI.createLight(location.clone().add(0.0,3.0,0.0),15,false);
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
				if(tmpPlayers != players){
					players = tmpPlayers;
					hologramPlayers.removeLine(0);
					hologramPlayers.insertTextLine(0,players+" "+StringUtil.inflect(players,new String[]{"hrac","hraci","hracu"}));
				}
			}
			if(stand == null || stand.isDead() || !stand.isValid()) this.spawn();
		}

		public void click(Player player){
			if(server == ServerType.LOBBY) return;
			BungeeMessages.connectPlayerToServer(player,server);
		}

		private ItemStack getHelmet(){
			switch(server){
				case LOBBY: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTllYjlkYTI2Y2YyZDMzNDEzOTdhN2Y0OTEzYmEzZDM3ZDFhZDEwZWFlMzBhYjI1ZmEzOWNlYjg0YmMifX19");
				case SURVIVAL: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcLzQ5ZTJjY2E5YWU4MDYyZTZjYWE0ZDE0YjM0OWVmYTM3ODdlZGI5ZjE1OGMxNDdkN2VkYTA5MjQxOWI3NmFmY2QifX19");
				case FALLING: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2I3MTIxNzIwYmQyYjlhMTU5OTQ1ZGFkNDQxMmJhZDcwZTAwOTY2NjYwMGI3ZjJhZmRhNGRlMmMyNmE1MzExZiJ9fX0=");
				case CREATIVE: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcLzliOWRiMWYyYTVhOTc5MTY2ZmI3NThlZTRkN2M4Zjk0N2JhNjgyOTY4N2E5OTZlNDZiZTM0Yzc5ZTQ5ODYifX19");
				case FIGHTS: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk3ZWRlNDlmNTJlMjVhZGVhYTU4YjFkNzgxNmZjY2UyMjI5ZmQ4ZjI0ZWI1ZjA2NWIyM2YzNzY1ODk0NGUifX19");
				case BEDWARS: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcLzg4ZDUyNjQyOGFhNWFmNTk0ZGRlMmViN2RhZmI1OGZjYmFhNGRkM2I3ZmUzOGY3NzU3ODUxZTgzNDMyMDRhIn19fQ==");
				case HIDENSEEK: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcLzZmNjhkNTA5YjVkMTY2OWI5NzFkZDFkNGRmMmU0N2UxOWJjYjFiMzNiZjFhN2ZmMWRkYTI5YmZjNmY5ZWJmIn19fQ==");
				case BLOCKPARTY: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7Im1ldGFkYXRhIjp7Im1vZGVsIjoic2xpbSJ9LCJ1cmwiOiJodHRwOlwvXC90ZXh0dXJlcy5taW5lY3JhZnQubmV0XC90ZXh0dXJlXC9mMWVjYzkyMWY4NjkyNjEzZmIzYjExMDQ0ZTZiZDNjZGQ0YmFjMDU1MThmZjg4MmZmMjk4MzIyOTMyZjFlZCJ9fX0=");
				case RAGEMODE: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcL2YxMThlNTc5MjFiOWQ1ZjM2Yzk5YTE3NDZiOWRkMjFkYTliYTJlMDNhOTFmYTE2NWRjOTI1MmVjYzQ5MGI4In19fQ==");
				case PAINTBALL: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcLzY5MzdlYWRjNTNjMzliOTY4OGNjMzk2NTVkMWI3OGZkNjEyYzFjZDYyNWMyYTg5NjM4YzVlMjcyMTZjNmU0ZCJ9fX0=");
				case DOMINATE: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjNjNzFhODVlZWIzY2Q2NDQ5MTU5Njc1YWE4OTI3OGEyYTFkNTg3YjRkMGI3NjgxNzRmYzJlMTVjOWJlNGQifX19");
				case RACES: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzk4NzNhNzYwMjNkNmNmYmZhMTExMjBlNWFmNWNkM2ZiNDMzYTczNTdkMTk1MTI5NmU4YzljZDE5OGM0ZTc3YiJ9fX0=");
				default:break;
			}
			return new ItemStack(Material.SKELETON_SKULL);
		}

		private ItemStack getChestplate(){
			ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
			if(server == ServerType.LOBBY) item.setType(Material.AIR);
			else if(server == ServerType.SURVIVAL) item = new ItemStack(Material.DIAMOND_CHESTPLATE);
			else if(server == ServerType.CREATIVE) ItemUtil.setLetherColor(item,"#2CCC1E");
			else if(server == ServerType.FALLING) item = new ItemStack(Material.IRON_CHESTPLATE);
			else if(server == ServerType.FIGHTS) item = new ItemStack(Material.GOLDEN_CHESTPLATE);
			else if(server == ServerType.BEDWARS) ItemUtil.setLetherColor(item,"#CC0000");
			else if(server == ServerType.HIDENSEEK) ItemUtil.setLetherColor(item,"#3A86CC");
			else if(server == ServerType.BLOCKPARTY) ItemUtil.setLetherColor(item,"#DC82EB");
			else if(server == ServerType.RAGEMODE) ItemUtil.setLetherColor(item,"#AAAAAA");
			else if(server == ServerType.PAINTBALL) ItemUtil.setLetherColor(item,"#E6823B");
			else if(server == ServerType.DOMINATE) item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
			else if(server == ServerType.RACES) ItemUtil.setLetherColor(item,"#FFFFFF");
			return item;
		}

		private ItemStack getLeggings(){
			ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);
			if(server == ServerType.LOBBY) item.setType(Material.AIR);
			else if(server == ServerType.SURVIVAL) return item;
			else if(server == ServerType.CREATIVE) ItemUtil.setLetherColor(item,"#2CCC1E");
			else if(server == ServerType.FIGHTS) return item;
			else if(server == ServerType.BEDWARS) item = new ItemStack(Material.IRON_LEGGINGS);
			else if(server == ServerType.HIDENSEEK) ItemUtil.setLetherColor(item,"#3A86CC");
			else if(server == ServerType.BLOCKPARTY) ItemUtil.setLetherColor(item,"#DC82EB");
			else if(server == ServerType.RAGEMODE) ItemUtil.setLetherColor(item,"#AAAAAA");
			else if(server == ServerType.PAINTBALL) ItemUtil.setLetherColor(item,"#E6823B");
			else if(server == ServerType.DOMINATE) item = new ItemStack(Material.IRON_LEGGINGS);
			else if(server == ServerType.RACES) ItemUtil.setLetherColor(item,"#000000");
			return item;
		}

		private ItemStack getBoots(){
			ItemStack item = new ItemStack(Material.LEATHER_BOOTS);
			if(server == ServerType.LOBBY) item.setType(Material.AIR);
			else if(server == ServerType.SURVIVAL) ItemUtil.setLetherColor(item,"#FFFFFF");
			else if(server == ServerType.CREATIVE) ItemUtil.setLetherColor(item,"#2FB024");
			else if(server == ServerType.FALLING) item = new ItemStack(Material.GOLDEN_BOOTS);
			else if(server == ServerType.FIGHTS) item = new ItemStack(Material.DIAMOND_BOOTS);
			else if(server == ServerType.BEDWARS) ItemUtil.setLetherColor(item,"#CC0000");
			else if(server == ServerType.HIDENSEEK) ItemUtil.setLetherColor(item,"#2566A3");
			else if(server == ServerType.BLOCKPARTY) ItemUtil.setLetherColor(item,"#B451C4");
			else if(server == ServerType.RAGEMODE) ItemUtil.setLetherColor(item,"#888888");
			else if(server == ServerType.PAINTBALL) ItemUtil.setLetherColor(item,"#C77031");
			else if(server == ServerType.DOMINATE) item = new ItemStack(Material.GOLDEN_BOOTS);
			else if(server == ServerType.RACES) ItemUtil.setLetherColor(item,"#E75422");
			return item;
		}

		@SuppressWarnings("deprecation")
		private ItemStack getMainHand(){
			switch(server){
				case SURVIVAL: return new ItemStack(Material.IRON_PICKAXE);
				case CREATIVE: return new ItemStack(Material.GRASS_BLOCK);
				case FALLING: return new ItemStack(Material.ANDESITE);
				case FIGHTS: return new ItemStack(Material.IRON_SWORD);
				case BEDWARS: return new ItemStack(Material.RED_BED);
				case HIDENSEEK: return new ItemStack(Material.BOOKSHELF);
				case BLOCKPARTY: return ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6XC9cL3RleHR1cmVzLm1pbmVjcmFmdC5uZXRcL3RleHR1cmVcLzhmOTIzM2MxMjQ3ZTAzZTlmZDI3NzQyNzM3ZTc5ZTRjY2ViZDIyNWE5YjA1OWQ1OTZkNWNkMzRlMjZmMjE2NSJ9fX0=");
				case RAGEMODE: return new ItemStack(Material.BOW);
				case PAINTBALL: return new ItemStack(Material.SNOWBALL);
				case DOMINATE: return new ItemStack(Material.GOLDEN_SWORD);
				case RACES: return new ItemStack(Material.SADDLE);
				default:break;
			}
			return new ItemStack(Material.AIR);
		}

		private ItemStack getOffHand(){
			ItemStack item = new ItemStack(Material.AIR);
			switch(server){
				case SURVIVAL: return new ItemStack(Material.TORCH);
				case DOMINATE: return new ItemStack(Material.BEACON);
				case FIGHTS: return new ItemStack(Material.SHIELD);
				default:break;
			}
			return item;
		}
	}
}