package realcraft.share.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class RandomUtil {
	private static final Random random = new Random();

	public static boolean getRandomBoolean(){
		return random.nextBoolean();
	}

	public static double getRandomDouble(double min,double max){
		return min+Math.random()*(max-min);
	}

	public static int getRandomInteger(int min,int max){
		return random.nextInt((max - min) + 1) + min;
	}

	public static String getRandomHex(int byteLength) {
		SecureRandom secureRandom = new SecureRandom();
		byte[] token = new byte[byteLength];
		secureRandom.nextBytes(token);
		return new BigInteger(1,token).toString(16);
	}
}