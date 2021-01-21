package de.htwb.ai.kbe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.htwb.ai.kbe.dao.SongDAO;
import de.htwb.ai.kbe.dao.UserDAO;
import de.htwb.ai.kbe.model.Song;
import de.htwb.ai.kbe.model.User;
import de.htwb.ai.kbe.service.SongService;
import de.htwb.ai.kbe.service.UserService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.AfterClass;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

//@ExtendWith(SpringExtension.class)
//@ContextConfiguration("/test-config.xml")
public class SongControllerTests {
    private static MockMvc mockMvc;
    private static Session session;
    private static Transaction tx;
    private static SessionFactory sessionFactory;
    private static String token;
    private static HttpHeaders headers;

//    @Autowired
//    IUserService iUserService;
//    @Autowired
//    ISongService iSongService;

    private static final int currentID = 2;

    // get all

    @BeforeAll
    public static void setup() {

        sessionFactory = new MetadataSources(
                new StandardServiceRegistryBuilder()
                        .configure()
                        .build()
        ).buildMetadata().buildSessionFactory();

        UserService userService = new UserService(new UserDAO(sessionFactory));

        mockMvc = MockMvcBuilders.standaloneSetup(
                new SongController(//iSongService, iUserService
                        new SongService(new SongDAO(sessionFactory)),
                        userService
                )).build();

        session = sessionFactory.getCurrentSession();
        tx = session.beginTransaction();

        User u = User.builder().withUserId("mmuster").withFirstname("Maxime").withLastname("Muster").withPassword("pass1234").build();

        session.save(u);
        session.save(Song.builder().withTitle("Straight Outta Compton").withArtist("N.W.A.").withLabel("Ruthless").withReleased(1988).build());
        session.save(Song.builder().withTitle("Gangsta Gangsta").withArtist("N.W.A.").withLabel("Ruthless").withReleased(1988).build());
        session.save(Song.builder().withTitle("Fuck tha Police").withArtist("N.W.A.").withLabel("Ruthless").withReleased(1988).build());
        tx.commit();

        session = sessionFactory.getCurrentSession();
        tx = session.beginTransaction();
        token = userService.generateNewToken(u.getUserId(), u.getPassword());
        tx.commit();

        headers = new HttpHeaders();
        headers.add("Authorization", token);
    }

    @BeforeEach
    void beforeEach() {
        session = sessionFactory.getCurrentSession();
        tx = session.beginTransaction();
    }

    @AfterEach
    void afterEach() {
        tx.commit();
    }

//    @AfterClass
//    public static void closeTransaction() {
//        session.close();
//    }

    @Test
    void getAllSongsShouldReturnOKAndOnePage() throws Exception {
        mockMvc.perform(get("/songs/").headers(headers)).
                andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.totalPages").value(1))
        ;
    }

    // get ID

    @Test
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

    @Test
    void addSongShouldReturn201ForValidJSON() throws Exception {
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

//        JSONParser parser = new JSONParser();
//        JSONObject result = (JSONObject) parser.parse(content);
//        int id = (int) result.getAsNumber("id");
//        mockMvc.perform(delete("/songs/" + id))
//                .andExpect(status().is(204))
//        ;
//        tx.commit();
    }

    @Test
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

//    @Test
//    void addSongShouldReturn415ForXML() throws Exception {
//        String xml =
//
//                "<title>Dopeman (Remix)</title>\n" +
//                        "<artist>N.W.A.</artist>\n" +
//                        "<label>Ruthless</label>\n" +
//                        "<released>1988</released>";
//
//        mockMvc.perform(post("/songs/")
//                .contentType(MediaType.APPLICATION_ATOM_XML)
//                .content(xml))
//                .andExpect(status().is(415))
//        ;
//    }

//    @Test
//    void addSongShouldReturn415ForEmptyPayload() throws Exception {
//        mockMvc.perform(post("/songs/"))
//                .andExpect(status().is(415))
//        ;
//    }

    // put ID

//    @Test
//    void updateSongShouldReturn204ForValidIDAndJSON() throws Exception {
//        String json =
//
//                "{" +
//                        "    \"id\": \"" + (currentID+2) + "\"," +
//                        "    \"title\": \"I Aint tha 1\"," +
//                        "    \"artist\": \"N.W.A.\"," +
//                        "    \"label\": \"Ruthless\"," +
//                        "    \"released\": 1988" +
//                        "}";
//
//        mockMvc.perform(put("/songs/" + (currentID+2))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json))
//                .andExpect(status().is(204))
//        ;
//    }

    @Test
    void updateSongShouldReturn400ForInvalidIDInPayload() throws Exception {
        String json =

                "{" +
                        "    \"id\": \"3000\"," +
                        "    \"title\": \"Dopeman\"," +
                        "    \"artist\": \"N.W.A.\"," +
                        "    \"label\": \"Ruthless\"," +
                        "    \"released\": 1988" +
                        "}";

        mockMvc.perform(put("/songs/" + 1)
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

    @Test
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

//    @Test
//    void updateSongShouldReturn415ForXML() throws Exception {
//        String xml =
//
//                "<id>11</id>\n" +
//                        "<title>Dopeman (Remix)</title>\n" +
//                        "<artist>N.W.A.</artist>\n" +
//                        "<label>Ruthless</label>\n" +
//                        "<released>1988</released>";
//
//        mockMvc.perform(put("/songs/" + currentID)
//                .contentType(MediaType.APPLICATION_ATOM_XML)
//                .content(xml))
//                .andExpect(status().is(415))
//        ;
//    }

//    @Test
//    void updateSongShouldReturn415ForEmptyPayload() throws Exception {
//        mockMvc.perform(put("/songs/" + currentID))
//                .andExpect(status().is(415))
//        ;
//    }

//     delete ID

    @Test
    void deleteSongShouldReturn204ForValidID() throws Exception {
        mockMvc.perform(delete("/songs/" + currentID))
                .andExpect(status().is(204))
        ;
    }

    @Test
    void deleteSongShouldReturn400ForInvalidID() throws Exception {
        mockMvc.perform(delete("/songs/3000"))
                .andExpect(status().is(400))
        ;
    }
}
