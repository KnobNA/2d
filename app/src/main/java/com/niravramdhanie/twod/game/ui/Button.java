package com.niravramdhanie.twod.game.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Interactive button component for menus and UI
 */
public class Button extends UIComponent {
    private String text;
    private Font font;
    private Color textColor;
    private Color backgroundColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color borderColor;
    private boolean hovered;
    private boolean pressed;
    private ButtonClickListener clickListener;
    
    public Button(int x, int y, int width, int height, String text) {
        super(x, y, width, height);
        this.text = text;
        this.font = new Font("Arial", Font.BOLD, 16);
        this.textColor = Color.WHITE;
        this.backgroundColor = new Color(70, 70, 70);
        this.hoverColor = new Color(100, 100, 100);
        this.pressedColor = new Color(50, 50, 50);
        this.borderColor = Color.WHITE;
        this.hovered = false;
        this.pressed = false;
        
        System.out.println("Button created: " + text + " at " + x + "," + y + 
                           " with dimensions " + width + "x" + height);
    }
    
    /**
     * Update the button state
     * @return true if the button state changed, false otherwise
     */
    public boolean update() {
        // Currently no animation or state changes in update
        // Will return false until we add animations or other state changes
        return false;
    }
    
    @Override
    public void render(Graphics2D g) {
        if (!visible) {
            System.out.println("Button '" + text + "' not visible, skipping render");
            return;
        }
        
        try {
            // Determine the color based on button state
            Color currentColor;
            if (pressed) {
                currentColor = pressedColor;
            } else if (hovered) {
                currentColor = hoverColor;
            } else {
                currentColor = backgroundColor;
            }
            
            // Draw background
            g.setColor(currentColor);
            g.fillRect(x, y, width, height);
            
            // Draw border - using a more visible color
            g.setColor(borderColor);
            g.drawRect(x, y, width, height);
            
            // Draw text - shift text slightly when pressed to give a pressed effect
            g.setFont(font);
            g.setColor(textColor);
            
            // Center the text
            int textWidth = g.getFontMetrics().stringWidth(text);
            int textHeight = g.getFontMetrics().getHeight();
            int textX = x + (width - textWidth) / 2;
            int textY = y + (height + textHeight / 2) / 2;
            
            // Apply a small offset when button is pressed to enhance button press visual feedback
            if (pressed) {
                textX += 1;
                textY += 1;
            }
            
            g.drawString(text, textX, textY);
        } catch (Exception e) {
            System.err.println("Error rendering button '" + text + "': " + e.getMessage());
        }
    }
    
    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }
    
    public boolean isHovered() {
        return hovered;
    }
    
    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
    
    public boolean isPressed() {
        return pressed;
    }
    
    public void click() {
        if (clickListener != null) {
            clickListener.onClick();
        }
    }
    
    public void setClickListener(ButtonClickListener listener) {
        this.clickListener = listener;
    }
    
    // Interface for button click events
    public interface ButtonClickListener {
        void onClick();
    }
    
    // Additional getters and setters
    public void setText(String text) { this.text = text; }
    public String getText() { return text; }
    
    public void setFont(Font font) { this.font = font; }
    public Font getFont() { return font; }
    
    public void setTextColor(Color color) { this.textColor = color; }
    public Color getTextColor() { return textColor; }
    
    public void setBackgroundColor(Color color) { this.backgroundColor = color; }
    public Color getBackgroundColor() { return backgroundColor; }
    
    public void setHoverColor(Color color) { this.hoverColor = color; }
    public Color getHoverColor() { return hoverColor; }
    
    public void setPressedColor(Color color) { this.pressedColor = color; }
    public Color getPressedColor() { return pressedColor; }
    
    public void setBorderColor(Color color) { this.borderColor = color; }
    public Color getBorderColor() { return borderColor; }
}