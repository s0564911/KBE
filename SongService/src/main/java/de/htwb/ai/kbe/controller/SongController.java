package de.htwb.ai.kbe.controller;

import de.htwb.ai.kbe.model.Song;
import de.htwb.ai.kbe.repo.SongRepository;
import de.htwb.ai.kbe.utils.AuthUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

//import de.htw.ai.kbe.utils.AuthUtils.*;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final DiscoveryClient discoveryClient;
    private final SongRepository songRepository;

    public SongController(DiscoveryClient discoveryClient, SongRepository repo) {
        this.discoveryClient = discoveryClient;
        this.songRepository = repo;
    }

    @RequestMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(
            @PathVariable String applicationName) {
        return this.discoveryClient.getInstances(applicationName);
    }

    //GET http://localhost:8082/songs
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Song>> getAllSongs(
            @RequestHeader(value = "Authorization", defaultValue = "") String token) {

        if (AuthUtils.authorize(token) == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Song> songs;
        try {
            songs = (List<Song>) songRepository.findAll();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (songs.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }

    //GET https://localhost:8082/songs/1
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Optional<Song>> getSong(@RequestHeader(value = "Authorization", defaultValue = "") String token,
                                                  @PathVariable("id") long id) {
        if (AuthUtils.authorize(token) == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Optional<Song> song;
        try {
            song = songRepository.findById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (song.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(song, HttpStatus.OK);
    }

    //POST https://localhost:8082/songs
    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Song> addSong(@RequestHeader(value = "Authorization", defaultValue = "") String token,
                                        @RequestBody Song s) {
        if (AuthUtils.authorize(token) == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/rest/songs/");
        Song song = Song.builder()
                .withTitle(s.getTitle())
                .withArtist(s.getArtist())
                .withLabel(s.getLabel())
                .withReleased(s.getReleased()).build();

        try {
            songRepository.save(song);
        } catch (Exception e) {
            return new ResponseEntity<>(song, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(song, headers, HttpStatus.CREATED);
    }

    //PUT https://localhost:8082/songs/1
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Song> updateSong(@RequestHeader(value = "Authorization", defaultValue = "") String token,
                                           @PathVariable("id") long id, @RequestBody Song s) {
        if (AuthUtils.authorize(token) == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Song song = Song.builder()
                .withTitle(s.getTitle())
                .withArtist(s.getArtist())
                .withLabel(s.getLabel())
                .withReleased(s.getReleased()).build();


        if (songRepository.findById(id).isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else if (song.getTitle() == null || s.getId() != id)
            return new ResponseEntity<>(song, HttpStatus.BAD_REQUEST);

        songRepository.save(song);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //DELETE https://localhost:8082/songs/1
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Song> deleteSong(@RequestHeader(value = "Authorization", defaultValue = "") String token,
                                           @PathVariable("id") long id) {
        if (AuthUtils.authorize(token) == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            songRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //PLAY https://localhost:8082/songs/1/play
    @RequestMapping(value = "/{id}/play", method = RequestMethod.GET)
    public ResponseEntity<Song> playSong(@RequestHeader(value = "Authorization", defaultValue = "") String token,
                                         @PathVariable("id") long id, HttpServletRequest request, HttpServletResponse response) {
        if (AuthUtils.authorize(token) == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        try {
            // call file service
            String url = "http://localhost:8080/files/" + id;
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", token);

            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            // create HttpHeaders for ResponseEntity
            HttpHeaders headers = new HttpHeaders();
            for (int i = 0;; i++) {
                String headerName = connection.getHeaderFieldKey(i);
                String headerValue = connection.getHeaderField(i);
                if(headerName != null && headerValue != null){
                    headers.set(headerName, headerValue);
                }
                if (headerName == null && headerValue == null) {
                    break;
                }
            }

            try (InputStream inputStream = connection.getInputStream();
                 OutputStream outputStream = response.getOutputStream();
            )
            {
                IOUtils.copy(inputStream, outputStream);
            }
            // create ResponseEntity
            return new ResponseEntity<>(headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
