package de.htwb.ai.kbe.repo;

import de.htwb.ai.kbe.model.Song;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends CrudRepository<Song, Long> {

//    List<Song> findByUserId(String userId);
}
