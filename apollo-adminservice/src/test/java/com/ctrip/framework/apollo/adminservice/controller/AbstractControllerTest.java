package com.ctrip.framework.apollo.adminservice.controller;

import com.ctrip.framework.apollo.AdminServiceTestConfiguration;
import javax.annotation.PostConstruct;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdminServiceTestConfiguration.class)
@WebIntegrationTest(randomPort = true)
public abstract class AbstractControllerTest {

  RestTemplate restTemplate = new TestRestTemplate();
  @Value("${local.server.port}")
  int port;
  @Autowired
  private HttpMessageConverters httpMessageConverters;

  @PostConstruct
  private void postConstruct() {
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
    restTemplate.setMessageConverters(httpMessageConverters.getConverters());
  }
}
