package it.dva.shorturl.server;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

@Service
public class ShortURLDecoderRedisImpl implements ShortURLdecoder{

	private static Logger LOG = LoggerFactory.getLogger(ShortURLDecoderRedisImpl.class);
	
	Jedis jedis = new Jedis("my-redis");
			
	
	@Override
	public String decodeShortURL(String url) throws Exception {

		UrlValidator urlValidator = new UrlValidator(
				new String[] {"http","https"}
				);

		if (urlValidator.isValid(url)) {
			
			String id = HashCodec.toHash(url);
			
			LOG.info("id -> "+id+" "+"decodeShortURL -> "+ url);
			
			if (jedis.get(id)==null) jedis.set("ranking#"+id, "0"); 
			jedis.set(id,url );
			return id;
		
		}		
		throw new RuntimeException("Invalid URL ");

	}

	@Override
	public String undecodeShortURL(String shortUrl) throws Exception {
		String longUrl = jedis.get(shortUrl);
		if (longUrl==null) {
			throw new UrlNotFoundException();
		}
		jedis.set("ranking#"+shortUrl,String.valueOf(Integer.parseInt(jedis.get("ranking#"+shortUrl))+1));
		return longUrl;
	}

	@Override
	public String getStatistic(String shortUrl) throws Exception {
		return jedis.get("ranking#"+shortUrl);
	}
	

}
