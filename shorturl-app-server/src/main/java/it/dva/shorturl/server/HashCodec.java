package it.dva.shorturl.server;

public class HashCodec {
	
	
	public static String toHash(String from) {
		char[] allowedSymbols = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
	    char[] hash = new char[6];

	    for (int i = 0; i < from.length(); i++)
	    {
	        hash[i % 6] = (char)(hash[i % 6] ^ from.toCharArray()[i]);
	    }
	    for (int i = 0; i < 6; i++)
	    {
	        hash[i] = allowedSymbols[hash[i] % allowedSymbols.length];
	    }
	    return new String(hash);
	}
}
