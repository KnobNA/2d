package com.niravramdhanie.twod.game.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.niravramdhanie.twod.game.core.GameStateManager;

public class MouseHandler implements MouseListener, MouseMotionListener {
    private int mouseX;
    private int mouseY;
    private boolean clicked;
    private GameStateManager gsm;
    
    public MouseHandler() {
        mouseX = 0;
        mouseY = 0;
        clicked = false;
    }
    
    public void setGameStateManager(GameStateManager gsm) {
        this.gsm = gsm;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        clicked = true;
        
        if (gsm != null) {
            gsm.mousePressed(x, y);
            
            gsm.requestRedraw();
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        clicked = false;
        
        if (gsm != null) {
            gsm.mouseReleased(x, y);
            
            gsm.requestRedraw();
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        mouseX = x;
        mouseY = y;
        
        if (gsm != null) {
            gsm.mouseMoved(x, y);
            
            gsm.requestRedraw();
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        // Not used
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        // Not used
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        // Not used
    }
    
    // Getters
    public int getMouseX() {
        return mouseX;
    }
    
    public int getMouseY() {
        return mouseY;
    }
    
    public boolean isClicked() {
        return clicked;
    }
}