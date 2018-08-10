package realcraft.bukkit.cosmetics.cosmetic;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import realcraft.bukkit.cosmetics.CosmeticPlayer;
import realcraft.bukkit.cosmetics.Cosmetics;
import realcraft.bukkit.users.Users;

public abstract class Cosmetic {

	private CosmeticType type;

	public Cosmetic(CosmeticType type){
		this.type = type;
	}

	public CosmeticType getType(){
		return type;
	}

	public boolean isRunning(Player player){
		CosmeticPlayer cPlayer = Cosmetics.getCosmeticPlayer(Users.getUser(player));
		return cPlayer.hasCosmetic(this.getType()) && cPlayer.getCosmeticData(this.getType()).isRunning();
	}

	public void setRunning(Player player,boolean running){
		CosmeticPlayer cPlayer = Cosmetics.getCosmeticPlayer(Users.getUser(player));
		if(cPlayer.hasCosmetic(this.getType())){
			cPlayer.getCosmeticData(this.getType()).setRunning(running);
			if(running) this.run(player);
			else clear(player);
		}
	}

	public boolean isEnabled(Player player){
		CosmeticPlayer cPlayer = Cosmetics.getCosmeticPlayer(Users.getUser(player));
		return (cPlayer.hasCosmetic(this.getType()) && cPlayer.getCosmeticData(this.getType()).isEnabled());
	}

	public void setEnabled(Player player,boolean enabled){
		CosmeticPlayer cPlayer = Cosmetics.getCosmeticPlayer(Users.getUser(player));
		if(cPlayer.hasCosmetic(this.getType())){
			if(enabled) Cosmetics.disableCosmetics(player,this.getType().getCategory());
			cPlayer.getCosmeticData(this.getType()).setEnabled(enabled);
			this.setRunning(player,enabled);
		}
	}

	public ItemStack getItemStack(){
		ItemStack item = new ItemStack(this.getType().getMaterial());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.getType().getName());
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack getItemStack(Player player){
		CosmeticPlayer cPlayer = Cosmetics.getCosmeticPlayer(Users.getUser(player));
		ItemStack item = this.getItemStack();
		if(cPlayer.hasCosmetic(this.getType())){
			int amount = cPlayer.getCosmeticData(this.getType()).getAmount();
			if(amount > 64) amount = 64;
			item.setAmount(amount);
		}
		else item.setType(Material.GRAY_DYE);
		return item;
	}

	public abstract void run(Player player);
	public abstract void clear(Player player);
}