package realcraft.bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import realcraft.bukkit.anticheat.AntiCheat;
import realcraft.bukkit.antispam.AntiSpam;
import realcraft.bukkit.auth.Auth;
import realcraft.bukkit.banmanazer.BanManazer;
import realcraft.bukkit.chat.*;
import realcraft.bukkit.coins.Coins;
import realcraft.bukkit.config.Config;
import realcraft.bukkit.cosmetics.Cosmetics;
import realcraft.bukkit.creative.CancelGrow;
import realcraft.bukkit.creative.DisableSpectator;
import realcraft.bukkit.creative.PlotSquaredWorldEdit;
import realcraft.bukkit.creative.SchematicBrush;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.develop.*;
import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.fights.Fights;
import realcraft.bukkit.friends.Friends;
import realcraft.bukkit.heads.CosmeticHeads;
import realcraft.bukkit.lobby.Lobby;
import realcraft.bukkit.mapmanager.MapManager;
import realcraft.bukkit.minihry.EventCmds;
import realcraft.bukkit.minihry.GamesReminder;
import realcraft.bukkit.mute.Mute;
import realcraft.bukkit.others.Canvas;
import realcraft.bukkit.others.MapServerTeleport;
import realcraft.bukkit.others.WEBlockCompleter;
import realcraft.bukkit.pets.PetsManager;
import realcraft.bukkit.report.Report;
import realcraft.bukkit.restart.Restart;
import realcraft.bukkit.sitting.Sitting;
import realcraft.bukkit.skins.Skins;
import realcraft.bukkit.sockets.SocketManager;
import realcraft.bukkit.spawn.ServerSpawn;
import realcraft.bukkit.spectator.Spectator;
import realcraft.bukkit.survival.MapCrafter;
import realcraft.bukkit.survival.PassiveMode;
import realcraft.bukkit.survival.RandomSpawn;
import realcraft.bukkit.survival.ViewDistanceLimiter;
import realcraft.bukkit.survival.economy.Economy;
import realcraft.bukkit.survival.residences.CheckResidences;
import realcraft.bukkit.survival.residences.ResidenceSigns;
import realcraft.bukkit.survival.sells.Sells;
import realcraft.bukkit.survival.shops.ShopManager;
import realcraft.bukkit.survival.traders.Traders;
import realcraft.bukkit.survival.trading.Trading;
import realcraft.bukkit.teleport.TeleportRequests;
import realcraft.bukkit.test.Test;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.vip.VipManager;
import realcraft.bukkit.wrappers.HologramsApi;
import realcraft.share.ServerType;
import realcraft.share.users.UserRank;

public class RealCraft extends JavaPlugin implements Listener {
	private static RealCraft instance;
	private static boolean TESTSERVER;
	private static ServerType serverType;
	private boolean maintenance = false;

	public Config config;
	public DB db;

	private BanManazer banmanazer;
	private Mute mute;
	private AntiSpam antispam;
	public ChatLog chatlog;
	private EventCmds eventcmds;
	private Restart restart;
	public Auth auth;
	private CheckResidences checkresidences;
	private ChatTips chattips;
	private ResidenceSigns residencesigns;
	private ChatPrivate chatprivate;
	public ChatAdvert chatadvert;
	private ChatAdmin chatadmin;
	private ChatFormat chatformat;
	private ChatCommandSpy chatcommandspy;
	private TeleportRequests teleportrequests;
	private Report report;
	private DisableSpectator disablespectator;
	private CancelGrow cancelgrow;
	public Lobby lobby;
	public AntiCheat anticheat;
	public GamesReminder gamesreminder;
	private Trading trading;
	private MapCrafter mapcrafter;
	public Skins skins;
	private Fights fights;
	private SocketManager socketmanager;

	public String serverName;

	public Essentials essentials;

	public static RealCraft getInstance(){
		return instance;
	}

	public static boolean isTestServer(){
		return TESTSERVER;
	}

	public static ServerType getServerType(){
		return serverType;
	}

