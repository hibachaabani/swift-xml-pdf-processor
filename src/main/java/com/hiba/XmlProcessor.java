package com.hiba;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class XmlProcessor {

    public String getDirectText(Node node) {
        StringBuilder sb = new StringBuilder();
        NamedNodeMap attributes = node.getAttributes();
        Node currencyAttr = (attributes != null) ? attributes.getNamedItem("Ccy") : null;
        String currency = (currencyAttr != null) ? currencyAttr.getTextContent().trim() : "";

        NodeList kids = node.getChildNodes();
        for (int i = 0; i < kids.getLength(); i++) {
            Node kid = kids.item(i);
            if (kid.getNodeType() == Node.TEXT_NODE) {
                String text = kid.getTextContent().trim();
                if (!text.isEmpty()) sb.append(text);
            }
        }
        if (!sb.toString().isEmpty() && !currency.isEmpty()) sb.append(" ").append(currency);
        return sb.toString();
    }

    //  le premier élément non vide d’un noeud
    public Node getFirstValueElement(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String text = cleanText(child.getTextContent().trim());
                if (!text.isEmpty()) return child;
                Node deeper = getFirstValueElement(child);
                if (deeper != null) return deeper;
            }
        }
        return null;
    }



    //  espaces multiples
    private String cleanText(String input) {
        if (input == null) return "";
        return input.replaceAll("[\\uFFFD]", " ")
                .replaceAll(":+", ":")
                .replaceAll("\\s+", " ")
                .trim();
    }

    // Charger les mappings XML → labels
    public static Map<String, String> loadLabelMapping(String resourcePath) throws IOException {
        Properties props = new Properties();
        try (InputStream is = XmlProcessor.class.getResourceAsStream(resourcePath)) {
            if (is == null) throw new FileNotFoundException(resourcePath + " introuvable");
            props.load(new InputStreamReader(is, StandardCharsets.UTF_8));
        }
        Map<String, String> map = new HashMap<>();
        for (String key : props.stringPropertyNames()) map.put(key, props.getProperty(key));
        return map;
    }

    // Charger les sections/subSections/subSubSections depuis JSON
    public static Map<String, Set<String>> loadSections(String resourcePath) throws IOException {
        try (InputStream is = XmlProcessor.class.getResourceAsStream(resourcePath)) {
            if (is == null) throw new FileNotFoundException(resourcePath + " introuvable");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<String>> sections = mapper.readValue(is, Map.class);
            Map<String, Set<String>> result = new HashMap<>();
            result.put("Sections", new HashSet<>(sections.getOrDefault("Sections", Collections.emptyList())));
            result.put("subSections", new HashSet<>(sections.getOrDefault("subSections", Collections.emptyList())));
            result.put("subSubSections", new HashSet<>(sections.getOrDefault("subSubSections", Collections.emptyList())));
            return result;
        }
    }
}
