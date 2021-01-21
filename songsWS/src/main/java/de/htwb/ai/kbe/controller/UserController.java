package de.htwb.ai.kbe.controller;

import de.htwb.ai.kbe.model.User;
import de.htwb.ai.kbe.service.IUserService;

import java.util.ArrayList;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    //unencrypt on post and check if username and pass is correct, if correct u get user and can continue
    @RequestMapping(method = RequestMethod.POST, produces = "text/plain", consumes = "application/json")
    public ResponseEntity<String> authorize(@RequestBody User u) {
        if (u.getUserId() == null || u.getPassword() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getUserByUserId(u.getUserId());
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
