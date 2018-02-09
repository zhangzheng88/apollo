package com.ctrip.framework.apollo.adminservice.configuration;

import com.ctrip.framework.apollo.adminservice.filter.RequestLoggingFilterWithExcludedUris;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Create by zhangzheng on 2018/1/20
 */
@Configuration
public class RequestLoggingFilterConfig {

  @Bean
  public FilterRegistrationBean logFilter() {

    RequestLoggingFilterWithExcludedUris filter
        = new RequestLoggingFilterWithExcludedUris();
    filter.setIncludeQueryString(true);
    filter.setIncludePayload(true);
    filter.setMaxPayloadLength(10000);
    filter.addExcludedUri("/health");

    FilterRegistrationBean filterBean = new FilterRegistrationBean();
    filterBean.setFilter(filter);
    filterBean.addUrlPatterns("/*");
    return filterBean;
  }
}
