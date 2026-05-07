package backend.rest.farmDeviceTypes;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import backend.domain.FarmDeviceType;
import io.u2ware.common.docs.MockMvcRestDocs;

@Component
public class FarmDeviceTypeDocs extends MockMvcRestDocs{

  public FarmDeviceType newEntity(String... entity){
    FarmDeviceType body = new FarmDeviceType();
    body.setFarmDeviceTypeKey(entity.length > 0 ? entity[0] : super.randomText("farmDeviceTypeKey"));
    body.setFarmDeviceTypeName(entity.length > 1 ? entity[1] : super.randomText("farmDeviceTypeName"));
    return body;
  }

  public Map<String, Object> updateEntity(Map<String, Object> body, String entity){
    body.put("farmDeviceTypeName", entity);
    return body;
  }

  public Map<String, Object> setSearch(String keyword, String option){
    Map<String, Object> body = new HashMap<>();
    body.put("keyword", keyword);
    body.put("option", option);
    return body;
  }
  
}
