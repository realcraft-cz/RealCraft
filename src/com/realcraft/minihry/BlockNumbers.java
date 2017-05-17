package com.realcraft.minihry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import com.realcraft.RealCraft;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;

@SuppressWarnings("deprecation")
public class BlockNumbers {
	RealCraft plugin;
	WorldEditPlugin we;
	EditSession session;

	List<CuboidClipboard> schematics = new ArrayList<CuboidClipboard>();

	public BlockNumbers(RealCraft realcraft){
		plugin = realcraft;
		we = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
		importNumbers();
	}

	private void importNumbers(){
		for(int i=0;i<10;i++){
			try {
				CuboidClipboard schematic = loadSchematic(new File("plugins/RealCraft/"+i+".schematic"));
				schematics.add(schematic);
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}

	public void pasteNumber(int number,Location location,int rotation){
		CuboidClipboard schematic = schematics.get(number);
		World world = plugin.getServer().getWorld("world");
		if(schematic != null){
			try {
				session = we.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(world),64);
				schematic.rotate2D(rotation);
				schematic.place(session,new Vector(location.getBlockX(),location.getBlockY(),location.getBlockZ()),false);
				schematic.rotate2D(-rotation);
			} catch (MaxChangedBlocksException e){
				e.printStackTrace();
			}
		}
	}

	private CuboidClipboard loadSchematic(File file) throws IOException {
		CuboidClipboard schematic = null;
		try {
			schematic = MCEditSchematicFormat.getFormat(file).load(file);
			return schematic;
		} catch (com.sk89q.worldedit.data.DataException | IOException e){
			e.printStackTrace();
		}
		return schematic;
    }
}