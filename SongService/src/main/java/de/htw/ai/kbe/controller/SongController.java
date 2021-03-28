package de.htw.ai.kbe.controller;

import de.htw.ai.kbe.model.Song;
import de.htw.ai.kbe.repo.SongRepository;
import de.htw.ai.kbe.utils.AuthUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
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

//    // ToDo - make REST-HTTP-Request to Auth (localhost:8081)
//    public boolean auth(String token) {
//        if (token.equals("")) {
//            return false;
//        } else {
//            try {
//                String url = "http://localhost:8080/auth";
//                URL urlObj = new URL(url);
//                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
//
//                connection.setRequestMethod("GET");
//                connection.setRequestProperty("Authorization", token);
//
//                System.out.println("Send 'HTTP GET' request to : " + url);
//
//                int responseCode = connection.getResponseCode();
//
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    BufferedReader inputReader = new BufferedReader(
//                            new InputStreamReader(connection.getInputStream()));
//                    String inputLine;
//                    StringBuilder response = new StringBuilder();
//
//                    while ((inputLine = inputReader.readLine()) != null) {
//                        response.append(inputLine);
//                    }
//
//                    inputReader.close();
//
//                    return !String.valueOf(response).isEmpty();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }
}
