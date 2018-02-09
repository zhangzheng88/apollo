package com.ctrip.framework.apollo.foundation.internals.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.ctrip.framework.foundation.internals.provider.DefaultApplicationProvider;
import java.io.File;
import java.io.FileInputStream;
import org.junit.Before;
import org.junit.Test;

public class DefaultApplicationProviderTest {

  String PREDEFINED_APP_ID = "SampleApp";
  private DefaultApplicationProvider defaultApplicationProvider;

  @Before
  public void setUp() throws Exception {
    defaultApplicationProvider = new DefaultApplicationProvider();
  }

  @Test
  public void testLoadAppProperties() throws Exception {
    defaultApplicationProvider.initialize();

    assertEquals(PREDEFINED_APP_ID, defaultApplicationProvider.getAppId());
    assertTrue(defaultApplicationProvider.isAppIdSet());
  }


  @Test
  public void testLoadAppPropertiesWithSystemProperty() throws Exception {
    String someAppId = "someAppId";
    System.setProperty("application.name", someAppId);
    defaultApplicationProvider.initialize();
    System.clearProperty("application.name");

    assertEquals(someAppId, defaultApplicationProvider.getAppId());
    assertTrue(defaultApplicationProvider.isAppIdSet());
  }

  @Test
  public void testLoadAppPropertiesFailed() throws Exception {
    File baseDir = new File("src/test/resources/META-INF");
    File appProperties = new File(baseDir, "some-invalid-app.properties");

    defaultApplicationProvider.initialize(new FileInputStream(appProperties));

    assertEquals(null, defaultApplicationProvider.getAppId());
    assertFalse(defaultApplicationProvider.isAppIdSet());
  }
}
