package com.ctrip.framework.foundation.internals.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.foundation.internals.Utils;
import com.ctrip.framework.foundation.internals.io.BOMInputStream;
import com.ctrip.framework.foundation.spi.provider.Provider;
import com.ctrip.framework.foundation.spi.provider.ServerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultServerProvider implements ServerProvider {
  private static final Logger logger = LoggerFactory.getLogger(DefaultServerProvider.class);
  private static final String APP_PROPERTIES_CLASSPATH = "/container.properties";

  private String m_env;
  private String m_dc;

  private Properties m_serverProperties = new Properties();

  @Override
  public void initialize() {
    try {
      InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_PROPERTIES_CLASSPATH);
      if (in == null) {
        in = DefaultServerProvider.class.getResourceAsStream(APP_PROPERTIES_CLASSPATH);
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
          m_serverProperties.load(new InputStreamReader(new BOMInputStream(in), StandardCharsets.UTF_8));
        } finally {
          in.close();
        }
      }

      initEnvType();
      initDataCenter();
    } catch (Throwable ex) {
      logger.error("Initialize DefaultServerProvider failed.", ex);
    }
  }

  @Override
  public String getDataCenter() {
    return m_dc;
  }

  @Override
  public boolean isDataCenterSet() {
    return m_dc != null;
  }

  @Override
  public String getEnvType() {
    return m_env;
  }

  @Override
  public boolean isEnvTypeSet() {
    return m_env != null;
  }

  @Override
  public String getProperty(String name, String defaultValue) {
    if ("apollo.env".equalsIgnoreCase(name)) {
      String val = getEnvType();
      return val == null ? defaultValue : val;
    } else if ("apollo.cluster".equalsIgnoreCase(name)) {
      String val = getDataCenter();
      return val == null ? defaultValue : val;
    } else {
      String val = m_serverProperties.getProperty(name, defaultValue);
      return val == null ? defaultValue : val.trim();
    }
  }

  @Override
  public Class<? extends Provider> getType() {
    return ServerProvider.class;
  }

  private void initEnvType() {
    // 1. Try to get environment from JVM system property
    m_env = System.getProperty("apollo.env");
    if (!Utils.isBlank(m_env)) {
      m_env = m_env.trim();
      logger.info("Environment is set to [{}] by JVM system property 'env'.", m_env);
      return;
    }

//    // 2. Try to get environment from OS environment variable
//    m_env = System.getenv("ENV");
//    if (!Utils.isBlank(m_env)) {
//      m_env = m_env.trim();
//      logger.info("Environment is set to [{}] by OS env variable 'ENV'.", m_env);
//      return;
//    }

    // 3. Try to get environment from file "container.properties"
    m_env = m_serverProperties.getProperty("apollo.env");
    if (!Utils.isBlank(m_env)) {
      m_env = m_env.trim();
      logger.info("Environment is set to [{}] by property 'apollo.env' in container.properties.", m_env);
      return;
    }

    // try to get environment from os environment variable
    m_env = System.getenv("APPLICATION_STANDARD_ENV");
    if (!Utils.isBlank(m_env)) {
      m_env = convertEnv(m_env);
      logger.info("Environment is set to [{}] by OS env variable 'ENV'.", m_env);
      return;
    }


    // 4. Set environment to default daily.
    m_env = "daily";
    logger.warn("Environment is set to null. Because it is not available in either (1) JVM system property 'env', (2) OS env variable 'ENV' nor (3) property 'env' from the properties InputStream.");
  }

  private String convertEnv(String m_env){
    //由于apollo只支持三个环境，所以将线上的perf转成qa，将per转成prod
    switch (m_env.trim().toUpperCase()) {
      case "PERF":
        return "qa";
      case "PRE":
        return "prod";
      default:
        return m_env;
    }
  }

  private void initDataCenter() {
    // 1. Try to get environment from JVM system property
    m_dc = System.getProperty("apollo.cluster");
    if (!Utils.isBlank(m_dc)) {
      m_dc = m_dc.trim();
      logger.info("Data Center is set to [{}] by JVM system property 'idc'.", m_dc);
      return;
    }

    // 2. Try to get idc from OS environment variable
//    m_dc = System.getenv("IDC");
//    if (!Utils.isBlank(m_dc)) {
//      m_dc = m_dc.trim();
//      logger.info("Data Center is set to [{}] by OS env variable 'IDC'.", m_dc);
//      return;
//    }

    // 3. Try to get idc from from file "container.properties"
    m_dc = m_serverProperties.getProperty("apollo.cluster");
    if (!Utils.isBlank(m_dc)) {
      m_dc = m_dc.trim();
      logger.info("Data Center is set to [{}] by property 'apollo.cluster' in container.properties.", m_dc);
      return;
    }
//     2. Try to get idc from OS environment variable
    m_dc = System.getenv("APPLICATION_IDC");
    if (!Utils.isBlank(m_dc)) {
      m_dc = m_dc.trim();
      logger.info("Data Center is set to [{}] by OS env variable 'IDC'.", m_dc);
      return;
    }

    // 4. Set Data Center to null.
    m_dc = null;
    logger.debug("Data Center is set to null. Because it is not available in either (1) JVM system property 'idc', (2) OS env variable 'IDC' nor (3) property 'idc' from the properties InputStream.");
  }

  @Override
  public String toString() {
    return "environment [" + getEnvType() + "] data center [" + getDataCenter() + "] properties: " + m_serverProperties
        + " (DefaultServerProvider)";
  }
}
