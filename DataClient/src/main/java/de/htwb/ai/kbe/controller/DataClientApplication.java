package de.htwb.ai.kbe.controller;

import de.htwb.ai.kbe.model.Song;
import de.htwb.ai.kbe.model.SongList;
import de.htwb.ai.kbe.model.User;
import de.htwb.ai.kbe.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableEurekaClient
@EnableJpaRepositories("de.htwb.ai.kbe.dao")
public class DataClientApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(DataClientApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@RestController
@RequestMapping(value = "/songs")
@ComponentScan("de.htwb.ai.kbe")
class SongController {

    private final DiscoveryClient discoveryClient;
    private final ISongService songService;
    private final IUserService userService;

//    @Autowired(required = true)
//    public void setSongService(ISongService songService) {
//        this.songService = songService;
//    }
//
//    @Autowired(required = true)
//    public void setUserService(IUserService userService) {
//        this.userService = userService;
//    }

    public SongController(DiscoveryClient discoveryClient, ISongService songService, IUserService userService) {
        this.discoveryClient = discoveryClient;
        this.songService = songService;
        this.userService = userService;
    }

    @RequestMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(
            @PathVariable String applicationName) {
        return this.discoveryClient.getInstances(applicationName);
    }

    //GET https://localhost:8443/songsWS-KBE/rest/songs
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Song>> getAllSongs(
            @RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader) {

        if (!auth(optionalHeader)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Song> songs = songService.getAllSongs();
        if (songs == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }

    //GET https://localhost:8443/songsWS-KBE/rest/songs/1
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Song> getSong(@RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader,
                                        @PathVariable("id") int id) {
        if (!auth(optionalHeader)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Song song = songService.getSongById(id);
        if (song == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(song, HttpStatus.OK);
    }

    //POST https://localhost:8443/songsWS-KBE/rest/songs
    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Song> addSong(@RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader,
                                        @RequestBody Song s) {
        if (!auth(optionalHeader)) {
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
            songService.addSong(song);
        } catch (Exception e) {
            return new ResponseEntity<>(song, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(song, headers, HttpStatus.CREATED);
    }

    //PUT https://localhost:8443/songsWS-KBE/rest/songs/10
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Song> updateSong(@RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader,
                                           @PathVariable("id") int id, @RequestBody Song s) {
        if (!auth(optionalHeader)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Song song = Song.builder()
                .withTitle(s.getTitle())
                .withArtist(s.getArtist())
                .withLabel(s.getLabel())
                .withReleased(s.getReleased()).build();


        if (songService.getSongById(id) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else if (song.getTitle() == null || s.getId() != id)
            return new ResponseEntity<>(song, HttpStatus.BAD_REQUEST);

        songService.updateSong(song);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //DELETE https://localhost:8443/songsWS-KBE/rest/songs/10
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Song> deleteSong(@RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader,
                                           @PathVariable("id") int id) {
        if (!auth(optionalHeader)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            songService.deleteSong(id);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    //helper
    public boolean auth(String optionalHeader) {
        if (optionalHeader.equals("")) {
            return false;
        } else return userService.validateJWT(optionalHeader);
    }
}

@RestController
@RequestMapping(value = "/songLists")
@ComponentScan("de.htwb.ai.kbe")
class SongListController {

    private final DiscoveryClient discoveryClient;
    private final ISongListService songListService;
    private final IUserService userService;
    private final ISongService songService;

    public SongListController(DiscoveryClient discoveryClient, ISongListService songListService, IUserService userService, ISongService songService) {
        this.discoveryClient = discoveryClient;
        this.userService = userService;
        this.songListService = songListService;
        this.songService = songService;
    }

    @RequestMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(
            @PathVariable String applicationName) {
        return this.discoveryClient.getInstances(applicationName);
    }

    /**
     * @param userId
     * @param optionalHeader
     * @return returns public or private songlist based on if the requester owns the songlist
     */
    @RequestMapping(params="userId",method = RequestMethod.GET,produces = {"application/json", "application/xml"})
    public ResponseEntity<List<SongList>> getSongListByParam(
            @RequestParam("userId") String userId,
            @RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader) {

        if (!auth(optionalHeader)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUserId(userId);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } ///idk if this comparison works
        if (userService.compareTokenToUser(optionalHeader, user)) {
            List<SongList> sl = songListService.getAllSonglistsByUser(user);
            return new ResponseEntity<>(sl, HttpStatus.OK);
        } else {
            List<SongList> sl = songListService.getPublicSonglistsByUser(user);
            return new ResponseEntity<>(sl, HttpStatus.OK);
        }
    }

    /**
     * @param id
     * @param optionalHeader
     * @return returns songlist based on id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {"application/json", "application/xml"})
    public ResponseEntity<SongList> getSongList(@PathVariable("id") int id,
                                                @RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader) {
        if (!auth(optionalHeader)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SongList songList = songListService.getSonglistById(id);
        if (songList == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!songListService.getSonglistOwnerById(id).getUserId().equals(
                userService.getUserByUserId(userService.getUsernameFromToken(optionalHeader)).getUserId())) {
            if (songList.getIsPrivate()) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            System.out.println(songListService.getSonglistOwnerById(id));
            System.out.println(userService.getUserByUserId(userService.getUsernameFromToken(optionalHeader)));
        }
        return new ResponseEntity<>(songList, HttpStatus.OK);
    }

    //post
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<SongList> addSongList(@RequestBody SongList s,
                                                @RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader) {
        if (!auth(optionalHeader)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/rest/songs/");

        Set<Song> checkList = s.getSongList();
        for (Song song : checkList) {
            Song compare = songService.getSongById(song.getId());
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
                .withIsPriv(s.getIsPrivate())
                .withOwnerid(userService.getUserByUserId(userService.getUsernameFromToken(optionalHeader)))
                .withSongs(s.getSongList()).build();
        try {
            songListService.addSonglist(songList);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(songList, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(songList, headers, HttpStatus.CREATED);
    }


    /**
     * @param id
     * @param optionalHeader
     * @return deletes entry based on id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<SongList> deleteSongList(@PathVariable("id") int id,
                                                   @RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader) {
        if (!auth(optionalHeader)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (userService.compareTokenToUser(optionalHeader, songListService.getSonglistOwnerById(id))) {
            try {
                songListService.deleteSonglistById(id);
            } catch (EntityNotFoundException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    //helper
    public boolean auth(String optionalHeader) {
        if (optionalHeader.equals("")) {
            return false;
        } else return userService.validateJWT(optionalHeader);
    }


}
