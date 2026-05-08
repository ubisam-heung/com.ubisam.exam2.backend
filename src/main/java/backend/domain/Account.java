package backend.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import backend.domain.auditing.AuditedEntity;
import backend.domain.properties.AttributesSet;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "example_account")
@EqualsAndHashCode(callSuper = true)
public class Account extends AuditedEntity{
  
  @Id
  private String id;

  private String provider;
  private String username;
  private AttributesSet roles;

  public static Collection<GrantedAuthority> getAuthorities(AttributesSet roles){
    if(roles == null){
      return Collections.emptyList();
    }
    return roles.stream().map(r -> new SimpleGrantedAuthority(r.toString())).collect(Collectors.toList());
  }
}
