package com.hmdp.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // 业务队列(带TTL和死信配置)
    @Bean(name = "canal.queue")
    public Queue canalQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 60000); // 消息存活60秒
        args.put("x-dead-letter-exchange", "dlx.exchange");
        args.put("x-dead-letter-routing-key", "dlx.routingKey");
        return new Queue("canal.queue", true, false, false, args);
    }

    // 死信队列
    @Bean(name = "dlx.queue")
    public Queue dlxQueue() {
        return new Queue("dlx.queue", true);
    }

    // 死信交换机
    @Bean(name = "dlx.exchange")
    public DirectExchange dlxExchange() {
        return new DirectExchange("dlx.exchange");
    }

    // 绑定关系
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue())
                .to(dlxExchange())
                .with("dlx.routingKey");
    }
}
