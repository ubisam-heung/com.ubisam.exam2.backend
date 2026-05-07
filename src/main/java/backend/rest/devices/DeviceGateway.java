package backend.rest.devices;

public interface DeviceGateway {
  String sendAndReceive(String farmDeviceSerial, String commandValue);
}
