package sprBootEureka;


import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;


@RestController
public class ConsumerController {

	private static Logger logger = Logger.getLogger(ConsumerController.class);

    @Autowired
    ProducerClient producerClient;
    
    
    @RequestMapping("/")									// root mapping
    @HystrixCommand(fallbackMethod = "getProducerFallback")
    public String consume(HttpServletRequest request) {
    	
        logger.info("Going to call producerClient...");
    	String responseStr = producerClient.getValue();
    	        
    	logger.info("responseStr:"+responseStr);
        return responseStr;
    }
    
    String getProducerFallback(HttpServletRequest req) {
    	logger.info("!!! Using ProducerFallback method !!!");
    	return "999";
    }
    
    

}
