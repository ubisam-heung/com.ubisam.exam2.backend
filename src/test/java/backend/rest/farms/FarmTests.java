package backend.rest.farms;

import static io.u2ware.common.docs.MockMvcRestDocs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import backend.domain.Farm;
import backend.oauth2.Oauth2Docs;
import backend.rest.farmDevices.FarmDeviceDocs;
import backend.rest.farmPlants.FarmPlantDocs;
import io.u2ware.common.data.jpa.repository.query.JpaSpecificationBuilder;

@SpringBootTest
@AutoConfigureMockMvc
public class FarmTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private Oauth2Docs od;

  @Autowired
  private FarmDocs fd;

  @Autowired
  private FarmRepository farmRepository;

  @Autowired
  private FarmPlantDocs fpd;

  @Autowired
  private FarmDeviceDocs fdd;

  @Test
  void contextLoads() throws Exception{
    // 유저 설정
    Jwt u = od.jose("user");

    // 사전 설정
    mvc.perform(post("/rest/farmPlants").content(fpd::newEntity, "충주사과").auth(u)).andExpect(is2xx()).andDo(result(fpd::context, "fpdentity1"));
    mvc.perform(post("/rest/farmPlants").content(fpd::newEntity, "보령배").auth(u)).andExpect(is2xx()).andDo(result(fpd::context, "fpdentity2"));
    mvc.perform(post("/rest/farmPlants").content(fpd::newEntity, "서울바나나").auth(u)).andExpect(is2xx()).andDo(result(fpd::context, "fpdentity3"));
    String farmPlantLink1 = fpd.context("fpdentity1", "$._links.self.href");
    String farmPlantLink2 = fpd.context("fpdentity2", "$._links.self.href");
    String farmPlantLink3 = fpd.context("fpdentity3", "$._links.self.href");

    mvc.perform(post("/rest/farmDevices").content(fdd::newEntity, "01xx01").auth(u)).andExpect(is2xx()).andDo(result(fdd::context, "fddentity1"));
    mvc.perform(post("/rest/farmDevices").content(fdd::newEntity, "02xx01").auth(u)).andExpect(is2xx()).andDo(result(fdd::context, "fddentity2"));
    mvc.perform(post("/rest/farmDevices").content(fdd::newEntity, "03xx01").auth(u)).andExpect(is2xx()).andDo(result(fdd::context, "fddentity3"));
    String farmDeviceLink1 = fdd.context("fddentity1", "$._links.self.href");
    String farmDeviceLink2 = fdd.context("fddentity2", "$._links.self.href");
    String farmDeviceLink3 = fdd.context("fddentity3", "$._links.self.href");

    Map<String, Object> req = new HashMap<>();
    req.put("title", "entity1");
    req.put("farmName", "A농장");
    req.put("farmOwner", "김길동");
    req.put("farmLocation", "서울");
    req.put("farmPlantLinks", Set.of(farmPlantLink1, farmPlantLink2));
    req.put("farmDeviceLinks", Set.of(farmDeviceLink1, farmDeviceLink2));

    // Crud - C
    mvc.perform(post("/rest/farms").content(req)).andExpect(is4xx());
    mvc.perform(post("/rest/farms").content(req).auth(u)).andExpect(is2xx()).andDo(result(fd::context, "entity1"));
    req = fd.context("entity1", "$");

    // Crud - R
    String uri = fd.context("entity1", "$._links.self.href");
    mvc.perform(post(uri)).andExpect(is4xx());
    mvc.perform(post(uri).auth(u)).andExpect(is2xx());

    // Crud - U
    req.put("farmName", "B농장");
    req.put("farmPlantLinks", Set.of(farmPlantLink3, farmPlantLink2));
    req.put("farmDeviceLinks", Set.of(farmDeviceLink3, farmDeviceLink2));
    mvc.perform(put(uri).content(req)).andExpect(is4xx());
    mvc.perform(put(uri).content(req).auth(u)).andExpect(is2xx());

    // Crud - D
    mvc.perform(delete(uri)).andExpect(is4xx());
    mvc.perform(delete(uri).auth(u)).andExpect(is2xx());
  }

  @Test
  void contextLoads2() throws Exception{
    List<Farm> result;
    boolean hasResult;

    List<Farm> farmList = new ArrayList<>();
    for(int i=1; i<=30; i++){
      farmList.add(fd.newEntity(i+"농장", i+"위치"));
    }
    farmRepository.saveAll(farmList);

    JpaSpecificationBuilder<Farm> nameQuery = JpaSpecificationBuilder.of(Farm.class);
    nameQuery.where().and().eq("farmName", "4농장");
    result = farmRepository.findAll(nameQuery.build());
    hasResult = result.stream().anyMatch(u -> "4농장".equals(u.getFarmName()));
    assertEquals(true, hasResult);

    JpaSpecificationBuilder<Farm> locationQuery = JpaSpecificationBuilder.of(Farm.class);
    locationQuery.where().and().eq("farmLocation", "5위치");
    result = farmRepository.findAll(locationQuery.build());
    hasResult = result.stream().anyMatch(u -> "5위치".equals(u.getFarmLocation()));
    assertEquals(true, hasResult);
  }
  
  @Test
  void contextLoads3() throws Exception{
    // 유저 설정
    Jwt u = od.jose("user1");

    List<Farm> farmList = new ArrayList<>();
    for(int i=1; i<=30; i++){
      farmList.add(fd.newEntity(i+"농장", i+"위치"));
    }
    farmRepository.saveAll(farmList);

    String uri = "/rest/farms/search";

    // Search - 단일
    mvc.perform(post(uri).content(fd::setSearch, "2농장", "farmName").auth(u)).andExpect(is2xx());
    mvc.perform(post(uri).content(fd::setSearch, "7위치", "farmLocation").auth(u)).andExpect(is2xx());

    // Search - 페이지네이션
    mvc.perform(post(uri).param("size", "6").auth(u)).andExpect(is2xx());

    // Search - 정렬
    mvc.perform(post(uri).param("sort", "farmName,desc").auth(u)).andExpect(is2xx());
  }
}
