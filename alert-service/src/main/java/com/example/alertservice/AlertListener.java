package com.example.alertservice;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

record Alert(String deviceId, String metric, double value, String severity, String threshold) {}

@Component
class AlertListener {
  @Value("${app.thresholds.cpuTempC}") double cpuTemp;
  @Value("${app.thresholds.gpuTempC}") double gpuTemp;
  @Value("${app.thresholds.cpuLoadPct}") double cpuLoad;
  @Value("${app.thresholds.diskUsedPct}") double diskUsed;
  @Value("${app.thresholds.voltageMin}") double voltageMin;
  @Value("${app.topics.alerts}") String outTopic;
  private final KafkaTemplate<String, Object> kafka;
  AlertListener(KafkaTemplate<String, Object> kafka) { this.kafka = kafka; }

  @KafkaListener(topics = "${app.topics.in}", groupId = "alert-service-v1")
  public void onMessage(ConsumerRecord<String, Telemetry> rec) {
    Telemetry t = rec.value();
    List<Alert> alerts = new ArrayList<>();
    if (t.metrics().cpuTempC() != null && t.metrics().cpuTempC() > cpuTemp) alerts.add(new Alert(t.deviceId(), "cpuTempC", t.metrics().cpuTempC(), "HIGH", ">" + cpuTemp));
    if (t.metrics().gpuTempC() != null && t.metrics().gpuTempC() > gpuTemp) alerts.add(new Alert(t.deviceId(), "gpuTempC", t.metrics().gpuTempC(), "HIGH", ">" + gpuTemp));
    if (t.metrics().cpuLoadPct() != null && t.metrics().cpuLoadPct() > cpuLoad) alerts.add(new Alert(t.deviceId(), "cpuLoadPct", t.metrics().cpuLoadPct(), "MEDIUM", ">" + cpuLoad));
    if (t.metrics().diskUsedPct() != null && t.metrics().diskUsedPct() > diskUsed) alerts.add(new Alert(t.deviceId(), "diskUsedPct", t.metrics().diskUsedPct(), "MEDIUM", ">" + diskUsed));
    if (t.metrics().voltageV() != null && t.metrics().voltageV() < voltageMin) alerts.add(new Alert(t.deviceId(), "voltageV", t.metrics().voltageV(), "HIGH", "<" + voltageMin));
    for (Alert a: alerts) {
      kafka.send(outTopic, t.deviceId(), a);
      System.out.printf("EMAIL[%s]: %s %s=%s threshold %s%n", a.severity(), t.deviceId(), a.metric(), a.value(), a.threshold());
    }
  }
}
