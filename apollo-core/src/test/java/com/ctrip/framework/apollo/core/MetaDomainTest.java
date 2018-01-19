package com.ctrip.framework.apollo.core;

import com.ctrip.framework.apollo.core.enums.Env;

import org.junit.Assert;
import org.junit.Test;

public class MetaDomainTest {

  @Test
  public void testGetMetaDomain() {
    Assert.assertEquals("http://localhost:8080", MetaDomainConsts.getDomain(Env.LOCAL));
    Assert.assertEquals("http://localhost:8080", MetaDomainConsts.getDomain(Env.DEV));
    Assert.assertEquals("apollo-metaserver.s.qima-inc.com", MetaDomainConsts.getDomain(Env.PROD));
  }
}
