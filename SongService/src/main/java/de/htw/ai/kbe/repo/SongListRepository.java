package de.htw.ai.kbe.repo;

import de.htw.ai.kbe.model.Song;
import de.htw.ai.kbe.model.SongList;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SongListRepository extends CrudRepository<SongList, Long> {

    List<SongList> findAllSongListsByOwnerId(String ownerId);
    List<SongList> findAllByOwnerIdAndIsPrivate(String ownerId, Boolean priv);
}
