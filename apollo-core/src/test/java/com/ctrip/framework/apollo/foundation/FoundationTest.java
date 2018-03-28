package com.ctrip.framework.apollo.foundation;

import com.ctrip.framework.foundation.Foundation;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class FoundationTest {

  private static final String someEnv = "dev";
  private static final String someApp = "SampleApp";

  @BeforeClass
  public static void before() {
    System.setProperty("application.name", someApp);
    System.setProperty("apollo.env", someEnv);
  }

  @AfterClass
  public static void afterClass() {
    System.clearProperty("apollo.env");
    System.clearProperty("application.name");
  }

  @Test
  public void testApp() {
    // 获取AppId
    String appId = Foundation.app().getAppId();
    Assert.assertEquals(someApp, appId);
  }

  @Test
  public void testServer() {
    // 获取当前环境
    String envType = Foundation.server().getEnvType();
    String cluster = Foundation.server().getDataCenter();
    Assert.assertEquals("daily", envType);
    Assert.assertEquals("dev", cluster);
  }

  @Test
  public void testNet() {
    // 获取本机IP和HostName
    String hostAddress = Foundation.net().getHostAddress();
    String hostName = Foundation.net().getHostName();

    Assert.assertNotNull("No host address detected.", hostAddress);
    Assert.assertNotNull("No host name resolved.", hostName);
  }

}
