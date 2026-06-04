package com.unicompanion.backend.repository;

import com.unicompanion.backend.model.AdminRequest;
import com.unicompanion.backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRequestRepository extends MongoRepository<AdminRequest, String> {
    Optional<AdminRequest> findByUser(User user);
}
