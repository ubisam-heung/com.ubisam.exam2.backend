package backend.rest.farmPlants;

import static io.u2ware.common.docs.MockMvcRestDocs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import backend.domain.FarmPlant;
import backend.oauth2.Oauth2Docs;
import io.u2ware.common.data.jpa.repository.query.JpaSpecificationBuilder;

@SpringBootTest
@AutoConfigureMockMvc
public class FarmPlantTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private Oauth2Docs od;

  @Autowired
  private FarmPlantDocs fpd;

  @Autowired
  private FarmPlantRepository farmPlantRepository;

  @Test
  void contextLoads() throws Exception{
    // 유저 설정
    Jwt u = od.jose("user");

    // 사전 설정
    Set<Object> farmPlantDevice = new HashSet<>();
    farmPlantDevice.add("Thermometer");
    farmPlantDevice.add("Hygrometer");

    Map<String, Object> req = new HashMap<>();
    req.put("title", "entity1");
    req.put("farmPlantName", "충주사과");
    req.put("farmPlantSpecies", "사과");
    req.put("farmPlantDevices", farmPlantDevice);

    // Crud - C
    mvc.perform(post("/rest/farmPlants").content(req)).andExpect(is4xx());
    mvc.perform(post("/rest/farmPlants").content(req).auth(u)).andExpect(is2xx()).andDo(result(fpd::context, "entity1"));
    req = fpd.context("entity1", "$");

    // Crud - R
    String uri = fpd.context("entity1", "$._links.self.href");
    mvc.perform(post(uri)).andExpect(is4xx());
    mvc.perform(post(uri).auth(u)).andExpect(is2xx());

    // Crud - U
    req.put("farmPlantName", "보령배");
    req.put("farmPlantSpecies", "배");
    mvc.perform(put(uri).content(req)).andExpect(is4xx());
    mvc.perform(put(uri).content(req).auth(u)).andExpect(is2xx());

    // Crud - D
    mvc.perform(delete(uri)).andExpect(is4xx());
    mvc.perform(delete(uri).auth(u)).andExpect(is2xx());
  }

  @Test
  void contextLoads2() throws Exception{
    List<FarmPlant> result;
    boolean hasResult;

    List<FarmPlant> farmPlantList = new ArrayList<>();
    for(int i=1; i<=30; i++){
      farmPlantList.add(fpd.newEntity(i+"서울사과", i+"사과"));
    }
    farmPlantRepository.saveAll(farmPlantList);

    JpaSpecificationBuilder<FarmPlant> nameQuery = JpaSpecificationBuilder.of(FarmPlant.class);
    nameQuery.where().and().eq("farmPlantName", "6서울사과");
    result = farmPlantRepository.findAll(nameQuery.build());
    hasResult = result.stream().anyMatch(u -> "6서울사과".equals(u.getFarmPlantName()));
    assertEquals(true, hasResult);

    JpaSpecificationBuilder<FarmPlant> speciesQuery = JpaSpecificationBuilder.of(FarmPlant.class);
    speciesQuery.where().and().eq("farmPlantSpecies", "4사과");
    result = farmPlantRepository.findAll(speciesQuery.build());
    hasResult = result.stream().anyMatch(u -> "4사과".equals(u.getFarmPlantSpecies()));
    assertEquals(true, hasResult);
  }
  
  @Test
  void contextLoads3() throws Exception{
    // 유저 설정
    Jwt u = od.jose("user1");

    List<FarmPlant> farmPlantList = new ArrayList<>();
    for(int i=1; i<=30; i++){
      farmPlantList.add(fpd.newEntity(i+"서울사과", i+"사과"));
    }
    farmPlantRepository.saveAll(farmPlantList);

    String uri = "/rest/farmPlants/search";

    // Search - 단일
    mvc.perform(post(uri).content(fpd::setSearch, "5서울사과", "farmPlantName").auth(u)).andExpect(is2xx());
    mvc.perform(post(uri).content(fpd::setSearch, "7사과", "farmPlantSpecies").auth(u)).andExpect(is2xx());

    // Search - 페이지네이션
    mvc.perform(post(uri).param("size", "6").auth(u)).andExpect(is2xx());

    // Search - 정렬
    mvc.perform(post(uri).param("sort", "farmPlantName,desc").auth(u)).andExpect(is2xx());
  }
}
