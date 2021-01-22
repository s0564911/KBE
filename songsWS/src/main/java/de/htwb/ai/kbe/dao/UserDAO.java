package de.htwb.ai.kbe.dao;

import de.htwb.ai.kbe.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Repository("userDAO")
public class UserDAO implements IUserDAO {

//    private static final Logger logger = LoggerFactory.getLogger(SongDAO.class);

    private final SessionFactory sessionFactory;
    private final String private_key="oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKo";
    
    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User getUserByUserId(String userId) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(User.class, userId);
    }

    @Override
    public String generateNewToken(String user, String password) {
    	  long nowMillis = System.currentTimeMillis();
          Date now = new Date(nowMillis);
          byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(private_key);
          Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());

          JwtBuilder builder = Jwts.builder()
                  .setIssuedAt(now)
                  .claim("user",user)
                  .claim("password", password)
                  .signWith(signingKey,SignatureAlgorithm.HS256); 
          return builder.compact();
    }  
    /**
     * @param jws
     * @return boolean if the token provided returns a valid username+password combination
     */
    @Override
    public boolean isValidToken(String jws) {
	    try	{
	    	Session session = sessionFactory.getCurrentSession();
	        
	    	byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(private_key);
	        Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
	        
	    	Jws<Claims> claims = Jwts.parserBuilder()
	    			  .setSigningKey(signingKey)
	    			  .build()
	    			  .parseClaimsJws(jws);
	    			String user = (String) claims.getBody().get("user");
	    			String password = (String) claims.getBody().get("password");
	    			if ( session.get(User.class, user).getPassword().equals(password)) {
	    				return true;
	    			}
	    			return false;
	    			
	    }
	    catch(JwtException e){
	    	System.out.print(e);
	    	return false;
	    }
    	
    }
    /**
     * 
     * @param token
     * @param user
     * @return boolean return true the user is the same as the one in the param token
     */
    @Override
    public boolean compareTokenToUser(String token, User user) {
    	try {
	    	byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(private_key);
	        Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
	    	Jws<Claims> claims = Jwts.parserBuilder()
	    			  .setSigningKey(signingKey)
	    			  .build()
	    			  .parseClaimsJws(token);
	    			String userOfToken = (String) claims.getBody().get("user");
	    			if (userOfToken.equals(user.getUserId())) {return true;}
	    			return false;
	    			
    			
    }
    catch(JwtException e){
    	System.out.print(e);
    	return false;
    }	
}
    
    /**
     * 
     * @param jws
     * @return String of the username, assumes the token is valid
     */
    @Override
    public String getUserFromToken(String jws) {
    	try {
	    	byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(private_key);
	        Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
	    	Jws<Claims> claims = Jwts.parserBuilder()
	    			  .setSigningKey(signingKey)
	    			  .build()
	    			  .parseClaimsJws(jws);
	    			String user = (String) claims.getBody().get("user");
	    			
	    			
	    			return user;
    			
    }
    catch(JwtException e){
    	System.out.print(e);
    	return "";
    }	
    }
}