	public void onEnable(){
		instance = this;
		serverName = getServer().getMotd();
		serverType = ServerType.getByName(serverName);
		essentials = (Essentials) this.getServer().getPluginManager().getPlugin("Essentials");
		config = new Config(this);
		TESTSERVER = config.getBoolean("testserver",false);
		if(config.getBoolean("lobby.maintenance."+serverName,false)){
			this.maintenance = true;
		}
		DB.init();
		new Users();
		new HologramsApi(this);
		banmanazer = new BanManazer(this);
		new Spectator(this);
		mute = new Mute(this);
		antispam = new AntiSpam(this);
		anticheat = new AntiCheat(this);
		chatlog = new ChatLog(this);
		chattips = new ChatTips(this);
		chatprivate = new ChatPrivate(this);
		chatadvert = new ChatAdvert(this);
		chatadmin = new ChatAdmin(this);
		chatformat = new ChatFormat(this);
		chatcommandspy = new ChatCommandSpy(this);
		teleportrequests = new TeleportRequests(this);
		report = new Report(this);
		skins = new Skins();
		gamesreminder = new GamesReminder(this);
		new SchematicBrush();
		new Coins();
		new Friends();
		new ServerSpawn();
		if(serverName.equalsIgnoreCase("lobby")){
			new Cosmetics();
			auth = new Auth(this);
			eventcmds = new EventCmds(this);
			cancelgrow = new CancelGrow(this);
			lobby = new Lobby(this);
			new Sitting();
		}
		else if(serverName.equalsIgnoreCase("survival")){
			checkresidences = new CheckResidences(this);
			residencesigns = new ResidenceSigns(this);
			trading = new Trading(this);
			mapcrafter = new MapCrafter(this);
			new PassiveMode();
			new RandomSpawn();
			new ShopManager();
			new Sells();
			new Economy();
			lobby = new Lobby(this);
			new Sitting();
			new ViewDistanceLimiter();
			new PetsManager();
			new Traders();
		}
		else if(serverName.equalsIgnoreCase("bedwars") ||
				serverName.equalsIgnoreCase("hidenseek") ||
				serverName.equalsIgnoreCase("blockparty") ||
				serverName.equalsIgnoreCase("ragemode") ||
				serverName.equalsIgnoreCase("paintball") ||
				serverName.equalsIgnoreCase("dominate") ||
				serverName.equalsIgnoreCase("races") ||
				serverName.equalsIgnoreCase("uhc")){
			cancelgrow = new CancelGrow(this);
		}
		else if(serverName.equalsIgnoreCase("creative")){
			disablespectator = new DisableSpectator(this);
			cancelgrow = new CancelGrow(this);
			new PlotSquaredWorldEdit();
			lobby = new Lobby(this);
			new Sitting();
		}
		else if(serverName.equalsIgnoreCase("maps")){
			cancelgrow = new CancelGrow(this);
			new MapManager();
		}
		else if(serverName.equalsIgnoreCase("fights")){
			fights = new Fights();
		}
		if(RealCraft.getServerType() != ServerType.MAPS){
			new MapServerTeleport();
		}
		if(RealCraft.getServerType() == ServerType.FALLING){
			new FallManager();
			trading = new Trading(this);
			new Sitting();
		}
		restart = new Restart(this);
		socketmanager = new SocketManager();
		new TabList();
		new PacketListener();
		new Test();
		new CosmeticHeads();
		new LocationsSaver();
		new WorldTeleporter();
		new WorldLoader();
		new LampControl();
		new ChunkGenerator();
		new Schema();
		new VipManager();
		new Canvas();
		new WEBlockCompleter();
		this.getServer().getPluginManager().registerEvents(this,this);
		this.updateWorldRules();

		if (RealCraft.isTestServer()) {
			if (RealCraft.getServerType() == ServerType.LOBBY) {
				new PetsManager();
				new Traders();
			}
		}
	}

	public void onDisable(){
		config.onDisable();
		DB.onDisable();
		if(lobby != null) lobby.onDisable();
		socketmanager.onDisable();
		if(fights != null) fights.onDisable();
	}

	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled = true)
	public void PlayerJoinEvent(PlayerJoinEvent event){
		event.setJoinMessage("");
		final Player player = event.getPlayer();
		if(Users.getUser(player).isLogged() || !serverName.equalsIgnoreCase("lobby")){
			for(PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
		}

		if(!Spectator.isPlayerSpectating(player)){
			this.getServer().getScheduler().scheduleSyncDelayedTask(this,new Runnable(){
				@Override
				public void run(){
					for(Player user : Bukkit.getServer().getOnlinePlayers()){
						if(user != player && Users.getUser(user).isLogged()){
							user.sendMessage(RealCraft.parseColors("&2[&a+&2] "+player.getDisplayName()+"&f je &aonline"));
						}
					}
				}
			},20);
		}
	}

	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled = false)
	public void PlayerLoginEvent(PlayerLoginEvent event){
		if(maintenance){
			if(!Users.getUser(event.getPlayer()).getRank().isMinimum(UserRank.BUILDER)){
				event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
				event.setKickMessage("�fServer je docasne nedostupny, zkuste to prosim pozdeji.");
			}
		}
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		event.setQuitMessage("");
		Player player = event.getPlayer();
		for(Player user : Bukkit.getServer().getOnlinePlayers()){
			if(user != player && Users.getUser(user).isLogged()){
				user.sendMessage(RealCraft.parseColors("&4[&c-&4] "+player.getDisplayName()+"&f je &coffline"));
			}
		}
	}

	public static String parseColors(String message){
		return ChatColor.translateAlternateColorCodes('&',message);
	}

	private void updateWorldRules(){
		for(World world : Bukkit.getWorlds()){
			world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false);
		}

		if (RealCraft.isTestServer() && RealCraft.getServerType() == ServerType.LOBBY) {
			for (World world : Bukkit.getWorlds()) {
				world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
			}
		}
	}

	//https://gist.github.com/aadnk/3928137
	public class TabList {
		public TabList(){
			ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),ListenerPriority.HIGH,PacketType.Play.Client.KEEP_ALIVE){
				@Override
				public void onPacketReceiving(PacketEvent event){
					if(event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE){
						int ping = (int)((System.nanoTime()/1000000L)-event.getPacket().getLongs().read(0));
						Users.getUser(event.getPlayer()).setPing(ping);
					}
				}
			});
			Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					for(Player player : Bukkit.getServer().getOnlinePlayers()){
						int ping = Users.getUser(player).getPing();
						player.setPlayerListHeaderFooter("�r\n    �e�lRealCraft.cz�r    \n�r","�r\n�a"+ping+" ms �7| �e"+Users.getOnlineUsers().size()+"/100\n�7play.realcraft.cz\n�r");
						if(!player.getPlayerListName().equalsIgnoreCase(player.getDisplayName())) player.setPlayerListName(player.getDisplayName());
					}
				}
			},2*20,2*20);
		}
	}

	public class PacketListener {
		public PacketListener(){
			ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),ListenerPriority.HIGH,PacketType.Play.Client.ADVANCEMENTS,PacketType.Play.Server.ADVANCEMENTS,PacketType.Play.Server.RECIPES){
				@Override
				public void onPacketSending(PacketEvent event){
					if(RealCraft.getServerType() != ServerType.SURVIVAL && RealCraft.getServerType() != ServerType.FALLING){
						event.setCancelled(true);
					}
				}
			});
		}
	}
}