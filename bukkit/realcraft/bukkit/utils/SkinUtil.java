package realcraft.bukkit.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import realcraft.bukkit.database.DB;
import realcraft.share.skins.Skin;

public class SkinUtil {

	public static String getPlayerSkin(Player player){
		return SkinUtil.getPlayerSkin(player.getName());
	}

	public static String getPlayerSkin(String name){
		String skin = null;
		ResultSet rs = DB.query("SELECT user_skin FROM authme WHERE user_name = '"+name.toLowerCase()+"'");
		try {
			if(rs.next()){
				skin = rs.getString("user_skin");
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return skin;
	}

	public static Skin getSkin(String name){
		Skin skin = null;
		ResultSet rs = DB.query("SELECT skin_value FROM skins_cache WHERE skin_name = '"+name.toLowerCase()+"'");
		try {
			if(rs.next()){
				String value = rs.getString("skin_value");
				skin = new Skin(name,"",value,"");
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return skin;
	}
}