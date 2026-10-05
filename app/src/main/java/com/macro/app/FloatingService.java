package com.macro.app;

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

public class FloatingService extends Service {

    private WindowManager windowManager;
    private LinearLayout floatingView;
    private WindowManager.LayoutParams params;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // إنشاء الواجهة برمجياً لضمان عدم حدوث أخطاء الموارد
        floatingView = new LinearLayout(this);
        floatingView.setOrientation(LinearLayout.VERTICAL);
        floatingView.setBackgroundColor(Color.parseColor("#DD222222"));
        floatingView.setPadding(25, 25, 25, 25);

        Button btnTestClick = new Button(this);
        btnTestClick.setText("🎯 تجربة نقرة");
        btnTestClick.setTextColor(Color.WHITE);

        Button btnTestSwipe = new Button(this);
        btnTestSwipe.setText("↔️ تجربة سحب");
        btnTestSwipe.setTextColor(Color.WHITE);

        Button btnClose = new Button(this);
        btnClose.setText("❌ إغلاق");
        btnClose.setTextColor(Color.WHITE);

        floatingView.addView(btnTestClick);
        floatingView.addView(btnTestSwipe);
        floatingView.addView(btnClose);

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
        params.x = 100;
        params.y = 300;

        windowManager.addView(floatingView, params);

        // تحريك القائمة العائمة بالإصبع
        floatingView.setOnTouchListener(new View.OnTouchListener() {
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
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                }
                return false;
            }
        });

        // تجربة النقر في منتصف الشاشة
        btnTestClick.setOnClickListener(v -> {
            MacroAccessibilityService service = MacroAccessibilityService.getInstance();
            if (service != null) {
                service.performClick(500, 1000, 50);
                Toast.makeText(this, "تم تنفيذ النقر عند (500, 1000)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "تأكد من تفعيل خدمة الوصول أولاً!", Toast.LENGTH_SHORT).show();
            }
        });

        // تجربة السحب من أسفل إلى أعلى
        btnTestSwipe.setOnClickListener(v -> {
            MacroAccessibilityService service = MacroAccessibilityService.getInstance();
            if (service != null) {
                service.performSwipe(500, 1500, 500, 500, 400);
                Toast.makeText(this, "تم تنفيذ السحب!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "تأكد من تفعيل خدمة الوصول أولاً!", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> stopSelf());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) windowManager.removeView(floatingView);
    }
}
