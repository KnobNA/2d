package com.niravramdhanie.twod.game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.niravramdhanie.twod.game.utils.ResourceLoader;

/**
 * Button entity that can be pressed by the player to trigger various actions.
 */
public class Button extends Entity {
    // Button states
    private boolean pressed;
    private boolean playerIsOverlapping;
    private long pressTime;
    private static final long PRESS_COOLDOWN = 1000; // 1 second cooldown
    
    // Visual properties
    private BufferedImage buttonUpImage;
    private BufferedImage buttonDownImage;
    private Color upColor;
    private Color downColor;
    
    // Button action type
    public enum ButtonAction {
        OPEN_DOOR,
        SPAWN_ITEMS,
        TOGGLE_PLATFORM,
        TRIGGER_TRAP,
        CUSTOM
    }
    
    private ButtonAction actionType;
    private int actionValue; // Can be used for various purposes depending on action type
    private ButtonActionListener actionListener;
    
    public Button(float x, float y, int width, int height, ButtonAction actionType) {
        super(x, y, width, height);
        this.actionType = actionType;
        this.pressed = false;
        this.playerIsOverlapping = false;
        this.actionValue = 0;
        
        // Set default colors
        this.upColor = new Color(220, 0, 0);    // Red when not pressed
        this.downColor = new Color(100, 0, 0);  // Dark red when pressed
        
        loadImages();
    }
    
    private void loadImages() {
        try {
            // Load button images
            buttonUpImage = ResourceLoader.loadImage("/sprites/button_up.png");
            buttonDownImage = ResourceLoader.loadImage("/sprites/button_down.png");
        } catch (Exception e) {
            System.err.println("Error loading button images: " + e.getMessage());
            buttonUpImage = null;
            buttonDownImage = null;
        }
    }
    
    @Override
    public void update() {
        // Check if button can be pressed again after cooldown
        if (pressed && System.currentTimeMillis() - pressTime > PRESS_COOLDOWN) {
            if (!playerIsOverlapping) {
                pressed = false;
            }
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        if (buttonUpImage != null && buttonDownImage != null) {
            // Draw the appropriate button image based on state
            if (pressed) {
                g.drawImage(buttonDownImage, (int)position.x, (int)position.y, width, height, null);
            } else {
                g.drawImage(buttonUpImage, (int)position.x, (int)position.y, width, height, null);
            }
        } else {
            // Fallback to colored rectangle if images not available
            if (pressed) {
                g.setColor(downColor);
            } else {
                g.setColor(upColor);
            }
            g.fillRect((int)position.x, (int)position.y, width, height);
            
            // Draw button top
            g.setColor(Color.DARK_GRAY);
            int topHeight = height / 4;
            if (pressed) {
                topHeight = height / 8; // Shorter when pressed
            }
            g.fillRect((int)position.x + width/4, (int)position.y - topHeight, width/2, topHeight);
        }
    }
    
    /**
     * Called when a player is on the button
     * @param player The player entity
     */
    public void playerOverlap(BallPlayer player) {
        playerIsOverlapping = true;
        
        // If not pressed and player is on top, press the button
        if (!pressed) {
            pressed = true;
            pressTime = System.currentTimeMillis();
            
            // Trigger the button action
            triggerAction();
        }
    }
    
    /**
     * Called when a player leaves the button
     */
    public void playerLeave() {
        playerIsOverlapping = false;
    }
    
    /**
     * Trigger the button's action
     */
    private void triggerAction() {
        System.out.println("Button pressed! Action: " + actionType);
        
        // Execute action based on type
        switch (actionType) {
            case OPEN_DOOR:
                System.out.println("Opening door with ID: " + actionValue);
                break;
            case SPAWN_ITEMS:
                System.out.println("Spawning " + actionValue + " items");
                break;
            case TOGGLE_PLATFORM:
                System.out.println("Toggling platform with ID: " + actionValue);
                break;
            case TRIGGER_TRAP:
                System.out.println("Triggering trap with ID: " + actionValue);
                break;
            case CUSTOM:
                System.out.println("Triggering custom action with value: " + actionValue);
                break;
        }
        
        // Notify listener if set
        if (actionListener != null) {
            actionListener.onButtonPressed(this);
        }
    }
    
    // Getters and setters
    public boolean isPressed() {
        return pressed;
    }
    
    public void setPressed(boolean pressed) {
        this.pressed = pressed;
        if (pressed) {
            pressTime = System.currentTimeMillis();
        }
    }
    
    public ButtonAction getActionType() {
        return actionType;
    }
    
    public void setActionType(ButtonAction actionType) {
        this.actionType = actionType;
    }
    
    public int getActionValue() {
        return actionValue;
    }
    
    public void setActionValue(int actionValue) {
        this.actionValue = actionValue;
    }
    
    public void setActionListener(ButtonActionListener listener) {
        this.actionListener = listener;
    }
    
    public void setColors(Color upColor, Color downColor) {
        this.upColor = upColor;
        this.downColor = downColor;
    }
    
    /**
     * Interface for button action callbacks
     */
    public interface ButtonActionListener {
        void onButtonPressed(Button button);
    }
} 