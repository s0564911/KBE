package de.htwb.ai.kbe.controller;

import java.util.List;

import de.htwb.ai.kbe.dao.ISongDAO;
import de.htwb.ai.kbe.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.htwb.ai.kbe.model.Song;

@RestController
@RequestMapping(value = "/songs")
public class SongController {

    @Autowired
    private ISongService songService;

//    @Autowired
//    private ISongDAO songDAO;

    //GET http://localhost:8080/songsWS-KBE/rest/songs
    @RequestMapping
    public ResponseEntity<List<Song>> getAllSongs() {
//        HttpHeaders headers = new HttpHeaders();
        List<Song> songs = songService.getAllSongs();

        if (songs == null) {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }
//        headers.add("Some Information");
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }

    //GET http://localhost:8080/songsWS-KBE/rest/songs/1
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Song> getSong(@PathVariable("id") int id) {
        Song song = songService.getSongById(id);
        if (song == null) {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }
        return new ResponseEntity<>(song, HttpStatus.OK);
    }

    // ToDo: response method implementation, tests, users - rest down is crap - also smh file encoding again

    //POST http://localhost:8080/songsWS-KBE/rest/songs
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<Song> addSong(@PathVariable("title") String title,
                                        @PathVariable("artist") String artist) {
        Song song = Song.builder().withTitle(title).withArtist(artist).build();
        songService.addSong(song);
        if (song == null) {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }
        return new ResponseEntity<>(song, HttpStatus.OK);
    }

    //PUT http://localhost:8080/songsWS-KBE/rest/songs/1
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Song> addSong(@PathVariable("id") int id,
                                        @PathVariable("title") String title,
                                        @PathVariable("artist") String artist) {
        Song song = Song.builder().withTitle(title).withArtist(artist).build();
        songService.updateSong(song);
        if (song == null) {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }
        return new ResponseEntity<>(song, HttpStatus.OK);
    }

    //DELETE http://localhost:8080/songsWS-KBE/rest/songs/1
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteSong(@PathVariable("id") int id) {
        songService.deleteSong(id);
    }

    // see if elephantSQL or implementation failed
    // Status OK -> elephantSQL
    // Status 404 -> implementation
    @RequestMapping(value = "/test")
    public ResponseEntity<Song> doTest() {
        return new ResponseEntity<>(HttpStatus.OK);
    }


}

