package com.rokt.api.model;

import java.time.Instant;

public class FileEntry {
    private Instant eventTime;
    private String email;
    private String sessionId;

    public FileEntry(Instant eventTime, String email, String sessionId) {
        this.eventTime = eventTime;
        this.email = email;
        this.sessionId = sessionId;
    }

    public FileEntry() {}

    public Instant getEventTime() {
        return eventTime;
    }

    public String getEmail() {
        return email;
    }

    public String getSessionId() {
        return sessionId;
    }
}
