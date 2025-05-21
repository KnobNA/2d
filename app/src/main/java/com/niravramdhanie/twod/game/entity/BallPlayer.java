package com.niravramdhanie.twod.game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import com.niravramdhanie.twod.game.graphics.Animation;
import com.niravramdhanie.twod.game.graphics.SpriteSheet;
import com.niravramdhanie.twod.game.utils.ResourceLoader;

public class BallPlayer extends Entity {
    // Movement flags
    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    
    // Movement properties
    private float moveSpeed;
    private float maxSpeed;
    
    // Health properties
    private int health;
    private int maxHealth;
    
    // Animations
    private BufferedImage idleSprite; // Cache the idle sprite
    private BufferedImage rightSprite; // Cache the right movement sprite
    private BufferedImage leftSprite;  // Cache the left movement sprite
    private BufferedImage upSprite;    // Cache the up movement sprite
    private BufferedImage downSprite;  // Cache the down movement sprite
    private Animation idleAnim;
    private Animation rightAnim;
    private Animation leftAnim;
    private Animation upAnim;
    private Animation downAnim;
    private Animation currentAnim;
    private boolean spritesLoaded = false; // Flag to track if sprites are loaded
    
    // Game properties
    private int screenWidth;
    private int screenHeight;
    private List<Block> blocks;
    
    // Box carrying
    private Box carriedBox;
    
    public BallPlayer(float x, float y, int width, int height, int screenWidth, int screenHeight) {
        super(x, y, width, height);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.carriedBox = null;
        
        // Set movement properties - instant movement with no friction
        moveSpeed = 0.84f;   // 1.2f * 0.7 = 0.84f
        maxSpeed = 2.8f;     // 4.0f * 0.7 = 2.8f
        
        // Set health properties
        this.maxHealth = 100;
        this.health = this.maxHealth;
        
        // Initialize flags
        left = false;
        right = false;
        up = false;
        down = false;
        
        // Load sprites and animations
        initAnimations();
    }
    
    private void initAnimations() {
        try {
            // Load character sprites
            idleSprite = ResourceLoader.loadImage("/sprites/Character_Idle.png");
            rightSprite = ResourceLoader.loadImage("/sprites/Character_Right.png");
            leftSprite = ResourceLoader.loadImage("/sprites/Character_Left.png");
            upSprite = ResourceLoader.loadImage("/sprites/Character_Up.png");
            downSprite = ResourceLoader.loadImage("/sprites/Character_Down.png");
            
            if (idleSprite != null && rightSprite != null && leftSprite != null && 
                upSprite != null && downSprite != null) {
                System.out.println("Player sprites loaded successfully");
                
                // Create animations
                idleAnim = new Animation();
                idleAnim.addFrame(idleSprite, 150);
                
                rightAnim = new Animation();
                rightAnim.addFrame(rightSprite, 150);
                
                leftAnim = new Animation();
                leftAnim.addFrame(leftSprite, 150);
                
                upAnim = new Animation();
                upAnim.addFrame(upSprite, 150);
                
                downAnim = new Animation();
                downAnim.addFrame(downSprite, 150);
                
                // Set default animation
                currentAnim = idleAnim;
                spritesLoaded = true;
                System.out.println("Player animations initialized successfully");
            } else {
                System.err.println("Failed to load player sprite images");
                createEmptyAnimations();
            }
        } catch (Exception e) {
            System.err.println("Error initializing animations: " + e.getMessage());
            e.printStackTrace();
            // Create empty animations as fallback
            createEmptyAnimations();
        }
    }
    
