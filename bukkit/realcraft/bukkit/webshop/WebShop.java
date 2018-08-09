package realcraft.bukkit.webshop;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.users.Users;
import realcraft.share.ServerType;
import realcraft.share.database.DB;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WebShop implements Listener, Runnable {

	private static final String PAYMENTS = "payments";
	private static final int TYPE_VIP = 4;
	private static final int TYPE_ITEM = 5;
	private Essentials essentials;

	public WebShop(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,2*20,2*20);
		essentials = (Essentials) RealCraft.getInstance().getServer().getPluginManager().getPlugin("Essentials");
	}

	@Override
	public void run(){
		checkUnfinishedPayments();
	}

	public void checkUnfinishedPayments(){
		for(Player player : Bukkit.getOnlinePlayers()){
			this.checkPlayerPayments(player);
		}
	}

	public void checkPlayerPayments(Player player){
		ResultSet rs = DB.query("SELECT * FROM "+PAYMENTS+" WHERE payment_finished = '0' AND user_id = '"+Users.getUser(player).getId()+"' AND ("+(RealCraft.getServerType() == ServerType.SURVIVAL ? "payment_type = '"+TYPE_VIP+"' OR payment_type = '"+TYPE_ITEM+"'" : "payment_type = '"+TYPE_VIP+"'")+") LIMIT 1");
		try {
			while(rs.next()){
				if(rs.getInt("payment_type") == TYPE_VIP){
					this.processVIPPayment(player,rs.getInt("payment_id"),rs.getInt("payment_amount"));
				}
				/*else if(rs.getInt("payment_type") == TYPE_ITEM){
					this.processItemPayment(player,rs.getInt("payment_id"),rs.getInt("payment_meta1"),rs.getInt("payment_meta2"),rs.getInt("payment_amount"));
				}*/
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public void processVIPPayment(Player player,int payment,int days){
		DB.update("UPDATE "+PAYMENTS+" SET payment_finished = '"+(System.currentTimeMillis()/1000)+"' WHERE payment_id = '"+payment+"'");
		int time = PermissionsEx.getPermissionManager().getUser(player).getOptionInteger("group-dVIP-until",null,0);
		if(time > System.currentTimeMillis()/1000) time = (time-((int)(System.currentTimeMillis()/1000)));
		if(days > 0) PermissionsEx.getPermissionManager().getUser(player).addGroup("dVIP",null,days*86400+time);
		else {
			PermissionsEx.getPermissionManager().getUser(player).addGroup("dVIP",null);
			PermissionsEx.getPermissionManager().getUser(player).removePermission("group-dVIP-until");
		}
		Users.getUser(player).reload();
		essentials.getUser(player).setDisplayNick();
		player.sendMessage("§7-----------------------------------");
		player.sendMessage("§r");
		player.sendMessage("§r §r §aPolozka uspesne nactena");
		player.sendMessage("§r §r §b§lVIP §f("+(days > 0 ? days+" dni" : "neomezene")+")");
		player.sendMessage("§r");
		player.sendMessage("§7-----------------------------------");
		player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
	}

	/*public void processItemPayment(Player player,int payment,int id,int data,int amount){
		if(player.getInventory().firstEmpty() != -1){
			RealCraft.getInstance().db.update("UPDATE "+PAYMENTS+" SET payment_finished = '"+(System.currentTimeMillis()/1000)+"' WHERE payment_id = '"+payment+"'");

			ItemStack item = null;
			Material material = Material.getMaterial(id);
			if(material == Material.ENCHANTED_BOOK){
				item = new ItemStack(material,amount);
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
				meta.addStoredEnchant(Enchantment.getById(data),Enchantment.getById(data).getMaxLevel(),false);
				item.setItemMeta(meta);
			}
			else item = new ItemStack(material,amount,(short)0,(byte)data);

			player.sendMessage("§7-----------------------------------");
			player.sendMessage("§r");
			player.sendMessage("§r §r §aPolozka uspesne nactena");
			player.sendMessage("§r §r §6"+item.getType().toString()+(amount > 1 ? " §f("+amount+"x)" : ""));
			player.sendMessage("§r");
			player.sendMessage("§7-----------------------------------");
			player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
			player.getInventory().addItem(item);
		}
	}*/
}