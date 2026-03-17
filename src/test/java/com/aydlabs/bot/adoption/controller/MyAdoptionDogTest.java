package com.aydlabs.bot.adoption.controller;

import com.aydlabs.bot.adoption.DogAdoptionScheduler;
import com.aydlabs.bot.adoption.database.DogRepository;
import com.aydlabs.bot.producers.MyTopicProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MyAdoptionDogTest {

    @Mock
    private PromptChatMemoryAdvisor promptChatMemoryAdvisor;

    @Mock(answer = Answers.RETURNS_SELF)
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private JdbcClient jdbcClient;

    @Mock
    private DogRepository dogRepository;

    @Mock
    private VectorStore vectorStore;

    @Mock
    private DogAdoptionScheduler dogAdoptionScheduler;

    @Mock
    private MyTopicProducer producer;

    @Mock
    private ChatClient chatClient;

    private MockMvc mockMvc;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        final JdbcClient.StatementSpec stmtSpec = mock(JdbcClient.StatementSpec.class);
        final JdbcClient.MappedQuerySpec<Integer> mappedSpec = mock(JdbcClient.MappedQuerySpec.class);
        when(jdbcClient.sql(anyString())).thenReturn(stmtSpec);
        when(stmtSpec.query(Integer.class)).thenReturn(mappedSpec);
        when(mappedSpec.single()).thenReturn(1);

        when(chatClientBuilder.build()).thenReturn(chatClient);

        final MyAdoptionDog controller = new MyAdoptionDog(
                promptChatMemoryAdvisor, chatClientBuilder, jdbcClient,
                dogRepository, vectorStore, dogAdoptionScheduler, producer);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @SuppressWarnings("unchecked")
    void inquire_returnsAiResponse() throws Exception {
        final ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        final ChatClient.CallResponseSpec callSpec = mock(ChatClient.CallResponseSpec.class);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.advisors(any(Consumer.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callSpec);
        when(callSpec.content()).thenReturn("Hola!");

        mockMvc.perform(get("/testUser/assistant").param("question", "hola"))
               .andExpect(status().isOk())
               .andExpect(content().string("Hola!"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void inquire_passesConversationIdFromPathVariable() throws Exception {
        final ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        final ChatClient.CallResponseSpec callSpec = mock(ChatClient.CallResponseSpec.class);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.advisors(any(Consumer.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callSpec);
        when(callSpec.content()).thenReturn("OK");

        mockMvc.perform(get("/testUser/assistant").param("question", "hola"))
               .andExpect(status().isOk());

        final ArgumentCaptor<Consumer<ChatClient.AdvisorSpec>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(requestSpec).advisors(captor.capture());

        final ChatClient.AdvisorSpec advisorSpec = mock(ChatClient.AdvisorSpec.class);
        captor.getValue().accept(advisorSpec);
        verify(advisorSpec).param(ChatMemory.CONVERSATION_ID, "testUser");
    }

    @Test
    void sendMessage_callsProducerAndReturnsOK() throws Exception {
        mockMvc.perform(get("/testUser/notify").param("question", "test message"))
               .andExpect(status().isOk())
               .andExpect(content().string("OK"));

        verify(producer).send("testUser", "test message");
    }

    @Test
    void sendMessage_doesNotInvokeAi() throws Exception {
        mockMvc.perform(get("/testUser/notify").param("question", "test"))
               .andExpect(status().isOk());

        verifyNoInteractions(chatClient);
    }
}
