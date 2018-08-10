package realcraft.bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.earth2me.essentials.Essentials;
import net.minecraft.server.v1_13_R1.IChatBaseComponent;
import net.minecraft.server.v1_13_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_13_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_13_R1.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
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
import realcraft.bukkit.develop.LampControl;
import realcraft.bukkit.develop.LocationsSaver;
import realcraft.bukkit.develop.WorldTeleporter;
import realcraft.bukkit.fights.Fights;
import realcraft.bukkit.friends.Friends;
import realcraft.bukkit.gameparty.GameParty;
import realcraft.bukkit.heads.CosmeticHeads;
import realcraft.bukkit.lobby.Lobby;
import realcraft.bukkit.minihry.EventCmds;
import realcraft.bukkit.minihry.GamesReminder;
import realcraft.bukkit.mute.Mute;
import realcraft.bukkit.nicks.NickManager;
import realcraft.bukkit.others.VipComamnd;
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
import realcraft.bukkit.survival.economy.Economy;
import realcraft.bukkit.survival.residences.CheckResidences;
import realcraft.bukkit.survival.residences.ResidenceSigns;
import realcraft.bukkit.survival.sells.Sells;
import realcraft.bukkit.survival.trading.Trading;
import realcraft.bukkit.teleport.TeleportRequests;
import realcraft.bukkit.test.Test;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.Glow;
import realcraft.bukkit.utils.ItemUtil;
import realcraft.bukkit.webshop.WebShop;
import realcraft.share.ServerType;
import realcraft.share.users.UserRank;

import java.lang.reflect.Field;

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
	private ChatNotice chatnotice;
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
		serverName = getServer().getServerName();
		serverType = ServerType.getByName(serverName);
		essentials = (Essentials) this.getServer().getPluginManager().getPlugin("Essentials");
		ItemUtil.init();
		config = new Config(this);
		TESTSERVER = config.getBoolean("testserver",false);
		if(config.getBoolean("lobby.maintenance."+serverName,false)){
			this.maintenance = true;
		}
		DB.init();
		new Users();
		banmanazer = new BanManazer(this);
		new Spectator(this);
		mute = new Mute(this);
		antispam = new AntiSpam(this);
		anticheat = new AntiCheat(this);
		chatlog = new ChatLog(this);
		chatnotice = new ChatNotice(this);
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
		new WebShop();
		new NickManager();
		new Coins();
		new Friends();
		new GameParty();
		new Cosmetics();
		new ServerSpawn();
		if(serverName.equalsIgnoreCase("lobby")){
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
			//new ShopManager();//TODO: uncomment
			new Sells();
			new Economy();
			lobby = new Lobby(this);
			new Sitting();
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
		else if(serverName.equalsIgnoreCase("fights")){
			fights = new Fights();
		}
		restart = new Restart(this);
		Glow.registerGlow();
		socketmanager = new SocketManager();
		new TabList();
		new PacketListener();
		new Test();
		new CosmeticHeads(this);
		new LocationsSaver();
		new WorldTeleporter();
		new LampControl();
		new VipComamnd();
		this.getServer().getPluginManager().registerEvents(this,this);
		this.updateWorldRules();
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

		if(RealCraft.getServerType() == ServerType.SURVIVAL){
			Bukkit.getScheduler().runTaskLater(this,new Runnable(){
				@Override
				public void run(){
					if(player.isOnline()){
						int cx = player.getLocation().getChunk().getX();
						int cz = player.getLocation().getChunk().getZ();
						int view = Bukkit.getViewDistance();
						for(int x=-view;x<=view;x++){
							for(int z=-view;z<=view;z++){
								net.minecraft.server.v1_13_R1.Chunk chunk = ((CraftChunk)player.getWorld().getChunkAt(cx+x,cz+z)).getHandle();
								PacketPlayOutMapChunk packet = new PacketPlayOutMapChunk(chunk,20);
								((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
							}
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
				event.setKickMessage("§fServer je docasne nedostupny, zkuste to prosim pozdeji.");
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
			world.setGameRuleValue("announceAdvancements","false");
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
						TabList.this.setPlayerHeaderFooter(player,"§r\n    §e§lRealCraft.cz§r    \n§r","§r\n§a"+ping+" ms §7| §e"+Users.getOnlineUsers().size()+"/100\n§7play.realcraft.cz\n§r");
						if(!player.getPlayerListName().equalsIgnoreCase(player.getDisplayName())) player.setPlayerListName(player.getDisplayName());
					}
				}
			},2*20,2*20);
		}

		private void setPlayerHeaderFooter(Player player,String header,String footer){
			PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
			try {
				IChatBaseComponent component = ChatSerializer.a("{\"text\":\""+header+"\"}");
				Field field = packet.getClass().getDeclaredField("a");
				field.setAccessible(true);
				field.set(packet,component);
				field.setAccessible(false);
			} catch (Exception e){
				e.printStackTrace();
			}
			try {
				IChatBaseComponent component = ChatSerializer.a("{\"text\":\""+footer+"\"}");
				Field field = packet.getClass().getDeclaredField("b");
				field.setAccessible(true);
				field.set(packet,component);
				field.setAccessible(false);
			} catch (Exception e){
				e.printStackTrace();
			}
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public class PacketListener {
		public PacketListener(){
			ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),ListenerPriority.HIGH,PacketType.Play.Server.ADVANCEMENTS,PacketType.Play.Server.RECIPES){
				@Override
				public void onPacketSending(PacketEvent event){
					if(RealCraft.getServerType() != ServerType.SURVIVAL){
						event.setCancelled(true);
					}
				}
			});
		}
	}
}