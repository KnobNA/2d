package com.niravramdhanie.twod.game.entity;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import com.niravramdhanie.twod.game.utils.ResourceLoader;

/**
 * A box entity that can be picked up and carried by the player.
 * The box can be in an active or inactive state, where the active state
 * means it will follow the rewind system.
 */
public class Box extends Block {
    // Movement tracking
    private boolean isBeingCarried;
    private float relativeX; // Relative position to player when picked up
    private float relativeY;
    
    // Box properties
    private boolean isActive; // Whether the box follows the rewind system
    private boolean isMovable; // Whether the box can be picked up
    private boolean fullRewindTracking; // Whether to fully track all box movements during rewind
    private boolean isRewinding; // Whether the box is currently being rewound
    
    // Visual properties
    private Color boxColor;
    private Color activeBoxColor;
    private BufferedImage boxImage;
    private BufferedImage activeBoxImage;
    
    // Reference to the carrier (player that is carrying this box)
    private Entity carrier;
    
    /**
     * Creates a new box entity.
     * 
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param isActive Whether the box follows the rewind system
     * @param isMovable Whether the box can be picked up
     */
    public Box(float x, float y, int width, int height, boolean isActive, boolean isMovable) {
        super(x, y, width, height);
        this.isBeingCarried = false;
        this.isActive = isActive;
        this.isMovable = isMovable;
        this.fullRewindTracking = false; // Default to simple rewind
        this.isRewinding = false;
        this.carrier = null;
        
        // Default colors (used as fallback if image loading fails)
        this.boxColor = new Color(139, 69, 19); // Brown
        this.activeBoxColor = new Color(205, 133, 63); // Peru (lighter brown)
        
        try {
            // Load box image from sprites directory
            boxImage = ResourceLoader.loadImage("/sprites/box.png");
            // Use the same image for both active and inactive states
            activeBoxImage = boxImage;
        } catch (Exception e) {
            System.err.println("Error loading box image: " + e.getMessage());
            boxImage = null;
            activeBoxImage = null;
        }
    }
    
    @Override
    public void update() {
        // Box behavior is mainly handled in PlayState
    }
    
    /**
     * Override collision detection to disable collision when being carried or when colliding with the carrier
     */
    @Override
    public boolean checkCollision(Entity other) {
        // If being carried, don't register any collisions at all with the carrier
        if (isBeingCarried && (carrier == other || other == carrier)) {
            return false;
        }
        // Otherwise, use the standard collision detection
        return super.checkCollision(other);
    }
    
    @Override
    public void render(Graphics2D g) {
        // Use images if available, otherwise draw with colors
        if ((isActive && activeBoxImage != null) || (!isActive && boxImage != null)) {
            g.drawImage(isActive ? activeBoxImage : boxImage, 
                       (int)position.x, (int)position.y, width, height, null);
        } else {
            // Fallback to drawing with colors
            drawBox(g);
        }
    }
    
    /**
     * Draws the box with gradients and highlights for a 3D effect
     */
    private void drawBox(Graphics2D g) {
        // Save original paint
        java.awt.Paint originalPaint = g.getPaint();
        
        // Choose appropriate color based on state
        Color baseColor = isActive ? activeBoxColor : boxColor;
        Color topColor = baseColor.brighter();
        Color bottomColor = baseColor.darker();
        
        // Create gradient for 3D effect
        GradientPaint gradient = new GradientPaint(
            (int)position.x, (int)position.y, topColor,
            (int)position.x, (int)position.y + height, bottomColor
        );
        
        // Apply gradient and draw box
        g.setPaint(gradient);
        g.fillRect((int)position.x, (int)position.y, width, height);
        
        // Draw box edges (darker)
        g.setColor(bottomColor.darker());
        g.drawRect((int)position.x, (int)position.y, width, height);
        
        // Draw highlights
        int highlight = 4;
        int shadow = 4;
        
        // Top highlight
        g.setColor(new Color(255, 255, 255, 100));
        g.fillRect((int)position.x + 1, (int)position.y + 1, width - 2, highlight);
        
        // Left highlight
        g.fillRect((int)position.x + 1, (int)position.y + highlight + 1, 
                   highlight, height - highlight - shadow - 1);
        
        // Bottom shadow
        g.setColor(new Color(0, 0, 0, 80));
        g.fillRect((int)position.x + 1, (int)position.y + height - shadow, 
                   width - 2, shadow - 1);
        
        // Right shadow
        g.fillRect((int)position.x + width - shadow, (int)position.y + highlight + 1, 
                   shadow - 1, height - highlight - shadow - 1);
        
        // Draw a small indicator if the box is active
        if (isActive) {
            int indicatorSize = 6;
            g.setColor(new Color(0, 255, 0, 180));
            g.fillOval((int)position.x + width - indicatorSize - 2, 
                       (int)position.y + 2, indicatorSize, indicatorSize);
        }
        
        // Restore original paint
        g.setPaint(originalPaint);
    }
    
