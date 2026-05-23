package com.turalabdullayev.cv_scanner.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
	private final Path rootStorageLocation = Paths.get("temp-cvs").toAbsolutePath().normalize();

	public String unzipCvFolder(MultipartFile zipFile) throws IOException {
		String uploadId = UUID.randomUUID().toString();
		Path targetDirPath = this.rootStorageLocation.resolve(uploadId);
		Files.createDirectories(targetDirPath);

		try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
			ZipEntry zipEntry = zis.getNextEntry();

			while (zipEntry != null) {
				String fileName = zipEntry.getName();

				if (!zipEntry.isDirectory() && !fileName.startsWith("__MACOSX") && !fileName.contains(".DS_Store")) {
					File newFile = new File(targetDirPath.toFile(), new File(fileName).getName());
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
		return targetDirPath.toString();
	}
}