package com.niravramdhanie.twod.game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import com.niravramdhanie.twod.game.actions.Action;

/**
 * A button that can only be activated by boxes being placed on top of it.
 * The button is active when a box is on top of it and inactive otherwise.
 */
public class WeightedButton extends Button {
    private static final Color WEIGHTED_BUTTON_COLOR = new Color(150, 75, 0); // Brown color
    private static final Color WEIGHTED_BUTTON_ACTIVE_COLOR = new Color(200, 100, 0); // Lighter brown when active
    
    public WeightedButton(float x, float y, int width, int height, Action action) {
        super(x, y, width, height, action);
        // Override the default button colors with weighted button colors
        setColor(WEIGHTED_BUTTON_COLOR);
        setActiveColor(WEIGHTED_BUTTON_ACTIVE_COLOR);
    }
    
    /**
     * Override activate to prevent player interaction.
     * Weighted buttons can only be activated by boxes.
     */
    @Override
    public boolean activate() {
        // Do nothing - weighted buttons can't be activated by player
        return false;
    }
    
    /**
     * Updates the button's state based on whether a box is on top of it
     * @param boxes List of boxes to check for collision
     */
    public void update(List<Box> boxes) {
        boolean wasActivated = isActivated();
        boolean isBoxOnTop = false;
        
        // Check if any box is on top of the button
        for (Box box : boxes) {
            if (isBoxOnButton(box)) {
                isBoxOnTop = true;
                break;
            }
        }
        
        // Update activation state
        if (isBoxOnTop && !wasActivated) {
            // Use the parent class's activate method directly to bypass our override
            super.activate();
        } else if (!isBoxOnTop && wasActivated) {
            deactivate();
        }
    }
    
    /**
     * Checks if a box is on top of the button
     * @param box The box to check
     * @return true if the box is on top of the button
     */
    private boolean isBoxOnButton(Box box) {
        // Calculate the center points
        float buttonCenterX = getX() + getWidth() / 2;
        float buttonCenterY = getY() + getHeight() / 2;
        float boxCenterX = box.getX() + box.getWidth() / 2;
        float boxCenterY = box.getY() + box.getHeight() / 2;
        
        // Calculate the distance between centers
        float dx = Math.abs(buttonCenterX - boxCenterX);
        float dy = Math.abs(buttonCenterY - boxCenterY);
        
        // Box is considered "on" the button if its center is within the button's bounds
        return dx < getWidth() / 2 && dy < getHeight() / 2;
    }
    
    @Override
    public void render(Graphics2D g) {
        super.render(g);
        
        // Draw a weight icon to indicate this is a weighted button
        g.setColor(Color.WHITE);
        g.drawString("W", (int)getX() + getWidth()/2 - 4, (int)getY() + getHeight()/2 + 4);
    }
} 