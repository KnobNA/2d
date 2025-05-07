package com.niravramdhanie.twod.game.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.niravramdhanie.twod.game.core.GameStateManager;
import com.niravramdhanie.twod.game.ui.Button;

public class PauseState extends GameState {
    private List<Button> buttons;
    private Font titleFont;
    private Font buttonFont;
    private int screenWidth;
    private int screenHeight;
    
    public PauseState(GameStateManager gsm) {
        super(gsm);
        this.screenWidth = gsm.getWidth();
        this.screenHeight = gsm.getHeight();
        titleFont = new Font("Arial", Font.BOLD, 48);
        buttonFont = new Font("Arial", Font.PLAIN, 24);
        buttons = new ArrayList<>();
    }
    
    @Override
    public void init() {
        // Create buttons
        int buttonWidth = 200;
        int buttonHeight = 50;
        int startX = screenWidth / 2 - buttonWidth / 2;
        int startY = screenHeight / 2 - 50;
        int padding = 20;
        
        buttons.add(new Button(startX, startY, buttonWidth, buttonHeight, "Resume"));
        buttons.add(new Button(startX, startY + buttonHeight + padding, buttonWidth, buttonHeight, "Options"));
        buttons.add(new Button(startX, startY + (buttonHeight + padding) * 2, buttonWidth, buttonHeight, "Main Menu"));
    }
    
    @Override
    public void update() {
        // Update button states
        for (Button button : buttons) {
            button.update();
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        // Title
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        String title = "PAUSED";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, screenWidth / 2 - titleWidth / 2, 150);
        
        // Buttons
        g.setFont(buttonFont);
        for (Button button : buttons) {
            button.render(g);
        }
    }
    
    @Override
    public void keyPressed(int k) {
        // Handle key press
    }
    
    @Override
    public void keyReleased(int k) {
        // Handle key release
    }
    
    @Override
    public void mousePressed(int x, int y) {
        // Handle button presses
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).contains(x, y)) {
                buttons.get(i).setPressed(true);
            }
        }
    }
    
    @Override
    public void mouseReleased(int x, int y) {
        // Handle button releases
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).contains(x, y) && buttons.get(i).isPressed()) {
                // Handle button action
                switch (i) {
                    case 0: // Resume button
                        gsm.setState(GameStateManager.PLAY_STATE);
                        break;
                    case 1: // Options button
                        // Transition to options state
                        break;
                    case 2: // Main Menu button
                        gsm.setState(GameStateManager.MENU_STATE);
                        break;
                }
            }
            buttons.get(i).setPressed(false);
        }
    }
    
    @Override
    public void mouseMoved(int x, int y) {
        // Update button hover states
        for (Button button : buttons) {
            button.setHovered(button.contains(x, y));
        }
    }
}