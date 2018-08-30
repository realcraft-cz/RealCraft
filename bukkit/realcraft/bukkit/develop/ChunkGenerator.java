package realcraft.bukkit.develop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkPopulateEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.others.AbstractCommand;

public class ChunkGenerator extends AbstractCommand implements Runnable {

	private boolean enabled;
	private Player player;

	private int index = 1;
	private long lastPopulate = 0;

	public ChunkGenerator(){
		super("chunkgen");
		Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(),this,20,20);
	}

	@Override
	public void perform(Player player,String[] args){
		if(!player.hasPermission("group.Manazer")){
			return;
		}
		if(args.length == 0){
			player.sendMessage("/chunkgen <index>");
			player.sendMessage("/chunkgen cancel");
			return;
		}
		if(args[0].equalsIgnoreCase("cancel")){
			this.enabled = false;
			player.sendMessage("Generovani preruseno");
			return;
		}
		int index;
		try {
			index = Integer.valueOf(args[0]);
		} catch (NumberFormatException e){
			player.sendMessage("§cZadej cele cislo.");
			return;
		}
		this.index = index;
		this.enabled = true;
		this.player = player;
	}

	@Override
	public void run(){
		if(enabled){
			if(player == null || !player.isOnline()){
				player = null;
				return;
			}
			if(lastPopulate+1000 > System.currentTimeMillis()){
				return;
			}
			int[] coords = ChunkGenerator.getIndexCoords(index);
			int distance = Bukkit.getViewDistance()*16;
			Location location = new Location(player.getWorld(),coords[0]*distance,100,coords[1]*distance);
			player.sendMessage("Chunk generating: "+index+" ["+location.getBlockX()+","+location.getBlockZ()+"]");
			System.out.println("Chunk generating: "+index+" ["+location.getBlockX()+","+location.getBlockZ()+"]");
			player.teleport(location);
			index ++;
		}
	}

	@EventHandler
	public void ChunkPopulateEvent(ChunkPopulateEvent event){
		lastPopulate = System.currentTimeMillis();
		//System.out.println("ChunkPopulateEvent ["+(event.getChunk().getX() << 4)+";"+(event.getChunk().getZ() << 4)+"]");
	}

	private static int[] getIndexCoords(int index){
		int dir = 1;
		int step = 1;
		int round = 1;
		int x = 0,y = 1;
		for(int i=1;i<=index;i++){
			int sideSize = ((round*8)/4)+1;
			if(step == 1){
				x -= 1;
				y -= 1*2;
			} else {
				if(dir == 1) x += 1;
				else if(dir == 2) y += 1;
				else if(dir == 3) x -= 1;
				else if(dir == 4) y -= 1;
			}
			if(step == sideSize) dir = 2;
			else if(step == (sideSize+sideSize)-1) dir = 3;
			else if(step == (sideSize+sideSize+sideSize)-2) dir = 4;
			else if(step == (sideSize+sideSize+sideSize+sideSize)-4){
				dir = 1;
				round ++;
				step = 0;
			}
			step ++;
		}
		return new int[]{x,y};
	}
}