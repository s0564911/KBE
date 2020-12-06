package de.htwb.ai.kbe.service;

import de.htwb.ai.kbe.dao.ISongDAO;
import de.htwb.ai.kbe.model.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service//("songService")
public class SongService implements ISongService{

    // ToDo - maybe skip service for Direct Injection? try calling

    @Autowired
    private ISongDAO songDAO;

    @Override
    @Transactional
    public List<Song> getAllSongs() {
        return this.songDAO.getAllSongs();
    }

    @Override
    @Transactional
    public Song getSongById(int id) {
        return this.songDAO.getSongById(id);
    }

    @Override
    @Transactional
    public void addSong(Song song) {
        this.songDAO.addSong(song);
    }

    @Override
    @Transactional
    public void updateSong(Song song) {
        this.songDAO.updateSong(song);
    }

    @Override
    @Transactional
    public void deleteSong(int id) {
        this.songDAO.deleteSong(id);
    }
}
