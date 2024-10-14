package com.prakash.CSVDemo.serviceImpl;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.prakash.CSVDemo.entity.User;
import com.prakash.CSVDemo.repository.UserRepository;
import com.prakash.CSVDemo.service.FileService;


/*--------------I have tested all CRUD operations using Postman, with the CSV file uploaded 
via form-data for each operation. This includes file uploads, updates, 
and deletions, where I selected the CSV file in form-data to handle the 
operations seamlessly  --------------------------------*/



@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean hasCsvFormat(MultipartFile file) {
        return "text/csv".equals(file.getContentType());
    }

    @Override
    public void processAndSaveData(MultipartFile file) {
        try {
            List<User> users = csvToUsers(file.getInputStream());
            userRepository.saveAll(users); // Create
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> readAllUsers() {
        return userRepository.findAll(); // Read
    }

    @Override
    public void updateUsersFromCSV(MultipartFile file) {
        try {
            List<User> users = csvToUsers(file.getInputStream());
            for (User csvUser : users) {
                User userFromDb = userRepository.findById(csvUser.getId()).orElse(null);
                if (userFromDb != null) {
                    userFromDb.setFirstName(csvUser.getFirstName());
                    userFromDb.setLastName(csvUser.getLastName());
                    userRepository.save(userFromDb); // Update
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUsersFromCSV(MultipartFile file) {
        try {
            List<User> users = csvToUsers(file.getInputStream());
            for (User csvUser : users) {
                userRepository.deleteById(csvUser.getId()); // Delete
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<User> csvToUsers(InputStream inputStream) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            // Skip BOM if present
            String firstLine = fileReader.readLine();
            if (firstLine != null && firstLine.startsWith("\uFEFF")) {
                firstLine = firstLine.substring(1); // Remove BOM character
            }

            // Parse the file content
            CSVParser csvParser = CSVParser.parse(firstLine + "\n" + fileReader.lines().reduce("", (acc, line) -> acc + "\n" + line),
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

            List<User> users = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser) {
                User user = new User(Long.parseLong(csvRecord.get("Id")), csvRecord.get("FirstName"), csvRecord.get("LastName"));
                users.add(user);
            }

            return users;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    
    @Override
    public void exportUsersToCSVFile(String filePath) {
        List<User> users = userRepository.findAll();

        try (FileWriter fileWriter = new FileWriter(filePath , true);  // 'true' to append data to the existing file
             CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader("Id", "FirstName", "LastName"))) {

            for (User user : users) {
                csvPrinter.printRecord(user.getId(), user.getFirstName(), user.getLastName());
            }

            csvPrinter.flush();
            System.out.println("Data successfully written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

}
