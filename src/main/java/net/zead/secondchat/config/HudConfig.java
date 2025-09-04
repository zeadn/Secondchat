package net.zead.secondchat.config;

public class HudConfig {
    private int xOffset = -360; // Offset from right side of window
    private int y = 490; // Y position from top
    private int width = 200; // Width of the HUD
    private int backgroundColor = 0x80000000; // Background color with alpha

    // Getters and setters
    public int getXOffset() {
        return xOffset;
    }

    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}