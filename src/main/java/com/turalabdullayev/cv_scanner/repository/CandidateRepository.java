package com.turalabdullayev.cv_scanner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.turalabdullayev.cv_scanner.model.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
	List<Candidate> findByTechnicalSkillsContainingIgnoreCaseAndYearsOfExperienceGreaterThanEqualAndPreferredLocationContainingIgnoreCase(
			String skills, int minExperience, String location);
}