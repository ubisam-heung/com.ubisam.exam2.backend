package backend.rest.devices;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "farm.device.gateway", havingValue = "mock")
public class DeviceGatewayMock implements DeviceGateway{

  protected Log logger = LogFactory.getLog(getClass());

  @Override
  public String sendAndReceive(String farmDeviceSerial, String commandValue){
    logger.info("Mock :" + farmDeviceSerial + "commandValue: "+ commandValue);

    // 예시로 고정값을 반환 (장치에서 반환하는 값)
    String mockResponse = "01 00 11 00 AA";
    logger.info("MockResponse: "+ mockResponse);
    return mockResponse;
  }
  
}
