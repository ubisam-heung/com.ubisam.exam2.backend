package backend.rest.devices;

import java.util.UUID;

import backend.domain.Device;
import io.u2ware.common.data.jpa.repository.RestfulJpaRepository;

public interface DeviceRepository extends RestfulJpaRepository<Device, UUID>{
  
}
