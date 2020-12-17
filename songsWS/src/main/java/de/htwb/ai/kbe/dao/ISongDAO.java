package de.htwb.ai.kbe.dao;

import de.htwb.ai.kbe.model.Song;

import java.util.List;

public interface ISongDAO {

    List<Song> getAllSongs();
    Song getSongById(int id);
    void addSong(Song song);
    void updateSong(Song song);
    void deleteSong(int id);
}
