package backend.rest.farmDeviceTypes;

import java.util.UUID;

import org.springframework.data.rest.core.annotation.RestResource;

import backend.domain.FarmDeviceType;
import io.u2ware.common.data.jpa.repository.RestfulJpaRepository;
import java.util.List;


public interface FarmDeviceTypeRepository extends RestfulJpaRepository<FarmDeviceType, UUID>{

  @RestResource(exported = false)
  List<FarmDeviceType> findByFarmDeviceTypeName(String farmDeviceTypeName); 
  
}
