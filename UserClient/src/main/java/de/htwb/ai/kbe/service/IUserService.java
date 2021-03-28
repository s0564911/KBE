package de.htwb.ai.kbe.service;

import de.htwb.ai.kbe.model.User;

//@Component
//@Service("IUserService")
public interface IUserService {

    User getUserByUserId(String userId);
    String generateNewToken(String user, String password);
    boolean validateJWT(String jws);
    
    String getUsernameFromToken(String jws);
    boolean compareTokenToUser(String token, User user);
    
}
