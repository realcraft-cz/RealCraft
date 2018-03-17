package realcraft.bukkit.lobby;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import cz.ceph.LampControl.utils.SwitchBlock;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.playermanazer.PlayerManazer;
import realcraft.bukkit.utils.FireworkUtil;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.utils.Particles;
import realcraft.bukkit.utils.RandomUtil;
import realcraft.bukkit.utils.StringUtil;
import realcraft.bukkit.utils.Title;

//http://weblog.jamisbuck.org/2011/2/7/maze-generation-algorithm-recap
//https://java.net/projects/trackbot-greenfoot/sources/svn/content/trunk/Version1/TrackBot/maze/RecursiveBacktrackerMazeGenerator.java

public class LobbyLabyrinth implements Listener, Runnable {

	private RealCraft plugin;
	private static final int REWARD = 100;
	private static final int REPEAT_LIMIT = 600;
	private int sizeX;
	private int sizeY;
	private int thick;
	private int height = 5;
	private Location location;
	private Location finish;
	private Location teleport;
	private Location[][] empties = new Location[2][2];
	private LabyrinthField[][] fields = null;
	private long pickedUp = 0;
	private boolean enabled = true;
	private boolean toRegenerate = false;
	private SwitchBlock switchBlock;
	private HashMap<String,LabyrinthPlayer> players = new HashMap<String,LabyrinthPlayer>();

