package db;

import java.security.SecureRandom;
import java.util.Random;

public class generateID {
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final Random rng = new SecureRandom();    


	public static String randomUUID(int length){
	    StringBuilder sb = new StringBuilder();
	    int spacer = 0;
	    while(length > 0){
	        length--;
	        spacer++;
	        sb.append(ALPHABET.charAt(rng.nextInt(ALPHABET.length())));
	    }
	    return sb.toString();
	}
	public static void main (String[] args) {
		String res = randomUUID(16);
		System.out.println(res);
	}
}
