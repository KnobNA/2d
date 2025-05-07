package com.niravramdhanie.twod.game.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GamePanel extends JPanel {
    private BufferedImage image;
    private Graphics2D g2d;
    private int width;
    private int height;
    private Font debugFont;
    private long lastFrameTime = 0;
    private int fpsCount = 0;
    private int currentFps = 0;
    private boolean showFpsCounter = true;
    
    public GamePanel(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        requestFocus();
        
        // Enable double buffering - this is crucial for smooth rendering
        setDoubleBuffered(true);
        
        // Create the debug font
        debugFont = new Font("Arial", Font.PLAIN, 12);
        
        // Create compatible image for better performance
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                  .getDefaultScreenDevice().getDefaultConfiguration();
        image = gc.createCompatibleImage(width, height);
        
        g2d = (Graphics2D) image.getGraphics();
        
        // Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        System.out.println("GamePanel initialized at " + width + "x" + height);
        
        // Draw a initial loading screen
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.WHITE);
        g2d.setFont(debugFont);
        g2d.drawString("Loading game...", width/2 - 40, height/2);
        repaint();
    }
    
    public void render(GameStateManager gsm) {
        // Check if graphics context is available
        if (g2d == null) {
            System.err.println("Graphics context is null!");
            return;
        }
        
        // Check if GSM is available
        if (gsm == null) {
            // Draw loading message
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, height);
            g2d.setColor(Color.WHITE);
            g2d.setFont(debugFont);
            g2d.drawString("Loading game...", width/2 - 40, height/2);
            repaint();
            return;
        }
        
        try {
            // Calculate FPS
            long currentTime = System.currentTimeMillis();
            fpsCount++;
            
            // Update FPS counter once per second
            if (currentTime - lastFrameTime >= 1000) {
                currentFps = fpsCount;
                fpsCount = 0;
                lastFrameTime = currentTime;
            }
            
            // Clear the buffer
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, height);
            
            // Let the current game state render its content
            gsm.render(g2d);
            
            // Additional debug info
            if (showFpsCounter) {
                g2d.setColor(Color.YELLOW);
                g2d.setFont(debugFont);
                g2d.drawString("FPS: " + currentFps, 10, height - 20);
            }
            
            // Only request a repaint if we're in an active state that needs constant updates
            // MenuState is static and doesn't need constant updates
            if (gsm.getCurrentState() != GameStateManager.MENU_STATE || gsm.needsConstantUpdates()) {
                repaint();
            }
        } catch (Exception e) {
            System.err.println("Error in GamePanel.render(): " + e.getMessage());
            e.printStackTrace();
            
            // Draw error message
            g2d.setColor(Color.RED);
            g2d.drawString("Rendering Error: " + e.getMessage(), 10, 30);
            repaint();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (image != null) {
            // Draw the buffered image to the screen
            g.drawImage(image, 0, 0, this);
        } else {
            // Fallback if image is null
            g.setColor(Color.RED);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.WHITE);
            g.drawString("Buffer not initialized!", 10, 30);
        }
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        
        // Recreate the graphics context if needed after the component is added to a container
        if (g2d == null && image != null) {
            g2d = (Graphics2D) image.getGraphics();
            
            // Re-enable anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        
        // Force repaint when added to container
        System.out.println("Panel added to container, forcing repaint");
        repaint();
    }

    /**
     * Forces a repaint of the panel regardless of state
     * Call this when you need an immediate repaint without waiting for the game loop
     */
    public void forceRepaint() {
        System.out.println("Force repainting panel");
        repaint();
    }
}