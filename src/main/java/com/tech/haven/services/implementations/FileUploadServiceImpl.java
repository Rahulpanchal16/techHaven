package com.tech.haven.services.implementations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tech.haven.exceptions.BadApiRequestException;
import com.tech.haven.services.FileUploadService;

@Service
public class FileUploadServiceImpl implements FileUploadService {

	// private static final Logger logger =
	// LoggerFactory.getLogger(FileUploadServiceImpl.class);

	@Override
	public String imageUpload(MultipartFile file, String path) throws IOException {

		// take the extension from original file name and append it to a randomly
		// generated file name to avoid clashes in file names
		String originalFilename = file.getOriginalFilename();
		@SuppressWarnings("null")
		String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
		String randomName = UUID.randomUUID().toString();
		String randomFilename = randomName + fileExtension;
		String fullPathWithFilename = path + File.separator + randomFilename;

		long fileSize = file.getSize();
		System.out.println(fileSize + " bytes");

		// checking for image format(.png,.jpg,.jpeg allowed)
		if ((fileExtension.equalsIgnoreCase(".png") || fileExtension.equalsIgnoreCase(".jpg")
				|| fileExtension.equalsIgnoreCase(".jpeg")) && (fileSize < 512000)) {
			// save the file

			// creating the folder if it does not exist
			File folder = new File(path);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			Files.copy(file.getInputStream(), Paths.get(fullPathWithFilename));

			return randomFilename;

		} else {
			throw new BadApiRequestException(
					"file format: " + fileExtension + " not allowed & file must be less than 500KB");
		}

	}

	@Override
	public InputStream getResource(String filePath, String fileName) throws FileNotFoundException {
		String fullFileDestination = filePath + fileName;
		InputStream inputStream = new FileInputStream(fullFileDestination);
		return inputStream;
	}

}
