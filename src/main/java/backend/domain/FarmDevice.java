package backend.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "example_farm_device")
public class FarmDevice {

  @Id
  @GeneratedValue
  private UUID id;

  // 물리 장치 시리얼 키
  private String farmDeviceSerial; 

  // 장치 상태 ("ACTIVE", "STOP", 등)
  private String farmDeviceState;

  @ManyToOne
  @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  @RestResource(exported = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private FarmDeviceType farmDeviceType;

  @Transient
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Link farmDeviceTypeLink;

  @ElementCollection
  @CollectionTable(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), name = "example_farm_device_history")
  private Set<FarmDeviceHistory> farmDeviceHistory = new HashSet<>();

  @Embeddable
  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class FarmDeviceHistory{
    private String farmDeviceName;
    private String farmDeviceState;
    private String farmDeviceTimestamp;
  }

  @Transient
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String keyword;

  @Transient
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String option;
  
}
