package com.masterthesis.metricscollector.kafka.listeners;

import io.prometheus.client.Gauge;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

@Service
public class KafkaNetworkMetricsListener {

    private static final Gauge httpRequestCount = Gauge.build()
            .name("http_requests_total")
            .help("Total number of HTTP requests")
            .register();

    private static final Gauge httpRequestDuration = Gauge.build()
            .name("http_request_duration_seconds")
            .help("HTTP request duration in seconds")
            .register();


    @KafkaListener(topicPartitions = @TopicPartition(topic = "auth-service-topic", partitions = {"2"}))
    public void listenRequestCount(ConsumerRecord<String, String> record){
        double count = Double.parseDouble(record.value());
        httpRequestCount.set(count);
        System.out.println("Message for Request Count " + record.value());
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "auth-service-topic", partitions = {"3"}))
    public void listenRequestDuration(ConsumerRecord<String, String> record){
        double duration = Double.parseDouble(record.value());
        httpRequestDuration.set(duration);
        System.out.println("Message for Request Duration " + record.value());
    }

}
