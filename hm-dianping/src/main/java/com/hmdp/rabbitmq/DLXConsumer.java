package com.hmdp.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class DLXConsumer {

    @RabbitListener(queues = "dlx.queue")
    public void handleDlxMessage(Message message, Channel channel) throws IOException {
        String msgId = message.getMessageProperties().getMessageId();
        log.warn("收到死信消息：{}", msgId);

        // 记录到数据库或日志文件
//        failedMessageService.saveFailedMessage(
//                new FailedMessage(msgId, new String(message.getBody()),
//                        "DLX_EXPIRED", "Exceed max retry or TTL")
//        );

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
