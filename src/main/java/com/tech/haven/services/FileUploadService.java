package com.tech.haven.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;


public interface FileUploadService {

	public String imageUpload(MultipartFile file, String path) throws IOException;

	public InputStream getResource(String filePath, String fileName) throws FileNotFoundException;

}
