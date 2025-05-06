package com.hmdp.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CancalQueueReceiver {

    @RabbitListener(queues = "cancal.queue")
    public void process(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String msgId = message.getMessageProperties().getMessageId();

        try {
            System.out.println("CancalQueueReceiver: " + message);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("CancalQueueReceiver: " + message);
        } catch (IOException e) {
            handleNack(message, channel, deliveryTag, e);
        }
    }


    private void handleNack(Message message, Channel channel,
                            long deliveryTag, Exception ex) {
        String msgId = message.getMessageProperties().getMessageId();
        try {
            // 3. 拒绝消息并重新入队
            channel.basicNack(deliveryTag, false, true);
            log.warn("消息重新入队：{}", msgId);
        } catch (IOException nackEx) {
            // 4. Nack失败时持久化到数据库
//            failedMessageService.saveFailedMessage(
//                    new FailedMessage(msgId, new String(message.getBody()),
//                            "NACK_FAILURE", ex.getMessage())
//            );
            log.error("NACK操作失败，消息持久化：{}", msgId, nackEx);
        }
    }

    private void handleBusiness(Message message) throws Exception {
        // 业务逻辑实现
        // 抛出异常模拟处理失败
    }

}
