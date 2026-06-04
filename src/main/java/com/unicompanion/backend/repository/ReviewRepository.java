package com.unicompanion.backend.repository;

import com.unicompanion.backend.model.Post;
import com.unicompanion.backend.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByPost(Post post);
}
