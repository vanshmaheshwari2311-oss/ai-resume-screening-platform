package com.vansh.resume_screening;

import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@Service
public class ResumeTextExtractor {

    public String extractText(MultipartFile file) {

        try {

            // Create Apache Tika parser
            AutoDetectParser parser = new AutoDetectParser();

            // Allow unlimited text extraction
            BodyContentHandler handler =
                    new BodyContentHandler(-1);

            // Store file metadata
            Metadata metadata = new Metadata();

            // Parser context
            ParseContext context = new ParseContext();

            // Parse the uploaded PDF/DOCX file
            parser.parse(
                    file.getInputStream(),
                    handler,
                    metadata,
                    context
            );

            // Get extracted text
            String extractedText = handler.toString();

            // Make sure null is never returned
            if (extractedText == null) {
                return "";
            }

            return extractedText.trim();

        } catch (IOException | SAXException | TikaException e) {

            // Print a simple error message instead of stack trace
            System.err.println(
                    "Resume text extraction failed: "
                    + e.getMessage()
            );

            return "";
        }
    }
}