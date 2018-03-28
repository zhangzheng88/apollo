package com.ctrip.framework.foundation.youzan.provider;

import com.ctrip.framework.foundation.internals.Utils;
import com.ctrip.framework.foundation.spi.provider.Provider;
import com.ctrip.framework.foundation.spi.provider.ServerProvider;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create by zhangzheng on 3/26/18
 */
public class YouzanServerProvider implements ServerProvider{

  private Logger logger = LoggerFactory.getLogger(YouzanServerProvider.class);

  @Override
  public String getEnvType() {
    return m_env;
  }

  @Override
  public boolean isEnvTypeSet() {
    return m_env != null;
  }

  @Override
  public String getDataCenter() {
    return m_dc;
  }

  @Override
  public boolean isDataCenterSet() {
    return m_dc != null;
  }

  @Override
  public void initialize(InputStream in){
    initEnvAndCluster();
  }

  private String zan_env;
  private void initZanEnv() {
    // 1. Try to get environment from JVM system property
    zan_env = System.getProperty("apollo.env");
    if (!Utils.isBlank(zan_env)) {
      zan_env = zan_env.trim();
      logger.info("zan environment is set to [{}] by JVM system property 'apollo.env'.", zan_env);
      return;
    }

    // try to get environment from os environment variable
    zan_env = System.getenv("APPLICATION_STANDARD_ENV");
    if (!Utils.isBlank(zan_env)) {
      logger.info("zan environment is set to [{}] by OS env variable 'APPLICATION_STANDARD_ENV'.",
          zan_env);
      return;
    }

    //try to get environment from os file
    zan_env = YouzanConfigProvider.getEnv();
    if(!Utils.isBlank(zan_env)){
      logger.info("zan environment is set to [{}] by OS file", zan_env);
      return;
    }

    // 4. Set environment to default daily.
    zan_env = "dev";
    logger.warn("zan environment is set to [{}]",zan_env);
  }

  private String m_dc;
  private String m_env;
  private void initEnvAndCluster(){
    initZanEnv();
    switch (zan_env.toUpperCase().trim()){
      case "DEV":
        m_env = "daily";
        m_dc = "dev";
        break;
      case "DAILY":
        m_env = "daily";
        m_dc = getSC();//daily环境集群名默认去sc
        break;
      case "QA":
        m_env = "qa";
        m_dc = getSC();//qa环境集群名默认去sc
        break;
      case "PERF":
        m_env = "qa";
        m_dc = "perf";
        break;
      case "PRE":
        m_env = "prod";
        m_dc = "pre";
        break;
      case "PROD":
        m_env = "prod";
        m_dc = getProdIdc();
        break;
    }
    String idc = System.getProperty("apollo.cluster");
    if (!Utils.isBlank(idc)) {
      m_dc = idc.trim();//如果程序自定义了cluster，那么就使用程序自定义的cluster
    }
    logger.info("apollo env is set to [{}], apollo cluster is set to [{}]",m_env, m_dc);
  }

  private String getProdIdc(){
    String idc = System.getenv("APPLICATION_IDC");
    if (!Utils.isBlank(idc)) {
      return idc.trim();
    }

    // try to get idc from os file
    idc = YouzanConfigProvider.getDc();
    if(!Utils.isBlank(idc)){
      return idc.trim();
    }
    return "default";
  }

  private String getSC(){
    String sc = System.getenv("APPLICATION_SERVICE_CHAIN");
    if (!Utils.isBlank(sc)) {
      return sc.trim();
    }

    // try to get idc from os file
    sc = YouzanConfigProvider.getServiceChain();
    if(!Utils.isBlank(sc)){
      return sc.trim();
    }
    return "default";

  }


  @Override
  public Class<? extends Provider> getType() {
    return ServerProvider.class;
  }

  @Override
  public String getProperty(String name, String defaultValue) {
    return null;
  }

  @Override
  public void initialize() {
    initialize(null);
  }
}
