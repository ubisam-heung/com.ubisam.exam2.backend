package backend.rest.farmPlants;

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

import backend.domain.FarmPlant;
import io.u2ware.common.data.jpa.repository.query.JpaSpecificationBuilder;
import io.u2ware.common.data.rest.core.annotation.HandleAfterRead;
import io.u2ware.common.data.rest.core.annotation.HandleBeforeRead;

@Component
@RepositoryEventHandler
public class FarmPlantHandler {

  protected Log logger = LogFactory.getLog(getClass());

  @HandleBeforeRead
  public void HandleBeforeRead(FarmPlant e, Specification<FarmPlant> spec) throws Exception{
    logger.info("@HandleBeforeRead: "+ e);
    JpaSpecificationBuilder<FarmPlant> query = JpaSpecificationBuilder.of(FarmPlant.class);
    String keyword = e.getKeyword();
    String option = e.getOption();
    if(keyword == null || keyword.trim().isEmpty()){
      query.where().build(spec);
      return;
    }
    switch (option) {
      case "farmPlantAll":
        query.where()
          .and().like("farmPlantName", "%"+keyword+"%")
          .or().like("farmPlantSpecies", "%"+keyword+"%")
          .or().like("farmPlantDevices", "%"+keyword+"%")
          .build(spec);
        break;
      case "farmPlantName":
        query.where()
          .and().like("farmPlantName", "%"+keyword+"%")
          .build(spec);
        break;
      case "farmPlantSpecies":
        query.where()
          .and().like("farmPlantSpecies", "%"+keyword+"%")
          .build(spec);
        break;
      case "farmPlantDevices":
        query.where()
          .and().like("farmPlantDevices", "%"+keyword+"%")
          .build(spec);
        break;
      default:
        query.where()
          .and().like("farmPlantName", "%"+keyword+"%")
          .or().like("farmPlantSpecies", "%"+keyword+"%")
          .or().like("farmPlantDevices", "%"+keyword+"%")
          .build(spec);
        break;
    }
  }
  
  @HandleAfterRead
  public void HandleAfterRead(FarmPlant e, Serializable r) throws Exception{
    logger.info("@HandleAfterRead: "+ e);
    logger.info("@HandleAfterRead: "+ r);
  }

  @HandleBeforeCreate
  public void HandleBeforeCreate(FarmPlant e) throws Exception{
    logger.info("@HandleBeforeCreate: "+ e);
  }

  @HandleBeforeSave
  public void HandleBeforeSave(FarmPlant e) throws Exception{
    logger.info("@HandleBeforeSave: "+ e);
  }

  @HandleBeforeDelete
  public void HandleBeforeDelete(FarmPlant e) throws Exception{
    logger.info("@HandleBeforeDelete: "+ e);
  }

  @HandleAfterCreate
  public void HandleAfterCreate(FarmPlant e) throws Exception{
    logger.info("@HandleAfterCreate: "+ e);
  }

  @HandleAfterSave
  public void HandleAfterSave(FarmPlant e) throws Exception{
    logger.info("@HandleAfterSave: "+ e);
  }

  @HandleAfterDelete
  public void HandleAfterDelete(FarmPlant e) throws Exception{
    logger.info("@HandleAfterDelete: "+ e);
  }

}
