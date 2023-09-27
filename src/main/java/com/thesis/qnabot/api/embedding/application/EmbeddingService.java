package com.thesis.qnabot.api.embedding.application;

import com.thesis.qnabot.api.embedding.application.port.in.GetEmbeddingUseCase;
import com.thesis.qnabot.api.embedding.application.port.in.PdfToStringUseCase;
import com.thesis.qnabot.api.embedding.application.port.out.OpenAiEmbeddingReadPort;
import com.thesis.qnabot.api.embedding.domain.Embedding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService implements GetEmbeddingUseCase, PdfToStringUseCase {

    private final OpenAiEmbeddingReadPort openAiEmbeddingReadPort;

    public Embedding getEmbedding(String apiKey, String input) {
        return Embedding.builder()
                .index(input)
                .values(openAiEmbeddingReadPort.getEmbedding(apiKey, input))
                .build();
    }

    @Override
    public String toString(MultipartFile file) {
        String parsedText = "";
        try {

            // Create a temporary file and copy the content of the MultipartFile to it
            File tempFile = File.createTempFile("temp", null);
            try (OutputStream os = new FileOutputStream(tempFile)) {
                os.write(file.getBytes());
            }

            // Create a RandomAccessRead from the temporary file
            RandomAccessRead randomAccessRead = new RandomAccessBufferedFileInputStream(tempFile);

            // Create a PDFParser and parse the RandomAccessRead
            PDFParser parser = new PDFParser(randomAccessRead);
            parser.parse();

            COSDocument cosDoc = parser.getDocument();
            PDFTextStripper pdfStripper = new PDFTextStripper();
            PDDocument pdDoc = new PDDocument(cosDoc);
            parsedText = pdfStripper.getText(pdDoc);

            // Close resources and delete the temporary file
            pdDoc.close();
            randomAccessRead.close();
            tempFile.delete();

            log.info(parsedText);
        } catch (Exception e) {
            log.error("Failed to parse file", e);
        }

        return parsedText;
    }
}
