package sprBootEureka;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;

@RestController
public class ConsumerController {

	private static Logger logger = Logger.getLogger(ConsumerController.class);

	@Autowired
	DiscoveryClient discoveryClient;
	  
	  
    @RequestMapping("/")									// root mapping
    public String consume(HttpServletRequest request) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka("PRODUCER", false);
        RestTemplate restTemplate = new RestTemplate();
    	logger.info("Going to call instance.homePageUrl:"+instance.getHomePageUrl());

        String responseStr = restTemplate.getForObject(instance.getHomePageUrl(), String.class);
        
    	logger.info("responseStr:"+responseStr);
        return responseStr;
    }

}
