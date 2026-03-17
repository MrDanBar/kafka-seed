package com.aydlabs.bot.producers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaProducerTask extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerTask.class);

    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final String key;
    private final String message;

    public KafkaProducerTask(final KafkaProducer<String, String> producer,
                             final String topic,
                             final String key,
                             final String message) {
        this.producer = producer;
        this.topic = topic;
        this.key = key;
        this.message = message;
    }

    @Override
    public void run() {
        final ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                LOGGER.error("Failed to send message to topic {}", topic, exception);
            } else {
                LOGGER.info("Message sent to topic={} partition={} offset={} message={}",
                        metadata.topic(), metadata.partition(), metadata.offset(), message);
            }
        });
    }
}
