package com.turalabdullayev.cv_scanner.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turalabdullayev.cv_scanner.model.Candidate;
import com.turalabdullayev.cv_scanner.repository.CandidateRepository;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

	private final CandidateRepository candidateRepository;

	public CandidateController(CandidateRepository candidateRepository) {
		this.candidateRepository = candidateRepository;
	}

	@GetMapping
	public ResponseEntity<List<Candidate>> getAllCandidates() {
		return ResponseEntity.ok(candidateRepository.findAll());
	}

	@GetMapping("/search")
	public ResponseEntity<List<Candidate>> searchCandidates(
			@RequestParam(value = "skills", defaultValue = "") String skills,
			@RequestParam(value = "minExperience", defaultValue = "0") int minExperience,
			@RequestParam(value = "location", defaultValue = "") String location) {

		List<Candidate> filteredCandidates = candidateRepository
				.findByTechnicalSkillsContainingIgnoreCaseAndYearsOfExperienceGreaterThanEqualAndPreferredLocationContainingIgnoreCase(
						skills, minExperience, location);

		return ResponseEntity.ok(filteredCandidates);
	}
}