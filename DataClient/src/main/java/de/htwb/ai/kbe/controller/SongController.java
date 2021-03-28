//package de.htwb.ai.kbe.controller;
//
//import java.util.List;
//
//import de.htwb.ai.kbe.service.ISongService;
//import de.htwb.ai.kbe.service.IUserService;
//
////import org.graalvm.compiler.lir.LIRInstruction.Use;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//
//import de.htwb.ai.kbe.model.Song;
//
//import javax.persistence.EntityNotFoundException;

//@EnableEurekaServer
//@RestController
//@RequestMapping(value = "/songs")
//public class SongController {
//
//    private final DiscoveryClient discoveryClient;
//
//    private final ISongService songService;
//
//    private final IUserService userService;
//
//    public SongController(DiscoveryClient discoveryClient, ISongService songService, IUserService userService) {
//        this.discoveryClient = discoveryClient;
//        this.songService = songService;
//        this.userService = userService;
//    }
//
//    @RequestMapping("/service-instances/{applicationName}")
//    public List<ServiceInstance> serviceInstancesByApplicationName(
//            @PathVariable String applicationName) {
//        return this.discoveryClient.getInstances(applicationName);
//    }
//
//    //GET https://localhost:8443/songsWS-KBE/rest/songs
//    @RequestMapping(method = RequestMethod.GET)
//    public ResponseEntity<List<Song>> getAllSongs(
//    		@RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader) {
//
//    	if (!auth(optionalHeader)) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
//
//        List<Song> songs = songService.getAllSongs();
//        if (songs == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(songs, HttpStatus.OK);
//    }
//
//    //GET https://localhost:8443/songsWS-KBE/rest/songs/1
//    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
//    public ResponseEntity<Song> getSong(@RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader,
//    @PathVariable("id") int id) {
//    	if (!auth(optionalHeader)) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
//
//        Song song = songService.getSongById(id);
//        if (song == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(song, HttpStatus.OK);
//    }
//
//    //POST https://localhost:8443/songsWS-KBE/rest/songs
//    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
//    public ResponseEntity<Song> addSong(@RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader,
//    @RequestBody Song s) {
//    	if (!auth(optionalHeader)) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Location", "/rest/songs/");
//        Song song = Song.builder()
//                .withTitle(s.getTitle())
//                .withArtist(s.getArtist())
//                .withLabel(s.getLabel())
//                .withReleased(s.getReleased()).build();
//
//        try {
//            songService.addSong(song);
//        } catch (Exception e) {
//            return new ResponseEntity<>(song, HttpStatus.BAD_REQUEST);
//        }
//
//        return new ResponseEntity<>(song, headers, HttpStatus.CREATED);
//    }
//
//    //PUT https://localhost:8443/songsWS-KBE/rest/songs/10
//    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
//    public ResponseEntity<Song> updateSong(@RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader,
//    @PathVariable("id") int id, @RequestBody Song s) {
//    	if (!auth(optionalHeader)) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
//
//        Song song = Song.builder()
//                .withTitle(s.getTitle())
//                .withArtist(s.getArtist())
//                .withLabel(s.getLabel())
//                .withReleased(s.getReleased()).build();
//
//
//        if (songService.getSongById(id) == null)
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        else if (song.getTitle() == null || s.getId() != id)
//            return new ResponseEntity<>(song, HttpStatus.BAD_REQUEST);
//
//        songService.updateSong(song);
//
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    //DELETE https://localhost:8443/songsWS-KBE/rest/songs/10
//    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
//    public ResponseEntity<Song> deleteSong(@RequestHeader(value = "Authorization", defaultValue = "") String optionalHeader,
//    @PathVariable("id") int id) {
//    	if (!auth(optionalHeader)) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
//        try {
//            songService.deleteSong(id);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//
//    //helper
//    public boolean auth (String optionalHeader) {
//    	if (optionalHeader.equals("")) {
//    		return false;
//    	}
//    	else if (userService.validateJWT(optionalHeader)) {
//    		return true;
//    	}
//    	else {return false;}
//    }
//
//
//}

