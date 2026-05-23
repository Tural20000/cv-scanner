package com.turalabdullayev.cv_scanner.batch;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.Tika;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.turalabdullayev.cv_scanner.model.Candidate;

@Component
public class CvItemProcessor implements ItemProcessor<File, Candidate> {

	private final Tika tika = new Tika();

	@Override
	public Candidate process(File file) throws Exception {
		String rawText = tika.parseToString(file);

		String fullName = extractFullName(file.getName(), rawText);
		int experience = extractExperience(rawText);
		String skills = extractSkills(rawText);
		String location = extractLocation(rawText);

		return Candidate.builder().fullName(fullName).yearsOfExperience(experience).technicalSkills(skills)
				.location(location).fileName(file.getName()).build();
	}

	private String extractFullName(String fileName, String text) {
		String cleanName = fileName.replaceAll("(?i)\\.(pdf|docx|doc|txt)", "").replace("_", " ").replace("-", " ");

		if (cleanName.equalsIgnoreCase("cv") || cleanName.length() < 3) {
			String[] lines = text.split("\\r?\\n");
			for (String line : lines) {
				if (!line.trim().isEmpty() && line.trim().length() > 3) {
					return line.trim();
				}
			}
		}
		return cleanName;
	}

	private int extractExperience(String text) {
		Pattern pattern = Pattern.compile("(\\d+)\\s*(?:years?|il|gəl|experience|təcrübə)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			try {
				return Integer.parseInt(matcher.group(1));
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
	}

	private String extractSkills(String text) {
		StringBuilder foundSkills = new StringBuilder();
		String lowerText = text.toLowerCase();

		String[] skillKeywords = { "Java", "Spring Boot", "Spring", "SQL", "MySQL", "PostgreSQL", "Hibernate", "Docker",
				"Git", "JavaScript", "React", "Python" };

		for (String skill : skillKeywords) {
			if (lowerText.contains(skill.toLowerCase())) {
				if (foundSkills.length() > 0) {
					foundSkills.append(", ");
				}
				foundSkills.append(skill);
			}
		}
		return foundSkills.length() > 0 ? foundSkills.toString() : "Təyin edilmədi";
	}

	private String extractLocation(String text) {
		String lowerText = text.toLowerCase();

		if (lowerText.contains("remote") || lowerText.contains("məsafədən")) {
			return "Remote";
		} else if (lowerText.contains("baku") || lowerText.contains("bakı")) {
			return "Baku";
		} else if (lowerText.contains("germany") || lowerText.contains("almaniya")) {
			return "Germany";
		}
		return "Bilinmir";
	}
}