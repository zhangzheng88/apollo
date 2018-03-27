package com.ctrip.framework.foundation.youzan.provider;

import com.ctrip.framework.foundation.internals.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create by zhangzheng on 2018/2/11
 */
public class YouzanConfigProvider {

  private static YouzanConfigProvider instance;
  private static Logger logger = LoggerFactory.getLogger(YouzanConfigProvider.class);
  private static final String CONFIGFILENAME = "/etc/profile.d/yzapp.sh";

  private static final String APPLICATION_NAME_KEY = "APPLICATION_NAME";
  private static final String ENV_KEY = "APPLICATION_STANDARD_ENV";
  private static final String DC_KEY = "APPLICATION_IDC";
  private static final String SERVICE_CHAIN = "APPLICATION_SERVICE_CHAIN";

  private static Boolean isIntialized = false;
  private static Properties configProperties = new Properties();



  private YouzanConfigProvider(){}

  protected static void initialize(InputStream inputStream) throws IOException {
    configProperties.load(new InputStreamReader(inputStream));
    if(!isIntialized){
      isIntialized=true;
    }

  }
  // for test
  protected static void clear(){
    configProperties.clear();
  }

  private static void initialize(){
    try {
      File file = new File(CONFIGFILENAME);
      if(file.exists() && file.canRead()){
        logger.info("loading {}", file.getAbsolutePath());
        FileInputStream fis = new FileInputStream(file);
        initialize(fis);
      }
    } catch (Exception e) {
      logger.error("load config file error", e);
    }
  }

  public static String getAppId() {
    if(!isIntialized){
      initialize();
    }
    String appId = configProperties.getProperty(APPLICATION_NAME_KEY);
    if(appId != null){
      appId = appId.trim();
    }
    return appId;
  }

  public static String getEnv() {
    if(!isIntialized){
      initialize();
    }
    String env = configProperties.getProperty(ENV_KEY);
    if(env!=null){
      env.trim();
    }
    return env;

  }

  public static String getDc() {
    if(!isIntialized){
      initialize();
    }
    String dc = configProperties.getProperty(DC_KEY);
    if(dc!=null){
      dc.trim();
    }
    return dc;
  }
  //返回sc标示，如果sc!=env, 则为真正的sc
  public static String getServiceChain(){
    if(!isIntialized){
      initialize();
    }
    String sc = configProperties.getProperty(SERVICE_CHAIN);
    if(!Utils.isBlank(sc) && sc.contains("_")){
      return sc.split("_")[0];
    }else{
      return null;
    }
  }
}
