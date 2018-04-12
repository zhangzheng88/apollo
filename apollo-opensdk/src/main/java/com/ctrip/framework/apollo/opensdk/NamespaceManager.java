package com.ctrip.framework.apollo.opensdk;

import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.opensdk.dto.OpenEnvClusterDTO;
import com.ctrip.framework.apollo.opensdk.dto.OpenItemDTO;
import com.ctrip.framework.apollo.opensdk.dto.OpenNamespaceDTO;
import com.ctrip.framework.apollo.opensdk.dto.OpenReleaseDTO;
import com.ctrip.framework.foundation.Foundation;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private static OkHttpClient client;
  private static Gson gson = new Gson();
  private static final String PORTAL_URL = ConfigReader.portalUrl();

  private static Logger logger = LoggerFactory.getLogger("okhttp3");
  static {
    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(
        new HttpLoggingInterceptor.Logger() {
          @Override
          public void log(String s) {
            logger.debug(s);
          }
        });
    httpLoggingInterceptor.setLevel(Level.BODY);
    client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
  }

  private NamespaceManager(){}

  private Headers headers(){
    return Headers.of("Authorization",token,"Content-Type", "application/json;charset=UTF-8");
  }

  private RequestBody jsonBody(Object body){
    return RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), gson.toJson(body));
  }

  public OpenApiResult<OpenNamespaceDTO> info(){
    changeClusterIfNotExist();
    Map<String,String> uriVariables = ImmutableMap.of("appId",appId,"clusterName",clusterName,"namespaceName", namespaceName,
        "portal_address", PORTAL_URL, "env",env.name());
    String url = "{portal_address}/openapi/v1/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}";

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
    changeClusterIfNotExist();
    Map<String,String> uriVariables = ImmutableMap.of("appId",appId,"clusterName",clusterName,"namespaceName", namespaceName,
        "portal_address", PORTAL_URL, "env",env.name());
    String url = "{portal_address}/openapi/v1/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/items";

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
    changeClusterIfNotExist();
    String url = "{portal_address}/openapi/v1/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/items/{key}";
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
    changeClusterIfNotExist();
    String url = "{portal_address}/openapi/v1/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/items/{key}?operator={operator}";
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
    changeClusterIfNotExist();
    Map<String,String> uriVariables = ImmutableMap.of("appId",appId,"clusterName",clusterName,"namespaceName", namespaceName,
        "portal_address", PORTAL_URL, "env",env.name());
    String url = "{portal_address}/openapi/v1/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/releases";

    Map body = ImmutableMap.of("releaseTitle",releaseTitle, "releaseComment",releaseTitle, "releasedBy", dataChangedBy, "isEmergencyPublish",true);
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

  /**
   * 如果sdk操作的集群不存在，那么就去操作default集群的配置
   */
  private void changeClusterIfNotExist(){
    Map<String,String> uriVariables = ImmutableMap.of("appId",appId, "portal_address", PORTAL_URL);
    String url = "{portal_address}/openapi/v1/apps/{appId}/envclusters";

    Request request = new Request.Builder().get().url(HttpUtil.parseUrlTemplate(url, uriVariables)).headers(headers()).build();
    try {
      Response response = client.newCall(request).execute();
      if(response.isSuccessful()){
        Type type = new TypeToken<List<OpenEnvClusterDTO>>(){}.getType();
        List<OpenEnvClusterDTO> openEnvClusterDTOS = gson.fromJson(response.body().string(), type);
        for(OpenEnvClusterDTO openEnvClusterDTO:openEnvClusterDTOS){
          if(openEnvClusterDTO.getEnv().toUpperCase().equals(env.name().toUpperCase())){
            if(!openEnvClusterDTO.getClusters().contains(clusterName)){
              logger.info("the cluster {} not exist, so the opensdk will operate the default cluster", clusterName);
              clusterName = "default";
            }
          }
        }
      }
    } catch (IOException e) {
      logger.error("open sdk can't load env and cluster info", e);
    }

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

    public Builder namespace(String namespace){
      namespaceManager.namespaceName = namespace;
      return this;
    }
    public Builder token(String token){
      namespaceManager.token = token;
      return this;
    }


    public NamespaceManager build(){
      Env env = Env.fromString(Foundation.server().getEnvType());//read env from application context
      namespaceManager.env = env;
      namespaceManager.clusterName = Foundation.server().getDataCenter();
      namespaceManager.dataChangedBy = "opensdk";
      return namespaceManager;
    }

  }


}
