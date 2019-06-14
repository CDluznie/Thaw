package fr.umlv.thaw.util.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class SecurityUtils {
	
	private static String saltShuffle(String str) {
		Objects.requireNonNull(str);
		int length = str.length();
		int index1 = length/3, index2 = 3*length/4;
		return "9?2" +
				str.substring(index1, index2) + 
				"!@#" + 
				str.substring(index2, length) +
				"hbx" +
				str.substring(0, index1) +
				";)";
	}
	
	private static String sha1(String str, String encoding) {
		Objects.requireNonNull(str);
		Objects.requireNonNull(encoding);
		String crypt = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.reset();
			md.update(str.getBytes(encoding));
			crypt = new BigInteger(1, md.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			System.err.println("no such algorithm SHA-1");
			System.exit(1);
		} catch (UnsupportedEncodingException e) {
			System.err.println("unsupported encoding " + encoding);
			System.exit(1);
		}
		return crypt;
	}

	/**
	 * Crypt the specified string.
	 * 
	 * @param 	str the specified string
	 * @return	The crypted string.
	 * @throws 	NullPointerException if str is null
	 */
	public static String crypt(String str) {
		return sha1(saltShuffle(Objects.requireNonNull(str)), "UTF-8");
	}
	
}
