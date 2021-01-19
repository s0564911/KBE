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
    @Override
    @Transactional
    public String generateNewToken(String user, String password) {
        return this.userDAO.generateNewToken( user,  password);
    }
    @Override
    @Transactional
    public boolean validateJWT(String jws) {
    	return this.userDAO.isValidToken(jws);
    }

	@Override
	@Transactional
	public String getUsernameFromToken(String jws) {
		// TODO Auto-generated method stub
		return this.userDAO.getUserFromToken(jws);
	}

	@Override
	@Transactional
	public boolean compareTokenToUser(String token, User user) {
		// TODO Auto-generated method stub
		return this.userDAO.compareTokenToUser(token, user);
	}

}
