package realcraft.share.utils;

public class StringUtil {

	public static String inflect(int value,String[] inflections){
		if(value == 1) return inflections[0];
		else if(value >= 2 && value <= 4) return inflections[1];
		return inflections[2];
	}

	public static String combineSplit(int startIndex,String[] string){
		StringBuilder builder = new StringBuilder();
		if(string.length >= 1){
			for(int i=startIndex;i<string.length;i++){
				builder.append(string[i]);
				builder.append(" ");
			}
			if(builder.length() > 1){
				builder.deleteCharAt(builder.length()-1);
				return builder.toString();
			}
		}
		return null;
	}
}