    /**
     * Picks up the box, storing its relative position to the player.
     * 
     * @param playerX The player's X position
     * @param playerY The player's Y position
     * @param carrier The entity carrying this box
     * @return True if the box was picked up, false if it can't be picked up
     */
    public boolean pickUp(float playerX, float playerY, Entity carrier) {
        // If being rewound and player tries to pick up, cancel rewind
        if (isRewinding && carrier != null) {
            isRewinding = false;
            System.out.println("Box rewind cancelled due to player pickup");
        }
        
        if (!isMovable || isBeingCarried) {
            return false;
        }
        
        // Store relative position to the player (center to center)
        float playerCenterX = playerX + carrier.getWidth() / 2;
        float playerCenterY = playerY + carrier.getHeight() / 2;
        float boxCenterX = position.x + width / 2;
        float boxCenterY = position.y + height / 2;
        
        // Calculate offset from player center to box center
        // This preserves the exact relative position at pickup moment
        relativeX = boxCenterX - playerCenterX;
        relativeY = boxCenterY - playerCenterY;
        
        // Log the pickup for debugging
        System.out.println("Box picked up with relative position: " + relativeX + ", " + relativeY);
        System.out.println("Player center: " + playerCenterX + ", " + playerCenterY);
        System.out.println("Box center: " + boxCenterX + ", " + boxCenterY);
        
        isBeingCarried = true;
        this.carrier = carrier;
        
        return true;
    }
    
    /**
     * Picks up the box without a specific carrier reference.
     * This is used for recorded pickup actions during rewind.
     * 
     * @param playerX The player's X position
     * @param playerY The player's Y position
     * @return True if the box was picked up, false if it can't be picked up
     */
    public boolean pickUp(float playerX, float playerY) {
        // Allow pickup without carrier during rewind (for recorded actions)
        if (isRewinding) {
            return pickUp(playerX, playerY, null);
        }
        return false;
    }
    
    /**
     * Updates the box position when being carried by the player.
     * 
     * @param playerX The player's X position
     * @param playerY The player's Y position
     */
    public void updateCarriedPosition(float playerX, float playerY) {
        if (!isBeingCarried) return;
        
        if (carrier != null) {
            // Calculate position using the current player position and the relative offset
            float playerCenterX = playerX + carrier.getWidth() / 2;
            float playerCenterY = playerY + carrier.getHeight() / 2;
            
            // Get player velocity if available (for smoother movement)
            float playerVelX = 0;
            float playerVelY = 0;
            if (carrier instanceof BallPlayer) {
                playerVelX = ((BallPlayer)carrier).getVelocity().x;
                playerVelY = ((BallPlayer)carrier).getVelocity().y;
            }
            
            // Position the box with its center at the correct offset from player center
            // Maintain the original relative position but adjust for any movement direction
            float boxCenterX = playerCenterX + relativeX;
            float boxCenterY = playerCenterY + relativeY;
            
            // Convert back to top-left corner position
            position.x = boxCenterX - width / 2;
            position.y = boxCenterY - height / 2;
        } else {
            // Fallback if carrier is null
            position.x = playerX + relativeX;
            position.y = playerY + relativeY;
        }
    }
    
    /**
     * Handles the end of recording while the box is being held.
     * This ensures the box is properly dropped when recording ends.
     */
    public void handleRecordingEnd() {
        if (isBeingCarried) {
            System.out.println("Recording ended while box was being held - dropping box");
            drop();
        }
    }
    
