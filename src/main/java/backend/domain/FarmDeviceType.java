package backend.domain;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
@Table(name = "example_farm_device_type")
public class FarmDeviceType {

  @Id
  @GeneratedValue
  private UUID id;

  // "Thermometer", "Hygrometer" 등
  private String farmDeviceTypeKey;
  
  // "온도계", "습도계" 등
  private String farmDeviceTypeName;

  // "°C", "%" 등
  private String farmDeviceTypeUnit;

  /*
    장치가 더 추가되면 (예를들면 조명센서 추가) 해당 도메인에 필드만 추가하면됌
  */

  @Transient
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String keyword;

  @Transient
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String option;
  
}
