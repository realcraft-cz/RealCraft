package com.realcraft.lobby;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.realcraft.RealCraft;
import com.realcraft.playermanazer.PlayerManazer;
import com.realcraft.utils.FireworkUtil;
import com.realcraft.utils.LocationUtil;
import com.realcraft.utils.Particles;
import com.realcraft.utils.RandomUtil;
import com.realcraft.utils.Title;

//http://weblog.jamisbuck.org/2011/2/7/maze-generation-algorithm-recap
//https://java.net/projects/trackbot-greenfoot/sources/svn/content/trunk/Version1/TrackBot/maze/RecursiveBacktrackerMazeGenerator.java

public class LobbyLabyrinth implements Listener, Runnable {

	private RealCraft plugin;
	private int size;
	private int thick;
	private int height = 5;
	private Location location;
	private Location finish;
	private LabyrinthField[][] fields = null;
	private static final int REWARD = 100;
	private long pickedUp = 0;

	public LobbyLabyrinth(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		this.loadLabyrinth();
		this.generate();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,this,20,20);
	}

	public void onReload(){
	}

	private void loadLabyrinth(){
		size = plugin.config.getConfig().getInt("labyrinth.size");
		thick = plugin.config.getConfig().getInt("labyrinth.thick");
		location = LocationUtil.getConfigLocation(plugin.config.getConfig(),"labyrinth.location");
		finish = LocationUtil.getConfigLocation(plugin.config.getConfig(),"labyrinth.finish");
	}

	@Override
	public void run(){
		boolean found = false;
		for(Entity entity : finish.getWorld().getNearbyEntities(finish,2.0,2.0,2.0)){
			if(entity.getType() == EntityType.DROPPED_ITEM){
				if(((Item)entity).getItemStack().getType() == Material.EMERALD){
					found = true;
				}
			}
		}
		if(!found && pickedUp+10*1000 < System.currentTimeMillis()){
			Item item = finish.getWorld().dropItem(finish,new ItemStack(Material.EMERALD));
			item.setVelocity(new Vector(0,0,0));
			item.setPickupDelay(20);
			item.teleport(finish);
		}
		if(found) Particles.VILLAGER_HAPPY.display(0.7f,1f,0.7f,0f,10,finish.clone().add(0,1.0,0),64.0);
	}

	@EventHandler
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		String command = event.getMessage().substring(1);
		if(command.equalsIgnoreCase("maze")){
			LobbyLabyrinth.this.generate();
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void PlayerPickupItemEvent(PlayerPickupItemEvent event){
		if(event.getItem().getItemStack().getType() == Material.EMERALD && event.getItem().getLocation().distanceSquared(finish) < 10){
			Player player = event.getPlayer();
			final int reward = PlayerManazer.getPlayerInfo(player).giveCoins(REWARD);
			FireworkUtil.spawnFirework(finish.clone().add(0,0.5,0),FireworkEffect.Type.BALL,Color.LIME,true,false);
			Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					Title.showTitle(player,"§aLabyrint dokoncen",0.5,20,0.5);
					player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
					Bukkit.broadcastMessage("§b[Labyrint] §6"+player.getName()+"§f dokoncil labyrint a ziskava §a+"+reward+" coins");
				}
			},5);
			Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
				public void run(){
					PlayerManazer.getPlayerInfo(player).runCoinsEffect("§aLabyrint dokoncen",reward);
				}
			},20);
			Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
				public void run(){
					player.teleport(plugin.auth.getServerSpawn());
					player.playSound(player.getLocation(),Sound.ENTITY_BAT_TAKEOFF,0.5f,1);
				}
			},5*20);
			pickedUp = System.currentTimeMillis();
			event.getItem().remove();
			event.setCancelled(true);
		}
	}

	private void generate(){
		this.init();
		this.clear();
		this.generateFields();
		this.draw();
	}

	private void clear(){
		for(int x=1;x<=size;x++){
			for(int y=1;y<=size;y++){
				fields[x][y].clear();
			}
		}
	}

	private void init(){
		fields = new LabyrinthField[size+2][size+2];
		for(int x=0;x<size+2;x++){
			for(int y=0;y<size+2;y++){
				fields[x][y] = new LabyrinthField(x,y);
				fields[x][y].north = true;
				fields[x][y].east = true;
				fields[x][y].south = true;
				fields[x][y].west = true;
				fields[x][y].visited = false;
			}
		}
	}

	private void generateFields(){
		LinkedList<TempCell> stack = new LinkedList<TempCell>();

		TempCell cell = new TempCell(1,1);
		stack.addFirst(cell);
		int[] neighbours = new int[4];

		do {
			fields[cell.x][cell.y].visited = true;

			int freeNeighbourCount = 0;
			for(int i=0;i<4;i++){
				switch(i){
					case 0:{
						if (cell.y > 1 && !fields[cell.x][cell.y-1].visited){
							neighbours[freeNeighbourCount++] = i;
						}
						break;
					}
					case 1:{
						if (cell.x <= size-1 && !fields[cell.x+1][cell.y].visited){
							neighbours[freeNeighbourCount++] = i;
						}
						break;
					}
					case 2:{
						if (cell.y <= size-1 && !fields[cell.x][cell.y+1].visited){
							neighbours[freeNeighbourCount++] = i;
						}
						break;
					}
					case 3:{
						if (cell.x > 1 && !fields[cell.x-1][cell.y].visited){
							neighbours[freeNeighbourCount++] = i;
						}
						break;
					}
				}
			}

			if(freeNeighbourCount > 0){
				stack.addFirst(cell);
				cell = new TempCell(cell.x,cell.y);
				switch(neighbours[RandomUtil.getRandomInteger(0, freeNeighbourCount-1)]){
					case 0:{
						fields[cell.x][cell.y].south = false;
						cell.y --;
						fields[cell.x][cell.y].north = false;
						break;
					}
					case 1:{
						fields[cell.x][cell.y].east = false;
						cell.x ++;
						fields[cell.x][cell.y].west = false;
						break;
					}
					case 2:{
						fields[cell.x][cell.y].north = false;
						cell.y ++;
						fields[cell.x][cell.y].south = false;
						break;
					}
					case 3:{
						fields[cell.x][cell.y].west = false;
						cell.x --;
						fields[cell.x][cell.y].east = false;
						break;
					}
				}
			} else {
				cell = stack.removeFirst();
			}
		} while (!stack.isEmpty());
	}

	private void draw(){
		for(int x=1;x<=size;x++){
			for(int y=1;y<=size;y++){
				fields[x][y].draw();
			}
		}
	}

	private class TempCell {
		private int x,y;
		public TempCell(int x,int y){
			this.x = x;
			this.y = y;
		}
	}

	private static final Material[][] wallTypes = new Material[][]{
		{Material.LOG,Material.SMOOTH_BRICK,Material.SMOOTH_BRICK,Material.SMOOTH_BRICK,Material.LOG},
		{Material.LOG,Material.SMOOTH_BRICK,Material.STEP,Material.SMOOTH_BRICK,Material.LOG},
		{Material.LOG,Material.STEP,Material.STEP,Material.STEP,Material.LOG},
		{Material.LOG,Material.SMOOTH_BRICK,Material.STEP,Material.SMOOTH_BRICK,Material.LOG},
		{Material.LOG,Material.SMOOTH_BRICK,Material.SMOOTH_BRICK,Material.SMOOTH_BRICK,Material.LOG}
	};

	private static final byte[][] wallDatas = new byte[][]{
		{0,2,0,2,0},
		{0,0,3,0,0},
		{0,3,3,3,0},
		{0,0,3,0,0},
		{0,2,0,2,0},
	};

	private class LabyrinthField {

		public int x;
		public int y;
		public boolean visited;
		public boolean north = true;
		public boolean east = true;
		public boolean south = true;
		public boolean west = true;

		public LabyrinthField(int x,int y){
			this.x = x;
			this.y = y;
		}

		public void clear(){
			this.empty(x,y,x+1,y);
			this.empty(x,y+1,x+1,y+1);
			this.empty(x,y,x,y+1);
			this.empty(x+1,y,x+1,y+1);
		}

		public void draw(){
			if(south) this.line(x,y,x+1,y);
			if(north) this.line(x,y+1,x+1,y+1);
			if(west) this.line(x,y,x,y+1);
			if(east) this.line(x+1,y,x+1,y+1);
		}

		private void empty(int x1,int y1,int x2,int y2){
			x1 = x1*thick-(thick+1);
			y1 = y1*thick-(thick+1);
			x2 = x2*thick-(thick+1);
			y2 = y2*thick-(thick+1);
			for(int i=0;i<=thick;i++){
				for(int y=0;y<height;y++){
					location.clone().add(x1+(x1 != x2 ? i : 0),y,y1+(y1 != y2 ? i : 0)).getBlock().setType(Material.AIR);
				}
			}
		}

		@SuppressWarnings("deprecation")
		private void line(int x1,int y1,int x2,int y2){
			x1 = x1*thick-(thick+1);
			y1 = y1*thick-(thick+1);
			x2 = x2*thick-(thick+1);
			y2 = y2*thick-(thick+1);
			for(int i=0;i<=thick;i++){
				for(int y=0;y<height;y++){
					location.clone().add(x1+(x1 != x2 ? i : 0),y,y1+(y1 != y2 ? i : 0)).getBlock().setType(wallTypes[y][i]);
					location.clone().add(x1+(x1 != x2 ? i : 0),y,y1+(y1 != y2 ? i : 0)).getBlock().setData(wallDatas[y][i]);
				}
			}
		}
	}
}