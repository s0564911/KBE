package de.htwb.ai.kbe.controller;

import de.htwb.ai.kbe.utils.AuthUtils;
import io.jsonwebtoken.*;
import org.springframework.cloud.client.discovery.DiscoveryClient ;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.htwb.ai.kbe.model.User;
import de.htwb.ai.kbe.repo.UserRepository;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final DiscoveryClient discoveryClient;
    private final UserRepository userRepository;
    
    public AuthController(DiscoveryClient discoveryClient, UserRepository repo) {
        this.discoveryClient = discoveryClient;
        this.userRepository = repo;
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
            user = userRepository.findByUserId(u.getUserId()).get(0);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        String token = AuthUtils.generateNewToken(u.getUserId(), u.getPassword());
        HttpHeaders header = new HttpHeaders();
        header.add("Content-type", "text/plain");

//        if (user == null || user.getUserId() == null || user.getPassword() == null) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//
//        if (user.getUserId().equals(u.getUserId()) && user.getPassword().equals(u.getPassword())) {
//            return new ResponseEntity<>(token, header, HttpStatus.OK);
//        }

        if (user != null && user.getUserId() != null && user.getPassword() != null &&
                user.getUserId().equals(u.getUserId()) && user.getPassword().equals(u.getPassword())) {
            return new ResponseEntity<>(token, header, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String isValidToken(@RequestHeader(value = "Authorization", defaultValue = "") String token) {
        try {

            String private_key = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKo"; // should be saved elsewhere
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(private_key);
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());

            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            String user = (String) claims.getBody().get("user");
            String pass = (String) claims.getBody().get("password");
            if (userRepository.findByUserId(user).get(0).getPassword().equals(pass))
                return user;
            else
                return null;
        } catch (JwtException e) {
            e.printStackTrace();
            return null;
        }
    }
}
