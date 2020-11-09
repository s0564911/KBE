package htwb.ai.KBE;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;

/*
 * todo:
 * connect to external database
 * check if auto increment is okay, otherwise handle id increment
 * properly handle all bad requests as point 5 declares
 * clean up, error handling, tests
 */

public class SongServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;


    private static Connection getConnection() throws URISyntaxException, SQLException {
        //URI dbUri = new URI(System.getenv("DATABASE_URL"));

        //String username = dbUri.getUserInfo().split(":")[0];
        //String password = dbUri.getUserInfo().split(":")[1];
        //String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

//	    return DriverManager.getConnection("jdbc:postgresql://localhost:5432/testing", "lars", "lars123");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }


        String url = "jdbc:postgresql://kandula.db.elephantsql.com:5432/brtaqfrd";
        String username = "brtaqfrd";
        String password = "hye32qPx7vB13zqsAMtlEsp6B17M9srq";

        return DriverManager.getConnection(url, username, password);
    }


    //INSERT INTO songs(id,title,artist,label,released) VALUES  (0,'title1ball', 'miley', 'disney', 1993);
    //CREATE TABLE songs (id INT PRIMARY KEY NOT NULL, title VARCHAR(100) NOT NULL, artist VARCHAR(100), label VARCHAR(100), released INT);

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        try {

            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("error class not found exception");
            e.printStackTrace();

        }
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        PrintWriter out = response.getWriter();

        if (request.getParameter("songId") != null) {
            String songId = request.getParameter("songId");
            try {
                Connection connection = getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, title, artist, label, released FROM songs WHERE id=" + songId + ";");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String artist = rs.getString("artist");
                    String label = rs.getString("label");
                    int released = rs.getInt("released");
                    String jsonString = objectMapper.writeValueAsString(new OurSong(id, title, artist, label, released));
                    response.setContentType("application/json");
                    out.print(jsonString);
                    out.flush();

                }
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (request.getParameter("all") != null) {
            try {
                Connection connection = getConnection();
                Statement stmt = connection.createStatement();
                List<OurSong> list = new ArrayList<OurSong>();
                ResultSet rs = stmt.executeQuery("SELECT id, title, artist, label, released FROM songs;");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String artist = rs.getString("artist");
                    String label = rs.getString("label");
                    int released = rs.getInt("released");
                    OurSong aSong = new OurSong(id, title, artist, label, released);
                    list.add(aSong);
                }
                String jsonString = objectMapper.writeValueAsString(list);
                response.setContentType("application/json");
                out.print(jsonString);
                out.flush();
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();


        ServletInputStream inputStream = request.getInputStream();
        byte[] inBytes = IOUtils.toByteArray(inputStream);
        try (PrintWriter out = response.getWriter()) {
            OurSong song = objectMapper.readValue(new String(inBytes), OurSong.class);
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("INSERT INTO songs(title,artist,label,released) VALUES ('" + song.getTitle() + "', '" + song.getArtist() + "', '" + song.getLabel() + "', '" + song.getReleased() + "');");
            ResultSet rs = stmt.executeQuery("SELECT id,title FROM songs WHERE title='" + song.getTitle() + "';");
            response.setStatus(HttpServletResponse.SC_CREATED);
            int id = 0;
            while (rs.next()) {
                id = rs.getInt("id");
            }
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setHeader("Location", "/songsservlet-KBE/songs?songId=" + id);
            response.getWriter().print(id);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();

        }


    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(405);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(405);
    }


}
