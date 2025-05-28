package com.niravramdhanie.twod.game.entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.niravramdhanie.twod.game.actions.DoorAction;
import com.niravramdhanie.twod.game.utils.ResourceLoader;

/**
 * A door entity that can be opened or closed.
 * When closed, it blocks player movement.
 * When open, it allows the player to pass through.
 * Extends Block to work with collision detection.
 */
public class Door extends Block implements DoorAction.DoorStateChangeListener {
    private String id;
    private boolean isOpen;
    private boolean isPermanentlyOpen;
    private BufferedImage doorImage;
    private BufferedImage doorOpenImage;
    private Color doorColor = new Color(139, 69, 19); // Brown door color
    private Color frameColor = new Color(101, 67, 33); // Darker frame color
    private Color openIndicatorColor = new Color(0, 255, 0, 128); // Semi-transparent green
    private Color permanentIndicatorColor = new Color(255, 215, 0, 160); // Semi-transparent gold
    private long openTime; // Time when the door was opened
    
    // Multi-cell door support
    private int gridWidth = 1;  // Width in grid cells (default: 1)
    private int gridHeight = 1; // Height in grid cells (default: 1)
    private int cellSize;       // Size of a single grid cell
    private boolean obeyGridSystem = true; // Whether the door obeys the grid system
    
    /**
     * Creates a new door entity.
     * 
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param id The unique ID of the door
     */
    public Door(float x, float y, int width, int height, String id) {
        super(x, y, width, height);
        this.id = id;
        this.isOpen = false;
        this.isPermanentlyOpen = false;
        this.openTime = 0;
        this.cellSize = width; // Assuming square cells
        
        try {
            // Load door images - using the same image for both states for now
            String[] possiblePaths = {
                "sprites/door.png",
                "/sprites/door.png",
                "resources/sprites/door.png",
                "/resources/sprites/door.png"
            };
            
            for (String path : possiblePaths) {
                System.out.println("Trying to load door texture from: " + path);
                doorImage = ResourceLoader.loadImage(path);
                if (doorImage != null) {
                    System.out.println("Successfully loaded door texture from: " + path);
                    break;
                }
            }
            
            // Use the same image for both states for now
            doorOpenImage = doorImage;
            
            if (doorImage == null) {
                System.err.println("Failed to load door texture from all possible paths!");
            }
        } catch (Exception e) {
            System.err.println("Error loading door images: " + e.getMessage());
            e.printStackTrace();
            doorImage = null;
            doorOpenImage = null;
        }
    }
    
    /**
     * Creates a new multi-cell door entity.
     * 
     * @param x The x position
     * @param y The y position
     * @param cellSize The size of a single grid cell
     * @param gridWidth The width in grid cells
     * @param gridHeight The height in grid cells
     * @param id The unique ID of the door
     * @param obeyGridSystem Whether the door obeys the grid system for collision
     */
    public Door(float x, float y, int cellSize, int gridWidth, int gridHeight, String id, boolean obeyGridSystem) {
        super(x, y, cellSize * gridWidth, cellSize * gridHeight);
        this.id = id;
        this.isOpen = false;
        this.isPermanentlyOpen = false;
        this.openTime = 0;
        this.cellSize = cellSize;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.obeyGridSystem = obeyGridSystem;
        
        try {
            // Load door images - using the same image for both states for now
            String[] possiblePaths = {
                "sprites/door.png",
                "/sprites/door.png",
                "resources/sprites/door.png",
                "/resources/sprites/door.png"
            };
            
            for (String path : possiblePaths) {
                System.out.println("Trying to load door texture from: " + path);
                doorImage = ResourceLoader.loadImage(path);
                if (doorImage != null) {
                    System.out.println("Successfully loaded door texture from: " + path);
                    break;
                }
            }
            
            // Use the same image for both states for now
            doorOpenImage = doorImage;
            
            if (doorImage == null) {
                System.err.println("Failed to load door texture from all possible paths!");
            }
        } catch (Exception e) {
            System.err.println("Error loading door images: " + e.getMessage());
            e.printStackTrace();
            doorImage = null;
            doorOpenImage = null;
        }
    }
    
