package sprBootEureka;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;


@RestController
public class ConsumerController {

	private static Logger logger = Logger.getLogger(ConsumerController.class);

	@Autowired
    RestTemplate restTemplate;	// NB: this must be Autowired-in rather than made locally in the consume method. 

	
    @RequestMapping("/")									// root mapping
    public String consume(HttpServletRequest request) {
    	String producerUrl = "http://producer";
    	logger.info("1 Going to call producerUrl:"+producerUrl);

        String responseStr = restTemplate.getForObject(producerUrl, String.class);
        
    	logger.info("responseStr:"+responseStr);
        return responseStr;
    }

}
