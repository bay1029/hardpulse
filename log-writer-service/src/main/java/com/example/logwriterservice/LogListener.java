package com.example.logwriterservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class LogListener {
  @KafkaListener(topics = "${app.topics.in}", groupId = "log-writer-service-v1")
  public void onMessage(Telemetry t) {
    System.out.printf("LOG: %s %s CPU=%.2f GPU=%.2f LOAD=%.2f DISK=%.2f%n",
      t.ts(), t.deviceId(),
      t.metrics().cpuTempC(), t.metrics().gpuTempC(),
      t.metrics().cpuLoadPct(), t.metrics().diskUsedPct());
  }
}
