package com.ctrip.framework.apollo.portal.controller;


import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.entity.vo.Organization;

import com.ctrip.framework.apollo.portal.spi.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RestController
@RequestMapping("/organizations")
public class OrganizationController {

  @Autowired
  OrganizationService organizationService;


  @RequestMapping
  public List<Organization> loadOrganization() {
    return organizationService.loadOrgs();
  }

}
