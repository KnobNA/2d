package com.niravramdhanie.twod.game.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Utility class to generate tilable texture patterns for blocks
 */
public class TextureGenerator {
    
    private static final Random random = new Random();
    
    /**
     * Generates a set of tilable stone brick textures
     * @param outputDir The directory to save textures to
     */
    public static void generateBlockTextures(String outputDir) {
        try {
            // Create output directory if it doesn't exist
            File outDir = new File(outputDir);
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            
            // Generate different texture types
            generateStoneBrickTexture(new File(outDir, "stone_brick1.png"), 64, 64, 
                    new Color(80, 80, 80), new Color(120, 120, 120));
            
            generateStoneBrickTexture(new File(outDir, "stone_brick2.png"), 64, 64, 
                    new Color(100, 90, 80), new Color(140, 130, 120));
            
            generateStoneBrickTexture(new File(outDir, "stone_brick3.png"), 64, 64, 
                    new Color(70, 90, 100), new Color(100, 120, 130));
            
            generateRoughStoneTexture(new File(outDir, "rough_stone.png"), 64, 64, 
                    new Color(90, 90, 90), new Color(120, 120, 120));
            
            generateMossyStoneTexture(new File(outDir, "mossy_stone.png"), 64, 64, 
                    new Color(80, 85, 75), new Color(110, 115, 105));
            
            System.out.println("Successfully generated texture files in: " + outputDir);
        } catch (Exception e) {
            System.err.println("Error generating textures: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generates a tilable stone brick texture
     * @param outputFile The file to save to
     * @param width Texture width
     * @param height Texture height
     * @param baseColor The base stone color
     * @param accentColor The mortar/accent color
     */
    private static void generateStoneBrickTexture(File outputFile, int width, int height, 
            Color baseColor, Color accentColor) throws IOException {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        // Fill background with mortar color
        g.setColor(accentColor);
        g.fillRect(0, 0, width, height);
        
        // Draw the brick pattern
        g.setColor(baseColor);
        
        // Brick sizes and spacing
        int brickWidth = width / 3;
        int brickHeight = height / 4;
        int mortarSize = 2;
        
        // First row - full bricks
        g.fillRect(0, 0, brickWidth - mortarSize, brickHeight - mortarSize);
        g.fillRect(brickWidth, 0, brickWidth - mortarSize, brickHeight - mortarSize);
        g.fillRect(brickWidth * 2, 0, brickWidth, brickHeight - mortarSize); // Last brick wraps around
        
        // Second row - offset bricks (half brick at start)
        g.fillRect(0, brickHeight, brickWidth/2 - mortarSize, brickHeight - mortarSize);
        g.fillRect(brickWidth/2, brickHeight, brickWidth - mortarSize, brickHeight - mortarSize);
        g.fillRect(brickWidth/2 + brickWidth, brickHeight, brickWidth - mortarSize, brickHeight - mortarSize);
        g.fillRect(brickWidth/2 + brickWidth * 2, brickHeight, brickWidth/2, brickHeight - mortarSize);
        
        // Third row - repeat first row pattern
        g.fillRect(0, brickHeight * 2, brickWidth - mortarSize, brickHeight - mortarSize);
        g.fillRect(brickWidth, brickHeight * 2, brickWidth - mortarSize, brickHeight - mortarSize);
        g.fillRect(brickWidth * 2, brickHeight * 2, brickWidth, brickHeight - mortarSize);
        
        // Fourth row - repeat second row pattern
        g.fillRect(0, brickHeight * 3, brickWidth/2 - mortarSize, brickHeight);
        g.fillRect(brickWidth/2, brickHeight * 3, brickWidth - mortarSize, brickHeight);
        g.fillRect(brickWidth/2 + brickWidth, brickHeight * 3, brickWidth - mortarSize, brickHeight);
        g.fillRect(brickWidth/2 + brickWidth * 2, brickHeight * 3, brickWidth/2, brickHeight);
        
        // Add slight noise/variation to make it look more natural
        addNoiseToTexture(img, 10);
        
        g.dispose();
        ImageIO.write(img, "PNG", outputFile);
    }
    
    /**
     * Generates a rough stone texture (more irregular pattern)
     */
    private static void generateRoughStoneTexture(File outputFile, int width, int height,
            Color baseColor, Color accentColor) throws IOException {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        // Fill with base color
        g.setColor(baseColor);
        g.fillRect(0, 0, width, height);
        
        // Create irregular stone pattern
        g.setColor(accentColor);
        
        // Create cracks and variations
        int numCracks = 15;
        for (int i = 0; i < numCracks; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = x1 + random.nextInt(width/4) - width/8;
            int y2 = y1 + random.nextInt(height/4) - height/8;
            
            g.drawLine(x1, y1, x2, y2);
        }
        
        // Draw some irregular stone shapes
        int numStones = 8;
        for (int i = 0; i < numStones; i++) {
            // Slightly lighter variant of base color
            g.setColor(new Color(
                    Math.min(255, baseColor.getRed() + random.nextInt(30) - 10),
                    Math.min(255, baseColor.getGreen() + random.nextInt(30) - 10),
                    Math.min(255, baseColor.getBlue() + random.nextInt(30) - 10)
            ));
            
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int stoneWidth = width/4 + random.nextInt(width/4);
            int stoneHeight = height/4 + random.nextInt(height/4);
            
            g.fillOval(x, y, stoneWidth, stoneHeight);
        }
        
        // Add noise for texture
        addNoiseToTexture(img, 20);
        
        g.dispose();
        ImageIO.write(img, "PNG", outputFile);
    }
    
    /**
     * Generates a mossy stone brick texture
     */
    private static void generateMossyStoneTexture(File outputFile, int width, int height,
            Color baseColor, Color accentColor) throws IOException {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        // First generate a regular stone brick texture
        g.setColor(accentColor);
        g.fillRect(0, 0, width, height);
        
        // Draw the brick pattern
        g.setColor(baseColor);
        
        // Brick sizes and spacing
        int brickWidth = width / 3;
        int brickHeight = height / 4;
        int mortarSize = 2;
        
        // First row
        g.fillRect(0, 0, brickWidth - mortarSize, brickHeight - mortarSize);
        g.fillRect(brickWidth, 0, brickWidth - mortarSize, brickHeight - mortarSize);
        g.fillRect(brickWidth * 2, 0, brickWidth, brickHeight - mortarSize);
        
        // Second row
        g.fillRect(0, brickHeight, brickWidth/2 - mortarSize, brickHeight - mortarSize);
        g.fillRect(brickWidth/2, brickHeight, brickWidth - mortarSize, brickHeight - mortarSize);
        g.fillRect(brickWidth/2 + brickWidth, brickHeight, brickWidth - mortarSize, brickHeight - mortarSize);
        g.fillRect(brickWidth/2 + brickWidth * 2, brickHeight, brickWidth/2, brickHeight - mortarSize);
        
        // Third row
        g.fillRect(0, brickHeight * 2, brickWidth - mortarSize, brickHeight - mortarSize);
        g.fillRect(brickWidth, brickHeight * 2, brickWidth - mortarSize, brickHeight - mortarSize);
        g.fillRect(brickWidth * 2, brickHeight * 2, brickWidth, brickHeight - mortarSize);
        
        // Fourth row
        g.fillRect(0, brickHeight * 3, brickWidth/2 - mortarSize, brickHeight);
        g.fillRect(brickWidth/2, brickHeight * 3, brickWidth - mortarSize, brickHeight);
        g.fillRect(brickWidth/2 + brickWidth, brickHeight * 3, brickWidth - mortarSize, brickHeight);
        g.fillRect(brickWidth/2 + brickWidth * 2, brickHeight * 3, brickWidth/2, brickHeight);
        
        // Now add moss in some areas
        Color mossColor = new Color(20, 130, 40, 180); // Semi-transparent green
        g.setColor(mossColor);
        
        // Add moss patches
        int numMossPatches = 12;
        for (int i = 0; i < numMossPatches; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int patchSize = 5 + random.nextInt(15);
            
            g.fillOval(x, y, patchSize, patchSize);
        }
        
        // Add moss along some edges
        g.setColor(new Color(30, 150, 50, 100));
        for (int i = 0; i < width; i += 4) {
            if (random.nextBoolean()) {
                int mossHeight = 2 + random.nextInt(4);
                g.fillRect(i, 0, 4, mossHeight);
            }
            if (random.nextBoolean()) {
                int mossHeight = 2 + random.nextInt(4);
                g.fillRect(i, height - mossHeight, 4, mossHeight);
            }
        }
        
        for (int i = 0; i < height; i += 4) {
            if (random.nextBoolean()) {
                int mossWidth = 2 + random.nextInt(4);
                g.fillRect(0, i, mossWidth, 4);
            }
            if (random.nextBoolean()) {
                int mossWidth = 2 + random.nextInt(4);
                g.fillRect(width - mossWidth, i, mossWidth, 4);
            }
        }
        
        // Add noise for texture
        addNoiseToTexture(img, 15);
        
        g.dispose();
        ImageIO.write(img, "PNG", outputFile);
    }
    
    /**
     * Adds noise to the texture to create a more natural look
     */
    private static void addNoiseToTexture(BufferedImage img, int intensity) {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                
                Color color = new Color(rgb, true);
                
                // Apply slight variation to each color channel
                int noiseR = random.nextInt(intensity) - intensity/2;
                int noiseG = random.nextInt(intensity) - intensity/2;
                int noiseB = random.nextInt(intensity) - intensity/2;
                
                int newR = Math.max(0, Math.min(255, color.getRed() + noiseR));
                int newG = Math.max(0, Math.min(255, color.getGreen() + noiseG));
                int newB = Math.max(0, Math.min(255, color.getBlue() + noiseB));
                
                Color newColor = new Color(newR, newG, newB, color.getAlpha());
                img.setRGB(x, y, newColor.getRGB());
            }
        }
    }
    
    /**
     * Main method to generate textures
     */
    public static void main(String[] args) {
        String outputDir = "app/src/main/resources/sprites/blocks";
        if (args.length > 0) {
            outputDir = args[0];
        }
        
        generateBlockTextures(outputDir);
    }
}
