package com.turalabdullayev.cv_scanner.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.turalabdullayev.cv_scanner.service.FileStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/cv")
@Tag(name = "CV Upload Controller")
public class CvUploadController {

	private final FileStorageService fileStorageService;
	private final JobLauncher jobLauncher;
	private final Job cvScannerJob;

	public CvUploadController(FileStorageService fileStorageService, JobLauncher jobLauncher, Job cvScannerJob) {
		this.fileStorageService = fileStorageService;
		this.jobLauncher = jobLauncher;
		this.cvScannerJob = cvScannerJob;
	}

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Zip formatinda CV toplu qovluqunu yukleyin")
	public ResponseEntity<String> uploadCvZip(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("Xahis olunur bos olmayan bir fayl yukleyin!");
		}

		try {
			String targetPath = fileStorageService.unzipCvFolder(file);

			JobParameters jobParameters = new JobParametersBuilder().addString("cvFolder", targetPath)
					.addLong("time", System.currentTimeMillis()).toJobParameters();

			jobLauncher.run(cvScannerJob, jobParameters);

			return ResponseEntity
					.ok("Fayl ugurla qebul edildi, cixarildi ve Batch prosesi basladildi! Qovluq: " + targetPath);

		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Proses zamani xeta bas verdi: " + e.getMessage());
		}
	}
}