package com.kiral;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ParserUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(ParserUtil.class);

    public Elements getAllElements(File htmlFile) {
        return parseDocument(htmlFile).map(Element::getAllElements).orElse(new Elements());
    }

    public Optional<Element> findElementById(File htmlFile, String targetElementId) {
        return parseDocument(htmlFile).map(document -> document.getElementById(targetElementId));
    }

    private Optional<Document> parseDocument(File htmlFile) {
        try {
            return Optional.of(Jsoup.parse(htmlFile, StandardCharsets.UTF_8.name(), htmlFile.getAbsolutePath()));
        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }

    }
}
