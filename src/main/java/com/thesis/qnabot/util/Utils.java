package com.thesis.qnabot.util;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Data
@Builder
@Slf4j
public class Utils {

    public static String toString(MultipartFile file) {

        String parsedText = "";
        try {
            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();


            if (contentType != null) {

                if (contentType.equals("application/pdf")) {
                    parsedText = getStringFromPdf(file);
                } else if (contentType.equals("text/plain")) {
                    parsedText = getStringFromTxt(file);
                } else {
                    throw new RuntimeException("Unsupported file type: " + contentType);
                }

            } else {

                if (fileName == null) {
                    throw new RuntimeException("File doesn't have a name");
                }

                log.warn("Content type not provided. Checking by file extension.");
                if (fileName.endsWith(".pdf")) {
                    parsedText = getStringFromPdf(file);
                } else if (fileName.endsWith(".txt")) {
                    parsedText = getStringFromTxt(file);
                } else {
                    throw new RuntimeException("Unsupported file type (no content type or unrecognized extension): " + fileName);
                }

            }

            log.info(parsedText);
        } catch (Exception e) {
            log.error("Failed to parse file", e);
        }

        return parsedText;
    }

    private static String getStringFromTxt(MultipartFile file) throws IOException {

        String parsedText;

        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        StringBuilder textBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            textBuilder.append(line).append("\n");
        }
        parsedText = textBuilder.toString();
        reader.close();

        return parsedText;
    }

    private static String getStringFromPdf(MultipartFile file) throws IOException {

        String parsedText;

        // Create a temporary file and copy the content of the MultipartFile to it
        File tempFile = File.createTempFile("temp", ".pdf");

        OutputStream os = new FileOutputStream(tempFile);
        os.write(file.getBytes());

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
        if (!tempFile.delete()) {
            log.warn("Could not delete tempFile " + tempFile.getName());
        }

        log.info(parsedText);

        return parsedText;
    }
}
