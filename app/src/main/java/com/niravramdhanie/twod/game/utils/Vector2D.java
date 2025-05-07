package com.niravramdhanie.twod.game.utils;

public class Vector2D {
    public float x;
    public float y;
    
    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2D add(Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }
    
    public Vector2D subtract(Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }
    
    public Vector2D multiply(float scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }
    
    public float getMagnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }
    
    public Vector2D normalize() {
        float magnitude = getMagnitude();
        return new Vector2D(x / magnitude, y / magnitude);
    }
}