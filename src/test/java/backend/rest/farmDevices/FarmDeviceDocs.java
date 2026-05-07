package backend.rest.farmDevices;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import backend.domain.FarmDevice;
import io.u2ware.common.docs.MockMvcRestDocs;

@Component
public class FarmDeviceDocs extends MockMvcRestDocs{

  public FarmDevice newEntity(String... entity){
    FarmDevice body = new FarmDevice();
    body.setFarmDeviceSerial(entity.length > 0 ? entity[0] : super.randomText("farmDeviceSerial"));
    body.setFarmDeviceState(entity.length > 1 ? entity[1] : super.randomText("farmDeviceState"));
    return body;
  }

  public Map<String, Object> setSearch(String keyword, String option){
    Map<String, Object> body = new HashMap<>();
    body.put("keyword", keyword);
    body.put("option", option);
    return body;
  }
  
}
