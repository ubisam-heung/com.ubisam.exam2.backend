package backend.rest.farmDevices;

import java.util.UUID;

import org.springframework.data.rest.core.annotation.RestResource;

import backend.domain.FarmDevice;
import io.u2ware.common.data.jpa.repository.RestfulJpaRepository;
import java.util.List;


public interface FarmDeviceRepository extends RestfulJpaRepository<FarmDevice, UUID>{

  @RestResource(exported = false)
  List<FarmDevice> findByFarmDeviceSerial(String farmDeviceSerial);
  
}
