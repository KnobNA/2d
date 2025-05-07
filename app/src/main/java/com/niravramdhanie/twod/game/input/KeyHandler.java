package com.niravramdhanie.twod.game.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.niravramdhanie.twod.game.core.GameStateManager;

public class KeyHandler implements KeyListener {
    private boolean[] keys;
    private GameStateManager gsm;
    
    public KeyHandler() {
        keys = new boolean[256];
    }
    
    public void setGameStateManager(GameStateManager gsm) {
        this.gsm = gsm;
    }
    
    public void update() {
        // This could be used to track key states
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        
        if (gsm != null) {
            gsm.keyPressed(e.getKeyCode());
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        
        if (gsm != null) {
            gsm.keyReleased(e.getKeyCode());
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
    
    public boolean isKeyDown(int keyCode) {
        return keys[keyCode];
    }
}