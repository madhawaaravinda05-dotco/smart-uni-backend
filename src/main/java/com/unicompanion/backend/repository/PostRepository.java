package com.unicompanion.backend.repository;

import com.unicompanion.backend.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByUniversityAndStatus(String university, String status);

    @org.springframework.data.mongodb.repository.Query("{ 'university': ?0, 'status': ?1, 'category': { $regex: ?2, $options: 'i' } }")
    List<Post> findByUniversityAndStatusAndCategory(String university, String status, String category);

    List<Post> findByPostedById(String id);
}
