package com.example.logwriterservice;
public record Telemetry(
  Integer schemaVersion,
  String deviceId,
  String ts,
  Metrics metrics
) {}
record Metrics(
  Double cpuTempC,
  Double gpuTempC,
  Integer fanRpm,
  Double voltageV,
  Double cpuLoadPct,
  Integer memUsedMb,
  Double diskUsedPct
) {}
