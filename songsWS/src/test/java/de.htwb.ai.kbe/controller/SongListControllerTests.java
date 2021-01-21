package de.htwb.ai.kbe.controller;

import de.htwb.ai.kbe.dao.SongDAO;
import de.htwb.ai.kbe.dao.UserDAO;
import de.htwb.ai.kbe.model.Song;
import de.htwb.ai.kbe.model.User;
import de.htwb.ai.kbe.service.SongService;
import de.htwb.ai.kbe.service.UserService;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.AfterClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SongListControllerTests {
//    private static MockMvc mockMvc;
//    private static Session session;
//    private static Transaction tx;
//    private static SessionFactory sessionFactory;
//    private static String token;
//    private static HttpHeaders headers;
//
//    private static final int currentID = 2;
//
//    // get all
//
//    @BeforeAll
//    public static void setup() {
//
//        sessionFactory = new MetadataSources(
//                new StandardServiceRegistryBuilder()
//                        .configure()
//                        .build()
//        ).buildMetadata().buildSessionFactory();
//
//        UserService userService = new UserService(new UserDAO(sessionFactory));
//
//        mockMvc = MockMvcBuilders.standaloneSetup(
//                new SongController(
//                        new SongService(new SongDAO(sessionFactory)),
//                        userService
//                )).build();
//
//        session = sessionFactory.getCurrentSession();
//        tx = session.beginTransaction();
//
//        User u = User.builder().withUserId("mmuster").withFirstname("Maxime").withLastname("Muster").withPassword("pass1234").build();
//        session.save(User.builder().withUserId("eschuler").withFirstname("Elena").withLastname("Schuler").withPassword("pass1234").build());
//
//        session.save(u);
//        session.save(Song.builder().withTitle("Straight Outta Compton").withArtist("N.W.A.").withLabel("Ruthless").withReleased(1988).build());
//        session.save(Song.builder().withTitle("Gangsta Gangsta").withArtist("N.W.A.").withLabel("Ruthless").withReleased(1988).build());
//        session.save(Song.builder().withTitle("Fuck tha Police").withArtist("N.W.A.").withLabel("Ruthless").withReleased(1988).build());
//        tx.commit();
//
//        session = sessionFactory.getCurrentSession();
//        tx = session.beginTransaction();
//        token = userService.generateNewToken(u.getUserId(), u.getPassword());
//        tx.commit();
//
//        headers = new HttpHeaders();
//        headers.add("Authorization", token);
//    }
//
//    @BeforeEach
//    void beforeEach() {
//        session = sessionFactory.getCurrentSession();
//        tx = session.beginTransaction();
//    }
//
//    @AfterEach
//    void afterEach() {
//        tx.commit();
//    }
//
//    @AfterClass
//    public static void closeTransaction() {
//        session.close();
//    }


}
