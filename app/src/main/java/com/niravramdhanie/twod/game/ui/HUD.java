package com.niravramdhanie.twod.game.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.niravramdhanie.twod.game.entity.BallPlayer;

/**
 * Heads-Up Display for game information like health, score, etc.
 */
public class HUD {
    private BallPlayer player;
    private int score;
    private int width;
    private int height;
    private boolean visible;
    private List<UIComponent> components;
    
    public HUD(int width, int height) {
        this.width = width;
        this.height = height;
        this.visible = true;
        this.score = 0;
        this.components = new ArrayList<>();
    }
    
    public void setPlayer(BallPlayer player) {
        this.player = player;
    }
    
    public void update() {
        if (!visible || player == null) return;
        
        // Update all components
        for (UIComponent component : components) {
            component.update();
        }
    }
    
    public void render(Graphics2D g) {
        if (!visible) return;
        
        // Draw health bar
        if (player != null) {
            int healthBarWidth = 200;
            int healthBarHeight = 20;
            int healthBarX = 20;
            int healthBarY = 20;
            
            // Health bar background
            g.setColor(Color.GRAY);
            g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
            
            // Health bar fill
            float healthPercent = player.getHealth() / (float)player.getMaxHealth();
            g.setColor(new Color(255 - (int)(255 * healthPercent), (int)(255 * healthPercent), 0));
            g.fillRect(healthBarX, healthBarY, (int)(healthBarWidth * healthPercent), healthBarHeight);
            
            // Health bar border
            g.setColor(Color.BLACK);
            g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
            
            // Health text
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("Health: " + player.getHealth() + "/" + player.getMaxHealth(), 
                      healthBarX + 5, healthBarY + 15);
        }
        
        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, width - 150, 30);
        
        // Render all additional components
        for (UIComponent component : components) {
            component.render(g);
        }
    }
    
    public void addComponent(UIComponent component) {
        components.add(component);
    }
    
    public void removeComponent(UIComponent component) {
        components.remove(component);
    }
    
    public void clearComponents() {
        components.clear();
    }
    
    // Getters and setters
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public void addScore(int points) { this.score += points; }
    
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
}