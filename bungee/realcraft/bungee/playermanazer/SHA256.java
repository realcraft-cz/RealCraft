package realcraft.bungee.playermanazer;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 */
public class SHA256 {

    /**
     * Method getSHA256.
     *
     * @param message String
     *
     * @return String * @throws NoSuchAlgorithmException
     * @throws NoSuchAlgorithmException
     */
    public static String getSHA256(String message){
        MessageDigest sha256 = null;
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e){
			e.printStackTrace();
		}
        sha256.reset();
        sha256.update(message.getBytes());
        byte[] digest = sha256.digest();
        return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest));
    }

    /**
     * Method getHash.
     *
     * @param password String
     * @param salt     String
     * @param name     String
     *
     * @return String * @throws NoSuchAlgorithmException * @see fr.xephi.authme.security.crypts.EncryptionMethod#getHash(String, String, String)
     */

    public static String getHash(String password, String salt){
        return "$SHA$" + salt + "$" + getSHA256(getSHA256(password) + salt);
    }

    /**
     * Method comparePassword.
     *
     * @param hash       String
     * @param password   String
     * @param playerName String
     *
     * @return boolean * @throws NoSuchAlgorithmException * @see fr.xephi.authme.security.crypts.EncryptionMethod#comparePassword(String, String, String)
     */

    public static boolean comparePassword(String hash, String password){
        String[] line = hash.split("\\$");
        return hash.equals(getHash(password, line[2]));
    }

}