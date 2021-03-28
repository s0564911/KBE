package de.htw.ai.kbe.repo;

import de.htw.ai.kbe.model.Song;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends CrudRepository<Song, Long> {

//    List<Song> findByUserId(String userId);
}
