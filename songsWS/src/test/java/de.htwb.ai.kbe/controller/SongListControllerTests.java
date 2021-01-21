package de.htwb.ai.kbe.controller;

import de.htwb.ai.kbe.dao.SongDAO;
import de.htwb.ai.kbe.dao.SongListDAO;
import de.htwb.ai.kbe.dao.UserDAO;
import de.htwb.ai.kbe.model.Song;
import de.htwb.ai.kbe.model.SongList;
import de.htwb.ai.kbe.model.User;
import de.htwb.ai.kbe.service.SongListService;
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
import org.hamcrest.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SongListControllerTests {
    private static MockMvc mockMvc;
    private static Session session;
    private static Transaction tx;
    private static SessionFactory sessionFactory;
    private static String token;
    private static HttpHeaders headers;

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
                new SongListController(
                        new SongListService(new SongListDAO(sessionFactory)),
                        userService,
                        new SongService(new SongDAO(sessionFactory))
                )).build();

        session = sessionFactory.getCurrentSession();
        tx = session.beginTransaction();

        User mmuster = User.builder().withUserId("mmuster").withFirstname("Maxime").withLastname("Muster").withPassword("pass1234").build();
        User eschuler = User.builder().withUserId("eschuler").withFirstname("Elena").withLastname("Schuler").withPassword("pass1234").build();

        Song song1 = Song.builder().withTitle("Straight Outta Compton").withArtist("N.W.A.").withLabel("Ruthless").withReleased(1988).build();
        Song song2 = Song.builder().withTitle("Gangsta Gangsta").withArtist("N.W.A.").withLabel("Ruthless").withReleased(1988).build();
        Song song3 = Song.builder().withTitle("Fuck tha Police").withArtist("N.W.A.").withLabel("Ruthless").withReleased(1988).build();
        Song song4 = Song.builder().withTitle("Parental Discretion Iz Advised").withArtist("N.W.A.").withLabel("Ruthless").withReleased(1988).build();
        Song song5 = Song.builder().withTitle("8 Ball (Remix)").withArtist("N.W.A.").withLabel("Ruthless").withReleased(1988).build();
        Song song6 = Song.builder().withTitle("Something Like That").withArtist("N.W.A.").withLabel("Ruthless").withReleased(1988).build();

        session.save(mmuster);
        session.save(eschuler);
        session.save(song1);
        session.save(song2);
        session.save(song3);
        session.save(song4);
        session.save(song5);
        session.save(song6);
        tx.commit();

        session = sessionFactory.getCurrentSession();
        tx = session.beginTransaction();
        token = userService.generateNewToken(mmuster.getUserId(), mmuster.getPassword());
        tx.commit();

        session = sessionFactory.getCurrentSession();
        tx = session.beginTransaction();

        headers = new HttpHeaders();
        headers.add("Authorization", token);

        SongList songList1 = SongList.builder().withOwnerid(mmuster).withIsPriv(true).withName("Liste1").withSongs(new HashSet<>(Arrays.asList(song4, song5))).build();
        SongList songList2 = SongList.builder().withOwnerid(mmuster).withIsPriv(false).withName("Liste2").withSongs(new HashSet<>(Arrays.asList(song1, song2))).build();
        SongList songList3 = SongList.builder().withOwnerid(eschuler).withIsPriv(true).withName("Liste3").withSongs(new HashSet<>(Arrays.asList(song3, song6))).build();
        SongList songList4 = SongList.builder().withOwnerid(eschuler).withIsPriv(false).withName("Liste4").withSongs(new HashSet<>(Arrays.asList(song1, song4))).build();

        session.save(songList1);
        session.save(songList2);
        session.save(songList3);
        session.save(songList4);

        tx.commit();
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

    @AfterClass
    public static void closeTransaction() {
        session.close();
    }

    @Test
    void getSongListShouldReturnOk() throws Exception {
        mockMvc.perform(get("/songLists/1").headers(headers))
                .andExpect(status().isOk());
    }
    @Test
    void getSongListShouldReturnCorrectJson() throws Exception {
        mockMvc.perform(get("/songLists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Liste1"))
                .andExpect(jsonPath("$.ownerId").value("mmuster"))
                .andExpect(jsonPath("$.isPrivate").value(true))
                .andExpect(jsonPath("$.songList").isArray())
                .andExpect(jsonPath("$.songList", Matchers.hasSize(2)))
                .andReturn().getResponse().getContentAsString();

    }
    @Test
    void getSongListShouldReturn404forWrongId() throws Exception {
        mockMvc.perform(get("/songLists/3000").headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void getWithIdShouldReturnOk() throws Exception{
        mockMvc.perform(get("/songLists?userId=mmuster").headers(headers))
                .andExpect(status().isOk());
    }

    @Test
    void getWithIdtShouldReturn404forWrongId() throws Exception {
        mockMvc.perform(get("/songLists?userId=hierstehtunsinn").headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void getWithIdForAuthorizedUser() throws Exception {
        mockMvc.perform(get("/songLists?userId=mmuster").headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void getWithIdForNonAuthorizedUser() throws Exception {
        mockMvc.perform(get("/songLists?userId=eschuler").headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void postSongListShouldReturnOKForValidSongList() throws Exception {
        String json = "{\n" +
                "\t\"isPrivate\": true,\n" +
                "\t\"name\": \"MaximesPrivate\",\n" +
                "\t\"songList\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 5,\n" +
                "\t\t\t\"title\": \"8 Ball (Remix)\",\n" +
                "\t\t\t\"artist\": \"N.W.A.\",\n" +
                "\t\t\t\"label\": \"Ruthless\",\n" +
                "\t\t\t\"released\": 1988\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 6,\n" +
                "\t\t\t\"title\": \"Something Like That\",\n" +
                "\t\t\t\"artist\": \"N.W.A.\",\n" +
                "\t\t\t\"label\": \"Ruthless\",\n" +
                "\t\t\t\"released\": 1988\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}\n" +
                " ";

        mockMvc.perform(post("/songLists/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers))
                .andExpect(status().is(201));
    }

    @Test
    void postSongListShouldReturn400ForInvalidSongList() throws Exception {
        String json = "{\n" +
                "\t\"isPrivate\": true,\n" +
                "\t\"name\": \"MaximesPrivate\",\n" +
                "\t\"songList\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 3,\n" +
                "\t\t\t\"title\": \"8 Ball (Remix)\",\n" +
                "\t\t\t\"artist\": \"N.W.A.\",\n" +
                "\t\t\t\"label\": \"Ruthless\",\n" +
                "\t\t\t\"released\": 1988\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 6,\n" +
                "\t\t\t\"title\": \"Something Like That\",\n" +
                "\t\t\t\"artist\": \"N.W.A.\",\n" +
                "\t\t\t\"label\": \"Ruthless\",\n" +
                "\t\t\t\"released\": 1988\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}\n" +
                " ";

        mockMvc.perform(post("/songLists/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers))
                .andExpect(status().is(400));
    }

    @Test
    void postSongListShouldReturn400ForInvalidSongListWithNonExistingID() throws Exception {
        String json = "{\n" +
                "\t\"isPrivate\": true,\n" +
                "\t\"name\": \"MaximesPrivate\",\n" +
                "\t\"songList\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 20,\n" +
                "\t\t\t\"title\": \"8 Ball (Remix)\",\n" +
                "\t\t\t\"artist\": \"N.W.A.\",\n" +
                "\t\t\t\"label\": \"Ruthless\",\n" +
                "\t\t\t\"released\": 1988\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": 6,\n" +
                "\t\t\t\"title\": \"Something Like That\",\n" +
                "\t\t\t\"artist\": \"N.W.A.\",\n" +
                "\t\t\t\"label\": \"Ruthless\",\n" +
                "\t\t\t\"released\": 1988\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}\n" +
                " ";

        mockMvc.perform(post("/songLists/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers))
                .andExpect(status().is(400));
    }

    @Test
    void postSongListShouldReturn400ForEmptySongList() throws Exception {
        String json = "{\n" +
                "\t\"isPrivate\": true,\n" +
                "\t\"name\": \"MaximesPrivate\",\n" +
                "\t\"songList\": [\n" +
                "\t\t{\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}\n" +
                " ";

        mockMvc.perform(post("/songLists/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .headers(headers))
                .andExpect(status().is(400));
    }

    @Test
    void deleteSongListShouldReturnOKForValidID() throws Exception {
        mockMvc.perform(delete("/songLists/2" ).headers(headers))
                .andExpect(status().is(204))
        ;
    }

    @Test
    void deleteSongListShouldReturn403ForListOfOthers() throws Exception {
        mockMvc.perform(delete("/songLists/3").headers(headers))
                .andExpect(status().is(403))
        ;
    }

}
