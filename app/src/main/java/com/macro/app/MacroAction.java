package com.macro.app;

import org.json.JSONException;
import org.json.JSONObject;

public class MacroAction {
    public enum Type {
        CLICK_XY,
        CLICK_IMAGE,
        SWIPE,
        WAIT,
        PRESS_BACK,
        PRESS_HOME
    }

    private Type type;
    private String name;
    private int x = 500, y = 1000;
    private int endX = 500, endY = 500;
    private long delayBefore = 0;   // وقت الانتظار قبل النقر (ms)
    private long delayAfter = 1000;  // وقت الانتظار بعد النقر (ms)
    private long duration = 50;     // مدة النقر أو السحب (ms)
    private double matchThreshold = 0.70; // نسبة مطابقة الصورة 70%
    private String imagePath = "";  // مسار الصورة الملتقطة

    public MacroAction(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    // Getters and Setters
    public Type getType() { return type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getEndX() { return endX; }
    public void setEndX(int endX) { this.endX = endX; }
    public int getEndY() { return endY; }
    public void setEndY(int endY) { this.endY = endY; }
    public long getDelayBefore() { return delayBefore; }
    public void setDelayBefore(long delayBefore) { this.delayBefore = delayBefore; }
    public long getDelayAfter() { return delayAfter; }
    public void setDelayAfter(long delayAfter) { this.delayAfter = delayAfter; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    public double getMatchThreshold() { return matchThreshold; }
    public void setMatchThreshold(double matchThreshold) { this.matchThreshold = matchThreshold; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    // تحويل الخطوة إلى JSON للحفظ
    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("type", type.name());
        json.put("name", name);
        json.put("x", x);
        json.put("y", y);
        json.put("endX", endX);
        json.put("endY", endY);
        json.put("delayBefore", delayBefore);
        json.put("delayAfter", delayAfter);
        json.put("duration", duration);
        json.put("matchThreshold", matchThreshold);
        json.put("imagePath", imagePath);
        return json;
    }

    // استرجاع الخطوة من JSON
    public static MacroAction fromJSONObject(JSONObject json) throws JSONException {
        Type type = Type.valueOf(json.getString("type"));
        String name = json.getString("name");
        MacroAction action = new MacroAction(type, name);
        action.setX(json.optInt("x", 500));
        action.setY(json.optInt("y", 1000));
        action.setEndX(json.optInt("endX", 500));
        action.setEndY(json.optInt("endY", 500));
        action.setDelayBefore(json.optLong("delayBefore", 0));
        action.setDelayAfter(json.optLong("delayAfter", 1000));
        action.setDuration(json.optLong("duration", 50));
        action.setMatchThreshold(json.optDouble("matchThreshold", 0.70));
        action.setImagePath(json.optString("imagePath", ""));
        return action;
    }
}
