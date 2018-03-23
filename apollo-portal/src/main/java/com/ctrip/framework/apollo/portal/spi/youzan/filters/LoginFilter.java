package com.ctrip.framework.apollo.portal.spi.youzan.filters;

import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;
import com.ctrip.framework.apollo.portal.spi.youzan.YouzanUserInfoHolder;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Create by zhangzheng on 2018/1/17
 */
public class LoginFilter implements Filter{
    private static final String redURL = "http://cas.qima-inc.com/public/oauth/authorize?name=%s&qs=%s";
    private static final String STATIC_RESOURCE_REGEX = ".*\\.(js|html|htm|png|css|woff2)$";

    private Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    private PortalConfig portalConfig;
    private UserInfoHolder userInfoHolder;

    public LoginFilter(PortalConfig portalConfig, UserInfoHolder userInfoHolder) {
        this.portalConfig = portalConfig;
        this.userInfoHolder = userInfoHolder;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }



    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String requestUri = ((HttpServletRequest) request).getRequestURI();
        HttpSession httpSession = ((HttpServletRequest) request).getSession();
        try {
            if (!isOpenAPIRequest(requestUri) && !isStaticResource(requestUri) && !isCasCallBackRequest(requestUri)
                    && !isHealthCheck(requestUri)) {
                UserInfo userInfo = (UserInfo)httpSession.getAttribute("loginUserInfo");
                if (userInfo == null) {
                    logger.info("没有从上下文获取到用户的登录信息，跳转到cas系统进行认证");
                    ((HttpServletResponse)response).sendRedirect(String.format(redURL,portalConfig.getValue("cas.projectName"),requestUri));
                    return;
                }
                //设置到UseInfoHolder中
                ((YouzanUserInfoHolder)userInfoHolder).setUser(userInfo);
            }
        } catch (Throwable e) {
            logger.error("check user login error.", e);
        }

        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }

    private boolean isOpenAPIRequest(String uri) {
        return !Strings.isNullOrEmpty(uri) && uri.startsWith("/openapi");
    }

    private boolean isStaticResource(String uri) {
        return !Strings.isNullOrEmpty(uri) && uri.matches(STATIC_RESOURCE_REGEX);
    }
    private boolean isCasCallBackRequest(String uri){
        return !Strings.isNullOrEmpty(uri) && uri.startsWith("/oauth/cas/callback");
    }

    private boolean isHealthCheck(String uri){
        return !Strings.isNullOrEmpty(uri) && uri.startsWith("/health");
    }
}
