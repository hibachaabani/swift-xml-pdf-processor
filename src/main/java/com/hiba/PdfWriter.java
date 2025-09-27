package com.hiba;

import com.hiba.PdfLayoutConfig;
import com.hiba.XmlProcessor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PdfWriter {

    public static class ProcessResult {
        public PDPageContentStream content;
        public float y;

        public ProcessResult(PDPageContentStream content, float y) {
            this.content = content;
            this.y = y;
        }
    }

    // Titre centré
    public void drawTitle(PDPageContentStream content, String title, PDType1Font font) throws IOException {
        float fontSize = PdfLayoutConfig.TITLE_SIZE;
        float titleWidth = font.getStringWidth(title) / 1000 * fontSize;
        float pageWidth = PDRectangle.A4.getWidth();
        float titleX = (pageWidth - titleWidth) / 2;
        float titleY = PDRectangle.A4.getHeight() - 20;

        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(titleX, titleY);
        content.showText(title);
        content.endText();
    }

    // Traitement récursif XML → PDF
    public ProcessResult processNode(PDDocument doc, PDPageContentStream content, Node node, int indent,
                                     float y, Set<String> Sections, Set<String> subSections, Set<String> subSubSections,
                                     Map<String, String> labelMapping, XmlProcessor xmlProcessor) throws IOException {

        if (y < PdfLayoutConfig.BOTTOM_MARGIN) {
            content.close();
            PDPage newPage = new PDPage(PDRectangle.A4);
            doc.addPage(newPage);
            content = new PDPageContentStream(doc, newPage);
            y = PdfLayoutConfig.START_Y;
        }

        String nodeName = node.getNodeName();
        String localName = nodeName.contains(":") ? nodeName.split(":", 2)[1] : nodeName;
        String label = labelMapping.getOrDefault(localName, localName);
        String value = xmlProcessor.getDirectText(node);
        boolean isSection = Sections.contains(localName);
        boolean isSubSection = subSections.contains(localName);
        boolean isSubSubSection = subSubSections.contains(localName);

        ProcessResult result;

        // Fr / To
        if (nodeName.equals("Fr") || nodeName.equals("To")) {
            Node firstValueNode = getFirstValueElement(node);
            if (firstValueNode != null) {
                String tag = labelMapping.getOrDefault(localName, localName);
                String val = cleanText(firstValueNode.getTextContent().trim());
                if (!val.isEmpty()) {
                    result = drawText(doc, content, indent + 1, "- " + tag, val, y, false, false);
                    content = result.content;
                    y = result.y;
                }
            }
            return new ProcessResult(content, y);
        }

        if (isSection) {
            result = drawSection(doc, content, indent, label, y);
        } else if (isSubSection) {
            result = drawText(doc, content, indent, "• " + label, "", y, true, false);
        } else if (isSubSubSection) {
            int subSubIndent = indent + 2;
            result = drawText(doc, content, subSubIndent, label, "", y, true, true);
        } else if (!value.isEmpty()) {
            result = drawText(doc, content, indent + 1, "- " + label, value, y, false, false);
        } else {
            result = new ProcessResult(content, y);
        }

        content = result.content;
        y = result.y;

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) continue;

            int childIndent = isSubSubSection ? indent + 2 : indent + (isSection ? 1 : 0);
            ProcessResult res = processNode(doc, content, child, childIndent, y,
                    Sections, subSections, subSubSections, labelMapping, xmlProcessor);
            content = res.content;
            y = res.y;
        }

        return new ProcessResult(content, y);
    }

    // Texte normal, gras ou subSubSection
    // Texte normal, gras ou subSubSection
    public ProcessResult drawText(PDDocument doc, PDPageContentStream content, int indent, String key,
                                  String value, float y, boolean isBold, boolean isSubSubSection) throws IOException {
        PDType1Font font = isBold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA;

        float fontSize;
        float xPos;

        if (isSubSubSection) {
            fontSize = PdfLayoutConfig.SUBSUBSECTION_SIZE;
            xPos = PdfLayoutConfig.MARGIN + PdfLayoutConfig.SUBSUBSECTION_INDENT;
        } else if (isBold) { // SubSection
            fontSize = PdfLayoutConfig.SUBSECTION_SIZE;
            xPos = PdfLayoutConfig.MARGIN + PdfLayoutConfig.SUBSECTION_INDENT + indent * PdfLayoutConfig.INDENT_SIZE;
        } else { // texte normal
            fontSize = PdfLayoutConfig.TEXT_SIZE_NORMAL;
            xPos = PdfLayoutConfig.MARGIN + indent * PdfLayoutConfig.INDENT_SIZE;
        }

        float maxWidth = PdfLayoutConfig.MAX_WIDTH - (xPos - PdfLayoutConfig.MARGIN);

        // Préparer le texte
        String textToPrint;
        if (isSubSubSection) {
            textToPrint = "- " + key;
            if (!value.isEmpty()) {
                textToPrint += " : " + value;
            }
        } else {
            textToPrint = value.isEmpty() ? key : key + " : " + value;
        }

        // Découpage du texte
        List<String> lines = wrapText(textToPrint, font, fontSize, maxWidth);

        // Écriture sur le PDF
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(xPos, y);
        for (String line : lines) {
            content.showText(line);
            y -= PdfLayoutConfig.LINE_HEIGHT;
        }
        content.endText();

        return new ProcessResult(content, y);
    }

    // Dessiner une Section
    public ProcessResult drawSection(PDDocument doc, PDPageContentStream content, int indent, String title, float y) throws IOException {
        PDType1Font font = PDType1Font.HELVETICA_BOLD;
        float fontSize = PdfLayoutConfig.SECTION_SIZE;

        float xPos = PdfLayoutConfig.MARGIN + PdfLayoutConfig.SECTION_INDENT + indent * PdfLayoutConfig.INDENT_SIZE;

        List<String> lines = wrapText(title, font, fontSize, PdfLayoutConfig.MAX_WIDTH - xPos + PdfLayoutConfig.MARGIN);

        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(xPos, y);
        for (String line : lines) {
            content.showText(line);
            y -= PdfLayoutConfig.LINE_HEIGHT;
        }
        content.endText();

        // Ligne sous la section
        y -= PdfLayoutConfig.SECTION_SPACING;
        content.moveTo(xPos, y);
        content.lineTo(xPos + PdfLayoutConfig.SECTION_LINE_WIDTH, y);
        content.stroke();

        y -= PdfLayoutConfig.LINE_HEIGHT;
        y -= PdfLayoutConfig.BETWEEN_SECTIONS_SPACING;

        return new ProcessResult(content, y);
    }

    // Wrapping du texte
    private List<String> wrapText(String text, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        float currentWidth = 0;
        for (String word : text.split(" ")) {
            float wordWidth = font.getStringWidth(word + " ") / 1000 * fontSize;
            if (currentWidth + wordWidth > maxWidth) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString().trim());
                    currentLine = new StringBuilder();
                    currentWidth = 0;
                }
            }
            currentLine.append(word).append(" ");
            currentWidth += wordWidth;
        }
        if (currentLine.length() > 0) lines.add(currentLine.toString().trim());
        return lines;
    }

    private Node getFirstValueElement(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && child.getTextContent() != null
                    && !child.getTextContent().trim().isEmpty()) {
                return child;
            }
        }
        return null;
    }

    private String cleanText(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }
}
