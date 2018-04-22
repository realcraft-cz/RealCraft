package realcraft.share.skins;

import java.sql.ResultSet;
import java.sql.SQLException;

import realcraft.share.database.DB;

public class SkinUtil {

	public static Skin getSkin(String name){
		Skin skin = null;
		ResultSet rs = DB.query("SELECT skin_value,skin_signature FROM skins_cache WHERE skin_name = '"+name.toLowerCase()+"'");
		try {
			if(rs.next()){
				String value = rs.getString("skin_value");
				String signature = rs.getString("skin_signature");
				skin = new Skin(name,"",value,signature);
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return skin;
	}
}