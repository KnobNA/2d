package com.niravramdhanie.twod.game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.niravramdhanie.twod.game.utils.ResourceLoader;

public class Block extends Entity {
    private BufferedImage blockImage;
    private Color color;
    
    public Block(float x, float y, int width, int height) {
        super(x, y, width, height);
        
        try {
            // Load block image
            blockImage = ResourceLoader.loadImage("/sprites/block.png");
            
            // Set a default color (used if image fails to load)
            color = new Color(50, 100, 150);
        } catch (Exception e) {
            System.err.println("Error loading block image: " + e.getMessage());
            blockImage = null;
            color = new Color(50, 100, 150);
        }
    }
    
    @Override
    public void update() {
        // Blocks are static in this example, so no update logic needed
    }
    
    @Override
    public void render(Graphics2D g) {
        try {
            if (blockImage != null) {
                g.drawImage(blockImage, (int)position.x, (int)position.y, width, height, null);
            } else {
                // Fallback if image isn't loaded
                g.setColor(color);
                g.fillRect((int)position.x, (int)position.y, width, height);
            }
        } catch (Exception e) {
            // Ultimate fallback
            System.err.println("Error rendering block: " + e.getMessage());
            g.setColor(Color.MAGENTA);
            g.fillRect((int)position.x, (int)position.y, width, height);
        }
    }
}