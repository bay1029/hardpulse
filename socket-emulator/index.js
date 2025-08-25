const net = require('net');
const PORT = process.env.EMULATOR_PORT ? parseInt(process.env.EMULATOR_PORT) : 7001;
const DEVICES = (process.env.DEVICES || "gpu-01,gpu-02,gpu-03,gpu-04").split(",");
function sample() {
  const d = DEVICES[Math.floor(Math.random()*DEVICES.length)];
  console.log(`sending...`)
  return JSON.stringify({
    schemaVersion: 1, deviceId: d, ts: new Date().toISOString(),
    metrics: { cpuTempC: 50 + Math.random()*50, gpuTempC: 45 + Math.random()*50,
      fanRpm: 1200 + Math.floor(Math.random()*1200), voltageV: 10 + Math.random()*3,
      cpuLoadPct: Math.random()*100, memUsedMb: 2000 + Math.floor(Math.random()*14000),
      diskUsedPct: Math.random()*100 },
    location: { site:"dc-1", rack:"r12", slot:"s03" }
  }) + "\n";
}
const server = net.createServer(socket => {
  const t = setInterval(()=> socket.write(sample()), 200);
  socket.on('end', ()=> clearInterval(t));
  socket.on('error', ()=> clearInterval(t));
});
server.listen(PORT, ()=> console.log(`Emulator listening on ${PORT}`));