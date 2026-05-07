package backend.rest.farms;

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

import backend.domain.Farm;
import backend.domain.FarmDevice;
import backend.domain.FarmPlant;
import backend.domain.properties.LinkConversion;
import io.u2ware.common.data.jpa.repository.query.JpaSpecificationBuilder;
import io.u2ware.common.data.rest.core.annotation.HandleAfterRead;
import io.u2ware.common.data.rest.core.annotation.HandleBeforeRead;

@Component
@RepositoryEventHandler
public class FarmHandler {

  protected Log logger = LogFactory.getLog(getClass());

  @Autowired
  private LinkConversion linkConversion;

  public void conversion(Farm e) throws Exception{
    logger.info("conversion1: " + e.getFarmDeviceLinks());
    logger.info("conversion1: " + e.getFarmPlantLinks());
    linkConversion.convertWithEntity(FarmDevice.class, e.getFarmDeviceLinks(), ref->{e.setFarmDevices(ref);});
    linkConversion.convertWithEntity(FarmPlant.class, e.getFarmPlantLinks(), ref->{e.setFarmPlants(ref);});
  }

  @HandleBeforeRead
  public void HandleBeforeRead(Farm e, Specification<Farm> spec) throws Exception{
    logger.info("@HandleBeforeRead: "+ e);
    JpaSpecificationBuilder<Farm> query = JpaSpecificationBuilder.of(Farm.class);
    String keyword = e.getKeyword();
    String option = e.getOption();
    if(keyword == null || keyword.trim().isEmpty()){
      query.where().build(spec);
      return;
    }
    switch (option) {
      case "farmAll":
        query.where()
          .and().like("farmName", "%"+keyword+"%")
          .or().like("farmOwner", "%"+keyword+"%")
          .or().like("farmLocation", "%"+keyword+"%")
          .build(spec);
        break;
      case "farmName":
        query.where()
          .and().like("farmName", "%"+keyword+"%")
          .build(spec);
        break;
      case "farmOwner":
        query.where()
          .and().like("farmOwner", "%"+keyword+"%")
          .build(spec);
        break;
      case "farmLocation":
        query.where()
          .and().like("farmLocation", "%"+keyword+"%")
          .build(spec);
        break;
      default:
        query.where()
          .and().like("farmName", "%"+keyword+"%")
          .or().like("farmOwner", "%"+keyword+"%")
          .or().like("farmLocation", "%"+keyword+"%")
          .build(spec);
        break;
    }
  }
  
  @HandleAfterRead
  public void HandleAfterRead(Farm e, Serializable r) throws Exception{
    logger.info("@HandleAfterRead: "+ e);
    logger.info("@HandleAfterRead: "+ r);
  }

  @HandleBeforeCreate
  public void HandleBeforeCreate(Farm e) throws Exception{
    conversion(e);
    logger.info("@HandleBeforeCreate: "+ e);
  }

  @HandleBeforeSave
  public void HandleBeforeSave(Farm e) throws Exception{
    conversion(e);
    logger.info("@HandleBeforeSave: "+ e);
  }

  @HandleBeforeDelete
  public void HandleBeforeDelete(Farm e) throws Exception{
    logger.info("@HandleBeforeDelete: "+ e);
  }

  @HandleAfterCreate
  public void HandleAfterCreate(Farm e) throws Exception{
    logger.info("@HandleAfterCreate: "+ e);
  }

  @HandleAfterSave
  public void HandleAfterSave(Farm e) throws Exception{
    logger.info("@HandleAfterSave: "+ e);
  }

  @HandleAfterDelete
  public void HandleAfterDelete(Farm e) throws Exception{
    logger.info("@HandleAfterDelete: "+ e);
  }

}
