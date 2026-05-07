package backend.rest.farmDeviceTypes;

import static io.u2ware.common.docs.MockMvcRestDocs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import backend.domain.FarmDeviceType;
import backend.oauth2.Oauth2Docs;
import io.u2ware.common.data.jpa.repository.query.JpaSpecificationBuilder;

@SpringBootTest
@AutoConfigureMockMvc
public class FarmDeviceTypeTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private Oauth2Docs od;

  @Autowired
  private FarmDeviceTypeDocs fdtd;

  @Autowired
  private FarmDeviceTypeRepository farmDeviceTypeRepository;

  @Test
  void contextLoads() throws Exception{
    // 유저 설정
    Jwt u = od.jose("user");

    // Crud - C
    mvc.perform(post("/rest/farmDeviceTypes").content(fdtd::newEntity, "Thermometer")).andExpect(is4xx());
    mvc.perform(post("/rest/farmDeviceTypes").content(fdtd::newEntity, "Thermometer").auth(u)).andExpect(is2xx()).andDo(result(fdtd::context, "entity1"));

    // Crud - R
    String uri = fdtd.context("entity1", "$._links.self.href");
    mvc.perform(post(uri)).andExpect(is4xx());
    mvc.perform(post(uri).auth(u)).andExpect(is2xx());

    // Crud - U
    Map<String, Object> body = fdtd.context("entity1", "$");
    mvc.perform(put(uri).content(fdtd::updateEntity, body, "Hygrometer")).andExpect(is4xx());
    mvc.perform(put(uri).content(fdtd::updateEntity, body, "Hygrometer").auth(u)).andExpect(is2xx());

    // Crud - D
    mvc.perform(delete(uri)).andExpect(is4xx());
    mvc.perform(delete(uri).auth(u)).andExpect(is2xx());
  }

  @Test
  void contextLoads2() throws Exception{
    List<FarmDeviceType> result;
    boolean hasResult;

    List<FarmDeviceType> farmDeviceTypeList = new ArrayList<>();
    for(int i=1; i<=30; i++){
      farmDeviceTypeList.add(fdtd.newEntity(i+"meter", i+"도계"));
    }
    farmDeviceTypeRepository.saveAll(farmDeviceTypeList);

    JpaSpecificationBuilder<FarmDeviceType> keyQuery = JpaSpecificationBuilder.of(FarmDeviceType.class);
    keyQuery.where().and().eq("farmDeviceTypeKey", "3meter");
    result = farmDeviceTypeRepository.findAll(keyQuery.build());
    hasResult = result.stream().anyMatch(u -> "3meter".equals(u.getFarmDeviceTypeKey()));
    assertEquals(true, hasResult);

    JpaSpecificationBuilder<FarmDeviceType> nameQuery = JpaSpecificationBuilder.of(FarmDeviceType.class);
    nameQuery.where().and().eq("farmDeviceTypeName", "2도계");
    result = farmDeviceTypeRepository.findAll(nameQuery.build());
    hasResult = result.stream().anyMatch(u -> "2도계".equals(u.getFarmDeviceTypeName()));
    assertEquals(true, hasResult);
  }
  
  @Test
  void contextLoads3() throws Exception{
    // 유저 설정
    Jwt u = od.jose("user1");

    List<FarmDeviceType> farmDeviceTypeList = new ArrayList<>();
    for(int i=1; i<=30; i++){
      farmDeviceTypeList.add(fdtd.newEntity(i+"meter", i+"도계"));
    }
    farmDeviceTypeRepository.saveAll(farmDeviceTypeList);

    String uri = "/rest/farmDeviceTypes/search";

    // Search - 단일
    mvc.perform(post(uri).content(fdtd::setSearch, "6meter", "farmDeviceTypeKey").auth(u)).andExpect(is2xx());
    mvc.perform(post(uri).content(fdtd::setSearch, "7도계", "farmDeviceTypeName").auth(u)).andExpect(is2xx());

    // Search - 페이지네이션
    mvc.perform(post(uri).param("size", "6").auth(u)).andExpect(is2xx());

    // Search - 정렬
    mvc.perform(post(uri).param("sort", "farmDeviceTypeKey,desc").auth(u)).andExpect(is2xx());
  }
}
