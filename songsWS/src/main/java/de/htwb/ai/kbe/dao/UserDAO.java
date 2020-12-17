package de.htwb.ai.kbe.dao;

import de.htwb.ai.kbe.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.util.Base64;

@Repository("userDAO")
public class UserDAO implements IUserDAO {

//    private static final Logger logger = LoggerFactory.getLogger(SongDAO.class);

    private final SessionFactory sessionFactory;

    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User getUserByUserId(String userId) {
        Session session = sessionFactory.getCurrentSession();
        return session.load(User.class, userId);
    }

    @Override
    public String generateNewToken() {
        byte[] randomBytes = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        Base64.Encoder base64Encoder = Base64.getUrlEncoder();
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}