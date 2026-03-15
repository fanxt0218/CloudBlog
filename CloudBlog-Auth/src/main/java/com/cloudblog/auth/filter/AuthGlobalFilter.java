package com.cloudblog.auth.filter;

import cn.hutool.core.text.AntPathMatcher;
import com.cloudblog.auth.config.AuthProperties;
import com.cloudblog.auth.util.JwtTool;
import com.cloudblog.common.exception.UnauthorizedException;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.RedisTokenBlacklistUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements Filter, Ordered {

    private final AuthProperties authProperties;

    private final JwtTool jwtTool;

    private final RedisTokenBlacklistUtil redisTokenBlacklistUtil;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        log.info("请求URI: {}", requestURI);
        log.info("请求方法: {}", method);

        //1.获取request
        //3.获取token
        String token = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);

        // 检查 token 是否在黑名单中
        if (token != null && redisTokenBlacklistUtil.isBlacklisted(token)) {
            log.info("Token 在黑名单中，拒绝访问：{}", requestURI);
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpResponse.setContentType("application/json;charset=utf-8");
            AjaxResult result = AjaxResult.error(401, "Token 已失效，请重新登录");
            httpResponse.getWriter().write(new ObjectMapper().writeValueAsString(result));
            return;
        }

        //2.判断是否需要拦截
        if (IsExclude(httpRequest.getRequestURI())){
            //尝试解析token
            Long userId = null;
            try {
                userId = jwtTool.parseToken(token);
            } catch (UnauthorizedException e) {
                // 说明用户没有有效token，未登录
                //放行
                chain.doFilter(servletRequest, servletResponse);
                return;
            }
            // 解析成功，表示登录，传递用户信息
            String userInfo = userId.toString();
            httpRequest.setAttribute("user-info", userInfo);
        }


        //4.解析token
        Long userId = null;
        try {
            userId = jwtTool.parseToken(token);
        } catch (UnauthorizedException e) {
            // 直接处理认证异常
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpResponse.setContentType("application/json;charset=utf-8");
            AjaxResult result = AjaxResult.error(e.getCode(), e.getMessage());
            httpResponse.getWriter().write(new ObjectMapper().writeValueAsString(result));
            //请求终止
            return;
        }
        //5.传递用户信息
        String userInfo = userId.toString();
        httpRequest.setAttribute("user-info", userInfo);
        log.info("过滤器用户ID: {},用户信息: {}", userId, userInfo);
        //6.放行
        chain.doFilter(servletRequest, servletResponse);
    }

    private boolean IsExclude(String string) {
        for (String pathPattern : authProperties.getExcludePaths()){
            if (antPathMatcher.match(pathPattern, string)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
