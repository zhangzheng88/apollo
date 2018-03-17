package com.ctrip.framework.apollo.opensdk;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by zhangzheng on 3/17/18
 */
public class HttpUtil {

  private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");


  public static  String parseUrlTemplate(String urlTemplate, Map<String,String> uriVariables) {
    if (urlTemplate == null) {
      return null;
    }
    if (urlTemplate.indexOf('{') == -1) {
      return urlTemplate;
    }

    Matcher matcher = NAMES_PATTERN.matcher(urlTemplate);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      String match = matcher.group(1);
      String variableName = getVariableName(match);
      String variableValue = uriVariables.get(variableName);

      String replacement = Matcher.quoteReplacement(variableValue);
      matcher.appendReplacement(sb, replacement);
    }
    matcher.appendTail(sb);
    return sb.toString();
  }
  private static  String getVariableName(String match) {
    int colonIdx = match.indexOf(':');
    return (colonIdx != -1 ? match.substring(0, colonIdx) : match);
  }

}
