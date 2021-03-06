package com.ctrip.framework.foundation.internals.provider;

import com.ctrip.framework.foundation.internals.Utils;
import com.ctrip.framework.foundation.internals.io.BOMInputStream;
import com.ctrip.framework.foundation.spi.provider.ApplicationProvider;
import com.ctrip.framework.foundation.spi.provider.Provider;
import com.ctrip.framework.foundation.youzan.provider.YouzanConfigProvider;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultApplicationProvider implements ApplicationProvider {

  public static final String APP_PROPERTIES_CLASSPATH = "/container.properties";
  private static final Logger logger = LoggerFactory.getLogger(DefaultApplicationProvider.class);
  private Properties m_appProperties = new Properties();

  private String m_appId;

  @Override
  public void initialize() {
    try {
      InputStream in = Thread.currentThread().getContextClassLoader()
          .getResourceAsStream(APP_PROPERTIES_CLASSPATH);
      if (in == null) {
        in = DefaultApplicationProvider.class.getResourceAsStream(APP_PROPERTIES_CLASSPATH);
      }

      if (in == null) {
        logger.warn("{} not found from classpath!", APP_PROPERTIES_CLASSPATH);
      }
      initialize(in);
    } catch (Throwable ex) {
      logger.error("Initialize DefaultApplicationProvider failed.", ex);
    }
  }

  @Override
  public void initialize(InputStream in) {
    try {
      if (in != null) {
        try {
          m_appProperties
              .load(new InputStreamReader(new BOMInputStream(in), StandardCharsets.UTF_8));
        } finally {
          in.close();
        }
      }

      initAppId();
    } catch (Throwable ex) {
      logger.error("Initialize DefaultApplicationProvider failed.", ex);
    }
  }

  @Override
  public String getAppId() {
    return m_appId;
  }

  @Override
  public boolean isAppIdSet() {
    return !Utils.isBlank(m_appId);
  }

  @Override
  public String getProperty(String name, String defaultValue) {
    if ("application.name".equals(name)) {
      String val = getAppId();
      return val == null ? defaultValue : val;
    } else {
      String val = m_appProperties.getProperty(name, defaultValue);
      return val == null ? defaultValue : val;
    }
  }

  @Override
  public Class<? extends Provider> getType() {
    return ApplicationProvider.class;
  }

  private void initAppId() {
    // 1. Get app.id from System Property
    m_appId = System.getProperty("application.name");
    if (!Utils.isBlank(m_appId)) {
      m_appId = m_appId.trim();
      logger.info("App ID is set to {} by app.id property from System Property", m_appId);
      return;
    }

    // 2. Try to get app id from container.properties.
    m_appId = m_appProperties.getProperty("application.name");
    if (!Utils.isBlank(m_appId)) {
      m_appId = m_appId.trim();
      logger.info("App ID is set to {} by app.id property from {}", m_appId,
          APP_PROPERTIES_CLASSPATH);
      return;
    }

    //3. Try to get app id from os environment variables
    m_appId = System.getenv("APPLICATION_NAME");
    if (!Utils.isBlank(m_appId)) {
      m_appId = m_appId.trim();
      logger.info("App ID is set to {} by OS env variable APPLICATION_NAME", m_appId);
      return;
    }

    //3. Try to get app id from os file
    m_appId = YouzanConfigProvider.getAppId();
    if(!Utils.isBlank(m_appId)){
      m_appId = m_appId.trim();
      logger.info("App ID is set to {} by os file", m_appId);
      return;
    }

    m_appId = null;
    logger.warn("app.id is not available from System Property and {}. It is set to null",
        APP_PROPERTIES_CLASSPATH);
  }

  @Override
  public String toString() {
    return "appId [" + getAppId() + "] properties: " + m_appProperties
        + " (DefaultApplicationProvider)";
  }
}
