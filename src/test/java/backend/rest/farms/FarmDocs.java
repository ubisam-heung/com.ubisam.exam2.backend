package backend.rest.farms;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import backend.domain.Farm;
import io.u2ware.common.docs.MockMvcRestDocs;

@Component
public class FarmDocs extends MockMvcRestDocs{

  public Farm newEntity(String... entity){
    Farm body = new Farm();
    body.setFarmName(entity.length > 0 ? entity[0] : super.randomText("farmName"));
    body.setFarmLocation(entity.length > 1 ? entity[1] : super.randomText("farmLocation"));
    return body;
  }

  public Map<String, Object> setSearch(String keyword, String option){
    Map<String, Object> body = new HashMap<>();
    body.put("keyword", keyword);
    body.put("option", option);
    return body;
  }
  
}
