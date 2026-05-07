package backend.rest.farmPlants;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import backend.domain.FarmPlant;
import backend.domain.properties.AttributesSet;
import io.u2ware.common.docs.MockMvcRestDocs;

@Component
public class FarmPlantDocs extends MockMvcRestDocs{

  public FarmPlant newEntity(String... entity){
    FarmPlant body = new FarmPlant();
    body.setFarmPlantName(entity.length > 0 ? entity[0] : super.randomText("farmPlantName"));
    body.setFarmPlantSpecies(entity.length > 1 ? entity[1] : super.randomText("farmPlantSpecies"));
    body.setFarmPlantDevices(entity.length > 2
      ? new AttributesSet(entity[2])
      : new AttributesSet(super.randomText("farmPlantDevices"))
    );
    return body;
  }

  public Map<String, Object> setSearch(String keyword, String option){
    Map<String, Object> body = new HashMap<>();
    body.put("keyword", keyword);
    body.put("option", option);
    return body;
  }
  
}
