package com.kiral;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final int FULL_ARGS_LENGTH = 3;
    private static final int ONLY_PATH_ARGS_LENGTH = 2;

    public static void main(String[] args) {
        if (args.length == ONLY_PATH_ARGS_LENGTH || args.length == FULL_ARGS_LENGTH) {
            String customId = null;
            String pathToOriginalFile = args[0];
            String pathToDuplicate = args[1];

            LOGGER.info("All mandatory parameters are set. Path to original file: '{}', path to duplicate: '{}'",
                    pathToOriginalFile, pathToDuplicate);

            if (args.length == FULL_ARGS_LENGTH) {
                customId = args[2];
                LOGGER.info("Custom id were provided! Value: {}", customId);
            }

            Crawler crawler = new Crawler();
            crawler.printMostRelevant(pathToOriginalFile, pathToDuplicate, customId);
        } else {
            LOGGER.warn("Please provide path to original file and its diff!");
        }


    }
}
