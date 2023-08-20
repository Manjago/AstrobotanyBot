package com.temnenkov.astorobotanybot;

import com.temnenkov.astorobotanybot.protocol.GeminiContent;
import com.temnenkov.astorobotanybot.protocol.GeminiContentLoader;
import com.temnenkov.astorobotanybot.protocol.GeminiURLStreamHandlerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private final static Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) throws MalformedURLException {
        URL.setURLStreamHandlerFactory (new GeminiURLStreamHandlerFactory());
        var geminiContentLoader = new GeminiContentLoader();
        GeminiContent geminiContent = geminiContentLoader.loadGeminiContent(new URL("gemini://astrobotany.mozz.us/app/plant"));
        if (geminiContent.getException() != null) {
            System.out.println("Exception happens " + geminiContent.getException());
            logger.log(Level.SEVERE, "wow", geminiContent.getException());
        }
        if (geminiContent.getContent() != null) {
            System.out.println(new String(geminiContent.getContent()));
        }

    }
}