    /**
     * Drops the box at its current position.
     */
    public void drop() {
        if (!isBeingCarried) return;
        
        isBeingCarried = false;
        carrier = null;
        
        // Ensure the box is positioned on the grid when dropped
        // This helps prevent the box from being placed at weird positions
        if (carrier instanceof BallPlayer) {
            // Snap to grid if needed - uncomment if you want this behavior
            /*
            int gridCellSize = 32; // Should match GRID_CELL_SIZE in PlayState
            float gridX = Math.round(position.x / gridCellSize) * gridCellSize;
            float gridY = Math.round(position.y / gridCellSize) * gridCellSize;
            position.x = gridX;
            position.y = gridY;
            */
        }
        
        System.out.println("Box dropped at position: " + position.x + ", " + position.y);
    }
    
    /**
     * Checks if the box is being carried.
     * 
     * @return True if being carried, false otherwise
     */
    public boolean isBeingCarried() {
        return isBeingCarried;
    }
    
    /**
     * Gets the entity that is carrying this box.
     * 
     * @return The entity carrying this box, or null if not being carried
     */
    public Entity getCarrier() {
        return carrier;
    }
    
    /**
     * Checks if the box is in an active state (follows rewind).
     * 
     * @return True if active, false otherwise
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Sets whether the box is in an active state.
     * 
     * @param isActive Whether the box should follow the rewind system
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    /**
     * Checks if the box is movable.
     * 
     * @return True if the box can be picked up, false otherwise
     */
    public boolean isMovable() {
        return isMovable;
    }
    
    /**
     * Sets whether the box is movable.
     * 
     * @param isMovable Whether the box can be picked up
     */
    public void setMovable(boolean isMovable) {
        this.isMovable = isMovable;
    }
    
    /**
     * Overrides position setter to handle the carried state
     */
    @Override
    public void setX(float x) {
        if (!isBeingCarried || isRewinding) {
            super.setX(x);
        }
    }
    
    /**
     * Overrides position setter to handle the carried state
     */
    @Override
    public void setY(float y) {
        if (!isBeingCarried || isRewinding) {
            super.setY(y);
        }
    }
    
    /**
     * Gets the relative X position to the carrier when picked up.
     * 
     * @return The relative X position
     */
    public float getRelativeX() {
        return relativeX;
    }
    
    /**
     * Gets the relative Y position to the carrier when picked up.
     * 
     * @return The relative Y position
     */
    public float getRelativeY() {
        return relativeY;
    }
    
    /**
     * Sets the relative X position to the carrier.
     * 
     * @param relativeX The relative X position
     */
    public void setRelativeX(float relativeX) {
        this.relativeX = relativeX;
    }
    
    /**
     * Sets the relative Y position to the carrier.
     * 
     * @param relativeY The relative Y position
     */
    public void setRelativeY(float relativeY) {
        this.relativeY = relativeY;
    }
    
    /**
     * Gets whether the box tracks its full path during rewind or just handles carrying.
     * 
     * @return True if full tracking is enabled, false for simple carrying-only rewind
     */
    public boolean hasFullRewindTracking() {
        return fullRewindTracking;
    }
    
    /**
     * Sets whether the box should track its full path during rewind or just handle carrying.
     * <p>
     * When true: The box's full movement history is tracked and replayed during rewind,
     * and collisions are detected during rewind to prevent the box from passing through walls.
     * <p>
     * When false: The box is only affected by rewind when being carried, in which case
     * it simply teleports with the player back to the beginning position.
     * 
     * @param fullRewindTracking True for full movement tracking, false for carrying-only
     */
    public void setFullRewindTracking(boolean fullRewindTracking) {
        this.fullRewindTracking = fullRewindTracking;
    }
    
    /**
     * Checks for collision with other entities or blocks.
     * Use for checking collision during rewind.
     * 
     * @param blocks List of blocks to check collision against
     * @return True if a collision is detected, false otherwise
     */
    public boolean checkCollisionWithBlocks(List<Block> blocks) {
        for (Block block : blocks) {
            // Skip self
            if (block == this) continue;
            
            // Check collision with block
            if (checkCollision(block)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sets whether the box is currently being rewound.
     * 
     * @param isRewinding Whether the box is being rewound
     */
    public void setRewinding(boolean isRewinding) {
        this.isRewinding = isRewinding;
    }
    
    /**
     * Checks if the box is currently being rewound.
     * 
     * @return True if the box is being rewound, false otherwise
     */
    public boolean isRewinding() {
        return isRewinding;
    }
} 