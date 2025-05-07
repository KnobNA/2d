package com.niravramdhanie.twod.game.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.niravramdhanie.twod.game.core.GameStateManager;
import com.niravramdhanie.twod.game.entity.BallPlayer;
import com.niravramdhanie.twod.game.entity.Block;
import com.niravramdhanie.twod.game.entity.Button;
import com.niravramdhanie.twod.game.entity.Button.ButtonAction;
import com.niravramdhanie.twod.game.entity.Button.ButtonActionListener;

public class PlayState extends GameState implements ButtonActionListener {
    private BallPlayer player;
    private List<Block> blocks;
    private List<Button> buttons;
    private int screenWidth;
    private int screenHeight;
    private Random random;
    private boolean initialized = false;
    
    // Game state for button actions
    private boolean doorOpen = false;
    private int itemsSpawned = 0;
    private boolean platformActive = false;
    
    public PlayState(GameStateManager gsm, int screenWidth, int screenHeight) {
        super(gsm);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.random = new Random();
        System.out.println("PlayState created with dimensions: " + screenWidth + "x" + screenHeight);
    }
    
    // Overload constructor for backward compatibility if needed
    public PlayState(GameStateManager gsm) {
        super(gsm);
        this.screenWidth = gsm.getWidth();
        this.screenHeight = gsm.getHeight();
        this.random = new Random();
        System.out.println("PlayState created with dimensions from GSM: " + screenWidth + "x" + screenHeight);
    }
    
