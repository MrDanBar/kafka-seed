package com.aydlabs.bot;

import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class MyBot {

    static void main(final String[] args) {
        SpringApplication.run(MyBot.class, args);
    }

    @Bean
    public PromptChatMemoryAdvisor promptChatMemoryAdvisor(final DataSource dataSource) {
        var jdbc = JdbcChatMemoryRepository
                .builder()
                .dataSource(dataSource)
                .build();

        var chatMessageWindow = MessageWindowChatMemory
                .builder()
                .chatMemoryRepository(jdbc)
                .build();

        return PromptChatMemoryAdvisor
                .builder(chatMessageWindow)
                .build();
    }
}
