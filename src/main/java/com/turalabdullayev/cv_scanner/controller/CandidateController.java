package com.turalabdullayev.cv_scanner.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turalabdullayev.cv_scanner.model.Candidate;
import com.turalabdullayev.cv_scanner.repository.CandidateRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/candidates")
@Tag(name = "Candidate Controller", description = "Namizədlərin filterlənməsi və Excel formatında ixracı")
public class CandidateController {

	private final CandidateRepository candidateRepository;

	public CandidateController(CandidateRepository candidateRepository) {
		this.candidateRepository = candidateRepository;
	}

	@GetMapping
	@Operation(summary = "Bütün namizədlərin siyahısını gətir")
	public ResponseEntity<List<Candidate>> getAllCandidates() {
		return ResponseEntity.ok(candidateRepository.findAll());
	}

	@GetMapping("/search")
	@Operation(summary = "Namizədləri bacarıq, təcrübə və məkana görə filterlə")
	public ResponseEntity<List<Candidate>> searchCandidates(
			@RequestParam(value = "skills", defaultValue = "") String skills,
			@RequestParam(value = "minExperience", defaultValue = "0") int minExperience,
			@RequestParam(value = "location", defaultValue = "") String location) {

		List<Candidate> filteredCandidates = candidateRepository
				.findByTechnicalSkillsContainingIgnoreCaseAndYearsOfExperienceGreaterThanEqualAndLocationContainingIgnoreCase(
						skills, minExperience, location);

		return ResponseEntity.ok(filteredCandidates);
	}

	@GetMapping("/export")
	@Operation(summary = "Filterlənmiş namizədləri Excel (.xlsx) olaraq yüklə")
	public ResponseEntity<InputStreamResource> exportToExcel(
			@RequestParam(value = "skills", defaultValue = "") String skills,
			@RequestParam(value = "minExperience", defaultValue = "0") int minExperience,
			@RequestParam(value = "location", defaultValue = "") String location) throws IOException {

		List<Candidate> candidates = candidateRepository
				.findByTechnicalSkillsContainingIgnoreCaseAndYearsOfExperienceGreaterThanEqualAndLocationContainingIgnoreCase(
						skills, minExperience, location);

		String[] columns = { "ID", "Tam Adı", "Təcrübə (İl)", "Texniki Bacarıqlar", "Məkan", "Fayl Adı" };

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Namizədlər");

			// Header dizaynı
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.WHITE.getIndex());

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			headerCellStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			Row headerRow = sheet.createRow(0);
			for (int col = 0; col < columns.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(columns[col]);
				cell.setCellStyle(headerCellStyle);
			}

			int rowIdx = 1;
			for (Candidate candidate : candidates) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(candidate.getId());
				row.createCell(1).setCellValue(candidate.getFullName());
				row.createCell(2).setCellValue(candidate.getYearsOfExperience());
				row.createCell(3).setCellValue(candidate.getTechnicalSkills());
				row.createCell(4).setCellValue(candidate.getLocation());
				row.createCell(5).setCellValue(candidate.getFileName());
			}

			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}

			workbook.write(out);
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=namizedler_hesabat.xlsx");

			return ResponseEntity.ok().headers(headers)
					.contentType(MediaType
							.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
					.body(new InputStreamResource(in));
		}
	}
}