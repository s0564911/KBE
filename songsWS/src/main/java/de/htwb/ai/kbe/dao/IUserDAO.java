package de.htwb.ai.kbe.dao;

import de.htwb.ai.kbe.model.User;

public interface IUserDAO {

    User getUserByUserId(String userId);
    String generateNewToken(String user, String password);
    boolean isValidToken(String jws);

	String getUserFromToken(String jws);
	boolean compareTokenToUser(String jws, User user);
}
