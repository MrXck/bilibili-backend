package com.bilibili.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class RabbitMQConfig{
    public static final String DYNAMIC_EXCHANGE = "dynamic.topic";
    public static final String DYNAMIC_INSERT_QUEUE = "dynamic.insert.queue";
    public static final String DYNAMIC_DELETE_QUEUE = "dynamic.delete.queue";
    public static final String DYNAMIC_UPDATE_QUEUE = "dynamic.update.queue";

    @Bean("dynamicExchange")
    public Exchange dynamicExchange(){
        // topicExchange 使用topic类型的交换机
        // durable 是否持久化
        return ExchangeBuilder.topicExchange(DYNAMIC_EXCHANGE).durable(true).build();
    }

    @Bean("insertQueue")
    public Queue insertQueue(){
        return QueueBuilder.durable(DYNAMIC_INSERT_QUEUE).build();
    }

    @Bean("updateQueue")
    public Queue updateQueue(){
        return QueueBuilder.durable(DYNAMIC_UPDATE_QUEUE).build();
    }

    @Bean("deleteQueue")
    public Queue deleteQueue(){
        return QueueBuilder.durable(DYNAMIC_DELETE_QUEUE).build();
    }

    @Bean
    public Binding bindInsertQueueExchange(@Qualifier("insertQueue")Queue queue, @Qualifier("dynamicExchange")Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("dynamic.insert.#").noargs();
    }

    @Bean
    public Binding bindUpdateQueueExchange(@Qualifier("updateQueue")Queue queue, @Qualifier("dynamicExchange")Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("dynamic.update.#").noargs();
    }

    @Bean
    public Binding bindDeleteQueueExchange(@Qualifier("deleteQueue")Queue queue, @Qualifier("dynamicExchange")Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("dynamic.delete.#").noargs();
    }
}