    private void createEmptyAnimations() {
        spritesLoaded = false;
        // Create basic animations with null frames as a fallback
        idleAnim = new Animation();
        rightAnim = new Animation();
        leftAnim = new Animation();
        upAnim = new Animation();
        downAnim = new Animation();
        currentAnim = idleAnim;
    }
    
    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }
    
    /**
     * Sets the box being carried by the player.
     * 
     * @param box The box being carried, or null if not carrying any box
     */
    public void setCarriedBox(Box box) {
        this.carriedBox = box;
    }
    
    /**
     * Gets the box being carried by the player.
     * 
     * @return The box being carried, or null if not carrying any box
     */
    public Box getCarriedBox() {
        return carriedBox;
    }
    
    /**
     * Checks if the player is carrying a box.
     * 
     * @return True if carrying a box, false otherwise
     */
    public boolean isCarryingBox() {
        return carriedBox != null;
    }
    
    @Override
    public void update() {
        // Track previous animation for transition checks
        Animation previousAnim = currentAnim;
        
        // Reset velocity immediately when no keys are pressed for instant stopping
        if (!left && !right) velocity.x = 0;
        if (!up && !down) velocity.y = 0;
        
        // Calculate input direction vector
        float dirX = 0, dirY = 0;
        if (left) dirX -= 1;
        if (right) dirX += 1;
        if (up) dirY -= 1;
        if (down) dirY += 1;
        
        // Normalize diagonal movement
        if (dirX != 0 && dirY != 0) {
            // Calculate the normalized direction
            float length = (float)Math.sqrt(dirX * dirX + dirY * dirY);
            dirX /= length;
            dirY /= length;
        }
        
        // Apply movement only if keys are pressed (no acceleration buildup)
        if (dirX != 0) {
            velocity.x = dirX * maxSpeed;
        }
        
        if (dirY != 0) {
            velocity.y = dirY * maxSpeed;
        }
        
        // Set appropriate animation based on movement direction
        if (spritesLoaded) {
            if (velocity.x > 0) {
                currentAnim = rightAnim;
            } else if (velocity.x < 0) {
                currentAnim = leftAnim;
            } else if (velocity.y < 0) {
                currentAnim = upAnim;
            } else if (velocity.y > 0) {
                currentAnim = downAnim;
            } else {
                currentAnim = idleAnim;
            }
        }
        
        // If animation changed, reset the animation
        if (previousAnim != currentAnim && currentAnim != null) {
            currentAnim.reset();
        }
        
        // Calculate new position
        float newX = position.x + velocity.x;
        float newY = position.y + velocity.y;
        
        // Check screen boundaries
        if (newX < 0) newX = 0;
        if (newX > screenWidth - width) newX = screenWidth - width;
        if (newY < 0) newY = 0;
        if (newY > screenHeight - height) newY = screenHeight - height;
        
        // Check block collisions for X movement
        boolean collisionX = false;
        position.x = newX;
        
        if (checkBlockCollisionX()) {
            collisionX = true;
            // Position already corrected in checkBlockCollisionX()
            velocity.x = 0;
        }
        
        // Check block collisions for Y movement
        boolean collisionY = false;
        position.y = newY;
        
        if (checkBlockCollisionY()) {
            collisionY = true;
            // Position already corrected in checkBlockCollisionY()
            velocity.y = 0;
        }
        
        // If no collision occurred, move normally
        if (!collisionX) position.x = newX;
        if (!collisionY) position.y = newY;
        
        // Update current animation
        if (spritesLoaded && currentAnim != null) {
            currentAnim.update();
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        try {
            // Check if we have a valid sprite animation
            if (spritesLoaded && currentAnim != null) {
                BufferedImage currentFrame = currentAnim.getCurrentFrame();
                if (currentFrame != null) {
                    g.drawImage(currentFrame, (int)position.x, (int)position.y, width, height, null);
                    return;
                } else {
                    System.err.println("Current animation frame is null: " + 
                        (currentAnim == idleAnim ? "idle" : 
                         currentAnim == rightAnim ? "right" : 
                         currentAnim == leftAnim ? "left" : 
                         currentAnim == upAnim ? "up" : "down"));
                }
            } else {
                System.err.println("Cannot render sprite: spritesLoaded=" + spritesLoaded + 
                                  ", currentAnim=" + (currentAnim == null ? "null" : "not null"));
            }
            
            // Fallback if sprites aren't loaded or animation has no frames
            g.setColor(Color.RED);
            g.fillOval((int)position.x, (int)position.y, width, height);
        } catch (Exception e) {
            System.err.println("Error rendering player: " + e.getMessage());
            e.printStackTrace();
            
            // Emergency fallback
            g.setColor(Color.RED);
            g.fillOval((int)position.x, (int)position.y, width, height);
        }
    }
    
    // Health getters and setters
    public int getHealth() { return health; }
    public void setHealth(int health) { 
        this.health = Math.max(0, Math.min(maxHealth, health)); 
    }
    
    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { 
        this.maxHealth = maxHealth;
        // Adjust current health if it exceeds the new max
        if (health > maxHealth) health = maxHealth;
    }
    
    public void damage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
    }
    
    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }
    
    public boolean isAlive() {
        return health > 0;
    }
    
    // Input handlers
    public void setLeft(boolean left) { this.left = left; }
    public void setRight(boolean right) { this.right = right; }
    public void setUp(boolean up) { this.up = up; }
    public void setDown(boolean down) { this.down = down; }
    
    /**
     * Gets the player's velocity vector.
     * 
     * @return The velocity vector
     */
    public com.niravramdhanie.twod.game.utils.Vector2D getVelocity() {
        return velocity;
    }
    
    /**
     * Checks for X-axis collisions with blocks.
     * When carrying a box, the box acts as an extension of the player's hitbox.
     * 
     * @return True if a collision occurred, false otherwise
     */
    private boolean checkBlockCollisionX() {
        // First check player collision with blocks (but not with the carried box)
        for (Block block : blocks) {
            if (block != carriedBox && checkCollision(block)) {
                // Resolve X collision
                if (velocity.x > 0) { // Moving right
                    position.x = block.getX() - width;
                } else if (velocity.x < 0) { // Moving left
                    position.x = block.getX() + block.getWidth();
                }
                velocity.x = 0;
                return true;
            }
        }
        
        // If carrying a box, check if the box would collide with any blocks
        if (carriedBox != null && velocity.x != 0) {
            // Calculate where the box would be after the player's movement
            float boxX = position.x + width/2 - carriedBox.getWidth()/2 + carriedBox.getRelativeX();
            float boxY = position.y + height/2 - carriedBox.getHeight()/2 + carriedBox.getRelativeY();
            
            // Create a rectangle representing the box's position
            java.awt.Rectangle boxBounds = new java.awt.Rectangle(
                (int)boxX, (int)boxY, carriedBox.getWidth(), carriedBox.getHeight()
            );
            
            // Check for collisions with any block
            for (Block block : blocks) {
                if (block != carriedBox) {
                    java.awt.Rectangle blockBounds = block.getBounds();
                    
                    if (boxBounds.intersects(blockBounds)) {
                        // Resolve collision just like we would for the player
                        if (velocity.x > 0) { // Moving right
                            position.x = block.getX() - width - (boxX + carriedBox.getWidth() - position.x - width);
                        } else if (velocity.x < 0) { // Moving left
                            position.x = block.getX() + block.getWidth() - (boxX - position.x);
                        }
                        velocity.x = 0;
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Checks for Y-axis collisions with blocks.
     * When carrying a box, the box acts as an extension of the player's hitbox.
     * 
     * @return True if a collision occurred, false otherwise
     */
    private boolean checkBlockCollisionY() {
        // First check player collision with blocks (but not with the carried box)
        for (Block block : blocks) {
            if (block != carriedBox && checkCollision(block)) {
                // Resolve Y collision
                if (velocity.y > 0) { // Moving down
                    position.y = block.getY() - height;
                } else if (velocity.y < 0) { // Moving up
                    position.y = block.getY() + block.getHeight();
                }
                velocity.y = 0;
                return true;
            }
        }
        
        // If carrying a box, check if the box would collide with any blocks
        if (carriedBox != null && velocity.y != 0) {
            // Calculate where the box would be after the player's movement
            float boxX = position.x + width/2 - carriedBox.getWidth()/2 + carriedBox.getRelativeX();
            float boxY = position.y + height/2 - carriedBox.getHeight()/2 + carriedBox.getRelativeY();
            
            // Create a rectangle representing the box's position
            java.awt.Rectangle boxBounds = new java.awt.Rectangle(
                (int)boxX, (int)boxY, carriedBox.getWidth(), carriedBox.getHeight()
            );
            
            // Check for collisions with any block
            for (Block block : blocks) {
                if (block != carriedBox) {
                    java.awt.Rectangle blockBounds = block.getBounds();
                    
                    if (boxBounds.intersects(blockBounds)) {
                        // Resolve collision just like we would for the player
                        if (velocity.y > 0) { // Moving down
                            position.y = block.getY() - height - (boxY + carriedBox.getHeight() - position.y - height);
                        } else if (velocity.y < 0) { // Moving up
                            position.y = block.getY() + block.getHeight() - (boxY - position.y);
                        }
                        velocity.y = 0;
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Override collision detection to disable collision with the carried box
     */
    @Override
    public boolean checkCollision(Entity other) {
        // Don't collide with the box we're carrying
        if (carriedBox != null && other == carriedBox) {
            return false;
        }
        // Use standard collision detection for everything else
        return super.checkCollision(other);
    }
}