package com.ctrip.framework.apollo.opensdk;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Create by zhangzheng on 3/17/18
 */
public class HttpUtilTest {

  @Test
  public void testUrlParse(){
    String appId = "demo";
    String clusterName = "default";
    String namespaceName = "application";
    String PORTAL_URL = "12233";
    String env = "env";
    Map<String,String> uriVariables = ImmutableMap
        .of("appId",appId,"clusterName",clusterName,"namespaceName", namespaceName,
            "portal_address", PORTAL_URL, "env",env);
    String url = "http://{portal_address}/openapi/v1/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/items";
    String parsedUrl = HttpUtil.parseUrlTemplate(url, uriVariables);
    String expectedUrl = String.format("http://%s/openapi/v1/envs/%s/apps/%s/clusters/%s/namespaces/%s/items", PORTAL_URL, env, appId, clusterName, namespaceName);
    assertEquals(parsedUrl, expectedUrl);
  }


}
