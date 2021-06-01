package it.dva.shorturl.server;

public interface ShortURLdecoder {
	
	
	public String decodeShortURL(String url) throws Exception;
	
	public String undecodeShortURL(String url) throws Exception;
	
	public String getStatistic(String url) throws Exception;
	
	
}
