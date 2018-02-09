package com.ctrip.framework.apollo.portal.spi.youzan;

import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.entity.vo.Organization;
import com.ctrip.framework.apollo.portal.spi.OrganizationService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Create by zhangzheng on 2018/1/31
 */
public class YouzanOrganizationService implements OrganizationService {

  private static String OPEN_API_BASE = "http://oa.s.qima-inc.com";
  @Autowired
  PortalConfig portalConfig;
  @Autowired
  private RestTemplate restTemplate;
  private Logger logger = LoggerFactory.getLogger(YouzanOrganizationService.class);

  @Override
  public List<Organization> loadOrgs() {
    logger.info("start to load orgs from open api");
    String url = OPEN_API_BASE + "/api/v1/departments/tree";
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("authorization", "auth " + portalConfig.getValue("openapi.appsecret"));
    HttpEntity httpEntity = new HttpEntity(httpHeaders);
    ParameterizedTypeReference<ResponseData<List<YouzanOrganization>>> typeReference = new ParameterizedTypeReference<ResponseData<List<YouzanOrganization>>>() {
    };
    ResponseEntity<ResponseData<List<YouzanOrganization>>> responseEntity = restTemplate
        .exchange(url, HttpMethod.GET, httpEntity, typeReference);
    List<YouzanOrganization> leafOrgs = new ArrayList<>();
    if (responseEntity.getBody().code == 0) {
      for (YouzanOrganization organization : responseEntity.getBody().data) {
        leafOrgs.addAll(recursion(organization, null));
      }
    }

    return leafOrgs.stream().map(YouzanOrganization::toOrganization).collect(Collectors.toList());


  }


  private List<YouzanOrganization> recursion(YouzanOrganization organization,
      List<YouzanOrganization> paths) {
    List<YouzanOrganization> organizations = new ArrayList<>();
    if (organization != null && organization.is_leaf) {
      if (paths.size() >= 2) {
        //将部门名字中加上父级部门的名字
        StringBuilder newName = new StringBuilder("");
        for (YouzanOrganization path : paths.subList(1, paths.size())) {
          newName.append(path.getName()).append("-");
        }
        newName.append(organization.getName());
        organization.setName(newName.toString());
      }
      organizations.add(organization);
      return organizations;
    }
    if (organization != null && organization.sub != null && organization.sub.size() > 0) {
      for (YouzanOrganization subOrg : organization.sub) {
        List<YouzanOrganization> copyPaths = new ArrayList<>();
        if (paths != null) {
          copyPaths.addAll(paths);
        }
        copyPaths.add(organization);
        organizations.addAll(recursion(subOrg, copyPaths));
      }
    }
    return organizations;
  }

  static class YouzanOrganization {

    int id;
    String name;
    boolean is_leaf;
    List<YouzanOrganization> sub;

    public Organization toOrganization() {
      Organization organization = new Organization();
      organization.setOrgId(String.valueOf(id));
      organization.setOrgName(name);
      return organization;
    }

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public boolean isIs_leaf() {
      return is_leaf;
    }

    public void setIs_leaf(boolean is_leaf) {
      this.is_leaf = is_leaf;
    }

    public List<YouzanOrganization> getSub() {
      return sub;
    }

    public void setSub(List<YouzanOrganization> sub) {
      this.sub = sub;
    }


  }
}
