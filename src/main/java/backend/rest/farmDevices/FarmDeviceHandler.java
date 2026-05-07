package backend.rest.farmDevices;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import backend.domain.FarmDevice;
import backend.domain.FarmDeviceType;
import backend.domain.properties.LinkConversion;
import io.u2ware.common.data.jpa.repository.query.JpaSpecificationBuilder;
import io.u2ware.common.data.rest.core.annotation.HandleAfterRead;
import io.u2ware.common.data.rest.core.annotation.HandleBeforeRead;

@Component
@RepositoryEventHandler
public class FarmDeviceHandler {

  protected Log logger = LogFactory.getLog(getClass());

  @Autowired
  private LinkConversion linkConversion;

  public void conversion(FarmDevice e) throws Exception{
    logger.info("conversion1: " + e.getFarmDeviceTypeLink());
    linkConversion.convertWithEntity(FarmDeviceType.class, e.getFarmDeviceTypeLink(), ref->{e.setFarmDeviceType(ref);});
  }

  @HandleBeforeRead
  public void HandleBeforeRead(FarmDevice e, Specification<FarmDevice> spec) throws Exception{
    logger.info("@HandleBeforeRead: "+ e);
    JpaSpecificationBuilder<FarmDevice> query = JpaSpecificationBuilder.of(FarmDevice.class);
    String keyword = e.getKeyword();
    String option = e.getOption();
    if(keyword == null || keyword.trim().isEmpty()){
      query.where().build(spec);
      return;
    }
    switch (option) {
      case "farmDeviceAll":
        query.where()
          .and().like("farmDeviceSerial", "%"+keyword+"%")
          .or().like("farmDeviceState", "%"+keyword+"%")
          .build(spec);
        break;
      case "farmDeviceSerial":
        query.where()
          .and().like("farmDeviceSerial", "%"+keyword+"%")
          .build(spec);
        break;
      case "farmDeviceState":
        query.where()
          .and().like("farmDeviceState", "%"+keyword+"%")
          .build(spec);
        break;
      default:
        query.where()
          .and().like("farmDeviceSerial", "%"+keyword+"%")
          .or().like("farmDeviceState", "%"+keyword+"%")
          .build(spec);
        break;
    }
  }
  
  @HandleAfterRead
  public void HandleAfterRead(FarmDevice e, Serializable r) throws Exception{
    logger.info("@HandleAfterRead: "+ e);
    logger.info("@HandleAfterRead: "+ r);
  }

  @HandleBeforeCreate
  public void HandleBeforeCreate(FarmDevice e) throws Exception{
    conversion(e);
    logger.info("@HandleBeforeCreate: "+ e);
  }

  @HandleBeforeSave
  public void HandleBeforeSave(FarmDevice e) throws Exception{
    conversion(e);
    logger.info("@HandleBeforeSave: "+ e);
  }

  @HandleBeforeDelete
  public void HandleBeforeDelete(FarmDevice e) throws Exception{
    logger.info("@HandleBeforeDelete: "+ e);
  }

  @HandleAfterCreate
  public void HandleAfterCreate(FarmDevice e) throws Exception{
    logger.info("@HandleAfterCreate: "+ e);
  }

  @HandleAfterSave
  public void HandleAfterSave(FarmDevice e) throws Exception{
    logger.info("@HandleAfterSave: "+ e);
  }

  @HandleAfterDelete
  public void HandleAfterDelete(FarmDevice e) throws Exception{
    logger.info("@HandleAfterDelete: "+ e);
  }

}
