package com.realcraft;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import com.anticheat.AntiCheat;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.earth2me.essentials.Essentials;
import com.parkour.Parkour;
import com.realcraft.antispam.AntiSpam;
import com.realcraft.auth.Auth;
import com.realcraft.auth.AuthLoginEvent;
import com.realcraft.banmanazer.BanManazer;
import com.realcraft.chat.ChatAdmin;
import com.realcraft.chat.ChatAdvert;
import com.realcraft.chat.ChatCommandSpy;
import com.realcraft.chat.ChatFormat;
import com.realcraft.chat.ChatLog;
import com.realcraft.chat.ChatNotice;
import com.realcraft.chat.ChatPrivate;
import com.realcraft.chat.ChatTips;
import com.realcraft.config.Config;
import com.realcraft.creative.CancelGrow;
import com.realcraft.creative.DisableSpectator;
import com.realcraft.creative.PlotSquaredWorldEdit;
import com.realcraft.creative.SchematicBrush;
import com.realcraft.database.MySQL;
import com.realcraft.heads.CosmeticHeads;
import com.realcraft.lobby.Lobby;
import com.realcraft.mapcrafter.MapCrafter;
import com.realcraft.minihry.EventCmds;
import com.realcraft.minihry.GamesReminder;
import com.realcraft.minihry.SignBlockProtection;
import com.realcraft.moderatorchat.ModeratorChat;
import com.realcraft.mute.Mute;
import com.realcraft.nicks.NickManager;
import com.realcraft.playermanazer.PlayerManazer;
import com.realcraft.playermanazer.PlayerManazer.PlayerInfo;
import com.realcraft.quiz.Quiz;
import com.realcraft.report.Report;
import com.realcraft.residences.CheckResidences;
import com.realcraft.residences.ResidenceSigns;
import com.realcraft.restart.Restart;
import com.realcraft.schema.Schema;
import com.realcraft.skins.Skins;
import com.realcraft.sockets.SocketManager;
import com.realcraft.spectator.Spectator;
import com.realcraft.survival.PassiveMode;
import com.realcraft.survival.RandomSpawn;
import com.realcraft.teleport.TeleportRequests;
import com.realcraft.test.Test;
import com.realcraft.trading.Trading;
import com.realcraft.utils.Glow;
import com.realcraft.utils.Title;
import com.realcraft.votes.Votes;
import com.realcraft.webshop.WebShop;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter;

public class RealCraft extends JavaPlugin implements Listener {
	private static RealCraft instance;
	private static boolean TESTSERVER;
	private static ServerType serverType;
	private boolean maintenance = false;

	public Config config;
	public MySQL db;

