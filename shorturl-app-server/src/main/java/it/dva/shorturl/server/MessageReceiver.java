package it.dva.shorturl.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

@Component
public class MessageReceiver {

	private static final String RPC_QUEUE_NAME = "rpc_queue";
	
	
	ShortURLdecoder service = new ShortURLDecoderRedisImpl();
	
	private static Logger LOG = LoggerFactory.getLogger(MessageReceiver.class);
	
	public MessageReceiver() throws Exception{
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("my-rabbit");
	    factory.setPort(5672);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicQos(1);

            System.out.println(" [x] Awaiting RPC requests");

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    LOG.info("Messaggio arrived -> "+message);
                    LOG.info("Service -> "+service);
                    if (message!=null && !message.isEmpty() ) {
                    	Pattern p = Pattern.compile("stat ");   // the pattern to search for
             		    Matcher m = p.matcher(message);
             		    
             		    // now try to find at least one match
             		    if (m.find()) {
             		    	LOG.error(" url " + message.substring("stat ".length()));
             		    	response = service.getStatistic(message.substring("stat ".length()));
             		    	
                    	}else {
	                    	if (message.length()==6) {
	                    		response = service.undecodeShortURL(message);
	                    	}else {
	                    		response = service.decodeShortURL(message);
	                    	}
                    	}
                    }
                    throw new Exception("Invalid URL");
                      
                } catch (UrlNotFoundException e) {
                    response = "Url not found";
                } catch (Exception e) {
                    LOG.error(" [.] " + e.toString(),e);
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
	
	
	public static void main(String[] args) {
		    String stringToSearch = "stat jdjdhdh";

		    Pattern p = Pattern.compile("stat ");   // the pattern to search for
		    Matcher m = p.matcher(stringToSearch);
		    
		    // now try to find at least one match
		    if (m.find()) {
		    	System.out.println(stringToSearch.substring("stat ".length()));	
		      System.out.println("Found a match");
		    }else
		      System.out.println("Did not find a match");
	}
		
}
