package de.htwb.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class DiscoveryServiceApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(DiscoveryServiceApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
