package com.niravramdhanie.twod.game.ui;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Base class for all UI components
 */
public abstract class UIComponent {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean visible;
    
    public UIComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visible = true;
    }
    
    /**
     * Update the UI component's state
     * @return true if the component state changed, false otherwise
     */
    public abstract boolean update();
    public abstract void render(Graphics2D g);
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public boolean contains(int x, int y) {
        return getBounds().contains(x, y);
    }
    
    // Getters and setters
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
}