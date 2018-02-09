package com.ctrip.framework.apollo.portal.spi.defaultimpl;

import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.entity.vo.Organization;
import com.ctrip.framework.apollo.portal.spi.OrganizationService;
import java.util.List;

/**
 * Create by zhangzheng on 2018/1/31
 */
public class DefaultOrganizationService implements OrganizationService {

  private PortalConfig portalConfig;

  public DefaultOrganizationService(PortalConfig portalConfig) {
    this.portalConfig = portalConfig;
  }

  @Override
  public List<Organization> loadOrgs() {
    return portalConfig.organizations();
  }

}