	public PlayerManazer playermanazer;
	private BanManazer banmanazer;
	private Mute mute;
	private AntiSpam antispam;
	public ChatLog chatlog;
	private ModeratorChat moderatorchat;
	private ChatNotice chatnotice;
	private EventCmds eventcmds;
	private Restart restart;
	private Votes votes;
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
	private SignBlockProtection signblockprotection;
	private DisableSpectator disablespectator;
	private CancelGrow cancelgrow;
	public Lobby lobby;
	public AntiCheat anticheat;
	public GamesReminder gamesreminder;
	private Schema schema;
	private Trading trading;
	private MapCrafter mapcrafter;
	private Parkour parkour;
	public Skins skins;
	private CosmeticHeads cosmeticheads;
	private Quiz quiz;
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
		config = new Config(this);
		TESTSERVER = config.getBoolean("testserver",false);
		if(config.getBoolean("lobby.maintenance."+serverName,false)){
			this.maintenance = true;
		}
		db = new MySQL(this);
		playermanazer = new PlayerManazer(this);
		banmanazer = new BanManazer(this);
		new Spectator(this);
		mute = new Mute(this);
		antispam = new AntiSpam(this);
		anticheat = new AntiCheat(this);
		chatlog = new ChatLog(this);
		moderatorchat = new ModeratorChat(this);
		chatnotice = new ChatNotice(this);
		chattips = new ChatTips(this);
		chatprivate = new ChatPrivate(this);
		chatadvert = new ChatAdvert(this);
		chatadmin = new ChatAdmin(this);
		chatformat = new ChatFormat(this);
		chatcommandspy = new ChatCommandSpy(this);
		teleportrequests = new TeleportRequests(this);
		report = new Report(this);
		skins = new Skins(this);
		gamesreminder = new GamesReminder(this);
		quiz = new Quiz(this);
		new SchematicBrush();
		new WebShop();
		new NickManager();
		if(serverName.equalsIgnoreCase("lobby")){
			auth = new Auth(this);
			eventcmds = new EventCmds(this);
			cancelgrow = new CancelGrow(this);
			new Test();
			if(RealCraft.isTestServer()){
				cosmeticheads = new CosmeticHeads(this);
				new PassiveMode();
				new RandomSpawn();
			}
		}
		else if(serverName.equalsIgnoreCase("survival")){
			checkresidences = new CheckResidences(this);
			residencesigns = new ResidenceSigns(this);
			trading = new Trading(this);
			mapcrafter = new MapCrafter(this);
			new PassiveMode();
			new RandomSpawn();
		}
		else if(serverName.equalsIgnoreCase("bedwars") ||
				serverName.equalsIgnoreCase("hidenseek") ||
				serverName.equalsIgnoreCase("blockparty") ||
				serverName.equalsIgnoreCase("ragemode") ||
				serverName.equalsIgnoreCase("paintball") ||
				serverName.equalsIgnoreCase("dominate") ||
				serverName.equalsIgnoreCase("uhc")){
			eventcmds = new EventCmds(this);
			signblockprotection = new SignBlockProtection(this);
			cancelgrow = new CancelGrow(this);
		}
		else if(serverName.equalsIgnoreCase("creative")){
			disablespectator = new DisableSpectator(this);
			cancelgrow = new CancelGrow(this);
			cosmeticheads = new CosmeticHeads(this);
			cancelgrow = new CancelGrow(this);
			new PlotSquaredWorldEdit();
		}
		lobby = new Lobby(this);
		restart = new Restart(this);
		votes = new Votes(this);
		schema = new Schema(this);
		Glow.registerGlow();
		if(serverName.equalsIgnoreCase("parkour")){
			parkour = new Parkour(this);
			eventcmds = new EventCmds(this);
		}
		socketmanager = new SocketManager();
		new TabList();
		new PacketListener();
		this.getServer().getPluginManager().registerEvents(this,this);
	}

	public void onDisable(){
		config.onDisable();
		db.onDisable();
		if(lobby != null) lobby.onDisable();
		socketmanager.onDisable();
	}

	public static String getServerName(String server){
		if(server.equalsIgnoreCase("lobby")) return "Lobby";
		else if(server.equalsIgnoreCase("survival")) return "Survival";
		else if(server.equalsIgnoreCase("creative")) return "Creative";
		else if(server.equalsIgnoreCase("bedwars")) return "BedWars";
		else if(server.equalsIgnoreCase("hidenseek")) return "Hide & Seek";
		else if(server.equalsIgnoreCase("blockparty")) return "BlockParty";
		else if(server.equalsIgnoreCase("ragemode")) return "RageMode";
		else if(server.equalsIgnoreCase("paintball")) return "Paintball";
		else if(server.equalsIgnoreCase("dominate")) return "Dominate";
		else if(server.equalsIgnoreCase("parkour")) return "Parkour";
		return "unknown";
	}

	public static int getServerPortOrder(String server){
		if(server.equalsIgnoreCase("lobby")) return 0;
		else if(server.equalsIgnoreCase("survival")) return 1;
		else if(server.equalsIgnoreCase("creative")) return 2;
		else if(server.equalsIgnoreCase("bedwars")) return 3;
		else if(server.equalsIgnoreCase("hidenseek")) return 4;
		else if(server.equalsIgnoreCase("blockparty")) return 5;
		else if(server.equalsIgnoreCase("ragemode")) return 6;
		else if(server.equalsIgnoreCase("paintball")) return 7;
		else if(server.equalsIgnoreCase("dominate")) return 10;
		else if(server.equalsIgnoreCase("parkour")) return 9;
		return 0;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(args.length > 0){
			if(args[0].equalsIgnoreCase("reload")){
				this.reloadConfig();
				config.onReload();
				banmanazer.onReload();
				mute.onReload();
				antispam.onReload();
				chatlog.onReload();
				moderatorchat.onReload();
				chatnotice.onReload();
				if(eventcmds != null) eventcmds.onReload();
				restart.onReload();
				if(votes != null) votes.onReload();
				if(lobby != null) lobby.onReload();
				auth.onReload();
				if(checkresidences != null) checkresidences.onReload();
				chattips.onReload();
				if(residencesigns != null) residencesigns.onReload();
				if(disablespectator != null) disablespectator.onReload();
				if(cancelgrow != null) cancelgrow.onReload();
				if(trading != null) trading.onReload();
				if(mapcrafter != null) mapcrafter.onReload();
				if(parkour != null) parkour.onReload();
				if(cosmeticheads != null) cosmeticheads.onReload();
				if(quiz != null) quiz.onReload();
				chatprivate.onReload();
				chatadvert.onReload();
				chatadmin.onReload();
				chatformat.onReload();
				chatcommandspy.onReload();
				teleportrequests.onReload();
				report.onReload();
				signblockprotection.onReload();
				gamesreminder.onReload();
				schema.onReload();
				getServer().getLogger().info("RealCraft reloaded!");
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled = true)
	public void PlayerJoinEvent(PlayerJoinEvent event){
		event.setJoinMessage("");
		final Player player = event.getPlayer();
		if(this.playermanazer.getPlayerInfo(player).isLogged() || !serverName.equalsIgnoreCase("lobby")){
			Title.showTitle(player,"§6"+RealCraft.getServerName(this.serverName),1,3,1);
			Title.showSubTitle(player,"§3RealCraft.cz",1,3,1);
			this.playermanazer.getPlayerInfo(player).setLogged(true);
			for(PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());

			AuthLoginEvent callevent = new AuthLoginEvent(player);
			Bukkit.getServer().getPluginManager().callEvent(callevent);
		}

		if(!Spectator.isPlayerSpectating(player)){
			this.getServer().getScheduler().scheduleSyncDelayedTask(this,new Runnable(){
				@Override
				public void run(){
					for(Player user : Bukkit.getServer().getOnlinePlayers()){
						if(user != player && playermanazer.getPlayerInfo(user).isLogged()){
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
			PlayerInfo playerInfo = playermanazer.getPlayerInfo(event.getPlayer());
			if(playerInfo == null || playerInfo.getRank() < 45){
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
			if(user != player && this.playermanazer.getPlayerInfo(user).isLogged()){
				user.sendMessage(RealCraft.parseColors("&4[&c-&4] "+player.getDisplayName()+"&f je &coffline"));
			}
		}
	}

	public static String parseColors(String message){
		return ChatColor.translateAlternateColorCodes('&',message);
	}

	//https://gist.github.com/aadnk/3928137
	public class TabList {
		public TabList(){
			Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					for(Player player : Bukkit.getServer().getOnlinePlayers()){
						int ping = ((CraftPlayer)player).getHandle().ping;
						TabList.this.setPlayerHeaderFooter(player,"§r\n    §e§lRealCraft.cz§r    \n§r","§r\n§a"+ping+" ms §7| §e"+lobby.lobbymenu.getAllPlayersCount()+"/100\n§7play.realcraft.cz\n§r");
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
			ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),ListenerPriority.HIGH,PacketType.Play.Server.ADVANCEMENTS){
				@Override
				public void onPacketSending(PacketEvent event){
					if(RealCraft.getServerType() != ServerType.SURVIVAL && event.getPacketType() == PacketType.Play.Server.ADVANCEMENTS){
						event.setCancelled(true);
					}
				}
			});
		}
	}
}