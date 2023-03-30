package com.rokt.api.model;

import java.time.Instant;

public class FileProcessingRequest {
    private String filename;
    private Instant from;
    private Instant to;

    public FileProcessingRequest(String filename, Instant from, Instant to) {
        this.filename = filename;
        this.from = from;
        this.to = to;
    }

    public FileProcessingRequest() {}

    public String getFilename() {
        return filename;
    }

    public Instant getFrom() {
        return from;
    }

    public Instant getTo() {
        return to;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFrom(Instant from) {
        this.from = from;
    }

    public void setTo(Instant to) {
        this.to = to;
    }
}
