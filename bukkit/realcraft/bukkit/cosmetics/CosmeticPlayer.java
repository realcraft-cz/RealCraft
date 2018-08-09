package realcraft.bukkit.cosmetics;

import org.bukkit.entity.Player;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticCategory;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics.CosmeticCrystals.CosmeticCrystal;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class CosmeticPlayer {

	private static final String COSMETICS_COSMETICS = "cosmetics_cosmetics";

	private User user;
	private Player player;

	private HashMap<CosmeticType,CosmeticData> cosmetics = new HashMap<>();
	private CosmeticCategory menuCategory;
	private CosmeticCrystal menuCrystal;
	private int menuPage = 1;

	public CosmeticPlayer(User user){
		this.user = user;
		this.reload();
	}

	public User getUser(){
		return user;
	}

	public Player getPlayer(){
		if(player == null || !player.isOnline() || !player.isValid()){
			player = Users.getPlayer(this.getUser());
		}
		return player;
	}

	public boolean hasCosmetic(CosmeticType type){
		return (cosmetics.containsKey(type) && cosmetics.get(type).getAmount() > 0);
	}

	public CosmeticData getCosmeticData(CosmeticType type){
		return cosmetics.get(type);
	}

	public CosmeticCategory getMenuCategory(){
		return menuCategory;
	}

	public void setMenuCategory(CosmeticCategory menuCategory){
		this.menuCategory = menuCategory;
	}

	public CosmeticCrystal getMenuCrystal(){
		return menuCrystal;
	}

	public void setMenuCrystal(CosmeticCrystal menuCrystal){
		this.menuCrystal = menuCrystal;
	}

	public int getMenuPage(){
		return menuPage;
	}

	public void setMenuPage(int menuPage){
		this.menuPage = menuPage;
	}

	public void addCosmetic(CosmeticType type,int amount){
		if(!this.hasCosmetic(type)) cosmetics.put(type,new CosmeticData(type,false,amount));
		else this.getCosmeticData(type).setAmount(this.getCosmeticData(type).getAmount()+amount);
		this.saveCosmetic(this.getCosmeticData(type));
	}

	private void saveCosmetic(CosmeticData data){
		ResultSet rs = DB.query("SELECT * FROM "+COSMETICS_COSMETICS+" WHERE user_id = '"+this.getUser().getId()+"' AND cosmetic_id = '"+data.getType().getId()+"'");
		try {
			if(rs.next()){
				DB.update("UPDATE "+COSMETICS_COSMETICS+" SET "
						+"user_id = '"+this.getUser().getId()+"',"
						+"cosmetic_amount = '"+data.getAmount()+"',"
						+"cosmetic_enabled = '"+(data.isEnabled() ? 1 : 0)+"'"
						+"WHERE user_id = '"+this.getUser().getId()+"' AND cosmetic_id = '"+data.getType().getId()+"'");
			} else {
				DB.update("INSERT INTO "+COSMETICS_COSMETICS+" (user_id,cosmetic_id,cosmetic_amount,cosmetic_enabled) VALUES('"+this.getUser().getId()+"','"+data.getType().getId()+"','"+data.getAmount()+"','"+(data.isEnabled() ? 1 : 0)+"')");
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public void reload(){
		cosmetics.clear();
		ResultSet rs = DB.query("SELECT * FROM "+COSMETICS_COSMETICS+" WHERE user_id = '"+this.getUser().getId()+"'");
		try {
			while(rs.next()){
				CosmeticType type = CosmeticType.fromId(rs.getInt("cosmetic_id"));
				int amount = rs.getInt("cosmetic_amount");
				boolean enabled = rs.getBoolean("cosmetic_enabled");
				cosmetics.put(type,new CosmeticData(type,enabled,amount));
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof CosmeticPlayer){
			CosmeticPlayer toCompare = (CosmeticPlayer) object;
			return (toCompare.getUser().equals(this.getUser()));
		}
		return false;
	}

	public class CosmeticData {

		private CosmeticType type;
		private boolean running = false;
		private boolean enabled;
		private int amount;

		public CosmeticData(CosmeticType type,boolean enabled,int amount){
			this.type = type;
			this.enabled = enabled;
			this.amount = amount;
		}

		public CosmeticType getType(){
			return type;
		}

		public boolean isRunning(){
			return running;
		}

		public void setRunning(boolean running){
			this.running = running;
		}

		public boolean isEnabled(){
			return enabled;
		}

		public void setEnabled(boolean enabled){
			if(this.enabled != enabled){
				this.enabled = enabled;
				CosmeticPlayer.this.saveCosmetic(this);
			}
		}

		public int getAmount(){
			return amount;
		}

		public void setAmount(int amount){
			this.amount = amount;
			if(this.amount < 0) this.amount = 0;
		}
	}
}