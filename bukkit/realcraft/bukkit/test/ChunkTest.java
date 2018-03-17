package realcraft.bukkit.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import realcraft.bukkit.RealCraft;

public class ChunkTest implements Listener {

	public ChunkTest(){
		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if(command.equalsIgnoreCase("chunktest") && (player.hasPermission("group.Manazer") || player.hasPermission("group.Admin"))){
			event.setCancelled(true);
			ArrayList<ChunkInfo> chunks = new ArrayList<ChunkInfo>();
			for(Chunk chunk : player.getWorld().getLoadedChunks()){
				chunks.add(new ChunkInfo(chunk.getX(),chunk.getZ(),chunk.getEntities().length,chunk.getTileEntities().length));
			}

			Collections.sort(chunks,new Comparator<ChunkInfo>(){
				@Override
				public int compare(ChunkInfo chunk1,ChunkInfo chunk2){
					int compare = Integer.compare(chunk1.entities,chunk2.entities);
					if(compare > 0) return -1;
					else if(compare < 0) return 1;
					return 0;
				}
			});

			player.sendMessage(" ");
			player.sendMessage("§6Entity:");
			for(int i=0;i<5;i++){
				ChunkInfo info = chunks.get(i);
				TextComponent message = new TextComponent("§6Chunk [§c"+info.x+"§6;§c"+info.z+"§6]: §c"+info.entities+" §6entit, §c"+info.tiles+" §6tile-entit");
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Klikni pro teleport").create()));
				message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/lagg tpchunk "+info.x+" "+info.z+" "+player.getWorld().getName()));
				player.spigot().sendMessage(message);
			}

			Collections.sort(chunks,new Comparator<ChunkInfo>(){
				@Override
				public int compare(ChunkInfo chunk1,ChunkInfo chunk2){
					int compare = Integer.compare(chunk1.tiles,chunk2.tiles);
					if(compare > 0) return -1;
					else if(compare < 0) return 1;
					return 0;
				}
			});

			player.sendMessage(" ");
			player.sendMessage("§6Tile-entity:");
			for(int i=0;i<5;i++){
				ChunkInfo info = chunks.get(i);
				TextComponent message = new TextComponent("§6Chunk [§c"+info.x+"§6;§c"+info.z+"§6]: §c"+info.entities+" §6entit, §c"+info.tiles+" §6tile-entit");
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Klikni pro teleport").create()));
				message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/lagg tpchunk "+info.x+" "+info.z+" "+player.getWorld().getName()));
				player.spigot().sendMessage(message);
			}
		}
	}

	private class ChunkInfo {

		public int x;
		public int z;
		public int entities;
		public int tiles;

		public ChunkInfo(int x,int z,int entities,int tiles){
			this.x = x;
			this.z = z;
			this.entities = entities;
			this.tiles = tiles;
		}
	}
}