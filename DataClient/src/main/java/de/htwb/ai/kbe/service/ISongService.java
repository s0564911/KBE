package de.htwb.ai.kbe.service;

import de.htwb.ai.kbe.model.Song;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

//@Component
//@Service("ISongService")
public interface ISongService {

    List<Song> getAllSongs();
    Song getSongById(int id);
    void addSong(Song song);
    void updateSong(Song song);
    void deleteSong(int id);
}