package de.htwb.ai.kbe.service;

import de.htwb.ai.kbe.dao.IUserDAO;
import de.htwb.ai.kbe.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
public class UserService implements IUserService{

    private final IUserDAO userDAO;

    public UserService(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    @Transactional
    public User getUserByUserId(String userId) {
        return this.userDAO.getUserByUserId(userId);
    }

    public String generateNewToken() {
        return this.userDAO.generateNewToken();
    }

//    @Override
//    @Transactional
//    public List<User> getUsers() {
//        return this.userDAO.getUsers();
//    }
}
