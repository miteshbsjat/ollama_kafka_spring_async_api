package com.example.oksai;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class JobProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "llm-jobs-topic";

    public JobProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendJobRequest(LlmJobMessage message) {
        kafkaTemplate.send(TOPIC, message.jobId(), message);
    }
}