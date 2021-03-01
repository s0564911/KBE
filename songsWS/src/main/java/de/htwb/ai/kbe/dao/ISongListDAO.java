package de.htwb.ai.kbe.dao;


import de.htwb.ai.kbe.model.SongList;
import de.htwb.ai.kbe.model.User;

import java.util.List;

public interface ISongListDAO {

    SongList getSonglistById(int id);
    List<SongList> getPublicSonglistsByUser(User user);
    List<SongList> getAllSonglistsByUser(User user);
    void addSonglist(SongList songList);
    void deleteSonglistById(int id);
	User getSonglistOwnerById(int id);
}
