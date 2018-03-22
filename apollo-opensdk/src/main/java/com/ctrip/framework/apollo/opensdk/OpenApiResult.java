package com.ctrip.framework.apollo.opensdk;

/**
 * Create by zhangzheng on 2018/3/14
 */
public class OpenApiResult<T> {

  public static final int SUCCESS = 0;
  public static final int FAIL = 1;

  T body;
  String errmsg;
  int code;

  public T getBody() {
    return body;
  }

  public String getErrmsg() {
    return errmsg;
  }


  public int getCode() {
    return code;
  }

}
