package com.prakash.CSVDemo.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import com.prakash.CSVDemo.entity.User;


public interface FileService {
    boolean hasCsvFormat(MultipartFile file);
    void processAndSaveData(MultipartFile file);   // Create
    List<User> readAllUsers();                     // Read
    void updateUsersFromCSV(MultipartFile file);   // Update
    void deleteUsersFromCSV(MultipartFile file);   // Delete
	void exportUsersToCSVFile(String filePath);
}
