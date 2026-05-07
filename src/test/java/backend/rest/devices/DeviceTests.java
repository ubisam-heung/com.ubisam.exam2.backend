package backend.rest.devices;

import static io.u2ware.common.docs.MockMvcRestDocs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;

import backend.oauth2.Oauth2Docs;
import backend.rest.farmDeviceTypes.FarmDeviceTypeDocs;
import backend.rest.farmDevices.FarmDeviceDocs;
import io.u2ware.common.stomp.client.WebsocketStompClient;
import io.u2ware.common.stomp.client.config.WebsocketStompProperties;
import io.u2ware.common.stomp.client.handlers.StompJsonFrameHandler;

@SpringBootTest
@AutoConfigureMockMvc
public class DeviceTests {

  protected Log logger = LogFactory.getLog(getClass());

  @Autowired
  private MockMvc mvc;

  @Autowired
  private Oauth2Docs od;

  @Autowired
  private WebsocketStompProperties properties;

  @Autowired
  private FarmDeviceTypeDocs fdtd;

  @Autowired
  private FarmDeviceDocs fdd;

  @Autowired
  private WebsocketStompClient wsc;

  @Test
  void contextLoads() throws Exception{
    CompletableFuture<Void> sent = new CompletableFuture<>();
    CompletableFuture<Void> received = new CompletableFuture<>();

    // 유저 설정
    Jwt u = od.jose("admin", "ROLE_ADMIN");
    // 사전 설정
    mvc.perform(post("/rest/farmDeviceTypes").auth(u).content(fdtd::newEntity, "Thermometer", "온도계")).andExpect(is2xx()).andDo(result(fdtd::context, "fdtdEntity1")).andDo(print());
    String farmDeviceTypeLink1 = fdtd.context("fdtdEntity1", "$._links.self.href");

    Map<String, Object> req = new HashMap<>();
    req.put("title", "fddEntity1");
    req.put("farmDeviceSerial", "00xx00");
    req.put("farmDeviceState", "ACTIVE");
    req.put("farmDeviceTypeLink", farmDeviceTypeLink1);    

    mvc.perform(post("/rest/farmDevices").content(req).auth(u)).andExpect(is2xx()).andDo(result(fdd::context, "fddEntity1")).andDo(print());
    
    String farmDeviceUri = fdd.context("fddEntity1", "$._links.self.href");

    String url = properties.getUrl();

    boolean isReady = false;
    for(int i = 0; i< 50; i++){
      if(wsc.isConnected()){
        Thread.sleep(400);
        isReady = true;
        break;
      }
      Thread.sleep(100);
    }
    if(!isReady){
      logger.error("Stomp 연결 에러!");
    }

    WebsocketStompClient.withSockJS().connect(url).whenComplete((c, e) -> {
      if( e != null ){
        sent.completeExceptionally(e);
        received.completeExceptionally(e);
        return;
      }
      c.subscribe("/topic/device", new StompJsonFrameHandler() { 
        @Override
        public void handleFrame(StompHeaders headers, JsonNode payload){
          logger.info("Received Payload: "+ payload);

          JsonNode innerPayload = payload.path("payload");
          if(!innerPayload.isMissingNode()
            && innerPayload.has("Received Message")
            && innerPayload.path("status").asText("").equals("OK")
            && innerPayload.path("farmDeviceSerial").asText("").equals("00xx00")){
              received.complete(null);
            }
        }
      }).whenComplete((c1, e1) -> {
        if(e1 != null){
          sent.completeExceptionally(e1);
          received.completeExceptionally(e1);
          return;
        }

        Map<String, Object> message = new HashMap<>();
        message.put("farmDeviceSerial", "00xx00");
        message.put("value", "00 11 00 11 xx");

        c.send("/app/device", message).whenComplete((r, e2) -> {
          if(e2 != null){
            sent.completeExceptionally(e2);
            received.completeExceptionally(e2);
            return;
          }
          sent.complete(null);
        });
      });
    });

    sent.get(10, TimeUnit.SECONDS);
    received.get(10, TimeUnit.SECONDS);
    Thread.sleep(500);

    mvc.perform(post(farmDeviceUri).auth(u)).andExpect(is2xx()).andDo(result(fdd::context, "fddEntity2"));
    String farmDeviceState = fdd.context("fddEntity2", "$.farmDeviceState");
    assertEquals("01 00 11 00 AA", farmDeviceState);
  }
  
}
