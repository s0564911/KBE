package de.htwb.ai.kbe.controller;

import de.htwb.ai.kbe.model.Song;
import de.htwb.ai.kbe.model.SongList;
import de.htwb.ai.kbe.repo.SongListRepository;
import de.htwb.ai.kbe.repo.SongRepository;
import de.htwb.ai.kbe.utils.AuthUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value = "/songLists")
class SongListController {

    private final DiscoveryClient discoveryClient;
    private final SongListRepository songListRepository;
    private final SongRepository songRepository;

    public SongListController(DiscoveryClient discoveryClient, SongListRepository songListRepository, SongRepository songRepository) {
        this.discoveryClient = discoveryClient;
        this.songListRepository = songListRepository;
        this.songRepository = songRepository;
    }

    @RequestMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(
            @PathVariable String applicationName) {
        return this.discoveryClient.getInstances(applicationName);
    }

    @RequestMapping(params = "userId", method = RequestMethod.GET, produces = {"application/json", "application/xml"})
    public ResponseEntity<List<SongList>> getSongListByParam(
            @RequestParam("userId") String userId,
            @RequestHeader(value = "Authorization", defaultValue = "") String token) {
        String user = AuthUtils.authorize(token);
        if (user == null || user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!user.equals(userId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            List<SongList> sl = songListRepository.findAllSongListsByOwnerId(user);
            return new ResponseEntity<>(sl, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {"application/json", "application/xml"})
    public ResponseEntity<Optional<SongList>> getSongList(@PathVariable("id") long id,
                                                @RequestHeader(value = "Authorization", defaultValue = "") String token) {
        String user = AuthUtils.authorize(token);
        if (user == null || user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Optional<SongList> songList = songListRepository.findById(id);
        if (songList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (songList.get().getIsPrivate() && !songList.get().getOwnerId().equals(user)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(songList, HttpStatus.OK);
    }

    //post
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<SongList> addSongList(@RequestBody SongList s,
                                                @RequestHeader(value = "Authorization", defaultValue = "") String token) {
        String user = AuthUtils.authorize(token);
        if (user == null || user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/songs/");

        Set<Song> checkList = s.getSongList();
        for (Song song : checkList) {
            Song compare = null;
            if (songRepository.findById(song.getId()).isPresent())
                compare = songRepository.findById(song.getId()).get();
            if (compare == null || song.getTitle() == null ||
                    !(compare.getArtist().equals(song.getArtist()) &&
                            compare.getLabel().equals(song.getLabel()) &&
                            compare.getReleased() == song.getReleased() &&
                            compare.getTitle().equals(song.getTitle()))) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        SongList songList = SongList.builder()
                .withName(s.getName())
                .withIsPrivate(s.getIsPrivate())
                .withOwnerId(user)
//                .build();
                .withSongs(s.getSongList()).build();

        try {
            songListRepository.save(songList);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(songList, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(songList, headers, HttpStatus.CREATED);
    }

    //put
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity<SongList> updateSongList(@RequestBody SongList s, @PathVariable("id") long id,
                                                   @RequestHeader(value = "Authorization", defaultValue = "") String token) {
        String user = AuthUtils.authorize(token);
        if (user == null || user.isEmpty() || (songListRepository.findById(id).isPresent() && !user.equals(songListRepository.findById(id).get().getOwnerId()))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (songListRepository.findById(id).isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/songs/");

        Set<Song> checkList = s.getSongList();
        for (Song song : checkList) {
            Song compare = null;
            if (songRepository.findById(song.getId()).isPresent())
                compare = songRepository.findById(song.getId()).get();
            if (compare == null || song.getTitle() == null ||
                    !(compare.getArtist().equals(song.getArtist()) &&
                            compare.getLabel().equals(song.getLabel()) &&
                            compare.getReleased() == song.getReleased() &&
                            compare.getTitle().equals(song.getTitle()))) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        SongList songList = SongList.builder()
                .withId(id)
                .withName(s.getName())
                .withIsPrivate(s.getIsPrivate())
                .withOwnerId(user)
                .withSongs(s.getSongList()).build();

        try {
            songListRepository.save(songList);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(songList, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(songList, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<SongList> deleteSongList(@PathVariable("id") long id,
                                                   @RequestHeader(value = "Authorization", defaultValue = "") String token) {
        String user = AuthUtils.authorize(token);
        if (user == null || user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (songListRepository.findById(id).isPresent() && user.equals(songListRepository.findById(id).get().getOwnerId())) {
            try {
                songListRepository.delete(songListRepository.findById(id).get());
            } catch (EntityNotFoundException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
//
//    public String auth(String token) {
//        if (token.equals("")) {
//            return null;
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
//                    return String.valueOf(response);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
}