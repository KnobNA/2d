package com.niravramdhanie.twod.game;

import javax.swing.SwingUtilities;

import com.niravramdhanie.twod.game.core.Game;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting the game application...");
        
        // Set system properties for rendering
        System.setProperty("sun.java2d.opengl", "True");
        
        // Create and start everything in the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Create the game
                System.out.println("Creating game instance");
                Game game = new Game("My 2D Game", 800, 600);
                
                // Game will start itself when initialization is complete
                System.out.println("Game instance created");
            } catch (Exception e) {
                System.err.println("Error starting the game: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}