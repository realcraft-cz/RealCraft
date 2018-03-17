package realcraft.bukkit.lobby;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.SimpleNPCDataStore;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.util.YamlStorage;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import realcraft.bukkit.RealCraft;
import ru.beykerykt.lightapi.LightAPI;

public class LobbyCitizens implements Listener, Runnable {
	RealCraft plugin;

	public NPCRegistry npcRegistry;
	HashMap<Integer,Citizen> citizens = new HashMap<Integer,Citizen>();

	HashMap<Player,Long> clicked = new HashMap<Player,Long>();

	private File file;
	private FileConfiguration config;

	public LobbyCitizens(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,5*20,5*20);
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
			@Override
			public void run(){
				loadCitizens();
			}
		},20);
	}

	public void onReload(){
	}

	@Override
	public void run(){
		for(Entry<Integer,Citizen> entry: citizens.entrySet()){
			entry.getValue().update();
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		for(Entry<Integer,Citizen> entry: citizens.entrySet()){
			entry.getValue().updateSkin();
		}
	}

	@EventHandler
	public void NPCRightClickEvent(NPCRightClickEvent event){
		NPC npc = event.getNPC();
		if(citizens.containsKey(npc.getId())){
			Player player = event.getClicker();
			citizens.get(npc.getId()).onPlayerClick(player);
		}
	}

	@EventHandler
	public void NPCLeftClickEvent(NPCLeftClickEvent event){
		NPC npc = event.getNPC();
		if(citizens.containsKey(npc.getId())){
			Player player = event.getClicker();
			citizens.get(npc.getId()).onPlayerClick(player);
		}
	}

	private void loadCitizens(){
		this.npcRegistry = CitizensAPI.createAnonymousNPCRegistry(SimpleNPCDataStore.create(new YamlStorage(new File(RealCraft.getInstance().getDataFolder()+"/citizens.tmp.yml"))));
		file = new File(RealCraft.getInstance().getDataFolder()+"/citizens.yml");
		if(file.exists()){
			config = new YamlConfiguration();
			try {
				config.load(file);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		Set<String> ids = config.getConfigurationSection("citizens").getKeys(false);
		for(String id : ids){
			Location location = new Location(Bukkit.getServer().getWorld("world"),config.getDouble("citizens."+id+"."+"x"),config.getDouble("citizens."+id+"."+"y"),config.getDouble("citizens."+id+"."+"z"),(float)config.getDouble("citizens."+id+"."+"yaw"),(float)config.getDouble("citizens."+id+"."+"pitch"));
			citizens.put(Integer.valueOf(id),new Citizen(Integer.valueOf(id),config.getString("citizens."+id+"."+"name"),config.getString("citizens."+id+"."+"server"),config.getString("citizens."+id+"."+"skin"),location));
		}
	}

	public class Citizen {
		private int id;
		private String name = "";
		private String server = "";
		private String skin = "";
		private int skinSchedule = -1;
		private Location location;
		private NPC npc;
		private Hologram hologramName;
		private Hologram hologramPlayers;
		private String playersString = "";

		public Citizen(int id,String name,String server,String skin,Location location){
			this.id = id;
			this.name = RealCraft.parseColors(name);
			this.server = server;
			this.skin = skin;
			this.location = location;
			this.npc = npcRegistry.createNPC(EntityType.PLAYER,UUID.fromString("00000000-0000-0000-0000-00000000000"+id),id,"");
			npc.spawn(location);
			npc.setName("&eklikni pravym");

			npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA,skin);
			npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST,false);
			SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
			if(skinnable != null) skinnable.setSkinName(skin);

			Equipment equip = npc.getTrait(Equipment.class);
			switch(server){
				case "survival":
					equip.set(Equipment.EquipmentSlot.HAND,new ItemStack(Material.WORKBENCH,1));
					break;
				case "creative":
					equip.set(Equipment.EquipmentSlot.HAND,new ItemStack(Material.GRASS,1));
					break;
				case "bedwars":
					equip.set(Equipment.EquipmentSlot.HAND,new ItemStack(Material.BED,1));
					break;
				case "hidenseek":
					equip.set(Equipment.EquipmentSlot.HAND,new ItemStack(Material.BOOKSHELF,1));
					break;
				case "ragemode":
					equip.set(Equipment.EquipmentSlot.HAND,new ItemStack(Material.BOW,1));
					break;
				case "blockparty":
					equip.set(Equipment.EquipmentSlot.HAND,new ItemStack(Material.RECORD_12,1));
					break;
				case "paintball":
					equip.set(Equipment.EquipmentSlot.HAND,new ItemStack(Material.SNOW_BALL,1));
					break;
				case "parkour":
					equip.set(Equipment.EquipmentSlot.HAND,new ItemStack(Material.LADDER,1));
					break;
				case "dominate":
					equip.set(Equipment.EquipmentSlot.HAND,new ItemStack(Material.BEACON,1));
					equip.set(Equipment.EquipmentSlot.CHESTPLATE,new ItemStack(Material.CHAINMAIL_CHESTPLATE,1));
					equip.set(Equipment.EquipmentSlot.LEGGINGS,new ItemStack(Material.IRON_LEGGINGS,1));
					equip.set(Equipment.EquipmentSlot.BOOTS,new ItemStack(Material.IRON_BOOTS,1));
					break;
			}

			hologramName = HologramsAPI.createHologram(plugin,location.clone().add(0.0,2.9,0.0));
			hologramPlayers = HologramsAPI.createHologram(plugin,location.clone().add(0.0,2.6,0.0));

			hologramName.insertTextLine(0,this.name);
			hologramPlayers.insertTextLine(0,"0 hracu");

			LightAPI.createLight(location.clone().add(0.0,2.0,0.0),15,false);
		}

		public void update(){
			int players = plugin.lobby.lobbymenu.getPlayersCount(server);
			if(!playersString.equals(players+" hracu")){
				hologramPlayers.removeLine(0);
				hologramPlayers.insertTextLine(0,players+" hracu");
				this.playersString = players+" hracu";
			}
			npc.teleport(location,TeleportCause.PLUGIN);
			npc.faceLocation(location.clone().add(0,0,2.0));
		}

		public void updateSkin(){
			if(skinSchedule != -1) RealCraft.getInstance().getServer().getScheduler().cancelTask(skinSchedule);
			skinSchedule = RealCraft.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
					if(skinnable != null) skinnable.setSkinName(skin);
					skinSchedule = -1;
				}
			},2*20);
		}

		public int getId(){
			return this.id;
		}

		public void onPlayerClick(Player player){
			if(clicked.get(player) == null || clicked.get(player)+1000 < System.currentTimeMillis()){
				clicked.put(player,System.currentTimeMillis());
				plugin.lobby.lobbymenu.connectPlayerToServer(player,server);
			}
		}
	}
}