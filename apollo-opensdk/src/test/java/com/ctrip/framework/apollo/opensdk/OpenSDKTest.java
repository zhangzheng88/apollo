package com.ctrip.framework.apollo.opensdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.ctrip.framework.apollo.opensdk.dto.OpenItemDTO;
import com.ctrip.framework.apollo.opensdk.dto.OpenNamespaceDTO;
import com.ctrip.framework.apollo.opensdk.dto.OpenReleaseDTO;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Create by zhangzheng on 2018/3/10
 */
public class OpenSDKTest {

  private static String token;

  @BeforeClass
  public static void setUp(){
    token = ConfigReader.getProperty("token");
  }

  @Test
  public void testCreateInstance(){
    String someAppId = "someAppId";
    String someNamespaceName = "someNamespaceName";
    String someToken = "token";
    NamespaceManager instance = NamespaceManager.builder().appId(someAppId)
        .namespace(someNamespaceName).token(someToken).build();
    assertNotNull(instance);
    assertEquals(someAppId, instance.appId);
    assertEquals("dev", instance.clusterName);
    assertEquals(someNamespaceName, instance.namespaceName);
    assertEquals(someToken, instance.token);
    assertEquals("DAILY", instance.env.name());
    assertEquals("opensdk", instance.dataChangedBy);
  }


  @Test(expected = Exception.class)
  public void testCreateItemWithNoAuth(){
    String someAppId = "demo";
    String someNamespaceName = "application";
    NamespaceManager instance = NamespaceManager.builder().appId(someAppId)
        .namespace(someNamespaceName).build();
    OpenApiResult<OpenItemDTO> result = instance.createItem("testKey","testValue","comment");
  }



  @Test
  public void testCURD(){
    testCreateItemWithAuth();
    testUpdateItem();
    testDeleteItem();
    testReleaseItem();
  }


  private void testCreateItemWithAuth(){
    String someAppId = "demo";
    String someNamespaceName = "application";
    NamespaceManager instance = NamespaceManager.builder().appId(someAppId)
        .namespace(someNamespaceName).token(token).build();
    OpenApiResult<OpenItemDTO> result = instance.createItem("testKey", "testValue", "comment");
    assertEquals(result.code, OpenApiResult.SUCCESS);
    assertNotNull(result.body);
    assertNull(result.errmsg);
  }


  private void testUpdateItem(){
    String someAppId = "demo";
    String someNamespaceName = "application";
    NamespaceManager instance = NamespaceManager.builder().appId(someAppId)
        .namespace(someNamespaceName).token(token).build();
    String key = "testKey";
    String newValue = "newValue";
    String comment = "openapiUpdateItem";
    OpenApiResult result = instance.updateItem(key, newValue, comment);
    assertEquals(OpenApiResult.SUCCESS, result.code);
    assertNull(result.errmsg);
  }


  private void testDeleteItem(){
    String someAppId = "demo";
    String someNamespaceName = "application";
    NamespaceManager instance = NamespaceManager.builder().appId(someAppId)
        .namespace(someNamespaceName).token(token).build();
    OpenApiResult result = instance.deleteItem("testKey");
    assertEquals(result.code, OpenApiResult.SUCCESS);
    assertNull(result.errmsg);
  }


  private void testReleaseItem(){
    String someAppId = "demo";
    String someNamespaceName = "application";
    NamespaceManager instance = NamespaceManager.builder().appId(someAppId)
        .namespace(someNamespaceName).token(token).build();
    OpenApiResult<OpenReleaseDTO> result = instance.release("openapi Release");
    assertEquals(OpenApiResult.SUCCESS, result.code);
    assertNotNull(result.body);
    assertNull(result.errmsg);
  }
  
  @Test
  public void testInfo(){
    String someAppId = "demo";
    String someNamespaceName = "application";
    NamespaceManager instance = NamespaceManager.builder().appId(someAppId)
        .namespace(someNamespaceName).token(token).build();
    OpenApiResult<OpenNamespaceDTO> result = instance.info();
    assertEquals(OpenApiResult.SUCCESS, result.code);
    assertNotNull(result.body);
    assertEquals(result.body.getAppId(), someAppId);
    assertNull(result.errmsg);
  }

}
