package com.macro.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

public class FloatingService extends Service {

    private WindowManager windowManager;
    private View bubbleView;
    private LinearLayout menuLayout;
    private WindowManager.LayoutParams params;
    private boolean isExpanded = false;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundServiceNotification();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // إنشاء الزر الدائري الرئيسي (Floating Widget Bubble)
        Button bubbleButton = new Button(this);
        bubbleButton.setText("🤖");
        bubbleButton.setTextSize(20);
        bubbleButton.setBackgroundColor(Color.parseColor("#8A2BE2"));
        bubbleButton.setTextColor(Color.WHITE);

        // إنشاء قائمة الخيارات
        menuLayout = new LinearLayout(this);
        menuLayout.setOrientation(LinearLayout.VERTICAL);
        menuLayout.setBackgroundColor(Color.parseColor("#DD1E1E1E"));
        menuLayout.setPadding(10, 10, 10, 10);
        menuLayout.setVisibility(View.GONE);

        Button btnPlay = createMenuBtn("▶️ Play Mode");
        Button btnEdit = createMenuBtn("✏️ Edit Mode");
        Button btnClose = createMenuBtn("❌ Close");

        menuLayout.addView(btnPlay);
        menuLayout.addView(btnEdit);
        menuLayout.addView(btnClose);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(bubbleButton, new LinearLayout.LayoutParams(140, 140));
        container.addView(menuLayout);

        bubbleView = container;

        int layoutFlag = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 50;
        params.y = 500;

        windowManager.addView(bubbleView, params);

        // التعامل مع السحب والضغط للكرة العائمة
        bubbleButton.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;
            private static final int CLICK_THRESHOLD = 10;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(bubbleView, params);
                        return true;
                    case MotionEvent.ACTION_UP:
                        int diffX = Math.abs((int) (event.getRawX() - initialTouchX));
                        int diffY = Math.abs((int) (event.getRawY() - initialTouchY));
                        if (diffX < CLICK_THRESHOLD && diffY < CLICK_THRESHOLD) {
                            toggleMenu();
                        }
                        return true;
                }
                return false;
            }
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, MacroEditorActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            toggleMenu();
        });

        btnPlay.setOnClickListener(v -> {
            Toast.makeText(this, "بدء تنفيذ الماكرو...", Toast.LENGTH_SHORT).show();
            toggleMenu();
        });

        btnClose.setOnClickListener(v -> stopSelf());
    }

    private void toggleMenu() {
        if (isExpanded) {
            menuLayout.setVisibility(View.GONE);
        } else {
            menuLayout.setVisibility(View.VISIBLE);
        }
        isExpanded = !isExpanded;
    }

    private Button createMenuBtn(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(12);
        b.setTextColor(Color.WHITE);
        b.setBackgroundColor(Color.parseColor("#333333"));
        return b;
    }

    private void startForegroundServiceNotification() {
        String channelId = "macro_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Macro Service", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("MacroBot Running")
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .build();
        startForeground(102, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bubbleView != null) windowManager.removeView(bubbleView);
    }
}
