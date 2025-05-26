package com.niravramdhanie.twod.game.utils;

import java.util.HashMap;
import java.util.Map;

import com.niravramdhanie.twod.game.entity.Entity;

/**
 * A grid system for organizing and placing game entities.
 * The grid spans the entire screen and scales to fit screens of any size.
 */
public class GridSystem {
    private int gridWidth;
    private int gridHeight;
    private int cellSize;
    private int screenWidth;
    private int screenHeight;
    private int horizontalCells;
    private int verticalCells;
    private Map<String, Entity> gridEntities;
    
    /**
     * Creates a new grid system with the specified dimensions.
     * 
     * @param gridWidth The width of each grid cell in pixels (before scaling)
     * @param gridHeight The height of each grid cell in pixels (before scaling)
     * @param screenWidth The width of the screen in pixels
     * @param screenHeight The height of the screen in pixels
     */
    public GridSystem(int gridWidth, int gridHeight, int screenWidth, int screenHeight) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.gridEntities = new HashMap<>();
        
        // Calculate the number of cells that fit horizontally and vertically
        this.horizontalCells = screenWidth / gridWidth;
        this.verticalCells = screenHeight / gridHeight;
        
        // Calculate the actual cell size after scaling to fit the screen
        this.cellSize = Math.min(screenWidth / horizontalCells, screenHeight / verticalCells);
        
        System.out.println("Grid created: " + horizontalCells + "x" + verticalCells + 
                           " cells, cell size: " + cellSize + "px");
    }
    
    /**
     * Returns the real-world X position for a grid X coordinate.
     * 
     * @param gridX The X position on the grid
     * @return The actual X position in pixels
     */
    public int gridToScreenX(int gridX) {
        return gridX * cellSize;
    }
    
    /**
     * Returns the real-world Y position for a grid Y coordinate.
     * 
     * @param gridY The Y position on the grid
     * @return The actual Y position in pixels
     */
    public int gridToScreenY(int gridY) {
        return gridY * cellSize;
    }
    
    /**
     * Converts a screen X coordinate to a grid X coordinate.
     * 
     * @param screenX The X position in pixels
     * @return The X position on the grid
     */
    public int screenToGridX(int screenX) {
        return screenX / cellSize;
    }
    
    /**
     * Converts a screen Y coordinate to a grid Y coordinate.
     * 
     * @param screenY The Y position in pixels
     * @return The Y position on the grid
     */
    public int screenToGridY(int screenY) {
        return screenY / cellSize;
    }
    
    /**
     * Places an entity at the specified grid position.
     * 
     * @param entity The entity to place
     * @param gridX The X position on the grid
     * @param gridY The Y position on the grid
     * @return True if the entity was placed successfully, false if the cell was already occupied
     */
    public boolean placeEntity(Entity entity, int gridX, int gridY) {
        String key = gridX + "," + gridY;
        
        // Check if the cell is already occupied
        if (gridEntities.containsKey(key)) {
            System.out.println("Cell " + gridX + "," + gridY + " is already occupied");
            return false;
        }
        
        // Calculate the actual position in pixels
        float x = gridToScreenX(gridX);
        float y = gridToScreenY(gridY);
        
        // Set the entity's position
        entity.setX(x);
        entity.setY(y);
        
        // Store the entity in the grid
        gridEntities.put(key, entity);
        
        System.out.println("Placed entity at grid " + gridX + "," + gridY + " (pixel: " + x + "," + y + ")");
        return true;
    }
    
    /**
     * Registers an entity in a grid cell without changing its position.
     * This is useful for multi-cell entities that occupy more than one grid cell.
     * 
     * @param entity The entity to register
     * @param gridX The X position on the grid
     * @param gridY The Y position on the grid
     * @return True if the entity was registered successfully, false if the cell was already occupied
     */
    public boolean registerEntityInCell(Entity entity, int gridX, int gridY) {
        String key = gridX + "," + gridY;
        
        // Check if the cell is already occupied
        if (gridEntities.containsKey(key)) {
            System.out.println("Cell " + gridX + "," + gridY + " is already occupied");
            return false;
        }
        
        // Store the entity in the grid without changing its position
        gridEntities.put(key, entity);
        
        System.out.println("Registered entity in grid cell " + gridX + "," + gridY);
        return true;
    }
    
    /**
     * Removes an entity from the specified grid position.
     * 
     * @param gridX The X position on the grid
     * @param gridY The Y position on the grid
     * @return The entity that was removed, or null if no entity was at that position
     */
    public Entity removeEntity(int gridX, int gridY) {
        String key = gridX + "," + gridY;
        return gridEntities.remove(key);
    }
    
    /**
     * Gets the entity at the specified grid position.
     * 
     * @param gridX The X position on the grid
     * @param gridY The Y position on the grid
     * @return The entity at the specified position, or null if no entity is there
     */
    public Entity getEntity(int gridX, int gridY) {
        String key = gridX + "," + gridY;
        return gridEntities.get(key);
    }
    
    /**
     * Checks if a grid position is occupied by an entity.
     * 
     * @param gridX The X position on the grid
     * @param gridY The Y position on the grid
     * @return True if the position is occupied, false otherwise
     */
    public boolean isOccupied(int gridX, int gridY) {
        String key = gridX + "," + gridY;
        return gridEntities.containsKey(key);
    }
    
    /**
     * Clears all entities from the grid.
     */
    public void clearGrid() {
        gridEntities.clear();
    }
    
    /**
     * Gets the width of a grid cell in pixels.
     * 
     * @return The cell size
     */
    public int getCellSize() {
        return cellSize;
    }
    
    /**
     * Gets the number of horizontal cells in the grid.
     * 
     * @return The number of horizontal cells
     */
    public int getHorizontalCells() {
        return horizontalCells;
    }
    
    /**
     * Gets the number of vertical cells in the grid.
     * 
     * @return The number of vertical cells
     */
    public int getVerticalCells() {
        return verticalCells;
    }
    
    /**
     * Resizes the grid to fit a new screen size.
     * 
     * @param newScreenWidth The new screen width in pixels
     * @param newScreenHeight The new screen height in pixels
     */
    public void resize(int newScreenWidth, int newScreenHeight) {
        this.screenWidth = newScreenWidth;
        this.screenHeight = newScreenHeight;
        
        // Recalculate the grid dimensions
        this.horizontalCells = screenWidth / gridWidth;
        this.verticalCells = screenHeight / gridHeight;
        this.cellSize = Math.min(screenWidth / horizontalCells, screenHeight / verticalCells);
        
        // Reposition all entities based on the new grid size
        Map<String, Entity> newPositions = new HashMap<>();
        for (Map.Entry<String, Entity> entry : gridEntities.entrySet()) {
            String[] coords = entry.getKey().split(",");
            int gridX = Integer.parseInt(coords[0]);
            int gridY = Integer.parseInt(coords[1]);
            
            Entity entity = entry.getValue();
            entity.setX(gridToScreenX(gridX));
            entity.setY(gridToScreenY(gridY));
            
            newPositions.put(gridX + "," + gridY, entity);
        }
        
        // Replace the grid entities with the repositioned ones
        this.gridEntities = newPositions;
    }
} 