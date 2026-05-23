package com.turalabdullayev.cv_scanner.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service

public class FileStorageService {
	private final Path tempStorageLocation = Paths.get("temp-cvs").toAbsolutePath().normalize();

	public String unzipCvFolder(MultipartFile zipFile) throws IOException {
		clearTempDirectory();
		Files.createDirectories(this.tempStorageLocation);

		try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
			ZipEntry zipEntry = zis.getNextEntry();

			while (zipEntry != null) {
				String fileName = zipEntry.getName();

				if (!zipEntry.isDirectory() && !fileName.startsWith("__MACOSX") && !fileName.contains(".DS_Store")) {
					File newFile = new File(this.tempStorageLocation.toFile(), new File(fileName).getName());
					try (FileOutputStream fos = new FileOutputStream(newFile)) {
						byte[] buffer = new byte[1024];
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);

						}
					}
				}
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
		}
		return this.tempStorageLocation.toString();

	}

	private void clearTempDirectory() {
		File dir = this.tempStorageLocation.toFile();
		if (dir.exists()) {
			File[] files = dir.listFiles();
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
			dir.delete();
		}
	}

}
