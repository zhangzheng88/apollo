package com.ctrip.framework.apollo.foundation.internals.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ctrip.framework.foundation.internals.provider.DefaultServerProvider;


public class DefaultServerProviderTest {
  private DefaultServerProvider defaultServerProvider;
  private String PREDEFINED_ENV="dev";
  private String PREDEFINED_CLUSTER="default";

  @Before
  public void setUp() throws Exception {
    cleanUp();
    defaultServerProvider = new DefaultServerProvider();
  }

  @After
  public void tearDown() throws Exception {
    cleanUp();
  }

  private void cleanUp() {
    System.clearProperty("apollo.env");
    System.clearProperty("apollo.cluster");
  }

  @Test
  public void testEnvWithSystemProperty() throws Exception {
    String someEnv = "someEnv";
    String someDc = "someDc";
    System.setProperty("apollo.env", someEnv);
    System.setProperty("apollo.cluster", someDc);

    defaultServerProvider.initialize(null);

    assertEquals(someEnv, defaultServerProvider.getEnvType());
    assertEquals(someDc, defaultServerProvider.getDataCenter());
  }

  @Test
  public void testWithPropertiesStream() throws Exception {
    File baseDir = new File("src/test/resources/");
    File serverProperties = new File(baseDir, "container.properties");
    defaultServerProvider.initialize(new FileInputStream(serverProperties));

    assertEquals(PREDEFINED_CLUSTER, defaultServerProvider.getDataCenter());
    assertTrue(defaultServerProvider.isEnvTypeSet());
    assertEquals(PREDEFINED_ENV, defaultServerProvider.getEnvType());
  }

  @Test
  public void testWithDefaultPropertyFile() throws Exception {
    defaultServerProvider.initialize();

    assertEquals(PREDEFINED_CLUSTER, defaultServerProvider.getDataCenter());
    assertTrue(defaultServerProvider.isEnvTypeSet());
    assertEquals(PREDEFINED_ENV, defaultServerProvider.getEnvType());
  }



  @Test
  public void testWithPropertiesStreamAndEnvFromSystemProperty() throws Exception {
    String prodEnv = "pro";
    System.setProperty("apollo.env", prodEnv);

    defaultServerProvider.initialize();


    assertEquals(PREDEFINED_CLUSTER, defaultServerProvider.getDataCenter());
    assertTrue(defaultServerProvider.isEnvTypeSet());
    assertEquals(prodEnv, defaultServerProvider.getEnvType());
  }

  @Test
  public void testWithNoPropertiesStream() throws Exception {
    defaultServerProvider.initialize(null);

    assertNull(defaultServerProvider.getDataCenter());
    assertFalse(defaultServerProvider.isEnvTypeSet());
    assertNull(defaultServerProvider.getEnvType());
  }
}
