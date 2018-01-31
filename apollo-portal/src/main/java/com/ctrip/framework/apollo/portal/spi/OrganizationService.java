package com.ctrip.framework.apollo.portal.spi;

import com.ctrip.framework.apollo.portal.entity.vo.Organization;

import java.util.List;

/**
 * Create by zhangzheng on 2018/1/31
 */
public interface OrganizationService {

    List<Organization> loadOrgs();

}
