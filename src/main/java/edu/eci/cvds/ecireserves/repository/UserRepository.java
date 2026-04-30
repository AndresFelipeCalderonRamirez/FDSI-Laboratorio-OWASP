package edu.eci.cvds.ecireserves.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.eci.cvds.ecireserves.enums.UserRole;
import edu.eci.cvds.ecireserves.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    List<User>findByName(String name);
    Optional<User> findByEmail(String email);
    List<User> findByRol(UserRole rol);
}
