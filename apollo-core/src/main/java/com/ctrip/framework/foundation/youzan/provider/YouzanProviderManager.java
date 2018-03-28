package com.ctrip.framework.foundation.youzan.provider;

import com.ctrip.framework.foundation.internals.DefaultProviderManager;
import com.ctrip.framework.foundation.internals.NullProviderManager;
import com.ctrip.framework.foundation.internals.provider.DefaultNetworkProvider;
import com.ctrip.framework.foundation.spi.ProviderManager;
import com.ctrip.framework.foundation.spi.provider.ApplicationProvider;
import com.ctrip.framework.foundation.spi.provider.NetworkProvider;
import com.ctrip.framework.foundation.spi.provider.Provider;
import com.ctrip.framework.foundation.spi.provider.ServerProvider;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create by zhangzheng on 3/26/18
 */
public class YouzanProviderManager implements ProviderManager {

  private static final Logger logger = LoggerFactory.getLogger(DefaultProviderManager.class);

  private Map<Class<? extends Provider>, Provider> m_providers =
      new LinkedHashMap<Class<? extends Provider>, Provider>();

  public YouzanProviderManager(){
    ApplicationProvider applicationProvider = new YouzanApplicationProvider();
    applicationProvider.initialize();
    m_providers.put(ApplicationProvider.class, applicationProvider);

    ServerProvider serverProvider = new YouzanServerProvider();
    serverProvider.initialize();
    m_providers.put(ServerProvider.class, serverProvider);

    NetworkProvider networkProvider = new DefaultNetworkProvider();
    networkProvider.initialize();
    m_providers.put(NetworkProvider.class, networkProvider);
  }

  @Override
  public String getProperty(String name, String defaultValue) {
    for (Provider provider : m_providers.values()) {
      String value = provider.getProperty(name, null);

      if (value != null) {
        return value;
      }
    }

    return defaultValue;

  }

  @Override
  public <T extends Provider> T provider(Class<T> clazz) {
    Provider provider = m_providers.get(clazz);

    if (provider != null) {
      return (T) provider;
    } else {
      logger.error(
          "No provider [{}] found in DefaultProviderManager, please make sure it is registered in DefaultProviderManager ",
          clazz.getName());
      return (T) NullProviderManager.provider;
    }
  }
}
