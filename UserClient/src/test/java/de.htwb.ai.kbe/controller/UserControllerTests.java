package de.htwb.ai.kbe.controller;

import de.htwb.ai.kbe.dao.UserDAO;
import de.htwb.ai.kbe.model.User;
import de.htwb.ai.kbe.service.UserService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.AfterClass;
import org.junit.jupiter.api.*;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTests {
    private static MockMvc mockMvc;
    private static Session session;

    @BeforeAll
    public static void setup() {

        SessionFactory sessionFactory = new MetadataSources(
                new StandardServiceRegistryBuilder()
                        .configure()
                        .build()
        ).buildMetadata().buildSessionFactory();

        DiscoveryClient discoveryClient = new DiscoveryClient() {
            @Override
            public String description() {
                return null;
            }

            @Override
            public List<ServiceInstance> getInstances(String serviceId) {
                return null;
            }

            @Override
            public List<String> getServices() {
                return null;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new UserController(
                                discoveryClient, new UserService(
                                        new UserDAO(sessionFactory)
                                ))).build();

        session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        session.save(User.builder().withUserId("mmuster").withFirstname("Maxime").withLastname("Muster").withPassword("pass1234").build());
    }

    @AfterClass
    public static void closeTransaction(){
        session.close();
    }

    @Test
    void authorizeUserShouldReturnOKAndTokenForValidCredentials() throws Exception {
        String json = "{\"userId\":\"mmuster\",\"password\":\"pass1234\"}";

        mockMvc.perform(post("/auth/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        ;
    }

    @Test
    void authorizeUserShouldReturn401ForInvalidPass() throws Exception {
        String json = "{\"userId\":\"mmuster\",\"password\":\"pass1235\"}";

        mockMvc.perform(post("/auth/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is(401))
        ;
    }

    @Test
    void authorizeUserShouldReturn401ForNoPass() throws Exception {
        String json = "{\"userId\":\"mmuster\"}";

        mockMvc.perform(post("/auth/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is(401))
        ;
    }

    @Test
    void authorizeUserShouldReturn401ForNoUser() throws Exception {
        String json = "{\"password\":\"pass1234\"}";

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
