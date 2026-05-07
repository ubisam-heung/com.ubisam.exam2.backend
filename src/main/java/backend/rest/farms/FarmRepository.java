package backend.rest.farms;

import java.util.UUID;

import org.springframework.data.rest.core.annotation.RestResource;

import backend.domain.Farm;
import io.u2ware.common.data.jpa.repository.RestfulJpaRepository;
import java.util.List;


public interface FarmRepository extends RestfulJpaRepository<Farm, UUID>{
  @RestResource(exported = false)
  List<Farm> findByFarmName(String farmName);
}
