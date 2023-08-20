package com.temnenkov.astorobotanybot;

import com.temnenkov.astorobotanybot.business.Plant;
import com.temnenkov.astorobotanybot.protocol.GeminiContentLoader;
import com.temnenkov.astorobotanybot.protocol.GeminiURLStreamHandlerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());


    public static void main(String[] args) {
        try{
            URL.setURLStreamHandlerFactory (new GeminiURLStreamHandlerFactory());
            final var plant = new Plant("gemini://astrobotany.mozz.us/app/visit/681e29e57c88440db684a72e38bb041b").load();
            final int waterQty = plant.waterQty();
            System.out.println("before: " + waterQty);
            if (waterQty < 75) {
                plant.doWater();
            }
            final int waterQtyAfter = plant.load().waterQty();
            System.out.println("after: " + waterQtyAfter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tost() throws MalformedURLException {
        URL.setURLStreamHandlerFactory (new GeminiURLStreamHandlerFactory());
        final var geminiContent = GeminiContentLoader.loadGeminiContent(new URL("gemini://astrobotany.mozz.us/app/plant"));
        if (geminiContent.getException() != null) {
            System.out.println("Exception happens " + geminiContent.getException());
            logger.log(Level.SEVERE, "wow", geminiContent.getException());
        }
        if (geminiContent.getContent() != null) {
            System.out.println(new String(geminiContent.getContent()));
        }

    }
}
