package realcraft.bukkit.mapmanager.maps;

import com.google.gson.JsonElement;
import org.bukkit.Location;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.*;

public class MapPaintball extends Map {

	private MapDataMap<MapDataLocation> spawns = new MapDataMap<>("spawns");
	private MapDataList<MapDataLocation> drops = new MapDataList<>("drops");
	private MapDataList<PaintballSpeed> speeds = new MapDataList<>("speeds");
	private MapDataList<PaintballJumpArea> jumps = new MapDataList<>("jumps");
	private MapDataList<MapDataLocation> machineguns = new MapDataList<>("machineguns");

	public MapPaintball(int id){
		super(id,MapType.PAINTBALL);
	}

	public MapDataMap<MapDataLocation> getSpawns(){
		return spawns;
	}

	public MapDataList<MapDataLocation> getDrops(){
		return drops;
	}

	public MapDataList<PaintballSpeed> getSpeeds(){
		return speeds;
	}

	public MapDataList<PaintballJumpArea> getJumps(){
		return jumps;
	}

	public MapDataList<MapDataLocation> getMachineGuns(){
		return machineguns;
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(spawns);
		data.addProperty(drops);
		data.addProperty(speeds);
		data.addProperty(jumps);
		data.addProperty(machineguns);
		return data;
	}

	@Override
	public void loadData(MapData data){
		spawns.loadData(data);
		drops.loadData(data);
		jumps.loadData(data);
		speeds.loadData(data);
		machineguns.loadData(data);
	}

	private enum MapTeam {
		RED, BLUE;

		public static MapTeam getByName(String name){
			return MapTeam.valueOf(name.toUpperCase());
		}

		public String toString(){
			return this.name().toLowerCase();
		}
	}

	private class PaintballSpeed extends MapDataLocation {

		private int duration;

		public PaintballSpeed(int duration,Location location){
			super(location);
			this.duration = duration;
		}

		public PaintballSpeed(JsonElement element){
			super(element);
			this.duration = element.getAsJsonObject().get("duration").getAsInt();
		}

		public int getDuration(){
			return duration;
		}
	}

	private class PaintballJumpArea extends MapDataLocationArea {

		private double force;

		public PaintballJumpArea(double force,Location locFrom,Location locTo){
			super(locFrom,locTo);
			this.force = force;
		}

		public PaintballJumpArea(JsonElement element){
			super(element);
			this.force = element.getAsJsonObject().get("force").getAsDouble();
		}

		public double getForce(){
			return force;
		}
	}
}