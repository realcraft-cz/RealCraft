package realcraft.bukkit.cosmetics.menu;

import realcraft.bukkit.cosmetics.cosmetic.CosmeticCategory;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;

public class CosmeticMenuItem {

	private CosmeticMenuItemType type;
	private CosmeticCategory category;
	private CosmeticType cosmetic;

	public CosmeticMenuItem(CosmeticMenuItemType type){
		this.type = type;
	}

	public CosmeticMenuItem(CosmeticMenuItemType type,CosmeticCategory category){
		this.type = type;
		this.category = category;
	}

	public CosmeticMenuItem(CosmeticMenuItemType type,CosmeticType cosmetic){
		this.type = type;
		this.cosmetic = cosmetic;
	}

	public CosmeticMenuItemType getType(){
		return type;
	}

	public CosmeticCategory getCategory(){
		return category;
	}

	public CosmeticType getCosmetic(){
		return cosmetic;
	}

	public enum CosmeticMenuItemType {
		ALL, CATEGORY, COSMETIC, CLEAR;
	}
}