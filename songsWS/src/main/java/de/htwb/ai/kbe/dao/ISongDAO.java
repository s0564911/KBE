package de.htwb.ai.kbe.dao;

import de.htwb.ai.kbe.model.Song;

//import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ISongDAO { // extends JpaRepository {

    List<Song> getAllSongs();
    Song getSongById(int id);
    void addSong(Song song);
    void updateSong(Song song);
    void deleteSong(int id);
}
