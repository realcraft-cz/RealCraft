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

	public static String timeFormat(int time){
		int min = (int) Math.floor(time / 60);
		int sec = time % 60;
		String minStr = (min < 10) ? "0" + String.valueOf(min) : String.valueOf(min);
		String secStr = (sec < 10) ? "0" + String.valueOf(sec) : String.valueOf(sec);
		return minStr + ":" + secStr;
	}
}