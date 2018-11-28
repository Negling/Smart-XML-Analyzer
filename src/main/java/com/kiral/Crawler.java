package com.kiral;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Crawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    private static final String DEFAULT_ELEMENT_ID = "make-everything-ok-button";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String RESULT_DELIMITER = LINE_SEPARATOR + "AND" + LINE_SEPARATOR;

    private ParserUtil parserUtil;

    /*
     * Its always better to escape static utils and dependencies for sake of further testing
     */
    public Crawler(ParserUtil parserUtil) {
        this.parserUtil = parserUtil;
    }

    public Crawler() {
        this(new ParserUtil());
    }

    public void printMostRelevant(String pathToOriginalFile, String pathToDuplicate, String elementId) {
        List<String> pathsToMostRelevant = findMostRelevant(pathToOriginalFile, pathToDuplicate, elementId).stream()
                .map(this::generatePathToElement)
                .collect(Collectors.toList());

        if (pathsToMostRelevant.isEmpty()) {
            LOGGER.info("No matching elements were found! I'm sorry! :(");
        } else if (pathsToMostRelevant.size() == 1) {
            LOGGER.info("Most relevant element was found! Path: {}", pathsToMostRelevant.get(0));
        } else {
            String message = pathsToMostRelevant.stream().collect(Collectors.joining(RESULT_DELIMITER, LINE_SEPARATOR, ""));

            LOGGER.info("There is several element matches with same score: {}", message);
        }
    }

    private String generatePathToElement(Element element) {
        Deque<String> elementHierarchyValues = new ArrayDeque<>();
        elementHierarchyValues.add(surroundWithBrackets(element.text()));

        Element currentElement = element;

        while (nonNull(currentElement)) {
            String elementName = currentElement.tagName();

            if (currentElement.elementSiblingIndex() > 0) {
                elementName = elementName + surroundWithBrackets(String.valueOf(currentElement.siblingIndex()));
            }

            elementHierarchyValues.addFirst(elementName);
            currentElement = currentElement.parent();
        }

        return String.join(" -> ", elementHierarchyValues);
    }

    private String surroundWithBrackets(String target) {
        return "[" + target + "]";
    }

    private List<Element> findMostRelevant(String pathToOriginalFile, String pathToDuplicate, String elementId) {
        // fallback if no custom element id provided
        String id = isNull(elementId) ? DEFAULT_ELEMENT_ID : elementId;

        File originalFile = new File(pathToOriginalFile);
        File duplicateFile = new File(pathToDuplicate);

        return parserUtil.findElementById(originalFile, id)
                .map(this::collectMetadata)
                .map(metadata -> findMostRelevantByMetadata(metadata, parserUtil.getAllElements(duplicateFile)))
                .orElse(new ArrayList<>());
    }

    private List<Element> findMostRelevantByMetadata(Metadata metadata, Elements duplicateFileElements) {
        List<Element> mostRelevant = new ArrayList<>();
        int maxMatchScore = metadata.attributes.size() + 1;
        int currentMaxMatchScore = 0;

        for (Element element : duplicateFileElements) {
            int currentMatchScore = calculateMatchScore(metadata, element);

            if (currentMatchScore == maxMatchScore) {
                mostRelevant.add(element);

                // simply return on perfect match
                return mostRelevant;
            } else if (currentMatchScore > currentMaxMatchScore) {
                mostRelevant.clear();
                mostRelevant.add(element);

                currentMaxMatchScore = currentMatchScore;
            } else if (currentMatchScore > 0 && currentMatchScore == currentMaxMatchScore) {
                // if element has match score same as current max score - save it as one of possible matches
                mostRelevant.add(element);
            }
        }

        return mostRelevant;
    }

    private Metadata collectMetadata(Element element) {
        // skip 'id' element from matching, as it present only at prototype element
        Attributes attributes = element.attributes().clone();
        attributes.remove("id");

        return new Metadata(attributes, element.text());
    }

    private int calculateMatchScore(Metadata metadata, Element currentElement) {
        int matchScore = 0;

        for (Attribute attribute : metadata.getAttributes()) {
            String targetAttributeValue = currentElement.attr(attribute.getKey());

            if (!targetAttributeValue.isEmpty() && attribute.getValue().equals(targetAttributeValue)) {
                matchScore++;
            }
        }

        if (metadata.getText().equals(currentElement.text())) {
            matchScore++;
        }

        return matchScore;
    }

    private static class Metadata {
        private final Attributes attributes;
        private final String text;

        private Metadata(Attributes attributes, String text) {
            this.attributes = attributes;
            this.text = text;
        }

        public Attributes getAttributes() {
            return attributes;
        }

        public String getText() {
            return text;
        }
    }
}
