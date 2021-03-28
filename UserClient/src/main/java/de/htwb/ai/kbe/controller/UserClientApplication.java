package de.htwb.ai.kbe.controller;

import de.htwb.ai.kbe.model.User;

//import de.htwb.ai.kbe.service.IUserService;
import de.htwb.ai.kbe.service.IUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
//import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@EnableEurekaClient
@EnableJpaRepositories("de.htwb.ai.kbe.dao")
public class UserClientApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(UserClientApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@RestController
@RequestMapping(value = "/auth")
@ComponentScan("de.htwb.ai.kbe")
class UserController {

    private final DiscoveryClient discoveryClient;
    private final IUserService userService;

    public UserController(DiscoveryClient discoveryClient, IUserService userService) {
        this.discoveryClient = discoveryClient;
        this.userService = userService;
    }

    @RequestMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(
            @PathVariable String applicationName) {
        return this.discoveryClient.getInstances(applicationName);
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/plain", consumes = "application/json")
    public ResponseEntity<String> authorize(@RequestBody User u) {
        if (u.getUserId() == null || u.getPassword() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user;
        try {
            user = userService.getUserByUserId(u.getUserId());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        String token = userService.generateNewToken(u.getUserId(), u.getPassword());
        HttpHeaders header = new HttpHeaders();
        header.add("Content-type", "text/plain");

        if (user == null || user.getUserId() == null || user.getPassword() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (user.getUserId().equals(u.getUserId()) && user.getPassword().equals(u.getPassword())) {
            return new ResponseEntity<>(token, header, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
