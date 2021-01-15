package de.htwb.ai.kbe.controller;

import java.util.List;

import de.htwb.ai.kbe.service.ISongService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.htwb.ai.kbe.model.Song;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping(value = "/songs")
public class SongController {

    private final ISongService songService;

    public SongController(ISongService songService) {
        this.songService = songService;
    }

    //GET https://localhost:8443/songsWS-KBE/rest/songs
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Song>> getAllSongs() {
        List<Song> songs = songService.getAllSongs();

        if (songs == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }

    //GET https://localhost:8443/songsWS-KBE/rest/songs/1
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Song> getSong(@PathVariable("id") int id) {
        Song song = songService.getSongById(id);
        if (song == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(song, HttpStatus.OK);
    }

    //POST https://localhost:8443/songsWS-KBE/rest/songs
    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Song> addSong(@RequestBody Song s) {

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
    public ResponseEntity<Song> updateSong(@PathVariable("id") int id, @RequestBody Song s) {


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
    public ResponseEntity<Song> deleteSong(@PathVariable("id") int id) {
        try {
            songService.deleteSong(id);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

