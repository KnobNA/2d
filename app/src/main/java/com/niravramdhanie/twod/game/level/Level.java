package com.niravramdhanie.twod.game.level;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.niravramdhanie.twod.game.entity.Block;
import com.niravramdhanie.twod.game.entity.Box;
import com.niravramdhanie.twod.game.entity.Button;
import com.niravramdhanie.twod.game.entity.Entity;
import com.niravramdhanie.twod.game.utils.GridSystem;

/**
 * Represents a game level with entities placed on a grid.
 */
public class Level {
    private GridSystem grid;
    private List<Entity> entities;
    private Random random;
    private int screenWidth;
    private int screenHeight;
    
    /**
     * Creates a new level with the specified dimensions.
     * 
     * @param screenWidth The width of the screen in pixels
     * @param screenHeight The height of the screen in pixels
     * @param gridCellSize The size of each grid cell in pixels
     */
    public Level(int screenWidth, int screenHeight, int gridCellSize) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.grid = new GridSystem(gridCellSize, gridCellSize, screenWidth, screenHeight);
        this.entities = new ArrayList<>();
        this.random = new Random();
    }
    
    /**
     * Adds a block at the specified grid position.
     * 
     * @param gridX The X position on the grid
     * @param gridY The Y position on the grid
     * @return True if the block was added successfully, false if the position was already occupied
     */
    public boolean addBlock(int gridX, int gridY) {
        int cellSize = grid.getCellSize();
        Block block = new Block(0, 0, cellSize, cellSize);
        
        if (grid.placeEntity(block, gridX, gridY)) {
            entities.add(block);
            return true;
        }
        
        return false;
    }
    
    /**
     * Adds any entity at the specified grid position.
     * 
     * @param entity The entity to add
     * @param gridX The X position on the grid
     * @param gridY The Y position on the grid
     * @return True if the entity was added successfully, false if the position was already occupied
     */
    public boolean addEntity(Entity entity, int gridX, int gridY) {
        int cellSize = grid.getCellSize();
        entity.setWidth(cellSize);
        entity.setHeight(cellSize);
        
        if (grid.placeEntity(entity, gridX, gridY)) {
            entities.add(entity);
            return true;
        }
        
        return false;
    }
    
    /**
     * Removes an entity at the specified grid position.
     * 
     * @param gridX The X position on the grid
     * @param gridY The Y position on the grid
     * @return True if an entity was removed, false otherwise
     */
    public boolean removeEntityAt(int gridX, int gridY) {
        Entity entity = grid.removeEntity(gridX, gridY);
        if (entity != null) {
            entities.remove(entity);
            return true;
        }
        return false;
    }
    
    /**
     * Gets the entity at the specified grid position.
     * 
     * @param gridX The X position on the grid
     * @param gridY The Y position on the grid
     * @return The entity at the specified position, or null if no entity is there
     */
    public Entity getEntityAt(int gridX, int gridY) {
        return grid.getEntity(gridX, gridY);
    }
    
    /**
     * Creates a border of blocks around the level.
     */
    public void addBorderBlocks() {
        int horizontalCells = grid.getHorizontalCells();
        int verticalCells = grid.getVerticalCells();
        
        // Add top and bottom borders
        for (int x = 0; x < horizontalCells; x++) {
            addBlock(x, 0);
            addBlock(x, verticalCells - 1);
        }
        
        // Add left and right borders (skip corners as they're already added)
        for (int y = 1; y < verticalCells - 1; y++) {
            addBlock(0, y);
            addBlock(horizontalCells - 1, y);
        }
    }
    
    /**
     * Creates the first level layout with blocks and designated areas.
     */
    public void createLevel1() {
        clearLevel();
        
        // Add border blocks
        addBorderBlocks();
    }
    
    /**
     * Creates the second level layout with blocks and designated areas.
     */
    public void createLevel2() {
        clearLevel();
        
        // Add border blocks for level 2
        addBorderBlocks();
        
        // Additional level 2 specific layout will be added here later
    }
    
    /**
     * Updates all entities in the level.
     */
    public void update() {
        for (Entity entity : entities) {
            entity.update();
        }
    }
    
    /**
     * Renders all entities in the level.
     * 
     * @param g The Graphics2D object to render to
     */
    public void render(Graphics2D g) {
        // First render all non-box entities
        for (Entity entity : entities) {
            if (!(entity instanceof Box)) {
                entity.render(g);
            }
        }
        
        // Then render all boxes
        for (Entity entity : entities) {
            if (entity instanceof Box) {
                entity.render(g);
            }
        }
    }
    
    /**
     * Gets all entities in the level.
     * 
     * @return A list of all entities
     */
    public List<Entity> getEntities() {
        return entities;
    }
    
    /**
     * Gets all blocks in the level.
     * 
     * @return A list of all blocks
     */
    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity instanceof Block) {
                blocks.add((Block) entity);
            }
        }
        return blocks;
    }
    
    /**
     * Gets all buttons in the level.
     * 
     * @return A list of all buttons
     */
    public List<Button> getButtons() {
        List<Button> buttons = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity instanceof Button) {
                buttons.add((Button) entity);
            }
        }
        return buttons;
    }
    
    /**
     * Adds a button at the specified grid position.
     * 
     * @param button The button to add
     * @param gridX The X position on the grid
     * @param gridY The Y position on the grid
     * @return True if the button was added successfully, false if the position was already occupied
     */
    public boolean addButton(Button button, int gridX, int gridY) {
        return addEntity(button, gridX, gridY);
    }
    
    /**
     * Gets the grid system.
     * 
     * @return The grid system
     */
    public GridSystem getGrid() {
        return grid;
    }
    
    /**
     * Clears all entities from the level.
     */
    public void clearLevel() {
        entities.clear();
        grid.clearGrid();
    }
    
    /**
     * Resizes the level to fit a new screen size.
     * 
     * @param newScreenWidth The new screen width in pixels
     * @param newScreenHeight The new screen height in pixels
     */
    public void resize(int newScreenWidth, int newScreenHeight) {
        this.screenWidth = newScreenWidth;
        this.screenHeight = newScreenHeight;
        grid.resize(newScreenWidth, newScreenHeight);
    }
} 