package realcraft.bukkit.cosmetics.cosmetic;

import org.bukkit.Material;
import org.bukkit.World;
import realcraft.bukkit.RealCraft;
import realcraft.share.ServerType;

public enum CosmeticCategory {

	HAT     (100, "Hlavy",            Material.DIAMOND_HELMET),
	//SUIT    (200, "Brneni",           Material.LEATHER_CHESTPLATE),
	GADGET  (300, "Gadgety",          Material.ENDER_PEARL),
	EFFECT  (400, "Efekty",           Material.NETHER_STAR),
	PET     (500, "Mazlikove",        Material.NAME_TAG);
	//MOUNT   (600, "Jezdecka zvirata", Material.SADDLE);

	private int id;
	private String name;
	private Material material;

	private CosmeticCategory(int id,String name,Material material){
		this.id = id;
		this.name = name;
		this.material = material;
	}

	public int getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public Material getMaterial(){
		return material;
	}

	public boolean isAvailable(World world){
		if(RealCraft.getServerType() == ServerType.LOBBY || RealCraft.getServerType() == ServerType.CREATIVE) return true;
		else if(this == HAT) return true;
		else if(this == EFFECT) return true;
		else if(this == PET) return true;
		//else if(this == SUIT && RealCraft.getServerType() != ServerType.SURVIVAL && world.getName().equalsIgnoreCase("world")) return true;
		else if(this == GADGET && RealCraft.getServerType() != ServerType.SURVIVAL && world.getName().equalsIgnoreCase("world")) return true;
		return false;
	}
}