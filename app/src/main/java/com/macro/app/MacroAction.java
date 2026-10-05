package com.macro.app;

public class MacroAction {
    public enum Type {
        CLICK_XY,
        CLICK_IMAGE,
        SWIPE,
        WAIT,
        PRESS_BACK,
        PRESS_HOME,
        TOAST
    }

    private Type type;
    private int x, y;
    private int endX, endY;
    private long duration;
    private String text;

    public MacroAction(Type type) {
        this.type = type;
    }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getEndX() { return endX; }
    public void setEndX(int endX) { this.endX = endX; }

    public int getEndY() { return endY; }
    public void setEndY(int endY) { this.endY = endY; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
