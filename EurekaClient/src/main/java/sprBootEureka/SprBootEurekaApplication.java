package sprBootEureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SprBootEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SprBootEurekaApplication.class, args);
    }
}
