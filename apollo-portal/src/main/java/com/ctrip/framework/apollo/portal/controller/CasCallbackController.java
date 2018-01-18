package com.ctrip.framework.apollo.portal.controller;

import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.spi.LogoutHandler;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;
import com.ctrip.framework.apollo.portal.spi.youzan.YouzanUserInfoHolder;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by zhangzheng on 2018/1/16
 */
@RestController
public class CasCallbackController {
    @Autowired
    UserInfoHolder userInfoHolder;

    @Autowired
    PortalConfig portalConfig;

    @Autowired
    LogoutHandler logoutHandler;

    private Logger logger = LoggerFactory.getLogger(CasCallbackController.class);
    private static String CAS_USER_URL = "https://cas.qima-inc.com/oauth/users/self?code=";



    @RequestMapping(value = "/oauth/cas/callback", method = RequestMethod.GET)
    public void casCallback(@RequestParam String code, @RequestParam String qs, HttpSession session, HttpServletResponse servletResponse){
        logger.info(String.format("cas login callback,code:%s,qs:%s" ,code,qs));
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpGet httpget = new HttpGet(CAS_USER_URL + code);
        // 加入头部验证参数
        httpget.setHeader("authorization", "oauth "+portalConfig.getValue("cas.secret"));
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(5000)
                .setConnectTimeout(5000)
                .setSocketTimeout(5000).build();
        httpget.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
        } catch (IOException e) {
            logger.error("exception",e);
        }
        HttpEntity entity = response.getEntity();
        String jsonStr = null;
        try {
            jsonStr = EntityUtils.toString(entity);
            logger.info("the return result:"+jsonStr);
        } catch (IOException e) {
            logger.error("exception",e);
        }
        httpget.releaseConnection();
        Gson gson = new Gson();
        Map jsonMap = gson.fromJson(jsonStr, HashMap.class);
        if ((Double)jsonMap.get("code")==0 && ((String)jsonMap.get("msg")).equals("ok")) {
            Map user = (Map) (((Map) jsonMap.get("data")).get("value"));
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId((String) user.get("username"));
            userInfo.setName((String) user.get("username"));
            userInfo.setEmail((String) user.get("email"));
            session.setAttribute("loginUserInfo",userInfo);
            try {
                servletResponse.sendRedirect(qs.startsWith("/oauth/cas/callback")?"/":qs);
            } catch (IOException e) {
                logger.error("重定向失败", e);
            }
        }else{
            logger.error("登录失败");
        }


    }

    @RequestMapping(value = "/oauth/cas/logout", method = RequestMethod.GET)
    public void casLogout(HttpSession session, HttpServletResponse servletResponse){
        logger.info("cas logout callback...");
        session.invalidate();
        try {
            servletResponse.sendRedirect("/");
        } catch (IOException e) {
            logger.error("重定向失败",e);
        }
    }



}


