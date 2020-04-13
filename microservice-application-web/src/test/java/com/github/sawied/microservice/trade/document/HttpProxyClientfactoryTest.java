package com.github.sawied.microservice.trade.document;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import com.github.sawied.microservice.trade.Application;
import com.github.sawied.microservice.trade.service.RemoteApiService;



@RunWith(SpringJUnit4ClassRunner.class)
@RestClientTest(components={RemoteApiService.class})
@AutoConfigureWebClient(registerRestTemplate=true)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK,classes= {Application.class})
public class HttpProxyClientfactoryTest {

    @Autowired
    private RemoteApiService service;

    @Autowired
    private MockRestServiceServer server;
    
 
    
    @Test
    public void getVehicleDetailsWhenResultIsSuccessShouldReturnDetails()
            throws Exception {
        this.server.expect(requestTo("http://localhost/api"))
                .andRespond(withSuccess("hello", MediaType.TEXT_PLAIN));
        String greeting = this.service.getRestApiResponse(1L);
       Assert.assertEquals("hello", greeting);
    }
}