    @Override
    public void update() {
        // Update door state
        if (isOpen && openTime == 0) {
            openTime = System.currentTimeMillis();
        } else if (!isOpen) {
            openTime = 0;
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        if (doorImage != null && doorOpenImage != null) {
            // Save the original transform
            java.awt.geom.AffineTransform originalTransform = g.getTransform();
            
            // Render using images for both single and multi-cell doors
            BufferedImage currentImage = isOpen ? doorOpenImage : doorImage;
            
            if (gridWidth == 1 && gridHeight == 1) {
                // Single cell door - draw with 180 degree rotation
                g.translate(position.x + width, position.y + height); // Move to bottom-right corner
                g.rotate(Math.PI); // Rotate 180 degrees
                g.drawImage(currentImage, 0, 0, width, height, null);
            } else {
                // Multi-cell door - tile the texture with 180 degree rotation
                int tileSize = Math.min(width / gridWidth, height / gridHeight);
                int tilesX = (width + tileSize - 1) / tileSize;
                int tilesY = (height + tileSize - 1) / tileSize;
                
                // Move to bottom-right corner of the door area
                g.translate(position.x + width, position.y + height);
                g.rotate(Math.PI); // Rotate 180 degrees
                
                for (int y = 0; y < tilesY; y++) {
                    for (int x = 0; x < tilesX; x++) {
                        g.drawImage(currentImage, 
                                 x * tileSize, 
                                 y * tileSize, 
                                 tileSize, tileSize, null);
                    }
                }
            }
            
            // Restore the original transform
            g.setTransform(originalTransform);
            
            // Draw open indicators if the door is open
            if (isOpen) {
                if (isPermanentlyOpen) {
                    drawPermanentOpenIndicator(g);
                } else {
                    drawOpenIndicator(g);
                }
            }
        } else {
            // Fallback rendering with rectangles if textures failed to load
            if (isOpen) {
                // Draw frame but no door
                drawDoorFrame(g);
                // Draw open indicator
                if (isPermanentlyOpen) {
                    drawPermanentOpenIndicator(g);
                } else {
                    drawOpenIndicator(g);
                }
            } else {
                // Draw both frame and door
                drawDoorFrame(g);
                drawDoorBody(g);
            }
        }
    }
    
    /**
     * Draws an indicator that the door is permanently open.
     * 
     * @param g The graphics context
     */
    private void drawPermanentOpenIndicator(Graphics2D g) {
        // Store original color
        Color originalColor = g.getColor();
        
        // Create a pulsating effect with gold color
        float alpha = 0.7f;
        if (openTime > 0) {
            long elapsed = System.currentTimeMillis() - openTime;
            alpha = 0.4f + (float)Math.abs(Math.sin(elapsed * 0.002f)) * 0.3f;
        }
        
        // Draw a glowing golden outline around the door frame
        g.setColor(new Color(
            permanentIndicatorColor.getRed(), 
            permanentIndicatorColor.getGreen(), 
            permanentIndicatorColor.getBlue(), 
            (int)(alpha * 255)
        ));
        
        // Draw glow over the door frame
        int glowSize = 6;  // Larger glow for permanent
        g.fillRoundRect(
            (int)position.x - glowSize, 
            (int)position.y - glowSize, 
            width + glowSize * 2, 
            height + glowSize * 2,
            10, 10
        );
        
        // Add "PERMANENTLY OPEN" text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        String text = "PERMANENTLY OPEN";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        
        // Draw background for text to improve readability
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(
            (int)position.x + (width - textWidth) / 2 - 2, 
            (int)position.y + height + 2, 
            textWidth + 4, 
            textHeight
        );
        
        // Draw text
        g.setColor(Color.YELLOW);
        g.drawString(text, 
            (int)position.x + (width - textWidth) / 2, 
            (int)position.y + height + textHeight);
        
        // Restore original color
        g.setColor(originalColor);
    }
    
    /**
     * Draws an indicator that the door is open.
     * 
     * @param g The graphics context
     */
    private void drawOpenIndicator(Graphics2D g) {
        // Store original color
        Color originalColor = g.getColor();
        
        // Create a pulsating effect
        float alpha = 0.6f;
        if (openTime > 0) {
            long elapsed = System.currentTimeMillis() - openTime;
            alpha = 0.3f + (float)Math.abs(Math.sin(elapsed * 0.003f)) * 0.3f;
        }
        
        // Draw a glowing outline around the door frame
        g.setColor(new Color(
            openIndicatorColor.getRed(), 
            openIndicatorColor.getGreen(), 
            openIndicatorColor.getBlue(), 
            (int)(alpha * 255)
        ));
        
        // Draw glow over the door frame
        int glowSize = 4;
        g.fillRoundRect(
            (int)position.x - glowSize, 
            (int)position.y - glowSize, 
            width + glowSize * 2, 
            height + glowSize * 2,
            8, 8
        );
        
        // Add "OPEN" text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        String text = "OPEN";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g.drawString(text, 
            (int)position.x + (width - textWidth) / 2, 
            (int)position.y + height + textHeight);
        
        // Restore original color
        g.setColor(originalColor);
    }
    
    /**
     * Draws the door frame.
     * 
     * @param g The graphics context
     */
    private void drawDoorFrame(Graphics2D g) {
        // Draw the door frame
        g.setColor(frameColor);
        
        int frameThickness = Math.max(width, height) / 16; // Thinner frame for larger doors
        
        // Left side
        g.fillRect((int)position.x, (int)position.y, frameThickness, height);
        
        // Right side
        g.fillRect((int)position.x + width - frameThickness, (int)position.y, frameThickness, height);
        
        // Top
        g.fillRect((int)position.x, (int)position.y, width, frameThickness);
        
        // Bottom
        g.fillRect((int)position.x, (int)position.y + height - frameThickness, width, frameThickness);
    }
    
