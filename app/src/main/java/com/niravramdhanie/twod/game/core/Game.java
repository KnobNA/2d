package com.niravramdhanie.twod.game.core;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.niravramdhanie.twod.game.input.KeyHandler;
import com.niravramdhanie.twod.game.input.MouseHandler;

public class Game implements Runnable {
    private JFrame window;
    private GamePanel gamePanel;
    private Thread gameThread;
    private GameStateManager gsm;
    private KeyHandler keyHandler;
    private MouseHandler mouseHandler;
    private boolean running;
    private final int FPS = 60;
    private int width;
    private int height;
    private boolean initialized = false;
    
    public Game(String title, int width, int height) {
        this.width = width;
        this.height = height;
        
        System.out.println("Creating game window with dimensions: " + width + "x" + height);
        
        // Use SwingUtilities for proper event dispatch thread handling
        SwingUtilities.invokeLater(() -> {
            try {
                window = new JFrame(title);
                window.setSize(width, height);
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setResizable(false);
                window.setLocationRelativeTo(null);
                
                gamePanel = new GamePanel(width, height);
                keyHandler = new KeyHandler();
                mouseHandler = new MouseHandler();
                
                System.out.println("Creating GameStateManager");
                gsm = new GameStateManager(width, height);
                
                System.out.println("Setting input handlers");
                gsm.setInputHandlers(keyHandler, mouseHandler);
                
                gamePanel.addKeyListener(keyHandler);
                gamePanel.addMouseListener(mouseHandler);
                gamePanel.addMouseMotionListener(mouseHandler);
                
                window.add(gamePanel);
                window.pack();
                window.setVisible(true);
                
                gamePanel.requestFocus();
                
                System.out.println("Window setup complete");
                
                // Set initialized flag and start game when UI is ready
                initialized = true;
                
                // Force an initial render to make sure menu appears immediately
                if (gamePanel != null && gsm != null) {
                    gamePanel.render(gsm);
                    gamePanel.forceRepaint();
                    
                    // Set up a delayed extra render after a short delay to ensure menu is visible
                    // This helps with some rendering quirks at startup
                    Timer initialRenderTimer = new Timer(150, e -> {
                        System.out.println("Delayed initial render triggered");
                        if (gamePanel != null && gsm != null) {
                            gsm.requestRedraw();
                            gamePanel.render(gsm);
                            gamePanel.forceRepaint();
                        }
                    });
                    initialRenderTimer.setRepeats(false);
                    initialRenderTimer.start();
                }
                
                // Start the game thread now that everything is initialized
                startGameThread();
            } catch (Exception e) {
                System.err.println("Error in game initialization: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    public void start() {
        System.out.println("Requesting game start");
        // This method is called from Main, but actual start is handled
        // after UI initialization completes
        
        // If already initialized (which shouldn't happen normally), start immediately
        if (initialized) {
            startGameThread();
        }
    }
    
    private void startGameThread() {
        System.out.println("Starting game thread");
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    @Override
    public void run() {
        System.out.println("Game loop started");
        
        // Verify everything is initialized before starting the game loop
        if (!initialized || gsm == null || gamePanel == null) {
            System.err.println("Game not properly initialized before running!");
            if (gsm == null) System.err.println("GameStateManager is null!");
            if (gamePanel == null) System.err.println("GamePanel is null!");
            return;
        }
        
        gameLoop();
    }
    
    private void gameLoop() {
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1000 / TARGET_FPS;
        
        long lastUpdateTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double ns = 1000000000.0 / TARGET_FPS;
        double delta = 0;
        int frames = 0;
        int updates = 0;
        
        // Force first render
        boolean forceFirstRender = true;
        
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastUpdateTime) / ns;
            lastUpdateTime = now;
            
            boolean needsRender = false;
            
            // Update game logic
            if (delta >= 1) {
                gsm.update();
                
                // Update key handler if available
                if (keyHandler != null) {
                    keyHandler.update();
                }
                
                updates++;
                delta--;
                needsRender = true;
            }
            
            // Render at consistent intervals for smooth display
            // With proper double buffering, we can render more frequently without flickering
            if (needsRender || gsm.needsConstantUpdates() || forceFirstRender) {
                forceFirstRender = false; // Reset after first forced render
                
                // Render using the improved double buffering system
                gamePanel.render(gsm);
                frames++;
            } else {
                // For static states, we still render occasionally to handle any UI updates
                // but at a reduced rate to save CPU
                try {
                    Thread.sleep(16); // ~60 FPS limit for static content
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // Output FPS and UPS every second for debugging
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames + ", UPS: " + updates);
                frames = 0;
                updates = 0;
            }
            
            // Sleep to maintain consistent frame rate and prevent excessive CPU usage
            try {
                long elapsed = System.nanoTime() - now;
                long sleepTime = OPTIMAL_TIME - elapsed / 1000000;
                
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        stop();
    }
    
    private void stop() {
        try {
            System.out.println("Stopping game");
            running = false;
            
            // Clean up graphics resources
            if (gamePanel != null) {
                gamePanel.dispose();
            }
            
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            System.err.println("Error stopping game: " + e.getMessage());
            e.printStackTrace();
        }
    }
}