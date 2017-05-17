package com.realcraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.inventivetalent.tabapi.TabAPI;

import com.anticheat.AntiCheat;
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
import com.realcraft.database.MySQL;
import com.realcraft.heads.CosmeticHeads;
import com.realcraft.lobby.Lobby;
import com.realcraft.mapcrafter.MapCrafter;
import com.realcraft.minihry.EventCmds;
import com.realcraft.minihry.GamesPlayersCount;
import com.realcraft.minihry.GamesReminder;
import com.realcraft.minihry.SignBlockProtection;
import com.realcraft.moderatorchat.ModeratorChat;
import com.realcraft.mute.Mute;
import com.realcraft.playermanazer.PlayerManazer;
import com.realcraft.pvp.CompassTracker;
import com.realcraft.pvp.DisableFly;
import com.realcraft.quiz.Quiz;
import com.realcraft.report.Report;
import com.realcraft.residences.CheckResidences;
import com.realcraft.residences.ResidenceSigns;
import com.realcraft.restart.Restart;
import com.realcraft.schema.Schema;
import com.realcraft.skins.Skins;
import com.realcraft.teleport.TeleportRequests;
import com.realcraft.test.Test;
import com.realcraft.trading.Trading;
import com.realcraft.utils.Glow;
import com.realcraft.utils.Title;
import com.realcraft.votes.Votes;

public class RealCraft extends JavaPlugin implements Listener {
	private static RealCraft instance;

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
	private GamesPlayersCount gamesplayerscount;
	private DisableFly disablefly;
	private CompassTracker compasstracker;
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

	public String serverName;

	public Essentials essentials;

	public static RealCraft getInstance(){
		return instance;
	}

	public void onEnable(){
		instance = this;
		serverName = getServer().getServerName();
		essentials = (Essentials) this.getServer().getPluginManager().getPlugin("Essentials");
		config = new Config(this);
		db = new MySQL(this);
		playermanazer = new PlayerManazer(this);
		banmanazer = new BanManazer(this);
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
		if(serverName.equalsIgnoreCase("lobby")){
			auth = new Auth(this);
			eventcmds = new EventCmds(this);
			quiz = new Quiz(this);
			cancelgrow = new CancelGrow(this);
			new Test();
		}
		else if(serverName.equalsIgnoreCase("survival")){
			checkresidences = new CheckResidences(this);
			residencesigns = new ResidenceSigns(this);
			trading = new Trading(this);
			mapcrafter = new MapCrafter(this);
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
		new TabList();
		this.getServer().getPluginManager().registerEvents(this,this);
	}

	public void onDisable(){
		config.onDisable();
		db.onDisable();
		if(lobby != null) lobby.onDisable();
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
		else if(server.equalsIgnoreCase("uhc")) return "UHC";
		else if(server.equalsIgnoreCase("parkour")) return "Parkour";
		return "unknown";
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
				anticheat.onReload();
				chatlog.onReload();
				moderatorchat.onReload();
				chatnotice.onReload();
				if(eventcmds != null) eventcmds.onReload();
				restart.onReload();
				if(votes != null) votes.onReload();
				if(lobby != null) lobby.onReload();
				auth.onReload();
				if(gamesplayerscount != null) gamesplayerscount.onReload();
				if(disablefly != null) disablefly.onReload();
				if(compasstracker != null) compasstracker.onReload();
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

		if(!AntiCheat.isPlayerSpectating(player)){
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

	public class TabList {
		public TabList(){
			getServer().getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					for(Player player : Bukkit.getServer().getOnlinePlayers()){
						int ping = ((CraftPlayer)player).getHandle().ping;
						TabAPI.setHeaderFooter(player,new String[]{"§r","    §e§lRealCraft.cz§r    ","§r"},new String[]{"§r","§a"+ping+" ms §7| §e"+lobby.lobbymenu.getAllPlayersCount()+"/100","§7play.realcraft.cz","§r"});
						if(!player.getPlayerListName().equalsIgnoreCase(player.getDisplayName())) player.setPlayerListName(player.getDisplayName());
					}
				}
			},2*20,2*20);
		}
	}
}