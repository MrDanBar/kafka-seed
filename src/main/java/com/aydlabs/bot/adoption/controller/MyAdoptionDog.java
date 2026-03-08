package com.aydlabs.bot.adoption.controller;

import com.aydlabs.bot.adoption.DogAdoptionScheduler;
import com.aydlabs.bot.adoption.database.DogRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@ResponseBody
class MyAdoptionDog {

    private static final String SYSTEM = """
            You are an AI powered assistant to help people adopt a dog from the adoption agency named "AYDLABS NE" with locations in Guatemala only. Information about the dogs available will be presented below. If there is no information, then return a polite response suggesting we don't have any dogs available. Spanish responses only.
            """;

    private final ChatClient ai;

    public MyAdoptionDog(final PromptChatMemoryAdvisor promptChatMemoryAdvisor,
                         final ChatClient.Builder ai,
                         final JdbcClient db,
                         final DogRepository repository,
                         final VectorStore vectorStore,
                         final DogAdoptionScheduler dogAdoptionScheduler) {
        var count = db
                .sql("select count(*) from vector_store")
                .query(Integer.class)
                .single();
        if (count == 0) {
            System.out.println("No vector store available");
            repository.findAll()
                      .forEach(dog -> {
                          var dogument = new Document("id: %s, name: %s, description: %s".formatted(dog.id(), dog.name(), dog.description()));
                          vectorStore.add(List.of(dogument));
                      });
        }

        this.ai = ai.defaultSystem(SYSTEM)
                    .defaultAdvisors(
                            promptChatMemoryAdvisor,
                            QuestionAnswerAdvisor.builder(vectorStore).build())
                    .defaultTools(dogAdoptionScheduler)
                    .build();
    }

    @GetMapping("/{user}/assistant")
    public String inquire(@PathVariable final String user, @RequestParam final String question) {
        return ai
                .prompt()
                .user(question)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, user))
                .call()
                .content();
    }
}