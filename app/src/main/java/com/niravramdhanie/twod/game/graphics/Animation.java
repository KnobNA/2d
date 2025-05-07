package com.niravramdhanie.twod.game.graphics;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Animation {
    private List<BufferedImage> frames;
    private List<Integer> durations;
    private int currentFrame;
    private long startTime;
    private boolean playing;
    
    public Animation() {
        frames = new ArrayList<>();
        durations = new ArrayList<>();
        currentFrame = 0;
        startTime = System.currentTimeMillis();
        playing = true;
    }
    
    public void addFrame(BufferedImage frame, int duration) {
        frames.add(frame);
        durations.add(duration);
    }
    
    public void update() {
        if (playing && !frames.isEmpty()) {
            long elapsed = System.currentTimeMillis() - startTime;
            
            if (elapsed > durations.get(currentFrame)) {
                currentFrame++;
                startTime = System.currentTimeMillis();
                
                if (currentFrame >= frames.size()) {
                    currentFrame = 0; // Loop animation
                }
            }
        }
    }
    
    public BufferedImage getCurrentFrame() {
        if (frames.isEmpty()) return null;
        return frames.get(currentFrame);
    }
    
    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
    
    public boolean isPlaying() {
        return playing;
    }
    
    public void reset() {
        currentFrame = 0;
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Checks if this animation has any frames
     * @return true if the animation has at least one frame, false otherwise
     */
    public boolean hasFrames() {
        return !frames.isEmpty();
    }
    
    /**
     * Gets the number of frames in this animation
     * @return the number of frames
     */
    public int getFrameCount() {
        return frames.size();
    }
}