package de.htwb.ai.kbe.controller;

import de.htwb.ai.kbe.dao.UserDAO;
import de.htwb.ai.kbe.service.UserService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTests {
    private MockMvc mockMvc;
    private Transaction tx;
    private Session session;

    @BeforeEach
    public void setup() throws InterruptedException {
        // enhance calm pls
        Thread.sleep(10000);
        SessionFactory sessionFactory = new MetadataSources(
                new StandardServiceRegistryBuilder()
                        .configure()
                        .build()
        ).buildMetadata().buildSessionFactory();

        mockMvc =
                MockMvcBuilders.standaloneSetup(
                        new UserController(
                                new UserService(
                                        new UserDAO(sessionFactory)
                                ))).build();

        session = sessionFactory.getCurrentSession();
        tx = session.beginTransaction();
    }

    @AfterEach
    void closeTransaction(){
        tx.commit();
        session.close();
    }

    @Test
    void authorizeUserShouldReturnOKAndTokenForValidCredentials() throws Exception {
        String json = "{\"userid\":\"mmuster\",\"password\":\"pass1234\"}";

        mockMvc.perform(post("/auth/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        ;
    }

    @Test
    void authorizeUserShouldReturn401ForInvalidPass() throws Exception {
        String json = "{\"userid\":\"mmuster\",\"password\":\"pass1235\"}";

        mockMvc.perform(post("/auth/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is(401))
        ;
    }

    @Test
    void authorizeUserShouldReturn401ForNoPass() throws Exception {
        String json = "{\"userid\":\"mmuster\"}";

        mockMvc.perform(post("/auth/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is(401))
        ;
    }

    @Test
    void authorizeUserShouldReturn415ForNoBody() throws Exception {
        mockMvc.perform(post("/auth/"))
                .andExpect(status().is(415))
        ;
    }

}
