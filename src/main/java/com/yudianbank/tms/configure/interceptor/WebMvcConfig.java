package com.yudianbank.tms.configure.interceptor;

import com.yudianbank.tms.util.ProjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 自定义的拦截器注册(用于注册应用登录拦截器)
 *
 * @author Song Lea
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebMvcConfig.class);

    private static final Pattern SWAGGER_URL_PATTERN = Pattern.compile("^[\\s\\S]*/swagger-ui.html[\\s\\S]*$");
    private static final Pattern DRUID_URL_PATTERN = Pattern.compile("^[\\s\\S]*/druid/[\\s\\S]+\\.html$");

    private UserLoginInterceptor userLoginInterceptor;  // 用户登录拦截器

    @Value("${tms.report.platform.password}")
    private String defaultPassword;

    public WebMvcConfig() {
    }

    @Autowired
    public WebMvcConfig(UserLoginInterceptor userLoginInterceptor) {
        Assert.notNull(userLoginInterceptor, "WebMvcConfig.userLoginInterceptor must not be null!");
        this.userLoginInterceptor = userLoginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLoginInterceptor)
                .addPathPatterns("/api/tms/schedule/handler/*")
                .excludePathPatterns("/api/tms/schedule/sign/*");
        super.addInterceptors(registry);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String url = httpServletRequest.getRequestURI(); // 取请求地址
            if (url != null && (SWAGGER_URL_PATTERN.matcher(url).matches()
                    || DRUID_URL_PATTERN.matcher(url).matches())) {
                LOGGER.info("请求Swagger2与druid的界面地址【{}】需要判断是否登录！", url);
                Cookie[] cookies = httpServletRequest.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (UserLoginInterceptor.DEFAULT_COOKIE_NAME.equals(cookie.getName())
                                && ProjectUtil.encoderByMd5(defaultPassword).equals(cookie.getValue())) {
                            chain.doFilter(request, response);
                            return;
                        }
                    }
                }
                // 请求中没有cookie未cookie解析不正确
                response.setContentType(MediaType.TEXT_HTML_VALUE);
                response.setCharacterEncoding(ProjectUtil.DEFAULT_CHARSET);
                response.getWriter().write("抱歉，请先<a href='"
                        + httpServletRequest.getContextPath() + UserLoginInterceptor.LOGIN_URL + "'> 登录 </a>！");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}