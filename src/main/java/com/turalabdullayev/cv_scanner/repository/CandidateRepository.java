package com.turalabdullayev.cv_scanner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.turalabdullayev.cv_scanner.model.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long>, JpaSpecificationExecutor<Candidate> {

	List<Candidate> findByTechnicalSkillsContainingIgnoreCaseAndYearsOfExperienceGreaterThanEqualAndLocationContainingIgnoreCase(
			String skills, int minExperience, String location);
}