package com.prakash.CSVDemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prakash.CSVDemo.entity.User;
import com.prakash.CSVDemo.response.ResponseMessage;
import com.prakash.CSVDemo.service.FileService;



/*--------------I have tested all CRUD operations using Postman, with the CSV file uploaded 
via form-data for each operation. This includes file uploads, updates, 
and deletions, where I selected the CSV file in form-data to handle the 
operations seamlessly  --------------------------------*/



@RestController
@RequestMapping("/files")
public class FileController {
    
    @Autowired
    private FileService fileService;

    // CREATE
    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        if (fileService.hasCsvFormat(file)) {
            fileService.processAndSaveData(file);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Uploaded the file successfully: " + file.getOriginalFilename()));
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Please upload a CSV file"));
    }
    
    // UPDATE
    @PutMapping("/update")
    public ResponseEntity<String> updateUsers(@RequestParam("file") MultipartFile file) {
        if (fileService.hasCsvFormat(file)) {
            fileService.updateUsersFromCSV(file);
            return ResponseEntity.status(HttpStatus.OK).body("Users updated successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a CSV file!");
    }

    // DELETE
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUsers(@RequestParam("file") MultipartFile file) {
        if (fileService.hasCsvFormat(file)) {
            fileService.deleteUsersFromCSV(file);
            return ResponseEntity.status(HttpStatus.OK).body("Users deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a CSV file!");
    }
    
    
    // READ  -----> get All data in postman from database
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return fileService.readAllUsers();
    }
    
    // READ ---->  Transfer the data in CSV file from Database
    @PostMapping("/export-to-file")
    public ResponseEntity<String> exportToCSVFile(@RequestParam("filePath") String filePath) {
        try {
            fileService.exportUsersToCSVFile(filePath);  // Export data to the specified file
            return ResponseEntity.status(HttpStatus.OK).body("Data successfully written to " + filePath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error writing to file: " + e.getMessage());
        }
    }
    
}
