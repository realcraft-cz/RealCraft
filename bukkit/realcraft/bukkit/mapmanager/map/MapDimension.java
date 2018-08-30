package realcraft.bukkit.mapmanager.map;

public class MapDimension {

	private int x,y,z;

	public MapDimension(int x,int y,int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public int getZ(){
		return z;
	}

	public static class MapDimensionDefault extends MapDimension {

		public MapDimensionDefault(){
			super(256,256,256);
		}
	}
}