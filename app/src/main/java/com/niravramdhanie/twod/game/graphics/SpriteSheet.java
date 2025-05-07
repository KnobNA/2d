package com.niravramdhanie.twod.game.graphics;

import java.awt.image.BufferedImage;

public class SpriteSheet {
    private BufferedImage spriteSheet;
    private int spriteWidth;
    private int spriteHeight;
    
    public SpriteSheet(BufferedImage spriteSheet, int spriteWidth, int spriteHeight) {
        this.spriteSheet = spriteSheet;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        
        if (spriteSheet != null) {
            System.out.println("SpriteSheet created with dimensions: " + 
                              spriteSheet.getWidth() + "x" + spriteSheet.getHeight() + 
                              ", sprite size: " + spriteWidth + "x" + spriteHeight);
        } else {
            System.err.println("Warning: Null spritesheet provided to SpriteSheet constructor");
        }
    }
    
    public BufferedImage getSprite(int row, int col) {
        if (spriteSheet == null) {
            System.err.println("Cannot get sprite: spriteSheet is null");
            return null;
        }
        
        int x = col * spriteWidth;
        int y = row * spriteHeight;
        
        // Make sure we're not trying to get a sprite outside the sheet boundaries
        if (x >= spriteSheet.getWidth() || y >= spriteSheet.getHeight()) {
            System.err.println("Sprite coordinates out of bounds: (" + row + "," + col + ") -> (" + 
                             x + "," + y + ") from sheet " + spriteSheet.getWidth() + "x" + spriteSheet.getHeight());
            return null;
        }
        
        try {
            // Calculate actual width and height (for edge sprites)
            int width = Math.min(spriteWidth, spriteSheet.getWidth() - x);
            int height = Math.min(spriteHeight, spriteSheet.getHeight() - y);
            
            if (width <= 0 || height <= 0) {
                System.err.println("Invalid sprite dimensions: " + width + "x" + height);
                return null;
            }
            
            // Get the subimage
            BufferedImage sprite = spriteSheet.getSubimage(x, y, width, height);
            return sprite;
        } catch (Exception e) {
            System.err.println("Error extracting sprite at (" + row + "," + col + "): " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Gets the number of sprites that can fit horizontally in this sheet
     */
    public int getColumnCount() {
        return spriteSheet != null ? spriteSheet.getWidth() / spriteWidth : 0;
    }
    
    /**
     * Gets the number of sprites that can fit vertically in this sheet
     */
    public int getRowCount() {
        return spriteSheet != null ? spriteSheet.getHeight() / spriteHeight : 0;
    }
}