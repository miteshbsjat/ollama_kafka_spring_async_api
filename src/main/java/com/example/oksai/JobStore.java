package com.example.oksai;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JobStore {
    private final ConcurrentHashMap<String, JobDetails> store = new ConcurrentHashMap<>();

    public void save(String jobId, JobDetails details) {
        store.put(jobId, details);
    }

    public JobDetails get(String jobId) {
        return store.get(jobId);
    }
}
