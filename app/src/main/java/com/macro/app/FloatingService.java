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
    private LinearLayout floatingControlPanel;
    private WindowManager.LayoutParams params;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startForegroundServiceNotification();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // انشاء شريط الأدوات العائم
        floatingControlPanel = new LinearLayout(this);
        floatingControlPanel.setOrientation(LinearLayout.VERTICAL);
        floatingControlPanel.setBackgroundColor(Color.parseColor("#CC111111"));
        floatingControlPanel.setPadding(15, 15, 15, 15);

        // إضافة أزرار التحكم
        Button btnPlay = createToolButton("▶️ تشغيل");
        Button btnAddClick = createToolButton("➕ نقطة نقر");
        Button btnAddSwipe = createToolButton("↔️ مسار سحب");
        Button btnClose = createToolButton("❌ إغلاق");

        floatingControlPanel.addView(btnPlay);
        floatingControlPanel.addView(btnAddClick);
        floatingControlPanel.addView(btnAddSwipe);
        floatingControlPanel.addView(btnClose);

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
        params.x = 20;
        params.y = 400;

        windowManager.addView(floatingControlPanel, params);

        // ميزة تحريك الشريط بأي اتجاه عبر السحب
        floatingControlPanel.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;

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
                        windowManager.updateViewLayout(floatingControlPanel, params);
                        return true;
                }
                return false;
            }
        });

        // البرمجة والتفاعل للأزرار
        btnAddClick.setOnClickListener(v -> {
            MacroAccessibilityService service = MacroAccessibilityService.getInstance();
            if (service != null) {
                service.performClick(500, 1000, 50);
                Toast.makeText(this, "تم تنفيذ تجربة نقرة!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "يرجى تفعيل خدمة إمكانية الوصول أولاً!", Toast.LENGTH_LONG).show();
            }
        });

        btnAddSwipe.setOnClickListener(v -> {
            MacroAccessibilityService service = MacroAccessibilityService.getInstance();
            if (service != null) {
                service.performSwipe(500, 1500, 500, 600, 300);
                Toast.makeText(this, "تم تنفيذ تجربة سحب!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "يرجى تفعيل خدمة إمكانية الوصول أولاً!", Toast.LENGTH_LONG).show();
            }
        });

        btnPlay.setOnClickListener(v -> 
            Toast.makeText(this, "سيتم تشغيل حلقة السيناريو...", Toast.LENGTH_SHORT).show()
        );

        btnClose.setOnClickListener(v -> stopSelf());
    }

    private Button createToolButton(String text) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextSize(12);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.parseColor("#333333"));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 5, 0, 5);
        btn.setLayoutParams(layoutParams);
        return btn;
    }

    private void startForegroundServiceNotification() {
        String channelId = "macro_bot_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "MacroBot Control",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("MacroBot شغال")
                .setContentText("شريط التحكم العائم نشط")
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .build();

        startForeground(101, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingControlPanel != null) windowManager.removeView(floatingControlPanel);
    }
}
