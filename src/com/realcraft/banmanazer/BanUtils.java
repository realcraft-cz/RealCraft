package com.realcraft.banmanazer;

import java.net.InetAddress;

import org.apache.commons.lang3.StringUtils;

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

	public static long[] getRangeFromWildcard(String ipStr){
		String[] ocelots = ipStr.split("\\.");
		
		if(ocelots.length != 4) return null;
		
		String[] fromIp = new String[4];
		String[] toIp = new String[4];
		
		for(int i = 0; i < ocelots.length; i++){
			if(ocelots[i].equals("*")){
				fromIp[i] = "0";
			} else {
				fromIp[i] = ocelots[i];
			}
		}
		
		for(int i = 0; i < ocelots.length; i++){
			if(ocelots[i].equals("*")){
				toIp[i] = "255";
			} else {
				toIp[i] = ocelots[i];
			}
		}
		
		long fromIpAddress = toLong(StringUtils.join(fromIp, "."));
		long toIpAddress = toLong(StringUtils.join(toIp, "."));
	
		return new long[] { fromIpAddress, toIpAddress };
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
