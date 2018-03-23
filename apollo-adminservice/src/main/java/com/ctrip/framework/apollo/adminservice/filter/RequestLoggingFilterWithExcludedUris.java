package com.ctrip.framework.apollo.adminservice.filter;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Create by zhangzheng on 2018/1/21
 */
public class RequestLoggingFilterWithExcludedUris extends CommonsRequestLoggingFilter{
    private LinkedHashSet<String> excludeUris = new LinkedHashSet<>();
    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        for(String uri:excludeUris){
            if(uri.equals(requestUri)){
                return false;
            }
        }
        return super.shouldLog(request);
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        super.beforeRequest(request,message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        super.afterRequest(request, message);
    }

    public void addExcludedUri(String uri){
        this.excludeUris.add(uri);
    }

}
