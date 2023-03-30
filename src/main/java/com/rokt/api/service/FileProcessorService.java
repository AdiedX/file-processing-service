package com.rokt.api.service;

import com.rokt.api.model.FileEntry;
import com.rokt.api.model.FileProcessingRequest;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FileProcessorService {
    public List<FileEntry> processFile(FileProcessingRequest request, Path path) throws IOException, DateTimeException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(path)))
            .lines()
            .parallel()
            .filter(entry -> {
                Instant eventTime = Instant.parse(entry.split(" ")[0]);
                return (eventTime.equals(request.getFrom()) || eventTime.isAfter(request.getFrom()))
                    && (eventTime.equals(request.getTo()) || eventTime.isBefore(request.getTo()));
            })
            .map(entry -> {
                String[] splitEntry = entry.split(" ");
                return new FileEntry(Instant.parse(splitEntry[0]), splitEntry[1], splitEntry[2]);
            })
            .sorted(Comparator.comparing(FileEntry::getEventTime))
            .collect(Collectors.toList());
    }

    public FileProcessingRequest buildRequest(Map<String, String> request) {
        return new FileProcessingRequest(
            request.get("filename"),
            Instant.parse(request.get("from")),
            Instant.parse(request.get("to"))
        );
    }

    public boolean validateRequest(Map<String, String> request) {
        return !Objects.isNull(request.get("filename"))
            && !Objects.isNull(request.get("from"))
            && !Objects.isNull(request.get("to"));
    }
}
