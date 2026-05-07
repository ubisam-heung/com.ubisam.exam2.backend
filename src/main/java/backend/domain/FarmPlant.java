package backend.domain;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import backend.domain.properties.AttributesSet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
@Table(name = "example_farm_plant")
public class FarmPlant {

  @Id
  @GeneratedValue
  private UUID id;

  // 재배 식물 이름 ("충주사과")
  private String farmPlantName; 

  // 재배 식물 종 ("사과")
  private String farmPlantSpecies; 

  // 해당 재배 식물이 필요하는 기계 ["Thermometer", "Hygrometer" 등]
  @Column(length = 1024*100)
  private AttributesSet farmPlantDevices = new AttributesSet();

  @Transient
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String keyword;

  @Transient
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String option;
  
}
