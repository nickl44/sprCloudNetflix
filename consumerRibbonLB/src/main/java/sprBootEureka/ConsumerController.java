package sprBootEureka;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;


@RestController
public class ConsumerController {

	private static Logger logger = Logger.getLogger(ConsumerController.class);

	@Autowired
	LoadBalancerClient loadBalancer;
	
    @RequestMapping("/")									// root mapping
    public String consume(HttpServletRequest request) {
    	
        ServiceInstance instance = loadBalancer.choose("producer");
        URI producerUri = URI.create("http://"+instance.getHost()+":"+instance.getPort());
    	
        RestTemplate restTemplate = new RestTemplate();

        logger.info("Going to call producerUri:"+producerUri);

        String responseStr = restTemplate.getForObject(producerUri, String.class);
        
    	logger.info("responseStr:"+responseStr);
        return responseStr;
    }

}
