package de.htwb.ai.kbe.dao;

import de.htwb.ai.kbe.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;

@Repository("userDAO")
public class UserDAO implements IUserDAO {

//    private static final Logger logger = LoggerFactory.getLogger(SongDAO.class);

    private final SessionFactory sessionFactory;
    private final String private_key = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKo";

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
                .claim("user", user)
                .claim("password", password)
                .signWith(signingKey, SignatureAlgorithm.HS256);
        return builder.compact();
    }

    @Override
    public boolean isValidToken(String jws) {
        try {
            Session session = sessionFactory.getCurrentSession();

            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(private_key);
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());

            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(jws);
            String user = (String) claims.getBody().get("user");
            String password = (String) claims.getBody().get("password");
            return session.get(User.class, user).getPassword().equals(password);

        } catch (JwtException e) {
			e.printStackTrace();
            return false;
        }

    }

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
            return userOfToken.equals(user.getUserId());


        } catch (JwtException e) {
			e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getUserFromToken(String jws) {
        try {
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(private_key);
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(jws);

            return (String) claims.getBody().get("user");

        } catch (JwtException e) {
			e.printStackTrace();
            return "";
        }
    }
}