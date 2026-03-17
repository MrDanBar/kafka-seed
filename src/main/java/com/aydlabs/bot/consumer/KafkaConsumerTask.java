package com.aydlabs.bot.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class KafkaConsumerTask extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerTask.class);
    private static final String TOPIC = "my-topic-1";

    private final KafkaConsumer<String, String> consumer;

    public KafkaConsumerTask(final KafkaConsumer<String, String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        consumer.subscribe(List.of(TOPIC));

        LOGGER.info("Starting Kafka Consumer Task");
        try {

            while (true) {

                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : consumerRecords) {
                    LOGGER.info("==> off:{} -> part:{} -> k:{} -> v:{}",
                                record.offset(),
                                record.partition(),
                                record.key(),
                                record.value());
                }

            }
        } finally {
            LOGGER.error("Un-Starting Kafka Consumer Task");
            consumer.close();
        }
    }
}
