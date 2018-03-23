package com.ctrip.framework.apollo.portal.spi.youzan;

import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.spi.UserService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class YouzanUserService implements UserService {
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private PortalConfig portalConfig;

  private static String OPEN_API_BASE = "http://oa.s.qima-inc.com";


  @Override
  public List<UserInfo> searchUsers(String keyword, int offset, int limit) {
    String url = OPEN_API_BASE+"/api/v1/users/simple?keyword={keyword}&on_work=1";
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("authorization","auth "+portalConfig.getValue("openapi.appsecret","d40a15b5-024c-410e-bb6f-97cadda77148"));
    HttpEntity httpEntity = new HttpEntity(httpHeaders);
    Map<String,String> uriVariables = new HashMap<>();
    uriVariables.put("keyword",keyword);
    ParameterizedTypeReference<ResponseData<List<YouzanUserInfo>>> typeReference = new ParameterizedTypeReference<ResponseData<List<YouzanUserInfo>>>() {};
    ResponseEntity<ResponseData<List<YouzanUserInfo>>> responseEntity = restTemplate.exchange(url, HttpMethod.GET,httpEntity, typeReference,uriVariables);
    List<UserInfo> userInfos = new ArrayList<>();
    if(responseEntity.getBody()!=null && responseEntity.getBody().msg.equals("ok")){
      for(YouzanUserInfo youzanUserInfo:responseEntity.getBody().data){
        userInfos.add(youzanUserInfo.toUserInfo());
      }
    }
    if(userInfos.size()>(limit+offset)){
      userInfos = userInfos.subList(offset,offset+limit);
    }
    return userInfos;

  }

  @Override
  public UserInfo findByUserId(String userId) {
    List<UserInfo> userInfos = searchUsers(userId,0,1);
    if(userInfos.size()>0){
      return userInfos.get(0);
    }
    return null;
  }

  public List<UserInfo> findByUserIds(List<String> userIds) {
    List<UserInfo> userInfos = new ArrayList<>();
    for(String userId:userIds){
      UserInfo userInfo = findByUserId(userId);
      if(userInfo!=null){
        userInfos.add(userInfo);
      }
    }
    return userInfos;
  }


  static class YouzanUserInfo{
    private String nickname;
    private String username;
    private String realname;
    private String id;
    private String mobile;
    private String email;

    public String getNickname() {
      return nickname;
    }

    public void setNickname(String nickname) {
      this.nickname = nickname;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getRealname() {
      return realname;
    }

    public void setRealname(String realname) {
      this.realname = realname;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getMobile() {
      return mobile;
    }

    public void setMobile(String mobile) {
      this.mobile = mobile;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public UserInfo toUserInfo() {
      UserInfo userInfo = new UserInfo();
      userInfo.setUserId(nickname);
      userInfo.setName(realname);
      userInfo.setEmail(email);
      return userInfo;
    }
  }

}
