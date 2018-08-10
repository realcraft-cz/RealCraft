package realcraft.bukkit.survival;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.users.Users;

import java.io.IOException;
import java.io.PrintWriter;

public class MapCrafter {
	RealCraft plugin;

	public MapCrafter(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,new Runnable(){
			@Override
			public void run(){
				savePlayers();
			}
		},2*20,2*20);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,new Runnable(){
			@Override
			public void run(){
				saveResidences();
			}
		},2*20,60*20);
	}

	public void onReload(){
	}

	public void savePlayers(){
		JsonArray array = new JsonArray();
		for(Player player : plugin.getServer().getOnlinePlayers()){
			if(player.getWorld().getName().equalsIgnoreCase("world") && player.getGameMode() != GameMode.SPECTATOR){
				JsonObject playerJSON = new JsonObject();
				playerJSON.addProperty("username",player.getName());
				playerJSON.addProperty("world","world_survival");
				playerJSON.addProperty("x",player.getLocation().getBlockX());
				playerJSON.addProperty("y",player.getLocation().getBlockY());
				playerJSON.addProperty("z",player.getLocation().getBlockZ());
				playerJSON.addProperty("avatar",Users.getUser(player).getId()+"-"+Users.getUser(player).getAvatar());
				array.add(playerJSON);
			}
		}
		JsonObject json = new JsonObject();
		json.add("players",array);
		try {
		    PrintWriter writer = new PrintWriter("/var/www/realcraft/www/maps/players.json","UTF-8");
		    writer.write(json.toString());
		    writer.close();
		} catch (IOException e){
		}
	}

	public void saveResidences(){
		JsonArray array = new JsonArray();
		for(String name : Residence.getInstance().getResidenceManager().getResidenceList()){
			ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByName(name);
			if(residence != null && residence.getWorld().equalsIgnoreCase("world")){
				JsonObject playerJSON = new JsonObject();
				playerJSON.addProperty("name",residence.getName());
				playerJSON.addProperty("owner",residence.getOwner());
				CuboidArea area = residence.getArea("main");
				if(area != null){
					playerJSON.add("pos1",MapCrafter.toJSONLocation(area.getLowLoc()));
					playerJSON.add("pos2",MapCrafter.toJSONLocation(area.getHighLoc()));
					array.add(playerJSON);
				}
			}
		}
		JsonObject json = new JsonObject();
		json.add("residences",array);
		try {
		    PrintWriter writer = new PrintWriter("/var/www/realcraft/www/maps/residences.json","UTF-8");
		    writer.write(json.toString());
		    writer.close();
		} catch (IOException e){
		}
	}

	public static JsonObject toJSONLocation(Location location){
		if(location == null) return null;
		JsonObject json = new JsonObject();
		json.addProperty("x",location.getX());
		json.addProperty("y",location.getY());
		json.addProperty("z",location.getZ());
		json.addProperty("yaw",location.getYaw());
		json.addProperty("pitch",location.getPitch());
		return json;
	}
}