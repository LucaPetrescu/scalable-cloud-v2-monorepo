package com.masterthesis.metricscollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class MetricsCollectorApplication {

	public static void main(String[] args) {

		try{

			String zookeeperStartCommand = "cmd /c start cmd.exe /K \"C:\\kafka\\bin\\windows\\zookeeper-server-start.bat C:\\kafka\\config\\zookeeper.properties\"";
			String kafkaStartCommand = "cmd /c start cmd.exe /K \"C:\\kafka\\bin\\windows\\kafka-server-start.bat C:\\kafka\\config\\server.properties\"";
			System.out.println("Starting Zookeeper...");
			ProcessBuilder zookeeperProcess = new ProcessBuilder("cmd.exe", "/c", zookeeperStartCommand);
			zookeeperProcess.start();

			// Wait for a few seconds to ensure Zookeeper has time to start
			Thread.sleep(5000);  // 10 seconds delay

			// Start Kafka
			System.out.println("Starting Kafka...");
			ProcessBuilder kafkaProcess = new ProcessBuilder("cmd.exe", "/c", kafkaStartCommand);
			kafkaProcess.start();

			System.out.println("Zookeeper and Kafka have been started.");

		}catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		SpringApplication.run(MetricsCollectorApplication.class, args);

    }

}
