package com.ctrip.framework.apollo.opensdk;

import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.opensdk.dto.OpenItemDTO;
import com.ctrip.framework.apollo.opensdk.dto.OpenNamespaceDTO;
import com.ctrip.framework.apollo.opensdk.dto.OpenReleaseDTO;
import com.ctrip.framework.foundation.Foundation;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Create by zhangzheng on 2018/3/10
 */
public class NamespaceManager {

  protected Env env;
  protected String appId;
  protected String clusterName;
  protected String namespaceName;
  protected String token;
  protected String dataChangedBy;

  private static OkHttpClient client = new OkHttpClient.Builder().build();
  private static Gson gson = new Gson();
  private static final String PORTAL_URL = "apollo-portal.prod.qima-inc.com";

  private NamespaceManager(){}

  private Headers headers(){
    return Headers.of("Authorization",token,"Content-Type", "application/json;charset=UTF-8");
  }

  private RequestBody jsonBody(Object body){
    return RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), gson.toJson(body));
  }

  public OpenApiResult<OpenNamespaceDTO> info(){
    Map<String,String> uriVariables = ImmutableMap.of("appId",appId,"clusterName",clusterName,"namespaceName", namespaceName,
        "portal_address", PORTAL_URL, "env",env.name());
    String url = "http://{portal_address}/openapi/v1/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}";

    OpenApiResult<OpenNamespaceDTO> result = new OpenApiResult();
    try {
      Request request = new Request.Builder().get().url(HttpUtil.parseUrlTemplate(url, uriVariables)).headers(headers()).build();
      Response response = client.newCall(request).execute();
      if(response.isSuccessful()){
        result.code = OpenApiResult.SUCCESS;
        result.body = gson.fromJson(response.body().string(), OpenNamespaceDTO.class);
      }else{
        result.code = OpenApiResult.FAIL;
        result.errmsg = response.body().string();
      }
    } catch (Exception e) {
      result.code = OpenApiResult.FAIL;
      result.errmsg = e.getMessage();
    }
    return result;
  }

  /**
   * 新增配置接口
   * @return
   */
  public OpenApiResult<OpenItemDTO> createItem(String key,String value,String comment){
    Map<String,String> uriVariables = ImmutableMap.of("appId",appId,"clusterName",clusterName,"namespaceName", namespaceName,
        "portal_address", PORTAL_URL, "env",env.name());
    String url = "http://{portal_address}/openapi/v1/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/items";

    OpenItemDTO itemDTO = new OpenItemDTO();
    itemDTO.setKey(key);
    itemDTO.setValue(value);
    itemDTO.setComment(comment);
    itemDTO.setDataChangeCreatedBy(dataChangedBy);

    Request request = new Request.Builder().post(jsonBody(itemDTO)).url(HttpUtil.parseUrlTemplate(url, uriVariables)).headers(headers()).build();
    OpenApiResult result = new OpenApiResult();
    try {
      Response response = client.newCall(request).execute();
      if(response.isSuccessful()){
        result.code = OpenApiResult.SUCCESS;
        result.body = gson.fromJson(response.body().string(), OpenItemDTO.class);
      }else{
        result.code = OpenApiResult.FAIL;
        result.errmsg = response.body().string();
      }
    } catch (Exception e) {
      result.code = OpenApiResult.FAIL;
      result.errmsg = e.getMessage();
    }
    return result;
  }

  /**
   * 修改配置接口
   * @return
   */
  public OpenApiResult updateItem(String key, String newValue, String comment){

    String url = "http://{portal_address}/openapi/v1/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/items/{key}";
    Map<String,String> uriVariables = ImmutableMap.<String,String>builder().put("appId", appId).put("clusterName",clusterName)
        .put("namespaceName", namespaceName).put("portal_address", PORTAL_URL).put("env",env.name()).put("key", key).build();

    Map body = ImmutableMap.of("key",key, "value",newValue, "comment", comment, "dataChangeLastModifiedBy", dataChangedBy);

    OpenApiResult result = new OpenApiResult();

    try {
      Request request = new Request.Builder().put(jsonBody(body))
          .url(HttpUtil.parseUrlTemplate(url, uriVariables)).headers(headers()).build();
      Response response = client.newCall(request).execute();
      if(response.isSuccessful()) {
        result.code = OpenApiResult.SUCCESS;
      }else{
        result.code = OpenApiResult.FAIL;
        result.errmsg = response.body().string();
      }
    } catch (Exception e) {
      result.code = OpenApiResult.FAIL;
      result.errmsg = e.getMessage();
    }
    return result;
  }

  /**
   * 删除配置接口
   * @param key
   */
  public OpenApiResult deleteItem(String key){
    String url = "http://{portal_address}/openapi/v1/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/items/{key}?operator={operator}";
    Map<String,String> uriVariables = ImmutableMap.<String,String>builder().put("appId", appId).put("clusterName",clusterName)
        .put("namespaceName", namespaceName).put("portal_address", PORTAL_URL).put("env",env.name()).put("key", key)
        .put("operator",dataChangedBy).build();
    OpenApiResult result = new OpenApiResult();
    try {
      Request request = new Request.Builder().delete().headers(headers()).url(HttpUtil.parseUrlTemplate(url, uriVariables)).build();
      Response response = client.newCall(request).execute();
      if(response.isSuccessful()) {
        result.code = OpenApiResult.SUCCESS;
      }else{
        result.code = OpenApiResult.FAIL;
        result.errmsg = response.body().string();
      }

    } catch (Exception e) {
      result.code = OpenApiResult.FAIL;
      result.errmsg = e.getMessage();
    }
    return result;
  }

  /**
   * 发布配置接口
   * @param releaseTitle
   * @return
   */
  public OpenApiResult<OpenReleaseDTO> release(String releaseTitle){
    Map<String,String> uriVariables = ImmutableMap.of("appId",appId,"clusterName",clusterName,"namespaceName", namespaceName,
        "portal_address", PORTAL_URL, "env",env.name());
    String url = "http://{portal_address}/openapi/v1/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/releases";

    Map body = ImmutableMap.of("releaseTitle",releaseTitle, "releaseComment",releaseTitle, "releasedBy", dataChangedBy);
    OpenApiResult<OpenReleaseDTO> result = new OpenApiResult();
    try {

      Request request = new Request.Builder().post(jsonBody(body)).headers(headers()).url(HttpUtil.parseUrlTemplate(url, uriVariables)).build();
      Response response = client.newCall(request).execute();
      if(response.isSuccessful()){
        result.body = gson.fromJson(response.body().string(), OpenReleaseDTO.class);
        result.code = OpenApiResult.SUCCESS;
      }else{
        result.code = OpenApiResult.FAIL;
        result.errmsg = response.body().string();
      }
    } catch (Exception e) {
      result.code = OpenApiResult.FAIL;
      result.errmsg = e.getMessage();
    }
    return result;
  }



  public static Builder builder(){
    return new Builder();
  }

  public static class Builder{

    private NamespaceManager namespaceManager = new NamespaceManager();

    public Builder appId(String appId){
      namespaceManager.appId = appId;
      return this;
    }

    public Builder cluster(String cluster){
      namespaceManager.clusterName = cluster;
      return this;
    }

    public Builder namespace(String namespace){
      namespaceManager.namespaceName = namespace;
      return this;
    }
    public Builder token(String token){
      namespaceManager.token = token;
      return this;
    }

    public Builder dataChangedBy(String username){
      namespaceManager.dataChangedBy = username;
      return this;
    }

    public NamespaceManager build(){
      Env env = Env.fromString(Foundation.server().getEnvType());//read env from application context
      namespaceManager.env = env;
      return namespaceManager;
    }

  }


}
