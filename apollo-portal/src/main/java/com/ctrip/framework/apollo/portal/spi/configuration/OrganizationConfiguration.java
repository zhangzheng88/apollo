package com.ctrip.framework.apollo.portal.spi.configuration;

import com.ctrip.framework.apollo.common.condition.ConditionalOnMissingProfile;
import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.spi.defaultimpl.DefaultOrganizationService;
import com.ctrip.framework.apollo.portal.spi.youzan.YouzanOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Create by zhangzheng on 2018/1/31
 */
@Configuration
public class OrganizationConfiguration {


  @Configuration
  @Profile("youzan")
  static class YouzanOrganizationConfiguration {

    @Bean
    public YouzanOrganizationService youzanOrganizationService() {
      return new YouzanOrganizationService();
    }

  }

  @Configuration
  @ConditionalOnMissingProfile("youzan")
  static class DefaultOrganizationConfiguration {

    @Autowired
    PortalConfig portalConfig;

    @Bean
    public DefaultOrganizationService defaultOrganizationConfiguration() {
      return new DefaultOrganizationService(portalConfig);
    }

  }
}
