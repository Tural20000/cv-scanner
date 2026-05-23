package com.turalabdullayev.cv_scanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "candidates", indexes = { @Index(name = "idx_candidate_skills", columnList = "technicalSkills"),
		@Index(name = "idx_candidate_experience", columnList = "yearsOfExperience") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String fullName;
	private int yearsOfExperience;

	@Column(length = 2000)
	private String technicalSkills;

	private String preferredJobType;
	private String location;

	@Column(length = 1000)
	private String email;

	private String fileName;
}