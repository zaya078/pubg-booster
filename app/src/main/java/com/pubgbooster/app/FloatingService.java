package com.pubgbooster.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FloatingService extends Service {

    private WindowManager windowManager;
    private View floatingButton;
    private View floatingMenu;
    private boolean menuVisible = false;

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, buildNotification());
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        createFloatingButton();
        createFloatingMenu();
    }

    private void createFloatingButton() {
        floatingButton = new View(this);
        floatingButton.setBackgroundColor(Color.parseColor("#CC2196F3"));

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                60, 60,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 300;

        floatingButton.setOnClickListener(v -> toggleMenu());

        floatingButton.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY;

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
                        params.x = initialX + (int)(event.getRawX() - initialTouchX);
                        params.y = initialY + (int)(event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingButton, params);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(event.getRawX() - initialTouchX) < 10 &&
                            Math.abs(event.getRawY() - initialTouchY) < 10) {
                            toggleMenu();
                        }
                        return true;
                }
                return false;
            }
        });

        windowManager.addView(floatingButton, params);
    }

    private void createFloatingMenu() {
        LinearLayout menu = new LinearLayout(this);
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setBackgroundColor(Color.parseColor("#DD000000"));
        menu.setPadding(20, 20, 20, 20);

        Button btnPerformance = new Button(this);
        btnPerformance.setText("⚡ PERFORMANCE");
        btnPerformance.setBackgroundColor(Color.parseColor("#FF4CAF50"));
        btnPerformance.setTextColor(Color.WHITE);
        btnPerformance.setOnClickListener(v -> {
            RootCommands.performanceMode();
            Toast.makeText(this, "Performance Mode ON", Toast.LENGTH_SHORT).show();
            toggleMenu();
        });

        Button btnNormal = new Button(this);
        btnNormal.setText("🔋 NORMAL");
        btnNormal.setBackgroundColor(Color.parseColor("#FF2196F3"));
        btnNormal.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.topMargin = 10;
        btnNormal.setLayoutParams(lp);
        btnNormal.setOnClickListener(v -> {
            RootCommands.normalMode();
            Toast.makeText(this, "Normal Mode", Toast.LENGTH_SHORT).show();
            toggleMenu();
        });

        Button btnRam = new Button(this);
        btnRam.setText("🧹 RAM CLEAR");
        btnRam.setBackgroundColor(Color.parseColor("#FFFF9800"));
        btnRam.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp2.topMargin = 10;
        btnRam.setLayoutParams(lp2);
        btnRam.setOnClickListener(v -> {
            RootCommands.clearRam();
            Toast.makeText(this, "RAM Cleared", Toast.LENGTH_SHORT).show();
            toggleMenu();
        });

        menu.addView(btnPerformance);
        menu.addView(btnNormal);
        menu.addView(btnRam);

        WindowManager.LayoutParams menuParams = new WindowManager.LayoutParams(
                300, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABL
