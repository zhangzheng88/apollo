package com.ctrip.framework.apollo.portal.spi.youzan;

import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;

/**
 * Create by zhangzheng on 2018/1/16
 */
public class YouzanUserInfoHolder implements UserInfoHolder {

  private ThreadLocal<UserInfo> userInfoThreadLocal = new ThreadLocal<>();

  @Override
  public UserInfo getUser() {
    return userInfoThreadLocal.get();
  }

  public void setUser(UserInfo userInfo) {
    userInfoThreadLocal.set(userInfo);
  }
}
