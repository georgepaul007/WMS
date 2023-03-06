package com.example.wms.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${topic.name.edit-product-details}")
    private String topicName;

    @Value("${topic.name.product}")
    private String productTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topicEditProduct() {
//        return new NewTopic("incomingRequests", 1, ;
        return TopicBuilder.name(topicName)
                .partitions(1)
                .build();
    }
    @Bean
    public NewTopic topicProduct() {
//        return new NewTopic("incomingRequests", 1, ;
        return TopicBuilder.name(productTopic)
                .partitions(1)
                .build();
    }
}
