package com.niravramdhanie.twod.game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import com.niravramdhanie.twod.game.utils.ResourceLoader;

/**
 * A block entity that represents a wall or obstacle in the game.
 * Uses the wall.png texture from the sprites folder.
 */
public class Block extends Entity {
    private static final Color FALLBACK_COLOR = new Color(50, 100, 150);
    
    // Single cached instance of the wall texture
    private static BufferedImage wallTexture = null;
    
    // Instance fields
    private final BufferedImage blockImage;
    private final Color color;
    
    public Block(float x, float y, int width, int height) {
        super(x, y, width, height);
        
        // Load wall texture if not already loaded
        synchronized (Block.class) {
            if (wallTexture == null) {
                try {
                    wallTexture = ResourceLoader.loadImage("sprites/wall.png");
                    if (wallTexture == null) {
                        System.err.println("Failed to load wall texture");
                    }
                } catch (Exception e) {
                    System.err.println("Error loading wall texture: " + e.getMessage());
                    wallTexture = null;
                }
            }
        }
        
        // Set the block image to use the wall texture
        this.blockImage = wallTexture;
        this.color = FALLBACK_COLOR;
    }
    
    /**
     * Creates a block with a specific level (keeps the same signature for compatibility).
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @param level Level number (unused, kept for compatibility)
     */
    public Block(float x, float y, int width, int height, int level) {
        this(x, y, width, height);
    }
    
    @Override
    public void update() {
        // Blocks are static, so no update logic is needed
    }
    
    @Override
    public void render(Graphics2D g) {
        if (g == null) {
            return;
        }
        
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