package com.yudianbank.tms.configure.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yudianbank.tms.util.ProjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.io.PrintWriter;
import java.util.Map;

/**
 * 用户登录拦截器
 *
 * @author Song Lea
 */
@Configuration
public class UserLoginInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoginInterceptor.class);

    // 登录url
    public static final String LOGIN_URL = "/api/tms/schedule/sign/index";

    // 默认的Cookie名
    public static final String DEFAULT_COOKIE_NAME = "TMS_REPORT_STATISTICS";

    @Value("${tms.report.platform.password}")
    private String defaultPassword;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        Map<String, String[]> params = request.getParameterMap();
        LOGGER.info("拦截到界面请求URL【{}】的参数列表【{}】", request.getRequestURI(),
                getRequestParameters(params));
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // 判断请求中的Cookie信息
                if (DEFAULT_COOKIE_NAME.equals(cookie.getName())
                        && ProjectUtil.encoderByMd5(defaultPassword).equals(cookie.getValue()))
                    return true;
            }
        }
        // 约定所有以/index结尾的URL为加载界面的请求
        if (request.getRequestURI() != null && request.getRequestURI().endsWith("index")) {
            response.sendRedirect(request.getContextPath() + LOGIN_URL);
        } else {
            // ajax请求不能使用response.sendRedirect()来重定向
            PrintWriter out = response.getWriter();
            out.println("No Login");
        }
        LOGGER.warn("请求中无对应Cookie信息,跳转到登录界面！");
        return false;
    }

    // 获取请求参数信息
    private static String getRequestParameters(Map<String, String[]> params) {
        if (params == null) return "";
        StringBuilder result = new StringBuilder("");
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (String value : values) {
                result.append(key).append("=").append(value).append("&");
            }
        }
        return result.toString();
    }
}
