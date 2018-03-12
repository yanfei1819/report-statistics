package com.yudianbank.tms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 凯京物流云报表管理模块后台统计实现
 *
 * @author Song Lea
 */
// 相关于使用@Configuration,@EnableAutoConfiguration与@ComponentScan的默认属性
@SpringBootApplication
// 扫描@WebServlet,@WebFilter,@WebListener注解,只有使用servlet容器时作用
@ServletComponentScan(value = {"com.yudianbank.tms.servlet"})
// 启注解事务管理,等同于xml配置方式的<tx:annotation-driven />,可以使用@Transactional注解
@EnableTransactionManagement
public class ReportStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportStatisticsApplication.class, args);
    }

    // 对404与500错误界面
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return (container -> {
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
            container.addErrorPages(error404Page, error500Page);
        });
    }
}
