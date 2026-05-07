package backend.rest.devices;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import backend.domain.Device;
import backend.domain.auditing.AuditedAuditor;
import backend.domain.exception.ResponseStatusExceptions;
import io.u2ware.common.data.rest.core.annotation.HandleAfterRead;
import io.u2ware.common.data.rest.core.annotation.HandleBeforeRead;

@Component
@RepositoryEventHandler
public class DeviceHandler {

  protected Log logger = LogFactory.getLog(getClass());

  @HandleBeforeCreate
  public void handleBeforeCreate(Device e) throws Exception{
    logger.info("[HandleBeforeCreate] : "+ e);
    if(AuditedAuditor.hasNotPermission("ROLE_ADMIN")){
      throw ResponseStatusExceptions.UNAUTHORIZED;
    }
  }

  @HandleBeforeSave
  public void handleBeforeSave(Device e) throws Exception{
    logger.info("[HandleBeforeSave] : "+ e);
    if(AuditedAuditor.hasNotPermission("ROLE_ADMIN")){
      throw ResponseStatusExceptions.UNAUTHORIZED;
    }
  }

  @HandleBeforeDelete
  public void handleBeforeDelete(Device e) throws Exception{
    logger.info("[HandleBeforeDelete] : "+ e);
    if(AuditedAuditor.hasNotPermission("ROLE_ADMIN")){
      throw ResponseStatusExceptions.UNAUTHORIZED;
    }
  }

  @HandleBeforeRead
  public void handleBeforeRead(Device e, Specification<Device> s) throws Exception{
    logger.info("[HandleBeforeRead] : "+ e);
    if(AuditedAuditor.hasNotPermission("ROLE_ADMIN")){
      throw ResponseStatusExceptions.UNAUTHORIZED;
    }
  }

  @HandleAfterRead
  public void handleAfterRead(Device e, Serializable s) throws Exception{
    logger.info("[HandleAfterRead] : "+ e);
    logger.info("[HandleAfterRead] : "+ s);
  }

  @HandleAfterSave
  public void handleAfterSave(Device e) throws Exception{
    logger.info("[HandleAfterSave] : "+ e);
  }

  @HandleAfterDelete
  public void handleAfterDelete(Device e) throws Exception{
    logger.info("[HandleAfterDelete] : "+ e);
  }
}
