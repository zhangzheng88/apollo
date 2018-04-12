package com.ctrip.framework.apollo.opensdk;

import java.io.IOException;
import java.util.Properties;

/**
 * Create by zhangzheng on 4/11/18
 */
public class ConfigReader {

  private static Properties properties;

  static {
    try {
      properties = new Properties();
      properties.load(ConfigReader.class.getResourceAsStream("/opensdk.properties"));
    } catch (IOException e) {
      throw new RuntimeException("cat not find opensdk config file");
    }
  }

  public static String portalUrl(){
    return properties.getProperty("portalUrl");
  }

  public static String getProperty(String key){
    return properties.getProperty(key);
  }

}
