package de.htwb.ai.kbe.dao;

import de.htwb.ai.kbe.model.SongList;
import de.htwb.ai.kbe.model.User;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.crypto.spec.SecretKeySpec;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.bind.DatatypeConverter;

import java.util.List;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Repository("songListDAO")
public class SongListDAO implements ISongListDAO {

    private final SessionFactory sessionFactory;

    public SongListDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
   
    @Override
    public SongList getSonglistById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(SongList.class, id);

    }
    
    @Override
    public User getSonglistOwnerById(int id) {
    	Session session = sessionFactory.getCurrentSession();
         
         SongList sl = session.get(SongList.class, id);
         return sl.getOwnerid();
    }

	@Override
	public List<SongList> getAllSonglistsByUser(User user) {
		Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<SongList> cq = cb.createQuery(SongList.class);
        Root<SongList> root = cq.from(SongList.class);
        cq.select(root);
        cq.where( cb.equal(root.get("ownerId"), user));
        List<SongList> result = session.createQuery(cq).getResultList();
        return result;
	}


	@Override
	public List<SongList> getPublicSonglistsByUser(User user) {
		Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<SongList> cq = cb.createQuery(SongList.class);
        Root<SongList> root = cq.from(SongList.class);
        
        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(root.get("ownerId"), user);
        predicates[1] = cb.isFalse(root.get("isPriv") );
        cq.select(root).where(predicates);
        List<SongList> result = session.createQuery(cq).getResultList();
        return result;
	}


    @Override
    public void addSonglist(SongList songList) {
        Session session = this.sessionFactory.getCurrentSession();
        session.save(songList);
    }


    @Override
    public void deleteSonglistById(int id) {
        Session session = this.sessionFactory.getCurrentSession();
        SongList sl = session.load(SongList.class, id);
        if(null != sl){
            session.delete(sl);
        }
    }



}
