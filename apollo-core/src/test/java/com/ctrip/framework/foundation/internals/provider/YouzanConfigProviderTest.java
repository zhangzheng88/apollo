package com.ctrip.framework.foundation.internals.provider;

import static org.junit.Assert.*;

import java.io.InputStream;
import org.junit.Test;

/**
 * Create by zhangzheng on 2018/2/11
 */
public class YouzanConfigProviderTest {

  @Test
  public void initialize() throws Exception {
    InputStream inputStream = YouzanConfigProviderTest.class.getResourceAsStream("/youzan-config.properties");
    YouzanConfigProvider.initialize(inputStream);
    assertEquals("prod", YouzanConfigProvider.getEnv());
    assertEquals("account", YouzanConfigProvider.getAppId());
    assertEquals("bc", YouzanConfigProvider.getDc());
  }

}
