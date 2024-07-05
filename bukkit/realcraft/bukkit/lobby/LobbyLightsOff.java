package realcraft.bukkit.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.Button;
import org.bukkit.scheduler.BukkitRunnable;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.coins.Coins;
import realcraft.bukkit.develop.LampControl;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.share.utils.RandomUtil;
import realcraft.share.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class LobbyLightsOff implements Listener {

	private static final int PLAY_TIMEOUT = 60*1000;
	private static final int REWARD = 100;
	private static final int SIZE_X = 5;
	private static final int SIZE_Y = 4;

	private Location[] locations;
	private Location resetLocation;
	private boolean[][] lamps = new boolean[SIZE_Y][SIZE_X];
	private boolean running = true;
	private long lastClick;

	private HashMap<String,Long> lastPlayed = new HashMap<>();

	public LobbyLightsOff(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				LobbyLightsOff.this.reset();
			}
		});
	}

	private Location[] getLocations(){
		if(locations == null){
			File file = new File(RealCraft.getInstance().getDataFolder()+"/lightsoff.yml");
			if(file.exists()){
				FileConfiguration config = new YamlConfiguration();
				try {
					config.load(file);
				} catch (Exception e){
					e.printStackTrace();
				}
				locations = new Location[2];
				locations[0] = LocationUtil.getConfigLocation(config,"minLoc");
				locations[1] = LocationUtil.getConfigLocation(config,"maxLoc");
				resetLocation = LocationUtil.getConfigLocation(config,"resetLoc");
			}
		}
		return locations;
	}

	private void reset(){
		lastClick = System.currentTimeMillis();
		running = true;
		for(int i=0;i<SIZE_Y;i++){
			for(int a=0;a<SIZE_X;a++){
				lamps[i][a] = false;
			}
		}
		for(int i=0;i<SIZE_Y;i++){
			for(int a=0;a<SIZE_X;a++){
				if(RandomUtil.getRandomInteger(1,10) >= 8){
					this.clickLamp(a,i);
				}
			}
		}
		this.draw();
	}

	private boolean getLamp(int x,int y){
		return lamps[y][x];
	}

	private void setLamp(int x,int y,boolean state){
		lamps[y][x] = state;
	}

	private void toggleLamp(int x,int y){
		this.setLamp(x,y,!lamps[y][x]);
	}

	private ArrayList<int[]> getSurroundingLamps(int x,int y){
		ArrayList<int[]> lamps = new ArrayList<int[]>();
		if(x > 0) lamps.add(new int[]{x-1,y});
		if(x < SIZE_X-1) lamps.add(new int[]{x+1,y});
		if(y > 0) lamps.add(new int[]{x,y-1});
		if(y < SIZE_Y-1) lamps.add(new int[]{x,y+1});
		return lamps;
	}

	private void clickLamp(Player player,int x,int y){
		if(!running) return;
		if(lastPlayed.containsKey(player.getName()) && lastPlayed.get(player.getName())+PLAY_TIMEOUT > System.currentTimeMillis()){
			int minutes = (int)Math.ceil(((lastPlayed.get(player.getName())+PLAY_TIMEOUT)-(System.currentTimeMillis()))/1000/60f);
			player.sendMessage("§cHrat znovu muzes za "+minutes+" "+StringUtil.inflect(minutes,new String[]{"minutu","minuty","minut"}));
			return;
		}
		this.clickLamp(x,y);
		if(this.isFinished()){
			running = false;
			lastPlayed.put(player.getName(),System.currentTimeMillis());
			this.runFinish();
			int coins = Users.getUser(player).giveCoins(REWARD,false);
			Bukkit.broadcastMessage("§d[Lampy] §6"+player.getName()+" §fzhasnul vsechny lampy a ziskava §a+"+coins+" coins");
			Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
				public void run(){
					Coins.runCoinsEffect(player,"§aLampy zhasnuty",coins);
				}
			},20);
		}
	}

	private void clickLamp(int x,int y){
		if(!running) return;
		lastClick = System.currentTimeMillis();
		this.toggleLamp(x,y);
		for(int[] lamp : this.getSurroundingLamps(x,y)){
			this.toggleLamp(lamp[0],lamp[1]);
		}
		this.draw();
	}

	private boolean isFinished(){
		for(int i=0;i<SIZE_Y;i++){
			for(int a=0;a<SIZE_X;a++){
				if(lamps[i][a]) return false;
			}
		}
		return true;
	}

	private void runFinish(){
		BukkitRunnable task = new BukkitRunnable(){
			int step = 0;
			@Override
			public void run(){
				for(int i=0;i<SIZE_Y;i++){
					for(int a=0;a<SIZE_X;a++){
						LobbyLightsOff.this.setLamp(a,i,(step%2 == 0));
					}
				}
				LobbyLightsOff.this.draw();
				step ++;
				if(step >= 15){
					this.cancel();
					LobbyLightsOff.this.reset();
				}
			}
		};
		task.runTaskTimer(RealCraft.getInstance(),4,4);
	}

	private void draw(){
		if(this.getLocations() == null) return;
		Location location1 = this.getLocations()[0];
		Location location2 = this.getLocations()[1];
		int ix = 0;
		int iy = 0;
		boolean xDiff = (location1.getBlockX() < location2.getBlockX());
		boolean zDiff = (location1.getBlockZ() < location2.getBlockZ());
		int y = Math.max(location1.getBlockY(),location2.getBlockY());
		int minY = Math.min(location1.getBlockY(),location2.getBlockY());
		while(y >= minY){
			int x = location1.getBlockX();
			while(xDiff ? x <= location2.getBlockX() : x >= location2.getBlockX()){
				int z = location1.getBlockZ();
				while(zDiff ? z <= location2.getBlockZ() : z >= location2.getBlockZ()){
					Location location = new Location(location1.getWorld(),x,y,z);
					LampControl.switchLamp(location.getBlock(),LobbyLightsOff.this.getLamp(ix,iy));
					ix ++;
					if(ix == SIZE_X){
						ix = 0;
						iy ++;
					}
					z += (zDiff ? 1 : -1);
				}
				x += (xDiff ? 1 : -1);
			}
			y --;
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void PlayerInteractEvent(PlayerInteractEvent event){
		Block block = event.getClickedBlock();
		if(block != null && event.getHand() == EquipmentSlot.HAND){
			if(block.getType() == Material.REDSTONE_LAMP){
				if(this.getLocations() == null) return;
				Location location1 = this.getLocations()[0];
				Location location2 = this.getLocations()[1];
				int ix = 0;
				int iy = 0;
				boolean xDiff = (location1.getBlockX() < location2.getBlockX());
				boolean zDiff = (location1.getBlockZ() < location2.getBlockZ());
				int y = Math.max(location1.getBlockY(),location2.getBlockY());
				int minY = Math.min(location1.getBlockY(),location2.getBlockY());
				while(y >= minY){
					int x = location1.getBlockX();
					while(xDiff ? x <= location2.getBlockX() : x >= location2.getBlockX()){
						int z = location1.getBlockZ();
						while(zDiff ? z <= location2.getBlockZ() : z >= location2.getBlockZ()){
							Location location = new Location(location1.getWorld(),x,y,z);
							if(location.equals(block.getLocation())){
								this.clickLamp(event.getPlayer(),ix,iy);
								event.setCancelled(true);
								return;
							}
							ix ++;
							if(ix == SIZE_X){
								ix = 0;
								iy ++;
							}
							z += (zDiff ? 1 : -1);
						}
						x += (xDiff ? 1 : -1);
					}
					y --;
				}
			}
			else if(block.getType() == Material.STONE_BUTTON || block.getType() == Material.OAK_BUTTON){
				if(block.getLocation().equals(resetLocation)){
					Button button = (Button)block.getState().getData();
					if(!button.isPowered()){
						if(lastClick+(10*1000) > System.currentTimeMillis()){
							event.getPlayer().sendMessage("§cTuto hru prave nekdo hraje!");
							return;
						}
						event.getPlayer().sendMessage("§d[Lampy] §fHra resetovana - zhasni vsechny lampy.");
						event.getPlayer().playSound(event.getPlayer().getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
						this.reset();
					}
				}
			}
		}
	}

	@EventHandler
	public void BlockRedstoneEvent(BlockRedstoneEvent event){
		Block block = event.getBlock();
		if(block != null && (block.getType() == Material.LEGACY_REDSTONE_LAMP_ON || block.getType() == Material.LEGACY_REDSTONE_LAMP_OFF)){
			if(this.getLocations() == null) return;
			Location location1 = this.getLocations()[0];
			Location location2 = this.getLocations()[1];
			boolean xDiff = (location1.getBlockX() < location2.getBlockX());
			boolean zDiff = (location1.getBlockZ() < location2.getBlockZ());
			int y = Math.max(location1.getBlockY(),location2.getBlockY());
			int minY = Math.min(location1.getBlockY(),location2.getBlockY());
			while(y >= minY){
				int x = location1.getBlockX();
				while(xDiff ? x <= location2.getBlockX() : x >= location2.getBlockX()){
					int z = location1.getBlockZ();
					while(zDiff ? z <= location2.getBlockZ() : z >= location2.getBlockZ()){
						Location location = new Location(location1.getWorld(),x,y,z);
						if(location.equals(block.getLocation())){
							event.setNewCurrent(15);
							return;
						}
						z += (zDiff ? 1 : -1);
					}
					x += (xDiff ? 1 : -1);
				}
				y --;
			}
		}
	}
}