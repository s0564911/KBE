package de.htwb.ai.kbe.dao;

import de.htwb.ai.kbe.model.Song;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository//("songDAO")
public class SongDAO implements ISongDAO {

//    private static final Logger logger = LoggerFactory.getLogger(SongDAO.class);

    private final SessionFactory sessionFactory;

    public SongDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Song> getAllSongs() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Song> cq = cb.createQuery(Song.class);
        Root<Song> root = cq.from(Song.class);
        cq.select(root);
        Query query = session.createQuery(cq);
        return query.getResultList();
//        for(Song s : songs) {
////            logger.info("Songs all :: " + s);
//        }
    }


    @Override
    public Song getSongById(int id) {
        Session currentSession = sessionFactory.getCurrentSession();
        return currentSession.get(Song.class, id); // or load?
////        logger.info("Song by ID = " + song);
//        return song;
    }

    @Override
    public void addSong(Song song) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(song);
//        logger.info("Song added = " + song);
    }

    @Override
    public void updateSong(Song song) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(song);
//        logger.info("Song updated = " + song);
    }

    @Override
    public void deleteSong(int id) {
        Session session = this.sessionFactory.getCurrentSession();
        Song song = (Song) session.load(Song.class, id);
        if(null != song){
            session.delete(song);
        }
//        logger.info("Song deleted = " + song);
    }

}
