package it.dva.shorturl.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class ShorturlAppServerApplication {

	
	
	@Autowired
	MessageReceiver receiver;
	
	
	public static void main(String[] args) {
		SpringApplication.run(ShorturlAppServerApplication.class, args);
	}
	
	
	@Bean
    public ShortURLdecoder service() {
        return new ShortURLDecoderRedisImpl();
    }
	
	

}
