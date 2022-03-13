package realcraft.bukkit.banmanazer;

import java.net.InetAddress;

public class BanUtils {
	
	public static String getAddress(InetAddress address){
		return address.getHostAddress().replace("/", "");
	}

	public static long toLong(String ip){
		String[] addressArray = ip.split("\\.");
		long result = 0;

		for(int i=0;i<addressArray.length;i++){
			int power = 3 - i;
			result += ((Integer.parseInt(addressArray[i]) % 256 * Math.pow(256, power)));
		}
		return result;
	}

	public static String toString(long ip){
		return ((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + (ip & 0xFF);
	}
	
	public static String combineSplit(int startIndex, String[] string) {
		StringBuilder builder = new StringBuilder();
		if (string.length >= 1) {
			for (int i = startIndex; i < string.length; i++) {
				builder.append(string[i]);
				builder.append(" ");
			}

			if (builder.length() > 1) {
				builder.deleteCharAt(builder.length() - 1);
				return builder.toString();
			}
		}
		return null;
	}
	
	public static boolean isNumeric(String str){
		return str.matches("-?\\d+(\\.\\d+)?");
	}
}
