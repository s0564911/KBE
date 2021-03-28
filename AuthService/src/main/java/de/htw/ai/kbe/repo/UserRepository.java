package de.htw.ai.kbe.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.htw.ai.kbe.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByUserId(String userId);
}
