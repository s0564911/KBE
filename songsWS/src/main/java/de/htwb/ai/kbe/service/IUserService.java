package de.htwb.ai.kbe.service;

import de.htwb.ai.kbe.model.User;

import java.util.List;

public interface IUserService {

    User getUserByUserId(String userId);
    String generateNewToken();
//    List<User> getUsers();
}
