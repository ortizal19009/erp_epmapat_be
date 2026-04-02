package com.epmapat.erp_epmapat.servicio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AbonadoFotoStorageService {

	@Value("${abonados.fotos.base-path:}")
	private String fotoBasePath;

	public String saveImageFile(Long idabonado, MultipartFile file, String subfolder) throws IOException {
		if (file == null || file.isEmpty()) {
			return null;
		}

		Path dir = resolveStorageDir(subfolder);
		Files.createDirectories(dir);

		String generatedFileName = randomFileName(idabonado, file.getOriginalFilename());
		Path target = dir.resolve(generatedFileName);
		Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
		return target.toString();
	}

	private Path resolveStorageDir(String subfolder) {
		LocalDate now = LocalDate.now();
		return Paths.get(resolveBasePath(), subfolder, String.valueOf(now.getYear()), String.format("%02d", now.getMonthValue()));
	}

	private String resolveBasePath() {
		if (StringUtils.hasText(fotoBasePath)) {
			return fotoBasePath;
		}

		String os = System.getProperty("os.name", "").toLowerCase();
		if (os.contains("win")) {
			return "C:/ERP/nube-local/abonados";
		}

		return "/opt/epmapat/fotos";
	}

	private String randomFileName(Long id, String originalName) {
		String ext = "";
		if (originalName != null && originalName.contains(".")) {
			ext = originalName.substring(originalName.lastIndexOf('.') + 1);
		}
		String timestamp = String.valueOf(Instant.now().getEpochSecond());
		return (id != null ? id + "_" : "") + timestamp + (ext.isEmpty() ? "" : "." + ext);
	}
}
