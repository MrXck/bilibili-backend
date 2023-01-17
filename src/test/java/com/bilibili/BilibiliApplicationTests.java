package com.bilibili;

import com.bilibili.config.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BilibiliApplicationTests {

    //@Autowired
    //private RabbitTemplate rabbitTemplate;

    @Test
    void va() {
        //System.out.println(rabbitTemplate);
        //
        //rabbitTemplate.setConfirmCallback((correlationData, b, s) -> {
        //    System.out.println(correlationData);
        //    System.out.println(b);
        //    System.out.println(s);
        //});
        //
        //rabbitTemplate.setReturnsCallback(System.out::println);
        //
        //
        //rabbitTemplate.convertAndSend(RabbitMQConfig.DYNAMIC_EXCHANGE, RabbitMQConfig.DYNAMIC_INSERT_QUEUE, "123123");
    }

}
