package com.hnp.ollamaspringai.resource;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/resource/chat")
public class ChatResource {

    Logger logger = Logger.getLogger(ChatResource.class.getName());

    private final ChatModel chatModel;
    private final VectorStore vectorStore;

    private final String PROMPT_TEMPLATE = """
     Answer the query strictly referring the provided context:
          {context}
      Query:
          {query}
      In case you don't have any answer from the context provided, just say:
          I'm sorry I don't have the information you are looking for.
    """;

    public ChatResource(ChatModel chatModel, VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }

    @PostMapping("/get-message")
    public String prompt(@RequestBody() String body) {

        logger.info("entering get-message, body=" + body);

        JsonParser springParser = JsonParserFactory.getJsonParser();
        Map<String, Object> map = springParser.parseMap(body);
        String message = map.get("msg").toString();


        List<Document> documents = vectorStore.similaritySearch(message);
        String info = documents.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining(System.lineSeparator()));

        for(Document document : documents) {
            System.out.println(document.getFormattedContent());
        }

        PromptTemplate promptTemplate = new PromptTemplate(PROMPT_TEMPLATE);
        promptTemplate.add("query", message);
        promptTemplate.add("context", info);
        String prompt = promptTemplate.render();

        return chatModel.call(prompt);
    }
}
