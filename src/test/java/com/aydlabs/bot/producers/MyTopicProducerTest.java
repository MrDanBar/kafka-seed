package com.aydlabs.bot.producers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyTopicProducerTest {

    @Mock
    private KafkaProducer<String, String> kafkaProducer;

    private MyTopicProducer producer;

    @BeforeEach
    void setUp() {
        producer = new MyTopicProducer(kafkaProducer);
    }

    @Test
    void send_withKeyAndMessage_callsProducerSend() {
        producer.send("user-1", "hello");

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaProducer).send(captor.capture(), any());

        final ProducerRecord<String, String> record = captor.getValue();
        assertThat(record.topic()).isEqualTo("my-topic-1");
        assertThat(record.key()).isEqualTo("user-1");
        assertThat(record.value()).isEqualTo("hello");
    }

    @Test
    void send_withMessageOnly_usesNullKey() {
        producer.send("hello");

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaProducer).send(captor.capture(), any());

        assertThat(captor.getValue().key()).isNull();
        assertThat(captor.getValue().value()).isEqualTo("hello");
    }

    @Test
    void close_flushesAndClosesProducer() {
        producer.close();

        final InOrder inOrder = inOrder(kafkaProducer);
        inOrder.verify(kafkaProducer).flush();
        inOrder.verify(kafkaProducer).close();
    }
}
