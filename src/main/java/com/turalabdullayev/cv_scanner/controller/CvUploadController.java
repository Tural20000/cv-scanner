package com.turalabdullayev.cv_scanner.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@Tag(name = "CV Upload & Monitor Controller")
public class CvUploadController {

	private final FileStorageService fileStorageService;
	private final JobLauncher jobLauncher;
	private final Job cvScannerJob;
	private final JobExplorer jobExplorer;

	public CvUploadController(FileStorageService fileStorageService, JobLauncher jobLauncher, Job cvScannerJob,
			JobExplorer jobExplorer) {
		this.fileStorageService = fileStorageService;
		this.jobLauncher = jobLauncher;
		this.cvScannerJob = cvScannerJob;
		this.jobExplorer = jobExplorer;
	}

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Zip formatında CV toplu qovluğunu yükləyin")
	public ResponseEntity<String> uploadCvZip(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("Xahiş olunur boş olmayan bir fayl yükləyin!");
		}

		try {
			String targetPath = fileStorageService.unzipCvFolder(file);

			JobParameters jobParameters = new JobParametersBuilder().addString("cvFolder", targetPath)
					.addLong("time", System.currentTimeMillis()).toJobParameters();

			JobExecution jobExecution = jobLauncher.run(cvScannerJob, jobParameters);

			return ResponseEntity
					.ok("Proses başladı! Monitorinq üçün Job ID: " + jobExecution.getId() + " | Qovluq: " + targetPath);

		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Proses zamanı xəta baş verdi: " + e.getMessage());
		}
	}

	@GetMapping("/status/{jobId}")
	@Operation(summary = "İşləyən Batch Job-un statusunu və statistikasını yoxlayın")
	public ResponseEntity<String> getJobStatus(@PathVariable Long jobId) {
		JobExecution jobExecution = jobExplorer.getJobExecution(jobId);
		if (jobExecution == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok("Job Status: " + jobExecution.getStatus() + " | Başlama Vaxtı: "
				+ jobExecution.getStartTime() + " | Bitmə Vaxtı: " + jobExecution.getEndTime());
	}
}