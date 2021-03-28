package de.htw.ai.kbe;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.htw.ai.kbe.controller.AuthController;
import de.htw.ai.kbe.repo.UserRepository;

import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "/test.properties")
@TestMethodOrder(OrderAnnotation.class)
public class UserEndpointTest {
    private MockMvc mockMvc;

    @Autowired
    private UserRepository uRepo;

    @BeforeEach
    public void setupMockMvc() {
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

        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(discoveryClient, uRepo)).build();
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