    @Override
    public void init() {
        System.out.println("PlayState.init() called");
        
        // Create blocks
        blocks = new ArrayList<>();
        createBlocks(10); // Create 10 random blocks
        
        // Create buttons
        buttons = new ArrayList<>();
        createButtons();
        
        try {
            // Create player in the lower middle of the screen
            int playerSize = 32;
            int playerX = screenWidth / 2 - playerSize / 2;
            int playerY = screenHeight - playerSize - 50; // 50 pixels from bottom
            System.out.println("Creating player at: " + playerX + "," + playerY);
            player = new BallPlayer(playerX, playerY, playerSize, playerSize, screenWidth, screenHeight);
            player.setBlocks(blocks);
            
            initialized = true;
            System.out.println("PlayState initialization complete");
        } catch (Exception e) {
            System.err.println("Error initializing player: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createButtons() {
        // Create different types of buttons
        int buttonWidth = 32;
        int buttonHeight = 8;
        
        // Door Button (top left area)
        Button doorButton = new Button(100, 100, buttonWidth, buttonHeight, ButtonAction.OPEN_DOOR);
        doorButton.setActionValue(1); // Door ID
        doorButton.setActionListener(this);
        buttons.add(doorButton);
        
        // Items Button (top right area)
        Button itemsButton = new Button(screenWidth - 150, 100, buttonWidth, buttonHeight, ButtonAction.SPAWN_ITEMS);
        itemsButton.setActionValue(3); // Number of items to spawn
        itemsButton.setColors(new Color(0, 200, 0), new Color(0, 100, 0)); // Green button
        itemsButton.setActionListener(this);
        buttons.add(itemsButton);
        
        // Platform Button (bottom left area)
        Button platformButton = new Button(150, screenHeight - 150, buttonWidth, buttonHeight, ButtonAction.TOGGLE_PLATFORM);
        platformButton.setActionValue(1); // Platform ID
        platformButton.setColors(new Color(0, 0, 220), new Color(0, 0, 100)); // Blue button
        platformButton.setActionListener(this);
        buttons.add(platformButton);
        
        // Trap Button (bottom right area)
        Button trapButton = new Button(screenWidth - 100, screenHeight - 100, buttonWidth, buttonHeight, ButtonAction.TRIGGER_TRAP);
        trapButton.setActionValue(2); // Trap ID
        trapButton.setColors(new Color(220, 220, 0), new Color(100, 100, 0)); // Yellow button
        trapButton.setActionListener(this);
        buttons.add(trapButton);
    }
    
    private void createBlocks(int numBlocks) {
        int blockWidth = 64;
        int blockHeight = 64;
        
        for (int i = 0; i < numBlocks; i++) {
            // Generate random positions, but don't spawn in the bottom center
            // where the player will start
            int x, y;
            boolean validPosition;
            
            do {
                validPosition = true;
                x = random.nextInt(screenWidth - blockWidth);
                y = random.nextInt(screenHeight - blockHeight);
                
                // Avoid spawning in the bottom center (player spawn area)
                int playerSpawnX = screenWidth / 2 - 50; // 50 is half player spawn width
                int playerSpawnY = screenHeight - 150; // 150 is approximate player spawn height
                int playerSpawnWidth = 100; // Width of the spawn area
                int playerSpawnHeight = 150; // Height of the spawn area
                
                // Check if block overlaps with player spawn area
                if (x < playerSpawnX + playerSpawnWidth &&
                    x + blockWidth > playerSpawnX &&
                    y < playerSpawnY + playerSpawnHeight &&
                    y + blockHeight > playerSpawnY) {
                    validPosition = false;
                }
                
                // Check if block overlaps with other blocks
                for (Block block : blocks) {
                    if (x < block.getX() + block.getWidth() + 10 &&
                        x + blockWidth + 10 > block.getX() &&
                        y < block.getY() + block.getHeight() + 10 &&
                        y + blockHeight + 10 > block.getY()) {
                        validPosition = false;
                        break;
                    }
                }
            } while (!validPosition);
            
            blocks.add(new Block(x, y, blockWidth, blockHeight));
        }
    }
    
    @Override
    public void update() {
        player.update();
        
        // Update blocks (if they had dynamic behavior)
        for (Block block : blocks) {
            block.update();
        }
        
        // Update buttons
        for (Button button : buttons) {
            button.update();
            
            // Check player-button interactions
            if (player.checkCollision(button)) {
                // Player is on the button
                button.playerOverlap(player);
                
                // Check for space key to activate the button
                if (interactionKeyPressed) {
                    interactionKeyPressed = false; // Reset flag after processing
                }
            } else {
                if (button.isPressed()) {
                    button.playerLeave();
                }
            }
        }
    }
    
    // Flag to track if the interaction key was pressed
    private boolean interactionKeyPressed = false;
    
    @Override
    public void render(Graphics2D g) {
        // Clear screen with background color
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        // Draw blocks
        if (blocks != null) {
            for (Block block : blocks) {
                block.render(g);
            }
        }
        
        // Draw buttons
        if (buttons != null) {
            for (Button button : buttons) {
                button.render(g);
            }
        }
        
        // Draw player
        if (player != null) {
            player.render(g);
        } else {
            // Fallback if player is null
            g.setColor(Color.RED);
            g.fillOval(screenWidth / 2 - 16, screenHeight - 16 - 50, 32, 32);
            
            // Debug message
            g.setColor(Color.WHITE);
            g.drawString("Player object is null!", 10, 20);
            
            // If not initialized, try to initialize
            if (!initialized) {
                System.out.println("Player was null, attempting to initialize again");
                init();
            }
        }
        
        // Draw instructions
        g.setColor(Color.WHITE);
        g.drawString("Walk over buttons to press them", 10, 20);
        
        // Draw button action state information
        g.setColor(Color.WHITE);
        g.drawString("Door: " + (doorOpen ? "Open" : "Closed"), 10, 40);
        g.drawString("Items: " + itemsSpawned, 10, 60);
        g.drawString("Platform: " + (platformActive ? "Active" : "Inactive"), 10, 80);
    }
    
    @Override
    public void keyPressed(int k) {
        if (k == KeyEvent.VK_LEFT) player.setLeft(true);
        if (k == KeyEvent.VK_RIGHT) player.setRight(true);
        if (k == KeyEvent.VK_UP) player.setUp(true);
        if (k == KeyEvent.VK_DOWN) player.setDown(true);
        
        // Alternative WASD controls
        if (k == KeyEvent.VK_A) player.setLeft(true);
        if (k == KeyEvent.VK_D) player.setRight(true);
        if (k == KeyEvent.VK_W) player.setUp(true);
        if (k == KeyEvent.VK_S) player.setDown(true);
        
        // Interaction key
        if (k == KeyEvent.VK_SPACE) {
            interactionKeyPressed = true;
        }
    }
    
    @Override
    public void keyReleased(int k) {
        if (k == KeyEvent.VK_LEFT) player.setLeft(false);
        if (k == KeyEvent.VK_RIGHT) player.setRight(false);
        if (k == KeyEvent.VK_UP) player.setUp(false);
        if (k == KeyEvent.VK_DOWN) player.setDown(false);
        
        // Alternative WASD controls
        if (k == KeyEvent.VK_A) player.setLeft(false);
        if (k == KeyEvent.VK_D) player.setRight(false);
        if (k == KeyEvent.VK_W) player.setUp(false);
        if (k == KeyEvent.VK_S) player.setDown(false);
        
        // Interaction key
        if (k == KeyEvent.VK_SPACE) {
            interactionKeyPressed = false;
        }
    }
    
    @Override
    public void mousePressed(int x, int y) {
        // Not used in this example
    }
    
    @Override
    public void mouseReleased(int x, int y) {
        // Not used in this example
    }
    
    @Override
    public void mouseMoved(int x, int y) {
        // Not used in this example
    }
    
    @Override
    public void onButtonPressed(Button button) {
        // Handle button actions
        switch (button.getActionType()) {
            case OPEN_DOOR:
                doorOpen = !doorOpen;
                System.out.println("Door " + button.getActionValue() + " is now " + (doorOpen ? "open" : "closed"));
                break;
                
            case SPAWN_ITEMS:
                itemsSpawned += button.getActionValue();
                System.out.println(button.getActionValue() + " items spawned, total: " + itemsSpawned);
                break;
                
            case TOGGLE_PLATFORM:
                platformActive = !platformActive;
                System.out.println("Platform " + button.getActionValue() + " is now " + (platformActive ? "active" : "inactive"));
                break;
                
            case TRIGGER_TRAP:
                System.out.println("Trap " + button.getActionValue() + " triggered!");
                // Could apply damage to player if in range
                if (random.nextBoolean()) {
                    player.damage(10);
                    System.out.println("Player took 10 damage from trap! Health: " + player.getHealth());
                }
                break;
                
            case CUSTOM:
                System.out.println("Custom action with value " + button.getActionValue() + " triggered");
                break;
        }
    }
}