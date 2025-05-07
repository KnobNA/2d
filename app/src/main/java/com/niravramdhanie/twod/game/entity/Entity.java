package com.niravramdhanie.twod.game.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.niravramdhanie.twod.game.utils.Vector2D;

public abstract class Entity {
    protected Vector2D position;
    protected Vector2D velocity;
    protected int width;
    protected int height;
    protected boolean active;
    
    public Entity(float x, float y, int width, int height) {
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(0, 0);
        this.width = width;
        this.height = height;
        this.active = true;
    }
    
    public abstract void update();
    public abstract void render(Graphics2D g);
    
    public Rectangle getBounds() {
        return new Rectangle((int)position.x, (int)position.y, width, height);
    }
    
    public boolean checkCollision(Entity other) {
        return getBounds().intersects(other.getBounds());
    }
    
    // Getters and setters
    public float getX() { return position.x; }
    public float getY() { return position.y; }
    public void setX(float x) { position.x = x; }
    public void setY(float y) { position.y = y; }
    public float getVelX() { return velocity.x; }
    public float getVelY() { return velocity.y; }
    public void setVelX(float vx) { velocity.x = vx; }
    public void setVelY(float vy) { velocity.y = vy; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}