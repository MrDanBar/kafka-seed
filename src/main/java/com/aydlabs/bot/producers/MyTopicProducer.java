package com.aydlabs.bot.producers;

import com.oracle.svm.core.annotate.Inject;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MyTopicProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyTopicProducer.class);
    private static final String TOPIC = "my-topic-1";

    private final KafkaProducer<String, String> producer;

    @Autowired
    public MyTopicProducer(@Value("${kafka.bootstrap-servers:localhost:9092}") final String bootstrapServers) {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.producer = new KafkaProducer<>(props);
    }

    MyTopicProducer(final KafkaProducer<String, String> producer) {
        this.producer = producer;
    }

    public void send(final String key, final String message) {
        final ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, key, message);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                LOGGER.error("Failed to send message to topic {}", TOPIC, exception);
            } else {
                LOGGER.info("Message sent to topic={} partition={} offset={}", metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }

    public void send(final String message) {
        send(null, message);
    }

    @PreDestroy
    public void close() {
        producer.flush();
        producer.close();
        LOGGER.info("KafkaProducer closed");
    }
}
