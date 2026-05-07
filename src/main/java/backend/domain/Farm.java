package backend.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
@Table(name = "example_farm")
public class Farm {

  @Id
  @GeneratedValue
  private UUID id;

  // 농장 이름
  private String farmName;

  // 농장주
  private String farmOwner; 

  // 농장 위치
  private String farmLocation;

  @ManyToMany
  @JoinTable(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), inverseForeignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  @RestResource(exported = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Set<FarmPlant> farmPlants;

  @Transient
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Set<Link> farmPlantLinks = new HashSet<>();

  @OneToMany
  @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  @RestResource(exported = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Set<FarmDevice> farmDevices;

  @Transient
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Set<Link> farmDeviceLinks = new HashSet<>();

  @Transient
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String keyword;

  @Transient
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String option;
  
}
