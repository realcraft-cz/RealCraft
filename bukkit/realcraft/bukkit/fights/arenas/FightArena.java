package realcraft.bukkit.fights.arenas;

import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.FightType;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.share.utils.RandomUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class FightArena {

	private int id;
	private String name;
	private FightType type;
	private FileConfiguration config;

	protected World world;
	private int time = -1;
	private Environment environment;
	private Biome biome;

	private Location basicLocation;
	private Location spectatorLocation;
	private int spectatorRadius;
	private ArrayList<Location> spawns = new ArrayList<Location>();
	private FightArenaRegion region;

	public FightArena(int id,String name,FightType type){
		this.id = id;
		this.name = name;
		this.type = type;
		this.initWorld();
	}

	public int getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public FightType getType(){
		return type;
	}

	public World getWorld(){
		if(world == null) world = Bukkit.getWorld(this.getConfig().getString("world"));
		return world;
	}

	public int getTime(){
		if(time == -1) time = this.getConfig().getInt("time",6000);
		return time;
	}

	public Environment getEnvironment(){
		if(environment == null) environment = Environment.valueOf(this.getConfig().getString("environment",Environment.NORMAL.toString()).toUpperCase());
		return environment;
	}

	public Biome getBiome(){
		if(biome == null) biome = Biome.valueOf(this.getConfig().getString("biome",Biome.THE_VOID.toString()).toUpperCase());
		return biome;
	}

	public FileConfiguration getConfig(){
		if(config == null){
			File file = new File(RealCraft.getInstance().getDataFolder()+"/fights/"+this.getType().toString()+"/"+this.getName()+"/"+"config.yml");
			if(file.exists()){
				config = new YamlConfiguration();
				try {
					config.load(file);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		return config;
	}

	public FightArenaRegion getRegion(){
		if(region == null) region = new FightArenaRegion(this);
		return region;
	}

	public ArrayList<Location> getSpawns(){
		return spawns;
	}

	public Location getRandomSpawn(){
		return spawns.get(RandomUtil.getRandomInteger(0,spawns.size()-1));
	}

	@SuppressWarnings("unchecked")
	public void loadSpawns(){
		List<Map<String, Object>> temps = (List<Map<String, Object>>) this.getConfig().get("spawns");
		if(temps != null && !temps.isEmpty()){
			for(Map<String, Object> spawn : temps){
				double x = Double.valueOf(spawn.get("x").toString());
				double y = Double.valueOf(spawn.get("y").toString());
				double z = Double.valueOf(spawn.get("z").toString());
				float yaw = Float.valueOf(spawn.get("yaw").toString());
				float pitch = Float.valueOf(spawn.get("pitch").toString());
				World world = Bukkit.getWorld(spawn.get("world").toString());
				if(world == null){
					world = Bukkit.createWorld(new WorldCreator(spawn.get("world").toString()));
					if(world == null){
						continue;
					}
				}
				spawns.add(new Location(world,x,y,z,yaw,pitch));
			}
		}
	}

	private void initWorld(){
		if(this.getWorld() == null){
			WorldCreator creator = new WorldCreator(this.getConfig().getString("world"));
			creator.type(WorldType.FLAT);
			creator.environment(this.getEnvironment());
			creator.generator(new CustomGenerator(this.getBiome()));
			world = Bukkit.getServer().createWorld(creator);
		}
		this.getWorld().setDifficulty(Difficulty.HARD);
		this.getWorld().setPVP(true);
		this.getWorld().setAutoSave(true);
		this.getWorld().setFullTime(this.getTime());
		this.getWorld().setMonsterSpawnLimit(0);
		this.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
		this.getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE,false);
		this.getWorld().setGameRule(GameRule.DO_MOB_SPAWNING,false);
	}

	public Location getBasicLocation(){
		if(basicLocation == null) basicLocation = LocationUtil.getConfigLocation(this.getConfig(),"location");
		return basicLocation;
	}

	public Location getSpectatorLocation(){
		if(spectatorLocation == null) spectatorLocation = LocationUtil.getConfigLocation(this.getConfig(),"spectator");
		return spectatorLocation;
	}

	public int getSpectatorRadius(){
		if(spectatorRadius == 0) spectatorRadius = this.getConfig().getInt("spectator.radius",128);
		return spectatorRadius;
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof FightArena){
			FightArena toCompare = (FightArena) object;
			return (toCompare.getId() == this.getId());
		}
		return false;
	}

	private class CustomGenerator extends ChunkGenerator {

		private Biome biome;

		public CustomGenerator(Biome biome){
			this.biome = biome;
		}

		@Override
		public boolean canSpawn(World world,int x,int z){
			return true;
		}

		@Override
		public ChunkData generateChunkData(World world,Random random,int cx,int cz,BiomeGrid biomeGrid){
			ChunkData data = this.createChunkData(world);
			for(int x=0;x<16;x++){
				for(int z=0;z<16;z++){
					biomeGrid.setBiome(x,z,biome);
				}
			}
			if(cx == 0 && cz == 0) data.setBlock(0,64,0,Material.BEDROCK);
			return data;
		}

		@Override
		public Location getFixedSpawnLocation(World world,Random random){
			return new Location(world,0,66,0);
		}
	}
}