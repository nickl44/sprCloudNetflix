#Spring Cloud Netflix Tutorial
##Microservices in this tutorial:

###EurekaServer
  Simplest possible Spring Boot project to launch a Eureka Server.
  (this was made from http://start.spring.io/ with 'Eureka Server' only selected)

  Start this app with:  
  `mvn spring-boot:run`  
  then browse to:  http://localhost:8761 to see EurekaServer interface

**Notable Project Components** (those changed from the generated project):
  `/src/main/java/sprBootEurekaServer/SprBootEurekaServerApplication.java`  // added @EnableEurekaServer class annotation.  
  `/src/main/resources/application.yml`   // replaces .properties. Important: contains server: port: 8761 definition.  
  `/pom.xml`      // NOT ADDED, but NB: contains spring-cloud-starter-eureka-server

###EurekaClient  
  This is the simpliest µService that can register itself with Eureka.
  Simplest possible Spr Boot project to launch a EurekaClient.
  (this was made from http://start.spring.io/ with 'Eureka' only selected)

  **Prerequisite:**
  Make sure EurekaServer is running.

  Start this app with:  
  `mvn spring-boot:run`  
  then browse to http://localhost:8761   // to see EurekaServer interface, you should see MYCLIENT has registered after 30secs  
  kill EurekaClient
    browse to http://localhost:8761   // you should see MYCLIENT has immediately gone.

  **Notable Project Components** (those changed from the generated project):
  `/src/main/java/sprBootEureka/SprBootEurekaApplication.java`  // added @EnableDiscoveryClient class annotation.  
  `/src/main/resources/bootstrap.yml`   // contains spring: application: name: MYCLIENT (Service name definition).  
  `/pom.xml`      // NOT ADDED, but NB: contains spring-cloud-starter-eureka

  **Observations:**  
    You will observe log output when services register with Eureka, here's a useful guide to the API used & codes exchanged https://github.com/Netflix/eureka/wiki/Eureka-REST-operations


###producer
  Simplest evolution of EurekaClient into a stateful producer.
  A Spring-MVC app that contains a simple counter that increments when hit, also capable of introducing a simulated delay.

  **Prerequisite:**  
    Make sure EurekaServer is running.

  Start this app with:  
  `SERVER_PORT=8078 mvn spring-boot:run`  // we override the port
  then hit:
    browse to http://localhost:8761   // to see EurekaServer interface, you should see PRODUCER has been registered after a few secs

  **Notable Project Components** (those changed from EurekaClient):  
  `/src/main/java/sprBootEureka/ProducerController.java`    // mvc controller holding a 'count' state that is incremented and returned on each call  
  `/src/main/resources/bootstrap.yml`   // contains spring: application: name: PRODUCER (Service name definition).  
  `/src/main/resources/application.yml` // we set 2 things here

  1. `metadataMap`, essential to distinquish between instances, if they are not different then only a single count of this service type appears in Eureka dashboard, see: http://projects.spring.io/spring-cloud/spring-cloud.html#_eureka_metadata_for_instances_and_clients

  2. `leaseRenewalIntervalInSeconds`, the heartbeat/registration period. Defaults to 30secs, we reduce to 10s here to speed up stable registration. http://projects.spring.io/spring-cloud/spring-cloud.html#_why_is_it_so_slow_to_register_a_service  
  `/src/main/java/sprBootEureka/SprBootEurekaApplication.java`  // UNCHANGED. NB contains @EnableDiscoveryClient class annotation.

###consumer
  Simplest evolution of EurekaClient into a consumer of the producer.

  **Prerequisite:**  
    Make sure EurekaServer is running.  
    Make sure producer is running.

  Start this app with:
  `SERVER_PORT=8081 mvn spring-boot:run`  
  then browse to http://localhost:8761 to see EurekaServer interface, you should see CONSUMER has been registered after a few secs  
  now hit consumer:
    `curl http://localhost:8081`   // this will trigger the consumer to fetch from producer and output the value, you'll see activity in consumer & producer logs.

  **Notable Project Components** (those changed from EurekaClient):
  `/src/main/java/sprBootEureka/ConsumerController.java`    // when triggered by hit: uses DiscoveryClient to locate PRODUCER , makes request using RestTemplate, returns value from Producer  
  `/src/main/resources/bootstrap.yml`   // contains spring: application: name: CONSUMER (Service name definition).  
  `/src/main/resources/application.yml` // we set 2 things here
  1. metadataMap, essential to distinquish between instances, if they are not different then only a single count of this service type appears in Eureka dashboard, see: http://projects.spring.io/spring-cloud/spring-cloud.html#_eureka_metadata_for_instances_and_clients  
  2. leaseRenewalIntervalInSeconds, the heartbeat/registration period. Defaults to 30secs, we reduce to 10s here to spead up stable registration. http://projects.spring.io/spring-cloud/spring-cloud.html#_why_is_it_so_slow_to_register_a_service
    /src/main/java/sprBootEureka/SprBootEurekaApplication.java  // UNCHANGED. NB contains @EnableDiscoveryClient class annotation.

###producer * 2
  Now we launch an additional producer service to observe EurekaServer side load balancing.

  **Prerequisite:**  
    Make sure EurekaServer is running.  
    Make sure producer is running.  
    Make sure consumer is running.  

  Start this app with:
  `SERVER_PORT=8079 mvn spring-boot:run`  // we override the port
  then hit:
    browse to http://localhost:8761   // to see EurekaServer interface, you should see an additional PRODUCER has been registered after a few secs, notice the distinction in names provided by metadataMap  
  now hit consumer:
    `curl http://localhost:8081`   // this will trigger the consumer to fetch from a producer and output the value, as the consumer refers to the EurekaServer on each invocation the EurekaServer round-robins between the producer address supplied! You'll see activity in consumer & both producer logs.  

  **Observations:**  
    You may kill the EurekaServer and the consumer will continue to function using cached values until the EurekaServer reappears.

###consumerMin
  Modified consumer to use Ribbon with autowired RestTemplate calling servicename: http://producer

  **Prerequisite:**  
    Make sure EurekaServer is running.  
    Make sure (2 * producer) running.  

  Start this app with:
    `SERVER_PORT=8081 mvn spring-boot:run`
  then hit:
    browse to http://localhost:8761   // to see EurekaServer interface, you should see CONSUMER has been registered after a few secs  
  now hit consumer:
    `curl http://localhost:8081`   // this will trigger the consumer to fetch from producer and output the value, you'll see activity in consumer & producer logs.

  **Notable Project Components** (those changed from consumer):  
  `/src/main/java/sprBootEureka/ConsumerController.java`    // no longer uses `DiscoveryClient` to locate producer, instead injected RestTemplate  
  `/src/main/resources/application.yml` // additions to set refresh/fetch timeout

  **Observations:**  
    NOT client side load balancing. consumerMin fetches a different producer instance every fetch timeout and talks to that until the next fetch when the EurekaServer is doing the effective loadbalancing. i.e. If you kill the EurekaServer the consumer never talks to a diff producer!


###consumerRibbonLB
  consumer using client side load balancing by Ribbon.

  **Prerequisite:**  
    Make sure EurekaServer is running.  
    Make sure (2 * producer) running.  

  Start this app with:
    `SERVER_PORT=8081 mvn spring-boot:run`  
  then browse to http://localhost:8761   // to see EurekaServer interface, you should see CONSUMER has been registered after a few secs  
  now hit consumer:
    `curl http://localhost:8081`   // this will trigger the consumer to fetch from a producer and output the value, you'll see activity in consumer & producers logs.

  **Notable Project Components** (those changed from EurekaClient):
  `/src/main/java/sprBootEureka/ConsumerController.java`    // Uses LoadBalancerClient which is provided by SpringCloud & Autowired-in. When triggered by hit: loadBalancer.choose does local load-balancing on service, request using RestTemplate, returns value from Producer.  
  `/pom.xml`      // added spring-cloud-starter-ribbon but NB it actually works 100% with just spring-cloud-starter-eureka !

  **Observations:**
    Full client side load balancing. If you kill the EurekaServer the consumerRibbonLB continues to LB between producer from last fetch.

###consumerFeign
  consumer using discovery & remote invocations by Feign with client side load balancing.

  **Prerequisite:**
    Make sure EurekaServer is running.
    Make sure (2 * producer) running.

  Start this app with:
    `SERVER_PORT=8081 mvn spring-boot:run`  
  then browse to http://localhost:8761   // to see EurekaServer interface, you should see CONSUMER has been registered after a few secs  
  now hit consumer:
  `curl localhost:8081`   // this will trigger the consumer to fetch from a producer and output the value, you'll see activity in consumer & producers logs.  

  **Notable Project Components:**
  `/src/main/java/sprBootEureka/ProducerClient.java`      // Java Interface, encapsulating producer invocation details, path & http method, like a client side rest controller. Annotated with @FeignClient("producer")  
  `/src/main/java/sprBootEureka/ConsumerController.java`  // Autowired-in ProducerClient and load-balances producer call simply by invoking declared methods on the ProducerClient interface.
  `/src/main/java/sprBootEureka/SprBootEurekaApplication.java`  // Added @EnableFeignClients class annotation.  
  `/pom.xml`    // added spring-cloud-starter-feign else it doesn't work  

  **Observations:**
    Full client side load balancing.  
    Try this: Kill EurekaServer, Kill a producer, Consumer will still function only hitting remaining producer. Restore producer on diff port. Bring back EurekaServer. Consumer correctly LBs back across both Producers, nice!  
    Try: Kill both producers, consumer call to producer will then fail as will curl call. A mini cascading failure! Exceptions galore! Enter Hystrix....  

###consumerFeignHystrix
  Simplest evolution of consumerFeign to add the Hystrix circuitBreaker.

  **Prerequisite:**  
    Make sure EurekaServer is running.  
    Make sure (2 * producer) running.

  Start this app with:
    `SERVER_PORT=8081 mvn spring-boot:run`
  then  browse to http://localhost:8761   // to see EurekaServer interface, you should see CONSUMER has been registered after a few secs
  now hit consumer:  
  `curl localhost:8081`   // this will trigger the consumer to fetch from a producer and output the value, you'll see activity in consumer & producers logs.
  now kill BOTH producers
  `ctrl-c, ctrl-c`
  hit consumer:
  `curl localhost:8081`   // you will see fallback/failover method invoked = a value of 999 is returned.

  **Notable Project Components** (those changed from consumerFeign):
  `/src/main/java/sprBootEureka/ConsumerController.java`  // Added @HystrixCommand annotation to consumer method specifying fallbackMethod. Added fallbackMethod impl: getProducerFallback(Request), this is called when all producers are down.
  `/src/main/java/sprBootEureka/SprBootEurekaApplication.java`  // Added @EnableCircuitBreaker class annotation.  
  `/pom.xml`    // added spring-cloud-starter-hystrix & spring-boot-start-actuator to enable monitoring stream on: http://localhost:8081/hystrix.stream

  **Observations:**
    Test resiliency by taking down all producers then bringing some back and on diff ports etc, it's great!
    The hystrix.stream is clearly only machine readable! Enter Hystrix Dashboard...

###HystrixDash
  Made with start.spring.io with single check in HystrixDashboard , plus added single annotation (below)

  **Prerequisite:**  
    Make sure EurekaServer is running.  
    Make sure min 1 producer running.  
    Make sure consumerFeignHystrix is running.  

  Start this app with:
  `SERVER_PORT=7777 mvn spring-boot:run`  
  browse to Hystrix Dashboard:
    http://localhost:7777/hystrix
  provide the hystrix stream url of the consumer to the dashboard UI:
    http://localhost:8081/hystrix.stream  
  now hit consumer a few times:
    curl localhost:8081   // reqd to cause hystrix to start producing monitoring data
  observe monitoring data on the dashboard

  **Notable Project Components** (those changed from generated spr boot app):  
  `/src/main/java/sprBootHystrixDash/SprBootHystrixDashApplication.java`  // Added @EnableHystrixDashboard
