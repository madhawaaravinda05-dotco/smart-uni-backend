package com.unicompanion.backend.repository;

import com.unicompanion.backend.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
    List<Report> findByUniversityAndStatusOrderByCreatedAtDesc(String university, String status);
    List<Report> findByStatusOrderByCreatedAtDesc(String status);
}
