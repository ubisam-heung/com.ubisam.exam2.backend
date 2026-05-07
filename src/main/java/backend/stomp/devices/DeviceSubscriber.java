package backend.stomp.devices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import backend.domain.Device;
import backend.domain.FarmDevice;
import backend.rest.devices.DeviceGateway;
import backend.rest.devices.DeviceRepository;
import backend.rest.farmDevices.FarmDeviceRepository;
import io.u2ware.common.stomp.client.WebsocketStompClient;
import io.u2ware.common.stomp.client.WebsocketStompClientHandler;
import io.u2ware.common.stomp.client.config.WebsocketStompProperties;

@Component
public class DeviceSubscriber implements WebsocketStompClientHandler{

  protected Log logger = LogFactory.getLog(getClass());

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private DeviceRepository deviceRepository;

  @Autowired
  private FarmDeviceRepository farmDeviceRepository;

  @Autowired
  private DeviceGateway deviceGateway;
  
  @Autowired
  private DeviceProperties deviceProperties;

  @Autowired
  private WebsocketStompProperties websocketStompProperties;

  @Override
  public String getDestination(){
    return websocketStompProperties.getSubscriptions().get(deviceProperties.getDestination());
  } 

  @Override
  @Transactional
  public void handleFrame(WebsocketStompClient client, JsonNode message){
    logger.info("Received: "+ message);

    ObjectNode ack = mapper.createObjectNode();
    String appDestination = "/app/" + deviceProperties.getDestination();
    String receviedMessageKey = deviceProperties.getReceivedMessageKey();

    try{
      JsonNode payloadNode = message.path("payload");

      if (!payloadNode.isMissingNode() && !payloadNode.isNull() && receviedMessageKey != null && payloadNode.has(receviedMessageKey)){
        return;
      }

      long timestamp = message.path("timestamp").asLong(System.currentTimeMillis());
      String principal = message.path("principal").asText();
      String formattedTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));

      String farmDeviceSerial = payloadNode.path("farmDeviceSerial").asText(null);
      String commandValue = payloadNode.path(deviceProperties.getMessageKey()).asText(null);

      Device d = new Device();
      d.setPrincipal(principal);
      d.setState("PENDING");
      d.setTimestamp(timestamp);
      deviceRepository.save(d);

      if(payloadNode.isMissingNode() || payloadNode.isNull()){
        d.setState("INVALID_PAYLOAD");
        deviceRepository.save(d);
        if(receviedMessageKey != null){
          ack.put(receviedMessageKey, deviceProperties.getCompletedMessage());
        }
        ack.put("status", "INVALID_PAYLOAD");
        client.send(appDestination, ack);
        return;
      }

      if(farmDeviceSerial == null || farmDeviceSerial.isBlank() || commandValue == null || commandValue.isBlank()){
        d.setState("MISSING_REQUIRED_FIELDS");
        deviceRepository.save(d);
        if(receviedMessageKey != null){
          ack.put(receviedMessageKey, deviceProperties.getCompletedMessage());
        }
        ack.put("status", "MISSING_REQUIRED_FIELDS");
        ack.put("required", "farmDeviceSerial, "+ deviceProperties.getMessageKey());
        client.send(appDestination, ack);
        return;
      }

      List<FarmDevice> farmDevices = farmDeviceRepository.findByFarmDeviceSerial(farmDeviceSerial);

      if(farmDevices == null || farmDevices.isEmpty()){
        d.setState("DEVICE_NOT_FOUND");
        deviceRepository.save(d);
        if(receviedMessageKey != null){
          ack.put(receviedMessageKey, deviceProperties.getCompletedMessage());
        }
        ack.put("status", "DEVICE_NOT_FOUND");
        ack.put("farmDevicePrincipal", principal);
        ack.put("farmDeviceSerial", farmDeviceSerial);
        client.send(appDestination, ack);
        return;
      }

      String responseValue = deviceGateway.sendAndReceive(farmDeviceSerial, commandValue);
      String stateToSave = (responseValue != null && !responseValue.isBlank()) ? responseValue : "NO_REPSONSE";

      d.setState(stateToSave);
      deviceRepository.save(d);

      for(FarmDevice farmDevice : farmDevices){
        farmDevice.setFarmDeviceState(stateToSave);
        if(farmDevice.getFarmDeviceHistory() == null){
          farmDevice.setFarmDeviceHistory(new HashSet<>());
        }
        FarmDevice.FarmDeviceHistory history = new FarmDevice.FarmDeviceHistory();
        history.setFarmDeviceName(farmDeviceSerial);
        history.setFarmDeviceState(stateToSave);
        history.setFarmDeviceTimestamp(formattedTimestamp);
        farmDevice.getFarmDeviceHistory().add(history);
      }
      farmDeviceRepository.saveAll(farmDevices);

      if(responseValue == null || responseValue.isBlank()){
        if(receviedMessageKey != null){
          ack.put(receviedMessageKey, deviceProperties.getCompletedMessage());
        }
        ack.put("status", "NO_RESPONSE");
        ack.put("farmDevicePrincipal", principal);
        ack.put("farmDeviceSerial", farmDeviceSerial);
        client.send(appDestination, ack);
        return;
      }

      if(receviedMessageKey != null){
        ack.put(receviedMessageKey, deviceProperties.getCompletedMessage());
      }
      ack.put("status", "OK");
      ack.put("farmDevicePrincipal", principal);
      ack.put("farmDeviceSerial", farmDeviceSerial);
      ack.put("state", responseValue);
      client.send(appDestination, ack);
    }catch(Exception ex){
      logger.error("DeviceSubscriber Error: " + ex);
      ack.put("status", "ERROR");
      ack.put("message", ex.getMessage() == null ? "UNKNOWN_ERROR" : ex.getMessage());
      client.send(appDestination, ack);
    }
  }
}
