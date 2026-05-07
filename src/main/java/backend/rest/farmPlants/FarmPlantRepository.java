package backend.rest.farmPlants;

import java.util.UUID;

import org.springframework.data.rest.core.annotation.RestResource;

import backend.domain.FarmPlant;
import io.u2ware.common.data.jpa.repository.RestfulJpaRepository;
import java.util.List;


public interface FarmPlantRepository extends RestfulJpaRepository<FarmPlant, UUID>{
  
  @RestResource(exported = false)
  List<FarmPlant> findByFarmPlantName(String farmPlantName);
}