	public LobbyLabyrinth(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		this.loadLabyrinth();
		this.generate();
		if(enabled) Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,this,20,20);
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),ListenerPriority.HIGH,PacketType.Play.Server.BLOCK_CHANGE){
			@Override
			public void onPacketSending(PacketEvent event){
				if(event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE && LobbyLabyrinth.this.getLabyrinthPlayer(event.getPlayer()).hasLimit()){
					Location blockLocation = event.getPacket().getBlockPositionModifier().read(0).toLocation(location.getWorld());
					Material material = event.getPacket().getBlockData().read(0).getType();
					Vector vecMin = empties[0][0].toVector();
					Vector vecMax = empties[0][1].toVector();
					if(material == Material.AIR && blockLocation.toVector().isInAABB(vecMin,vecMax)){
						event.setCancelled(true);
					}
				}
			}
		});
	}

	public void onReload(){
	}

	public LabyrinthPlayer getLabyrinthPlayer(Player player){
		if(!players.containsKey(player.getName())) players.put(player.getName(),new LabyrinthPlayer(player));
		return players.get(player.getName());
	}

	private void loadLabyrinth(){
		sizeX = plugin.config.getConfig().getInt("labyrinth.sizeX",0);
		sizeY = plugin.config.getConfig().getInt("labyrinth.sizeY",0);
		thick = plugin.config.getConfig().getInt("labyrinth.thick",0);
		if(sizeX == 0 || sizeY == 0){
			enabled = false;
			return;
		}
		location = LocationUtil.getConfigLocation(plugin.config.getConfig(),"labyrinth.location");
		finish = LocationUtil.getConfigLocation(plugin.config.getConfig(),"labyrinth.finish");
		teleport = LocationUtil.getConfigLocation(plugin.config.getConfig(),"labyrinth.teleport");
		empties[0][0] = LocationUtil.getConfigLocation(plugin.config.getConfig(),"labyrinth.empty1.min");
		empties[0][1] = LocationUtil.getConfigLocation(plugin.config.getConfig(),"labyrinth.empty1.max");
		empties[1][0] = LocationUtil.getConfigLocation(plugin.config.getConfig(),"labyrinth.empty2.min");
		empties[1][1] = LocationUtil.getConfigLocation(plugin.config.getConfig(),"labyrinth.empty2.max");
		switchBlock = new SwitchBlock();
		switchBlock.initWorld(location.getWorld());
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
		this.tryGenerate();
		this.checkEntrances();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void PlayerPickupItemEvent(PlayerPickupItemEvent event){
		if(event.getItem().getItemStack().getType() == Material.EMERALD && event.getItem().getLocation().distanceSquared(finish) < 10){
			Player player = event.getPlayer();
			if(!this.getLabyrinthPlayer(player).hasLimit()){
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
						player.teleport(teleport);
						player.playSound(player.getLocation(),Sound.ENTITY_BAT_TAKEOFF,0.5f,1);
					}
				},5*20);
				pickedUp = System.currentTimeMillis();
				toRegenerate = true;
				this.getLabyrinthPlayer(player).setLimit();
				this.loadEntrance(player);
				event.getItem().remove();
			}
			event.setCancelled(true);
		}
	}

	private void checkEntrances(){
		Vector minVec = location.clone().add(-1,-1,-1).toVector();
		Vector maxVec = location.clone().add(sizeX*thick,4,sizeY*thick).toVector();
		for(Player player : Bukkit.getOnlinePlayers()){
			this.loadEntrance(player);
			if(this.getLabyrinthPlayer(player).hasLimit() && player.getWorld().getName().equalsIgnoreCase("world")){
				this.getLabyrinthPlayer(player).updateHologram(player);
				if(player.getLocation().toVector().isInAABB(minVec,maxVec)){
					player.teleport(new Location(player.getWorld(),-70.5,53,-64.5,-90,0));
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void loadEntrance(Player player){
		if(player.getWorld().getName().equalsIgnoreCase("world")){
			if(player.getLocation().distanceSquared(empties[0][0]) < 16*16){
				Material material = (this.getLabyrinthPlayer(player).hasLimit() ? Material.IRON_FENCE : Material.AIR);
				for(int x=empties[0][0].getBlockX();x<=empties[0][1].getBlockX();x++){
					for(int y=empties[0][0].getBlockY();y<=empties[0][1].getBlockY();y++){
						for(int z=empties[0][0].getBlockZ();z<=empties[0][1].getBlockZ();z++){
							player.sendBlockChange(empties[0][0].getWorld().getBlockAt(x,y,z).getLocation(),material,(byte)0);
						}
					}
				}
			}
		}
	}

	private void tryGenerate(){
		if(toRegenerate){
			Vector minVec = location.clone().add(-1,-1,-1).toVector();
			Vector maxVec = location.clone().add(sizeX*thick,4,sizeY*thick).toVector();
			boolean found = false;
			for(Player player : Bukkit.getOnlinePlayers()){
				if(player.getLocation().toVector().isInAABB(minVec,maxVec)){
					found = true;
					break;
				}
			}
			if(!found){
				toRegenerate = false;
				LobbyLabyrinth.this.generate();
			}
		}
	}

	private void generate(){
		if(!enabled) return;
		this.init();
		this.clear();
		this.generateFields();
		this.draw();
		this.clearCorridors();
		this.makeHoles();
		//this.makeLamps();
		//this.makeTraps();
	}

	private void clear(){
		for(int x=1;x<=sizeX;x++){
			for(int y=1;y<=sizeY;y++){
				fields[x][y].clear();
			}
		}
	}

	private void clearCorridors(){
		for(int x=0;x<sizeX;x++){
			for(int y=0;y<sizeY;y++){
				for(int x2=0;x2<3;x2++){
					for(int y2=0;y2<5;y2++){
						for(int z2=0;z2<3;z2++){
							location.clone().add(x*thick+x2,y2,y*thick+z2).getBlock().setType(Material.AIR);
						}
					}
				}
				for(int x2=0;x2<3;x2++){
					for(int z2=0;z2<3;z2++){
						this.setRandomFloor(location.clone().add(x*thick+x2,-1,y*thick+z2).getBlock());
						this.setRandomFloor(location.clone().add(x*thick+x2,-2,y*thick+z2).getBlock());
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void setRandomFloor(Block block){
		switch(RandomUtil.getRandomInteger(0,5)){
			case 0:{
				block.setType(Material.STONE);
				block.setData((byte)0);
				break;
			}
			case 1:{
				block.setType(Material.SMOOTH_BRICK);
				block.setData((byte)0);
				break;
			}
			case 2:{
				block.setType(Material.SMOOTH_BRICK);
				block.setData((byte)2);
				break;
			}
			case 3:{
				block.setType(Material.COBBLESTONE);
				block.setData((byte)0);
				break;
			}
			case 4:{
				block.setType(Material.GRAVEL);
				block.setData((byte)0);
				break;
			}
			case 5:{
				block.setType(Material.STONE);
				block.setData((byte)5);
				break;
			}
		}
	}

	private void init(){
		fields = new LabyrinthField[sizeX+2][sizeY+2];
		for(int x=0;x<sizeX+2;x++){
			for(int y=0;y<sizeY+2;y++){
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
						if (cell.x <= sizeX-1 && !fields[cell.x+1][cell.y].visited){
							neighbours[freeNeighbourCount++] = i;
						}
						break;
					}
					case 2:{
						if (cell.y <= sizeY-1 && !fields[cell.x][cell.y+1].visited){
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
		for(int x=1;x<=sizeX;x++){
			for(int y=1;y<=sizeY;y++){
				fields[x][y].draw();
			}
		}
	}

	private void makeHoles(){
		for(Location[] locations : empties){
			for(int x=locations[0].getBlockX();x<=locations[1].getBlockX();x++){
				for(int y=locations[0].getBlockY();y<=locations[1].getBlockY();y++){
					for(int z=locations[0].getBlockZ();z<=locations[1].getBlockZ();z++){
						locations[0].getWorld().getBlockAt(x,y,z).setType(Material.AIR);
					}
				}
			}
		}
	}

	/*private void makeLamps(){
		for(int x=0;x<sizeX;x++){
			for(int y=0;y<sizeY;y++){
				boolean on = (x%3 == 0 && y%3 == 0);
				location.clone().add(x*thick+1,5,y*thick+1).getBlock().setType(Material.IRON_TRAPDOOR);
				Block block = location.clone().add(x*thick+1,6,y*thick+1).getBlock();
				if(on){
					block.setType(Material.REDSTONE_LAMP_ON);
					switchBlock.switchLamp(block,on);
				} else {
					block.setType(Material.STONE);
				}
			}
		}
	}*/

	/*private void makeTraps(){
		for(int x=0;x<sizeX;x++){
			for(int y=0;y<sizeY;y++){
				if(RandomUtil.getRandomInteger(1,15) == 1){
					location.clone().add(x*thick+0,RandomUtil.getRandomInteger(0,3),y*thick+0).getBlock().setType(Material.WEB);
					location.clone().add(x*thick+2,RandomUtil.getRandomInteger(0,3),y*thick+0).getBlock().setType(Material.WEB);
					location.clone().add(x*thick+0,RandomUtil.getRandomInteger(0,3),y*thick+2).getBlock().setType(Material.WEB);
					location.clone().add(x*thick+2,RandomUtil.getRandomInteger(0,3),y*thick+2).getBlock().setType(Material.WEB);
					for(int x2=0;x2<3;x2++){
						for(int z2=0;z2<3;z2++){
							location.clone().add(x*thick+x2,-1,y*thick+z2).getBlock().setType(Material.LAVA);
							location.clone().add(x*thick+x2,-2,y*thick+z2).getBlock().setType(Material.LAVA);
						}
					}
				}
			}
		}
	}*/

	private class TempCell {
		private int x,y;
		public TempCell(int x,int y){
			this.x = x;
			this.y = y;
		}
	}

	private static final Material[][] wallTypes = new Material[][]{
		{Material.LOG,Material.SMOOTH_BRICK,Material.SMOOTH_BRICK,Material.SMOOTH_BRICK,Material.LOG},
		{Material.LOG,Material.LEAVES,Material.STEP,Material.LEAVES,Material.LOG},
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

	private class LabyrinthPlayer {

		private Long finished = 0L;
		private Hologram hologram;
		private int minutes;

		public LabyrinthPlayer(Player player){
			hologram = HologramsAPI.createHologram(plugin,new Location(player.getWorld(),-69.5,56,-64.5,-90,0));
			hologram.getVisibilityManager().setVisibleByDefault(false);
			hologram.insertTextLine(0,"§7Labyrint muzes znovu");
			hologram.insertTextLine(1,"§7navstivit za 10 minut");
		}

		public boolean hasLimit(){
			return (finished+REPEAT_LIMIT*1000 > System.currentTimeMillis());
		}

		public void setLimit(){
			finished = System.currentTimeMillis();
		}

		public void updateHologram(Player player){
			if(this.hasLimit()){
				if(!hologram.getVisibilityManager().isVisibleTo(player)) hologram.getVisibilityManager().showTo(player);
				int tmpMinutes = (int)Math.ceil(((finished+REPEAT_LIMIT*1000)-(System.currentTimeMillis()))/1000/60f);
				if(tmpMinutes != minutes){
					minutes = tmpMinutes;
					hologram.removeLine(1);
					hologram.insertTextLine(1,"§7navstivit za "+minutes+" "+StringUtil.inflect(minutes,new String[]{"minutu","minuty","minut"}));
				}
			} else {
				if(hologram.getVisibilityManager().isVisibleTo(player)) hologram.getVisibilityManager().hideTo(player);
			}
		}
	}
}