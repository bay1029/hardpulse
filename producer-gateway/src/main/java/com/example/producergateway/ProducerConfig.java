package com.example.producergateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;

@Configuration
class ProducerConfig {
  @Value("${app.socket.host}") String host;
  @Value("${app.socket.port}") Integer port;
  @Value("${app.topics.telemetryRaw}") String topic;

  @Bean
  ApplicationRunner runner(KafkaTemplate<String, Telemetry> kafka, ObjectMapper om) {
    return args -> TcpClient.create()
      .host(host).port(port)
      .handle((in,out) -> in.receive().asString()
        .flatMap(line -> Mono.fromCallable(() -> om.readValue(line, Telemetry.class)))
        .doOnNext(t -> kafka.send(topic, t.deviceId(), t))
        .then())
      .connect()
      .subscribe();
  }
}
