package com.hnp.ollamaspringai.util;

import jakarta.annotation.PostConstruct;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileReader {

    @Value("classpath:file1.docx")
    private Resource resource;

    private final VectorStore vectorStore;

    public FileReader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }


    @PostConstruct
    public void init() throws IOException, TikaException, SAXException {

        // default doc reader
//        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
//                .withPageExtractedTextFormatter(
//                        new ExtractedTextFormatter.Builder()
//                                .build())
//                .build();
//
//        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource, config);

        // tika doc reader

        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        AutoDetectParser parser = new AutoDetectParser();

        // Parse document
        parser.parse(resource.getInputStream(), handler, metadata, new ParseContext());



        List<Document> docs = new ArrayList<>();
        docs.add(new Document(handler.toString()));


        TokenTextSplitter textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(docs));

//        (vectorStore).save(new File("vectorestore.json"));



    }
}
