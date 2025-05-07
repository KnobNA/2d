package com.niravramdhanie.twod.game.core;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.niravramdhanie.twod.game.input.KeyHandler;
import com.niravramdhanie.twod.game.input.MouseHandler;
import com.niravramdhanie.twod.game.state.GameState;
import com.niravramdhanie.twod.game.state.MenuState;
import com.niravramdhanie.twod.game.state.PauseState;
import com.niravramdhanie.twod.game.state.PlayState;

public class GameStateManager {
    private List<GameState> gameStates;
    private int currentState;
    private boolean initialized = false;
    
    // State identifiers
    public static final int MENU_STATE = 0;
    public static final int PLAY_STATE = 1;
    public static final int PAUSE_STATE = 2;
    
    // Screen dimensions
    private int width;
    private int height;
    
    // Input handlers
    private KeyHandler keyHandler;
    private MouseHandler mouseHandler;
    
    // Track which states need constant updates
    private boolean[] stateNeedsConstantUpdates = new boolean[3]; // 3 states for now
    
    public GameStateManager(int width, int height) {
        System.out.println("GameStateManager initialized with dimensions: " + width + "x" + height);
        this.width = width;
        this.height = height;
        gameStates = new ArrayList<>();
        
        // Add all game states
        gameStates.add(new MenuState(this));
        gameStates.add(new PlayState(this, width, height));
        gameStates.add(new PauseState(this));
        
        // Set which states need constant updates
        stateNeedsConstantUpdates[MENU_STATE] = false;   // Menu is static
        stateNeedsConstantUpdates[PLAY_STATE] = true;    // Gameplay needs continuous updates
        stateNeedsConstantUpdates[PAUSE_STATE] = false;  // Pause menu is static
        
        // Set the starting state
        setState(MENU_STATE);
        initialized = true;
    }
    
    public void setInputHandlers(KeyHandler kh, MouseHandler mh) {
        this.keyHandler = kh;
        this.mouseHandler = mh;
        
        // Forward input events to the states
        kh.setGameStateManager(this);
        mh.setGameStateManager(this);
    }
    
    public void setState(int state) {
        System.out.println("Setting game state to: " + state);
        currentState = state;
        
        try {
            gameStates.get(currentState).init();
            
            // Force an immediate redraw when changing states
            requestRedraw();
            
            // For menu state, we need to manually trigger a repaint since it's not constantly updating
            if (state == MENU_STATE && gameStates.get(currentState) instanceof MenuState) {
                ((MenuState) gameStates.get(currentState)).setNeedsRedraw(true);
            }
        } catch (Exception e) {
            System.err.println("Error initializing state " + state + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void update() {
        if (!initialized) return;
        
        try {
            gameStates.get(currentState).update();
        } catch (Exception e) {
            System.err.println("Error updating state " + currentState + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void render(Graphics2D g) {
        if (!initialized) return;
        
        try {
            gameStates.get(currentState).render(g);
        } catch (Exception e) {
            System.err.println("Error rendering state " + currentState + ": " + e.getMessage());
            e.printStackTrace();
            
            // Display error message on screen
            g.setColor(java.awt.Color.RED);
            g.drawString("Error rendering state: " + e.getMessage(), 10, 30);
        }
    }
    
    // Input handling methods - forward to current state
    
    public void keyPressed(int k) {
        if (!initialized) return;
        
        try {
            gameStates.get(currentState).keyPressed(k);
        } catch (Exception e) {
            System.err.println("Error handling keyPressed in state " + currentState + ": " + e.getMessage());
        }
    }
    
    public void keyReleased(int k) {
        if (!initialized) return;
        
        try {
            gameStates.get(currentState).keyReleased(k);
        } catch (Exception e) {
            System.err.println("Error handling keyReleased in state " + currentState + ": " + e.getMessage());
        }
    }
    
    public void mousePressed(int x, int y) {
        if (!initialized) return;
        
        try {
            gameStates.get(currentState).mousePressed(x, y);
        } catch (Exception e) {
            System.err.println("Error handling mousePressed in state " + currentState + ": " + e.getMessage());
        }
    }
    
    public void mouseReleased(int x, int y) {
        if (!initialized) return;
        
        try {
            gameStates.get(currentState).mouseReleased(x, y);
        } catch (Exception e) {
            System.err.println("Error handling mouseReleased in state " + currentState + ": " + e.getMessage());
        }
    }
    
    public void mouseMoved(int x, int y) {
        if (!initialized) return;
        
        try {
            gameStates.get(currentState).mouseMoved(x, y);
        } catch (Exception e) {
            System.err.println("Error handling mouseMoved in state " + currentState + ": " + e.getMessage());
        }
    }
    
    // Getters for screen dimensions
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getCurrentState() {
        return currentState;
    }
    
    /**
     * Checks if the current state needs constant updates and redraws
     * @return true if the current state needs continuous updates
     */
    public boolean needsConstantUpdates() {
        return stateNeedsConstantUpdates[currentState];
    }
    
    /**
     * Force a redraw of the current state
     */
    public void requestRedraw() {
        try {
            if (gameStates.get(currentState) instanceof MenuState) {
                ((MenuState) gameStates.get(currentState)).setNeedsRedraw(true);
            }
        } catch (Exception e) {
            System.err.println("Error in requestRedraw: " + e.getMessage());
        }
    }
}