package de.htwb.ai.kbe.service;

import de.htwb.ai.kbe.model.Song;

import java.util.List;

public interface ISongService {

    List<Song> getAllSongs();
    Song getSongById(int id);
    void addSong(Song song);
    void updateSong(Song song);
    void deleteSong(int id);
}