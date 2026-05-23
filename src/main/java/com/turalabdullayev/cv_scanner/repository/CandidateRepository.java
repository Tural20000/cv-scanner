package com.turalabdullayev.cv_scanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.turalabdullayev.cv_scanner.model.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

}
