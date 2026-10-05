package com.macro.app;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

public class MacroAccessibilityService extends AccessibilityService {

    private static MacroAccessibilityService instance;

    public static MacroAccessibilityService getInstance() {
        return instance;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    public void performClick(int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.StrokeDescription(path, 0, 50))
                .build();
        dispatchGesture(gestureDescription, null, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
