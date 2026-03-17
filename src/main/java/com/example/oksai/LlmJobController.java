package com.example.oksai;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/llm-jobs")
public class LlmJobController {

    private final JobProducerService producerService;
    private final JobStore jobStore;

    public LlmJobController(JobProducerService producerService, JobStore jobStore) {
        this.producerService = producerService;
        this.jobStore = jobStore;
    }

    // Submit a new job
    @PostMapping
    public ResponseEntity<JobSubmissionResponse> submitJob(@RequestBody JobRequest request) {
        String jobId = UUID.randomUUID().toString();

        // Register job as pending
        jobStore.save(jobId, new JobDetails("PENDING", request.prompt(), null));

        // Push to Kafka
        producerService.sendJobRequest(new LlmJobMessage(jobId, request.prompt()));

        return ResponseEntity.accepted().body(new JobSubmissionResponse(jobId, "Job submitted successfully"));
    }

    // Check job status/result
    @GetMapping("/{jobId}")
    public ResponseEntity<JobDetails> getJobStatus(@PathVariable String jobId) {
        JobDetails details = jobStore.get(jobId);
        if (details == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }
}

// Request/Response DTOs
record JobRequest(String prompt) {
}

record JobSubmissionResponse(String jobId, String message) {
}