package backend.stomp.devices;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class DeviceProperties {

  protected String destination = "device";
  protected String messageKey = "value";
  protected String receivedMessageKey = "Received Message";
  protected String completedMessage = "통신완료";
  
}
