package backend.rest.farmDevices;

import static io.u2ware.common.docs.MockMvcRestDocs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import backend.domain.FarmDevice;
import backend.domain.FarmDevice.FarmDeviceHistory;
import backend.oauth2.Oauth2Docs;
import backend.rest.farmDeviceTypes.FarmDeviceTypeDocs;
import io.u2ware.common.data.jpa.repository.query.JpaSpecificationBuilder;

@SpringBootTest
@AutoConfigureMockMvc
public class FarmDeviceTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private Oauth2Docs od;

  @Autowired
  private FarmDeviceDocs fdd;

  @Autowired
  private FarmDeviceRepository farmDeviceRepository;

  @Autowired
  private FarmDeviceTypeDocs fdtd;

  @Test
  void contextLoads() throws Exception{
    // 유저 설정
    Jwt u = od.jose("user");

    // 사전 설정
    mvc.perform(post("/rest/farmDeviceTypes").content(fdtd::newEntity, "Thermometer").auth(u)).andExpect(is2xx()).andDo(result(fdtd::context, "fdtdentity1"));
    mvc.perform(post("/rest/farmDeviceTypes").content(fdtd::newEntity, "Hygrometer").auth(u)).andExpect(is2xx()).andDo(result(fdtd::context, "fdtdentity2"));
    String farmDeviceTypeLink1 = fdtd.context("fdtdentity1", "$._links.self.href");
    String farmDeviceTypeLink2 = fdtd.context("fdtdentity2", "$._links.self.href");

    FarmDeviceHistory fdh1 = new FarmDeviceHistory("fdh1", "00 01 02 03", "20260507");
    FarmDeviceHistory fdh2 = new FarmDeviceHistory("fdh2", "01 05 01 02", "20260507");
    FarmDeviceHistory fdh3 = new FarmDeviceHistory("fdh3", "05 07 09 00", "20260507");

    Map<String, Object> req = new HashMap<>();
    req.put("title", "entity1");
    req.put("farmDeviceSerial", "01xx00");
    req.put("farmDeviceState", "ACTIVE");
    req.put("farmDeviceTypeLink", farmDeviceTypeLink1);
    req.put("farmDeviceHistory", new Object[]{fdh1, fdh2});

    // Crud - C
    mvc.perform(post("/rest/farmDevices").content(req)).andExpect(is4xx());
    mvc.perform(post("/rest/farmDevices").content(req).auth(u)).andExpect(is2xx()).andDo(result(fdd::context, "entity1"));
    req = fdd.context("entity1", "$");

    // Crud - R
    String uri = fdd.context("entity1", "$._links.self.href");
    mvc.perform(post(uri)).andExpect(is4xx());
    mvc.perform(post(uri).auth(u)).andExpect(is2xx());

    // Crud - U
    req.put("farmDeviceSerial", "02xx01");
    req.put("farmDeviceTypeLink", farmDeviceTypeLink2);
    req.put("farmDeviceHistory", new Object[]{fdh3, fdh2});
    mvc.perform(put(uri).content(req)).andExpect(is4xx());
    mvc.perform(put(uri).content(req).auth(u)).andExpect(is2xx());

    // Crud - D
    mvc.perform(delete(uri)).andExpect(is4xx());
    mvc.perform(delete(uri).auth(u)).andExpect(is2xx());
  }

  @Test
  void contextLoads2() throws Exception{
    List<FarmDevice> result;
    boolean hasResult;

    List<FarmDevice> farmDeviceList = new ArrayList<>();
    for(int i=1; i<=30; i++){
      farmDeviceList.add(fdd.newEntity(i+"xx01", i+"상태"));
    }
    farmDeviceRepository.saveAll(farmDeviceList);

    JpaSpecificationBuilder<FarmDevice> serialQuery = JpaSpecificationBuilder.of(FarmDevice.class);
    serialQuery.where().and().eq("farmDeviceSerial", "1xx01");
    result = farmDeviceRepository.findAll(serialQuery.build());
    hasResult = result.stream().anyMatch(u -> "1xx01".equals(u.getFarmDeviceSerial()));
    assertEquals(true, hasResult);

    JpaSpecificationBuilder<FarmDevice> stateQuery = JpaSpecificationBuilder.of(FarmDevice.class);
    stateQuery.where().and().eq("farmDeviceState", "5상태");
    result = farmDeviceRepository.findAll(stateQuery.build());
    hasResult = result.stream().anyMatch(u -> "5상태".equals(u.getFarmDeviceState()));
    assertEquals(true, hasResult);
  }
  
  @Test
  void contextLoads3() throws Exception{
    // 유저 설정
    Jwt u = od.jose("user1");

    List<FarmDevice> farmDeviceList = new ArrayList<>();
    for(int i=1; i<=30; i++){
      farmDeviceList.add(fdd.newEntity(i+"xx01", i+"상태"));
    }
    farmDeviceRepository.saveAll(farmDeviceList);

    String uri = "/rest/farmDevices/search";

    // Search - 단일
    mvc.perform(post(uri).content(fdd::setSearch, "1xx01", "farmDeviceSerial").auth(u)).andExpect(is2xx());
    mvc.perform(post(uri).content(fdd::setSearch, "7상태", "farmDeviceState").auth(u)).andExpect(is2xx());

    // Search - 페이지네이션
    mvc.perform(post(uri).param("size", "6").auth(u)).andExpect(is2xx());

    // Search - 정렬
    mvc.perform(post(uri).param("sort", "farmDeviceSerial,desc").auth(u)).andExpect(is2xx());
  }
}
