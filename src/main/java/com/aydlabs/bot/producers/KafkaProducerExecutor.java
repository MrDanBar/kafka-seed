package com.aydlabs.bot.producers;

import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaProducerExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerExecutor.class);

    private static final String TOPIC = "my-topic-1";

    private final ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final KafkaProducer<String, String> producer;

    public KafkaProducerExecutor(@Value("${kafka.bootstrap-servers:localhost:9092}") final String bootstrapServers) {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        this.producer = new KafkaProducer<>(props);
        LOGGER.info("KafkaProducerExecutor initialized with {} threads", Runtime.getRuntime().availableProcessors());
    }

    public void enviar(final String key, final String message) {
        executorService.submit(new KafkaProducerTask(producer, TOPIC, key, message));
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (final InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        producer.flush();
        producer.close();
        LOGGER.info("KafkaProducerExecutor shutdown complete");
    }
}
