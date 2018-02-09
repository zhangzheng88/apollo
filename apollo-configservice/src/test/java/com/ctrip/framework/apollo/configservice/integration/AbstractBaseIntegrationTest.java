package com.ctrip.framework.apollo.configservice.integration;

import com.ctrip.framework.apollo.ConfigServiceTestConfiguration;
import com.ctrip.framework.apollo.biz.config.BizConfig;
import com.ctrip.framework.apollo.biz.entity.Namespace;
import com.ctrip.framework.apollo.biz.entity.Release;
import com.ctrip.framework.apollo.biz.entity.ReleaseMessage;
import com.ctrip.framework.apollo.biz.repository.ReleaseMessageRepository;
import com.ctrip.framework.apollo.biz.repository.ReleaseRepository;
import com.ctrip.framework.apollo.biz.utils.ReleaseKeyGenerator;
import com.google.gson.Gson;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AbstractBaseIntegrationTest.TestConfiguration.class)
@WebIntegrationTest(randomPort = true)
public abstract class AbstractBaseIntegrationTest {

  RestTemplate restTemplate = new TestRestTemplate();
  @Value("${local.server.port}")
  int port;
  @Autowired
  private ReleaseMessageRepository releaseMessageRepository;
  @Autowired
  private ReleaseRepository releaseRepository;
  private Gson gson = new Gson();

  @PostConstruct
  private void postConstruct() {
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
  }

  protected String getHostUrl() {
    return "http://localhost:" + port;
  }

  protected void sendReleaseMessage(String message) {
    ReleaseMessage releaseMessage = new ReleaseMessage(message);
    releaseMessageRepository.save(releaseMessage);
  }

  public Release buildRelease(String name, String comment, Namespace namespace,
      Map<String, String> configurations, String owner) {
    Release release = new Release();
    release.setReleaseKey(ReleaseKeyGenerator.generateReleaseKey(namespace));
    release.setDataChangeCreatedTime(new Date());
    release.setDataChangeCreatedBy(owner);
    release.setDataChangeLastModifiedBy(owner);
    release.setName(name);
    release.setComment(comment);
    release.setAppId(namespace.getAppId());
    release.setClusterName(namespace.getClusterName());
    release.setNamespaceName(namespace.getNamespaceName());
    release.setConfigurations(gson.toJson(configurations));
    release = releaseRepository.save(release);

    return release;
  }

  protected void periodicSendMessage(ExecutorService executorService, String message,
      AtomicBoolean stop) {
    executorService.submit((Runnable) () -> {
      //wait for the request connected to server
      while (!stop.get() && !Thread.currentThread().isInterrupted()) {
        try {
          TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
        }

        //double check
        if (stop.get()) {
          break;
        }

        sendReleaseMessage(message);
      }
    });
  }

  @Configuration
  @Import(ConfigServiceTestConfiguration.class)
  protected static class TestConfiguration {

    @Bean
    public BizConfig bizConfig() {
      return new TestBizConfig();
    }
  }

  private static class TestBizConfig extends BizConfig {

    @Override
    public int appNamespaceCacheScanInterval() {
      return 50;
    }

    @Override
    public TimeUnit appNamespaceCacheScanIntervalTimeUnit() {
      return TimeUnit.MILLISECONDS;
    }
  }
}
