package realcraft.bungee.skins.utils;

public class StringUtil {
	public static String inflect(int value,String[] inflections){
		if(value == 1) return inflections[0];
		else if(value >= 2 && value <= 4) return inflections[1];
		return inflections[2];
	}
}
