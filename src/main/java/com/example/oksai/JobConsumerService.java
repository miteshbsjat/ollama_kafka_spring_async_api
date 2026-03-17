package com.example.oksai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class JobConsumerService {

    private final ChatClient chatClient;
    private final JobStore jobStore;

    public JobConsumerService(ChatClient.Builder chatClientBuilder, JobStore jobStore) {
        this.chatClient = chatClientBuilder.build();
        this.jobStore = jobStore;
    }

    @KafkaListener(topics = "llm-jobs-topic", groupId = "ollama-job-group")
    public void consumeJobRequest(LlmJobMessage message) {
        String jobId = message.jobId();

        try {
            // Update status to processing
            jobStore.save(jobId, new JobDetails("PROCESSING", message.prompt(), null));

            // Execute the long-running LLM call
            String response = chatClient.prompt()
                    .user(message.prompt())
                    .call()
                    .content();

            // Update status to completed with the result
            jobStore.save(jobId, new JobDetails("COMPLETED", message.prompt(), response));

        } catch (Exception e) {
            // Handle failures (e.g., model timeout, context window limits)
            jobStore.save(jobId, new JobDetails("FAILED", message.prompt(), e.getMessage()));
        }
    }
}