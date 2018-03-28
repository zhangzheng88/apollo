package com.ctrip.framework.apollo.opensdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.ctrip.framework.apollo.opensdk.dto.OpenItemDTO;
import com.ctrip.framework.apollo.opensdk.dto.OpenNamespaceDTO;
import com.ctrip.framework.apollo.opensdk.dto.OpenReleaseDTO;
import org.junit.Test;

/**
 * Create by zhangzheng on 2018/3/10
 */
public class OpenSDKTest {



  @Test
  public void testCreateInstance(){
    String someAppId = "someAppId";
    String someClustername = "someClusterName";
    String someNamespaceName = "someNamespaceName";
    String someToken = "token";
    String dataChangedBy = "dataChangedBy";
    NamespaceManager instance = NamespaceManager.builder().appId(someAppId).cluster(someClustername)
        .namespace(someNamespaceName).token(someToken).dataChangedBy(dataChangedBy).build();
    assertNotNull(instance);
    assertEquals(someAppId, instance.appId);
    assertEquals(someClustername, instance.clusterName);
    assertEquals(someNamespaceName, instance.namespaceName);
    assertEquals(someToken, instance.token);
    assertEquals("DAILY", instance.env.name());
    assertEquals(dataChangedBy, instance.dataChangedBy);
  }


  @Test(expected = Exception.class)
  public void testCreateItemWithNoAuth(){
    String someAppId = "demo";
    String someClustername = "default";
    String someNamespaceName = "application";
    NamespaceManager instance = NamespaceManager.builder().appId(someAppId).cluster(someClustername)
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
    String someClustername = "default";
    String someNamespaceName = "application";
    String token = "2bad744d24dc974adb0556052fc158c9264ec42d";
    NamespaceManager instance = NamespaceManager.builder().appId(someAppId).cluster(someClustername)
        .namespace(someNamespaceName).token(token).dataChangedBy("张正").build();
    OpenApiResult<OpenItemDTO> result = instance.createItem("testKey", "testValue", "comment");
    assertEquals(result.code, OpenApiResult.SUCCESS);
    assertNotNull(result.body);
    assertNull(result.errmsg);
  }


  private void testUpdateItem(){
    String someAppId = "demo";
    String someClustername = "default";
    String someNamespaceName = "application";
    String token = "2bad744d24dc974adb0556052fc158c9264ec42d";
    NamespaceManager instance = NamespaceManager.builder().appId(someAppId).cluster(someClustername)
        .namespace(someNamespaceName).token(token).dataChangedBy("张正").build();
    String key = "testKey";
    String newValue = "newValue";
    String comment = "openapiUpdateItem";
    OpenApiResult result = instance.updateItem(key, newValue, comment);
    assertEquals(OpenApiResult.SUCCESS, result.code);
    assertNull(result.errmsg);
  }


  private void testDeleteItem(){
    String someAppId = "demo";
    String someClustername = "default";
    String someNamespaceName = "application";
    String token = "2bad744d24dc974adb0556052fc158c9264ec42d";
    NamespaceManager instance = NamespaceManager.builder().appId(someAppId).cluster(someClustername)
        .namespace(someNamespaceName).token(token).dataChangedBy("张正").build();
    OpenApiResult result = instance.deleteItem("testKey");
    assertEquals(result.code, OpenApiResult.SUCCESS);
    assertNull(result.errmsg);
  }


  private void testReleaseItem(){
    String someAppId = "demo";
    String someClustername = "default";
    String someNamespaceName = "application";
    String token = "2bad744d24dc974adb0556052fc158c9264ec42d";
    NamespaceManager instance = NamespaceManager.builder().appId(someAppId).cluster(someClustername)
        .namespace(someNamespaceName).token(token).dataChangedBy("张正").build();
    OpenApiResult<OpenReleaseDTO> result = instance.release("openapi Release");
    assertEquals(OpenApiResult.SUCCESS, result.code);
    assertNotNull(result.body);
    assertNull(result.errmsg);
  }
  
  @Test
  public void testInfo(){
    String someAppId = "demo";
    String someClustername = "default";
    String someNamespaceName = "application";
    String token = "2bad744d24dc974adb0556052fc158c9264ec42d";
    NamespaceManager instance = NamespaceManager.builder().appId(someAppId).cluster(someClustername)
        .namespace(someNamespaceName).token(token).dataChangedBy("张正").build();
    OpenApiResult<OpenNamespaceDTO> result = instance.info();
    assertEquals(OpenApiResult.SUCCESS, result.code);
    assertNotNull(result.body);
    assertEquals(result.body.getAppId(), someAppId);
    assertNull(result.errmsg);
  }

}
