package com.example.producergateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

@Configuration
class ProducerConfig {
  @Value("${app.socket.host}") String host;
  @Value("${app.socket.port}") Integer port;
  @Value("${app.topics.telemetryRaw}") String topic;

  @Bean
  ApplicationRunner runner(KafkaTemplate<String, Telemetry> kafka, ObjectMapper om) {
    return args -> {
      try (Socket socket = new Socket(host, port);
           BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

        String line;
        while ((line = reader.readLine()) != null) {
          try {
            Telemetry telemetry = om.readValue(line, Telemetry.class);
            kafka.send(topic, telemetry.deviceId(), telemetry);
          } catch (Exception e) {
            // Log error and continue processing next line
            System.err.println("Error processing telemetry: " + e.getMessage());
          }
        }
      } catch (Exception e) {
        // Log connection or reading error
        System.err.println("Connection error: " + e.getMessage());
      }
    };
  }
}
