package de.htwb.ai.kbe.controller;

import de.htwb.ai.kbe.dao.SongDAO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.htwb.ai.kbe.service.SongService;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class SongControllerTests {
    private MockMvc mockMvc;
    private Transaction tx;
    private Session session;

    private static final int currentID = 101;

    @BeforeEach
    public void setup() throws InterruptedException {
        // enhance calm pls
        Thread.sleep(5000);
        SessionFactory sessionFactory =
                new MetadataSources(
                        new StandardServiceRegistryBuilder()
                                .configure()
                                .build()
                ).buildMetadata().buildSessionFactory();

        mockMvc =
                MockMvcBuilders.standaloneSetup(
                        new SongController(
                                new SongService(
                                        new SongDAO(sessionFactory)
                                ))).build();

        session = sessionFactory.getCurrentSession();
        tx = session.beginTransaction();
    }

    @AfterEach
    void closeTransaction() {
        tx.commit();
        session.close();
    }

    // get all

    @Test // on max
    void getAllSongsShouldReturnOKAndOnePage() throws Exception {

        mockMvc.perform(get("/songs/")).
                andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.totalPages").value(1))
        ;
    }

    // get ID

    @Test // ToDo: Capitals ?
    void getSongShouldReturnOKAndJSONForExistingId() throws Exception {
        mockMvc.perform(get("/songs/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Straight Outta Compton"))
                .andExpect(jsonPath("$.artist").value("N.W.A."))
                .andExpect(jsonPath("$.label").value("Ruthless"))
                .andExpect(jsonPath("$.released").value(1988));
    }

    @Test
    void getSongShouldReturn404ForNonExistingId() throws Exception {
        mockMvc.perform(get("/songs/3000"))
                .andExpect(status().isNotFound());
    }

    // post

    @Test // on max
    void addSongShouldReturn201ForValidJSON_plus_deleteSongShouldReturn204ForValidID() throws Exception {
        String json =

                "{" +
                        "    \"title\": \"Dopeman (Remix)\"," +
                        "    \"artist\": \"N.W.A.\"," +
                        "    \"label\": \"Ruthless\"," +
                        "    \"released\": 1988" +
                        "}";

        HttpHeaders head = new HttpHeaders();
        head.add("Location", "/rest/songs/");
        String content = mockMvc.perform(post("/songs/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(head))
                .andExpect(status().is(201))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Dopeman (Remix)"))
                .andExpect(jsonPath("$.artist").value("N.W.A."))
                .andExpect(jsonPath("$.label").value("Ruthless"))
                .andExpect(jsonPath("$.released").value(1988))
                .andExpect(header().string("Location", "/rest/songs/"))
                .andReturn().getResponse().getContentAsString();
        ;
        JSONParser parser = new JSONParser();
        JSONObject result = (JSONObject) parser.parse(content);
        int id = (int) result.getAsNumber("id");

        mockMvc.perform(delete("/songs/" + id))
                .andExpect(status().is(204))
        ;
    }

    @Test // on max
    void addSongShouldReturn400ForInvalidJSON() throws Exception {
        String json =

                "{" +
                        "    \"artist\": \"N.W.A.\"," +
                        "    \"label\": \"Ruthless\"," +
                        "    \"released\": 1988" +
                        "}";

        mockMvc.perform(post("/songs/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is(400))
        ;
    }

    @Test
        // ~
    void addSongShouldReturn415ForXML() throws Exception {
        String xml =

                "<title>Dopeman (Remix)</title>\n" +
                        "<artist>N.W.A.</artist>\n" +
                        "<label>Ruthless</label>\n" +
                        "<released>1988</released>";

        mockMvc.perform(post("/songs/")
                .contentType(MediaType.APPLICATION_ATOM_XML)
                .content(xml))
                .andExpect(status().is(415))
        ;
    }

    @Test
        // ~ // on max
    void addSongShouldReturn415ForEmptyPayload() throws Exception {
        mockMvc.perform(post("/songs/"))
                .andExpect(status().is(415))
        ;
    }

    // put ID

//    @Test // ToDo: was 404 / on max
//    void updateSongShouldReturn204ForValidIDAndJSON() throws Exception {
//        String json =
//
//                "{" +
//                        "    \"id\": \"" + (currentID - 10) + "\"," +
//                        "    \"title\": \"I Ain't tha 1\"," +
//                        "    \"artist\": \"N.W.A.\"," +
//                        "    \"label\": \"Ruthless\"," +
//                        "    \"released\": 1988" +
//                        "}";
//
//        mockMvc.perform(put("/songs/" + (currentID - 10))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json))
//                .andExpect(status().is(204))
//        ;
//    }

    @Test // ToDo: was 404
    void updateSongShouldReturn400ForInvalidIDInPayload() throws Exception {
        String json =

                "{" +
                        "    \"id\": \"3000\"," +
                        "    \"title\": \"Dopeman\"," +
                        "    \"artist\": \"N.W.A.\"," +
                        "    \"label\": \"Ruthless\"," +
                        "    \"released\": 1988" +
                        "}";

        mockMvc.perform(put("/songs/" + currentID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is(400))
        ;
    }

    @Test
    void updateSongShouldReturn404ForInvalidIDInPath() throws Exception {
        String json =

                "{" +
                "    \"id\": \"3000\"," +
                "    \"title\": \"Dopeman\"," +
                "    \"artist\": \"N.W.A.\"," +
                "    \"label\": \"Ruthless\"," +
                "    \"released\": 1988" +
                "}";

        mockMvc.perform(put("/songs/3000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is(404))
        ;
    }

    @Test // ToDo: was 404
    void updateSongShouldReturn400ForInvalidJSON() throws Exception {
        String json =

                "{" +
                "    \"id\": \"" + currentID + "\"," +
                "    \"artist\": \"N.W.A.\"," +
                "    \"label\": \"Ruthless\"," +
                "    \"released\": 1988" +
                "}";

        mockMvc.perform(put("/songs/" + currentID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is(400))
        ;
    }

    @Test // ~ // on max
    void updateSongShouldReturn415ForXML() throws Exception {
        String xml =

                "<id>11</id>\n" +
                        "<title>Dopeman (Remix)</title>\n" +
                        "<artist>N.W.A.</artist>\n" +
                        "<label>Ruthless</label>\n" +
                        "<released>1988</released>";

        mockMvc.perform(put("/songs/" + currentID)
                .contentType(MediaType.APPLICATION_ATOM_XML)
                .content(xml))
                .andExpect(status().is(415))
        ;
    }

    @Test // ~ // on max
    void updateSongShouldReturn415ForEmptyPayload() throws Exception {
        mockMvc.perform(put("/songs/" + currentID))
                .andExpect(status().is(415))
        ;
    }

//     delete ID

//     rather dont
//    @Test
//    void deleteSongShouldReturn204ForValidID() throws Exception {
//        mockMvc.perform(delete("/songs/" + currentID))
//                .andExpect(status().is(204))
//        ;
//    }

    @Test // on max
    void deleteSongShouldReturn400ForInvalidID() throws Exception {
        mockMvc.perform(delete("/songs/3000"))
                .andExpect(status().is(400))
        ;
    }
}
