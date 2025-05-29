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
import javax.swing.SwingUtilities;

public class GamePanel extends JPanel {
    // Double buffering: use two images and swap between them
    private BufferedImage frontBuffer;
    private BufferedImage backBuffer;
    private Graphics2D frontG2d;
    private Graphics2D backG2d;
    private final Object bufferLock = new Object(); // Synchronization for buffer swapping
    
    private int width;
    private int height;
    private Font debugFont;
    private long lastFrameTime = 0;
    private int fpsCount = 0;
    private int currentFps = 0;
    private boolean showFpsCounter = true;
    private boolean needsRepaint = false;
    
    public GamePanel(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        requestFocus();
        
        // Enable double buffering at JPanel level as well
        setDoubleBuffered(true);
        
        // Create the debug font
        debugFont = new Font("Arial", Font.PLAIN, 12);
        
        // Create compatible images for better performance
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                  .getDefaultScreenDevice().getDefaultConfiguration();
        
        // Create both front and back buffers
        frontBuffer = gc.createCompatibleImage(width, height);
        backBuffer = gc.createCompatibleImage(width, height);
        
        frontG2d = (Graphics2D) frontBuffer.getGraphics();
        backG2d = (Graphics2D) backBuffer.getGraphics();
        
        // Enable anti-aliasing for both graphics contexts
        setupGraphicsContext(frontG2d);
        setupGraphicsContext(backG2d);
        
        System.out.println("GamePanel initialized at " + width + "x" + height + " with double buffering");
        
        // Draw initial loading screen to back buffer
        synchronized (bufferLock) {
            backG2d.setColor(Color.BLACK);
            backG2d.fillRect(0, 0, width, height);
            backG2d.setColor(Color.WHITE);
            backG2d.setFont(debugFont);
            backG2d.drawString("Loading game...", width/2 - 40, height/2);
            
            // Swap buffers and request repaint
            swapBuffers();
        }
        
        // Request initial repaint on EDT
        SwingUtilities.invokeLater(this::repaint);
    }
    
    private void setupGraphicsContext(Graphics2D g2d) {
        // Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }
    
    private void swapBuffers() {
        // Swap the buffers
        BufferedImage tempImage = frontBuffer;
        Graphics2D tempG2d = frontG2d;
        
        frontBuffer = backBuffer;
        frontG2d = backG2d;
        
        backBuffer = tempImage;
        backG2d = tempG2d;
        
        needsRepaint = true;
    }

    public void render(GameStateManager gsm) {
        // Check if graphics context is available
        if (backG2d == null) {
            System.err.println("Back buffer graphics context is null!");
            return;
        }
        
        // Render to the back buffer
        synchronized (bufferLock) {
            // Check if GSM is available
            if (gsm == null) {
                // Draw loading message
                backG2d.setColor(Color.BLACK);
                backG2d.fillRect(0, 0, width, height);
                backG2d.setColor(Color.WHITE);
                backG2d.setFont(debugFont);
                backG2d.drawString("Loading game...", width/2 - 40, height/2);
                
                swapBuffers();
                // Schedule repaint on EDT
                SwingUtilities.invokeLater(this::repaint);
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
                
                // Clear the back buffer
                backG2d.setColor(Color.BLACK);
                backG2d.fillRect(0, 0, width, height);
                
                // Let the current game state render its content to back buffer
                gsm.render(backG2d);
                
                // Additional debug info
                if (showFpsCounter) {
                    backG2d.setColor(Color.YELLOW);
                    backG2d.setFont(debugFont);
                    backG2d.drawString("FPS: " + currentFps, 10, height - 20);
                }
                
                // Swap buffers after rendering is complete
                swapBuffers();
                
                // Schedule repaint on EDT only if needed
                if (gsm.getCurrentState() != GameStateManager.MENU_STATE || gsm.needsConstantUpdates()) {
                    SwingUtilities.invokeLater(this::repaint);
                }
            } catch (Exception e) {
                System.err.println("Error in GamePanel.render(): " + e.getMessage());
                e.printStackTrace();
                
                // Draw error message to back buffer
                backG2d.setColor(Color.RED);
                backG2d.drawString("Rendering Error: " + e.getMessage(), 10, 30);
                
                swapBuffers();
                SwingUtilities.invokeLater(this::repaint);
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        synchronized (bufferLock) {
            if (frontBuffer != null) {
                // Draw the front buffer to the screen
                g.drawImage(frontBuffer, 0, 0, this);
                needsRepaint = false;
            } else {
                // Fallback if front buffer is null
                g.setColor(Color.RED);
                g.fillRect(0, 0, width, height);
                g.setColor(Color.WHITE);
                g.drawString("Front buffer not initialized!", 10, 30);
            }
        }
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        
        // Recreate the graphics contexts if needed after the component is added to a container
        if ((frontG2d == null || backG2d == null) && frontBuffer != null && backBuffer != null) {
            frontG2d = (Graphics2D) frontBuffer.getGraphics();
            backG2d = (Graphics2D) backBuffer.getGraphics();
            
            // Re-setup graphics contexts
            setupGraphicsContext(frontG2d);
            setupGraphicsContext(backG2d);
        }
        
        // Force repaint when added to container
        System.out.println("Panel added to container, forcing repaint");
        SwingUtilities.invokeLater(this::repaint);
    }

    /**
     * Forces a repaint of the panel regardless of state
     * Call this when you need an immediate repaint without waiting for the game loop
     */
    public void forceRepaint() {
        System.out.println("Force repainting panel");
        SwingUtilities.invokeLater(this::repaint);
    }
    
    /**
     * Cleanup method to dispose of graphics resources
     */
    public void dispose() {
        synchronized (bufferLock) {
            if (frontG2d != null) {
                frontG2d.dispose();
                frontG2d = null;
            }
            if (backG2d != null) {
                backG2d.dispose();
                backG2d = null;
            }
            frontBuffer = null;
            backBuffer = null;
        }
    }
}