    /**
     * Draws the door body.
     * 
     * @param g The graphics context
     */
    private void drawDoorBody(Graphics2D g) {
        // Save original color and paint
        Color originalColor = g.getColor();
        java.awt.Paint originalPaint = g.getPaint();
        
        Color doorColor = new Color(139, 69, 19); // Brown door color
        
        // Door body fill
        int frameThickness = Math.max(width, height) / 16;
        int doorBodyX = (int)position.x + frameThickness;
        int doorBodyY = (int)position.y + frameThickness;
        int doorBodyWidth = width - (frameThickness * 2);
        int doorBodyHeight = height - (frameThickness * 2);
        
        // Create a gradient for 3D effect
        GradientPaint gradient = new GradientPaint(
            doorBodyX, doorBodyY, doorColor.brighter(),
            doorBodyX + doorBodyWidth, doorBodyY, doorColor.darker()
        );
        
        // Apply gradient and draw door
        g.setPaint(gradient);
        g.fillRect(doorBodyX, doorBodyY, doorBodyWidth, doorBodyHeight);
        
        // Add door details based on size
        if (gridWidth > 1 || gridHeight > 1) {
            // Add grid lines to make it look like multiple door panels
            g.setColor(new Color(101, 67, 33));
            
            // Vertical grid lines
            for (int i = 1; i < gridWidth; i++) {
                int x = doorBodyX + (i * (doorBodyWidth / gridWidth));
                g.fillRect(x - frameThickness/2, doorBodyY, frameThickness, doorBodyHeight);
            }
            
            // Horizontal grid lines
            for (int i = 1; i < gridHeight; i++) {
                int y = doorBodyY + (i * (doorBodyHeight / gridHeight));
                g.fillRect(doorBodyX, y - frameThickness/2, doorBodyWidth, frameThickness);
            }
        }
        
        // Add door handles
        g.setColor(Color.BLACK);
        
        // For horizontal doors (width > height), add handles in the middle of each cell
        if (gridWidth > gridHeight) {
            int handleY = doorBodyY + doorBodyHeight / 2;
            int handleSize = cellSize / 10;
            
            for (int i = 0; i < gridWidth; i++) {
                int handleX = doorBodyX + (i * (doorBodyWidth / gridWidth)) + (doorBodyWidth / gridWidth) - (doorBodyWidth / gridWidth) / 3;
                g.fillOval(handleX, handleY, handleSize, handleSize);
            }
        } 
        // For vertical doors (height > width), add handles in the middle of each cell
        else if (gridHeight > gridWidth) {
            int handleX = doorBodyX + doorBodyWidth - (doorBodyWidth / 3);
            int handleSize = cellSize / 10;
            
            for (int i = 0; i < gridHeight; i++) {
                int handleY = doorBodyY + (i * (doorBodyHeight / gridHeight)) + (doorBodyHeight / gridHeight) / 2;
                g.fillOval(handleX, handleY, handleSize, handleSize);
            }
        }
        // For square doors, add a handle in the middle right
        else {
            int handleSize = Math.max(doorBodyWidth, doorBodyHeight) / 10;
            g.fillOval(
                doorBodyX + doorBodyWidth - (doorBodyWidth / 3),
                doorBodyY + doorBodyHeight / 2, 
                handleSize,
                handleSize
            );
        }
        
        // Restore original paint and color
        g.setPaint(originalPaint);
        g.setColor(originalColor);
    }
    
    /**
     * Opens the door.
     */
    public void open() {
        isOpen = true;
    }
    
    /**
     * Opens the door permanently, so it can't be closed again.
     */
    public void openPermanently() {
        isOpen = true;
        isPermanentlyOpen = true;
        System.out.println("Door " + id + " has been permanently opened!");
    }
    
    /**
     * Checks if the door is permanently open.
     * 
     * @return True if permanently open, false otherwise
     */
    public boolean isPermanentlyOpen() {
        return isPermanentlyOpen;
    }
    
    /**
     * Closes the door if it's not permanently open.
     */
    public void close() {
        if (!isPermanentlyOpen) {
            isOpen = false;
        }
    }
    
    /**
     * Checks if the door is open.
     * 
     * @return True if open, false if closed
     */
    public boolean isOpen() {
        return isOpen;
    }
    
    /**
     * Gets the door ID.
     * 
     * @return The door ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the door ID.
     * 
     * @param id The door ID
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Toggles the door state unless it's permanently open.
     * 
     * @return The new door state
     */
    public boolean toggle() {
        if (!isPermanentlyOpen) {
            isOpen = !isOpen;
        }
        return isOpen;
    }
    
    @Override
    public void onDoorStateChanged(String doorId, boolean isOpen) {
        if (this.id.equals(doorId)) {
            if (isOpen) {
                open();
            } else if (!isPermanentlyOpen) {
                close();
            }
        }
    }
} 