package de.htwb.ai.kbe.service;

import de.htwb.ai.kbe.dao.ISongListDAO;
import de.htwb.ai.kbe.model.SongList;
import de.htwb.ai.kbe.model.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("songListService")
public class SongListService implements ISongListService{

    private final ISongListDAO songListDAO;

    public SongListService(ISongListDAO songListDAO) {
        this.songListDAO = songListDAO;
    }
    @Override
    @Transactional
    public SongList getSonglistById(int id) {
    	return this.songListDAO.getSonglistById(id);
    }
    
    @Override
    @Transactional
    public List<SongList> getPublicSonglistsByUser(User user){
    	return this.songListDAO.getPublicSonglistsByUser(user);
    }
    @Override
    @Transactional
    public List<SongList> getAllSonglistsByUser(User user){
    	return this.songListDAO.getAllSonglistsByUser(user);
    }
    @Override
    @Transactional
    public void addSonglist(SongList songList) {
    	this.songListDAO.addSonglist(songList);
    }
    @Override
    @Transactional
    public void deleteSonglistById(int id) {
    	this.songListDAO.deleteSonglistById(id);
    }
    @Override
    @Transactional
    public User getSonglistOwnerById(int id) {
    	return this.songListDAO.getSonglistOwnerById(id);
    }

}