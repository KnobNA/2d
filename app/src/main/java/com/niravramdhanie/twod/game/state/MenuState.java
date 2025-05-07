package com.niravramdhanie.twod.game.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.niravramdhanie.twod.game.core.GameStateManager;
import com.niravramdhanie.twod.game.ui.Button;
import com.niravramdhanie.twod.game.utils.ResourceLoader;

public class MenuState extends GameState {
    private BufferedImage background;
    private List<Button> buttons;
    private String title;
    private Font titleFont;
    private Font buttonFont;
    private int screenWidth;
    private int screenHeight;
    private boolean initialized = false;
    private boolean needsRedraw = true;
    private BufferedImage menuBuffer;
    private boolean renderingInProgress = false;
    
    public MenuState(GameStateManager gsm) {
        super(gsm);
        this.screenWidth = gsm.getWidth();
        this.screenHeight = gsm.getHeight();
        title = "My 2D Game";
        titleFont = new Font("Arial", Font.BOLD, 48);
        buttonFont = new Font("Arial", Font.PLAIN, 24);
        buttons = new ArrayList<>();
        System.out.println("MenuState created. Dimensions: " + screenWidth + "x" + screenHeight);
    }
    
    @Override
    public void init() {
        System.out.println("MenuState initializing...");
        
        try {
            // Load resources
            background = ResourceLoader.loadImage("/backgrounds/menu_bg.jpg");
            if (background == null) {
                System.err.println("Failed to load menu background image!");
            } else {
                System.out.println("Menu background loaded successfully");
            }
            
            // Create buttons
            int buttonWidth = 200;
            int buttonHeight = 50;
            int startX = screenWidth / 2 - buttonWidth / 2;
            int startY = screenHeight / 2 - 50;
            int padding = 20;
            
            buttons.clear(); // Clear any existing buttons in case init() is called multiple times
            buttons.add(new Button(startX, startY, buttonWidth, buttonHeight, "Play"));
            buttons.add(new Button(startX, startY + buttonHeight + padding, buttonWidth, buttonHeight, "Options"));
            buttons.add(new Button(startX, startY + (buttonHeight + padding) * 2, buttonWidth, buttonHeight, "Exit"));
            
            // Create hardware-accelerated buffer for the menu render with a compatible image
            // This improves rendering performance significantly
            java.awt.GraphicsConfiguration gc = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                                              .getDefaultScreenDevice().getDefaultConfiguration();
            menuBuffer = gc.createCompatibleImage(screenWidth, screenHeight, java.awt.Transparency.TRANSLUCENT);
            
            initialized = true;
            needsRedraw = true; // Always need redraw after initialization
            
            // Draw to the buffer immediately to ensure content is available for first render
            renderToBuffer();
            
            System.out.println("MenuState initialized");
        } catch (Exception e) {
            System.err.println("Error in MenuState.init(): " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Renders the menu content to the buffer
     */
    private void renderToBuffer() {
        if (menuBuffer == null) return;
        
        System.out.println("Rendering menu to buffer");
        
        // Get graphics from buffer with acceleration hints
        Graphics2D bufferG = menuBuffer.createGraphics();
        bufferG.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
                                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        bufferG.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, 
                                java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        bufferG.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, 
                                java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        
        // Clear the buffer first
        bufferG.setComposite(java.awt.AlphaComposite.Clear);
        bufferG.fillRect(0, 0, screenWidth, screenHeight);
        bufferG.setComposite(java.awt.AlphaComposite.SrcOver);
        
        // Draw background
        if (background != null) {
            bufferG.drawImage(background, 0, 0, screenWidth, screenHeight, null);
        } else {
            // Fallback if background image failed to load
            bufferG.setColor(new Color(50, 80, 120)); // Dark blue background
            bufferG.fillRect(0, 0, screenWidth, screenHeight);
        }
        
        // Draw title
        bufferG.setFont(titleFont);
        bufferG.setColor(Color.WHITE);
        int titleWidth = bufferG.getFontMetrics().stringWidth(title);
        bufferG.drawString(title, screenWidth / 2 - titleWidth / 2, 150);
        
        // Draw buttons
        bufferG.setFont(buttonFont);
        for (Button button : buttons) {
            button.render(bufferG);
        }
        
        // Cleanup
        bufferG.dispose();
        
        // Mark as no longer needing a redraw
        needsRedraw = false;
    }
    
    @Override
    public void update() {
        // Update button states if needed
        for (Button button : buttons) {
            if (button.update()) {
                // If any button state changed, we need to redraw
                needsRedraw = true;
            }
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        if (!initialized) {
            // If not initialized, always draw the initialization message
            g.setColor(Color.RED);
            g.drawString("Menu not fully initialized!", 10, 20);
            return;
        }

        try {
            if (needsRedraw && !renderingInProgress) {
                // Avoid concurrent redraws
                renderingInProgress = true;
                
                renderToBuffer();
                
                renderingInProgress = false;
            }
            
            // Draw the buffered menu image to the screen
            g.drawImage(menuBuffer, 0, 0, null);
            
        } catch (Exception e) {
            renderingInProgress = false; // Reset the flag in case of an error
            System.err.println("Error in MenuState.render(): " + e.getMessage());
            e.printStackTrace();
            
            // Display error on screen
            g.setColor(Color.RED);
            g.drawString("Error rendering menu: " + e.getMessage(), 10, 40);
        }
    }
    
    @Override
    public void keyPressed(int k) {
        // Handle key press in menu
    }
    
    @Override
    public void keyReleased(int k) {
        // Handle key release in menu
    }
    
    @Override
    public void mousePressed(int x, int y) {
        // Check if buttons are clicked
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).contains(x, y)) {
                buttons.get(i).setPressed(true);
                needsRedraw = true;
            }
        }
    }
    
    @Override
    public void mouseReleased(int x, int y) {
        // Handle button clicks
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).contains(x, y) && buttons.get(i).isPressed()) {
                // Handle button action
                switch (i) {
                    case 0: // Play button
                        gsm.setState(GameStateManager.PLAY_STATE);
                        break;
                    case 1: // Options button
                        // Could transition to an options state
                        break;
                    case 2: // Exit button
                        System.exit(0);
                        break;
                }
            }
            if (buttons.get(i).isPressed()) {
                buttons.get(i).setPressed(false);
                needsRedraw = true;
            }
        }
    }
    
    @Override
    public void mouseMoved(int x, int y) {
        // Update button hover states
        for (Button button : buttons) {
            boolean wasHovered = button.isHovered();
            button.setHovered(button.contains(x, y));
            // If hover state changed, we need to redraw
            if (wasHovered != button.isHovered()) {
                needsRedraw = true;
            }
        }
    }
    
    /**
     * Sets whether the menu needs to be redrawn
     * @param needsRedraw true if the menu needs redrawing
     */
    public void setNeedsRedraw(boolean needsRedraw) {
        this.needsRedraw = needsRedraw;
    }
}