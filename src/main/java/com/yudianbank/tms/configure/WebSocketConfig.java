package com.yudianbank.tms.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * 作业执行完毕后推送结果给界面
 *
 * @author Song Lea
 */
@Configuration
@EnableWebSocketMessageBroker // 表示开启使用STOMP协议来传输基于代理的消息
@EnableWebSocket
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    public static final String TOPIC_REQUIRE = "/topic/finishJob";

    @Override
    // 表示注册STOMP协议的节点，并指定映射的URL
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint("/endpointJob").withSockJS();
    }

    @Override
    // 用来配置消息代理，由于我们是实现推送功能，这里的消息代理是/topic
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
    }
}
