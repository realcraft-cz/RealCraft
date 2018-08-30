package realcraft.bukkit.mapmanager.map.data;

import org.bukkit.ChatColor;

public abstract class MapDataBounds extends MapDataEntry {

	private int min;
	private int max;

	public MapDataBounds(String name,int min,int max){
		super(name);
		this.min = min;
		this.max = max;
	}

	public int getMin(){
		return min;
	}

	public int getMax(){
		return max;
	}

	public boolean isValid(){
		return (this.size() >= this.getMin() && this.size() <= this.getMax());
	}

	public ChatColor getValidColor(){
		if(!this.isValid()) return ChatColor.RED;
		return ChatColor.GREEN;
	}

	public abstract int size();
}