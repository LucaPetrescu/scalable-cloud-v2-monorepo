package com.masterthesis.metricscollector.kafka.listeners;

import io.prometheus.client.Gauge;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;

public class KafkaSystemMetricsListener {

    private static final Gauge cpuMetrics = Gauge.build()
            .name("cpu_usage_percent")
            .help("CPU usage percentage")
            .register();

    private static final Gauge ramMetrics = Gauge.build()
            .name("ram_usage_percent")
            .help("RAM usage in bytes")
            .register();

    @KafkaListener(topics="auth-service-topic", topicPartitions = @TopicPartition(topic="auth-service-topic", partitions = {"0"}))
    public void listenCpuMetrics(String record){
        System.out.println("Message for CPU Usage " + record);
    }

//    @KafkaListener(topicPartitions = @TopicPartition(topic="auth-service-topic", partitions = {"1"}))
//    public void listenRamMetrics(ConsumerRecord<String, String> record){
//        System.out.println("Message for RAM Usage " + record.value());
//    }

}
