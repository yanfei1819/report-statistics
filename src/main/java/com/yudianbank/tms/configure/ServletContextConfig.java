package com.yudianbank.tms.configure;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * 获取ServletContext对象中注册的Bean
 *
 * @author Song Lea
 */
@Component
public class ServletContextConfig implements ServletContextAware {

    // 应用上下文中以获取对应的Service
    private static WebApplicationContext webApplicationContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String clazzName) {
        return (T) webApplicationContext.getBean(clazzName);
    }

    // redisTemplate的获取要单独设置key与value的序列化格式
    public static StringRedisTemplate getStringRedisTemplate() {
        return getBean(StringRedisTemplate.class);
    }

    public static <T> T getBean(Class<T> clazz) {
        return webApplicationContext.getBean(clazz);
    }
}