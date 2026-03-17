package com.aydlabs.bot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MyBotTest {

    @Mock
    private DataSource dataSource;

    @Test
    void promptChatMemoryAdvisor_buildsBeanSuccessfully() {
        final MyBot bot = new MyBot();
        final PromptChatMemoryAdvisor advisor = bot.promptChatMemoryAdvisor(dataSource);

        assertThat(advisor).isNotNull();
        assertThat(advisor).isInstanceOf(PromptChatMemoryAdvisor.class);
    }
}
