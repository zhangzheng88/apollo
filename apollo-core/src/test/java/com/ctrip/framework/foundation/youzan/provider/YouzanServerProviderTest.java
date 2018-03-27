package com.ctrip.framework.foundation.youzan.provider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Create by zhangzheng on 3/27/18
 */
public class YouzanServerProviderTest {

  @Test
  public void testProd() throws IOException {
    String somEnv = "prod";
    String someIdc = "bc";
    String someSc = "prj111_qa";
    String someAppId = "appname1";
    YouzanConfigProvider.initialize(getInputStream(somEnv,someIdc,someAppId,someSc));
    YouzanServerProvider youzanServerProvider = new YouzanServerProvider();
    youzanServerProvider.initialize();
    assertEquals("prod", youzanServerProvider.getEnvType());
    assertEquals("bc", youzanServerProvider.getDataCenter());
  }
  @Test
  public void testQA() throws IOException {
    String somEnv = "qa";
    String someIdc = "bc";
    String someSc = "prj111_qa";
    String someAppId = "appname1";
    YouzanConfigProvider.initialize(getInputStream(somEnv,someIdc,someAppId,someSc));
    YouzanServerProvider youzanServerProvider = new YouzanServerProvider();
    youzanServerProvider.initialize();
    assertEquals("qa", youzanServerProvider.getEnvType());
    assertEquals("prj111", youzanServerProvider.getDataCenter());
  }

  @Test
  public void testPerf() throws IOException {
    String somEnv = "perf";
    String someIdc = "bc";
    String someSc = "prj111_qa";
    String someAppId = "appname1";
    YouzanConfigProvider.initialize(getInputStream(somEnv,someIdc,someAppId,someSc));
    YouzanServerProvider youzanServerProvider = new YouzanServerProvider();
    youzanServerProvider.initialize();
    assertEquals("qa", youzanServerProvider.getEnvType());
    assertEquals("perf", youzanServerProvider.getDataCenter());
  }

  @Test
  public void testDefault() throws IOException {
    YouzanConfigProvider.clear();
    YouzanServerProvider youzanServerProvider = new YouzanServerProvider();
    youzanServerProvider.initialize();
    assertEquals("daily", youzanServerProvider.getEnvType());
    assertEquals("dev", youzanServerProvider.getDataCenter());
  }
  @Test
  public void testPre() throws IOException {
    String somEnv = "pre";
    String someIdc = "bc";
    String someSc = "prj111_qa";
    String someAppId = "appname1";
    YouzanConfigProvider.initialize(getInputStream(somEnv,someIdc,someAppId,someSc));
    YouzanServerProvider youzanServerProvider = new YouzanServerProvider();
    youzanServerProvider.initialize();
    assertEquals("prod", youzanServerProvider.getEnvType());
    assertEquals("pre", youzanServerProvider.getDataCenter());
  }

  @Test
  public void testDaily() throws IOException {
    String somEnv = "daily";
    String someIdc = "bc";
    String someSc = "prj111_qa";
    String someAppId = "appname1";
    YouzanConfigProvider.initialize(getInputStream(somEnv,someIdc,someAppId,someSc));
    YouzanServerProvider youzanServerProvider = new YouzanServerProvider();
    youzanServerProvider.initialize();
    assertEquals("daily", youzanServerProvider.getEnvType());
    assertEquals("prj111", youzanServerProvider.getDataCenter());
  }

  @Test
  public void testSystemProperty() throws IOException {
    String somEnv = "daily";
    String someIdc = "bc";
    String someSc = "prj111_qa";
    String someAppId = "appname1";
    String someCluster = "cluster";
    System.setProperty("apollo.cluster",someCluster);
    YouzanConfigProvider.initialize(getInputStream(somEnv,someIdc,someAppId,someSc));
    YouzanServerProvider youzanServerProvider = new YouzanServerProvider();
    youzanServerProvider.initialize();
    assertEquals("daily", youzanServerProvider.getEnvType());
    assertEquals(someCluster, youzanServerProvider.getDataCenter());
    System.clearProperty("apollo.cluster");

  }



  private InputStream getInputStream(String env,String cluster,String appId,String sc){
    String config = String.format("APPLICATION_STANDARD_ENV=%s\n"
        + "APPLICATION_NAME= %s\n"
        + "APPLICATION_IDC= %s\n"
        + "APPLICATION_SERVICE_CHAIN=%s", env, appId, cluster, sc);
    return new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));
  }

}
