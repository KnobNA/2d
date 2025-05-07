package com.niravramdhanie.twod.game.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ResourceLoader {
    // Cache loaded resources to avoid reloading the same resource multiple times
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();
    
    public static BufferedImage loadImage(String path) {
        // First check if the image is already in the cache
        if (imageCache.containsKey(path)) {
            System.out.println("Using cached image: " + path);
            return imageCache.get(path);
        }
        
        try {
            System.out.println("Attempting to load image: " + path);
            
            // Try different methods of loading the resource
            InputStream is = null;
            BufferedImage img = null;
            
            // Method 1: Standard class resource stream
            is = ResourceLoader.class.getResourceAsStream(path);
            
            // Method 2: Try with ClassLoader if method 1 fails
            if (is == null) {
                System.out.println("Trying alternate method with ClassLoader...");
                is = ResourceLoader.class.getClassLoader().getResourceAsStream(path.startsWith("/") ? path.substring(1) : path);
            }
            
            // Method 3: Try by URL if both previous methods fail
            if (is == null) {
                System.out.println("Trying alternate method with URL...");
                URL url = ResourceLoader.class.getResource(path);
                if (url != null) {
                    img = ImageIO.read(url);
                }
            }
            
            // Process the input stream if we have one and haven't loaded the image yet
            if (is != null && img == null) {
                img = ImageIO.read(is);
                is.close();
            }
            
            if (img == null) {
                System.err.println("Resource not found: " + path);
                System.err.println("Searched in classpath. Make sure the file exists in the correct location.");
                
                // Print some debug info about the classpath
                System.err.println("Working directory: " + System.getProperty("user.dir"));
                
                // Create a placeholder image with error pattern
                img = createPlaceholderImage(64, 64);
            } else {
                System.out.println("Successfully loaded image: " + path + " (" + img.getWidth() + "x" + img.getHeight() + ")");
                
                // Cache the successfully loaded image
                imageCache.put(path, img);
            }
            
            return img;
            
        } catch (IOException e) {
            System.err.println("Failed to load image: " + path);
            e.printStackTrace();
            // Create a placeholder image
            return createPlaceholderImage(64, 64);
        } catch (Exception e) {
            System.err.println("Unexpected error loading image: " + path);
            e.printStackTrace();
            return createPlaceholderImage(64, 64);
        }
    }
    
    private static BufferedImage createPlaceholderImage(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        // Draw error pattern (checkered magenta/black)
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        
        g.setColor(Color.MAGENTA);
        int tileSize = 8;
        for (int x = 0; x < width; x += tileSize) {
            for (int y = 0; y < height; y += tileSize) {
                if ((x / tileSize + y / tileSize) % 2 == 0) {
                    g.fillRect(x, y, tileSize, tileSize);
                }
            }
        }
        
        g.dispose();
        return img;
    }
    
    /**
     * Clears the resource cache
     */
    public static void clearCache() {
        imageCache.clear();
        System.out.println("Resource cache cleared");
    }
}