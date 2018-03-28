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
public class YouzanApplicationProviderTest {

  @Test
  public void testSetBySystemProperty(){
    String appKey = "application.name";
    String someAppId = "someappid";
    System.setProperty(appKey, someAppId);

    YouzanApplicationProvider youzanApplicationProvider = new YouzanApplicationProvider();
    youzanApplicationProvider.initialize();
    assertEquals(someAppId,youzanApplicationProvider.getAppId());

    System.clearProperty(appKey);
  }

  @Test
  public void testSetByServerProfile() throws IOException {
    String someAppId = "someapp";
    YouzanConfigProvider.initialize(getInputStream(someAppId));
    YouzanApplicationProvider youzanApplicationProvider = new YouzanApplicationProvider();
    youzanApplicationProvider.initialize();
    assertEquals(someAppId,youzanApplicationProvider.getAppId());

    YouzanConfigProvider.clear();
  }


  private InputStream getInputStream(String appId){
    String config = String.format("APPLICATION_STANDARD_ENV=prod\n"
        + "APPLICATION_NAME= %s\n"
        + "APPLICATION_IDC= bc\n"
        + "APPLICATION_SERVICE_CHAIN=prod", appId);
    return new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));
  }


}
