package com.turalabdullayev.cv_scanner.controller;

import java.io.IOException;

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
@Tag(name = "CV Upload Controller", description = "CV qovluqlarini qebul eden ve emal prosesini basladan API")

public class CvUploadController {
	private final FileStorageService fileStorageService;

	public CvUploadController(FileStorageService fileStorageService) {
		this.fileStorageService = fileStorageService;
	}

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Zip formatinda CV toplu qovluqunu yukleyin", description = "Bu API gelen zip faylini acir ve muveqqeti qovluqa yerlesdirir.")
	public ResponseEntity<String> uploadCvZip(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("Xahis olunur bos olmayan bir fayl yukleyin!");

		}

		try {
			String targetPath = fileStorageService.unzipCvFolder(file);
			return ResponseEntity.ok("Fayl ugurla qebul edildi ve cixarildi! Qovluq: " + targetPath);

		} catch (IOException e) {
			return ResponseEntity.internalServerError().body("Fayl cixarilarken xeta bas verdi: " + e.getMessage());

		}
	}

}
