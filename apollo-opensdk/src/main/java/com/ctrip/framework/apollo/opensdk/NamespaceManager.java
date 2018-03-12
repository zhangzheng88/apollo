package com.ctrip.framework.apollo.opensdk;

import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.opensdk.dto.OpenItemDTO;
import com.ctrip.framework.apollo.opensdk.dto.OpenNamespaceLockDTO;
import com.ctrip.framework.apollo.opensdk.dto.OpenReleaseDTO;
import com.ctrip.framework.foundation.Foundation;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
  private RestTemplate restTemplate = injector.getInstance(RestTemplate.class);

  private static Injector injector = Guice.createInjector(new ApolloModule());

  private static final String PORTAL_URL = "apollo-portal.prod.qima-inc.com";

  private NamespaceManager(){}




  /**
   * Apollo在生产环境（PRO）有限制规则：每次发布只能有一个人编辑配置，且该次发布的人不能是该次发布的编辑人。
   * 也就是说如果一个用户A修改了某个namespace的配置，那么在这个namespace发布前，只能由A修改，其它用户无法修改。
   * 同时，该用户A无法发布自己修改的配置，必须找另一个有发布权限的人操作。 这个接口就是用来获取当前namespace是否有人锁定的接口。
   * 在非生产环境（FAT、UAT），该接口始终返回没有人锁定。
   * @return
   */
  private OpenNamespaceLockDTO getLock(){
    Map<String,String> uriVariables = ImmutableMap.of("appId",appId,"clusterName",clusterName,"namespaceName", namespaceName,
        "portal_address", PORTAL_URL, "env",env.name());
    String url = "http://{portal_address}/openapi/v1/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/lock";

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "2bad744d24dc974adb0556052fc158c9264ec42d");
    httpHeaders.add("Content-Type","application/json;charset=UTF-8");
    HttpEntity httpEntity = new HttpEntity(httpHeaders);

    ResponseEntity<OpenNamespaceLockDTO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, OpenNamespaceLockDTO.class, uriVariables);

    if(responseEntity.getStatusCode().is2xxSuccessful()){
      return responseEntity.getBody();
    }else{
      return null;
    }
  }

  private HttpEntity httpEntity(){
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", token);
    httpHeaders.add("Content-Type","application/json;charset=UTF-8");
    HttpEntity httpEntity = new HttpEntity(httpHeaders);
    return httpEntity;
  }

  private <T> HttpEntity<T> httpEntity(T body){
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", token);
    httpHeaders.add("Content-Type","application/json;charset=UTF-8");
    HttpEntity<T> httpEntity = new HttpEntity(body, httpHeaders);
    return httpEntity;
  }

  /**
   * 新增配置接口
   * @return
   */
  public OpenItemDTO createItem(String key,String value,String comment){
    Map<String,String> uriVariables = ImmutableMap.of("appId",appId,"clusterName",clusterName,"namespaceName", namespaceName,
        "portal_address", PORTAL_URL, "env",env.name());
    String url = "http://{portal_address}/openapi/v1/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/items";

    OpenItemDTO itemDTO = new OpenItemDTO();
    itemDTO.setKey(key);
    itemDTO.setValue(value);
    itemDTO.setComment(comment);
    itemDTO.setDataChangeCreatedBy(dataChangedBy);

    ResponseEntity<OpenItemDTO> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity(itemDTO), OpenItemDTO.class, uriVariables);
    if(responseEntity.getStatusCode().is2xxSuccessful()){
      return responseEntity.getBody();
    }else{
      return null;
    }
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
    HttpEntity httpEntity = httpEntity(body);
    OpenApiResult result = new OpenApiResult();

    try {
      ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class, uriVariables);
      if(responseEntity.getStatusCode().is2xxSuccessful()) {
        result.code = OpenApiResult.SUCCESS;
      }else{
        result.code = OpenApiResult.FAIL;
        result.errmsg = responseEntity.getBody();
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
      ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity(), String.class, uriVariables);
      if(responseEntity.getStatusCode().is2xxSuccessful()) {
        result.code = OpenApiResult.SUCCESS;
      }else{
        result.code = OpenApiResult.FAIL;
        result.errmsg = responseEntity.getBody();
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
    HttpEntity httpEntity = httpEntity(body);
    OpenApiResult<OpenReleaseDTO> result = new OpenApiResult();
    try {
      ResponseEntity<OpenReleaseDTO> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, OpenReleaseDTO.class, uriVariables);

      if(responseEntity.getStatusCode().is2xxSuccessful()){
        result.body = responseEntity.getBody();
        result.code = OpenApiResult.SUCCESS;
      }else{
        result.code = OpenApiResult.FAIL;
        result.errmsg = responseEntity.getStatusCode().getReasonPhrase();
      }
    } catch (Exception e) {
      result.code = OpenApiResult.FAIL;
      result.errmsg = e.getMessage();
    }
    return result;
  }

  /**
   * 获取某个Namespace当前生效的已发布配置接口
   * @return
   */
  public OpenReleaseDTO getLastestRelease(){
    return null;
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


  private static class ApolloModule extends AbstractModule {

    @Override
    protected void configure() {
      bind(RestTemplate.class).in(Singleton.class);
    }
  }

  protected void setRestTemplate(RestTemplate restTemplate){
    //for test
    this.restTemplate = restTemplate;
  }


}
