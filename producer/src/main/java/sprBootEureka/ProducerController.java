package sprBootEureka;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class ProducerController {

	private static Logger logger = Logger.getLogger(ProducerController.class);

	private int counter = 0;

	/**
	 * Simple producer on root
	 * @return
	 */
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String produce() {
    	return produceValue();
    }
	
    /**
     * Allow specification of a delay on producer
     * @param delayMs
     * @return
     */
    @RequestMapping(value="/{delayMs}", method=RequestMethod.GET)
    public String produceWithDelay(@PathVariable String delayMs) {
		try {
			Thread.sleep(new Long(delayMs));
		} catch (Exception e) { logger.warn("Delay error"); }

		return produceValue();
    }
    
    /**
     * Producer is an increment counter operation.
     * @return
     */
    private String produceValue() {
    	counter++;
    	logger.info("counter:"+counter);
        return counter+"";
    }

}
