package com.hiba;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class XmlToPdfGenerator {

    private static Map<String, String> labelMapping;
    private static Set<String> Sections;
    private static Set<String> subSections;
    private static Set<String> subSubSections;

    public static void main(String[] args) {
        try {
            // Charger mappings et sections
            labelMapping = XmlProcessor.loadLabelMapping("/Label-mapping.properties");
            Map<String, Set<String>> sectionsMap = XmlProcessor.loadSections("/sections.json");
            Sections = sectionsMap.getOrDefault("Sections", Set.of());
            subSections = sectionsMap.getOrDefault("subSections", Set.of());
            subSubSections = sectionsMap.getOrDefault("subSubSections", Set.of());

            // Charger le XML
            Document xmlDoc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(XmlToPdfGenerator.class.getResourceAsStream("/data.xml"));
            xmlDoc.getDocumentElement().normalize();

            // Créer le PDF
            PDDocument pdfDoc = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            pdfDoc.addPage(page);

            PdfWriter pdfWriter = new PdfWriter();
            PDPageContentStream content = new PDPageContentStream(pdfDoc, page);


            String title = "FI To FI Customer Credit Transfer";
            pdfWriter.drawTitle(content, title, PDType1Font.HELVETICA_BOLD);



            // Processus XML → PDF
            XmlProcessor xmlProcessor = new XmlProcessor();
            float y = PdfLayoutConfig.START_Y;
            PdfWriter.ProcessResult result = pdfWriter.processNode(pdfDoc, content, xmlDoc.getDocumentElement(), 0, y,
                    Sections, subSections, subSubSections, labelMapping, xmlProcessor);
            result.content.close();

            // Sauvegarder PDF
            Path outputDir = Paths.get(System.getProperty("user.dir"), "output");
            Files.createDirectories(outputDir);
            pdfDoc.save(outputDir.resolve("user_friendly.pdf").toFile());
            pdfDoc.close();

            System.out.println("PDF généré avec succès à : " + outputDir.resolve("output.pdf"));

        } catch (Exception e) {
            System.err.println("Échec de la génération du PDF : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
