package com.sentiment.anlaysis.demo.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sentiment.anlaysis.demo.Database.FileDatabase;
import com.sentiment.anlaysis.demo.Module.FileModule;

@Service
public class FileService {
    @Autowired
	FileDatabase fileDatabase;
	
	private String URI = "D://Upload//UserFiles//";
	
	FileModule fileModule;
	
	public FileModule UploadFile(MultipartFile file, Long user_id) throws IOException {
		File dirFile = new File(URI);
		
		if(!dirFile.exists()) {
			dirFile.mkdir();
		}
		
		String name = file.getOriginalFilename() + System.currentTimeMillis();
		String path = URI + name;
		String type = file.getContentType();
		Files.copy(file.getInputStream(), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
		fileModule = new FileModule(user_id, path, name, type);
		
		return fileDatabase.save(fileModule);
	}
	
	public String filePath() {
		return fileModule.getPath();
	}
}
