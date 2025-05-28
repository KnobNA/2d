package com.niravramdhanie.twod.game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.niravramdhanie.twod.game.utils.ResourceLoader;

public class Block extends Entity {
    private BufferedImage blockImage;
    private Color color;
    private int textureVariant = 0; // Default to 0 (standard texture)
    private int level = 0; // Default to level 0 (no specific level)
    private static final Random random = new Random();
    
    // Cache for block textures
    private static final Map<String, BufferedImage> textureCache = new HashMap<>();
    
    public Block(float x, float y, int width, int height) {
        super(x, y, width, height);
        
        try {
            // Load default block image
            blockImage = ResourceLoader.loadImage("/sprites/block.png");
            
            // Set a default color (used if image fails to load)
            color = new Color(50, 100, 150);
        } catch (Exception e) {
            System.err.println("Error loading block image: " + e.getMessage());
            blockImage = null;
            color = new Color(50, 100, 150);
        }
    }
    
    /**
     * Creates a block with a specific texture variant for a level
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @param level Level number (1, 2, 3, etc.)
     */
    public Block(float x, float y, int width, int height, int level) {
        this(x, y, width, height);
        this.level = level;
        
        if (level == 1) {
            // For level 1, use stone brick textures with random variants
            textureVariant = random.nextInt(5); // 0-4 for 5 different textures
            loadLevelSpecificTexture();
        }
    }
    
    /**
     * Loads a level-specific texture based on the level and texture variant
     */
    private void loadLevelSpecificTexture() {
        try {
            String texturePath = null;
            
            // Select texture based on level and variant
            if (level == 1) {
                switch (textureVariant) {
                    case 0:
                        texturePath = "/sprites/blocks/stone_brick1.png";
                        break;
                    case 1:
                        texturePath = "/sprites/blocks/stone_brick2.png";
                        break;
                    case 2:
                        texturePath = "/sprites/blocks/stone_brick3.png";
                        break;
                    case 3:
                        texturePath = "/sprites/blocks/rough_stone.png";
                        break;
                    case 4:
                        texturePath = "/sprites/blocks/mossy_stone.png";
                        break;
                    default:
                        texturePath = "/sprites/blocks/stone_brick1.png";
                }
                
                // Use texture cache to avoid reloading same textures
                if (textureCache.containsKey(texturePath)) {
                    blockImage = textureCache.get(texturePath);
                } else {
                    BufferedImage texture = ResourceLoader.loadImage(texturePath);
                    if (texture != null) {
                        blockImage = texture;
                        textureCache.put(texturePath, texture);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading level-specific texture: " + e.getMessage());
            // Fallback to default block image which was already loaded in the constructor
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