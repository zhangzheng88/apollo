package com.ctrip.framework.foundation.youzan.provider;

import com.ctrip.framework.foundation.internals.Utils;
import com.ctrip.framework.foundation.spi.provider.ApplicationProvider;
import com.ctrip.framework.foundation.spi.provider.Provider;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create by zhangzheng on 3/26/18
 */
public class YouzanApplicationProvider implements ApplicationProvider {

  private Logger logger = LoggerFactory.getLogger(YouzanApplicationProvider.class);

  @Override
  public String getAppId() {
    return m_appId;
  }

  @Override
  public boolean isAppIdSet() {
    return m_appId != null;
  }

  @Override
  public void initialize(InputStream in) {
    initAppId();
  }

  private String m_appId = null;
  private void initAppId() {
    // 1. Get app.id from System Property
    m_appId = System.getProperty("application.name");
    if (!Utils.isBlank(m_appId)) {
      m_appId = m_appId.trim();
      logger.info("App ID is set to {} by app.id property from System Property", m_appId);
      return;
    }

    //3. Try to get app id from os environment variables, for docker server
    m_appId = System.getenv("APPLICATION_NAME");
    if (!Utils.isBlank(m_appId)) {
      m_appId = m_appId.trim();
      logger.info("App ID is set to {} by OS env variable APPLICATION_NAME", m_appId);
      return;
    }

    //3. Try to get app id from os file, for vm server
    m_appId = YouzanConfigProvider.getAppId();
    if (!Utils.isBlank(m_appId)) {
      m_appId = m_appId.trim();
      logger.info("App ID is set to {} by os file", m_appId);
      return;
    }

    m_appId = null;
    logger.warn("app.id is not available from System Property. It is set to null");
  }

  @Override
  public Class<? extends Provider> getType() {
    return ApplicationProvider.class;
  }

  @Override
  public String getProperty(String name, String defaultValue) {
    return null;
  }

  @Override
  public void initialize() {
    initialize(null);
  }
}
