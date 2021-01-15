package de.htwb.ai.kbe.dao;

import de.htwb.ai.kbe.model.User;

import java.util.List;

public interface IUserDAO {

    User getUserByUserId(String userId);
    String generateNewToken();

//    List<User> getUsers();
//    public Integer addUser(User User);
//    public void updateUser(User User);
//    public void deleteUser(String userId);
}
