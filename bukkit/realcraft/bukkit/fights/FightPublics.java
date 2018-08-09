package realcraft.bukkit.fights;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import net.md_5.bungee.api.ChatColor;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.arenas.FightPublicArena;
import realcraft.bukkit.fights.events.FightPlayerLeaveLobbyEvent;
import realcraft.bukkit.fights.spectators.FightPublicSpectator;
import realcraft.bukkit.fights.spectators.FightSpectator.FightSpectatorHotbarItem;
import realcraft.bukkit.utils.BorderUtil;
import realcraft.bukkit.utils.Title;
import realcraft.share.utils.RandomUtil;
import realcraft.share.utils.StringUtil;

public class FightPublics implements Runnable, Listener {

	private static final int GAME_TIME = 600;
	private static final String PREFIX = "§7[§eFFA§7]§r ";
	private static final String CHAR_HEART = "\u2764";

	private int gameTime;
	private int startTime;
	private int endTime;

	private FightState state;
	private FightPublicArena arena;
	private FightPublicSpectator spectator;
	private ArrayList<FightPublicArena> arenas = new ArrayList<FightPublicArena>();
	private FightPublicScoreboard scoreboard;

	public FightPublics(){
		this.loadArenas();
		this.changeArena();
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,20,20);
		Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				for(FightPublicArena arena : FightPublics.this.getArenas()){
					arena.getRegion().reset();
				}
			}
		},20);
	}

	@Override
	public void run(){
		if(this.getState() == FightState.STARTING){
			if(startTime > 0){
				startTime --;
				if(startTime == 0) this.startArena();
				else if(startTime <= 3){
					for(FightPlayer fPlayer : Fights.getFightPlayers(FightType.PUBLIC)){
						fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.BLOCK_NOTE_BLOCK_HAT,1f,1f);
						Title.showTitle(fPlayer.getPlayer(),Fights.NUMBERS[startTime-1],0,1.2,0);
					}
				}
			}
		}
		else if(this.getState() == FightState.INGAME){
			if(gameTime > 0){
				gameTime --;
				if(gameTime == 0) this.finishArena();
			}
		}
		else if(this.getState() == FightState.ENDING){
			if(endTime > 0){
				endTime --;
				if(endTime == 0) this.changeArena();
				else {
					if(endTime <= 5){
						for(FightPlayer fPlayer : Fights.getFightPlayers(FightType.PUBLIC)){
							fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.BLOCK_NOTE_BLOCK_HAT,1f,1f);
							Title.showTitle(fPlayer.getPlayer(),"§c§lKonec hry",0,1.2,0);
							Title.showSubTitle(fPlayer.getPlayer(),"§fZmena mapy za §e"+endTime+"s",0,1.2,0);
						}
					}
				}
			}
		}
		this.getScoreboard().update();
	}

	public int getGameTime(){
		return gameTime;
	}

	public void resetGameTime(){
		gameTime = GAME_TIME;
	}

	public FightState getState(){
		return state;
	}

	public void setState(FightState state){
		this.state = state;
	}

	public FightPublicArena getArena(){
		return arena;
	}

	public void setArena(FightPublicArena arena){
		this.arena = arena;
	}

	public ArrayList<FightPublicArena> getArenas(){
		return arenas;
	}

	private FightPublicArena getRandomArena(){
		return this.getRandomArena(1);
	}

	private FightPublicArena getRandomArena(int step){
		FightPublicArena arena = this.getArenas().get(RandomUtil.getRandomInteger(0,this.getArenas().size()-1));
		if(this.getArena() == arena && step < 100) arena = this.getRandomArena(step+1);
		return arena;
	}

	private void loadArenas(){
		File [] arenasFiles = new File(RealCraft.getInstance().getDataFolder()+"/fights/"+FightType.PUBLIC.toString()).listFiles();
		if(arenasFiles != null){
			for(File file : arenasFiles){
				if(file.isDirectory()){
					File config = new File(file.getPath()+"/config.yml");
					if(config.exists()){
						arenas.add(new FightPublicArena(Fights.getNewArenaId(),file.getName()));
					}
				}
			}
		}
	}

	public FightPublicScoreboard getScoreboard(){
		if(scoreboard == null) scoreboard = new FightPublicScoreboard();
		return scoreboard;
	}

	public FightPublicSpectator getSpectator(){
		if(spectator == null) spectator = new FightPublicSpectator();
		return spectator;
	}

	private void startArena(){
		this.resetGameTime();
		this.getArena().getWorld().setFullTime(this.getArena().getTime());
		this.setState(FightState.INGAME);
		for(FightPlayer fPlayer : Fights.getFightPlayers(FightType.PUBLIC)){
			fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.ENTITY_HORSE_ARMOR,1f,1f);
		}
	}

	private void finishArena(){
		endTime = 10;
		this.setState(FightState.ENDING);
		for(FightPlayer fPlayer : Fights.getFightPlayers(FightType.PUBLIC)){
			Title.showTitle(fPlayer.getPlayer(),"§c§lKonec hry",0.5,8,0.5);
			Title.showSubTitle(fPlayer.getPlayer(),"§7§kxx§r Losovani nove mapy §7§kxx",0.5,8,0.5);
		}
	}

	private void changeArena(){
		startTime = 7;
		this.resetGameTime();
		this.setState(FightState.STARTING);
		this.setArena(this.getRandomArena());
		for(FightPlayer fPlayer : Fights.getFightPlayers(FightType.PUBLIC)){
			fPlayer.getPlayer().teleport(Fights.getLobbyLocation());
			if(fPlayer.getState() == FightPlayerState.FIGHT) this.joinPlayer(fPlayer);
			else if(fPlayer.getState() == FightPlayerState.SPECTATOR) this.joinSpectator(fPlayer);
		}
	}

	@SuppressWarnings("deprecation")
	public void joinPlayer(FightPlayer fPlayer){
		if(fPlayer.getState() == FightPlayerState.NONE) Bukkit.getServer().getPluginManager().callEvent(new FightPlayerLeaveLobbyEvent(fPlayer));
		FightPublics.sendMessageInside("§b"+fPlayer.getUser().getName()+" §7se pripojil");
		fPlayer.setState(FightPlayerState.FIGHT);
		fPlayer.setQueue(false);
		fPlayer.reset();
		fPlayer.setArena(this.getArena());
		fPlayer.getPlayer().teleport(this.getArena().getRandomSpawn());
		fPlayer.getKit().equipPlayer(fPlayer);
		this.getScoreboard().addPlayer(fPlayer);
		for(FightPlayer fPlayer2 : Fights.getFightPlayers(FightType.PUBLIC)){
			if(fPlayer2.getState() == FightPlayerState.SPECTATOR) fPlayer.getPlayer().hidePlayer(fPlayer2.getPlayer());
		}
	}

	public void joinSpectator(FightPlayer fPlayer){
		FightPublics.sendMessageInside("§b"+fPlayer.getUser().getName()+" §7sleduje hru");
		fPlayer.setQueue(false);
		fPlayer.reset();
		fPlayer.setArena(this.getArena());
		this.getScoreboard().addPlayer(fPlayer);
		this.toggleSpectator(fPlayer);
	}

	@SuppressWarnings("deprecation")
	public void toggleSpectator(FightPlayer fPlayer){
		fPlayer.setState(FightPlayerState.SPECTATOR);
		fPlayer.getPlayer().teleport(this.getArena().getSpectatorLocation());
		fPlayer.reset();
		fPlayer.toggleSpectator();
		this.getScoreboard().addSpectator(fPlayer);
		for(FightPlayer fPlayer2 : Fights.getFightPlayers(FightType.PUBLIC)){
			if(fPlayer2.getState() != FightPlayerState.SPECTATOR) fPlayer2.getPlayer().hidePlayer(fPlayer.getPlayer());
			else fPlayer.getPlayer().showPlayer(fPlayer2.getPlayer());
		}
		for(FightSpectatorHotbarItem item : this.getSpectator().getHotbarItems()){
			fPlayer.getPlayer().getInventory().setItem(item.getIndex(),item.getItemStack());
		}
		BorderUtil.setBorder(fPlayer.getPlayer(),this.getArena().getSpectatorLocation(),this.getArena().getSpectatorRadius()*2);
	}

	public static void sendMessage(String message){
		Bukkit.broadcastMessage(PREFIX+message);
	}

	public static void sendMessageInside(String message){
		for(FightPlayer fPlayer : Fights.getFightPlayers(FightType.PUBLIC)){
			FightPublics.sendMessage(fPlayer,message);
		}
	}

	public static void sendMessage(FightPlayer fPlayer,String message){
		fPlayer.getPlayer().sendMessage(PREFIX+message);
	}

	@EventHandler(ignoreCancelled=true)
	public void EntityDamageEvent(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			if(this.getState() != FightState.INGAME){
				FightPlayer fPlayer = Fights.getFightPlayer((Player)event.getEntity());
				if(fPlayer.getArena().equals(this.getArena())){
					event.setCancelled(true);
					if(event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK) event.getEntity().setFireTicks(0);
				}
			}
			if(event.getCause() == DamageCause.VOID) ((Player)event.getEntity()).setHealth(0);
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(this.getState() != FightState.INGAME){
			if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
				FightPlayer fPlayer = Fights.getFightPlayer((Player)event.getEntity());
				FightPlayer fDamager = Fights.getFightPlayer((Player)event.getDamager());
				if(fPlayer.getArena().equals(this.getArena()) || fDamager.getArena().equals(this.getArena())) event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void EntityShootBowEvent(EntityShootBowEvent event){
		if(this.getState() != FightState.INGAME){
			if(event.getEntity() instanceof Player){
				FightPlayer fPlayer = Fights.getFightPlayer((Player)event.getEntity());
				if(fPlayer.getArena().equals(this.getArena())) event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		FightPlayer fPlayer = Fights.getFightPlayer(event.getPlayer());
		if(fPlayer.getArena().equals(this.getArena())){
			event.setRespawnLocation(this.getArena().getRandomSpawn());
			Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					if(fPlayer.getPlayer() != null && fPlayer.getState() == FightPlayerState.FIGHT && fPlayer.getArena().equals(FightPublics.this.getArena())) fPlayer.getKit().equipPlayer(fPlayer);
				}
			});
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void PlayerDeathEvent(PlayerDeathEvent event){
		FightPlayer fPlayer = Fights.getFightPlayer(event.getEntity());
		if(fPlayer.getArena().equals(this.getArena())){
			Player player = event.getEntity();
			Player killer = player.getKiller();
			if(killer != null && event.getEntity() != killer){
				killer.playSound(killer.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
				FightPublics.sendMessageInside("§c\u271E §b"+killer.getName()+" §7zabil hrace §b"+player.getName());
				if(killer.getInventory().getItem(8) != null && killer.getInventory().getItem(8).getType() == Material.GOLDEN_APPLE) killer.getInventory().setItem(8,new ItemStack(Material.GOLDEN_APPLE,killer.getInventory().getItem(8).getAmount()+1));
				else if(killer.getInventory().getItem(8) == null) killer.getInventory().setItem(8,new ItemStack(Material.GOLDEN_APPLE));
				else killer.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
				FightPlayer fKiller = Fights.getFightPlayer(killer);
				fKiller.getData().addKill();
				fPlayer.getData().addDeath();
				if(fKiller.getRank() != null && fPlayer.getRank() != null){
					int score = FightRank.getFFAScore(fKiller.getRank(),fPlayer.getRank());
					fKiller.getData().addScore(score);
					Title.sendActionBar(fKiller.getPlayer(),"§a+"+score+" bodu");
					fPlayer.getData().addScore(-score);
					Title.sendActionBar(fPlayer.getPlayer(),"§c-"+score+" bodu");
				}
			} else {
				FightPublics.sendMessageInside("§c\u271E §b"+player.getName()+" §7zemrel");
				fPlayer.getData().addDeath();
			}
		}
	}

	public class FightPublicScoreboard extends FightScoreboard {

		private Objective objective;

		public FightPublicScoreboard(){
			objective = this.getScoreboard().registerNewObjective("health","health");
			objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
			objective.setDisplayName(ChatColor.RED+CHAR_HEART);
		}

		@Override
		public void update(){
			int players = Fights.getFightPlayers(FightType.PUBLIC).size();
			this.clearLines();
			this.setTitle("§e§lFFA§r - "+StringUtil.timeFormat(FightPublics.this.getGameTime()));
			this.setLine(0,"");
			this.setLine(1,"§7Mapa: §6§l"+FightPublics.this.getArena().getName());
			this.setLine(2,"§7Hraje §f§l"+players+"§7 "+StringUtil.inflect(players,new String[]{"hrac","hraci","hracu"}));
			if(FightPublics.this.getState() == FightState.STARTING){
				this.setLine(3,"");
				this.setLine(4,"§fSouboj zacina");
				this.setLine(5,"§fza §a"+(startTime > 5 ? 5 : startTime)+" §fsekund");
				this.setLine(6,"");
				this.setLine(7,"§ewww.realcraft.cz");
			}
			else if(FightPublics.this.getState() == FightState.ENDING){
				this.setLine(3,"");
				this.setLine(4,"§fZmena mapy");
				this.setLine(5,"§fza §e"+endTime+" §fsekund");
				this.setLine(6,"");
				this.setLine(7,"§ewww.realcraft.cz");
			}
			else if(FightPublics.this.getState() == FightState.INGAME){
				this.setLine(3,"");
				this.setLine(4,"§ewww.realcraft.cz");
			}
			super.update();
		}
	}
}