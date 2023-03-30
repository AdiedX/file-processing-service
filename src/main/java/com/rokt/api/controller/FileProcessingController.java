package com.rokt.api.controller;

import com.rokt.api.model.FileEntry;
import com.rokt.api.model.FileProcessingRequest;
import com.rokt.api.service.FileProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class FileProcessingController {
    private static final Logger logger = Logger.getLogger(FileProcessingController.class.getName());
    private static final String APPLICATION_FILES_PATH = "/app/test-files/";
    private static final String TESTING_FILES_PATH = "src/main/resources/";
    private final FileProcessorService fileProcessorService;

    @Autowired
    public FileProcessingController(FileProcessorService fileProcessorService) {
        this.fileProcessorService = fileProcessorService;
    }

    @PostMapping("/")
    public List<FileEntry> fileProcessingController(@RequestBody Map<String, String> incomingRequest) {
        long startTime = System.currentTimeMillis();
        List<FileEntry> results = new ArrayList<>();

        try {
            if (!fileProcessorService.validateRequest(incomingRequest)) {
                return results;
            }

            FileProcessingRequest fileProcessingRequest = fileProcessorService.buildRequest(incomingRequest);
            String extension = fileProcessingRequest.getFilename().split("\\.")[1];

            if (!extension.equals("txt")) {
                return results;
            }

            if (fileProcessingRequest.getFrom().isAfter(fileProcessingRequest.getTo())) {
                return results;
            }

            String pathToFiles = APPLICATION_FILES_PATH;

            if (System.getProperty("testing") != null) {
                pathToFiles = TESTING_FILES_PATH;
            }

            Path path = Paths.get(pathToFiles + fileProcessingRequest.getFilename());
            logger.log(Level.INFO, "File path: " + path);

            if (!Files.exists(path)) {
                return results;
            }

            logger.info("Processing file " + fileProcessingRequest.getFilename());
            results = fileProcessorService.processFile(fileProcessingRequest, path);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to read from buffer: ", e);
        } catch (DateTimeException e) {
            logger.log(Level.SEVERE, "Unable to parse timestamp: ", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Internal error when processing file: ", e);
        }

        long endTime = System.currentTimeMillis();
        logger.info("Total time: " + (endTime - startTime) + " ms");
        return results;
    }
}
