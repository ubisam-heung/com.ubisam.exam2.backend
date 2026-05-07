package backend.rest.farmDeviceTypes;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import backend.domain.FarmDeviceType;
import io.u2ware.common.data.jpa.repository.query.JpaSpecificationBuilder;
import io.u2ware.common.data.rest.core.annotation.HandleAfterRead;
import io.u2ware.common.data.rest.core.annotation.HandleBeforeRead;

@Component
@RepositoryEventHandler
public class FarmDeviceTypeHandler {

  protected Log logger = LogFactory.getLog(getClass());

  @HandleBeforeRead
  public void HandleBeforeRead(FarmDeviceType e, Specification<FarmDeviceType> spec) throws Exception{
    logger.info("@HandleBeforeRead: "+ e);
    JpaSpecificationBuilder<FarmDeviceType> query = JpaSpecificationBuilder.of(FarmDeviceType.class);
    String keyword = e.getKeyword();
    String option = e.getOption();
    if(keyword == null || keyword.trim().isEmpty()){
      query.where().build(spec);
      return;
    }
    switch (option) {
      case "farmDeviceTypeAll":
        query.where()
          .and().like("farmDeviceTypeKey", "%"+keyword+"%")
          .or().like("farmDeviceTypeName", "%"+keyword+"%")
          .build(spec);
        break;
      case "farmDeviceTypeKey":
        query.where()
          .and().like("farmDeviceTypeKey", "%"+keyword+"%")
          .build(spec);
        break;
      case "farmDeviceTypeName":
        query.where()
          .and().like("farmDeviceTypeName", "%"+keyword+"%")
          .build(spec);
        break;
      default:
        query.where()
          .and().like("farmDeviceTypeKey", "%"+keyword+"%")
          .or().like("farmDeviceTypeName", "%"+keyword+"%")
          .build(spec);
        break;
    }
  }
  
  @HandleAfterRead
  public void HandleAfterRead(FarmDeviceType e, Serializable r) throws Exception{
    logger.info("@HandleAfterRead: "+ e);
    logger.info("@HandleAfterRead: "+ r);
  }

  @HandleBeforeCreate
  public void HandleBeforeCreate(FarmDeviceType e) throws Exception{
    logger.info("@HandleBeforeCreate: "+ e);
  }

  @HandleBeforeSave
  public void HandleBeforeSave(FarmDeviceType e) throws Exception{
    logger.info("@HandleBeforeSave: "+ e);
  }

  @HandleBeforeDelete
  public void HandleBeforeDelete(FarmDeviceType e) throws Exception{
    logger.info("@HandleBeforeDelete: "+ e);
  }

  @HandleAfterCreate
  public void HandleAfterCreate(FarmDeviceType e) throws Exception{
    logger.info("@HandleAfterCreate: "+ e);
  }

  @HandleAfterSave
  public void HandleAfterSave(FarmDeviceType e) throws Exception{
    logger.info("@HandleAfterSave: "+ e);
  }

  @HandleAfterDelete
  public void HandleAfterDelete(FarmDeviceType e) throws Exception{
    logger.info("@HandleAfterDelete: "+ e);
  }

}
